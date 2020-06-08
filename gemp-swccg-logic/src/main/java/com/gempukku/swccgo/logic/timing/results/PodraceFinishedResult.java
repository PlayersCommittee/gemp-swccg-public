package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a Podrace is finished.
 */
public class PodraceFinishedResult extends EffectResult {

    /**
     * Creates an effect result that is triggered when a Podrace is finished.
     * @param action the action performing this effect result
     */
    public PodraceFinishedResult(Action action) {
        super(Type.PODRACE_FINISHED, action.getPerformingPlayer());
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Podrace just finished";
    }
}
