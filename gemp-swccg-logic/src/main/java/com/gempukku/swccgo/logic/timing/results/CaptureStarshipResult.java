package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.CaptureOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a starship is captured.
 */
public class CaptureStarshipResult extends EffectResult {
    private PhysicalCard _sourceCard;
    private PhysicalCard _capturedCard;
    private CaptureOption _option;
    private PhysicalCard _attachedTo;

    /**
     * Creates an effect result that is emitted when a starship is captured.
     * @param performingPlayerId the performing player
     * @param sourceCard the card that is the source of the capturing
     * @param capturedCard the captured character
     * @param option the capturing option
     */
    public CaptureStarshipResult(String performingPlayerId, PhysicalCard sourceCard, PhysicalCard capturedCard, CaptureOption option) {
        super(Type.CAPTURED_STARSHIP, performingPlayerId);
        _sourceCard = sourceCard;
        _capturedCard = capturedCard;
        _option = option;
        _attachedTo = _capturedCard.getAttachedTo();
    }

    /**
     * Gets the source card of the capturing.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _sourceCard;
    }

    /**
     * Gets the captured starship.
     * @return the captured starship
     */
    public PhysicalCard getCapturedCard() {
        return _capturedCard;
    }

    /**
     * Gets the capturing option chosen.
     * @return the capturing option
     */
    public CaptureOption getOption() {
        return _option;
    }

    /**
     * Gets the card that the starship is now attached to.
     * @return the escort, or null
     */
    public PhysicalCard getAttachedTo() {
        return _attachedTo;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_capturedCard) + " captured";
    }
}
