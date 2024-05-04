package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect for revealing the top cards of a Lost Pile.
 */
public class RevealTopCardsOfLostPileEffect extends RevealTopCardsOfCardPileEffect {

    /**
     * Creates an effect for revealing the top cards of Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player to peek at cards
     * @param count the number of cards to peek at
     */
    public RevealTopCardsOfLostPileEffect(Action action, String playerId, int count) {
        super(action, playerId, playerId, Zone.LOST_PILE, count);
    }

    /**
     * Creates an effect for revealing the top cards of a specified player's Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player to peek at cards
     * @param cardPileOwner the owner of the card pile
     * @param count the number of cards to peek at
     */
    public RevealTopCardsOfLostPileEffect(Action action, String playerId, String cardPileOwner, int count) {
        super(action, playerId, cardPileOwner, Zone.LOST_PILE, count);
    }
}
