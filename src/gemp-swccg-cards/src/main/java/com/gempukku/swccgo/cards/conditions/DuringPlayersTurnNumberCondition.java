package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled during the player's specified turn number.
 */
public class DuringPlayersTurnNumberCondition implements Condition {
    private String _playerId;
    private int _turnNumber;

    /**
     * Creates a condition that is fulfilled during the player's specified turn number.
     * @param playerId the player
     * @param turnNumber the turn number
     */
    public DuringPlayersTurnNumberCondition(String playerId, int turnNumber) {
        _playerId = playerId;
        _turnNumber = turnNumber;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return _playerId.equals(gameState.getCurrentPlayerId()) && (gameState.getPlayersLatestTurnNumber(_playerId) == _turnNumber);
    }
}
