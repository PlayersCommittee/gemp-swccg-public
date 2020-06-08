package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put cards from hand on bottom of Reserve Deck.
 */
public class PutCardsFromHandOnBottomOfReserveDeckEffect extends PutCardsFromHandInCardPileEffect {

    /**
     * Creates an effect that causes the player to put all cards from hand on bottom of Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public PutCardsFromHandOnBottomOfReserveDeckEffect(Action action, String playerId) {
        super(action, playerId, Zone.RESERVE_DECK, true);
    }

    /**
     * Creates an effect that causes the player to put cards from hand on bottom of Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put on card pile
     * @param maximum the maximum number of cards to put on card pile
     */
    public PutCardsFromHandOnBottomOfReserveDeckEffect(Action action, String playerId, int minimum, int maximum) {
       super(action, playerId, minimum, maximum, Zone.RESERVE_DECK, playerId, true);
    }

    /**
     * Creates an effect that causes the player to put cards accepted by the specified filter from hand on bottom of Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put on card pile
     * @param maximum the maximum number of cards to put on card pile
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardsFromHandOnBottomOfReserveDeckEffect(Action action, String playerId, int minimum, int maximum, Filterable filters, boolean hidden) {
        super(action, playerId, minimum, maximum, Zone.RESERVE_DECK, playerId, true, filters, hidden);
    }
}
