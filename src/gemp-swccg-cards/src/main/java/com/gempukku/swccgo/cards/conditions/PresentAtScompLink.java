package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is "at", "present at" or "present with" a Scomp link.
 */
public class PresentAtScompLink implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the input card is "at", "present at" or "present with" a Scomp link.
     * @param card the card
     */
    public PresentAtScompLink(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.at_Scomp_Link.accepts(gameState, modifiersQuerying, card);
    }
}
