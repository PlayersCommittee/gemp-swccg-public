package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified ModifierFlag is active.
 */
public class FlagActiveCondition implements Condition {
    private ModifierFlag _modifierFlag;

    /**
     * Creates a condition that is fulfilled when the specified ModifierFlag is active.
     * @param modifierFlag the ModifierFlag
     */
    public FlagActiveCondition(ModifierFlag modifierFlag) {
        _modifierFlag = modifierFlag;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return modifiersQuerying.hasFlagActive(gameState, _modifierFlag);
    }
}
