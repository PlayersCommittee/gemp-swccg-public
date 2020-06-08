package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Map;

/**
 * A condition that is fulfilled when a card or a specified number of cards accepted by the specified filter can be
 * spotted on the table by a specified card.
 */
public class OnTableCondition implements Condition {
    private int _permSourceCardId;
    private int _count;
    private boolean _exactly;
    private Map<InactiveReason, Boolean> _spotOverrides;
    private Filter _filter;

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified filter is on the table.
     * @param source the card that is checking this condition
     * @param filter the filter
     */
    public OnTableCondition(PhysicalCard source, Filterable filter) {
        this(source, 1, null, filter);
    }

    /**
     * Creates a condition that is fulfilled when at least a specified number of cards accepted by the specified filter
     * are on the table.
     * @param source the card that is checking this condition
     * @param count the number of cards
     * @param filter the filter
     */
    public OnTableCondition(PhysicalCard source, int count, Filterable filter) {
        this(source, count, false, null, filter);
    }

    /**
     * Creates a condition that is fulfilled when at least a specified number of cards (or exactly a specified number of
     * cards) accepted by the specified filter are on the table.
     * @param source the card that is checking this condition
     * @param count the number of cards
     * @param exactly true if the number of cards must match the count exactly, otherwise false
     * @param filter the filter
     */
    public OnTableCondition(PhysicalCard source, int count, boolean exactly, Filterable filter) {
        this(source, count, exactly, null, filter);
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified filter is on the table.
     * @param source the card that is checking this condition
     * @param spotOverrides overrides for which inactive cards are visible to this condition check
     * @param filter the filter
     */
    public OnTableCondition(PhysicalCard source, Map<InactiveReason, Boolean> spotOverrides, Filterable filter) {
        this(source, 1, false, spotOverrides, filter);
    }

    /**
     * Creates a condition that is fulfilled when at least a specified number of cards accepted by the specified filter
     * are on the table.
     * @param source the card that is checking this condition
     * @param count the number of cards
     * @param spotOverrides overrides for which inactive cards are visible to this condition check
     * @param filter the filter
     */
    public OnTableCondition(PhysicalCard source, int count, Map<InactiveReason, Boolean> spotOverrides, Filterable filter) {
        this(source, count, false, spotOverrides, filter);
    }

    /**
     * Creates a condition that is fulfilled when at least a specified number of cards (or exactly a specified number of
     * cards) accepted by the specified filter are on the table.
     * @param source the card that is checking this condition
     * @param count the number of cards
     * @param exactly true if the number of cards must match the count exactly, otherwise false
     * @param spotOverrides overrides for which inactive cards are visible to this condition check
     * @param filter the filter
     */
    public OnTableCondition(PhysicalCard source, int count, boolean exactly, Map<InactiveReason, Boolean> spotOverrides, Filterable filter) {
        _permSourceCardId = source.getPermanentCardId();
        _count = count;
        _exactly = exactly;
        _spotOverrides = spotOverrides;
        _filter = Filters.and(filter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        Filter filterToUse = Filters.or(_filter, Filters.hasPermanentAboard(_filter), Filters.hasPermanentWeapon(_filter));
        if (_exactly) {
            return Filters.countActive(gameState.getGame(), source, _spotOverrides, filterToUse) == _count;
        }
        else {
            return Filters.canSpot(gameState.getGame(), source, _count, _spotOverrides, filterToUse);
        }
    }
}

