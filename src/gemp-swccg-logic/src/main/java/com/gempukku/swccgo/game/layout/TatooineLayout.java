package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the location layout for Tatooine.
 */
public class TatooineLayout extends AbstractSystemLayout {

    // Layout order for Tatooine:
    //  1) Sites (in forward or reverse order)
    //      A) Cantina
    //      B) Docking bay 94
    //      C) Mos Eisley
    //      D) Exterior sites
    //      E) Jabba's Palace
    //      F) Entrance Cavern
    //      G) Interior sites
    //      H) Rancor Pit
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public TatooineLayout() {
    }

    /**
     * Creates the location layout for Tatooine.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public TatooineLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Cantina
                        new LocationGroup("Cantina", Filters.Cantina),
                        //  B) Docking bay 94
                        new LocationGroup("Docking bay 94", Filters.Docking_Bay_94),
                        //  C) Mos Eisley
                        new LocationGroup("Mos Eisley", Filters.Mos_Eisley),
                        //  D) Exterior sites
                        new LocationGroup("Exterior sites", Filters.and(Filters.exterior_site, Filters.not(Filters.or(Filters.interior_site,
                                Filters.Mos_Eisley, Filters.Jabbas_Palace_site)), Filters.partOfSystem(systemName))),
                        //  E) Jabba's Palace
                        new LocationGroup("Jabba's Palace", Filters.Jabbas_Palace),
                        //  F) Entrance Cavern
                        new LocationGroup("Entrance Cavern", Filters.Entrance_Cavern),
                        //  G) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.Jabbas_Palace_site,
                                Filters.not(Filters.or(Filters.exterior_site, Filters.Rancor_Pit)))),
                        //  H) Rancor Pit
                        new LocationGroup("Rancor Pit", Filters.Rancor_Pit)));

        //  2) Clouds
        //  3) Planet
        //  4) Asteroids
        //  5) Big One
        //  6) Big One: Asteroid Cave
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
