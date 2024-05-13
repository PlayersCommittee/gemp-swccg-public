package com.gempukku.swccgo.logic.timing.processes;

import com.gempukku.swccgo.game.SwccgGame;

/**
 * The interface that defines methods that a game process must implement.
 */
public interface GameProcess {

    /**
     * Performs the game process.
     * @param game the game
     */
    void process(SwccgGame game);

    /**
     * Gets the next game process after this game process.
     * @return the next game process
     */
    GameProcess getNextProcess();
}
