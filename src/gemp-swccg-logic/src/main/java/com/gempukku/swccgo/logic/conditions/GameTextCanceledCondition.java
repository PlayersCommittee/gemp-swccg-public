package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the game text of the specified card is canceled.
 */
public class GameTextCanceledCondition implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the game text of the specified card is canceled.
     * @param card the card
     */
    public GameTextCanceledCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.isGameTextCanceled.accepts(gameState, modifiersQuerying, card);
    }
}
