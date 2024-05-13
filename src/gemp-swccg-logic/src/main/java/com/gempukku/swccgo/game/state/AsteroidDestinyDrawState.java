package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * This class contains the state information for drawing asteroid destiny.
 */
public class AsteroidDestinyDrawState {
    private PhysicalCard _starship;
    private PhysicalCard _location;

    /**
     * Creates state information for drawing asteroid destiny.
     * @param starship the starship
     * @param location the location (with "Asteroid Rules") that starship is at
     */
    public AsteroidDestinyDrawState(PhysicalCard starship, PhysicalCard location) {
        _starship = starship;
        _location = location;
    }

    /**
     * Gets the starship.
     * @return the location
     */
    public PhysicalCard getStarship() {
        return _starship;
    }

    /**
     * Gets the location.
     * @return the location
     */
    public PhysicalCard getLocation() {
        return _location;
    }

}
