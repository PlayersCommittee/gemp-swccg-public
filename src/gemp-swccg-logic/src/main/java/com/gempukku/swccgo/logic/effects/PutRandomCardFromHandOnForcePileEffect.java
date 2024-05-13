package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put a random card from hand on Force Pile.
 */
public class PutRandomCardFromHandOnForcePileEffect extends PutRandomCardsFromHandOnForcePileEffect {

    /**
     * Creates an effect that causes the player to put a random card from hand on Force Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param handOwner the owner of the hand
     */
    public PutRandomCardFromHandOnForcePileEffect(Action action, String playerId, String handOwner) {
        super(action, playerId, handOwner, 0, 1);
    }
}
