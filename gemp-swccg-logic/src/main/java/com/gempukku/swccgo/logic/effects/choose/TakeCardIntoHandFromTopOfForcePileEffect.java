package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player to take the top card of Force pile into hand.
 */
public class TakeCardIntoHandFromTopOfForcePileEffect extends DrawCardIntoHandFromForcePileEffect {

    /**
     * Creates an effect that causes the player to take the top card of Force pile into hand.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public TakeCardIntoHandFromTopOfForcePileEffect(Action action, String playerId) {
        super(action, playerId, true);
    }
}
