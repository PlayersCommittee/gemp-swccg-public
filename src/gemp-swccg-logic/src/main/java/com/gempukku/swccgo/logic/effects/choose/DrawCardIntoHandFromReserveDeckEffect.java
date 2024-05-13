package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to draw the top card into hand from Reserve Deck.
 */
public class DrawCardIntoHandFromReserveDeckEffect extends DrawCardsIntoHandFromReserveDeckEffect {

    /**
     * Creates an effect that causes the player to draw the top card into hand from Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public DrawCardIntoHandFromReserveDeckEffect(Action action, String playerId) {
        super(action, playerId, 1);
    }

    /**
     * A callback method for the cards drawn into hand.
     * @param cardsDrawn the cards drawn into hand
     */
    @Override
    protected final void cardsDrawnIntoHand(Collection<PhysicalCard> cardsDrawn) {
        if (cardsDrawn.size() == 1) {
            cardDrawnIntoHand(cardsDrawn.iterator().next());
        }
    }

    /**
     * A callback method for the card drawn into hand.
     * @param cardDrawn the card drawn into hand
     */
    protected void cardDrawnIntoHand(PhysicalCard cardDrawn) {
    }
}
