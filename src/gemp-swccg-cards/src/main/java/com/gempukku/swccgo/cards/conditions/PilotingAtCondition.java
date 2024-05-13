package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is piloting specified cards at a location accepted by the specified filter.
 */
public class PilotingAtCondition implements Condition {
    private int _permCardId;
    private Filter _pilotedFilter;
    private Filter _locationFilter;

    /**
     * Creates a condition that is fulfilled when the specified card is piloting at a location accepted by the location filter.
     * @param card the card
     * @param locationFilter the location filter
     */
    public PilotingAtCondition(PhysicalCard card, Filterable locationFilter) {
        this(card, Filters.any, locationFilter);
    }

    /**
     * Creates a condition that is fulfilled when the specified card is piloting a card accepted by the piloted filter
     * at a location accepted by the location filter.
     * @param card the card
     * @param pilotedFilter the filter for the starship or vehicle piloted
     * @param locationFilter the location filter
     */
    public PilotingAtCondition(PhysicalCard card, Filterable pilotedFilter, Filterable locationFilter) {
        _permCardId = card.getPermanentCardId();
        _pilotedFilter = Filters.and(pilotedFilter);
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.piloting(Filters.and(_pilotedFilter, Filters.at(_locationFilter))).accepts(gameState, modifiersQuerying, card);
    }
}
