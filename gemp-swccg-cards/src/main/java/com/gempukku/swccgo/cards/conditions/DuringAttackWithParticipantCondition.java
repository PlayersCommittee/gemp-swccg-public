package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled during an attack that includes a card (or at least a specified number of cards) accepted
 * by the specified filter participating.
 */
public class DuringAttackWithParticipantCondition implements Condition {
    private int _count;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled during an attack that includes a card accepted by the specified filter participating.
     * @param filters the filter
     */
    public DuringAttackWithParticipantCondition(Filterable filters) {
        this(1, filters);
    }

    /**
     * Creates a condition that is fulfilled during an attack that includes at least a specified number of cards accepted
     * by the specified filter participating.
     * @param count the number of cards
     * @param filters the filter
     */
    public DuringAttackWithParticipantCondition(int count, Filterable filters) {
        _count = count;
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (!gameState.isDuringAttack())
            return false;

        Filter filterToUse = Filters.and(Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters)));
        return Filters.filterCount(gameState.getAttackState().getAllCardsParticipating(), gameState.getGame(), _count, filterToUse).size() >= _count;
    }
}
