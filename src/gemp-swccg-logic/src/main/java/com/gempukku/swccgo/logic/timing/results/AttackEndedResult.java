package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when an attack has just ended.
 */
public class AttackEndedResult extends EffectResult {

    /**
     * Creates an effect result that is triggered when an attack has just ended.
     * @param action the action performing this effect result
     */
    public AttackEndedResult(Action action) {
        super(Type.ATTACK_ENDED, action.getPerformingPlayer());
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Attack just ended";
    }
}
