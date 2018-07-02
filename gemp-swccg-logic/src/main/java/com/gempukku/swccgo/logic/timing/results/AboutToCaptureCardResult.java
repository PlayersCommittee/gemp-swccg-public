package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to be captured.
 */
public class AboutToCaptureCardResult extends EffectResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToBeCaptured;
    private PreventableCardEffect _effect;

    /**
     * Creates an effect result that is emitted when the specified card is about to be captured.
     * @param action the action
     * @param cardToBeCaptured the card to be captured
     * @param effect the effect that can be used to prevent the card from being lost, or null
     */
    public AboutToCaptureCardResult(Action action, PhysicalCard cardToBeCaptured, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_BE_CAPTURED, action.getPerformingPlayer());
        _source = action.getActionSource();
        _cardToBeCaptured = cardToBeCaptured;
        _effect = effect;
    }

    /**
     * Gets the source card of the action.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _source;
    }

    /**
     * Gets the card to be captured.
     * @return the card
     */
    public PhysicalCard getCardToBeCaptured() {
        return _cardToBeCaptured;
    }

    /**
     * Gets the interface that can be used to prevent the card from being captured.
     * @return the interface
     */
    public PreventableCardEffect getPreventableCardEffect() {
        return _effect;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to capture " + GameUtils.getCardLink(_cardToBeCaptured);
    }
}
