package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled if the specified player has won a Podrace.
 */
public class WonPodraceCondition implements Condition {
    private String _playerId;

    /**
     * Creates a condition that is fulfilled if the specified player has won a Podrace.
     * @param playerId the player
     */
    public WonPodraceCondition(String playerId) {
        _playerId = playerId;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return GameConditions.hasWonPodrace(gameState.getGame(), _playerId);
    }
}
