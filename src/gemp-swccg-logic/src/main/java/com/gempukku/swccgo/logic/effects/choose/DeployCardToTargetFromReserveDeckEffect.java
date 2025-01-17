package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to search Reserve Deck for a card and deploy it to a specified
 * target (or deploy a specific card from Reserve Deck to a specified target).
 */
public class DeployCardToTargetFromReserveDeckEffect extends DeployCardFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a target accepted by the target filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromReserveDeckEffect(Action action, Filter cardFilter, Filter targetFilter, boolean reshuffle) {
        this(action, cardFilter, targetFilter, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a target accepted by the target filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromReserveDeckEffect(Action action, Filter cardFilter, Filter targetFilter, boolean forFree, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, targetFilter, null, null, forFree, 0, null, null, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a target accepted by the target filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromReserveDeckEffect(Action action, Filter cardFilter, Filter targetFilter, boolean forFree, DeploymentRestrictionsOption deploymentRestrictionsOption, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, targetFilter, null, null, forFree, 0, null, deploymentRestrictionsOption, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a target accepted by the target filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromReserveDeckEffect(Action action, Filter cardFilter, Filter targetFilter, boolean forFree, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, targetFilter, null, null, forFree, 0, null, deploymentRestrictionsOption, deployAsCaptiveOption, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a target accepted by the target filter.
     * @param action the action performing this effect
     * @param performingPlayerId the player to deploy cards
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromReserveDeckEffect(Action action, String performingPlayerId, Filter cardFilter, Filter targetFilter, boolean forFree, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, boolean reshuffle) {
        super(action, performingPlayerId, Zone.RESERVE_DECK, cardFilter, targetFilter, null, null, forFree, 0, null, deploymentRestrictionsOption, deployAsCaptiveOption, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a target accepted by the target filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param ignoreTargetFilterCardFilter the card filter for cards that ignore the target filter
     * @param forFreeCardFilter the card filter for cards that deploy for free, or null
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromReserveDeckEffect(Action action, Filter cardFilter, Filter targetFilter, Filter ignoreTargetFilterCardFilter, Filter forFreeCardFilter, boolean asReact, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, targetFilter, ignoreTargetFilterCardFilter, null, false, 0, forFreeCardFilter, null, null, asReact, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a target accepted by the target filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromReserveDeckEffect(Action action, Filter cardFilter, Filter targetFilter, boolean forFree, boolean asReact, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, targetFilter, null, null, forFree, 0, null, null, null, asReact, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it to
     * a target accepted by the target filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromReserveDeckEffect(Action action, Filter cardFilter, Filter targetFilter, float changeInCost, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, targetFilter, null, null, false, changeInCost, null, null, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a specific card from Reserve Deck to a
     * target accepted by the target filter.
     * @param action the action performing this effect
     * @param card the card
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromReserveDeckEffect(Action action, PhysicalCard card, Filter targetFilter, boolean forFree, float changeInCost, boolean reshuffle) {
        super(action, Zone.RESERVE_DECK, card, targetFilter, forFree, changeInCost, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a specific card from Reserve Deck to a
     * target accepted by the target filter.
     * @param action the action performing this effect
     * @param card the card
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromReserveDeckEffect(Action action, PhysicalCard card, Filter targetFilter, boolean forFree, boolean asReact, boolean reshuffle) {
        super(action, Zone.RESERVE_DECK, card, targetFilter, forFree, 0, asReact, reshuffle);
    }
}
