package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is in battle with a card (or a specified number of cards) accepted
 * by the specified filter.
 */
public class InBattleWithCondition implements Condition {
    private int _permCardId;
    private int _count;
    private boolean _exactly;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when the specified card is in battle with a card accepted by the specified filter.
     * @param card the card
     * @param filters the filter
     */
    public InBattleWithCondition(PhysicalCard card, Filterable filters) {
        this(card, 1, false, filters);
    }

    /**
     * Creates a condition that is fulfilled when at least a specified number of cards accepted by the specified filter
     * are in battle with the specified card.
     * @param card the card (also the card that is checking this condition)
     * @param count the number of cards
     * @param filters the filter
     */
    public InBattleWithCondition(PhysicalCard card, int count, Filterable filters) {
        this(card, count, false, filters);
    }

    /**
     * Creates a condition that is fulfilled when at least a specified number of cards (or an exact number of cards)
     * accepted by the specified filter are in battle with the specified card.
     * @param card the card (also the card that is checking this condition)
     * @param count the number of cards
     * @param exactly true if the number of cards must match the count exactly, otherwise false
     * @param filters the filter
     */
    public InBattleWithCondition(PhysicalCard card, int count, boolean exactly, Filterable filters) {
        _permCardId = card.getPermanentCardId();
        _count = count;
        _exactly = exactly;
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (!Filters.participatingInBattle.accepts(gameState, modifiersQuerying, card))
            return false;

        Filter filterToUse = Filters.and(Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters)));
        if (_exactly)
            return Filters.countActive(gameState.getGame(), card, Filters.and(filterToUse, Filters.inBattleWith(card))) == _count;
        else
            return Filters.canSpot(gameState.getGame(), card, _count, Filters.and(filterToUse, Filters.inBattleWith(card)));
    }
}
