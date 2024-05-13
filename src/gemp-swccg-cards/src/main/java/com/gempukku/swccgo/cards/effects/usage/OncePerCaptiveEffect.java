package com.gempukku.swccgo.cards.effects.usage;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.actions.GameTextAction;

/**
 * An effect is used for a card action that can be performed once per captivity. The effect will be successful if the limit
 * has not yet been reached.
 */
public class OncePerCaptiveEffect extends CheckCaptivityLimitEffect {

    /**
     * Creates an effect that checks if the card's "once per captive" limit for an action has been reached.
     * @param action the action performing this effect
     * @param captive the captive
     */
    public OncePerCaptiveEffect(GameTextAction action, PhysicalCard captive) {
        super(action, captive, 1);
    }
}