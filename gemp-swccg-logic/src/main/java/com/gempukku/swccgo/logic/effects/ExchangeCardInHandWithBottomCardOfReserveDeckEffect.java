package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardsInHandWithBottomCardInCardPileEffect;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to exchange a card from hand with bottom card of Reserve Deck.
 */
public class ExchangeCardInHandWithBottomCardOfReserveDeckEffect extends ExchangeCardsInHandWithBottomCardInCardPileEffect {

    /**
     * Creates an effect to exchange a card from hand with bottom card of Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public ExchangeCardInHandWithBottomCardOfReserveDeckEffect(Action action, String playerId) {
        super(action, playerId, Zone.RESERVE_DECK, 1, 1);
    }

    /**
     * Creates an effect to exchange a card from hand with bottom card of Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardInHandFilter the card in hand filter
     */
    public ExchangeCardInHandWithBottomCardOfReserveDeckEffect(Action action, String playerId, Filterable cardInHandFilter) {
        super(action, playerId, Zone.RESERVE_DECK, 1, 1, cardInHandFilter);
    }
}
