package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the location layout for Dagobah.
 */
public class DagobahLayout extends AbstractSystemLayout {

    // Layout order for Dagobah:
    //  1) Sites (in forward or reverse order)
    //      A) Exterior sites
    //      B) Dagobah: Cave
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public DagobahLayout() {
    }

    /**
     * Creates the location layout for Dagobah.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public DagobahLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Exterior sites
                        new LocationGroup("Exterior sites", Filters.and(Filters.exterior_site, Filters.partOfSystem(systemName))),
                        //  B) Dagobah: Cave
                        new LocationGroup("Dagobah: Cave", Filters.and(Filters.Dagobah_Cave))));

        //  2) Clouds
        //  3) Planet
        //  4) Asteroids
        //  5) Big One
        //  6) Big One: Asteroid Cave
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
