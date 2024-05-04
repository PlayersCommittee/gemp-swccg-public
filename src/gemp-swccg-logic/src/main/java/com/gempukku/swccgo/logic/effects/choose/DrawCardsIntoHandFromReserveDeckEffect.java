package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to draw the top cards into hand from Reserve Deck.
 */
public class DrawCardsIntoHandFromReserveDeckEffect extends DrawCardsIntoHandFromPileEffect {

    /**
     * Creates an effect that causes the player to draw the top cards into hand from Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param amount the number of cards to take into hand
     */
    public DrawCardsIntoHandFromReserveDeckEffect(Action action, String playerId, int amount) {
        super(action, playerId, amount, Zone.RESERVE_DECK, false, false);
    }

    /**
     * A callback method for the cards drawn into hand.
     * @param cards the cards drawn into hand
     */
    @Override
    protected void cardsDrawnIntoHand(Collection<PhysicalCard> cards) {
    }
}
