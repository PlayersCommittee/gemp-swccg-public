package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;

/**
 * Represents a location group for planet systems.
 */
public class PlanetSystemLocationGroup extends LocationGroup {

    /**
     * Needed to generate snapshot.
     */
    public PlanetSystemLocationGroup() {
    }

    /**
     * Creates a location group for a planet system.
     */
    public PlanetSystemLocationGroup(String systemName) {
        super("Planet system", Filters.and(Filters.planet_system, Filters.title(systemName)));
    }
}
