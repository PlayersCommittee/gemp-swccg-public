package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy cards from Reserve Deck to a specified location.
 */
public class DeployCardsToLocationFromReserveDeckEffect extends DeployCardsFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from Reserve Deck to a location accepted by the location filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param locationFilter the location filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsToLocationFromReserveDeckEffect(Action action, Filter cardFilter, int minimum, int maximum, Filterable locationFilter, boolean reshuffle) {
        this(action, cardFilter, minimum, maximum, locationFilter, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from Reserve Deck to a location accepted by the location filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param locationFilter the location filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsToLocationFromReserveDeckEffect(Action action, Filter cardFilter, int minimum, int maximum, Filterable locationFilter, float changeInCost, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, minimum, maximum, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)), null, null, false, changeInCost, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from Reserve Deck to a location accepted by the location filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsToLocationFromReserveDeckEffect(Action action, Filter cardFilter, int minimum, int maximum, Filterable locationFilter, boolean forFree, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, minimum, maximum, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)), null, null, forFree, 0, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from Reserve Deck to a location accepted by the location filter.
     * @param action the action performing this effect
     * @param performingPlayerId the player to deploy cards
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsToLocationFromReserveDeckEffect(Action action, String performingPlayerId, Filter cardFilter, int minimum, int maximum, Filterable locationFilter, boolean forFree, boolean reshuffle) {
        super(action, performingPlayerId, Zone.RESERVE_DECK, cardFilter, minimum, maximum, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)), null, null, forFree, 0, reshuffle);
    }
}
