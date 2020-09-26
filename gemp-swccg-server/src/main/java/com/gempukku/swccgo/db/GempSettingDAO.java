package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * An interface to represent the player database access object.
 */
public interface GempSettingDAO {

    /**
     * Gets whether the privateGamesEnabled setting is on or off
     * @return true if on, false if off
     */
    boolean privateGamesEnabled();

    /**
     * toggles the privateGamesEnabled setting
     */
    void togglePrivateGamesEnabled();
}
