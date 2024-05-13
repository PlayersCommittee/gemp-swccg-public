package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Map;

/**
 * A condition that is fulfilled when the specified player controls the specified location, or controls a location (or
 * at least a specified number of locations) accepted by the specified filter, or if the specified card controls a
 * location accepted by the specified filter.
 */
public class ControlsCondition implements Condition {
    private String _playerId;
    private Integer _permLocationCardId;
    private Integer _permCardId;
    private int _num;
    private Map<InactiveReason, Boolean> _spotOverrides;
    private Filter _locationFilter;

    /**
     * Creates a condition that is fulfilled when the specified player controls the specified location.
     * @param playerId the player
     * @param location the location
     */
    public ControlsCondition(String playerId, PhysicalCard location) {
        _playerId = playerId;
        _permLocationCardId = location.getPermanentCardId();
    }

    /**
     * Creates a condition that is fulfilled when the specified player controls a location accepted by the specified filter.
     * @param playerId the player
     * @param locationFilter the location filter
     */
    public ControlsCondition(String playerId, Filter locationFilter) {
        this(playerId, 1, locationFilter);
    }

    /**
     * Creates a condition that is fulfilled when the specified player controls at least a specified number of locations
     * accepted by the specified filter.
     * @param playerId the player
     * @param num the number of locations
     * @param locationFilter the location filter
     */
    public ControlsCondition(String playerId, int num, Filter locationFilter) {
        this(playerId, num, null, locationFilter);
    }

    /**
     * Creates a condition that is fulfilled when the specified player controls at least a specified number of locations
     * accepted by the specified filter.
     * @param playerId the player
     * @param num the number of locations
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param locationFilter the location filter
     */
    public ControlsCondition(String playerId, int num, Map<InactiveReason, Boolean> spotOverrides, Filter locationFilter) {
        _playerId = playerId;
        _num = num;
        _spotOverrides = spotOverrides;
        _locationFilter = locationFilter;
    }

    /**
     * Creates a condition that is fulfilled when the specified card controls a location accepted by the specified filter.
     * @param card the card
     * @param locationFilter the location filter
     */
    public ControlsCondition(PhysicalCard card, Filter locationFilter) {
        _permCardId = card.getPermanentCardId();
        _playerId = card.getOwner();
        _locationFilter = locationFilter;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard location = gameState.findCardByPermanentId(_permLocationCardId);
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (location != null)
            return Filters.controls(_playerId, _spotOverrides).accepts(gameState, modifiersQuerying, location);
        else if (card != null)
            return !Filters.undercover_spy.accepts(gameState, modifiersQuerying, card) && Filters.at(Filters.and(_locationFilter, Filters.controls(_playerId, _spotOverrides))).accepts(gameState, modifiersQuerying, card);
        else
            return Filters.canSpotFromTopLocationsOnTable(gameState.getGame(), _num, Filters.and(_locationFilter, Filters.controls(_playerId, _spotOverrides)));
    }
}
