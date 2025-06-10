package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled during a Force drain at a location accepted by the specified filter.
 */
public class DuringForceDrainAtCondition implements Condition {
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled during a Force drain at a location accepted by the specified filter.
     * @param filters the filter
     */
    public DuringForceDrainAtCondition(Filterable filters) {
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return gameState.isDuringForceDrain()
                && Filters.and(_filters).accepts(gameState, modifiersQuerying, gameState.getForceDrainLocation());
    }
}
