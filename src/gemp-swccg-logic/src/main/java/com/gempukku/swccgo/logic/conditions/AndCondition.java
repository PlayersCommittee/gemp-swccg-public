package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when all of the included conditions are fulfilled.
 */
public class AndCondition implements Condition {
    private Condition[] _conditions;

    /**
     * Creates a condition that is fulfilled when all the specified conditions are fulfilled.
     * @param conditions the conditions
     */
    public AndCondition(Condition... conditions) {
        _conditions = conditions;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        for (Condition condition : _conditions) {
            if (condition != null && !condition.isFulfilled(gameState, modifiersQuerying))
                return false;
        }

        return true;
    }
}
