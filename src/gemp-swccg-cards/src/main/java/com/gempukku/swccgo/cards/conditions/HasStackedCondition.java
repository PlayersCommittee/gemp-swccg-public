package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card has a card accepted by the specified filter stacked (face up) on it.
 */
public class HasStackedCondition implements Condition {
    private int _permCardId;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when the specified card has a card accepted by the specified filter stacked (face up) on it.
     * @param card the card
     * @param filters the filter
     */
    public HasStackedCondition(PhysicalCard card, Filterable filters) {
        _permCardId = card.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.hasStacked(_filters).accepts(gameState, modifiersQuerying, card);
    }
}
