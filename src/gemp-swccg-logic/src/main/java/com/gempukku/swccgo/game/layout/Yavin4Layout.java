package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the location layout for Yavin 4.
 */
public class Yavin4Layout extends AbstractSystemLayout {

    // Layout order for Yavin 4:
    //  1) Sites (in forward or reverse order)
    //      A) Interior sites
    //      B) Interior/Exterior sites
    //      C) Massassi Headquarters
    //      D) Exterior sites
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public Yavin4Layout() {
    }

    /**
     * Creates the location layout for Yavin 4.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public Yavin4Layout(final String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.exterior_site), Filters.partOfSystem(systemName))),
                        //  B) Interior/Exterior sites
                        new LocationGroup("Interior/Exterior sites", Filters.and(Filters.interior_site, Filters.exterior_site, Filters.partOfSystem(systemName))),
                        //  C) Massassi Headquarters
                        new LocationGroup("Massassi Headquarters", Filters.Massassi_Headquarters),
                        //  D) Exterior sites
                        new LocationGroup("Exterior sites", Filters.and(Filters.exterior_site, Filters.not(Filters.or(Filters.interior_site, Filters.Massassi_Headquarters)),
                                Filters.partOfSystem(systemName)))));

        //  2) Clouds
        //  3) Planet
        //  4) Asteroids
        //  5) Big One
        //  6) Big One: Asteroid Cave
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
