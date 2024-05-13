package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to be canceled on table.
 */
public class AboutToCancelCardOnTableResult extends EffectResult implements AboutToLeaveTableResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToBeCanceled;
    private PreventableCardEffect _effect;
    private boolean _allCardsSituation;

    /**
     * Creates an effect result that is emitted when the specified card is about to be canceled on table.
     * @param action the action
     * @param cardToBeCanceled the card to be canceled
     * @param effect the effect that can be used to prevent the card from being canceled
     */
    public AboutToCancelCardOnTableResult(Action action, PhysicalCard cardToBeCanceled, PreventableCardEffect effect) {
        this(action, action.getPerformingPlayer(), cardToBeCanceled, effect);
    }

    /**
     * Creates an effect result that is emitted when the specified card is about to be canceled on table.
     * @param action the action
     * @param performingPlayerId the performing player
     * @param cardToBeCanceled the card to be canceled
     * @param effect the effect that can be used to prevent the card from being canceled
    */
    public AboutToCancelCardOnTableResult(Action action, String performingPlayerId, PhysicalCard cardToBeCanceled, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_BE_CANCELED_ON_TABLE, performingPlayerId);
        _source = action.getActionSource();
        _cardToBeCanceled = cardToBeCanceled;
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
     * Gets the card to be lost.
     * @return the card
     */
    public PhysicalCard getCardToBeCanceled() {
        return _cardToBeCanceled;
    }

    /**
     * Gets the interface that can be used to prevent the card from being canceled.
     * @return the interface
     */
    public PreventableCardEffect getPreventableCardEffect() {
        return _effect;
    }

    /**
     * Gets the card about to leave table.
     * @return the card
     */
    public PhysicalCard getCardAboutToLeaveTable() {
        return _cardToBeCanceled;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to cancel " + GameUtils.getCardLink(_cardToBeCanceled);
    }

    /**
     * Determines if this is an all cards situation.
     * @return true or false
     */
    @Override
    public boolean isAllCardsSituation() {
        return _allCardsSituation;
    }
}
