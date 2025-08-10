package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the card is "trained by" specific cards.
 */
public class TrainedByCondition implements Condition {
    private int _permCardId;
    private Filter _trainedByFilter;

    /**
     * Creates a condition that is fulfilled when the card is "trained by" a card accepted by trainedByFilter.
     * @param card the card
     * @param trainedByFilter the trained by filter
     */
    public TrainedByCondition(PhysicalCard card, Filter trainedByFilter) {
        _permCardId = card.getPermanentCardId();
        _trainedByFilter = trainedByFilter;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.trainedBy(_trainedByFilter).accepts(gameState, modifiersQuerying, card);
    }
}
