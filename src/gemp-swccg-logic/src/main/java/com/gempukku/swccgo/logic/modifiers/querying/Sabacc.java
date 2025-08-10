package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface Sabacc extends BaseQuery {
	/**
	 * Gets the sabacc total for the player.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return the sabacc total
	 */
	default float getSabaccTotal(GameState gameState, String playerId) {
		float result = 0;

		for (PhysicalCard sabaccCard : gameState.getSabaccHand(playerId)) {
			result += sabaccCard.getSabaccValue();
		}

		// Check modifiers to "sabacc total"
		for (Modifier modifier : getModifiers(gameState, ModifierType.SABACC_TOTAL))
			result += modifier.getSabaccTotalModifier(playerId, gameState, query());

		result = Math.max(0, result);
		return result;
	}

	/**
	 * Determines if the card may have its destiny number cloned in sabacc by specified player when not in sabacc hand.
	 * @param gameState the game state
	 * @param card the card
	 * @param playerId the player
	 * @return true or false
	 */
	default boolean mayHaveDestinyNumberClonedInSabacc(GameState gameState, PhysicalCard card, String playerId) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_CLONE_DESTINY_IN_SABACC)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}
}
