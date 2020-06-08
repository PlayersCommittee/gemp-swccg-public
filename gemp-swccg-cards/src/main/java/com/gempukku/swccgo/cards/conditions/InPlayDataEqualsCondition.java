package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card's "in play data" is equal to the specified value.
 */
public class InPlayDataEqualsCondition implements Condition {
    private int _permCardId;
    private boolean _booleanValue;

    /**
     * Creates a condition that is fulfilled when the specified card's "in play data" is equal to the specified value.
     * @param card the card
     * @param value the boolean value
     */
    public InPlayDataEqualsCondition(PhysicalCard card, boolean value) {
        _permCardId = card.getPermanentCardId();
        _booleanValue = value;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return card.getWhileInPlayData() != null && card.getWhileInPlayData().getBooleanValue() == _booleanValue;
    }
}
