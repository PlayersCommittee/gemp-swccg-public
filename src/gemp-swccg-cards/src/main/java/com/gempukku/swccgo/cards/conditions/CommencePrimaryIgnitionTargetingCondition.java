package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.actions.CommencePrimaryIgnitionState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified system is being targeted by Commence Primary Ignition.
 */
public class CommencePrimaryIgnitionTargetingCondition implements Condition {
    private Filter _systemFilter;

    /**
     * Creates a condition that is fulfilled when a system accepted by the filter is being targeted by Commence Primary Ignition.
     * @param systemFilter the system filter
     */
    public CommencePrimaryIgnitionTargetingCondition(Filterable systemFilter) {
        _systemFilter = Filters.and(systemFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        EpicEventState epicEventState = gameState.getEpicEventState();
        if (epicEventState != null && epicEventState.getEpicEventType() == EpicEventState.Type.COMMENCE_PRIMARY_IGNITION) {
            CommencePrimaryIgnitionState commencePrimaryIgnitionState = (CommencePrimaryIgnitionState) epicEventState;
            return Filters.and(_systemFilter).accepts(gameState, modifiersQuerying, commencePrimaryIgnitionState.getPlanetSystem());
        }
        return false;
    }
}
