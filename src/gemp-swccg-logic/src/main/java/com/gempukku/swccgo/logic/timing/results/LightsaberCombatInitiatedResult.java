package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during lightsaber combat when the lightsaber combat is initiated.
 */
public class LightsaberCombatInitiatedResult extends EffectResult {

    /**
     * Creates an effect result that is triggered during lightsaber combat when the lightsaber combat is initiated.
     * @param action the action performing this effect result
     */
    public LightsaberCombatInitiatedResult(Action action) {
        super(Type.LIGHTSABER_COMBAT_INITIATED, action.getPerformingPlayer());
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Lightsaber combat just initiated";
    }
}
