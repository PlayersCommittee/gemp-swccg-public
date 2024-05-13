package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

import java.util.Collections;

/**
 * Represents the default location layout for a space system.
 */
public class DefaultSpaceLayout extends AbstractSystemLayout {

    // Default layout order for a space system:
    // 1) Space

    /**
     * Needed to generate snapshot.
     */
    public DefaultSpaceLayout() {
    }

    /**
     * Creates the default location layout for a space system.
     * @param systemName the system name
     * @param parsec the parsec number for the system
     */
    public DefaultSpaceLayout(String systemName, int parsec) {
        super(systemName, parsec);

        // 1) Space
        _groupOrders.add(
                new LocationFixedGroupOrder(Collections.singletonList(new LocationGroup("Space", Filters.and(Filters.space_system, Filters.title(systemName))))));

    }
}
