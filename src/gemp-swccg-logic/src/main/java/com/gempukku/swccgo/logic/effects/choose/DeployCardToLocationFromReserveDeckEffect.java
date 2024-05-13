package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to search Reserve Deck for a card and deploy it to a specified
 * location (or deploy a specific card from Reserve Deck to a specified location).
 */
public class DeployCardToLocationFromReserveDeckEffect extends DeployCardToTargetFromReserveDeckEffect {

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a location accepted by the location filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromReserveDeckEffect(Action action, Filter cardFilter, Filter locationFilter, boolean reshuffle) {
        this(action, cardFilter, locationFilter, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a location accepted by the location filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromReserveDeckEffect(Action action, Filter cardFilter, Filter locationFilter, DeploymentRestrictionsOption deploymentRestrictionsOption, boolean reshuffle) {
        this(action, cardFilter, locationFilter, false, deploymentRestrictionsOption, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a location accepted by the location filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param ignoreLocationFilterCardFilter the card filter for cards that ignore the location filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromReserveDeckEffect(Action action, Filter cardFilter, Filter locationFilter, Filter ignoreLocationFilterCardFilter, boolean reshuffle) {
        super(action, cardFilter, Filters.locationAndCardsAtLocation(locationFilter), ignoreLocationFilterCardFilter, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a location accepted by the location filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromReserveDeckEffect(Action action, Filter cardFilter, Filter locationFilter, boolean forFree, boolean reshuffle) {
        super(action, cardFilter, Filters.locationAndCardsAtLocation(locationFilter), forFree, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a location accepted by the location filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromReserveDeckEffect(Action action, Filter cardFilter, Filter locationFilter, boolean forFree, DeploymentRestrictionsOption deploymentRestrictionsOption, boolean reshuffle) {
        super(action, cardFilter, Filters.locationAndCardsAtLocation(locationFilter), forFree, deploymentRestrictionsOption, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a location accepted by the location filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromReserveDeckEffect(Action action, Filter cardFilter, Filter locationFilter, Filter forFreeFilter, boolean asReact, boolean reshuffle) {
        super(action, cardFilter, Filters.locationAndCardsAtLocation(locationFilter), null, forFreeFilter, asReact, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a location accepted by the location filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromReserveDeckEffect(Action action, Filter cardFilter, Filter locationFilter, boolean forFree, boolean asReact, boolean reshuffle) {
        super(action, cardFilter, Filters.locationAndCardsAtLocation(locationFilter), forFree, asReact, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a location accepted by the location filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromReserveDeckEffect(Action action, Filter cardFilter, Filter locationFilter, float changeInCost, boolean reshuffle) {
        super(action, cardFilter, Filters.locationAndCardsAtLocation(locationFilter), changeInCost, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a specific card from Reserve Deck to a
     * location accepted by the location filter.
     * @param action the action performing this effect
     * @param card the card
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromReserveDeckEffect(Action action, PhysicalCard card, Filter locationFilter, boolean forFree, boolean asReact, boolean reshuffle) {
        super(action, card, Filters.locationAndCardsAtLocation(locationFilter), forFree, asReact, reshuffle);
    }
}
