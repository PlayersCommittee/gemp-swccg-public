package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a card is placed in a card pile that was not on the table (e.g. in a card pile,
 * in hand, etc.).
 */
public class PutCardInCardPileFromOffTableResult extends EffectResult {
    private PhysicalCard _card;
    private String _cardPileOwner;
    private Zone _cardPile;
    private boolean _isPlayedInterrupt;

    /**
     * Creates an effect result that is triggered when a card is place in a card pile that was not on the table (e.g. in
     * a card pile, in hand, etc.).
     * @param action the action performing this effect result
     * @param card the card
     * @param cardPileOwner the card pile owner
     * @param cardPile the card pile
     * @param isPlayedInterrupt the card placed in card pile is a played Interrupt
     */
    public PutCardInCardPileFromOffTableResult(Action action, PhysicalCard card, String cardPileOwner, Zone cardPile, boolean isPlayedInterrupt) {
        this(action, action.getPerformingPlayer(), card, cardPileOwner, cardPile, isPlayedInterrupt);
    }

    /**
     * Creates an effect result that is triggered when a card is place in a card pile that was not on the table (e.g. in
     * a card pile, in hand, etc.).
     * @param action the action performing this effect result
     * @param performingPlayerId the performing player
     * @param card the card
     * @param cardPileOwner the card pile owner
     * @param cardPile the card pile
     * @param isPlayedInterrupt the card placed in card pile is a played Interrupt
     */
    public PutCardInCardPileFromOffTableResult(Action action, String performingPlayerId, PhysicalCard card, String cardPileOwner, Zone cardPile, boolean isPlayedInterrupt) {
        super(Type.PUT_IN_CARD_PILE_FROM_OFF_TABLE, performingPlayerId);
        _card = card;
        _cardPileOwner = cardPileOwner;
        _cardPile = cardPile;
        _isPlayedInterrupt = isPlayedInterrupt;
    }

    /**
     * The card placed in card pile.
     * @return the card
     */
    public PhysicalCard getCard() {
        return _card;
    }

    /**
     * Gets the owner of the card pile that the card that was not on the table (e.g. in a card pile, in hand, etc.) was
     * place in.
     * @return the card
     */
    public String getCardPileOwner() {
        return _cardPileOwner;
    }

    /**
     * Gets the card pile that the card that was not on the table (e.g. in a card pile, in hand, etc.) was place in.
     * @return the card pile
     */
    public Zone getCardPile() {
        return _cardPile;
    }

    /**
     * Determines if the card placed in card pile is a played Interrupt.
     * @return true or false
     */
    public boolean isPlayedInterrupt() {
        return _isPlayedInterrupt;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Placed card in " + getCardPile().getHumanReadable();
    }
}
