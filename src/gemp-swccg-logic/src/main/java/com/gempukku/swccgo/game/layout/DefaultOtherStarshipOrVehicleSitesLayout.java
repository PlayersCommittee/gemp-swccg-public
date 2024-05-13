package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * Represents the default location layout for other starship/vehicle sites.
 */
public class DefaultOtherStarshipOrVehicleSitesLayout extends AbstractOtherStarshipOrVehicleSitesLayout {

    // Default layout order for other starship/vehicle sites:
    //  1) Sites (in forward or reverse order)
    //      A) Interior sites
    //      B) Docking bay

    /**
     * Needed to generate snapshot.
     */
    public DefaultOtherStarshipOrVehicleSitesLayout() {
    }

    /**
     * Creates the location layout for other starship and vehicle sites.
     * @param starshipOrVehicle the starship or vehicle
     */
    public DefaultOtherStarshipOrVehicleSitesLayout(PhysicalCard starshipOrVehicle) {
        super(starshipOrVehicle);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.docking_bay), Filters.siteOfStarshipOrVehicle(starshipOrVehicle))),
                        //  B) Docking bay
                        new LocationGroup("Docking bay", Filters.and(Filters.docking_bay, Filters.siteOfStarshipOrVehicle(starshipOrVehicle)))));
    }
}
