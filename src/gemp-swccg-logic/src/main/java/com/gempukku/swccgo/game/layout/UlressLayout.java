package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the default location layout for a planet.
 */
public class UlressLayout extends AbstractSystemLayout {

    // Default layout order for a planet:
    //  1) Sites (in forward or reverse order)
    //      A) Ulress Space Port 'Docking Bay'
    //      B) Exterior Sites
    //      C) Club Antonia
	//		D) Club Antonia Bar
	//		E) Club Antonia Stage
	//		F) Club Antonia Backstage
	//		G) Ulress Alleyway
	//		H) Ixtal's Garage
    //      I) Interior Sites
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public UlressLayout() {
    }

    /**
     * Creates the location layout for Ulress.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public UlressLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Ulress Space Port 'Docking Bay'
                        new LocationGroup("Ulress Space Port 'Docking Bay'", Filters.Ulress_Space_Port),
                        //  B) Exterior Sites
                        new LocationGroup("Exterior sites", Filters.and(Filters.exterior_site, Filters.not(Filters.or(Filters.interior_site,
                                Filters.Ulress_Club_Antonia, Filters.Club_Antonia_Bar, Filters.Club_Antonia_Stage, Filters.Club_Antonia_Backstage, Filters.Ixtals_Garage)), Filters.partOfSystem(systemName))),
                        //  C) Club Antonia
                        new LocationGroup("Club Antonia", Filters.Ulress_Club_Antonia),
						//  D) Club Antonia Bar
                        new LocationGroup("Club Antonia Bar", Filters.Club_Antonia_Bar),
                        //  E) Club Antonia Stage
                        new LocationGroup("Club Antonia Stage", Filters.Club_Antonia_Stage),
                        //  F) Club Antonia Backstage
                        new LocationGroup("Club Antonia Backstage", Filters.Club_Antonia_Backstage),
                        //  G) Ulress Alleyway
                        new LocationGroup("Ulress Alleyway", Filters.Alleyway),
                        //  H) Ixtal's Garage
                        new LocationGroup("Ixtal's Garage", Filters.Ixtals_Garage),
						//  I) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.exterior_site), Filters.partOfSystem(systemName)))));
        //  2) Clouds
        //  3) Planet
        //  4) Asteroids
        //  5) Big One
        //  6) Big One: Asteroid Cave
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
