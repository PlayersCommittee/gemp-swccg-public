package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

/**
 * An effect that causes the player performing the action to choose and deploy a card to a specified location from hand.
 */
public class DeployCardToLocationFromHandEffect extends DeployCardToTargetFromHandEffect implements TargetingEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified location from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     */
    public DeployCardToLocationFromHandEffect(Action action, String playerId, Filter cardFilter, Filter locationFilter) {
        this(action, playerId, cardFilter, locationFilter, false);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified location from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param changeInCost the change in cost
     */
    public DeployCardToLocationFromHandEffect(Action action, String playerId, Filter cardFilter, Filter locationFilter, float changeInCost) {
        super(action, playerId, cardFilter, locationFilter, changeInCost);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified location from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployCardToLocationFromHandEffect(Action action, String playerId, Filter cardFilter, Filter locationFilter, boolean forFree) {
        this(action, playerId, cardFilter, Filters.locationAndCardsAtLocation(locationFilter), forFree, null);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified location from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     */
    public DeployCardToLocationFromHandEffect(Action action, String playerId, Filter cardFilter, Filter locationFilter, boolean forFree, DeploymentRestrictionsOption deploymentRestrictionsOption) {
        super(action, playerId, cardFilter, Filters.locationAndCardsAtLocation(locationFilter), forFree, deploymentRestrictionsOption);
    }

    /**
     * Creates an effect that causes the card owner to deploy the specified card from hand to the specified location.
     * @param action the action performing this effect
     * @param cardToDeploy the card to deploy
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     * @param asReact true if deploying as a react, otherwise false
     */
    public DeployCardToLocationFromHandEffect(Action action, PhysicalCard cardToDeploy, Filter locationFilter, boolean forFree, boolean asReact) {
        super(action, cardToDeploy, Filters.locationAndCardsAtLocation(locationFilter), forFree, asReact);
    }
}
