package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card has a card accepted by the specified filter attached.
 */
public class HasAttachedCondition implements Condition {
    private int _permCardId;
    private Filter _filters;
    private int _compareValue;

    /**
     * Creates a condition that is fulfilled when the specified card has a card accepted by the specified filter attached.
     * @param card the card
     * @param filters the filter
     */
    public HasAttachedCondition(PhysicalCard card, Filterable filters) {
        this(card, filters, 1);
    }

    /**
     * Creates a condition that is fulfilled when the specified card has at least the compareValue number of card
     *      accepted by the specified filter attached.
     * @param card the card
     * @param filters the filter
     * @param compareValue the minimum number of cards (accepted by the filter) that are attached to the card
     */
    public HasAttachedCondition(PhysicalCard card, Filterable filters, int compareValue) {
        _permCardId = card.getPermanentCardId();
        _filters = Filters.and(filters);
        _compareValue = compareValue;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if(_compareValue <= 1) /// should be able to remove and just use the lower section?
            return Filters.hasAttached(_filters).accepts(gameState, modifiersQuerying, card);
        else {
            return (Filters.countAllOnTable(gameState.getGame(),Filters.and(Filters.attachedTo(card),_filters)) >= _compareValue);
        }
    }
}
