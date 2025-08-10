package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns the result of the specified evaluator with the specified maximum value limit.
 */
public class MaxLimitEvaluator extends BaseEvaluator {
    private int _limit;
    private Evaluator _evaluator;

    /**
     * Creates an evaluator that returns the result of the specified evaluator with the specified maximum value limit.
     * @param evaluator the evaluator
     * @param limit the maximum value limit
     */
    public MaxLimitEvaluator(Evaluator evaluator, int limit) {
        _evaluator = evaluator;
        _limit = limit;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
        return Math.min(_limit, _evaluator.evaluateExpression(gameState, modifiersQuerying, cardAffected));
    }
}
