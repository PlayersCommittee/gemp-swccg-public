package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Map;

/**
 * A condition that is fulfilled when the specified card is with a card (or a specified number of cards) accepted by the
 * specified filter. This has the same meaning as "at same location as".
 */
public class WithCondition implements Condition {
    private int _permSourceCardId;
    private int _permCardId;
    private int _min;
    private Map<InactiveReason, Boolean> _spotOverrides;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when the specified card is with a card accepted by the specified filter.
     * @param card the card (also the card that is checking this condition)
     * @param filters the filter
     */
    public WithCondition(PhysicalCard card, Filterable filters) {
        this(card, 1, filters);
    }

    /**
     * Creates a condition that is fulfilled when the specified card is with a card accepted by the specified filter.
     * @param card the card (also the card that is checking this condition)
     * @param spotOverrides overrides for which inactive cards are visible to this condition check
     * @param filters the filter
     */
    public WithCondition(PhysicalCard card, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        this(card, 1, spotOverrides, filters);
    }

    /**
     * Creates a condition that is fulfilled when the specified card is with at least a specified number of cards accepted
     * by the specified filter.
     * @param card the card (also the card that is checking this condition)
     * @param min the number of cards
     * @param filters the filter
     */
    public WithCondition(PhysicalCard card, int min, Filterable filters) {
        this(card, min, null, filters);
    }

    /**
     * Creates a condition that is fulfilled when the specified card is with at least a specified number of cards accepted
     * by the specified filter.
     * @param card the card (also the card that is checking this condition)
     * @param min the number of cards
     * @param spotOverrides overrides for which inactive cards are visible to this condition check
     * @param filters the filter
     */
    public WithCondition(PhysicalCard card, int min, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        _permSourceCardId = card.getPermanentCardId();
        _permCardId = card.getPermanentCardId();
        _min = min;
        _spotOverrides = spotOverrides;
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (_min == 1) {
            Filter filterToUse = Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters));
            return Filters.with(source, _spotOverrides, filterToUse).accepts(gameState, modifiersQuerying, card);
        }
        else {
            Filter filterToUse = Filters.and(Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters)), Filters.with(card));
            return Filters.canSpot(gameState.getGame(), source, _min, _spotOverrides, filterToUse);
        }
    }
}
