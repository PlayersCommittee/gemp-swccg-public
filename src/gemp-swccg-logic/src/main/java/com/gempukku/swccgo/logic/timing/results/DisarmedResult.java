package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when a card is Disarmed.
 */
public class DisarmedResult extends EffectResult {
    private PhysicalCard _cardDisarmed;

    /**
     * Creates an effect result that is emitted when a card is Disarmed.
     * @param performingPlayerId the player performing the action
     * @param cardDisarmed the card that was Disarmed
     */
    public DisarmedResult(String performingPlayerId, PhysicalCard cardDisarmed) {
        super(Type.DISARMED, performingPlayerId);
        _cardDisarmed = cardDisarmed;
    }

    /**
     * Gets the card that was Disarmed.
     * @return the card
     */
    public PhysicalCard getCardDisarmed() {
        return _cardDisarmed;
    }


    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Disarmed " + GameUtils.getCardLink(_cardDisarmed);
    }
}
