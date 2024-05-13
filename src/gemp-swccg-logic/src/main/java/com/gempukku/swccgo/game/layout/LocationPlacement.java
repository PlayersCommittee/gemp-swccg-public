package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.common.LocationPlacementDirection;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * Represents a placement of a location on the table.
 */
public class LocationPlacement {

    private String _parentSystem;
    private Persona _parentPersona;
    private PhysicalCard _parentCard;
    private PhysicalCard _otherCard;
    private LocationPlacementDirection _direction;

    /**
     * Creates a placement of a location on the table.
     * @param parentSystem the name of the system the location will be in the group of, or null
     * @param parentPersona the starship or vehicle persona that the site will be related to, or null
     * @param parentCard the starship or vehicle card that the non-unique starship or vehicle site will be related to, or null
     * @param otherCard the other card in the location layout to place this card relative to, or null
     * @param placement the relative placement (to the otherCard) to the place the location
     */
    public LocationPlacement(String parentSystem, Persona parentPersona, PhysicalCard parentCard, PhysicalCard otherCard, LocationPlacementDirection placement) {
        _parentSystem = parentSystem;
        _parentPersona = parentPersona;
        _parentCard = parentCard;
        _otherCard = otherCard;
        _direction = placement;
    }

    /**
     * Gets the parent system.
     * @return the parent system, or null
     */
    public String getParentSystem() {
        return _parentSystem;
    }

    /**
     * Gets the parent starship or vehicle persona.
     * @return the parent starship or vehicle persona, or null
     */
    public Persona getParentStarshipOrVehiclePersona() {
        return _parentPersona;
    }

    /**
     * Gets the parent starship or vehicle card.
     * @return the parent starship or vehicle card, or null
     */
    public PhysicalCard getParentStarshipOrVehicleCard() {
        return _parentCard;
    }

    /**
     * Gets the location to deploy next to.
     * @return the location to deploy next to
     */
    public PhysicalCard getOtherCard() {
        return _otherCard;
    }

    /**
     * Sets the location to deploy next to.
     * @param otherCard the location to deploy next to
     */
    public void setOtherCard(PhysicalCard otherCard) {
        _otherCard = otherCard;
    }

    /**
     * Gets the valid directions to deploy the card relative to the other card, or to convert the other card.
     * @return the valid directions to deploy the card relative to the other card, or to convert the other card
     */
    public LocationPlacementDirection getDirection() {
        return _direction;
    }


    /**
     * Sets the valid directions to deploy the card relative to the other card, or to convert the other card.
     * @param direction the valid directions
     */
    public void setDirection(LocationPlacementDirection direction) {
        _direction = direction;
    }
}
