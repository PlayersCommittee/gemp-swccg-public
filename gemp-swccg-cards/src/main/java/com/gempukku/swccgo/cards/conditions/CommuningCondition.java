package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when a card accepted by the filter is 'communing'
 */
public class CommuningCondition implements Condition {
    private Filter _filter;

    /**
     * Creates a condition that is fulfilled when a card accepted by the filter is 'communing'
     * @param filterable filterable
     */
    public CommuningCondition(Filterable filterable) {
        _filter = Filters.and(filterable);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return modifiersQuerying.isCommuning(gameState, _filter);    }
}
