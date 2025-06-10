package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;


/**
 * A condition that is fulfilled when the given card is out of play
 */
public class OutOfPlayCondition implements Condition {
    private int _permSourceCardId;
    private Filter _filter;


    /**
     * Creates a condition that is fulfilled when the cards matching the filter are out of play
     * @param source the card that is checking this condition
     * @param filter the filter
     */
    public OutOfPlayCondition(PhysicalCard source,  Filterable filter) {
        _permSourceCardId = source.getPermanentCardId();
        _filter = Filters.and(filter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        return GameConditions.isOutOfPlay(gameState.getGame(), _filter);

    }
}
