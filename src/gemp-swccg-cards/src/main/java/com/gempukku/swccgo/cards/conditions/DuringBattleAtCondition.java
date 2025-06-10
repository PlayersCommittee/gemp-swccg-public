package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled during battle at a location accepted by the specified filter.
 */
public class DuringBattleAtCondition implements Condition {
    private Filter _locationFilter;

    /**
     * Creates a condition that is fulfilled during battle at a location accepted by the specified location filter.
     * @param locationFilter the location filter
     */
    public DuringBattleAtCondition(Filterable locationFilter) {
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return gameState.isDuringBattle()
                && Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, gameState.getBattleLocation());
    }
}
