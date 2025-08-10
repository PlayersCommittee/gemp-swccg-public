package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is orbiting the system of the specified name,
 * or when a card accepted by the specified cardFilter is orbiting the system of the specified name.
 */
public class OrbitingCondition implements Condition {
    private Integer _permCardId;
    private Filter _cardFilter;
    private String _system;

    /**
     * Creates a condition that is fulfilled when the specified card is orbiting the system of the specified name.
     * @param card the card
     * @param system the name of the system
     */
    public OrbitingCondition(PhysicalCard card, String system) {
        _permCardId = card.getPermanentCardId();
        _system = system;
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified cardFilter is orbiting the system of the
     * specified name.
     * @param cardFilter the card filter
     * @param system the name of the system
     */
    public OrbitingCondition(Filterable cardFilter, String system) {
        _cardFilter = Filters.and(cardFilter);
        _system = system;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (card != null) {
            return Filters.isOrbiting(_system).accepts(gameState, modifiersQuerying, card);
        }
        else {
            return Filters.canSpotFromTopLocationsOnTable(gameState.getGame(), Filters.and(_cardFilter, Filters.isOrbiting(_system)));
        }
    }
}
