package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * This effect result is triggered when a card is stacked from a card pile and card pile was not shuffled.
 */
public class StackedFromCardPileResult extends StackedCardResult {

    /**
     * Creates an effect result that is triggered when a card is stacked from a card pile and card pile was not shuffled.
     * @param action the action performing this effect result
     * @param card the card
     * @param stackedOn the card to stack on
     */
    public StackedFromCardPileResult(Action action, PhysicalCard card, PhysicalCard stackedOn) {
        super(action, Type.STACKED_FROM_CARD_PILE, card, stackedOn);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Stacked a card from a card pile";
    }
}
