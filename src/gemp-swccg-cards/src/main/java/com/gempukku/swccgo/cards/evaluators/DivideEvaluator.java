package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns the result of the specified evaluator divided by the specified amount.
 */
public class DivideEvaluator extends BaseEvaluator {
    private Evaluator _evaluator;
    private int _amount;
    private boolean _roundUp;

    /**
     * Creates an evaluator that returns the result of the specified evaluator divided by the specified amount.
     * @param evaluator the evaluator
     * @param amount the amount to divide by
     * @param roundUp true if rounding up, false if rounding down
     */
    public DivideEvaluator(Evaluator evaluator, int amount, boolean roundUp) {
        _evaluator = evaluator;
        _amount = amount;
        _roundUp = roundUp;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        float result = _evaluator.evaluateExpression(gameState, modifiersQuerying, self) / _amount;
        if (_roundUp) {
            result = (float) Math.ceil((double) result);
        }
        else {
            result = (float) Math.floor((double) result);
        }
        return result;
    }
}
