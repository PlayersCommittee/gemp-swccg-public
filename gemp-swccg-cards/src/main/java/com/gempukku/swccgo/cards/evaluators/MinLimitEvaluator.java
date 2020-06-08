package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the result of the specified evaluator with the specified minimum value limit.
 */
public class MinLimitEvaluator extends BaseEvaluator {
    private int _limit;
    private Evaluator _evaluator;

    /**
     * Creates an evaluator that returns the result of the specified evaluator with the specified minimum value limit.
     * @param evaluator the evaluator
     * @param limit the minimum value limit
     */
    public MinLimitEvaluator(Evaluator evaluator, int limit) {
        _evaluator = evaluator;
        _limit = limit;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
        return Math.max(_limit, _evaluator.evaluateExpression(gameState, modifiersQuerying, cardAffected));
    }
}
