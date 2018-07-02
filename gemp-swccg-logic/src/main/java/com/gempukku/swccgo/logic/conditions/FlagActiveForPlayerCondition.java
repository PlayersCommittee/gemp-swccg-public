package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified ModifierFlag is active for the specified player.
 */
public class FlagActiveForPlayerCondition implements Condition {
    private ModifierFlag _modifierFlag;
    private String _playerId;

    /**
     * Creates a condition that is fulfilled when the specified ModifierFlag is active for the specified player.
     * @param modifierFlag the ModifierFlag
     * @param playerId the player
     */
    public FlagActiveForPlayerCondition(ModifierFlag modifierFlag, String playerId) {
        _modifierFlag = modifierFlag;
        _playerId = playerId;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return modifiersQuerying.hasFlagActive(gameState, _modifierFlag, _playerId);
    }
}
