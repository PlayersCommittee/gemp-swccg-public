package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * The abstract class providing the common implementation for transport vehicles.
 */
public abstract class AbstractTransportVehicle extends AbstractVehicle {

    /**
     * Creates a blueprint for a transport vehicle.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param landspeed the landspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractTransportVehicle(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, float landspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, landspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a transport vehicle.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param landspeed the landspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractTransportVehicle(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, Float landspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, landspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a transport vehicle.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param landspeed the landspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractTransportVehicle(Side side, float destiny, float deployCost, float power, float armor, Float maneuver, float landspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, landspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a transport vehicle.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param landspeed the landspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractTransportVehicle(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, float landspeed, float forfeit, String title, Uniqueness uniqueness) {
        this(side, destiny, deployCost, power, armor, maneuver, landspeed, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a transport vehicle.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param landspeed the landspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractTransportVehicle(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, Float landspeed, float forfeit, String title, Uniqueness uniqueness) {
        this(side, destiny, deployCost, power, armor, maneuver, landspeed, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a transport vehicle.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param landspeed the landspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractTransportVehicle(Side side, float destiny, float deployCost, float power, float armor, Float maneuver, float landspeed, float forfeit, String title, Uniqueness uniqueness) {
        this(side, destiny, deployCost, power, armor, maneuver, landspeed, forfeit, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a transport vehicle.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param landspeed the landspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
￼    * @param rarity the rarity
     */
    protected AbstractTransportVehicle(Side side, float destiny, float deployCost, float power, Float armor, Float maneuver, Float landspeed, float forfeit, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, deployCost, power, armor, maneuver, landspeed, forfeit, title, uniqueness, expansionSet, rarity);
        setCardSubtype(CardSubtype.TRANSPORT);
    }

    /**
     * Gets a filter for the cards that are valid to be pilots (or drivers) of the specified card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forDeployment true if checking for deployment, otherwise false
     * @return the filter
     */
    @Override
    public final Filter getValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forDeployment) {
        Filter filter =  Filters.and(Filters.character, Filters.or(Filters.pilot, Filters.not(Filters.droid), ModelType.VEHICLE, ModelType.PROTOCOL, ModelType.BATTLE),
                Filters.notProhibitedFromHavingAtTarget(self), Filters.notProhibitedFromPiloting(self), getGameTextValidPilotFilter(playerId, game, self));
        if (forDeployment) {
            filter = Filters.and(filter, Filters.notProhibitedFromHavingDeployedTo(self));
        }
        return filter;
    }

    /**
     * This method is overridden by individual cards to provide a filter for valid pilots.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the filter
     */
    protected final Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return getGameTextValidDriverFilter(playerId, game, self);
    }

    /**
     * Gets a filter for the cards that are valid to be passengers of the specified card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forDeployment true if checking for deployment, otherwise false
     * @return the filter
     */
    @Override
    public final Filter getValidPassengerFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forDeployment) {
        Filter filter =  Filters.and(Filters.notProhibitedFromHavingAtTarget(self), getGameTextValidPassengerFilter(playerId, game, self));
        if (forDeployment) {
            filter = Filters.and(filter, Filters.notProhibitedFromHavingDeployedTo(self));
        }
        return filter;
    }

    /**
     * This method is overridden by individual cards to provide a filter for valid pilots.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the filter
     */
    protected Filter getGameTextValidPassengerFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.any;
    }

    /**
     * Sets the driver capacity.
     * @param capacity the driver capacity
     */
    protected final void setDriverCapacity(int capacity) {
        _pilotCapacity = capacity;
    }

    /**
     * This method is overridden by individual cards to provide a filter for valid drivers.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the filter
     */
    protected Filter getGameTextValidDriverFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.any;
    }
}
