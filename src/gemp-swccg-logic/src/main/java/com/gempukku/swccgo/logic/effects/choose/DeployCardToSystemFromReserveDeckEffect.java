package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy a card to a system from Reserve Deck.
 */
public class DeployCardToSystemFromReserveDeckEffect extends DeployCardFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified system from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param system the system name
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToSystemFromReserveDeckEffect(Action action, Filter cardFilter, String system, boolean reshuffle) {
        this(action, cardFilter, system, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified system from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param system the system name
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToSystemFromReserveDeckEffect(Action action, Filter cardFilter, String system, boolean forFree, boolean reshuffle) {
        this(action, cardFilter, system, null, forFree, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified system from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param system the system name
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToSystemFromReserveDeckEffect(Action action, Filter cardFilter, String system, Filter specialLocationConditions, boolean forFree, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, null, null, system, specialLocationConditions, forFree, null, 0, null, null, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified system from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param system the system name
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToSystemFromReserveDeckEffect(Action action, Filter cardFilter, String system, float changeInCost, Filter changeInCostCardFilter, DeploymentRestrictionsOption deploymentRestrictionsOption, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, null, null, system, null, false, null, changeInCost, changeInCostCardFilter, deploymentRestrictionsOption, null, false, reshuffle);
    }
}
