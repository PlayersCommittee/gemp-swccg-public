package com.gempukku.swccgo;

import com.gempukku.swccgo.game.Player;

public class PlayerLock {
    public static Object getLock(Player player) {
        return player.getName().intern();
    }

    public static Object getLock(String playerName) {
        return playerName.intern();
    }
}
