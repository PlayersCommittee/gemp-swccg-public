package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the location layout for SARLAC.
 */
public class SARLACLayout extends AbstractSystemLayout {

    // Layout order for SARLAC:
    //  1) Sites (in forward or reverse order)
    //      A) Interior sites
    //      B) Docking bay
    //  2) SARLAC

    /**
     * Needed to generate snapshot.
     */
    public SARLACLayout() {
    }

    /**
     * Creates the location layout for SARLAC.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public SARLACLayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Interior sites
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.exterior_site), Filters.partOfSystem(systemName))),
                        //  B) Docking bay
                        new LocationGroup("Docking bay", Filters.and(Filters.docking_bay, Filters.partOfSystem(systemName)))));

        List<LocationGroup> fixedGroupOrder = new ArrayList<LocationGroup>();
        //  2) SARLAC
        fixedGroupOrder.add(new LocationGroup("Death Star II", Filters.SARLAC_system));
        _groupOrders.add(
                new LocationFixedGroupOrder(fixedGroupOrder));
    }
}
