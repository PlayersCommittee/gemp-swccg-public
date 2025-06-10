package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when no card with the same title as the specified card has been played during the
 * current turn.
 */
public class CardTitleNotPlayedThisTurnCondition implements Condition {
    private int _permCardId;

    /**
     * Creates a condition that is fulfilled when no card with the same title as the specified card has been played during
     * the current turn.
     * @param card the card
     */
    public CardTitleNotPlayedThisTurnCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        for (String title : card.getTitles()) {
            if (modifiersQuerying.getCardTitlePlayedTurnLimitCounter(title).getUsedLimit() > 0) {
                return false;
            }
        }
        return true;
    }
}
