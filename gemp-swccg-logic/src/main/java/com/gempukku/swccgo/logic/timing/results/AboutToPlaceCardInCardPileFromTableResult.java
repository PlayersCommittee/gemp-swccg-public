package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to be placed in a card pile from table.
 */
public class AboutToPlaceCardInCardPileFromTableResult extends EffectResult implements AboutToLeaveTableResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToBePlacedInCardPile;
    private Zone _cardPile;
    private PreventableCardEffect _effect;
    private boolean _allCardsSituation;

    /**
     * Creates an effect result that is emitted when the specified card is about to be lost from table.
     * @param action the action
     * @param cardToBePlacedInCardPile the card to be placed in card pile
     * @param cardPile the card pile
     * @param effect the effect that can be used to prevent the card from being placed in card pile
     */
    public AboutToPlaceCardInCardPileFromTableResult(Action action, PhysicalCard cardToBePlacedInCardPile, Zone cardPile, PreventableCardEffect effect) {
        this(action, action.getPerformingPlayer(), cardToBePlacedInCardPile, cardPile, effect);
    }

    /**
     * Creates an effect result that is emitted when the specified card is about to be lost from table.
     * @param action the action
     * @param cardToBePlacedInCardPile the card to be placed in card pile
     * @param cardPile the card pile
     * @param effect the effect that can be used to prevent the card from being placed in card pile
     */
    public AboutToPlaceCardInCardPileFromTableResult(Action action, PhysicalCard cardToBePlacedInCardPile, Zone cardPile, PreventableCardEffect effect, boolean allCardsSituation) {
        this(action, action.getPerformingPlayer(), cardToBePlacedInCardPile, cardPile, effect);
        _allCardsSituation = allCardsSituation;
    }

    /**
     * Creates an effect result that is emitted when the specified card is about to be lost from table.
     * @param action the action
     * @param performingPlayerId the performing player
     * @param cardToBePlacedInCardPile the card to be placed in card pile
     * @param cardPile the card pile
     * @param effect the effect that can be used to prevent the card from being placed in card pile
    */
    public AboutToPlaceCardInCardPileFromTableResult(Action action, String performingPlayerId, PhysicalCard cardToBePlacedInCardPile, Zone cardPile, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_BE_PLACE_IN_CARD_PILE_FROM_TABLE, performingPlayerId);
        _source = action.getActionSource();
        _cardToBePlacedInCardPile = cardToBePlacedInCardPile;
        _cardPile = cardPile;
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
     * Gets the card to be placed in card pile.
     * @return the card
     */
    public PhysicalCard getCardToBePlacedInCardPile() {
        return _cardToBePlacedInCardPile;
    }

    /**
     * Gets the card pile.
     * @return the card pile
     */
    public Zone getCardPile() {
        return _cardPile;
    }

    /**
     * Gets the interface that can be used to prevent the card from being placed in a card pile.
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
        return _cardToBePlacedInCardPile;
    }

    /**
     * Determines if this is an all cards situation.
     * @return true or false
     */
    public boolean isAllCardsSituation() {
        return _allCardsSituation;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to place " + GameUtils.getCardLink(_cardToBePlacedInCardPile) + " in " + _cardPile.getHumanReadable();
    }
}
