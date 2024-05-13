package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.ReleaseOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a captive is released.
 */
public class ReleaseCaptiveResult extends EffectResult {
    private PhysicalCard _captiveReleased;
    private ReleaseOption _option;

    /**
     * Creates an effect result that is emitted when a captive is released.
     * @param performingPlayerId the performing player
     * @param captiveReleased the captive released
     * @param option the releasing option
     */
    public ReleaseCaptiveResult(String performingPlayerId, PhysicalCard captiveReleased, ReleaseOption option) {
        super(Type.RELEASED, performingPlayerId);
        _captiveReleased = captiveReleased;
        _option = option;
    }

    /**
     * Gets the released captive.
     * @return the released captive
     */
    public PhysicalCard getCaptiveReleased() {
        return _captiveReleased;
    }

    /**
     * Gets the releasing option chosen.
     * @return the releasing option
     */
    public ReleaseOption getOption() {
        return _option;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_captiveReleased) + " released";
    }
}
