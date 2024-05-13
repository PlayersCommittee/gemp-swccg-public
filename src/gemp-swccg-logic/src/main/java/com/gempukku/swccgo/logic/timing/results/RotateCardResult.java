package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The result triggered after a card is rotated.
 */
public class RotateCardResult extends EffectResult {
    private PhysicalCard _rotatedCard;

    /**
     * Creates an effect result that is emitted when a card is rotated.
     * @param performingPlayerId the performing player
     * @param rotatedCard the card that was rotated
     */
    public RotateCardResult(String performingPlayerId, PhysicalCard rotatedCard) {
        super(Type.ROTATE_CARD, performingPlayerId);
        _rotatedCard = rotatedCard;
    }

    /**
     * Gets the card that was rotated.
     * @return the card
     */
    public PhysicalCard getRotatedCard() {
        return _rotatedCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_rotatedCard) + " just rotated";
    }
}
