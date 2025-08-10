package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified player has a Senate majority.
 */
public class HasSenateMajorityCondition implements Condition {
    private String _playerId;

    /**
     * Creates a condition that is fulfilled when the specified player has a Senate majority.
     * @param playerId the player
     */
    public HasSenateMajorityCondition(String playerId) {
        _playerId = playerId;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        String playerWithSenateMajority = modifiersQuerying.getPlayerWithSenateMajority(gameState);
        return _playerId.equals(playerWithSenateMajority);
    }
}
