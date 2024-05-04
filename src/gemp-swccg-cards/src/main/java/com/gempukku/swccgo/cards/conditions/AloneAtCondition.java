package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is alone at a location accepted by the specified locationFilter.
 * or when a card accepted by the specified cardFilter is alone at a location accepted by the specified locationFilter.
 */
public class AloneAtCondition implements Condition {
    private Integer _permSourceCardId;
    private Integer _permCardId;
    private Filter _cardFilter;
    private Filter _locationFilter;

    /**
     * Creates a condition that is fulfilled when the specified card is alone at a location accepted by the specified
     * locationFilter.
     * @param card the card
     * @param locationFilter the location filter
     */
    public AloneAtCondition(PhysicalCard card, Filterable locationFilter) {
        _permCardId = card.getPermanentCardId();
        _cardFilter = null;
        _locationFilter = Filters.and(locationFilter);
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified cardFilter is alone at a location accepted
     * by the specified locationFilter.
     * @param source the card that is checking this condition
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     */
    public AloneAtCondition(PhysicalCard source, Filterable cardFilter, Filterable locationFilter) {
        _permSourceCardId = source.getPermanentCardId();
        _cardFilter = Filters.and(cardFilter);
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (card != null)
            return Filters.and(Filters.alone, Filters.at(_locationFilter)).accepts(gameState, modifiersQuerying, card);
        else
            return Filters.canSpot(gameState.getGame(), source, Filters.and(_cardFilter, Filters.alone, Filters.at(_locationFilter)));
    }
}
