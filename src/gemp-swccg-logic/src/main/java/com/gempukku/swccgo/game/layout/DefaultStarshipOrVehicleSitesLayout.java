package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the default location layout for starship/vehicle sites.
 */
public class DefaultStarshipOrVehicleSitesLayout extends AbstractStarshipOrVehicleSitesLayout {

    // Default layout order for starship/vehicle sites:
    //  1) Sites (in forward or reverse order)
    //      A) Interior sites
    //      B) Docking/launch bay

    /**
     * Needed to generate snapshot.
     */
    public DefaultStarshipOrVehicleSitesLayout() {
    }

    /**
     * Creates the location layout for starship and vehicle sites.
     * @param starshipOrVehicle the persona of the starship or vehicle
     */
    public DefaultStarshipOrVehicleSitesLayout(Persona starshipOrVehicle) {
        super(starshipOrVehicle);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.exterior_site), Filters.siteOfStarshipOrVehicle(starshipOrVehicle, false))),
                        //  B) Docking/launch bay
                        new LocationGroup("Docking/launch bay", Filters.and(Filters.exterior_site, Filters.siteOfStarshipOrVehicle(starshipOrVehicle, false)))));
    }
}
