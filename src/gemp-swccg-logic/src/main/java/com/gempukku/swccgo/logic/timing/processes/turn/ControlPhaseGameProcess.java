package com.gempukku.swccgo.logic.timing.processes.turn;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;
import com.gempukku.swccgo.logic.timing.processes.turn.general.EndOfPhaseGameProcess;
import com.gempukku.swccgo.logic.timing.processes.turn.general.PlayersPlayPhaseActionsInOrderGameProcess;
import com.gempukku.swccgo.logic.timing.processes.turn.general.StartOfPhaseGameProcess;

/**
 * The game process for the Control phase.
 */
public class ControlPhaseGameProcess implements GameProcess {
    private SwccgGame _game;

    @Override
    public void process(SwccgGame game) {
        _game = game;
        game.getGameState().setCurrentPhase(Phase.CONTROL);

        // Take game snapshot for start of phase
        game.takeSnapshot("Start of " + game.getGameState().getCurrentPlayerId() + "'s " + Phase.CONTROL.getHumanReadable().toLowerCase() + " phase #" + game.getGameState().getPlayersLatestTurnNumber(game.getGameState().getCurrentPlayerId()));
    }

    @Override
    public GameProcess getNextProcess() {
        return new StartOfPhaseGameProcess(Phase.CONTROL,
                new PlayersPlayPhaseActionsInOrderGameProcess(_game.getGameState().getPlayerOrder().getPlayOrder(_game.getGameState().getCurrentPlayerId(), true), 0,
                        new EndOfPhaseGameProcess(Phase.CONTROL,
                                new DeployPhaseGameProcess())));
    }
}
