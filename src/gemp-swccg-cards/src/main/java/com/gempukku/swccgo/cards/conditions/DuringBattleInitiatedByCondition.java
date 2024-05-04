package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled during a battle initiated by the specified player.
 */
public class DuringBattleInitiatedByCondition implements Condition {
    private String _playerId;

    /**
     * Creates a condition that is fulfilled during a battle initiated by the specified player.
     * @param playerId the player
     */
    public DuringBattleInitiatedByCondition(String playerId) {
        _playerId = playerId;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return gameState.isDuringBattleInitiatedBy(_playerId);
    }
}
