package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An abstract evaluator that returns the value of the specified variable (X, Y, etc.) on the specified card, using a
 * specified calculation for the base value of the variable.
 */
public abstract class CalculateCardVariableEvaluator extends BaseEvaluator {
    private int _permCardId;
    private Variable _variable;

    /**
     * Creates an evaluator that returns the value of the specified variable (X, Y, etc.) on the specified card.
     * @param card the card
     * @param variable the variable (X, Y, etc.)
     */
    public CalculateCardVariableEvaluator(PhysicalCard card, Variable variable) {
        _permCardId = card.getPermanentCardId();
        _variable = variable;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return modifiersQuerying.getVariableValue(gameState, card, _variable, baseValueCalculation(gameState, modifiersQuerying, cardAffected));
    }

    protected abstract float baseValueCalculation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected);
}
