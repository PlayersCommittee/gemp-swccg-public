package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;

/**
 * A condition that is fulfilled when cards with same card titles that are accepted by the specified filter can be spotted.
 */
public class SameCardTitlesCondition implements Condition {
    private int _permSourceCardId;
    private Filter _filter;

    /**
     * Creates a condition that is fulfilled when cards with same card titles that are accepted by the specified filter
     * can be spotted.
     * @param source the card that is checking this condition
     * @param filter the filter
     */
    public SameCardTitlesCondition(PhysicalCard source, Filterable filter) {
        _permSourceCardId = source.getPermanentCardId();
        _filter = Filters.and(filter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, _filter);
        for (PhysicalCard card : cards) {
            if (!Filters.filterCount(cards, gameState.getGame(), 1, Filters.sameTitleAs(card)).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}

