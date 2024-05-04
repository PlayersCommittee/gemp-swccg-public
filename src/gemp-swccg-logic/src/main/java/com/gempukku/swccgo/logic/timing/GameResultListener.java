package com.gempukku.swccgo.logic.timing;

import java.util.Map;

/**
 * The interface that defines the methods that game results listeners much implement.
 */
public interface GameResultListener {

    /**
     * This method is called when the game is finished.
     * @param winnerPlayerId the winning player
     * @param winReason the winning reason
     * @param loserPlayerIdsWithReasons losing players
     * @param winnerSide the side of the Force that won
     * @param loserSide the side of the Force that lost
     */
    void gameFinished(String winnerPlayerId, String winReason, Map<String, String> loserPlayerIdsWithReasons, String winnerSide, String loserSide);

    /**
     * This method is called when the game is canceled.
     */
    void gameCancelled();
}
