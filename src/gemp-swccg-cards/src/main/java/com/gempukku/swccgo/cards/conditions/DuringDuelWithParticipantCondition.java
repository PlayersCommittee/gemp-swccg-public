package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled during a duel that includes a card accepted by the specified filter participating.
 */
public class DuringDuelWithParticipantCondition implements Condition {
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled during a duel that includes a card accepted by the specified filter participating.
     * @param filters the filter
     */
    public DuringDuelWithParticipantCondition(Filterable filters) {
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (!gameState.isDuringDuel())
            return false;

        Filter filterToUse = Filters.and(Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters)));
        return !Filters.filterCount(gameState.getDuelState().getDuelParticipants(), gameState.getGame(), 1, filterToUse).isEmpty();
    }
}
