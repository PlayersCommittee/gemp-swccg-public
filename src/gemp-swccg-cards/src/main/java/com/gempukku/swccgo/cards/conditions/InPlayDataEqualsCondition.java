package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card's "in play data" is equal to the specified value.
 */
public class InPlayDataEqualsCondition implements Condition {
    private int _permCardId;
    private Boolean _booleanValue;
    private String _stringValue;

    /**
     * Creates a condition that is fulfilled when the specified card's "in play data" is equal to the specified value.
     * @param card the card
     * @param value the boolean value
     */
    public InPlayDataEqualsCondition(PhysicalCard card, boolean value) {
        _permCardId = card.getPermanentCardId();
        _booleanValue = value;
    }

    /**
     * Creates a condition that is fulfilled when the specified card's "in play data" is equal to the specified value.
     * @param card the card
     * @param value the String value
     */
    public InPlayDataEqualsCondition(PhysicalCard card, String value) {
        _permCardId = card.getPermanentCardId();
        _stringValue = value;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (_stringValue != null) {
            return card.getWhileInPlayData() != null && card.getWhileInPlayData().getTextValue().equals(_stringValue);
        }
        if (_booleanValue != null) {
            return card.getWhileInPlayData() != null && card.getWhileInPlayData().getBooleanValue() == _booleanValue;
        }

        return false;
    }
}
