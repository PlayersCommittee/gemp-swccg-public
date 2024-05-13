package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * Used for card pile actions, such as activating Force during activate phase and drawing a card from Force Pile during
 * draw phase.
 */
public class CardPileAction extends SystemQueueAction {
    private PhysicalCard _sourceCard;

    /**
     * Creates a card pile action that uses the specified card as the card to click on for the action.
     * @param playerId the performing player
     * @param sourceCard the card
     */
    public CardPileAction(String playerId, PhysicalCard sourceCard) {
        _sourceCard = sourceCard;
        setPerformingPlayer(playerId);
    }

    @Override
    public PhysicalCard getActionAttachedToCard() {
        return _sourceCard;
    }
}
