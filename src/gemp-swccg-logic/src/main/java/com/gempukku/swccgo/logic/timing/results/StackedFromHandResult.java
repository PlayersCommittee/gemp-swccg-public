package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * This effect result is triggered when a card is stacked from hand.
 */
public class StackedFromHandResult extends StackedCardResult {

    /**
     * Creates an effect result that is triggered when a card is stacked from a hand.
     * @param action the action performing this effect result
     * @param card the card
     * @param stackedOn the card to stack on
     */
    public StackedFromHandResult(Action action, PhysicalCard card, PhysicalCard stackedOn) {
        super(action, Type.STACKED_FROM_HAND, card, stackedOn);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Stacked a card from hand";
    }
}
