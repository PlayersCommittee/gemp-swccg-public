package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy a card to a specified location from Lost Pile.
 */
public class DeployCardToLocationFromLostPileEffect extends DeployCardToTargetFromLostPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified location from Lost Pile.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromLostPileEffect(Action action, Filter cardFilter, Filter locationFilter, boolean reshuffle) {
        this(action, cardFilter, locationFilter, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * to the specified location from Lost Pile.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromLostPileEffect(Action action, Filter cardFilter, Filter locationFilter, boolean forFree, boolean reshuffle) {
        super(action, cardFilter, Filters.locationAndCardsAtLocation(locationFilter), forFree, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a specific card to the specified location from Lost Pile.
     * @param action the action performing this effect
     * @param card the card
     * @param locationFilter the location filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardToLocationFromLostPileEffect(Action action, PhysicalCard card, Filter locationFilter, boolean forFree, boolean reshuffle) {
        super(action, card, Filters.locationAndCardsAtLocation(locationFilter), forFree, reshuffle);
    }
}
