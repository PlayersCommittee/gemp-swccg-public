package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;


/**
 * An effect that causes the specified player to lose a specified amount of Force from Reserve Deck.
 */
public class LoseForceFromReserveDeckEffect extends LoseForceEffect {

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force from Reserve Deck.
     * @param action the action performing this effect
     * @param playerToLoseForce the player to lose Force
     * @param amount the amount of Force to lose
     */
    public LoseForceFromReserveDeckEffect(Action action, String playerToLoseForce, float amount) {
        this(action, playerToLoseForce, amount, false);
    }

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force from Reserve Deck.
     * @param action the action performing this effect
     * @param playerToLoseForce the player to lose Force
     * @param amount the amount of Force to lose
     * @param cannotBeReduced true if Force loss cannot be reduced, otherwise false
     */
    public LoseForceFromReserveDeckEffect(Action action, String playerToLoseForce, float amount, boolean cannotBeReduced) {
        super(action, playerToLoseForce, amount, cannotBeReduced, false, false, false, true, false, null, false);
    }

    @Override
    public String getText(SwccgGame game) {
        return "Lose " + GuiUtils.formatAsString(_initialAmount) + " Force from Reserve Deck";
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return super.isPlayableInFull(game) && game.getGameState().getReserveDeckSize(_playerToLoseForce) >= _initialAmount;
    }
}

