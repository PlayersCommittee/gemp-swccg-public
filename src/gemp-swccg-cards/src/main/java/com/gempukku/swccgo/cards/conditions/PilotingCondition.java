package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is piloting a card accepted by the specified filter.
 */
public class PilotingCondition implements Condition {
    private int _permCardId;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when the specified card is piloting anything.
     * @param card the card
     */
    public PilotingCondition(PhysicalCard card) {
        this(card, Filters.any);
    }

    /**
     * Creates a condition that is fulfilled when the specified card is piloting a card accepted by the specified filter.
     * @param card the card
     * @param filters the filter
     */
    public PilotingCondition(PhysicalCard card, Filterable filters) {
        _permCardId = card.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.piloting(_filters).accepts(gameState, modifiersQuerying, card);
    }
}
