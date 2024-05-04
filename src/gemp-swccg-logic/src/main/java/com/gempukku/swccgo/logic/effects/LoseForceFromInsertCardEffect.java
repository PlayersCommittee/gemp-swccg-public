package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to lose a specified amount of Force due to an 'insert' card.
 */
public class LoseForceFromInsertCardEffect extends LoseForceEffect {

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force due to an 'insert' card.
     * @param action the action performing this effect
     * @param playerToLoseForce the player to lose Force
     * @param amount the amount of Force to lose
     */
    public LoseForceFromInsertCardEffect(Action action, String playerToLoseForce, float amount) {
        super(action, playerToLoseForce, amount, false, false, true, false, false, false, null,false, false);
    }
}
