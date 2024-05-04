package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is in an epic duel.
 */
public class InEpicDuelCondition implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the specified card is in an epic duel.
     * @param card the card
     */
    public InEpicDuelCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return gameState.isDuringDuel() && gameState.getDuelState().isEpicDuel() && gameState.isParticipatingInDuel(card);
    }
}
