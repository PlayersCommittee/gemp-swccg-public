package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled during the specified player's next turn after this condition object is created.
 */
public class PlayersNextTurnCondition implements Condition {
    private String _playerId;
    private int _turnNumber;

    /**
     * Creates a condition that is fulfilled during the specified player's next turn after this condition object is created.
     * @param playerId the player
     * @param game the game
     */
    public PlayersNextTurnCondition(String playerId, SwccgGame game) {
        _playerId = playerId;
        _turnNumber = game.getGameState().getPlayersLatestTurnNumber(playerId) + 1;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return gameState.getCurrentPlayerId().equals(_playerId)
                && gameState.getPlayersLatestTurnNumber(_playerId) == _turnNumber;
    }
}
