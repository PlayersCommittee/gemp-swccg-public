package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the included condition is not fulfilled.
 */
public class NotCondition implements Condition {
    private Condition _condition;

    /**
     * Creates a condition that is fulfilled when the specified condition is not fulfilled.
     * @param condition the condition
     */
    public NotCondition(Condition condition) {
        _condition = condition;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return !_condition.isFulfilled(gameState, modifiersQuerying);
    }
}
