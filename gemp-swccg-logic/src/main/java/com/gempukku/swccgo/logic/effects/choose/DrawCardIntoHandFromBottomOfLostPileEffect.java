package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to draw the bottom card into hand from Lost Pile.
 */
public class DrawCardIntoHandFromBottomOfLostPileEffect extends DrawCardsIntoHandFromBottomOfLostPileEffect {

    /**
     * Creates an effect that causes the player to draw the bottom card into hand from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public DrawCardIntoHandFromBottomOfLostPileEffect(Action action, String playerId) {
        super(action, playerId, 1);
    }

    /**
     * A callback method for the cards drawn into hand.
     * @param cards the cards drawn into hand
     */
    @Override
    protected final void cardsDrawnIntoHand(Collection<PhysicalCard> cards) {
        if (cards.size() == 1) {
            cardDrawnIntoHand(cards.iterator().next());
        }
    }

    /**
     * A callback method for the card drawn into hand.
     * @param card the card drawn into hand
     */
    protected void cardDrawnIntoHand(PhysicalCard card) {
    }
}
