package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put cards from hand on Reserve Deck.
 */
public class PutCardsFromHandOnReserveDeckEffect extends PutCardsFromHandInCardPileEffect {

    /**
     * Creates an effect that causes the player to put all cards from hand on Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public PutCardsFromHandOnReserveDeckEffect(Action action, String playerId) {
        super(action, playerId, Zone.RESERVE_DECK, false);
    }

    /**
     * Creates an effect that causes the player to put cards from hand on Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put on card pile
     * @param maximum the maximum number of cards to put on card pile
     */
    public PutCardsFromHandOnReserveDeckEffect(Action action, String playerId, int minimum, int maximum) {
       super(action, playerId, minimum, maximum, Zone.RESERVE_DECK, playerId, false);
    }

    /**
     * Creates an effect that causes the player to put cards accepted by the specified filter from hand on Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put on card pile
     * @param maximum the maximum number of cards to put on card pile
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardsFromHandOnReserveDeckEffect(Action action, String playerId, int minimum, int maximum, Filterable filters, boolean hidden) {
        super(action, playerId, minimum, maximum, Zone.RESERVE_DECK, playerId, false, filters, hidden);
    }
}
