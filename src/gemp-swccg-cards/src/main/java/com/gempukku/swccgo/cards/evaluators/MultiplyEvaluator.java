package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns the result of the specified evaluator multiplied by the specified amount.
 */
public class MultiplyEvaluator extends BaseEvaluator {
    private Evaluator _evaluator;
    private float _amount;

    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the specified amount.
     * @param amount the multiplier
     * @param evaluator the evaluator
     */
    public MultiplyEvaluator(float amount, Evaluator evaluator) {
        _amount = amount;
        _evaluator = evaluator;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _amount * _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
    }
}
