package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the location layout for Death Star II.
 */
public class DeathStarIILayout extends AbstractSystemLayout {

    // Layout order for Death Star II:
    //  1) Sites (in forward or reverse order)
    //      A) Throne Room
    //      B) Docking bay
    //  2) Death Star II
    //  3) Coolant Shaft
    //  4) Capacitors
    //  5) Reactor Core

    /**
     * Needed to generate snapshot.
     */
    public DeathStarIILayout() {
    }

    /**
     * Creates the location layout for Death Star II.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public DeathStarIILayout(String systemName, int parsec) {
        super(systemName, parsec);

        //  1) Sites (in forward or reverse order)
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        //  A) Throne Room
                        new LocationGroup("Throne Room", Filters.Throne_Room),
                        //  B) Chasm Walkway
                        new LocationGroup("Chasm Walkway", Filters.title(Title.Death_Star_II_Chasm_Walkway)),
                        //  C) Docking bay
                        new LocationGroup("Docking bay", Filters.and(Filters.docking_bay, Filters.partOfSystem(systemName)))));

        List<LocationGroup> fixedGroupOrder = new ArrayList<LocationGroup>();
        //  2) Death Star II
        fixedGroupOrder.add(new LocationGroup("Death Star II", Filters.Death_Star_II_system));
        //  3) Coolant Shaft
        fixedGroupOrder.add(new LocationGroup("Coolant Shaft", Filters.Coolant_Shaft));
        //  4) Capacitors
        fixedGroupOrder.add(new LocationGroup("Capacitors", Filters.Capacitors));
        //  5) Reactor Core
        fixedGroupOrder.add(new LocationGroup("Reactor Core", Filters.Reactor_Core));
        _groupOrders.add(
                new LocationFixedGroupOrder(fixedGroupOrder));
    }
}
