package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that reduces Force loss to a specified amount.
 */
public class ReduceForceLossToAmountEffect extends ReduceForceLossEffect {

    /**
     * Creates an effect that reduces Force loss to a specified amount.
     * @param action the action performing this effect
     * @param playerId the player whose Force loss is reduced
     * @param amount the amount the Force loss is reduced by
     */
    public ReduceForceLossToAmountEffect(Action action, String playerId, int amount) {
        this(action, playerId, amount, 0);
    }

    /**
     * Creates an effect that reduces Force loss to a specified amount.
     * @param action the action performing this effect
     * @param playerId the player whose Force loss is reduced
     * @param amount the amount the Force loss is reduced to
     * @param toMinimum the minimum the Force loss can be reduced to
     */
    public ReduceForceLossToAmountEffect(Action action, String playerId, int amount, int toMinimum) {
        super(action, playerId, amount, toMinimum, true);
    }
}
