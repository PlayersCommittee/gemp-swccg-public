package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is never fulfilled.
 */
public class FalseCondition implements Condition {

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return false;
    }
}
