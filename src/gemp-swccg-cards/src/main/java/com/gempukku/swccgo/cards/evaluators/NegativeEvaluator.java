package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns the result of the specified evaluator as a negative number.
 */
public class NegativeEvaluator extends BaseEvaluator {
    private Evaluator _evaluator;

    /**
     * Creates an evaluator that returns the result of the specified evaluator as a negative number.
     * @param evaluator the evaluator
     */
    public NegativeEvaluator(Evaluator evaluator) {
        _evaluator = evaluator;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return 0.0f-Math.abs(_evaluator.evaluateExpression(gameState, modifiersQuerying, self));
    }
}
