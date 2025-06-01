package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;


/**
 * An effect that causes the specified player to lose a specified amount of Force from hand.
 */
public class LoseForceFromHandEffect extends LoseForceEffect {

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force from hand.
     * @param action the action performing this effect
     * @param playerToLoseForce the player to lose Force
     * @param amount the amount of Force to lose
     */
    public LoseForceFromHandEffect(Action action, String playerToLoseForce, float amount) {
        this(action, playerToLoseForce, amount, false);
    }

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force from hand.
     * @param action the action performing this effect
     * @param playerToLoseForce the player to lose Force
     * @param amount the amount of Force to lose
     * @param cannotBeReduced true if Force loss cannot be reduced, otherwise false
     */
    public LoseForceFromHandEffect(Action action, String playerToLoseForce, float amount, boolean cannotBeReduced) {
        super(action, playerToLoseForce, amount, cannotBeReduced, null, false, true, false, false, null, false, false);
    }

    @Override
    public String getText(SwccgGame game) {
        return "Lose " + GuiUtils.formatAsString(_initialAmount) + " Force from hand";
    }
}
