package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that allows the player to peek at and reorder the top cards in the specified Reserve Deck.
 */
public class PeekAtAndReorderTopCardsOfReserveDeckEffect extends PeekAtAndReorderTopCardsOfCardPileEffect {

    /**
     * Create an effect that allows the performing player to peek at and reorder the the top cards in the specified player's Reserve Deck.
     * @param action the action performing this effect
     * @param cardPileOwner the card pile owner
     * @param numCards the number of cards to peek at and reorder
     */
    public PeekAtAndReorderTopCardsOfReserveDeckEffect(Action action, String cardPileOwner, int numCards) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardPileOwner, numCards);
    }
}
