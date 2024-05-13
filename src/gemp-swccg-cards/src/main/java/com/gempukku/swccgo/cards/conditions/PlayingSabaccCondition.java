package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.SabaccState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.List;

/**
 * A condition that is fulfilled when the specified card is playing sabacc.
 */
public class PlayingSabaccCondition implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the specified card is playing sabacc.
     * @param card the card
     */
    public PlayingSabaccCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        SabaccState sabaccState = gameState.getSabaccState();
        if (sabaccState == null)
            return false;

        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        List<PhysicalCard> sabaccPlayers = sabaccState.getSabaccPlayers();
        return sabaccPlayers.contains(card);
    }
}
