package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during a sabacc game when the sabacc totals are being calculated.
 */
public class SabaccTotalCalculatedResult extends EffectResult {

    /**
     * Creates an effect result that is triggered during a sabacc game when the sabacc totals are being calculated.
     * @param action the action performing this effect result
     */
    public SabaccTotalCalculatedResult(Action action) {
        super(Type.SABACC_TOTAL_CALCULATED, action.getPerformingPlayer());
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Calculating sabacc total";
    }
}
