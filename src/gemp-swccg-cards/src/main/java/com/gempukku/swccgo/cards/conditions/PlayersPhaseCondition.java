package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled during the specified player's phase.
 */
public class PlayersPhaseCondition implements Condition {
    private String _playerId;
    private Phase _phase;

    /**
     * Creates a condition that is fulfilled during the specified player's phase.
     * @param playerId the player
     * @param phase the phase
     */
    public PlayersPhaseCondition(String playerId, Phase phase) {
        _playerId = playerId;
        _phase = phase;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return gameState.getCurrentPlayerId().equals(_playerId)
                && gameState.getCurrentPhase().equals(_phase);
    }
}
