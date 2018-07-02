package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put a card from hand on Lost Pile.
 */
public class PutCardFromHandOnLostPileEffect extends PutCardsFromHandOnLostPileEffect {

    /**
     * Creates an effect that causes the player to put a card from hand on Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public PutCardFromHandOnLostPileEffect(Action action, String playerId) {
       super(action, playerId, 1, 1);
    }

    /**
     * Creates an effect that causes the player to put a card accepted by the specified filter from hand on Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardFromHandOnLostPileEffect(Action action, String playerId, Filterable filters, boolean hidden) {
        super(action, playerId, 1, 1, filters, hidden);
    }

    /**
     * Creates an effect that causes the player to put a card accepted by the specified filter from hand on specified
     * player's Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardFromHandOnLostPileEffect(Action action, String playerId, String cardPileOwner, Filterable filters, boolean hidden) {
        super(action, playerId, cardPileOwner, 1, 1, filters, hidden);
    }
}
