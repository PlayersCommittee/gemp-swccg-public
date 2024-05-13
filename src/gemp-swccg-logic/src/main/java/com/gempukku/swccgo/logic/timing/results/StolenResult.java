package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is stolen.
 */
public class StolenResult extends EffectResult {
    private PhysicalCard _cardStolen;
    private PhysicalCard _stolenFromLocation;

    /**
     * Creates an effect result that is emitted when a card is stolen.
     * @param performingPlayerId the player that performed the action
     * @param cardStolen the card that is stolen
     * @param stolenFromLocation the location the card was stolen from, or null
     */
    public StolenResult(String performingPlayerId, PhysicalCard cardStolen, PhysicalCard stolenFromLocation) {
        super(Type.STOLEN, performingPlayerId);
        _cardStolen = cardStolen;
        _stolenFromLocation = stolenFromLocation;
    }

    /**
     * Gets the card that was stolen.
     * @return the card
     */
    public PhysicalCard getStolenCard() {
        return _cardStolen;
    }

    /**
     * Gets the location that the card was stolen from.
     * @return the location, or null
     */
    public PhysicalCard getStolenFromLocation() {
        return _stolenFromLocation;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_cardStolen) + " just stolen";
    }
}
