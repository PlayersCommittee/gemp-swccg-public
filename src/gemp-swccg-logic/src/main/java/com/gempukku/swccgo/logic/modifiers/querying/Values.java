package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface Values extends BaseQuery {
	default float getVariableValue(GameState gameState, PhysicalCard physicalCard, Variable variable, float baseValue) {
		Float result = baseValue;

		// Apply modifier that sets initial value
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INITIAL_CALCULATION, physicalCard)) {
			if (modifier.isAffectedVariable(variable)) {
				result = modifier.getValue(gameState, query(), physicalCard);
			}
		}

		// Apply multiplication modifiers
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MULTIPLICATION_CALCULATION, physicalCard))
			result *= modifier.getMultiplicationCalculationModifier(gameState, query(), physicalCard, variable);

		// Apply addition modifiers
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ADDITION_CALCULATION, physicalCard))
			result += modifier.getAdditionCalculationModifier(gameState, query(), physicalCard, variable);

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.RESET_CALCULATION, physicalCard)) {
			if (modifier.isAffectedVariable(variable)) {
				float resetValue = modifier.getValue(gameState, query(), physicalCard);
				lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, resetValue) : resetValue;
			}
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the calculation total.
	 * @param gameState the game state
	 * @param calculationSource the source card during the calculation
	 * @param baseTotal the base total
	 * @return the calculation total
	 */
	default float getCalculationTotal(GameState gameState, PhysicalCard calculationSource, float baseTotal) {
		float result = baseTotal;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CALCULATION_TOTAL, calculationSource)) {
			result += modifier.getValue(gameState, query(), calculationSource);
		}
		return Math.max(0, result);
	}

	/**
	 * Gets the calculation total when targeting a specified card.
	 * @param gameState the game state
	 * @param calculationSource the source card during the calculation
	 * @param target the target
	 * @param baseTotal the base total
	 * @return the calculation total
	 */
	default float getCalculationTotalTargetingCard(GameState gameState, PhysicalCard calculationSource, PhysicalCard target, float baseTotal) {
		float result = baseTotal;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CALCULATION_TOTAL, calculationSource)) {
			result += modifier.getValue(gameState, query(), calculationSource);
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CALCULATION_TOTAL_WHEN_TARGETED, target)) {
			if (modifier.isActionSource(gameState, query(), calculationSource)) {
				result += modifier.getValue(gameState, query(), target);
			}
		}
		return Math.max(0, result);
	}
}
