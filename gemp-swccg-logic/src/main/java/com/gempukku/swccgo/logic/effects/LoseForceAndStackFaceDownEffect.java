package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to lose a specified amount of Force (may not be reduced) and stack that
 * Force face down on a specified card.
 */
public class LoseForceAndStackFaceDownEffect extends LoseForceEffect {

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force (may not be reduced) and
     * stack that Force face down on a specified card.
     * @param action the action performing this effect
     * @param playerToLoseForce the player
     * @param amount the amount of Force to lose
     * @param stackFaceDownOn the card lost as Force is instead stacked face down on
     */
    public LoseForceAndStackFaceDownEffect(Action action, String playerToLoseForce, float amount, PhysicalCard stackFaceDownOn) {
        this(action, playerToLoseForce, amount, stackFaceDownOn, false);
    }

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force (may not be reduced) and
     * stack that Force face down on a specified card.
     * @param action the action performing this effect
     * @param playerToLoseForce the player
     * @param amount the amount of Force to lose
     * @param stackFaceDownOn the card lost as Force is instead stacked face down on
     * @param asLiberationCard the card lost as Force is stacked as a liberation card
     */
    public LoseForceAndStackFaceDownEffect(Action action, String playerToLoseForce, float amount, PhysicalCard stackFaceDownOn, boolean asLiberationCard) {
        super(action, playerToLoseForce, amount, true, false, false, false, false, false, stackFaceDownOn, asLiberationCard);
    }
}
