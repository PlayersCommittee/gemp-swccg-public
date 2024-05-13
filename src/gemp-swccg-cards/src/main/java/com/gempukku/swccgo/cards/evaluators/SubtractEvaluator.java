package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the result of the specified evaluator with the specified amount subtracted.
 */
public class SubtractEvaluator extends BaseEvaluator {
    private Evaluator _startingValue;
    private Evaluator _amountToSubtract;

    /**
     * Creates an evaluator that returns the result of the specified value with calculated amount subtracted.
     * @param startingValue the starting value
     * @param amountToSubtract the evaluator for the amount to subtract
     */
    public SubtractEvaluator(float startingValue, Evaluator amountToSubtract) {
        this(new ConstantEvaluator(startingValue), amountToSubtract);
    }

    /**
     * Creates an evaluator that returns the result of the specified evaluator with the specified amount added.
     * @param startingValue the evaluator for the starting value
     * @param amountToSubtract the evaluator for the amount to subtract
     */
    public SubtractEvaluator(Evaluator startingValue, float amountToSubtract) {
        this(startingValue, new ConstantEvaluator(amountToSubtract));
    }

    /**
     * Creates an evaluator that returns the result of the specified evaluator with the specified amount added.
     * @param startingValue the evaluator for the starting value
     * @param amountToSubtract the evaluator for the amount to subtract
     */
    public SubtractEvaluator(Evaluator startingValue, Evaluator amountToSubtract) {
        _startingValue = startingValue;
        _amountToSubtract = amountToSubtract;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
        float value = _startingValue.evaluateExpression(gameState, modifiersQuerying, cardAffected);
        value -= _amountToSubtract.evaluateExpression(gameState, modifiersQuerying, cardAffected);
        return value;
    }
}
