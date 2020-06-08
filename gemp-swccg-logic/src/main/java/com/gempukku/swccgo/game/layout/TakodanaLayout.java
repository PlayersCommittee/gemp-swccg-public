package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the location layout for Takodana.
 */
public class TakodanaLayout extends AbstractSystemLayout {

    // Layout order for Takodana:
    // Double check on slack prior to commit
    //  1) Sites (in forward or reverse order)
    //      A) Exterior Sites (Example: generic Swamp)
    //      B) Takodana: Maz's Castle
    //      C) Interior Maz's Castle sites in any order (As of v-set 11, only Antechamber and Hidden Recess)
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public TakodanaLayout() {
    }

    /**
     * Creates the location layout for Tatooine.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public TakodanaLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        // A) Exterior sites:
                        new LocationGroup("Exterior Sites", Filters.and(Filters.exterior_site, Filters.not(Filters.or(Filters.interior_site, Filters.Mazs_Castle_Location)))),
                        new LocationGroup("Maz's Castle", Filters.Mazs_Castle),
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.Mazs_Castle_Location))));
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
