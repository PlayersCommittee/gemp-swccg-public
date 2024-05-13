package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when any of the included conditions are fulfilled.
 */
public class OrCondition implements Condition {
    private Condition[] _conditions;

    /**
     * Creates a condition that is fulfilled when any the specified conditions are fulfilled.
     * @param conditions the conditions
     */
    public OrCondition(Condition... conditions) {
        _conditions = conditions;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        for (Condition condition : _conditions) {
            if (condition != null && condition.isFulfilled(gameState, modifiersQuerying))
                return true;
        }

        return false;
    }
}
