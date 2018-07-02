package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;

/**
 * The effect result that is emitted when a card is about to be lost from table.
 */
public class AboutToLoseCardFromTableResult extends EffectResult implements AboutToLeaveTableResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToBeLost;
    private PreventableCardEffect _effect;
    private boolean _allCardsSituation;
    private Collection<PhysicalCard> _allCardsAboutToBeLost;

    /**
     * Creates an effect result that is emitted when the specified card is about to be lost from table.
     * @param action the action
     * @param cardToBeLost the card to be lost
     * @param effect the effect that can be used to prevent the card from being lost, or null
     * @param allCardsSituation true if this is an "all cards situation", otherwise false
     * @param allCardsAboutToBeLost all the cards about to be lost; this is used during all cards situation to see if
     *                              card aboard creature vehicles is also about to be lost, which would mean that character
     *                              could not "jump off"
     */
    public AboutToLoseCardFromTableResult(Action action, PhysicalCard cardToBeLost, PreventableCardEffect effect,
                                          boolean allCardsSituation, Collection<PhysicalCard> allCardsAboutToBeLost) {
        this(action, action.getPerformingPlayer(), cardToBeLost, effect, allCardsSituation, allCardsAboutToBeLost);
    }

    /**
     * Creates an effect result that is emitted when the specified card is about to be lost from table.
     * @param action the action
     * @param performingPlayerId the performing player
     * @param cardToBeLost the card to be lost
     * @param effect the effect that can be used to prevent the card from being lost, or null
     * @param allCardsSituation true if this is an "all cards situation", otherwise false
     * @param allCardsAboutToBeLost all the cards about to be lost; this is used during all cards situation to see if
     *                              card aboard creature vehicles is also about to be lost, which would mean that character
     *                              could not "jump off"
    */
    public AboutToLoseCardFromTableResult(Action action, String performingPlayerId, PhysicalCard cardToBeLost, PreventableCardEffect effect,
                                          boolean allCardsSituation, Collection<PhysicalCard> allCardsAboutToBeLost) {
        super(Type.ABOUT_TO_BE_LOST_FROM_TABLE, performingPlayerId);
        _source = action.getActionSource();
        _cardToBeLost = cardToBeLost;
        _effect = effect;
        _allCardsSituation = allCardsSituation;
        _allCardsAboutToBeLost = allCardsAboutToBeLost;
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
    public PhysicalCard getCardToBeLost() {
        return _cardToBeLost;
    }

    /**
     * Gets the interface that can be used to prevent the card from being lost.
     * @return the interface
     */
    public PreventableCardEffect getPreventableCardEffect() {
        return _effect;
    }

    /**
     * Determines if this is an all cards situation.
     * @return true or false
     */
    public boolean isAllCardsSituation() {
        return _allCardsSituation;
    }

    /**
     * Gets all cards about to be lost.
     * @return the interface
     */
    public Collection<PhysicalCard> getAllCardsAboutToBeLost() {
        return _allCardsAboutToBeLost;
    }

    /**
     * Gets the card about to leave table.
     * @return the card
     */
    public PhysicalCard getCardAboutToLeaveTable() {
        return _cardToBeLost;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to lose " + GameUtils.getCardLink(_cardToBeLost);
    }
}
