package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is "present with" a card (or at least a specified number of
 * cards) accepted by the specified filter.
 */
public class PresentWithCondition implements Condition {
    private int _permCardId;
    private int _count;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when the specified card is "present with" a card accepted by the specified filter.
     * @param card the card
     * @param filters the filter
     */
    public PresentWithCondition(PhysicalCard card, Filterable filters) {
        this(card, 1, filters);
    }

    /**
     * Creates a condition that is fulfilled when the specified card is "present with" at least a specified number of
     * cards accepted by the specified filter.
     * @param card the card
     * @param count the number of cards
     * @param filters the filter
     */
    public PresentWithCondition(PhysicalCard card, int count, Filterable filters) {
        _permCardId = card.getPermanentCardId();
        _count = count;
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.presentWith(card, _count, _filters).accepts(gameState, modifiersQuerying, card);
    }
}
