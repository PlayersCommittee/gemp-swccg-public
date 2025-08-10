package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.SabaccState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;


/**
 * A condition that is fulfilled when a specified game of sabacc is in progress.
 */
public class DuringSabaccCondition implements Condition {
    private Filter _sabaccInterruptFilter;

    /**
     * Creates a condition that is fulfilled when a specified game of sabacc is in progress.
     * @param sabaccInterruptFilter the type of sabacc game filter (defined by the Interrupt that started the sabacc game)
     */
    public DuringSabaccCondition(Filterable sabaccInterruptFilter) {
        _sabaccInterruptFilter = Filters.and(sabaccInterruptFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        SabaccState sabaccState = gameState.getSabaccState();
        if (sabaccState == null)
            return false;

        return Filters.and(_sabaccInterruptFilter).accepts(gameState, modifiersQuerying, sabaccState.getSabaccInterrupt());
    }
}
