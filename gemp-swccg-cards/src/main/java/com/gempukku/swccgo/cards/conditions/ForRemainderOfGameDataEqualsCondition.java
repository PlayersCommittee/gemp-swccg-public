package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card's "for remainder of game data" for the specified card id is
 * equal to a specified value.
 */
public class ForRemainderOfGameDataEqualsCondition implements Condition {
    private int _permCardId;
    private int _cardId;
    private boolean _value;

    /**
     * Creates a condition that is fulfilled when the specified card's "in play data" is not null.
     * @param card the card
     * @param cardId the card id
     * @param value the value
     */
    public ForRemainderOfGameDataEqualsCondition(PhysicalCard card, int cardId, boolean value) {
        _permCardId = card.getPermanentCardId();
        _cardId = cardId;
        _value = value;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return card.getForRemainderOfGameData().get(_cardId) != null
                && card.getForRemainderOfGameData().get(_cardId).getBooleanValue() == _value;
    }
}
