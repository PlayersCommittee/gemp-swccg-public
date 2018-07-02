package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to be stacked on another card from table.
 */
public class AboutToStackCardFromTableResult extends EffectResult implements AboutToLeaveTableResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToBeStacked;
    private PhysicalCard _stackOn;
    private PreventableCardEffect _effect;

    /**
     * Creates an effect result that is emitted when the specified card is about to be stacked on another card from table.
     * @param action the action
     * @param cardToBeStacked the card to be stacked
     * @param stackOn the card to stack on
     * @param effect the effect that can be used to prevent the card from being placed in card pile
     */
    public AboutToStackCardFromTableResult(Action action, PhysicalCard cardToBeStacked, PhysicalCard stackOn, PreventableCardEffect effect) {
        this(action, action.getPerformingPlayer(), cardToBeStacked, stackOn, effect);
    }

    /**
     * Creates an effect result that is emitted when the specified card is about to be stacked on another card from table.
     * @param action the action
     * @param performingPlayerId the performing player
     * @param cardToBeStacked the card to be stacked
     * @param stackOn the card to stack on
     * @param effect the effect that can be used to prevent the card from being placed in card pile
    */
    public AboutToStackCardFromTableResult(Action action, String performingPlayerId, PhysicalCard cardToBeStacked, PhysicalCard stackOn, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_BE_STACK_CARD_FROM_TABLE, performingPlayerId);
        _source = action.getActionSource();
        _cardToBeStacked = cardToBeStacked;
        _stackOn = stackOn;
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
     * Gets the card to be stacked.
     * @return the card to be stacked
     */
    public PhysicalCard getCardToBeStacked() {
        return _cardToBeStacked;
    }

    /**
     * Gets card to stack on.
     * @return the card to stack on
     */
    public PhysicalCard getCardToStackOn() {
        return _stackOn;
    }

    /**
     * Gets the interface that can be used to prevent the card from being stacked.
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
        return _cardToBeStacked;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to stack " + GameUtils.getCardLink(_cardToBeStacked) + " on " + GameUtils.getCardLink(_stackOn);
    }
}
