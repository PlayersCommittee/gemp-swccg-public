package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled after the player's specified turn number.
 */
public class AfterPlayersTurnNumberCondition implements Condition {
    private String _playerId;
    private int _turnNumber;

    /**
     * Creates a condition that is fulfilled after the player's specified turn number.
     * @param playerId the player
     * @param turnNumber the turn number
     */
    public AfterPlayersTurnNumberCondition(String playerId, int turnNumber) {
        _playerId = playerId;
        _turnNumber = turnNumber;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        int lastCompletedTurnNumber = gameState.getPlayersLatestTurnNumber(_playerId);
        if (_playerId.equals(gameState.getCurrentPlayerId())) {
            lastCompletedTurnNumber--;
        }
        return lastCompletedTurnNumber >= _turnNumber;
    }
}
