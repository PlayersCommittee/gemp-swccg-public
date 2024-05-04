package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;

/**
 * An effect for peeking at the top card of a Force Pile.
 */
public class PeekAtTopCardOfForcePileEffect extends PeekAtTopCardsOfForcePileEffect {

    /**
     * Creates an effect for peeking at the top card of Force Pile.
     * @param action the action performing this effect
     * @param playerId the player to peek at cards
     */
    public PeekAtTopCardOfForcePileEffect(Action action, String playerId) {
        super(action, playerId, 1);
    }

    /**
     * Creates an effect for peeking at the top card of a specified player's Force Pile.
     * @param action the action performing this effect
     * @param playerId the player to peek at cards
     * @param cardPileOwner the owner of the card pile
     */
    public PeekAtTopCardOfForcePileEffect(Action action, String playerId, String cardPileOwner) {
        super(action, playerId, cardPileOwner, 1);
    }

    /**
     * A callback method for the cards peeked at.
     * @param peekedAtCards the cards peeked at
     */
    @Override
    protected final void cardsPeekedAt(List<PhysicalCard> peekedAtCards) {
        if (peekedAtCards.size() == 1) {
            cardPeekedAt(peekedAtCards.iterator().next());
        }
    }

    /**
     * A callback method for the card peeked at.
     * @param peekedAtCard the card peeked at
     */
    protected void cardPeekedAt(PhysicalCard peekedAtCard) {
    }
}
