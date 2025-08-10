package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card's "in play data" is not null.
 */
public class InPlayDataSetCondition implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the specified card's "in play data" is not null.
     * @param card the card
     */
    public InPlayDataSetCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return card.getWhileInPlayData() != null;
    }
}
