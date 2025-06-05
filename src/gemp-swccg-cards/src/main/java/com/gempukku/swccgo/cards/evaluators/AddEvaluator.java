package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the result of the specified evaluator with the specified amount added.
 */
public class AddEvaluator extends BaseEvaluator {
    private Evaluator _evaluator;
    private Evaluator[] _amountsToAdd;

    /**
     * Creates an evaluator that returns the result of the specified evaluator with the specified amount added.
     * @param evaluator the evaluator
     * @param amountToAdd the amount to add
     */
    public AddEvaluator(Evaluator evaluator, float amountToAdd) {
        this(evaluator, new ConstantEvaluator(amountToAdd));
    }

    /**
     * Creates an evaluator that returns the result of the specified amount with the specified amount added.
     * @param initialAmount the initial amount
     * @param amountsToAdd the evaluators for the amounts to add
     */
    public AddEvaluator(float initialAmount, Evaluator... amountsToAdd) {
        this(new ConstantEvaluator(initialAmount), amountsToAdd);
    }

    /**
     * Creates an evaluator that returns the result of the specified evaluator with a calculated amounts added.
     * @param evaluator the evaluator
     * @param amountsToAdd the evaluators for the amounts to add
     */
    public AddEvaluator(Evaluator evaluator, Evaluator... amountsToAdd) {
        _evaluator = evaluator;
        _amountsToAdd = amountsToAdd;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
        float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, cardAffected);
        for (Evaluator amountToAdd : _amountsToAdd) {
            value += amountToAdd.evaluateExpression(gameState, modifiersQuerying, cardAffected);
        }
        return value;
    }
}
