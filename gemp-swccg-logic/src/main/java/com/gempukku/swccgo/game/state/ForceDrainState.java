package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * This class contains the state information for a Force drain.
 */
public class ForceDrainState {
    private SwccgGame _game;
    private String _playerId;
    private PhysicalCard _location;
    private boolean _canceled;

    /**
     * Creates state information for a Force drain.
     * @param game the game
     * @param playerId the player performing the Force drain
     * @param location the Force drain location
     */
    public ForceDrainState(SwccgGame game, String playerId, PhysicalCard location) {
        _game = game;
        _playerId = playerId;
        _location = location;
    }

    /**
     * Gets the player performing the Force drain.
     * @return the player
     */
    public String getPlayerId() {
        return _playerId;
    }

    /**
     * Gets the Force drain location
     * @return the location
     */
    public PhysicalCard getLocation() {
        return _location;
    }

    /**
     * Determines if the Force drain can continue.
     * @return true if Force drain can continue, otherwise false
     */
    public boolean canContinue() {
        if (_canceled)
            return false;

        return Filters.controlsForForceDrain(_playerId).accepts(_game.getGameState(), _game.getModifiersQuerying(), _location);
    }

    /**
     * Sets Force drain as canceled.
     */
    public void cancel() {
        _canceled = true;
    }
}
