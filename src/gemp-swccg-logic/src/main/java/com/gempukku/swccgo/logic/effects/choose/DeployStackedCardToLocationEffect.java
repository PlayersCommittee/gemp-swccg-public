package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose a stacked card and deploy it to a specified location
 * (or deploy a specific stacked card to a specified location).
 */
public class DeployStackedCardToLocationEffect extends DeployStackedCardEffect {

    /**
     * Creates an effect that causes the player performing the action to deploy a specific stacked card to a location
     * accepted by the location filter.
     * @param action the action performing this effect
     * @param card the card
     * @param locationFilter the location filter
     */
    public DeployStackedCardToLocationEffect(Action action, PhysicalCard card, Filter locationFilter) {
        this(action, card, locationFilter, false);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a specific stacked card to a location
     * accepted by the location filter.
     * @param action the action performing this effect
     * @param card the card
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployStackedCardToLocationEffect(Action action, PhysicalCard card, Filter locationFilter, boolean forFree) {
        super(action, card.getStackedOn(), card, Filters.locationAndCardsAtLocation(locationFilter), forFree);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a stacked card accepted by the card filter
     * to a location accepted by the location filter.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     */
    public DeployStackedCardToLocationEffect(Action action, PhysicalCard stackedOn, Filter cardFilter, Filter locationFilter) {
        super(action, stackedOn, cardFilter, Filters.locationAndCardsAtLocation(locationFilter), false, null, false, false);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a stacked card accepted by the card filter
     * to a location accepted by the location filter.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployStackedCardToLocationEffect(Action action, PhysicalCard stackedOn, Filter cardFilter, Filter locationFilter, boolean forFree) {
        super(action, stackedOn, cardFilter, Filters.locationAndCardsAtLocation(locationFilter), false, null, forFree, false);
    }
}
