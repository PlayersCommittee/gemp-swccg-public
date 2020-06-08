package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;

/**
 * An effect that causes the player to search Reserve Deck for a card and take it into hand (or take a specific card from
 * Reserve Deck into hand).
 */
public class TakeCardIntoHandFromReserveDeckEffect extends TakeCardsIntoHandFromReserveDeckEffect {

    /**
     * Creates an effect that causes the player to search Reserve Deck for a card and take it into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public TakeCardIntoHandFromReserveDeckEffect(Action action, String playerId, boolean reshuffle) {
        super(action, playerId, 1, 1, reshuffle);
    }

    /**
     * Creates an effect that causes the player to search Reserve Deck for a card accepted by the specified filter and
     * take it into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public TakeCardIntoHandFromReserveDeckEffect(Action action, String playerId, Filter filters, boolean reshuffle) {
        super(action, playerId, 1, 1, filters, reshuffle);
    }

    /**
     * Creates an effect that causes the player to take a specific card into hand from Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param card the card
     * @param hidden true if cards are not revealed, otherwise false
     */
    public TakeCardIntoHandFromReserveDeckEffect(Action action, String playerId, PhysicalCard card, boolean hidden) {
        super(action, playerId, Collections.singletonList(card), hidden);
    }

    /**
     * A callback method for the cards taken into hand.
     * @param cards the cards taken into hand
     */
    @Override
    protected final void cardsTakenIntoHand(Collection<PhysicalCard> cards) {
        if (cards.size() == 1) {
            cardTakenIntoHand(cards.iterator().next());
        }
    }

    /**
     * A callback method for the card taken into hand.
     * @param card the card taken into hand
     */
    protected void cardTakenIntoHand(PhysicalCard card) {
    }
}
