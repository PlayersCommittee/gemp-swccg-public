package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is in a battle initiated by the card's owner.
 */
public class InBattleInitiatedByOwnerCondition implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when the specified card is in a battle initiated by the card's owner.
     * @param card the card
     */
    public InBattleInitiatedByOwnerCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.participatingInBattleInitiatedByOwner.accepts(gameState, modifiersQuerying, card);
    }
}
