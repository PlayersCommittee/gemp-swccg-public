package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when a card accepted by the specified filter is "at" the same or related site that the
 * specified card is "at" (or "at" the site or "at" a related site if the input card is a location).
 */
public class AtSameOrRelatedSiteCondition implements Condition {
    private int _permSourceCardId;
    private int _permCardId;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified filter is "at" the same or related
     * site that the specified card is "at" (or "at" the site or "at" a related site if the input card is a location).
     * @param card the card (also the card that is checking this condition)
     * @param filters the filter
     */
    public AtSameOrRelatedSiteCondition(PhysicalCard card, Filterable filters) {
        _permSourceCardId = card.getPermanentCardId();
        _permCardId = card.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        Filter filterToUse = Filters.and(Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters)), Filters.atSameOrRelatedSite(card));
        return Filters.canSpot(gameState.getGame(), source, filterToUse);
    }
}
