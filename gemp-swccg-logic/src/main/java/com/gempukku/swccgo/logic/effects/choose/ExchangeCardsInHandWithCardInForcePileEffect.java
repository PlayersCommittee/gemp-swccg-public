package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to exchange a specified number of cards from hand with a card from Force Pile.
 */
public class ExchangeCardsInHandWithCardInForcePileEffect extends ExchangeCardsInHandWithCardInCardPileEffect {

    /**
     * Creates an effect to exchange a specified number of cards from hand with a card from Force Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards from hand to exchange
     * @param maximum the maximum number of cards from hand to exchange
     */
    public ExchangeCardsInHandWithCardInForcePileEffect(Action action, String playerId, int minimum, int maximum, boolean reshuffle) {
        super(action, playerId, Zone.FORCE_PILE, minimum, maximum, reshuffle);
    }
}
