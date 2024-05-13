package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a character goes 'missing'.
 */
public class GoneMissingResult extends EffectResult {
    private PhysicalCard _character;
    private boolean _wasCaptive;

    /**
     * Creates an effect result that is emitted when a character goes 'missing'.
     * @param performingPlayerId the performing player
     * @param character the character that went 'missing'
     * @param wasCaptive true if the character was a captive that went 'missing', otherwise false
     */
    public GoneMissingResult(String performingPlayerId, PhysicalCard character, boolean wasCaptive) {
        super(Type.GONE_MISSING, performingPlayerId);
        _character = character;
        _wasCaptive = wasCaptive;
    }

    /**
     * Gets the character that went 'missing'.
     * @return the character
     */
    public PhysicalCard getMissingCharacter() {
        return _character;
    }

    /**
     * Determines if character was a captive that went 'missing'.
     * @return true or false
     */
    public boolean wasCaptive() {
        return _wasCaptive;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_character) + " went 'missing'";
    }
}
