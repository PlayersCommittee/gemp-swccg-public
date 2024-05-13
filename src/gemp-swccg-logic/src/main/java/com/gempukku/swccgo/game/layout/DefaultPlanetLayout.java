package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the default location layout for a planet.
 */
public class DefaultPlanetLayout extends AbstractSystemLayout {

    // Default layout order for a planet:
    //  1) Sites (in forward or reverse order)
    //      A) Interior sites
    //      B) Interior/Exterior sites
    //      C) Exterior sites
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public DefaultPlanetLayout() {
    }

    /**
     * Creates the default location layout for a planet.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public DefaultPlanetLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.exterior_site), Filters.partOfSystem(systemName))),
                        //  B) Interior/Exterior sites
                        new LocationGroup("Interior/Exterior sites", Filters.and(Filters.interior_site, Filters.exterior_site, Filters.partOfSystem(systemName))),
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
