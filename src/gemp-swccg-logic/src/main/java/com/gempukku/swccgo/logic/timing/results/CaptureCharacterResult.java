package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.CaptureOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a character is captured.
 */
public class CaptureCharacterResult extends EffectResult {
    private PhysicalCard _sourceCard;
    private PhysicalCard _cardFiringWeapon;
    private PhysicalCard _capturedCard;
    private boolean _wasUndercover;
    private boolean _wasMissing;
    private CaptureOption _option;
    private PhysicalCard _prison;
    private PhysicalCard _seizedBy;

    /**
     * Creates an effect result that is emitted when a character is captured.
     * @param performingPlayerId the performing player
     * @param sourceCard the card that is the source of the capturing
     * @param cardFiringWeapon the card firing the weapon that caused the capturing, or null
     * @param capturedCard the captured character
     * @param wasUndercover true if the character was 'undercover' when captured
     * @param wasMissing true if the character was 'missing' when captured
     * @param option the capturing option
     */
    public CaptureCharacterResult(String performingPlayerId, PhysicalCard sourceCard, PhysicalCard cardFiringWeapon, PhysicalCard capturedCard, boolean wasUndercover, boolean wasMissing, CaptureOption option) {
        super(Type.CAPTURED, performingPlayerId);
        _sourceCard = sourceCard;
        _cardFiringWeapon = cardFiringWeapon;
        _capturedCard = capturedCard;
        _wasUndercover = wasUndercover;
        _wasMissing = wasMissing;
        _option = option;
        if (_option == CaptureOption.IMPRISONMENT) {
            _prison = _capturedCard.getAttachedTo();
        }
        if (_option == CaptureOption.SEIZE) {
            _seizedBy = _capturedCard.getAttachedTo();
        }
    }

    /**
     * Gets the source card of the capturing.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _sourceCard;
    }

    /**
     * Gets the card that firing weapon that caused the capturing.
     * @return the card that fired weapon, or null
     */
    public PhysicalCard getCardFiringWeapon() {
        return _cardFiringWeapon;
    }

    /**
     * Gets the captured character.
     * @return the captured character
     */
    public PhysicalCard getCapturedCard() {
        return _capturedCard;
    }

    /**
     * Determines if the captured character was 'undercover' when captured.
     * @return true or false
     */
    public boolean cardWasUndercover() {
        return _wasUndercover;
    }

    /**
     * Determines if the captured character was 'missing' when captured.
     * @return true or false
     */
    public boolean cardWasMissing() {
        return _wasMissing;
    }

    /**
     * Gets the capturing option chosen.
     * @return the capturing option
     */
    public CaptureOption getOption() {
        return _option;
    }

    /**
     * Gets the prison the character is imprisoned in when captured.
     * @return the prison, or null
     */
    public PhysicalCard getPrison() {
        return _prison;
    }

    /**
     * Gets the escort that seized the character when captured.
     * @return the escort, or null
     */
    public PhysicalCard getSeizedBy() {
        return _seizedBy;
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
