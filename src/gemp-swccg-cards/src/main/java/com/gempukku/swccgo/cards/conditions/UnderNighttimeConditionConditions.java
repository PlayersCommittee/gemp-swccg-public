package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is "under nighttime conditions".
 */
public class UnderNighttimeConditionConditions implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the specified card is "under nighttime conditions".
     * @param card the card
     */
    public UnderNighttimeConditionConditions(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.under_nighttime_conditions.accepts(gameState, modifiersQuerying, card);
    }
}
