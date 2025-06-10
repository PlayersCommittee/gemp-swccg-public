package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Map;

/**
 * A condition that is fulfilled when a card (or a specified number of cards) accepted by the specified filter
 * are "at" the location the specified card is "at" (or "at" the location if the specified card is a location).
 */
public class HereCondition implements Condition {
    private int _permCardId;
    private int _count;
    private boolean _exactly;
    private Filter _filters;
    private Map<InactiveReason, Boolean> _spotOverrides;

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified filter is "at" the location the specified
     * card is "at" (or "at" the location if the specified card is a location).
     * @param card the card (also the card that is checking this condition)
     * @param filters the filter
     */
    public HereCondition(PhysicalCard card, Filterable filters) {
        this(card, 1, false, filters);
    }


    /**
     * Creates a condition that is fulfilled when a card accepted by the specified filter is "at" the location the specified
     * card is "at" (or "at" the location if the specified card is a location).
     * @param card the card (also the card that is checking this condition)
     * @param spotOverrides the spotoverrides
     * @param filters the filter
     */
    public HereCondition(PhysicalCard card, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        this(card, 1, false, spotOverrides, filters);
    }

    /**
     * Creates a condition that is fulfilled when at least a specified number of cards accepted by the specified filter
     * is "at" the location the specified card is "at" (or "at" the location if the specified card is a location).
     * @param card the card (also the card that is checking this condition)
     * @param count the number of cards
     * @param filters the filter
     */
    public HereCondition(PhysicalCard card, int count, Filterable filters) {
        this(card, count, false, filters);
    }

    /**
     * Creates a condition that is fulfilled when at least a specified number of cards (or an exact number of cards)
     * accepted by the specified filter is "at" the location the specified card is "at" (or "at" the location if the
     * specified card is a location).
     * @param card the card (also the card that is checking this condition)
     * @param count the number of cards
     * @param exactly true if the number of cards must match the count exactly, otherwise false
     * @param filters the filter
     */
    public HereCondition(PhysicalCard card, int count, boolean exactly, Filterable filters) {
        this(card, count, exactly, null, filters);
    }

    /**
     * Creates a condition that is fulfilled when at least a specified number of cards (or an exact number of cards)
     * accepted by the specified filter is "at" the location the specified card is "at" (or "at" the location if the
     * specified card is a location).
     * @param card the card (also the card that is checking this condition)
     * @param count the number of cards
     * @param exactly true if the number of cards must match the count exactly, otherwise false
     * @param spotOverrides the spotoverrides
     * @param filters the filter
     */
    public HereCondition(PhysicalCard card, int count, boolean exactly, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        _permCardId = card.getPermanentCardId();
        _count = count;
        _exactly = exactly;
        _filters = Filters.and(filters);
        _spotOverrides = spotOverrides;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (_exactly)
            return Filters.countActive(gameState.getGame(), card, _spotOverrides, Filters.and(_filters, Filters.here(card))) == _count;
        else
            return Filters.canSpot(gameState.getGame(), card, _count, _spotOverrides, Filters.and(_filters, Filters.here(card)));
    }
}
