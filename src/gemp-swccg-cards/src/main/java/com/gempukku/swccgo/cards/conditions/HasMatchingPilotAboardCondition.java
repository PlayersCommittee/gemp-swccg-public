package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card has a matching pilot aboard.
 */
public class HasMatchingPilotAboardCondition implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the specified card has a matching pilot aboard.
     * @param card the card (also the card that is checking this condition)
     */
    public HasMatchingPilotAboardCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.hasMatchingPilotAboard(card).accepts(gameState, modifiersQuerying, card);
    }
}
