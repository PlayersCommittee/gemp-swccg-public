package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when a card accepted by the specified filter is "at" the same or adjacent site that the
 * specified card is "at" (or "at" the site or "at" an adjacent site if the input card is a site).
 */
public class AtSameOrAdjacentSiteCondition implements Condition {
    private int _permSourceCardId;
    private int _permCardId;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified filter is "at" the same or adjacent
     * site that the specified card is "at" (or "at" the site or "at" an adjacent site if the input card is a site).
     * @param card the card (also the card that is checking this condition)
     * @param filters the filter
     */
    public AtSameOrAdjacentSiteCondition(PhysicalCard card, Filterable filters) {
        _permSourceCardId = card.getPermanentCardId();
        _permCardId = card.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        Filter filterToUse = Filters.and(Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters)), Filters.atSameOrAdjacentSite(card));
        return Filters.canSpot(gameState.getGame(), source, filterToUse);
    }
}
