package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to draw the top card into hand from Force Pile.
 */
public class DrawCardIntoHandFromForcePileEffect extends DrawCardsIntoHandFromForcePileEffect {

    /**
     * Creates an effect that causes the player to draw the top card into hand from Force Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public DrawCardIntoHandFromForcePileEffect(Action action, String playerId) {
        this(action, playerId, false);
    }

    /**
     * Creates an effect that causes the player to draw the top card into hand from Force Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param isTakeIntoHand true if take into hand, false if draw into hand
     */
    protected DrawCardIntoHandFromForcePileEffect(Action action, String playerId, boolean isTakeIntoHand) {
        super(action, playerId, 1, isTakeIntoHand);
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
