package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is at the same system as a card (or a specified number of
 * cards) accepted by the specified filter.
 */
public class AtSameSystemAsCondition implements Condition {
    private int _permSourceCardId;
    private int _permCardId;
    private int _count;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when the specified card is at the same system as a card accepted by the
     * specified filter.
     * @param card the card (also the card that is checking this condition)
     * @param filters the filter
     */
    public AtSameSystemAsCondition(PhysicalCard card, Filterable filters) {
        this(card, 1, filters);
    }

    /**
     * Creates a condition that is fulfilled when the specified card is at the same system as at least a specified number
     * of cards accepted by the specified filter.
     * @param card the card (also the card that is checking this condition)
     * @param count the number of cards
     * @param filters the filter
     */
    public AtSameSystemAsCondition(PhysicalCard card, int count, Filterable filters) {
        _permSourceCardId = card.getPermanentCardId();
        _permCardId = card.getPermanentCardId();
        _count = count;
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (_count == 1) {
            return Filters.at(Filters.sameSystemAs(source, _filters)).accepts(gameState, modifiersQuerying, card);
        }
        else {
            Filter filterToUse = Filters.and(Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters)), Filters.atSameSystem(card));
            return Filters.canSpot(gameState.getGame(), source, _count, filterToUse);
        }
    }
}
