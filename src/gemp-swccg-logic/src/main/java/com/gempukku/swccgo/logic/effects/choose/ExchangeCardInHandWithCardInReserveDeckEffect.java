package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to exchange a card from hand with a card from Reserve Deck.
 */
public class ExchangeCardInHandWithCardInReserveDeckEffect extends ExchangeCardsInHandWithCardInCardPileEffect {

    /**
     * Creates an effect to exchange a card from hand with a card from Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param reshuffle true if the card pile is reshuffled after the exchange, otherwise false
     */
    public ExchangeCardInHandWithCardInReserveDeckEffect(Action action, String playerId, boolean reshuffle) {
        super(action, playerId, Zone.RESERVE_DECK, 1, 1, reshuffle);
    }

    /**
     * Creates an effect to exchange a card from hand with a card from Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param reshuffle true if the card pile is reshuffled after the exchange, otherwise false
     */
    public ExchangeCardInHandWithCardInReserveDeckEffect(Action action, String playerId, Filterable cardsInHandFilter, Filterable cardInPileFilter, boolean reshuffle) {
        super(action, playerId, Zone.RESERVE_DECK, 1, 1, cardsInHandFilter, cardInPileFilter, reshuffle);
    }

    /**
     * Creates an effect to exchange a card from hand with a card from Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param reshuffle true if the card pile is reshuffled after the exchange, otherwise false
     * @param hiddenFromHand true if the card exchanged from hand should be hidden from opponent
     */
    public ExchangeCardInHandWithCardInReserveDeckEffect(Action action, String playerId, Filterable cardsInHandFilter, Filterable cardInPileFilter, boolean reshuffle, boolean hiddenFromHand) {
        super(action, playerId, Zone.RESERVE_DECK, 1, 1, cardsInHandFilter, cardInPileFilter, reshuffle, hiddenFromHand);
    }

    /**
     * Creates an effect to exchange a card from hand with a card from Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param reshuffle true if the card pile is reshuffled after the exchange, otherwise false
     * @param hiddenFromHand true if the card exchanged from hand should be hidden from opponent
     * @param hiddenFromPile true if the card exchanged from pile should be hidden from opponent
     */
    public ExchangeCardInHandWithCardInReserveDeckEffect(Action action, String playerId, Filterable cardsInHandFilter, Filterable cardInPileFilter, boolean reshuffle, boolean hiddenFromHand, boolean hiddenFromPile) {
        super(action, playerId, Zone.RESERVE_DECK, 1, 1, cardsInHandFilter, cardInPileFilter, reshuffle, hiddenFromHand, hiddenFromPile);
    }
}
