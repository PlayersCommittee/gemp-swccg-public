package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * Used for a special card pile action for uploading a card.
 */
public class SpecialPlaytestingUploadAction extends CardPileAction {

    /**
     * Creates a special card pile action for uploading a card.
     * @param playerId the performing player
     * @param sourceCard the card
     */
    public SpecialPlaytestingUploadAction(String playerId, PhysicalCard sourceCard) {
        super(playerId, sourceCard);
    }

    @Override
    public String getText() {
        return "Take a card into hand";
    }
}
