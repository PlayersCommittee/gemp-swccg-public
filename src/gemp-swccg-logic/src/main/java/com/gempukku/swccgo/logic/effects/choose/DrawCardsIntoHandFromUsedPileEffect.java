package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to draw the top cards into hand from Used Pile.
 */
public class DrawCardsIntoHandFromUsedPileEffect extends DrawCardsIntoHandFromPileEffect {

    /**
     * Creates an effect that causes the player to draw the top cards into hand from Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param amount the number of cards to draw into hand
     */
    public DrawCardsIntoHandFromUsedPileEffect(Action action, String playerId, int amount) {
        super(action, playerId, amount, Zone.USED_PILE, false, false);
    }

    /**
     * A callback method for the cards drawn into hand.
     * @param cards the cards drawn into hand
     */
    @Override
    protected void cardsDrawnIntoHand(Collection<PhysicalCard> cards) {
    }
}
