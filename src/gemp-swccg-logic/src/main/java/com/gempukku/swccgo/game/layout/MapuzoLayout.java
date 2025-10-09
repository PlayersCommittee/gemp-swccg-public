package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the location layout for Mapuzo.
 */
public class MapuzoLayout extends AbstractSystemLayout {

    // Layout order for Mapuzo:
    //  1) Sites (in forward or reverse order)
    //      A) Underground Corridor
    //      B) Interior sites (except Underground Corridor)
    //      C) Exterior sites
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave
    
    /**
     * Needed to generate snapshot.
     */
    public MapuzoLayout() {
    }

    /**
     * Creates the location layout for Mapuzo.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public MapuzoLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Underground Corridor
                        new LocationGroup("Underground Corridor", Filters.Underground_Corridor),
                        //  B) Interior sites (except Underground Corridor)
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site,
                                Filters.not(Filters.or(Filters.exterior_site, Filters.Underground_Corridor)), Filters.partOfSystem(systemName))),
                        //  C) Exterior sites
                        new LocationGroup("Exterior sites", Filters.and(Filters.exterior_site, Filters.not(Filters.interior_site), Filters.partOfSystem(systemName)))));
        //  2) Clouds
        //  3) Planet
        //  4) Asteroids
        //  5) Big One
        //  6) Big One: Asteroid Cave
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
