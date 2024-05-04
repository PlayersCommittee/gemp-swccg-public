package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose a stacked card and deploy it to a specified target
 * (or deploy a specific stacked card to a specified target).
 */
public class DeployStackedCardToTargetEffect extends DeployStackedCardEffect {

    /**
     * Creates an effect that causes the player performing the action to deploy a specific stacked card to a target
     * accepted by the target filter.
     * @param action the action performing this effect
     * @param card the card
     * @param targetFilter the target filter
     */
    public DeployStackedCardToTargetEffect(Action action, PhysicalCard card, Filter targetFilter) {
        this(action, card, targetFilter, false);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a specific stacked card to a target
     * accepted by the target filter.
     * @param action the action performing this effect
     * @param card the card
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployStackedCardToTargetEffect(Action action, PhysicalCard card, Filter targetFilter, boolean forFree) {
        super(action, card.getStackedOn(), card, targetFilter, forFree);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a stacked card accepted by the card filter
     * to a target accepted by the target filter.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardFilter the card filter
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployStackedCardToTargetEffect(Action action, PhysicalCard stackedOn, Filter cardFilter, Filter targetFilter, boolean forFree) {
        super(action, stackedOn, cardFilter, targetFilter, false, null, forFree, false);
    }
}
