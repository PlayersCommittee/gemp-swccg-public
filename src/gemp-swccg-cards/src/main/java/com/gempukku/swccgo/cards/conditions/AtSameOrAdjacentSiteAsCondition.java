package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is at the same or adjacent site as a card accepted by the specified filter.
 */
public class AtSameOrAdjacentSiteAsCondition implements Condition {
    private int _permSourceCardId;
    private int _permCardId;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when the specified card is at the same or adjacent site as a card accepted by the
     * specified filter.
     * @param card the card (also the card that is checking this condition)
     * @param filters the filter
     */
    public AtSameOrAdjacentSiteAsCondition(PhysicalCard card, Filterable filters) {
        _permSourceCardId = card.getPermanentCardId();
        _permCardId = card.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.at(Filters.sameOrAdjacentSiteAs(source, _filters)).accepts(gameState, modifiersQuerying, card);
    }
}
