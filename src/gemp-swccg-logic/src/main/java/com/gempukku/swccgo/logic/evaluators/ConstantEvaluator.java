package com.gempukku.swccgo.logic.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns the specified value.
 */
public class ConstantEvaluator extends BaseEvaluator {
    private float _value;

    /**
     * Creates an evaluator that returns the specified value.
     * @param value the value
     */
    public ConstantEvaluator(float value) {
        _value = value;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _value;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, PhysicalCard targetCard) {
        return _value;
    }
}
