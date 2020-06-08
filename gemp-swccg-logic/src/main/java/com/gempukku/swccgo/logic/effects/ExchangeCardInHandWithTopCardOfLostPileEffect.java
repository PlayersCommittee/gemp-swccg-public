package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardsInHandWithTopCardInCardPileEffect;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to exchange a card from hand with top card of Lost Pile.
 */
public class ExchangeCardInHandWithTopCardOfLostPileEffect extends ExchangeCardsInHandWithTopCardInCardPileEffect {

    /**
     * Creates an effect to exchange a card from hand with top card of Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public ExchangeCardInHandWithTopCardOfLostPileEffect(Action action, String playerId) {
        super(action, playerId, Zone.LOST_PILE, 1, 1);
    }

    /**
     * Creates an effect to exchange a card from hand with top card of Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardInHandFilter the card in hand filter
     */
    public ExchangeCardInHandWithTopCardOfLostPileEffect(Action action, String playerId, Filterable cardInHandFilter) {
        super(action, playerId, Zone.LOST_PILE, 1, 1, cardInHandFilter);
    }
}
