package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled if the specified player has lost a Podrace.
 */
public class LostPodraceCondition implements Condition {
    private String _playerId;

    /**
     * Creates a condition that is fulfilled if the specified player has lost a Podrace.
     * @param playerId the player
     */
    public LostPodraceCondition(String playerId) {
        _playerId = playerId;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return false;
    }
}
