package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy a card to a specified target from Lost Pile.
 */
public class DeployCardToTargetFromLostPileEffect extends DeployCardFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified target from Lost Pile.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromLostPileEffect(Action action, Filter cardFilter, Filter targetFilter, boolean reshuffle) {
        this(action, cardFilter, targetFilter, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified target from Lost Pile.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromLostPileEffect(Action action, Filter cardFilter, Filter targetFilter, boolean forFree, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.LOST_PILE, cardFilter, targetFilter, null, null, forFree, 0, null, null, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a specific card to the specified target from Lost Pile.
     * @param action the action performing this effect
     * @param card the card
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromLostPileEffect(Action action, PhysicalCard card, Filter targetFilter, boolean forFree, boolean reshuffle) {
        super(action, Zone.LOST_PILE, card, targetFilter, forFree, 0, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified target from Lost Pile.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param asReact true if deploying as a react, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromLostPileEffect(Action action, Filter cardFilter, Filter targetFilter, boolean forFree, boolean asReact, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.LOST_PILE, cardFilter, targetFilter, null, null, forFree, 0, null, null, null, asReact, reshuffle);
    }
}
