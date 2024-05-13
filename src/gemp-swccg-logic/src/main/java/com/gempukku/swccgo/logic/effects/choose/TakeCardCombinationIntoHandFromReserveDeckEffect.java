package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to take a combination of cards into hand from Reserve Deck.
 */
public abstract class TakeCardCombinationIntoHandFromReserveDeckEffect extends TakeCardCombinationIntoHandFromPileEffect {

    /**
     * Creates an effect that causes the player to search Reserve Deck and take a combination of cards into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected TakeCardCombinationIntoHandFromReserveDeckEffect(Action action, String playerId, boolean reshuffle) {
        super(action, playerId, Zone.RESERVE_DECK, playerId, reshuffle);
    }
}
