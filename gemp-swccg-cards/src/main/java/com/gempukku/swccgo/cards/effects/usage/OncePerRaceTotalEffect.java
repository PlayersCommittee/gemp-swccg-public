package com.gempukku.swccgo.cards.effects.usage;

import com.gempukku.swccgo.logic.actions.GameTextAction;

/**
 * An effect is used for a card action that can be performed once per race total. The effect will be successful if the limit
 * has not yet been reached.
 */
public class OncePerRaceTotalEffect extends CheckRaceTotalLimitEffect {

    /**
     * Creates an effect that checks if the card's "once per race total" limit for an action has been reached.
     * @param action the action performing this effect
     * @param raceTotal the race total
     */
    public OncePerRaceTotalEffect(GameTextAction action, float raceTotal) {
        super(action, raceTotal, 1);
    }
}