package com.gempukku.swccgo.cards.effects.usage;

import com.gempukku.swccgo.logic.actions.GameTextAction;

/**
 * An effect is used for a card action that can be performed X times per battle. The effect will be successful if the
 * limit has not yet been reached.
 */
public class NumTimesPerBattleEffect extends CheckBattleLimitEffect {

    /**
     * Creates an effect that checks if the card's "X times per battle" limit for an action has been reached.
     * @param action the action performing this effect
     * @param count the value for X
     */
    public NumTimesPerBattleEffect(GameTextAction action, int count) {
        super(action, count);
    }
}