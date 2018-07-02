package com.gempukku.swccgo.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class to represent a player order.
 */
public class PlayerOrder {
    private List<String> _turnOrder;

    /**
     * Creates a player order with the specified order.
     * @param turnOrder the order
     */
    public PlayerOrder(List<String> turnOrder) {
        _turnOrder = turnOrder;
    }

    /**
     * Gets the players in order.
     * @return the players
     */
    public List<String> getAllPlayers() {
        return Collections.unmodifiableList(_turnOrder);
    }

    /**
     * Gets a play order starting with the specified player.
     * @param startingPlayerId the first player in the order
     * @param looped true if the play order repeats, otherwise false
     * @return the play order
     */
    public PlayOrder getPlayOrder(String startingPlayerId, boolean looped) {
        int currentPlayerIndex = _turnOrder.indexOf(startingPlayerId);
        List<String> playOrder = new ArrayList<String>();
        int nextIndex = currentPlayerIndex;
        do {
            playOrder.add(_turnOrder.get(nextIndex));
            nextIndex--;
            if (nextIndex < 0)
                nextIndex = _turnOrder.size() - 1;
        } while (currentPlayerIndex != nextIndex);
        return new PlayOrder(playOrder, looped);
    }
}
