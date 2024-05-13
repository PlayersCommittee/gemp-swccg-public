package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.filters.Filters;

public class InvisibleHandLayout extends AbstractStarshipOrVehicleSitesLayout {
    public InvisibleHandLayout(){
    }

    public InvisibleHandLayout(Persona starshipOrVehicle) {
        super(starshipOrVehicle);

        //  1) Sites (in forward or reverse order)
        _groupOrders.clear();
        _groupOrders.add(
                new LocationReversibleGroupOrder(
                        new LocationGroup("Docking Bay", Filters.Invisible_Hand_Docking_Bay),
                        new LocationGroup("Interior sites", Filters.and(Filters.interior_site, Filters.not(Filters.or(Filters.exterior_site, Filters.Invisible_Hand_Bridge)), Filters.siteOfStarshipOrVehicle(starshipOrVehicle, false))),
                        new LocationGroup("Bridge", Filters.Invisible_Hand_Bridge)));
    }
}