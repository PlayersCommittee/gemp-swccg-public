package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;

/**
 * A condition that is fulfilled when all the ability for a specified player at specified locations is provided
 * by cards that are accepted by a specified filter.
 */
public class AllAbilityAtLocationProvidedByCondition implements Condition {
    private int _permSourceCardId;
    private String _playerId;
    private Filter _locationFilter;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when all the ability for a specified player at specified locations is provided
     * by cards that are accepted by a specified filter.
     * @param source the card that is checking this condition
     * @param playerId the player
     * @param locationFilter the filter for locations
     * @param filters the filter
     */
    public AllAbilityAtLocationProvidedByCondition(PhysicalCard source, String playerId, Filterable locationFilter, Filterable filters) {
        _permSourceCardId = source.getPermanentCardId();
        _playerId = playerId;
        _locationFilter = Filters.and(locationFilter);
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        Collection<PhysicalCard> cardsWithAbility = Filters.filterActive(gameState.getGame(), source, Filters.and(Filters.owner(_playerId), Filters.hasAbilityOrHasPermanentPilotWithAbility, Filters.at(_locationFilter)));
        if (cardsWithAbility.isEmpty())
            return false;

        for (PhysicalCard cardWithAbility : cardsWithAbility) {
            if (!Filters.and(_filters).accepts(gameState, modifiersQuerying, cardWithAbility))
                return false;
        }

        return true;
    }
}

