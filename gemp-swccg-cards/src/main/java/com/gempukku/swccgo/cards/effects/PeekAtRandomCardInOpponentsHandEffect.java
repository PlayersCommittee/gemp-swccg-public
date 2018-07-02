package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that peeks at a random card from the opponent's hand.
 */
public class PeekAtRandomCardInOpponentsHandEffect extends PeekAtRandomCardsInOpponentsHandEffect {

    /**
     * Creates an effect that peeks at a random cards from the opponent's hand.
     * @param action the action performing this effect
     * @param playerId the player peeking at a card from opponent's hand
     */
    public PeekAtRandomCardInOpponentsHandEffect(Action action, String playerId) {
        super(action, playerId, 1);
    }
}
