package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the first value is greater than the second value.
 */
public class GreaterThanCondition implements Condition {
    private Evaluator _evaluator1;
    private Evaluator _evaluator2;

    /**
     * Creates a condition that is fulfilled when the first value is greater than the second value.
     *
     * @param evaluator1 the value to check
     * @param evaluator2 the value to check against
     */
    public GreaterThanCondition(Evaluator evaluator1, Evaluator evaluator2) {
        _evaluator1 = evaluator1;
        _evaluator2 = evaluator2;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return _evaluator1.evaluateExpression(gameState, modifiersQuerying, null) >
                _evaluator2.evaluateExpression(gameState, modifiersQuerying, null);
    }
}