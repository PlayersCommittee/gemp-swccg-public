package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a character is 'frozen'.
 */
public class FrozenResult extends EffectResult {
    private PhysicalCard _captive;

    /**
     * Creates an effect result that is emitted when a character is 'frozen'.
     * @param performingPlayerId the performing player
     * @param captive the frozen captive
     */
    public FrozenResult(String performingPlayerId, PhysicalCard captive) {
        super(Type.FROZEN, performingPlayerId);
        _captive = captive;
    }

    /**
     * Gets the frozen captive.
     * @return the frozen captive
     */
    public PhysicalCard getCaptive() {
        return _captive;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "'Froze' " + GameUtils.getCardLink(_captive);
    }
}
