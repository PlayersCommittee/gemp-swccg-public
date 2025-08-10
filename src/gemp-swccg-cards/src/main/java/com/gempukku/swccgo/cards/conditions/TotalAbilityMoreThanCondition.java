package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collection;

/**
 * A condition that is fulfilled when the specified player has more than a specified total ability at a location accepted
 * by the specified filter.
 */
public class TotalAbilityMoreThanCondition implements Condition {
    private String _playerId;
    private float _ability;
    private Filter _locationFilter;

    /**
     * Creates a condition that is fulfilled when the specified player has more than X total ability at a location accepted
     * by the specified filter.
     * @param playerId the filter
     * @param ability the amount of total ability
     * @param locationFilter the location filter
     */
    public TotalAbilityMoreThanCondition(String playerId, float ability, Filterable locationFilter) {
        _playerId = playerId;
        _ability = ability;
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        Collection<PhysicalCard> locations = Filters.filterTopLocationsOnTable(gameState.getGame(), _locationFilter);
        for (PhysicalCard location : locations) {
            if (modifiersQuerying.getTotalAbilityAtLocation(gameState, _playerId, location, false, false, false, null, false, false, null) > _ability) {
                return true;
            }
        }
        return false;
    }
}
