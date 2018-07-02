package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * The abstract class providing the common implementation for combat vehicles.
 */
public abstract class AbstractCombatVehicle extends AbstractVehicle {

    /**
     * Creates a blueprint for a combat vehicle.
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
    protected AbstractCombatVehicle(Side side, float destiny, float deployCost, float power, float armor, Float maneuver, float landspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, landspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a combat vehicle.
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
    protected AbstractCombatVehicle(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, float landspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, landspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a creature vehicle.
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
    protected AbstractCombatVehicle(Side side, float destiny, float deployCost, float power, Float armor, float maneuver, float landspeed, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, armor, maneuver, landspeed, forfeit, title, uniqueness);
        setCardSubtype(CardSubtype.COMBAT);
    }

    /**
     * Creates a blueprint for a creature vehicle.
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
    protected AbstractCombatVehicle(Side side, float destiny, float deployCost, float power, float armor, Float maneuver, float landspeed, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, deployCost, power, armor, maneuver, landspeed, forfeit, title, uniqueness);
        setCardSubtype(CardSubtype.COMBAT);
    }

    /**
     * Sets the pilot capacity.
     * @param capacity the pilot capacity
     */
    protected final void setPilotCapacity(int capacity) {
        _pilotCapacity = capacity;
    }

    /**
     * Sets the pilot or passenger capacity.
     * @param capacity the pilot or passenger capacity
     */
    protected final void setPilotOrPassengerCapacity(int capacity) {
        _pilotOrPassengerCapacity = capacity;
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
        return super.getValidPilotFilter(playerId, game, self, forDeployment);
    }
}
