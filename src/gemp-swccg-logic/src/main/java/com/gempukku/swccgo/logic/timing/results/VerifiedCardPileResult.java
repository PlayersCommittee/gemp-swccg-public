package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a card pile is verified.
 */
public class VerifiedCardPileResult extends EffectResult {
    private String _zoneOwner;
    private Zone _cardPile;

    /**
     * Creates an effect result that is triggered when a card pile is verified.
     * @param playerId the player verifying the card pile
     * @param zoneOwner the card pile owner
     * @param cardPile the card pile
     */
    public VerifiedCardPileResult(String playerId, String zoneOwner, Zone cardPile) {
        super(Type.VERIFY_CARD_PILE, playerId);
        _zoneOwner = zoneOwner;
        _cardPile = cardPile;
    }

    /**
     * Gets the owner of the card pile.
     * @return the owner
     */
    public String getZoneOwner() {
        return _zoneOwner;
    }

    /**
     * Gets the card pile.
     * @return the card pile
     */
    public Zone getCardPile() {
        return _cardPile;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just verified " + _zoneOwner + "'s " + _cardPile.getHumanReadable();
    }
}
