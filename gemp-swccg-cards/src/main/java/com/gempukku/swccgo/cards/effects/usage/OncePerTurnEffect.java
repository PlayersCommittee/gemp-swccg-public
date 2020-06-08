package com.gempukku.swccgo.cards.effects.usage;

import com.gempukku.swccgo.logic.actions.GameTextAction;

/**
 * An effect is used for a card action that can be performed once per turn. The effect will be successful if the limit
 * has not yet been reached.
 */
public class OncePerTurnEffect extends NumTimesPerTurnEffect {

    /**
     * Creates an effect that checks if the card's "once per turn" limit for an action has been reached.
     * @param action the action performing this effect
     */
    public OncePerTurnEffect(GameTextAction action) {
        super(action, 1);
    }
}