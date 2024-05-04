package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is attached to a card that is 'concealed' and only inactive because it is a 'concealed'.
 */
public class AttachedToConcealedOnlyCondition implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the specified card is an imprisoned captive.
     * @param card the card
     */
    public AttachedToConcealedOnlyCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);
        PhysicalCard attachedTo = card.getAttachedTo();

        return attachedTo != null && attachedTo.isConcealed()
                && gameState.isCardInPlayActive(attachedTo, false, false, false, true, false, false, false, false);
    }
}
