package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified player has a (podrace) race total more than X.
 */
public class RaceTotalMoreThanCondition implements Condition {
    private String _playerId;
    private float _total;

    /**
     * Creates a condition that is fulfilled when the specified player has a (podrace) race total more than X.
     * @param playerId the filter
     * @param total the total to compare against
     */
    public RaceTotalMoreThanCondition(String playerId, int total) {
        _playerId = playerId;
        _total = total;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return modifiersQuerying.getHighestRaceTotal(gameState, _playerId) > _total;
    }
}
