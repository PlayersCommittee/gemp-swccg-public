package com.gempukku.swccgo.cards.effects.usage;

import com.gempukku.swccgo.logic.actions.GameTextAction;

/**
 * An effect is used for a card action that can be performed once per Force loss. The effect will be successful if the limit
 * has not yet been reached.
 */
public class OncePerForceLossEffect extends CheckForceLossLimitEffect {

    /**
     * Creates an effect that checks if the card's "limit one per Force loss" limit for an action has been reached.
     * @param action the action performing this effect
     */
    public OncePerForceLossEffect(GameTextAction action) {
        super(action, 1);
    }
}