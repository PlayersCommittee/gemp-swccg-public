package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when an undercover spy's cover is broken.
 */
public class CoverBrokenResult extends EffectResult {
    private PhysicalCard _undercoverSpy;

    /**
     * Creates an effect result that is emitted when an undercover spy's cover is broken.
     * @param performingPlayerId the performing player
     * @param undercoverSpy the undercover spy
     */
    public CoverBrokenResult(String performingPlayerId, PhysicalCard undercoverSpy) {
        super(Type.COVER_BROKEN, performingPlayerId);
        _undercoverSpy = undercoverSpy;
    }

    /**
     * Gets the undercover spy.
     * @return the undercover spy
     */
    public PhysicalCard getUndercoverSpy() {
        return _undercoverSpy;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Broke " + GameUtils.getCardLink(_undercoverSpy) + "'s cover";
    }
}
