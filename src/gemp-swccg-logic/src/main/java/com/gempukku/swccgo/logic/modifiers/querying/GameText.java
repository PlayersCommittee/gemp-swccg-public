package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface GameText extends BaseQuery, Piloting {

	/**
	 * Determines if a card's game text may not be canceled.
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card's game text may not be canceled, otherwise false
	 */
	default boolean isProhibitedFromHavingGameTextCanceled(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_GAME_TEXT_CANCELED, card).isEmpty();
	}

	/**
	 * Determines if a card may not be suspended.
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card may not be suspended, otherwise false
	 */
	default boolean isProhibitedFromBeingSuspended(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_SUSPENDED, card).isEmpty();
	}

	/**
	 * Determines if the card has the specified gametext modification.
	 * @param gameState the game state
	 * @param card the card
	 * @param type the gametext modification type
	 * @return true if card has the modification, otherwise false
	 */
	default boolean hasGameTextModification(GameState gameState, PhysicalCard card, ModifyGameTextType type) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MODIFY_GAME_TEXT, card)) {
			if (modifier.getModifyGameTextType(gameState, query(), card)==type)
				return true;
		}
		return false;
	}

	/**
	 * Gets the number of times the card has the specified gametext modification applied cumulatively.
	 * @param gameState the game state
	 * @param card the card
	 * @param type the gametext modification type
	 * @return the number of times the card has the specified gametext modification
	 */
	default int getGameTextModificationCount(GameState gameState, PhysicalCard card, ModifyGameTextType type) {
		int count = 0;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MODIFY_GAME_TEXT, card)) {
			if (modifier.getModifyGameTextType(gameState, query(), card)==type) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Gets the cards (if any) that indicate that the specified card's game text is supposed to be canceled.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if supposed to be canceled, otherwise false
	 */
	default Collection<PhysicalCard> getCardsMarkingGameTextCanceled(GameState gameState, PhysicalCard card) {
		return getCardsMarkingGameTextCanceled(gameState, card, new ModifierCollectorImpl());
	}

	/**
	 * Gets the cards (if any) that indicate that the specified card's game text is supposed to be canceled.
	 * @param gameState the game state
	 * @param card the card
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if supposed to be canceled, otherwise false
	 */
	default Collection<PhysicalCard> getCardsMarkingGameTextCanceled(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
		Set<PhysicalCard> cards = new HashSet<PhysicalCard>();
		if (!isProhibitedFromHavingGameTextCanceled(gameState, card)) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_GAME_TEXT, card)) {
				cards.add(modifier.getSource(gameState));
				modifierCollector.addModifier(modifier);
			}
		}
		return cards;
	}

	/**
	 * Determines if the specified card's game text is canceled.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if canceled, otherwise false
	 */
	default boolean isGameTextCanceled(GameState gameState, PhysicalCard card) {
		return isGameTextCanceled(gameState, card, false, false);
	}

	/**
	 * Determines if the specified card's game text is canceled.
	 * @param gameState the game state
	 * @param card the card
	 * @param allowIfOnlyLanded true if game text is still allowed to be enabled if the card is landed (but still piloted for takeoff)
	 * @param skipUnpilotedCheck true if game text is still allowed to be enabled if the card is unpiloted
	 * @return true if canceled, otherwise false
	 */
	default boolean isGameTextCanceled(GameState gameState, PhysicalCard card, boolean allowIfOnlyLanded, boolean skipUnpilotedCheck) {
		if (card.isDejarikHologramAtHolosite())
			return true;

		if (card.isGameTextCanceled() || card.isBlownAway() || card.isCollapsed())
			return true;

		CardCategory cardCategory = card.getBlueprint().getCardCategory();

		if (!skipUnpilotedCheck) {
			// Check if starship or vehicle is in play but is not "piloted" (unless landed is allowed via parameter)
			if (cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.VEHICLE) {
				if (Filters.onTable.accepts(gameState, query(), card) && !isPiloted(gameState, card, allowIfOnlyLanded)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Gets the cards (if any) that indicate that the specified location's game text is supposed to be canceled on the side
	 * facing the specified player.
	 * @param gameState the game state
	 * @param card the card
	 * @param playerId the player
	 * @return true if canceled, otherwise false
	 */
	default Collection<PhysicalCard> getCardsMarkingGameTextCanceledForPlayer(GameState gameState, PhysicalCard card, String playerId) {
		return getCardsMarkingGameTextCanceledForPlayer(gameState, card, playerId, new ModifierCollectorImpl());
	}

	/**
	 * Gets the cards (if any) that indicate that the specified location's game text is supposed to be canceled on the side
	 * facing the specified player.
	 * @param gameState the game state
	 * @param card the card
	 * @param playerId the player
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if canceled, otherwise false
	 */
	default Collection<PhysicalCard> getCardsMarkingGameTextCanceledForPlayer(GameState gameState, PhysicalCard card, String playerId, ModifierCollector modifierCollector) {
		Set<PhysicalCard> cards = new HashSet<PhysicalCard>();

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_LOCATION_GAME_TEXT_FOR_PLAYER, card)) {
			if (modifier.isCanceledTextForPlayer(playerId)) {
				cards.add(modifier.getSource(gameState));
				modifierCollector.addModifier(modifier);
			}
		}
		return cards;
	}

	/**
	 * Determines if the specified location's game text is canceled on the side facing the specified player.
	 * @param gameState the game state
	 * @param card the card
	 * @param playerId the player
	 * @return true if canceled, otherwise false
	 */
	default boolean isLocationGameTextCanceledForPlayer(GameState gameState, PhysicalCard card, String playerId) {
		if (card.getBlueprint().getCardCategory() != CardCategory.LOCATION)
			return false;

		if (card.isGameTextCanceled() || card.isLocationGameTextCanceledForPlayer(playerId) || card.isBlownAway() || card.isCollapsed())
			return true;

		return false;
	}

	/**
	 * Gets the cards (if any) that indicate that the specified card is supposed to be suspended.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if supposed to be suspended, otherwise false
	 */
	default Collection<PhysicalCard> getCardsMarkingCardSuspended(GameState gameState, PhysicalCard card) {
		return getCardsMarkingCardSuspended(gameState, card, new ModifierCollectorImpl());
	}

	/**
	 * Gets the cards (if any) that indicate that the specified card is supposed to be suspended.
	 * @param gameState the game state
	 * @param card the card
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if supposed to be suspended, otherwise false
	 */
	default Collection<PhysicalCard> getCardsMarkingCardSuspended(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
		Set<PhysicalCard> cards = new HashSet<PhysicalCard>();
		if (isProhibitedFromBeingSuspended(gameState, card))
			return cards;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SUSPEND_CARD, card)) {
			cards.add(modifier.getSource(gameState));
			modifierCollector.addModifier(modifier);
		}
		return cards;
	}

	default PhysicalCard hasExpandedGameTextFromLocation(GameState gameState, Side sideExpandedFrom, PhysicalCard expandedToLocation, Side sideExpandedTo) {
		if (expandedToLocation.getBlueprint().getCardCategory() != CardCategory.LOCATION)
			return null;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EXPAND_LOCATION_GAME_TEXT, expandedToLocation)) {
			PhysicalCard expandedLocation = modifier.includesGameTextFrom(gameState, query(), sideExpandedFrom);
			if (expandedLocation != null) {
				boolean isExpandedLocationRotated = expandedLocation.isInverted();
				boolean isAffectedLocationRotated = expandedToLocation.isInverted();
				if ((isExpandedLocationRotated == isAffectedLocationRotated) == (sideExpandedFrom == sideExpandedTo)) {
					return expandedLocation;
				}
			}
		}

		return null;
	}
}
