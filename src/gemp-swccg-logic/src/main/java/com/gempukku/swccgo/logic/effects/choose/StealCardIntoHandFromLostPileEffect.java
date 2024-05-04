package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes the player to search opponent's Lost Pile for a card and steal it into hand.
 */
public class StealCardIntoHandFromLostPileEffect extends StealCardsIntoHandFromLostPileEffect {

    /**
     * Creates an effect that causes the player to search opponent's Lost Pile for a card accepted by the specified filter
     * and steal it into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     */
    public StealCardIntoHandFromLostPileEffect(Action action, String playerId, Filterable filters) {
        super(action, playerId, 1, 1, filters);
    }

    /**
     * A callback method for the cards stolen into hand.
     * @param cards the cards stolen into hand
     */
    @Override
    protected final void cardsStolenIntoHand(Collection<PhysicalCard> cards) {
        if (cards.size() == 1) {
            cardStolenIntoHand(cards.iterator().next());
        }
    }

    /**
     * A callback method for the card stolen into hand.
     * @param card the card stolen into hand
     */
    protected void cardStolenIntoHand(PhysicalCard card) {
    }
}
