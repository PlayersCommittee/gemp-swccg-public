package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the location layout for Coruscant.
 */
public class CoruscantLayout extends AbstractSystemLayout {

    // Layout order for Coruscant:
    //  1) Sites (in forward or reverse order)
    //      A) Interior sites
    //      B) Exterior sites
    //      C) Xizor's Palace
    //      D) Uplink Station
    //      E) Sewer
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public CoruscantLayout() {
    }

    /**
     * Creates the location layout for Coruscant.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public CoruscantLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.or(Filters.exterior_site,
                                Filters.Xizors_Palace_site, Filters.500_Republica)), Filters.partOfSystem(systemName))),
                        //  B) 500 Republica
                        new LocationGroup("500 Republica", Filters._500_Republica),
                        //  C) Private Platform
                        new LocationGroup("Private Platform", Filters.Private_Platform),
                        //  D) Exterior sites
                        new LocationGroup("Exterior sites", Filters.and(Filters.exterior_site, Filters.not(Filters.or(Filters.interior_site,
                                Filters.Xizors_Palace_site, Filters.Private_Platform)), Filters.partOfSystem(systemName))),
                        //  E) Xizor's Palace
                        new LocationGroup("Xizor's Palace", Filters.Xizors_Palace),
                        //  F) Uplink Station
                        new LocationGroup("Uplink Station", Filters.Uplink_Station),
                        //  G) Sewer
                        new LocationGroup("Sewer", Filters.Sewer)));

        //  2) Clouds
        //  3) Planet
        //  4) Asteroids
        //  5) Big One
        //  6) Big One: Asteroid Cave
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
