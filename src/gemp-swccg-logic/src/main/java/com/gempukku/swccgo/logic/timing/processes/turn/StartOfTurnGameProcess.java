package com.gempukku.swccgo.logic.timing.processes.turn;

import com.gempukku.swccgo.communication.InGameStatisticsListener;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;
import com.gempukku.swccgo.logic.timing.results.StartOfTurnResult;

/**
 * The game process for the start of a turn.
 */
public class StartOfTurnGameProcess implements GameProcess {

    @Override
    public void process(SwccgGame game) {
        final GameState gameState = game.getGameState();
        gameState.sendMessage("Start of " + gameState.getCurrentPlayerId() + "'s turn #" + gameState.getPlayersLatestTurnNumber(gameState.getCurrentPlayerId()));

        for(InGameStatisticsListener inGameStatisticsListener:game.getAllInGameStatisticsListeners()) {
            inGameStatisticsListener.writePileCounts(game,false);
        }

        SystemQueueAction action = new SystemQueueAction();
        // Remove any modifiers that expire at the start of the turn
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    public void doPlayEffect(SwccgGame game) {
                        game.getModifiersEnvironment().removeStartOfTurnModifiers();
                    }
                });
        // Trigger effect result for "Start of turn"
        action.appendEffect(
                new TriggeringResultEffect(action, new StartOfTurnResult()));
        // Set Total Force Generation
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                        String currentPlayerId = gameState.getCurrentPlayerId();
                        String opponent = game.getOpponent(currentPlayerId);

                        gameState.setPlayersTotalForceGeneration(currentPlayerId, modifiersQuerying.getTotalForceGeneration(gameState, currentPlayerId));
                        gameState.sendMessage(currentPlayerId + "'s Force generation for this turn is " + GuiUtils.formatAsString(gameState.getPlayersTotalForceGeneration(currentPlayerId)));

                        gameState.setPlayersTotalForceGeneration(opponent, modifiersQuerying.getTotalForceGeneration(gameState, opponent));
                        gameState.sendMessage(opponent + "'s Force generation for this turn is " + GuiUtils.formatAsString(gameState.getPlayersTotalForceGeneration(opponent)));

                        for(InGameStatisticsListener inGameStatisticsListener:game.getAllInGameStatisticsListeners()) {
                            inGameStatisticsListener.writeActivationTotals(game);
                        }
                    }
                });
        game.getActionsEnvironment().addActionToStack(action);
    }

    @Override
    public GameProcess getNextProcess() {
        return new ActivatePhaseGameProcess();
    }
}
