package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to be placed out of play from table.
 */
public class AboutToPlaceCardOutOfPlayFromTableResult extends EffectResult implements AboutToLeaveTableResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToBePlacedOutOfPlay;
    private PreventableCardEffect _effect;
    private boolean _allCardsSituation;

    /**
     * Creates an effect result that is emitted when the specified card is about to be placed out of play from table.
     * @param action the action
     * @param cardToBePlacedOutOfPlay the card to be placed out of play
     * @param effect the effect that can be used to prevent the card from being placed out of play
     */
    public AboutToPlaceCardOutOfPlayFromTableResult(Action action, PhysicalCard cardToBePlacedOutOfPlay, PreventableCardEffect effect) {
        this(action, action.getPerformingPlayer(), cardToBePlacedOutOfPlay, effect);
    }

    /**
     * Creates an effect result that is emitted when the specified card is about to be placed out of play from table.
     * @param action the action
     * @param performingPlayerId the performing player
     * @param cardToBePlacedOutOfPlay the card to be placed out of play
     * @param effect the effect that can be used to prevent the card from being placed out of play
     */
    public AboutToPlaceCardOutOfPlayFromTableResult(Action action, String performingPlayerId, PhysicalCard cardToBePlacedOutOfPlay, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_BE_PLACED_OUT_OF_PLAY_FROM_TABLE, performingPlayerId);
        _source = action.getActionSource();
        _cardToBePlacedOutOfPlay = cardToBePlacedOutOfPlay;
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
     * Gets the card to be placed out of play.
     * @return the card
     */
    public PhysicalCard getCardToBePlacedOutOfPlay() {
        return _cardToBePlacedOutOfPlay;
    }

    /**
     * Gets the interface that can be used to prevent the card from being placed out of play.
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
        return _cardToBePlacedOutOfPlay;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to place " + GameUtils.getCardLink(_cardToBePlacedOutOfPlay) + " out of play";
    }

    /**
     * Determines if this is an all cards situation.
     * @return true or false
     */
    public boolean isAllCardsSituation() {
        return _allCardsSituation;
    }
}
