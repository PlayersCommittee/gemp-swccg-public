package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes the player to search opponent's Lost Pile for cards and steal them into hand.
 */
public class StealCardsIntoHandFromLostPileEffect extends StealCardsIntoHandFromPileEffect {

    /**
     * Creates an effect that causes the player to search opponent's Lost Pile for cards accepted by the specified filter
     * and steal them into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public StealCardsIntoHandFromLostPileEffect(Action action, String playerId, int minimum, int maximum, Filterable filters) {
        super(action, playerId, minimum, maximum, Zone.LOST_PILE, false, filters, false);
    }

    /**
     * A callback method for the cards stolen into hand.
     * @param cards the cards stolen into hand
     */
    @Override
    protected void cardsStolenIntoHand(Collection<PhysicalCard> cards) {
    }
}
