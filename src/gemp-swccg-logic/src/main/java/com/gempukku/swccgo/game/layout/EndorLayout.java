package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the location layout for Endor.
 */
public class EndorLayout extends AbstractSystemLayout {

    // Layout order for Endor:
    //  1) Sites (in forward or reverse order)
    //      A) Bunker
    //      B) Landing Platform
    //      C) Exterior sites
    //      D) Ewok Village
    //      E) Chief Chirpa's Hut
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public EndorLayout() {
    }

    /**
     * Creates the location layout for Endor.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public EndorLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Bunker
                        new LocationGroup("Bunker", Filters.Bunker),
                        //  B) Landing Platform
                        new LocationGroup("Landing Platform", Filters.Landing_Platform),
                        //  C) Exterior sites
                        new LocationGroup("Exterior sites", Filters.and(Filters.exterior_site, Filters.not(Filters.or(Filters.Landing_Platform,
                                Filters.Ewok_Village)), Filters.partOfSystem(systemName))),
                        //  D) Ewok Village
                        new LocationGroup("Ewok Village", Filters.Ewok_Village),
                        //  E) Chief Chirpa's Hut
                        new LocationGroup("Chief Chirpa's Hut", Filters.Chief_Chirpas_Hut)));

        //  2) Clouds
        //  3) Planet
        //  4) Asteroids
        //  5) Big One
        //  6) Big One: Asteroid Cave
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
