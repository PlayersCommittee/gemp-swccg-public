package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the location layout for Jakku.
 */
public class JakkuLayout extends AbstractSystemLayout {

    // Layout order for Naboo:
    //  1) Sites (in forward or reverse order)
    //      A) Rey's Encampment
    //      B) Niima Outpost Shipyard
    //      C) Exterior sites
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public JakkuLayout() {
    }

    /**
     * Creates the location layout for Jakku.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public JakkuLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Rey's Encampment
                        new LocationGroup("Rey's Encampment", Filters.Reys_Encampment),
                        //  B) Niima Outpost Shipyard
                        new LocationGroup("Niima Outpost Shipyard", Filters.Niima_Outpost_Shipyard),
                        //  C) Exterior sites
                        new LocationGroup("Exterior sites", Filters.and(Filters.exterior_site, Filters.not(Filters.or(Filters.interior_site,
                                Filters.Niima_Outpost_Shipyard)), Filters.partOfSystem(systemName)))));

        //  2) Clouds
        //  3) Planet
        //  4) Asteroids
        //  5) Big One
        //  6) Big One: Asteroid Cave
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
