package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public interface Reacts extends BaseQuery {
	/**
	 * Gets the 'react' action option if the specified card is allowed to deploy as a 'react'.
	 * @param gameState the game state
	 * @param card the card
	 * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
	 * @param deployTargetFilter the filter for where the card can be played
	 * @return 'react' action option, or null
	 */
	default ReactActionOption getDeployAsReactOption(GameState gameState, PhysicalCard card, ReactActionOption reactActionFromOtherCard, Filter deployTargetFilter) {
		// Check if card is prohibited from any 'react'
		if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT, card).isEmpty()) {
			return null;
		}

		PhysicalCard location = gameState.getBattleOrForceDrainLocation();

		// Check if player may not 'react' to the location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, location)) {
			if (modifier.isForPlayer(card.getOwner())) {
				return null;
			}
		}

		// If can deploy to as 'react' due to another card, then return that option
		if (reactActionFromOtherCard != null) {
			return reactActionFromOtherCard;
		}

		// Gets possible 'react' to targets
		Collection<PhysicalCard> targets = Filters.filterActive(gameState.getGame(), null, Filters.and(deployTargetFilter, Filters.locationAndCardsAtLocation(Filters.sameCardId(location))));

		// Check if card may deploy as a 'react' to the target
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_AS_REACT_TO_TARGET, card)) {
			for (PhysicalCard target : targets) {
				if (modifier.isAffectedTarget(gameState, query(), target)) {

					return new ReactActionOption(modifier.getSource(gameState), modifier.isReactForFree(),
							modifier.getChangeInCost(), false, modifier.getText(gameState, query(), card), card, deployTargetFilter, null, modifier.isGrantedToDeployToTarget());
				}
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_WITH_PILOT_OR_DRIVER_AS_REACT_TO_TARGET, card)) {
			for (PhysicalCard target : targets) {
				if (modifier.isAffectedTarget(gameState, query(), target)) {

					return new ReactActionOption(modifier.getSource(gameState), modifier.isReactForFree(),
							modifier.getChangeInCost(), false, modifier.getText(gameState, query(), card), card, deployTargetFilter, modifier.getPilotOrDriverFilter(), modifier.isGrantedToDeployToTarget());
				}
			}
		}

		return null;
	}

	/**
	 * Gets the 'react' action option if the player can use the specified card to deploy other cards as a 'react'.
	 * @param playerId the player
	 * @param gameState the game state
	 * @param card the card
	 * @return 'react' action option, or null
	 */
	default List<ReactActionOption> getDeployOtherCardsAsReactOption(String playerId, GameState gameState, PhysicalCard card) {

		List<ReactActionOption> reactActionOptions = new LinkedList<>();

		// Check the cards in player's hand and the stacked cards that can deploy as if from hand.
		List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>();
		cardsToCheck.addAll(gameState.getHand(playerId));
		cardsToCheck.addAll(Filters.filter(gameState.getAllStackedCards(), gameState.getGame(), Filters.and(Filters.owner(playerId), Filters.canDeployAsIfFromHand)));
		if (cardsToCheck.isEmpty()) {
			return reactActionOptions;
		}

		// Check if card may deploy other cards as a 'react'
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_OTHER_CARD_AS_REACT_TO_TARGET, card)) {
			if (modifier.isForPlayer(playerId)) {
				Filter cardToReactFilter = modifier.getCardToReactFilter();
				Filter targetFilter = modifier.getTargetFilter();
				ReactActionOption reactActionOption = new ReactActionOption(card, modifier.isReactForFree(), modifier.getChangeInCost(),
						false, modifier.getActionText(), cardToReactFilter, targetFilter, null, modifier.isGrantedToDeployToTarget());

				List<PhysicalCard> validToDeployAsReact = new ArrayList<PhysicalCard>();

				// Check if card can deploy as a 'react' (performed by this card)
				for (PhysicalCard cardToCheck : cardsToCheck) {
					if (cardToReactFilter.accepts(gameState, query(), cardToCheck)) {
						reactActionOption.setCardToReactFilter(cardToCheck);

						Action deployAsReactAction = cardToCheck.getBlueprint().getDeployAsReactAction(playerId, gameState.getGame(),
								cardToCheck, reactActionOption, targetFilter);
						if (deployAsReactAction != null) {
							validToDeployAsReact.add(cardToCheck);
						}
					}
				}

				if (!validToDeployAsReact.isEmpty()) {
					// Update the filter with the cards that can actually deploy as a 'react' and return the action option
					reactActionOption.setCardToReactFilter(Filters.in(validToDeployAsReact));
					reactActionOptions.add(reactActionOption);
				}
			}
		}

		return reactActionOptions;
	}

	/**
	 * Gets the 'react' action option if the specified card is allowed to move as a 'react'.
	 * @param gameState the game state
	 * @param card the card
	 * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
	 * @param asReactAway true if 'react' away, otherwise 'react'
	 * @param moveTargetFilter the filter for where the card can be moved
	 * @return 'react' action options
	 */
	default ReactActionOption getMoveAsReactOption(GameState gameState, PhysicalCard card, ReactActionOption reactActionFromOtherCard, boolean asReactAway, Filter moveTargetFilter) {
		// Check if card is prohibited from any 'react'
		if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT, card).isEmpty()) {
			return null;
		}

		// Check if location is accepted by the move target filter
		PhysicalCard location = gameState.getBattleOrForceDrainLocation();
		if (asReactAway) {
			if (moveTargetFilter.accepts(gameState, query(), location)) {
				return null;
			}

			// Check if player may not 'react' from the location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_FROM_LOCATION, location)) {
				if (modifier.isForPlayer(card.getOwner())) {
					return null;
				}
			}

			// If can move to as 'react' due to another card, then only return that option
			if (reactActionFromOtherCard != null && reactActionFromOtherCard.isReactAway()) {
				return reactActionFromOtherCard;
			}

			// Check if card may move away as a 'react'
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_MOVE_AWAY_AS_REACT_TO_LOCATION, card)) {
				if (modifier.isAffectedTarget(gameState, query(), location)) {

					return new ReactActionOption(modifier.getSource(gameState), modifier.isReactForFree(),
							modifier.getChangeInCost(), true, modifier.getText(gameState, query(), card), card, moveTargetFilter, null, false);
				}
			}
		}
		else {
			if (!moveTargetFilter.accepts(gameState, query(), location)) {
				return null;
			}

			// Check if player may not 'react' to the location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, location)) {
				if (modifier.isForPlayer(card.getOwner())) {
					return null;
				}
			}

			// If can move to as 'react' due to another card, then only return that option
			if (reactActionFromOtherCard != null && !reactActionFromOtherCard.isReactAway()) {
				return reactActionFromOtherCard;
			}

			// Check if card may move as a 'react' to the location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_MOVE_AS_REACT_TO_LOCATION, card)) {
				if (modifier.isAffectedTarget(gameState, query(), location)) {

					return new ReactActionOption(modifier.getSource(gameState), modifier.isReactForFree(),
							modifier.getChangeInCost(), false, modifier.getText(gameState, query(), card), card, moveTargetFilter, null, false);
				}
			}
		}

		return null;
	}

	/**
	 * Gets the 'react' action option if the player can use the specified card to move other cards as a 'react'.
	 * @param playerId the player
	 * @param gameState the game state
	 * @param card the card
	 * @return 'react' action option, or null
	 */
	default ReactActionOption getMoveOtherCardsAsReactOption(String playerId, GameState gameState, PhysicalCard card) {

		// Check the cards in the player has in play.
		List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>(Filters.filterActive(gameState.getGame(), null,
				Filters.and(Filters.owner(playerId), Filters.or(Filters.character, Filters.starship, Filters.vehicle))));
		if (cardsToCheck.isEmpty()) {
			return null;
		}

		// Check if card may allow other cards to move as a 'react'
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_MOVE_OTHER_CARD_AS_REACT_TO_LOCATION, card)) {
			if (modifier.isForPlayer(playerId)) {
				Filter cardToReactFilter = modifier.getCardToReactFilter();
				Filter targetFilter = modifier.getTargetFilter();
				ReactActionOption reactActionOption = new ReactActionOption(card, modifier.isReactForFree(), modifier.getChangeInCost(),
						false, modifier.getActionText(), cardToReactFilter, targetFilter, null, false);

				List<PhysicalCard> validToMoveAsReact = new ArrayList<PhysicalCard>();

				// Check if card can move as a 'react' (performed by this card)
				for (PhysicalCard cardToCheck : cardsToCheck) {
					if (cardToReactFilter.accepts(gameState, query(), cardToCheck)) {

						Action moveAsReactAction = cardToCheck.getBlueprint().getMoveAsReactAction(playerId, gameState.getGame(),
								cardToCheck, reactActionOption, targetFilter);
						if (moveAsReactAction != null) {
							validToMoveAsReact.add(cardToCheck);
						}
					}
				}

				if (!validToMoveAsReact.isEmpty()) {
					// Update the filter with the cards that can actually move as a 'react' and return the action option
					reactActionOption.setCardToReactFilter(Filters.in(validToMoveAsReact));
					return reactActionOption;
				}
			}
		}

		return null;
	}

	/**
	 * Determines if the specified card is able to join the move as 'react'.
	 * @param playerId the player
	 * @param gameState the game state
	 * @param sourceCard the source card of the 'react'
	 * @param card the card
	 * @return true or false
	 */
	default boolean isCardEligibleToJoinMoveAsReact(String playerId, GameState gameState, PhysicalCard sourceCard, PhysicalCard card) {

		if (!Filters.and(Filters.owner(playerId), Filters.or(Filters.character, Filters.starship, Filters.vehicle)).accepts(gameState, query(), card)) {
			return false;
		}

		// Check if card may allow other cards to move as a 'react'
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_MOVE_OTHER_CARD_AS_REACT_TO_LOCATION, sourceCard)) {
			if (modifier.isForPlayer(playerId)) {
				Filter cardToReactFilter = modifier.getCardToReactFilter();
				Filter targetFilter = modifier.getTargetFilter();
				ReactActionOption curReactActionOption = new ReactActionOption(sourceCard, true, 0, false,
						modifier.getActionText(), cardToReactFilter, targetFilter, null, false);

				if (cardToReactFilter.accepts(gameState, query(), card)) {

					Action moveAsReactAction = card.getBlueprint().getMoveAsReactAction(playerId, gameState.getGame(),
							card, curReactActionOption, targetFilter);
					return moveAsReactAction != null;
				}
			}
		}

		return false;
	}

	/**
	 * Gets the 'react' action option if the player can use the specified card to move other cards away as a 'react'.
	 * @param playerId the player
	 * @param gameState the game state
	 * @param card the card
	 * @return 'react' action option, or null
	 */
	default ReactActionOption getMoveOtherCardsAwayAsReactOption(String playerId, GameState gameState, PhysicalCard card) {

		// Check the cards in the player has in play.
		List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>(Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.owner(playerId), Filters.or(Filters.character, Filters.starship, Filters.vehicle))));
		if (cardsToCheck.isEmpty()) {
			return null;
		}

		// Check if card may allow other cards to move away as a 'react'
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_MOVE_OTHER_CARD_AWAY_AS_REACT_TO_LOCATION, card)) {
			if (modifier.isForPlayer(playerId)) {
				Filter cardToReactFilter = modifier.getCardToReactFilter();
				Filter targetFilter = modifier.getTargetFilter();
				ReactActionOption reactActionOption = new ReactActionOption(card, modifier.isReactForFree(), modifier.getChangeInCost(),
						true, modifier.getActionText(), cardToReactFilter, targetFilter, null, false);

				List<PhysicalCard> validToMoveAwayAsReact = new ArrayList<PhysicalCard>();

				// Check if card can move away as a 'react' (performed by this card)
				for (PhysicalCard cardToCheck : cardsToCheck) {
					if (cardToReactFilter.accepts(gameState, query(), cardToCheck)) {

						Action moveAsReactAction = cardToCheck.getBlueprint().getMoveAsReactAction(playerId, gameState.getGame(),
								cardToCheck, reactActionOption, targetFilter);
						if (moveAsReactAction != null) {
							validToMoveAwayAsReact.add(cardToCheck);
						}
					}
				}

				if (!validToMoveAwayAsReact.isEmpty()) {
					// Update the filter with the cards that can actually move away as a 'react' and return the action option
					reactActionOption.setCardToReactFilter(Filters.in(validToMoveAwayAsReact));
					return reactActionOption;
				}
			}
		}

		return null;
	}

	/**
	 * Determines if the specified card is prohibited from participating in a 'react'.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card is prohibited from participating in a 'react', otherwise false
	 */
	default boolean isProhibitedFromParticipatingInReact(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT, card).isEmpty());
	}

	/**
	 * Determines if the specified player is prohibited from deploying cards as a 'react' to the current battle or Force
	 * Drain location.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return true if player may not deploy cards as a 'react', otherwise false
	 */
	default boolean isProhibitedFromDeployingAsReact(GameState gameState, String playerId) {
		PhysicalCard location = gameState.getBattleOrForceDrainLocation();
		if (location == null)
			return true;

		// Check if player may not 'react' to the location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, location)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}

		return false;
	}
}
