package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the location layout for Bespin.
 */
public class BespinLayout extends AbstractSystemLayout {

    // Layout order for Bespin:
    //  1) Sites (in forward or reverse order)
    //      A) 2nd docking bay
    //      B) Interior sites
    //      C) 1st docking bay
    //      D) Exterior sites
    //  2) Bespin: Cloud City
    //  3) Clouds
    //  4) Planet
    //  5) Asteroids
    //  6) Big One
    //  7) Big One: Asteroid Cave

    /**
     * Needed to generate snapshot.
     */
    public BespinLayout() {
    }

    /**
     * Creates the location layout for Bespin.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public BespinLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) 2nd docking bay
                        new BespinSecondDockingBayLocationGroup(),
                        //  B) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.exterior_site), Filters.partOfSystem(systemName))),
                        //  C) 1st docking bay
                        new BespinFirstDockingBayLocationGroup(),
                        //  D) Exterior sites
                        new LocationGroup("Exterior sites", Filters.and(Filters.exterior_site, Filters.not(Filters.docking_bay), Filters.partOfSystem(systemName)))));

        List<LocationGroup> fixedGroupOrder = new ArrayList<LocationGroup>();
        //  2) Bespin: Cloud City
        fixedGroupOrder.add(new LocationGroup("Bespin: Cloud City", Filters.and(Filters.Bespin_Cloud_City)));
        //  3) Clouds
        //  4) Planet
        //  5) Asteroids
        //  6) Big One
        //  7) Big One: Asteroid Cave
        fixedGroupOrder.addAll(getPlanetSystemAndSectorsLocationGroups(systemName));
       _groupOrders.add(
               new LocationFixedGroupOrder(fixedGroupOrder));
    }
}
