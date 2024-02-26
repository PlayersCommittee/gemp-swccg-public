package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy a card from Reserve Deck.
 */
public class DeployCardFromReserveDeckEffect extends DeployCardFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromReserveDeckEffect(Action action, Filter cardFilter, boolean reshuffle) {
        this(action, cardFilter, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromReserveDeckEffect(Action action, Filter cardFilter, Filter specialLocationConditions, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, null, null, specialLocationConditions, false, 0, null, null, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromReserveDeckEffect(Action action, Filter cardFilter, boolean forFree, boolean reshuffle) {
        super(action, Zone.RESERVE_DECK, cardFilter, null, null, forFree, 0, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromReserveDeckEffect(Action action, Filter cardFilter, DeploymentRestrictionsOption deploymentRestrictionsOption, boolean forFree, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, null, null, null, forFree, 0, null, deploymentRestrictionsOption, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param forFree true if deploying for free, otherwise false
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromReserveDeckEffect(Action action, Filter cardFilter, boolean forFree, boolean asReact, boolean reshuffle) {
        super(action, Zone.RESERVE_DECK, cardFilter, null,  null, forFree, 0, null, asReact, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param forFreeCardFilter the card filter for cards that deploy for free
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromReserveDeckEffect(Action action, Filter cardFilter, Filter forFreeCardFilter, boolean asReact, boolean reshuffle) {
        super(action, Zone.RESERVE_DECK, cardFilter, null, null, false, 0, forFreeCardFilter, asReact, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromReserveDeckEffect(Action action, Filter cardFilter, float changeInCost, boolean reshuffle) {
        super(action, Zone.RESERVE_DECK, cardFilter, null, null, false, changeInCost, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromReserveDeckEffect(Action action, Filter cardFilter, float changeInCost, boolean asReact, boolean reshuffle) {
        super(action, Zone.RESERVE_DECK, cardFilter, null, null, false, changeInCost, null, asReact, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromReserveDeckEffect(Action action, Filter cardFilter, float changeInCost, Filter changeInCostCardFilter, boolean reshuffle) {
        super(action, Zone.RESERVE_DECK, cardFilter, null, null, false, changeInCost, changeInCostCardFilter, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the location accepted by the target filter from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the location to deploy to
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reshuffle true if pile is reshuffled, otherwise false
     */

    public DeployCardFromReserveDeckEffect(Action action, Filter cardFilter, Filter targetFilter, float changeInCost, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, targetFilter, null, null, false, changeInCost, null, null, null, false, reshuffle);
    }
}
