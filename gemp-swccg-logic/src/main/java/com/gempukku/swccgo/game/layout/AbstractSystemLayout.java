package com.gempukku.swccgo.game.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of locations associated with a system.
 */
public abstract class AbstractSystemLayout extends AbstractLocationLayout {

    /**
     * Needed to generate snapshot.
     */
    public AbstractSystemLayout() {
    }

    /**
     * Creates a layout for a system.
     * @param systemName the system name
     * @param parsec the parsec
     */
    protected AbstractSystemLayout(String systemName, int parsec) {
        _parentTitle = systemName;
        _orderIndex = parsec;
    }

    /**
     * Gets list of location groups representing clouds, the planet system, asteroids, Big One, and Big One: Asteroid Cave
     * for the specified system.
     * @param systemName the system name
     */
    List<LocationGroup> getPlanetSystemAndSectorsLocationGroups(String systemName) {
        List<LocationGroup> locationGroups = new ArrayList<LocationGroup>();
        locationGroups.add(new CloudsLocationGroup(systemName));
        locationGroups.add(new PlanetSystemLocationGroup(systemName));
        locationGroups.add(new AsteroidsLocationGroup(systemName));
        locationGroups.add(new BigOneLocationGroup(systemName));
        locationGroups.add(new AsteroidCaveLocationGroup(systemName));
        return locationGroups;
    }
}
