package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the maximum result of the specified evaluators.
 */
public class MaxEvaluator extends BaseEvaluator {
    private Evaluator _evaluator1;
    private Evaluator _evaluator2;

    /**
     * Creates an evaluator that returns the maximum result of the specified evaluators.
     * @param evaluator1 an evaluator
     * @param evaluator2 an evaluator
     */
    public MaxEvaluator(Evaluator evaluator1, Evaluator evaluator2) {
        _evaluator1 = evaluator1;
        _evaluator2 = evaluator2;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
        return Math.max(_evaluator1.evaluateExpression(gameState, modifiersQuerying, cardAffected), _evaluator2.evaluateExpression(gameState, modifiersQuerying, cardAffected));
    }
}
