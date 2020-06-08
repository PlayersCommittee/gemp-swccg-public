package com.gempukku.swccgo.cards.effects.usage;

import com.gempukku.swccgo.logic.actions.GameTextAction;

/**
 * An effect is used for a card action that can be performed once per turn for card title. The effect will be successful if the limit
 * has not yet been reached.
 */
public class OncePerTurnForCardTitleEffect extends NumTimesPerTurnEffect {

    /**
     * Creates an effect that checks if the card's "once per turn" for card title limit for an action has been reached.
     * @param action the action performing this effect
     */
    public OncePerTurnForCardTitleEffect(GameTextAction action) {
        super(action, 1);
    }
}