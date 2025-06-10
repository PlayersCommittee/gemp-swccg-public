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
 * A condition that is fulfilled when the specified player controls the specified location with a card (or at least a
 * specified number of cards) accepted by the specified filter.
 */
public class ControlsWithCondition implements Condition {
    private int _permSourceCardId;
    private String _playerId;
    private Integer _permLocationCardId;
    private Filter _locationFilter;
    private int _count;
    private Map<InactiveReason, Boolean> _spotOverrides;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when the specified player controls the specified location with a card accepted
     * by the specified filter.
     * @param playerId the player
     * @param location the location
     * @param filters the filter
     */
    public ControlsWithCondition(String playerId, PhysicalCard location, Filterable filters) {
        this(playerId, location, 1, filters);
    }

    /**
     * Creates a condition that is fulfilled when the specified player controls the specified location with at least a
     * specified number of cards accepted by the specified filter.
     * @param playerId the player
     * @param location the location
     * @param count the number of cards
     * @param filters the filter
     */
    public ControlsWithCondition(String playerId, PhysicalCard location, int count, Filterable filters) {
        _permSourceCardId = location.getPermanentCardId();
        _playerId = playerId;
        _permLocationCardId = location.getPermanentCardId();
        _count = count;
        _filters = Filters.and(filters);
    }

    /**
     * Creates a condition that is fulfilled when the specified player controls a location accepted by the location filter
     * with a card accepted by the specified filter.
     * @param source the card that is checking this condition
     * @param playerId the player
     * @param locationFilter the location
     * @param filters the filter
     */
    public ControlsWithCondition(PhysicalCard source, String playerId, Filter locationFilter, Filterable filters) {
        this(source, playerId, 1, locationFilter, null, filters);
    }

    /**
     * Creates a condition that is fulfilled when the specified player controls at least a specified number of locations
     * accepted by the location filter with a card accepted by the specified filter.
     * @param source the card that is checking this condition
     * @param playerId the player
     * @param count the number of locations
     * @param locationFilter the location
     * @param filters the filter
     */
    public ControlsWithCondition(PhysicalCard source, String playerId, int count, Filter locationFilter, Filterable filters) {
        this(source, playerId, count, locationFilter, null, filters);
    }

    /**
     * Creates a condition that is fulfilled when the specified player controls at least a specified number of locations
     * accepted by the location filter with a card accepted by the specified filter.
     * @param source the card that is checking this condition
     * @param playerId the player
     * @param count the number of locations
     * @param locationFilter the location
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filters the filter
     */
    public ControlsWithCondition(PhysicalCard source, String playerId, int count, Filter locationFilter, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        _permSourceCardId = source.getPermanentCardId();
        _playerId = playerId;
        _count = count;
        _locationFilter = locationFilter;
        _spotOverrides = spotOverrides;
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard location = gameState.findCardByPermanentId(_permLocationCardId);

        Filter filterToUse = Filters.and(_filters, Filters.not(Filters.undercover_spy));

        if (location != null) {
            if (!Filters.controls(_playerId, _spotOverrides).accepts(gameState, modifiersQuerying, location))
                return false;

            return Filters.canSpot(gameState.getGame(), source, _count, _spotOverrides, Filters.and(filterToUse, Filters.owner(_playerId), Filters.at(location)));
        }
        else {
            return Filters.canSpotFromTopLocationsOnTable(gameState.getGame(), _count, Filters.and(_locationFilter, Filters.controls(_playerId, _spotOverrides), Filters.sameLocationAs(source, _spotOverrides, Filters.and(Filters.owner(_playerId), filterToUse))));
        }
    }
}
