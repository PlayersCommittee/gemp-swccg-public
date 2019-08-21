package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

public class CanAddDestinyToPowerCondition implements Condition {
    private String _playerId;

    /**
     * Creates a condition that is fulfilled if a card accepted by the filter been played by the player this turn.
     *
     * @param playerId the player
     */
    public CanAddDestinyToPowerCondition(String playerId) {
        _playerId = playerId;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return !gameState.getGame().getModifiersQuerying().mayNotAddDestinyDrawsToPower(gameState, _playerId);
    }
}
