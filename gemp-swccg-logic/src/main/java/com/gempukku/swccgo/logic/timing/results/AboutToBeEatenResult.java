package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to be 'eaten'.
 */
public class AboutToBeEatenResult extends EffectResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToBeEaten;
    private PhysicalCard _eatenByCard;

    /**
     * Creates an effect result that is emitted when the specified card is about to be 'eaten'.
     * @param action the action
     * @param cardToBeEaten the card to be 'eaten'
     * @param eatenByCard the card that is eating
     */
    public AboutToBeEatenResult(Action action, PhysicalCard cardToBeEaten, PhysicalCard eatenByCard) {
        super(Type.ABOUT_TO_BE_EATEN, action.getPerformingPlayer());
        _source = action.getActionSource();
        _cardToBeEaten = cardToBeEaten;
        _eatenByCard = eatenByCard;
    }

    /**
     * Gets the source card of the action.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _source;
    }

    /**
     * Gets the card to be 'eaten'.
     * @return the card
     */
    public PhysicalCard getCardToBeEaten() {
        return _cardToBeEaten;
    }

    /**
     * Gets the card that is eating.
     * @return the card that is eating
     */
    public PhysicalCard getEatenByCard() {
        return _eatenByCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_eatenByCard) + " is about to 'eat' " + GameUtils.getCardLink(_cardToBeEaten);
    }
}
