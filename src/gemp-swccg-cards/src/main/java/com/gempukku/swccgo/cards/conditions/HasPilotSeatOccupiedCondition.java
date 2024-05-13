package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card has its pilot capacity slot occupied.
 */
public class HasPilotSeatOccupiedCondition implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the specified card has its pilot capacity slot occupied.
     * @param card the card (also the card that is checking this condition)
     */
    public HasPilotSeatOccupiedCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return !gameState.getPilotCardsAboard(modifiersQuerying, card, false).isEmpty();
    }
}
