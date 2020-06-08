package com.gempukku.swccgo.logic.timing.processes.turn.general;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;
import com.gempukku.swccgo.logic.timing.results.EndOfPhaseResult;

/**
 * The game process for the end of a phase.
 */
public class EndOfPhaseGameProcess implements GameProcess {
    private Phase _phase;
    private GameProcess _followingGameProcess;

    /**
     * Creates a game process for the end of a phase.
     * @param phase the phase
     * @param followingGameProcess the next game process
     */
    public EndOfPhaseGameProcess(Phase phase, GameProcess followingGameProcess) {
        _phase = phase;
        _followingGameProcess = followingGameProcess;
    }

    @Override
    public void process(SwccgGame game) {
        SystemQueueAction action = new SystemQueueAction();
        // Trigger effect result for "End of phase"
        action.appendEffect(
                new TriggeringResultEffect(action, new EndOfPhaseResult(_phase)));
        game.getActionsEnvironment().addActionToStack(action);
    }

    @Override
    public GameProcess getNextProcess() {
        return _followingGameProcess;
    }
}
