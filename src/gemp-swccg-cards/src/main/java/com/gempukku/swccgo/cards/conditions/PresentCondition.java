package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is "present" at a location, or if a card (or at least a specified
 * number of cards) accepted by the specified filter is "present" at the location the specified card is "at", or if a card
 * (or at least a specified number of cards) accepted by the specified filter is "present" at the specified card if the
 * specified card is a location.
 */
public class PresentCondition implements Condition {
    private int _permSourceCardId;
    private int _count;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when the specified card is "present" at a location.
     * @param card the card
     */
    public PresentCondition(PhysicalCard card) {
        _permSourceCardId = card.getPermanentCardId();
        _count = 1;
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified filter is "present" at the specified
     * card (if the specified card is a location) or the location the specified card is "at".
     * @param card the card
     * @param filters the filter
     */
    public PresentCondition(PhysicalCard card, Filterable filters) {
        this(card, 1, filters);
    }

    /**
     * Creates a condition that is fulfilled when at least a specified number of cards accepted by the specified filter
     * are "present" at the specified card (if the specified card is a location) or the location the specified card is "at".
     * @param card the card
     * @param count the number of cards
     * @param filters the filter
     */
    public PresentCondition(PhysicalCard card, int count, Filterable filters) {
        _permSourceCardId = card.getPermanentCardId();
        _count = count;
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        if (_filters == null) {
            return modifiersQuerying.getLocationThatCardIsPresentAt(gameState, source) != null;
        }

        Filter filterToUse = Filters.or(_filters, Filters.hasPermanentWeapon(_filters));
        return Filters.canSpot(gameState.getGame(), source, _count, Filters.and(filterToUse, Filters.present(source)));
    }
}
