package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified player occupies more locations accepted by a specified filter than opponent.
 */
public class OccupiesMoreThanOpponentCondition implements Condition {
    private String _playerId;
    private Filter _locationFilter;

    /**
     * Creates a condition that is fulfilled when the specified player occupies a location accepted by the specified filter.
     * @param playerId the player
     * @param locationFilter the location filter
     */
    public OccupiesMoreThanOpponentCondition(String playerId, Filter locationFilter) {
        _playerId = playerId;
        _locationFilter = locationFilter;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return Filters.countTopLocationsOnTable(gameState.getGame(), Filters.and(Filters.occupies(_playerId), _locationFilter))
                > Filters.countTopLocationsOnTable(gameState.getGame(), Filters.and(Filters.occupies(gameState.getOpponent(_playerId)), _locationFilter));
    }
}
