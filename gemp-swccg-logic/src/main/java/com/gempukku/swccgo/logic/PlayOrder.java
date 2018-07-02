package com.gempukku.swccgo.logic;

import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a play order of players.
 */
public class PlayOrder implements Snapshotable<PlayOrder> {
    private List<String> _playOrder = new ArrayList<String>();
    private boolean _looped;
    private int _nextPlayerIndex;

    /**
     * Needed to generate snapshot.
     */
    public PlayOrder() {
    }

    @Override
    public void generateSnapshot(PlayOrder selfSnapshot, SnapshotData snapshotData) {
        PlayOrder snapshot = selfSnapshot;

        snapshot._playOrder.addAll(_playOrder);
        snapshot._looped = _looped;
        snapshot._nextPlayerIndex = _nextPlayerIndex;
    }

    /**
     * Creates a play order with the specified player order and whether the order repeats.
     * @param playOrder the player order
     * @param looped true if the order repeats, otherwise false if is just goes one time through the order
     */
    public PlayOrder(List<String> playOrder, boolean looped) {
        _playOrder.addAll(playOrder);
        _looped = looped;
    }

    /**
     * Gets the next player to take a turn in the order. If the last player in the order is returned and the
     * order is set to loop, then the following call to this method will return the first player again, otherwise if
     * it is not set to loop, then the following call will return null.
     * @return the next player to take a turn in the order
     */
    public String getNextPlayer() {
        if (_nextPlayerIndex >= getPlayerCount())
            return null;

        String nextPlayer = _playOrder.get(_nextPlayerIndex);
        _nextPlayerIndex++;
        if (_nextPlayerIndex >= getPlayerCount() && _looped)
            _nextPlayerIndex = 0;
        return nextPlayer;
    }

    /**
     * Gets the number of players in the order
     * @return the number of players in the order
     */
    public int getPlayerCount() {
        return _playOrder.size();
    }
}
