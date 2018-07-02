package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put a random card from hand on Used Pile.
 */
public class PutRandomCardFromHandOnUsedPileEffect extends PutRandomCardsFromHandOnUsedPileEffect {

    /**
     * Creates an effect that causes the player to put a random card from hand on Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param handOwner the owner of the hand
     */
    public PutRandomCardFromHandOnUsedPileEffect(Action action, String playerId, String handOwner) {
        super(action, playerId, handOwner, 0, 1);
    }
}
