package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;

/**
 * An effect for revealing the top card of a Reserve Deck.
 */
public class RevealTopCardOfReserveDeckEffect extends RevealTopCardsOfReserveDeckEffect {

    /**
     * Creates an effect for revealing the top card of Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player to revealing the cards
     */
    public RevealTopCardOfReserveDeckEffect(Action action, String playerId) {
        super(action, playerId, 1);
    }

    /**
     * Creates an effect for revealing the top card of a specified player's Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player to reveal the cards
     * @param cardPileOwner the owner of the card pile
     */
    public RevealTopCardOfReserveDeckEffect(Action action, String playerId, String cardPileOwner) {
        super(action, playerId, cardPileOwner, 1);
    }

    /**
     * A callback method for the cards revealed.
     * @param revealedCards the cards revealed
     */
    @Override
    protected final void cardsRevealed(List<PhysicalCard> revealedCards) {
        if (revealedCards.size() == 1) {
            cardRevealed(revealedCards.iterator().next());
        }
    }

    /**
     * A callback method for the card revealed.
     * @param revealedCard the card revealed
     */
    protected void cardRevealed(PhysicalCard revealedCard) {
    }
}
