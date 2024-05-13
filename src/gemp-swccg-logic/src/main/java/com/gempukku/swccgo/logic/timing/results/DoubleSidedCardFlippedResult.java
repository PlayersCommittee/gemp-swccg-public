package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a double-sided card is flipped.
 */
public class DoubleSidedCardFlippedResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is triggered when a double-sided card is flipped.
     * @param performingPlayerId the player that flipped the double-sided card
     * @param card the card that was flipped
     */
    public DoubleSidedCardFlippedResult(String performingPlayerId, PhysicalCard card) {
        super(Type.DOUBLE_SIDED_CARD_FLIPPED, performingPlayerId);
        _card = card;
    }

    /**
     * Gets the double-sided card that was flipped.
     * @return the card
     */
    public PhysicalCard getCardFlipped() {
        return _card;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Flipped " + GameUtils.getCardLink(_card);
    }
}
