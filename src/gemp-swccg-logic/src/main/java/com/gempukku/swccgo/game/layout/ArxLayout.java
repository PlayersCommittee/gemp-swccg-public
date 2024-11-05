package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents the default location layout for a planet.
 */
public class ArxLayout extends AbstractSystemLayout {

    // Default layout order for a planet:
    //  1) Sites (in forward or reverse order)
    //      A) Desert Space Port 'Docking Bay'
    //      B) The Iron Garage
    //      C) The Iron Legion
	//		D) Dark Ascent
	//		E) Eos City
	//		F) Eos City Space Port 'Docking Bay'
	//		G) Interior Sites
	//		H) Exterior Sites
    //  2) Clouds
    //  3) Planet
    //  4) Asteroids
    //  5) Big One
    //  6) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public ArxLayout() {
    }

    /**
     * Creates the location layout for Arx.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public ArxLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Desert Space Port 'Docking Bay'
                        new LocationGroup("Desert Space Port 'Docking Bay'", Filters.Desert_Space_Port_Docking_Bay),
                        //  B) The Iron Garage
                        new LocationGroup("The Iron Garage", Filters.Iron_Garage),
                        //  C) The Iron Legion
                        new LocationGroup("The Iron Legion", Filters.The_Iron_Legion),
						//  D) Dark Ascent
                        new LocationGroup("Dark Ascent", Filters.Dark_Ascent),
                        //  E) Eos City
                        new LocationGroup("Eos City", Filters.Eos_City),
                        //  F) Eos City Space Port 'Docking Bay'
                        new LocationGroup("Eos City Space Port 'Docking Bay'", Filters.Eos_City_Space_Port_Docking_Bay),
						//  G) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.exterior_site), Filters.partOfSystem(systemName))),
						//  H) Exterior Sites						
						new LocationGroup("Exterior sites", Filters.and(Filters.exterior_site, Filters.not(Filters.or(Filters.interior_site,
                                Filters.Desert_Space_Port_Docking_Bay, Filters.Iron_Garage, Filters.The_Iron_Legion, Filters.Dark_Ascent, Filters.Eos_City)), Filters.partOfSystem(systemName)))));
        //  2) Clouds
        //  3) Planet
        //  4) Asteroids
        //  5) Big One
        //  6) Big One: Asteroid Cave
        _groupOrders.add(
                new LocationFixedGroupOrder(getPlanetSystemAndSectorsLocationGroups(systemName)));
    }
}
