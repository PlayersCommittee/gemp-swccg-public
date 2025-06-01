package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to lose a specified amount of Force from Force Pile.
 */
public class LoseForceFromForcePileEffect extends LoseForceEffect {

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force from Force Pile.
     * @param action the action performing this effect
     * @param playerToLoseForce the player to lose Force
     * @param amount the amount of Force to lose
     */
    public LoseForceFromForcePileEffect(Action action, String playerToLoseForce, float amount) {
        this(action, playerToLoseForce, amount, false);
    }

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force from Life Force.
     * @param action the action performing this effect
     * @param playerToLoseForce the player to lose Force
     * @param amount the amount of Force to lose
     * @param cannotBeReduced true if Force loss cannot be reduced, otherwise false
     */
    public LoseForceFromForcePileEffect(Action action, String playerToLoseForce, float amount, boolean cannotBeReduced) {
        super(action, playerToLoseForce, amount, cannotBeReduced, null, false, false, false, false, null, false, false, true, Float.MIN_VALUE);
    }
}
