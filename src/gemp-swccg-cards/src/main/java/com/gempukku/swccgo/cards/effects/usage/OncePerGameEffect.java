package com.gempukku.swccgo.cards.effects.usage;

import com.gempukku.swccgo.logic.actions.GameTextAction;

/**
 * An effect is used for a card action that can be performed once per game. The effect will be successful if the limit
 * has not yet been reached.
 */
public class OncePerGameEffect extends NumTimesPerGameEffect {

    /**
     * Creates an effect that checks if the card's "once per game" limit for an action has been reached.
     * @param action the action performing this effect
     */
    public OncePerGameEffect(GameTextAction action) {
        super(action, 1);
    }
}