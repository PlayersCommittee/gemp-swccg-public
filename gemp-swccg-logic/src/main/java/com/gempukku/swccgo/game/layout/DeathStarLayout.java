package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the location layout for Death Star.
 */
public class DeathStarLayout extends AbstractSystemLayout {

    // Layout order for Death Star:
    //  1) Sites (in forward or reverse order)
    //      A) Interior sites
    //      B) Docking bay
    //  2) Death Star
    //  3) Trench

    /**
     * Needed to generate snapshot.
     */
    public DeathStarLayout() {
    }

    /**
     * Creates the location layout for Death Star.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public DeathStarLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.exterior_site), Filters.partOfSystem(systemName))),
                        //  B) Docking bay
                        new LocationGroup("Docking bay", Filters.and(Filters.docking_bay, Filters.partOfSystem(systemName)))));

        List<LocationGroup> fixedGroupOrder = new ArrayList<LocationGroup>();
        //  2) Death Star
        fixedGroupOrder.add(new LocationGroup("Death Star", Filters.Death_Star_system));
        //  3) Trench
        fixedGroupOrder.add(new TrenchLocationGroup());
        _groupOrders.add(
                new LocationFixedGroupOrder(fixedGroupOrder));
    }
}
