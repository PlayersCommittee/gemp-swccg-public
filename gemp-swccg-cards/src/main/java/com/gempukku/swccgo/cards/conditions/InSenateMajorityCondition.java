package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is in a Senate majority.
 */
public class InSenateMajorityCondition implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the specified card is in a Senate majority.
     * @param card the card
     */
    public InSenateMajorityCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (!Filters.at(Filters.Galactic_Senate).accepts(gameState, modifiersQuerying, card))
            return false;

        String playerWithSenateMajority = modifiersQuerying.getPlayerWithSenateMajority(gameState);
        return card.getOwner().equals(playerWithSenateMajority);
    }
}
