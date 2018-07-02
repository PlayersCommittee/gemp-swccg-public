package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during lightsaber combat for actions that involve adding or modifying lightsaber combat
 * destinies to be performed.
 */
public class LightsaberCombatAddOrModifyLightsaberCombatDestiniesStepResult extends EffectResult {

    /**
     * Creates an effect result that is triggered during lightsaber combat for actions that involve adding or modifying
     * lightsaber combat destinies to be performed.
     * @param action the action performing this effect result
     */
    public LightsaberCombatAddOrModifyLightsaberCombatDestiniesStepResult(Action action) {
        super(Type.LIGHTSABER_COMBAT_ADD_MODIFY_LIGHTSABER_COMBAT_DESTINIES_STEP, action.getPerformingPlayer());
    }

    @Override
    public boolean isPerformingPlayerRespondsFirst() {
        return true;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Lightsaber combat - add or modify lightsaber combat destinies";
    }
}
