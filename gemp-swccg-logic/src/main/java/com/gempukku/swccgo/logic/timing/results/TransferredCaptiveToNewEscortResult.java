package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when an escorted captive is transferred to a new escort.
 */
public class TransferredCaptiveToNewEscortResult extends EffectResult {
    private PhysicalCard _captive;
    private PhysicalCard _oldEscort;
    private PhysicalCard _newEscort;

    /**
     * Creates an effect result that is emitted when an escorted captive is transferred to a new escort.
     * @param captive the captive
     * @param oldEscort the old escort
     * @param newEscort the new escort
     */
    public TransferredCaptiveToNewEscortResult(PhysicalCard captive, PhysicalCard oldEscort, PhysicalCard newEscort) {
        super(Type.TRANSFERRED_CAPTIVE_TO_NEW_ESCORT, newEscort.getOwner());
        _captive = captive;
        _oldEscort = oldEscort;
        _newEscort = newEscort;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Transferred " + GameUtils.getCardLink(_captive) + " from " + GameUtils.getCardLink(_oldEscort) + " to " + GameUtils.getCardLink(_newEscort);
    }
}
