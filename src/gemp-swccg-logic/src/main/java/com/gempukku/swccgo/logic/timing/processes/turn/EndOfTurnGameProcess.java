package com.gempukku.swccgo.logic.timing.processes.turn;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;
import com.gempukku.swccgo.logic.timing.results.EndOfTurnResult;

/**
 * The game process for the end of a turn.
 */
public class EndOfTurnGameProcess implements GameProcess {
    @Override
    public void process(SwccgGame game) {
        SystemQueueAction action = new SystemQueueAction();
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    public void doPlayEffect(SwccgGame game) {
                        game.getGameState().setCurrentPhase(Phase.END_OF_TURN);
                    }
                });
        // Both players recirculate
        action.appendEffect(
                new RecirculateEffect(action, game.getGameState().getCurrentPlayerId()));
        action.appendEffect(
                new RecirculateEffect(action, game.getOpponent(game.getGameState().getCurrentPlayerId())));
        // Remove any modifiers that expire at the end of the turn
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    public void doPlayEffect(SwccgGame game) {
                        game.getModifiersEnvironment().removeEndOfTurnModifiers();
                    }
                });
        // Trigger effect result for "End of turn"
        action.appendEffect(
                new TriggeringResultEffect(action, new EndOfTurnResult()));
        // Remove any counters and action proxies that expire at the end of the turn
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    public void doPlayEffect(SwccgGame game) {
                        game.getModifiersEnvironment().removeEndOfTurnCounters();
                        game.getActionsEnvironment().removeEndOfTurnActionProxies();
                    }
                });
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    public void doPlayEffect(SwccgGame game) {
                        game.getGameState().setCurrentPhase(Phase.BETWEEN_TURNS);
                    }
                });
        game.getActionsEnvironment().addActionToStack(action);
    }

    @Override
    public GameProcess getNextProcess() {
        return new BetweenTurnsProcess();
    }
}
