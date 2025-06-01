package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * This class contains the state information for a Force drain.
 */
public class ForceDrainState {
    private final SwccgGame _game;
    private final String _playerId;
    private int _forceRemaining;
    private int _forceTotal;
    private int _forcePaid;
    private final PhysicalCard _location;
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

    /**
     * @param total The total Force amount that will be paid as part of this drain.
     */
    public void updateTotal(float total) {
        _forceTotal = (int) total;
    }

    /**
     * @return The total amount of force that will be paid as part of this drain.
     */
    public int getForceTotal() { return _forceTotal; }

    /**
     * @param remaining The remaining unpaid Force that has yet to be paid for this drain.
     */
    public void updateRemaining(float remaining) {
        _forceRemaining = (int) remaining;
    }

    /**
     * @return How much Force remains unpaid on this Force drain.
     */
    public int getForceRemaining() { return _forceRemaining; }

    /**
     * @param paid The total amount of Force that has been paid so far for this drain.
     */
    public void updatePaid(float paid) {
        _forcePaid = (int) paid;
    }

    /**
     * @return How much Force has been paid so far as part of this Force drain.
     */
    public int getForcePaid() { return _forcePaid; }
}
