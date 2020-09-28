package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when a card accepted specified filter has been Completed.
 */
public class UntinniEffectCompletedCondition implements Condition {
    private String _playerId;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when a card accepted specified filter has been Completed.
     *
     * @param filters the filter
     */
    public UntinniEffectCompletedCondition(String playerId, Filterable filters) {
        _playerId = playerId;
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return modifiersQuerying.hasCompletedUtinniEffect(gameState, _playerId, 1, _filters);
    }
}
