package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is made into an undercover spy.
 */
public class PutUndercoverResult extends EffectResult {
    private PhysicalCard _undercoverSpy;

    /**
     * Creates an effect result that is emitted when a card is made into an undercover spy.
     * @param performingPlayerId the performing player
     * @param undercoverSpy the undercover spy
     */
    public PutUndercoverResult(String performingPlayerId, PhysicalCard undercoverSpy) {
        super(Type.PUT_UNDERCOVER, performingPlayerId);
        _undercoverSpy = undercoverSpy;
    }

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
        return "Put " + GameUtils.getCardLink(_undercoverSpy) + " undercover";
    }
}
