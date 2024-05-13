package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

import java.util.Collections;

/**
 * Represents the default location layout for holosites.
 */
public class DefaultHolositeLayout extends AbstractHolositeLayout {

    // Default layout order for holosites:
    // 1) Holosite

    /**
     * Creates the location layout for holosites.
     */
    public DefaultHolositeLayout() {

        //  1) Holosite
        _groupOrders.add(
                new LocationFixedGroupOrder(Collections.singletonList(new LocationGroup("Holosite", Filters.holosite))));
    }
}
