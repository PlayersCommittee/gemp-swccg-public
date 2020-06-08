package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;

/**
 * An effect that reveals a random card from the opponent's hand.
 */
public class RevealRandomCardInOpponentsHandEffect extends RevealRandomCardsInOpponentsHandEffect {

    /**
     * Creates an effect that reveals a random cards from the opponent's hand.
     * @param action the action performing this effect
     * @param playerId the player revealing a card from opponent's hand
     */
    public RevealRandomCardInOpponentsHandEffect(Action action, String playerId) {
        super(action, playerId, 1);
    }

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
