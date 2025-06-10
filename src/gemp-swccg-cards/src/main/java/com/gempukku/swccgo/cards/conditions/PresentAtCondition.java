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
 * A condition that is fulfilled when the specified card is "present" at a location accepted by the specified filter.
 */
public class PresentAtCondition implements Condition {
    private int _permCardId;
    private Filter _cardToBePresentFilter;
    private Filter _locationFilter;

    /**
     * Creates a condition that is fulfilled when the specified card is "present" at a location accepted by the specified filter.
     * @param card the card
     * @param filters the filter
     */
    public PresentAtCondition(PhysicalCard card, Filterable filters) {
        _permCardId = card.getPermanentCardId();
        _locationFilter = Filters.and(filters);
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified filter is "present" at a location accepted by the specified filter.
     * @param cardToBePresentFilter the filter for which card is present
     * @param locationFilter the filter for the location
     */
    public PresentAtCondition(Filter cardToBePresentFilter, Filterable locationFilter) {
        _cardToBePresentFilter = cardToBePresentFilter;
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (_cardToBePresentFilter != null) {
            Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), null, _cardToBePresentFilter);
            for(PhysicalCard card: cards) {
                if (Filters.presentAt(_locationFilter).accepts(gameState, modifiersQuerying, card)) {
                    return true;
                }
            }
            return false;
        } else {
            PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

            return Filters.presentAt(_locationFilter).accepts(gameState, modifiersQuerying, card);
        }
    }
}
