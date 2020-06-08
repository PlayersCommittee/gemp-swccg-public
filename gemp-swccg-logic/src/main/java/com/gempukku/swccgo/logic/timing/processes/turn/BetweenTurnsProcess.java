package com.gempukku.swccgo.logic.timing.processes.turn;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;

/**
 * The game process that changes turns.
 */
public class BetweenTurnsProcess implements GameProcess {

    @Override
    public void process(SwccgGame game) {
        String nextPlayer = game.getOpponent(game.getGameState().getCurrentPlayerId());

        // Take game snapshot for start of turn
        game.takeSnapshot("Start of " + nextPlayer + "'s turn #" + (game.getGameState().getPlayersLatestTurnNumber(nextPlayer) + 1));

        game.getGameState().startPlayerTurn(nextPlayer);
    }

    @Override
    public GameProcess getNextProcess() {
        return new StartOfTurnGameProcess();
    }
}
