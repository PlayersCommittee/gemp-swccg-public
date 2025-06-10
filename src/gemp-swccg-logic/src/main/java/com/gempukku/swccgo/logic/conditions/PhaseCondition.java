package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled during the specified phase (or during the specified phase of the specified player's turn).
 */
public class PhaseCondition implements Condition {
    private Phase _phase;
    private String _playerId;

    /**
     * Creates a condition that is fulfilled during the specified phase.
     * @param phase the phase
     */
    public PhaseCondition(Phase phase) {
        this(phase, null);
    }

    /**
     * Creates a condition that is fulfilled during the specified phase of the specified player's turn.
     * @param phase the phase
     * @param playerId the player
     */
    public PhaseCondition(Phase phase, String playerId) {
        _phase = phase;
        _playerId = playerId;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return (_playerId == null || gameState.getCurrentPlayerId().equals(_playerId))
                && (_phase == null || gameState.getCurrentPhase() == _phase);
    }
}
