package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect for peeking at the top cards of a Reserve Deck.
 */
public class PeekAtTopCardsOfReserveDeckEffect extends PeekAtTopCardsOfCardPileEffect {

    /**
     * Creates an effect for peeking at the top cards of Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player to peek at cards
     * @param count the number of cards to peek at
     */
    public PeekAtTopCardsOfReserveDeckEffect(Action action, String playerId, int count) {
        super(action, playerId, playerId, Zone.RESERVE_DECK, count);
    }

    /**
     * Creates an effect for peeking at the top cards of a specified player's Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player to peek at cards
     * @param cardPileOwner the owner of the card pile
     * @param count the number of cards to peek at
     */
    public PeekAtTopCardsOfReserveDeckEffect(Action action, String playerId, String cardPileOwner, int count) {
        super(action, playerId, cardPileOwner, Zone.RESERVE_DECK, count);
    }
}
