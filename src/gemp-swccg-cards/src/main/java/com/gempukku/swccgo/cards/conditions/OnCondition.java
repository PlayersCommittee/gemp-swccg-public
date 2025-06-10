package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is on the system of the specified name,
 * or when a card accepted by the specified cardFilter is on the system of the specified name.
 */
public class OnCondition implements Condition {
    private Integer _permSourceCardId;
    private Integer _permCardId;
    private Filter _cardFilter;
    private String _system;

    /**
     * Creates a condition that is fulfilled when the specified card is on the system of the specified name.
     * @param card the card
     * @param system the name of the system
     */
    public OnCondition(PhysicalCard card, String system) {
        _permCardId = card.getPermanentCardId();
        _system = system;
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified cardFilter is on the system of the
     * specified name.
     * @param source the card that is checking this condition
     * @param cardFilter the card filter
     * @param system the name of the system
     */
    public OnCondition(PhysicalCard source, Filterable cardFilter, String system) {
        _permSourceCardId = source.getPermanentCardId();
        _cardFilter = Filters.and(cardFilter);
        _system = system;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (card != null) {
            return Filters.on(_system).accepts(gameState, modifiersQuerying, card);
        }
        else {
            Filter filterToUse = Filters.and(Filters.or(_cardFilter, Filters.hasPermanentAboard(_cardFilter), Filters.hasPermanentWeapon(_cardFilter)));
            return Filters.canSpot(gameState.getGame(), source, Filters.and(filterToUse, Filters.on(_system)));
        }
    }
}
