package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy a card to a specified target from hand.
 */
public class DeployCardToTargetFromHandEffect extends DeployCardFromHandEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified target from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     */
    public DeployCardToTargetFromHandEffect(Action action, String playerId, Filter cardFilter, Filter targetFilter) {
        this(action, playerId, cardFilter, targetFilter, false);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified target from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param changeInCost the change in cost
     */
    public DeployCardToTargetFromHandEffect(Action action, String playerId, Filter cardFilter, Filter targetFilter, float changeInCost) {
        super(action, playerId, cardFilter, targetFilter, changeInCost);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified target from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployCardToTargetFromHandEffect(Action action, String playerId, Filter cardFilter, Filter targetFilter, boolean forFree) {
        this(action, playerId, cardFilter, targetFilter, forFree, null);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified target from hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     */
    public DeployCardToTargetFromHandEffect(Action action, String playerId, Filter cardFilter, Filter targetFilter, boolean forFree, DeploymentRestrictionsOption deploymentRestrictionsOption) {
        super(action, playerId, cardFilter, targetFilter, null, null, forFree, 0, null, deploymentRestrictionsOption);
    }

    /**
     * Creates an effect that causes the card owner to deploy the specified card from hand to the specified target.
     * @param action the action performing this effect
     * @param cardToDeploy the card to deploy
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param asReact true if deploying as a react, otherwise false
     */
    public DeployCardToTargetFromHandEffect(Action action, PhysicalCard cardToDeploy, Filter targetFilter, boolean forFree, boolean asReact) {
        super(action, cardToDeploy, targetFilter, forFree, asReact, null);
    }
}
