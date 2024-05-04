package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the specified value if the specified condition is fulfilled, otherwise returns the specified
 * default value.
 */
public class ConditionEvaluator extends BaseEvaluator {
    private Evaluator _default;
    private Evaluator _conditionFulfilled;
    private Condition _condition;

    /**
     * Creates an evaluator that returns the value from the specified evaluator if the specified condition is fulfilled,
     * otherwise returns the value from the specified default evaluator.
     * @param aDefault the default evaluator to return the value of
     * @param conditionFulfilled the evaluator to return the value of if the condition is fulfilled
     * @param condition the condition
     */
    public ConditionEvaluator(Evaluator aDefault, Evaluator conditionFulfilled, Condition condition) {
        _default = aDefault;
        _conditionFulfilled = conditionFulfilled;
        _condition = condition;
    }

    /**
     * Creates an evaluator that returns the value from the specified evaluator if the specified condition is fulfilled,
     * otherwise returns the default value.
     * @param aDefault the default value
     * @param conditionFulfilled the evaluator to return the value of if the condition is fulfilled
     * @param condition the condition
     */
    public ConditionEvaluator(int aDefault, Evaluator conditionFulfilled, Condition condition) {
        _default = new ConstantEvaluator(aDefault);
        _conditionFulfilled = conditionFulfilled;
        _condition = condition;
    }

    /**
     * Creates an evaluator that returns the specified value if the specified condition is fulfilled, otherwise returns
     * the default value.
     * @param aDefault the default value
     * @param conditionFulfilled the value to return if the condition is fulfilled
     * @param condition the condition
     */
    public ConditionEvaluator(int aDefault, int conditionFulfilled, Condition condition) {
        _default = new ConstantEvaluator(aDefault);
        _conditionFulfilled = new ConstantEvaluator(conditionFulfilled);
        _condition = condition;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_condition.isFulfilled(gameState, modifiersQuerying))
            return _conditionFulfilled.evaluateExpression(gameState, modifiersQuerying, self);
        else
            return _default.evaluateExpression(gameState, modifiersQuerying, self);
    }
}
