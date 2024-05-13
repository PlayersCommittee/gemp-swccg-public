package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a card is stacked on another card
 */
public class StackedCardResult extends EffectResult {
    private PhysicalCard _card;
    private PhysicalCard _stackedOn;

    /**
     * Creates an effect result that is triggered when a card is stacked on another card (regardless of source)
     * @param action the action performing this effect result
     * @param effectResultType of stack action (STACKED_FROM_CARD_PILE, STACKED_FROM_HAND, etc)
     * @param card the card
     * @param stackedOn the card to stack on
     */
    public StackedCardResult(Action action, Type effectResultType, PhysicalCard card, PhysicalCard stackedOn) {
        super(effectResultType, action.getPerformingPlayer());
        _card = card;
        _stackedOn = stackedOn;
    }

    /**
     * Gets the card stacked onto another card.
     * @return the card
     */
    public PhysicalCard getCard() {
        return _card;
    }

    /**
     * Gets the card the card was stacked on.
     * @return the card
     */
    public PhysicalCard getStackedOn() {
        return _stackedOn;
    }

}
