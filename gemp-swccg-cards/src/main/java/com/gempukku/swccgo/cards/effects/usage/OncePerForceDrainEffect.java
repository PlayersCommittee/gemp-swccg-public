package com.gempukku.swccgo.cards.effects.usage;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;

/**
 * An effect is used for a card action that can be performed once per Force drain. The effect will be successful if the limit
 * has not yet been reached.
 */
public class OncePerForceDrainEffect extends CheckForceDrainLimitEffect {

    /**
     * Creates an effect that checks if the card's "limit one per Force drain" limit for an action has been reached.
     * @param action the action performing this effect
     */
    public OncePerForceDrainEffect(GameTextAction action) {
        super(action, 1);
    }

    /**
     * Creates an effect that checks if the card's "limit one per Force drain" limit for an action has been reached.
     * @param action the action performing this effect
     * @param gameTextActionId the game text action id
     */
    public OncePerForceDrainEffect(PlayCardAction action, GameTextActionId gameTextActionId) {
        super(action, gameTextActionId, 1);
    }
}