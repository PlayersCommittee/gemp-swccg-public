package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a player is about to remove a just-lost card from Lost Pile.
 */
public class AboutToRemoveJustLostCardFromLostPileResult extends EffectResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToRemoveFromLostPile;
    private PreventableCardEffect _effect;

    /**
     * Creates an effect result that is emitted when a player is about to remove a just-lost card from Lost Pile.
     * @param action the action
     * @param performingPlayerId the performing player
     * @param cardToRemoveFromLostPile the card to be removed from Lost Pile
     * @param effect the effect that can be used to prevent the card from being removed from Lost Pile
    */
    public AboutToRemoveJustLostCardFromLostPileResult(Action action, String performingPlayerId, PhysicalCard cardToRemoveFromLostPile, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_REMOVE_JUST_LOST_CARD_FROM_LOST_PILE, performingPlayerId);
        _source = action.getActionSource();
        _cardToRemoveFromLostPile = cardToRemoveFromLostPile;
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
     * Gets the card to remove from Lost Pile.
     * @return the card
     */
    public PhysicalCard getCardToRemoveFromLostPile() {
        return _cardToRemoveFromLostPile;
    }

    /**
     * Gets the interface that can be used to prevent the card from being lost.
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
        return "About to remove " + GameUtils.getCardLink(_cardToRemoveFromLostPile) + " from Lost Pile";
    }
}
