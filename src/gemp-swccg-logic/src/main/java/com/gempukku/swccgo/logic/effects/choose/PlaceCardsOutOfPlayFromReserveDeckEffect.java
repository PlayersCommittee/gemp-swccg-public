package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player to search Reserve Deck for cards and place them out of play.
 */
public class PlaceCardsOutOfPlayFromReserveDeckEffect extends PlaceCardsOutOfPlayFromPileEffect {

    /**
     * Creates an effect that causes the player to search Reserve Deck for cards and place them out of play.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public PlaceCardsOutOfPlayFromReserveDeckEffect(Action action, String playerId, String cardPileOwner, int minimum, int maximum, boolean reshuffle) {
        super(action, playerId, minimum, maximum, Zone.RESERVE_DECK, cardPileOwner, reshuffle);
    }

    /**
     * Creates an effect that causes the player to search Reserve Deck for cards accepted by the specified filter and place
     * them out of play.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public PlaceCardsOutOfPlayFromReserveDeckEffect(Action action, String playerId, String cardPileOwner, int minimum, int maximum, Filterable filters, boolean reshuffle) {
        super(action, playerId, minimum, maximum, Zone.RESERVE_DECK, cardPileOwner, false, filters, reshuffle);
    }
}
