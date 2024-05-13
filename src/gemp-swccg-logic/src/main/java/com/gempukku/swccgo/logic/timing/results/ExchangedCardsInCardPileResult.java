package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when cards are exchanged with a card pile and the card pile was not shuffled.
 */
public class ExchangedCardsInCardPileResult extends EffectResult {

    /**
     * Creates an effect result that is triggered when a cards are exchanged with a card pile and the card pile was not shuffled.
     * @param action the action performing this effect result
     */
    public ExchangedCardsInCardPileResult(Action action) {
        super(Type.EXCHANGE_WITH_CARD_PILE, action.getPerformingPlayer());
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Exchanged cards with a card pile";
    }
}
