package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.JediTestStatus;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified Jedi Test is completed.
 */
public class JediTestCompletedCondition implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the specified Jedi Test is completed.
     * @param card the card
     */
    public JediTestCompletedCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return card.getJediTestStatus() == JediTestStatus.COMPLETED;
    }
}
