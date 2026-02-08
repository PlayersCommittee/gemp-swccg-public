package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to search Force Pile for a card and deploy it to a specified
 * target (or deploy a specific card from Force Pile to a specified target).
 */
public class DeployCardToTargetFromForcePileEffect extends DeployCardFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to search Force Pile for a card and deploy it to
     * a target accepted by the target filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromForcePileEffect(Action action, Filter cardFilter, Filter targetFilter, boolean reshuffle) {
        this(action, cardFilter, targetFilter, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Force Pile for a card and deploy it to
     * a target accepted by the target filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToTargetFromForcePileEffect(Action action, Filter cardFilter, Filter targetFilter, boolean forFree, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.FORCE_PILE, cardFilter, targetFilter, null, null, forFree, 0, null, null, null, false, reshuffle);
    }
}
