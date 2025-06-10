package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Map;


/**
 * A condition that is fulfilled when no cards (or less than a specified number of cards) accepted by the specified
 * filter can be spotted on the table by a specified card.
 */
public class CantSpotCondition implements Condition {
    private int _permSourceCardId;
    private int _count;
    private Map<InactiveReason, Boolean> _spotOverrides;
    private Filter _filter;

    /**
     * Creates a condition that is fulfilled when no cards accepted by the specified filter are on the table.
     * @param source the card that is checking this condition
     * @param filter the filter
     */
    public CantSpotCondition(PhysicalCard source, Filterable filter) {
        this(source, 1, null, filter);
    }

    /**
     * Creates a condition that is fulfilled when less than a specified number of cards accepted by the specified filter
     * are on the table.
     * @param source the card that is checking this condition
     * @param count the number of cards
     * @param filter the filter
     */
    public CantSpotCondition(PhysicalCard source, int count, Filterable filter) {
        this(source, count, null, filter);
    }

    /**
     * Creates a condition that is fulfilled when no cards accepted by the specified filter are on the table.
     * @param source the card that is checking this condition
     * @param spotOverrides overrides for which inactive cards are visible to this condition check
     * @param filter the filter
     */
    public CantSpotCondition(PhysicalCard source, Map<InactiveReason, Boolean> spotOverrides, Filterable filter) {
        this(source, 1, spotOverrides, filter);
    }

    /**
     * Creates a condition that is fulfilled when less than a specified number of cards accepted by the specified filter
     * are on the table.
     * @param source the card that is checking this condition
     * @param count the number of cards
     * @param spotOverrides overrides for which inactive cards are visible to this condition check
     * @param filter the filter
     */
    public CantSpotCondition(PhysicalCard source, int count, Map<InactiveReason, Boolean> spotOverrides, Filterable filter) {
        _permSourceCardId = source.getPermanentCardId();
        _count = count;
        _spotOverrides = spotOverrides;
        _filter = Filters.and(filter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        return !Filters.canSpot(gameState.getGame(), source, _count, _spotOverrides, _filter);
    }
}
