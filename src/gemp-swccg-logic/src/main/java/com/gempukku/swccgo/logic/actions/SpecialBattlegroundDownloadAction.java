package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * Used for a special card pile action for downloading a battleground.
 */
public class SpecialBattlegroundDownloadAction extends CardPileAction {

    /**
     * Creates a special card pile action for downloading a battleground.
     * @param playerId the performing player
     * @param sourceCard the card
     */
    public SpecialBattlegroundDownloadAction(String playerId, PhysicalCard sourceCard) {
        super(playerId, sourceCard);
    }

    @Override
    public String getText() {
        return "Deploy unique battleground";
    }
}
