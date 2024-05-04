package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during a duel for actions that involve adding or modifying duel destinies to be performed.
 */
public class DuelAddOrModifyDuelDestiniesStepResult extends EffectResult {

    /**
     * Creates an effect result that is triggered during a duel for actions that involve adding or modifying duel destinies
     * to be performed.
     * @param action the action performing this effect result
     */
    public DuelAddOrModifyDuelDestiniesStepResult(Action action) {
        super(Type.DUEL_ADD_MODIFY_DUEL_DESTINIES_STEP, action.getPerformingPlayer());
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
        return "Duel - add or modify duel destinies";
    }
}
