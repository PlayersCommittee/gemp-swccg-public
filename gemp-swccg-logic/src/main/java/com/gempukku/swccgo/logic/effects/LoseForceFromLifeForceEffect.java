package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to lose a specified amount of Force from Life Force.
 */
public class LoseForceFromLifeForceEffect extends LoseForceEffect {

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force from Life Force.
     * @param action the action performing this effect
     * @param playerToLoseForce the player to lose Force
     * @param amount the amount of Force to lose
     */
    public LoseForceFromLifeForceEffect(Action action, String playerToLoseForce, float amount) {
        this(action, playerToLoseForce, amount, false);
    }

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force from Life Force.
     * @param action the action performing this effect
     * @param playerToLoseForce the player to lose Force
     * @param amount the amount of Force to lose
     * @param cannotBeReduced true if Force loss cannot be reduced, otherwise false
     */
    public LoseForceFromLifeForceEffect(Action action, String playerToLoseForce, float amount, boolean cannotBeReduced) {
        super(action, playerToLoseForce, amount, cannotBeReduced, false, false, false, false, true, null, false);
    }
}
