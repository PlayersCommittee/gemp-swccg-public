package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card in play with the specified play option.
 */
public class PlayCardOptionIdCondition implements Condition {
    private int _permCardId;
    private PlayCardOptionId _optionId;

    /**
     * Creates a condition that is fulfilled when the specified card in play with the specified play option.
     * @param card the card
     * @param optionId the option id
     */
    public PlayCardOptionIdCondition(PhysicalCard card, PlayCardOptionId optionId) {
        _permCardId = card.getPermanentCardId();
        _optionId = optionId;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return card.getZone().isInPlay() && card.getPlayCardOptionId() == _optionId;
    }
}
