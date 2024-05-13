package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put a random card from hand on Reserve Deck.
 */
public class PutRandomCardFromHandOnReserveDeckEffect extends PutRandomCardsFromHandOnReserveDeckEffect {

    /**
     * Creates an effect that causes the player to put a random card from hand on Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param handOwner the owner of the hand
     */
    public PutRandomCardFromHandOnReserveDeckEffect(Action action, String playerId, String handOwner) {
        super(action, playerId, handOwner, 0, 1);
    }
}
