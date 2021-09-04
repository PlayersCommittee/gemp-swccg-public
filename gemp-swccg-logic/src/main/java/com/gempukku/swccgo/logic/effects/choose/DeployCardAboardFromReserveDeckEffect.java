package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to search Reserve Deck for a card and deploy it aboard a specified
 * starship/vehicle (or deploy a specific card from Reserve Deck aboard a specified starship/vehicle).
 */
public class DeployCardAboardFromReserveDeckEffect extends DeployCardToTargetFromReserveDeckEffect {

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it aboard
     * any starship or vehicle.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardAboardFromReserveDeckEffect(Action action, Filter cardFilter, boolean reshuffle) {
        this(action, cardFilter, Filters.or(Filters.starship, Filters.vehicle, Filters.locationAndCardsAtLocation(Filters.or(Filters.starship_site, Filters.vehicle_site))), false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it aboard
     * a starship/vehicle accepted by starshipOrVehicleFilter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param starshipOrVehicleFilter the starship/vehicle filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardAboardFromReserveDeckEffect(Action action, Filter cardFilter, Filter starshipOrVehicleFilter, boolean reshuffle) {
        this(action, cardFilter, starshipOrVehicleFilter, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it aboard
     * a starship/vehicle accepted by starshipOrVehicleFilter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param starshipOrVehicleFilter the starship/vehicle filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardAboardFromReserveDeckEffect(Action action, Filter cardFilter, Filter starshipOrVehicleFilter, boolean forFree, boolean reshuffle) {
        super(action, cardFilter, Filters.or(starshipOrVehicleFilter, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehicleFilter))), forFree, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to search Reserve Deck for a card and deploy it aboard
     * a starship/vehicle accepted by starshipOrVehicleFilter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param starshipOrVehicleFilter the starship/vehicle filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardAboardFromReserveDeckEffect(Action action, Filter cardFilter, Filter starshipOrVehicleFilter, float changeInCost, boolean reshuffle) {
        super(action, cardFilter, Filters.or(starshipOrVehicleFilter, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehicleFilter))), changeInCost, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a specific card from Reserve Deck aboard
     * a starship/vehicle accepted by starshipOrVehicleFilter.
     * @param action the action performing this effect
     * @param card the card
     * @param starshipOrVehicleFilter the starship/vehicle filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardAboardFromReserveDeckEffect(Action action, PhysicalCard card, Filter starshipOrVehicleFilter, boolean forFree, boolean reshuffle) {
        super(action, card, Filters.or(starshipOrVehicleFilter, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehicleFilter))), forFree, false, reshuffle);
    }
}
