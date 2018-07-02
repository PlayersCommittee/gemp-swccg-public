package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during an attack when the attack is initiated.
 */
public class AttackInitiatedResult extends EffectResult {

    /**
     * Creates an effect result that is triggered during an attack when the attack is initiated.
     * @param action the action performing this effect result
     */
    public AttackInitiatedResult(Action action) {
        super(Type.ATTACK_INITIATED, action.getPerformingPlayer());
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Attack just initiated at " + GameUtils.getCardLink(game.getGameState().getAttackLocation());
    }
}
