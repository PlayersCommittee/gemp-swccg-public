package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when cards are removed from a card pile and the card pile was not shuffled.
 */
public class RemovedFromCardPileResult extends EffectResult {

    /**
     * Creates an effect result that is triggered when a cards are removed from a card pile and the card pile was not shuffled.
     * @param action the action performing this effect result
     */
    public RemovedFromCardPileResult(Action action) {
        super(Type.REMOVE_FROM_CARD_PILE, action.getPerformingPlayer());
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Removed card from a card pile";
    }
}
