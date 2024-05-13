package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to be returned to hand from table.
 */
public class AboutToReturnCardToHandFromTableResult extends EffectResult implements AboutToLeaveTableResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToBeReturnedToHand;
    private PreventableCardEffect _effect;
    private boolean _allCardsSituation;

    /**
     * Creates an effect result that is emitted when the specified card is about to be returned to hand from table.
     * @param action the action
     * @param cardToBeReturnedToHand the card to be returned to hand
     * @param effect the effect that can be used to prevent the card from being returned to hand
     */
    public AboutToReturnCardToHandFromTableResult(Action action, PhysicalCard cardToBeReturnedToHand, PreventableCardEffect effect) {
        this(action, action.getPerformingPlayer(), cardToBeReturnedToHand, effect);
    }

    /**
     * Creates an effect result that is emitted when the specified card is about to be returned to hand from table.
     * @param action the action
     * @param performingPlayerId the performing player
     * @param cardToBeReturnedToHand the card to be returned to hand
     * @param effect the effect that can be used to prevent the card from being returned to hand
    */
    public AboutToReturnCardToHandFromTableResult(Action action, String performingPlayerId, PhysicalCard cardToBeReturnedToHand, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_BE_RETURNED_TO_HAND_FROM_TABLE, performingPlayerId);
        _source = action.getActionSource();
        _cardToBeReturnedToHand = cardToBeReturnedToHand;
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
     * Gets the card to be returned to hand.
     * @return the card
     */
    public PhysicalCard getCardToBeReturnedToHand() {
        return _cardToBeReturnedToHand;
    }

    /**
     * Gets the interface that can be used to prevent the card from being returned to hand.
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
        return _cardToBeReturnedToHand;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to return " + GameUtils.getCardLink(_cardToBeReturnedToHand) + " to hand";
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
