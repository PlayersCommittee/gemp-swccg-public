package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled during the specified player's turn.
 */
public class PlayersTurnCondition implements Condition {
    private String _playerId;

    /**
     * Creates a condition that is fulfilled during the specified player's turn.
     * @param playerId the player
     */
    public PlayersTurnCondition(String playerId) {
        _playerId = playerId;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return gameState.getCurrentPlayerId().equals(_playerId);
    }
}
