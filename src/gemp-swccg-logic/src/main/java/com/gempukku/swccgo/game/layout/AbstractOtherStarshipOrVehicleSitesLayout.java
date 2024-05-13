package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * Represents a group of locations associated with a starship or vehicle card that only
 * has non-unique sites related to it. If a starship or vehicle has any unique site, then
 * AbstractStarshipOrVehicleSitesLayout should be used instead, even for non-unique sites
 * related to that starship or vehicle.
 */
public abstract class AbstractOtherStarshipOrVehicleSitesLayout extends AbstractLocationLayout {

    /**
     * Needed to generate snapshot.
     */
    public AbstractOtherStarshipOrVehicleSitesLayout() {
    }

    /**
     * Creates a layout for sites related to a starship or vehicle based on specific card.
     * @param starshipOrVehicle the starship or vehicle persona
     */
    protected AbstractOtherStarshipOrVehicleSitesLayout(PhysicalCard starshipOrVehicle) {
        _parentCard = starshipOrVehicle;
        _orderIndex = -1; // To make starship/vehicle sites show up left of the systems
    }
}
