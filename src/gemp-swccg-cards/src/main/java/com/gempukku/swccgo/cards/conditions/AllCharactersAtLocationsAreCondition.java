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
 * A condition that is fulfilled when all characters for a specified player at specified locations are cards that are
 * accepted by a specified filter.
 */
public class AllCharactersAtLocationsAreCondition implements Condition {
    private int _permSourceCardId;
    private String _playerId;
    private Filter _locationFilter;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when all characters for a specified player at specified locations are cards that are
     * accepted by a specified filter.
     * @param source the card that is checking this condition
     * @param playerId the player
     * @param locationFilter the filter for locations
     * @param filters the filter
     */
    public AllCharactersAtLocationsAreCondition(PhysicalCard source, String playerId, Filterable locationFilter, Filterable filters) {
        _permSourceCardId = source.getPermanentCardId();
        _playerId = playerId;
        _locationFilter = Filters.and(locationFilter);
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        Collection<PhysicalCard> characters = Filters.filterActive(gameState.getGame(), source, Filters.and(Filters.owner(_playerId), Filters.character, Filters.at(_locationFilter)));
        if (characters.isEmpty())
            return false;

        for (PhysicalCard character : characters) {
            if (!Filters.and(_filters).accepts(gameState, modifiersQuerying, character))
                return false;
        }

        return true;
    }
}

