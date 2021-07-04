package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when cards are removed from being stacked on a card.
 */
public class RemovedFromStackedResult extends EffectResult {

    /**
     * Creates an effect result that is triggered when a cards are removed from being stacked on a card.
     * @param action the action performing this effect result
     */
    public RemovedFromStackedResult(Action action) {
        super(Type.REMOVED_FROM_STACKED, action.getPerformingPlayer());
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Removed a stacked card";
    }
}
