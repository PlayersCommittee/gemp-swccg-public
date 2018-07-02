package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.common.Persona;

/**
 * Represents a group of locations associated with a starship or vehicle that has
 * sites associated with it based on persona. Non-unique starship or vehicle sites can also
 * deploy to this layout if the starship or vehicle card has the persona of this layout.
 */
public abstract class AbstractStarshipOrVehicleSitesLayout extends AbstractLocationLayout {

    /**
     * Needed to generate snapshot.
     */
    public AbstractStarshipOrVehicleSitesLayout() {
    }

    /**
     * Creates a layout for sites related to a starship or vehicle based on persona.
     * @param starshipOrVehicle the starship or vehicle persona
     */
    protected AbstractStarshipOrVehicleSitesLayout(Persona starshipOrVehicle) {
        _parentPersona = starshipOrVehicle;
        _orderIndex = -2; // To make starship/vehicle sites show up left of the systems and "other" starship/vehicle sites
    }
}
