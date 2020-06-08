package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted at the start of a turn.
 */
public class StartOfTurnResult extends EffectResult {

    /**
     * Creates an effect result that is emitted at the start of a turn.
     */
    public StartOfTurnResult() {
        super(Type.START_OF_TURN, null);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Start of turn";
    }
}
