package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to be stolen.
 */
public class AboutToBeStolenResult extends EffectResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToBeStolen;
    private PreventableCardEffect _effect;

    /**
     * Creates an effect result that is emitted when the specified card is about to be stolen.
     * @param action the action
     * @param cardToBeStolen the card to be stolen
     * @param effect the effect that can be used to prevent the card from being stolen
    */
    public AboutToBeStolenResult(Action action, PhysicalCard cardToBeStolen, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_BE_STOLEN, action.getPerformingPlayer());
        _source = action.getActionSource();
        _cardToBeStolen = cardToBeStolen;
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
     * Gets the card to be stolen.
     * @return the card
     */
    public PhysicalCard getCardToBeStolen() {
        return _cardToBeStolen;
    }

    /**
     * Gets the interface that can be used to prevent the card from being stolen.
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
        return "About to steal " + GameUtils.getCardLink(_cardToBeStolen);
    }
}
