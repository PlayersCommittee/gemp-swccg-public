package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the location layout for Hoth.
 */
public class HothLayout extends AbstractSystemLayout {

    // Layout order for Hoth:
    //  1) Sites (in forward or reverse order)
    //      A) Interior sites
    //      B) Interior/Exterior sites
    //      C) 1st Marker
    //      D) 2nd Marker
    //      E) 3rd Marker
    //      F) 4th Marker
    //      G) 5th Marker
    //      H) 6th Marker
    //      I) 7th Marker
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public HothLayout() {
    }

    /**
     * Creates the location layout for Hoth.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public HothLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.or(Filters.exterior_site,
                                Filters.marker_site)), Filters.partOfSystem(systemName))),
                        //  B) Interior/Exterior sites
                        new LocationGroup("Interior/Exterior sites", Filters.and(Filters.interior_site, Filters.exterior_site, Filters.partOfSystem(systemName))),
                        //  C) 1st Marker
                        new LocationGroup("1st Marker", Filters.First_Marker),
                        //  D) 2nd Marker
                        new LocationGroup("2nd Marker", Filters.Second_Marker),
                        //  E) 3rd Marker
                        new LocationGroup("3rd Marker", Filters.Third_Marker),
                        //  F) 4th Marker
                        new LocationGroup("4th Marker", Filters.Fourth_Marker),
                        //  G) 5th Marker
                        new LocationGroup("5th Marker", Filters.Fifth_Marker),
                        //  H) 6th Marker
                        new LocationGroup("6th Marker", Filters.Sixth_Marker),
                        //  I) 7th Marker
                        new LocationGroup("7th Marker", Filters.Seventh_Marker)));

        //  2) Clouds
        //  3) Planet
        //  4) Asteroids
        //  5) Big One
        //  6) Big One: Asteroid Cave
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
