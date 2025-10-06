package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardsInHandWithTopCardInCardPileEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to exchange a card from hand with top card of Force Pile.
 */
public class ExchangeCardInHandWithTopCardOfForcePileEffect extends ExchangeCardsInHandWithTopCardInCardPileEffect {

    /**
     * Creates an effect to exchange a card from hand with top card of Force Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public ExchangeCardInHandWithTopCardOfForcePileEffect(Action action, String playerId) {
        super(action, playerId, Zone.FORCE_PILE, 1, 1);
    }

    /**
     * Creates an effect to exchange a card from hand with top card of Force Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardInHandFilter the card in hand filter
     */
    public ExchangeCardInHandWithTopCardOfForcePileEffect(Action action, String playerId, Filterable cardInHandFilter) {
        super(action, playerId, Zone.FORCE_PILE, 1, 1, cardInHandFilter);
    }
    
}
