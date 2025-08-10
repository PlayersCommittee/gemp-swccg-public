package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to lose a specified amount of Force (may not be reduced) and stack that
 * Force face up on a specified card.
 */
public class LoseForceAndStackFaceUpEffect extends LoseForceEffect {

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force (may not be reduced) and
     * stack that Force face up on a specified card.
     * @param action the action performing this effect
     * @param playerToLoseForce the player
     * @param amount the amount of Force to lose
     * @param stackFaceDownOn the card lost as Force is instead stacked face up on
     */
    public LoseForceAndStackFaceUpEffect(Action action, String playerToLoseForce, float amount, PhysicalCard stackFaceDownOn) {
        super(action, playerToLoseForce, amount, true, null, false, false, false, false, stackFaceDownOn, false, false);
    }
}
