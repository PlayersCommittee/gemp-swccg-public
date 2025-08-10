package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface JediTests extends BaseQuery {
	/**
	 * Determines if the specified card may not attempt Jedi Tests.
	 * @param gameState the game state
	 * @param card the card
	 * @return true or false
	 */
	default boolean mayNotAttemptJediTests(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ATTEMPT_JEDI_TESTS, card).isEmpty());
	}

	/**
	 * Determines if the specified Jedi Test is placed on table when completed.
	 * @param gameState the game state
	 * @param card the Jedi Test
	 * @return true or false
	 */
	default boolean isJediTestPlacedOnTableWhenCompleted(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.PLACE_JEDI_TEST_ON_TABLE_WHEN_COMPLETED, card).isEmpty();
	}

	/**
	 * Determines if the specified Jedi Test is suspended instead of lost when target not on table.
	 * @param gameState the game state
	 * @param card the Jedi Test
	 * @return true or false
	 */
	default boolean isJediTestSuspendedInsteadOfLost(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.JEDI_TEST_SUSPENDED_INSTEAD_OF_LOST, card).isEmpty();
	}

	/**
	 * Gets the Jedi Test number of the specified Jedi Test.
	 * @param gameState the game state
	 * @param jediTest the card
	 * @return true or false
	 */
	default int getJediTestNumber(GameState gameState, PhysicalCard jediTest) {
		return jediTest.getBlueprint().getDestiny().intValue();
	}
}
