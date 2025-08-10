package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface Piles extends BaseQuery {
	/**
	 * Gets the number of cards the specified player draws in starting hand.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return the number of cards
	 */
	default int getNumCardsToDrawInStartingHand(GameState gameState, String playerId) {
		int numCards = 8;

		for (Modifier modifier : getModifiers(gameState, ModifierType.NUM_CARDS_DRAWN_IN_STARTING_HAND)) {
			if (modifier.isForPlayer(playerId)) {
				numCards = (int) modifier.getValue(gameState, query(), (PhysicalCard) null);
			}
		}
		return Math.max(0, numCards);
	}

	/**
	 * Determines if the player is explicitly not allowed to search the card pile using the game text action on the specified
	 * card.
	 * @param gameState the game state
	 * @param card the card
	 * @param playerId the player
	 * @param cardPile the card pile
	 * @param cardPileOwner the card pile owner
	 * @param gameTextActionId the game text action id
	 * @return true if card pile is not allowed to be searched, otherwise false
	 */
	default boolean isSearchingCardPileProhibited(GameState gameState, PhysicalCard card, String playerId, Zone cardPile,
			String cardPileOwner, GameTextActionId gameTextActionId) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANT_SEARCH_CARD_PILE, card))
			if (modifier.isProhibitedFromSearchingCardPile(gameState, query(), playerId, cardPile, cardPileOwner, gameTextActionId))
				return true;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_SEARCH_CARD_PILE, card))
			if (modifier.isProhibitedFromSearchingCardPile(gameState, query(), playerId, cardPile, cardPileOwner))
				return true;
		return false;
	}

	/**
	 * Determines if the specified player can remove cards from opponent hand using the specified card.
	 * @param gameState the game state
	 * @param actionSource the source card of the action
	 * @param playerId the player
	 * @return true or false
	 */
	default boolean mayNotRemoveCardsFromOpponentsHand(GameState gameState, PhysicalCard actionSource, String playerId) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_REMOVE_CARDS_FROM_OPPONENTS_HAND)) {
			if (modifier.isForPlayer(playerId)) {
				if (modifier.isActionSource(gameState, query(), actionSource)) {
					return true;
				}
			}
		}
		return false;
	}
}
