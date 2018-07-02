package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a race destiny is stacked on a card.
 */
public class RaceDestinyStackedResult extends EffectResult {
    private PhysicalCard _card;
    private PhysicalCard _stackedOn;

    /**
     * Creates an effect result that is triggered when a race destiny is stacked on a card.
     * @param action the action performing this effect result
     * @param card the card
     * @param stackedOn the card to stack on
     */
    public RaceDestinyStackedResult(Action action, PhysicalCard card, PhysicalCard stackedOn) {
        super(Type.RACE_DESTINY_STACKED, action.getPerformingPlayer());
        _card = card;
        _stackedOn = stackedOn;
    }

    /**
     * Gets the card stacked from hand.
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

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Race destiny just stacked on " + GameUtils.getCardLink(_stackedOn);
    }
}
