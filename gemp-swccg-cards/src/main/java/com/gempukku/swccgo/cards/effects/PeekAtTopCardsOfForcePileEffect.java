package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect for peeking at the top cards of a Force Pile.
 */
public class PeekAtTopCardsOfForcePileEffect extends PeekAtTopCardsOfCardPileEffect {

    /**
     * Creates an effect for peeking at the top cards of Force Pile.
     * @param action the action performing this effect
     * @param playerId the player to peek at cards
     * @param count the number of cards to peek at
     */
    public PeekAtTopCardsOfForcePileEffect(Action action, String playerId, int count) {
        super(action, playerId, playerId, Zone.FORCE_PILE, count);
    }

    /**
     * Creates an effect for peeking at the top cards of a specified player's Force Pile.
     * @param action the action performing this effect
     * @param playerId the player to peek at cards
     * @param cardPileOwner the owner of the card pile
     * @param count the number of cards to peek at
     */
    public PeekAtTopCardsOfForcePileEffect(Action action, String playerId, String cardPileOwner, int count) {
        super(action, playerId, cardPileOwner, Zone.FORCE_PILE, count);
    }
}
