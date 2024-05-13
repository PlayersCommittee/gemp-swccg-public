package com.gempukku.swccgo.logic.timing.processes.turn.general;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.PlayOrder;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GameSnapshot;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;

import java.util.ArrayList;
import java.util.List;

/**
 * The game process for allowing players to perform phase actions during a phase.
 */
public class PlayersPlayPhaseActionsInOrderGameProcess implements GameProcess {
    private PlayOrder _playOrder;
    private int _consecutivePasses;
    private GameProcess _followingGameProcess;
    private GameProcess _nextProcess;

    /**
     * Creates a game process for allowing players to perform phase actions during a phase.
     * @param playOrder the player order
     * @param consecutivePasses the current number of consecutive passes
     * @param followingGameProcess the game process to perform after this game process completes
     */
    public PlayersPlayPhaseActionsInOrderGameProcess(PlayOrder playOrder, int consecutivePasses, GameProcess followingGameProcess) {
        _playOrder = playOrder;
        _consecutivePasses = consecutivePasses;
        _followingGameProcess = followingGameProcess;
    }

    @Override
    public void process(final SwccgGame game) {
        final String playerId = _playOrder.getNextPlayer();
        _nextProcess = new PlayersPlayPhaseActionsInOrderGameProcess(game.getGameState().getPlayerOrder().getPlayOrder(playerId, true), _consecutivePasses, _followingGameProcess);

        // Gather any top-level actions the player can perform
        final List<Action> playableActions = game.getActionsEnvironment().getTopLevelActions(playerId);

        // Ask the player to choose an action or pass
        game.getUserFeedback().sendAwaitingDecision(playerId,
                new CardActionSelectionDecision(1, "Choose " + game.getGameState().getCurrentPhase().getHumanReadable() + " action or Pass", playableActions, playerId.equals(game.getGameState().getCurrentPlayerId()), true, false, false, true) {
                    @Override
                    public void decisionMade(String result) throws DecisionResultInvalidException {
                        // Check if revert to previous game state was chosen
                        if ("revert".equalsIgnoreCase(result)) {
                            performRevert(game, playerId);
                        }
                        else {
                            final Action action = getSelectedAction(result);
                            if (action != null) {

                                // Take game snapshot before top-level action performed
                                String snapshotSourceCardInfo = action.getActionSource() != null ? (": " + GameUtils.getCardLink(action.getActionSource())) : "";
                                game.takeSnapshot(playerId + ": " + action.getText() + snapshotSourceCardInfo);

                                action.appendAfterEffect(new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        if (action.isChoosingTargetsComplete() || action.wasCarriedOut()) {
                                            _nextProcess = new PlayersPlayPhaseActionsInOrderGameProcess(game.getGameState().getPlayerOrder().getPlayOrder(_playOrder.getNextPlayer(), true), 0, _followingGameProcess);
                                        }
                                        // Action was aborted, check with same player again
                                        else {
                                            checkPlayerAgain(game);
                                        }
                                    }
                                });
                                game.getActionsEnvironment().addActionToStack(action);
                            } else {
                                // If this is the player's Activate phase, and no Force was activated (but could have been),
                                // check if the player meant to click Pass.
                                if (game.getGameState().getCurrentPlayerId().equals(playerId) && game.getGameState().getCurrentPhase() == Phase.ACTIVATE
                                        && !game.getModifiersQuerying().isActivatingForceProhibited(game.getGameState(), playerId) && game.getGameState().getReserveDeckSize(playerId) > 0
                                        && game.getModifiersQuerying().getForceActivatedThisTurn(playerId, false) == 0) {

                                    game.getUserFeedback().sendAwaitingDecision(playerId,
                                            new YesNoDecision("You have not activated Force. Do you want to Pass?") {
                                                @Override
                                                protected void yes() {
                                                    playerPassed(game, playerId);
                                                }

                                                @Override
                                                protected void no() {
                                                    checkPlayerAgain(game);
                                                }
                                            });
                                } else {
                                    playerPassed(game, playerId);
                                }
                            }
                        }
                    }
                }
        );
    }

    /**
     * This method is called if the player passed.
     * @param game the game
     * @param playerId the player that passed
     */
    private void playerPassed(SwccgGame game, String playerId) {
        // Take game snapshot before player passed
        game.takeSnapshot(playerId + ": Pass");

        if (_consecutivePasses + 1 >= _playOrder.getPlayerCount())
            _nextProcess = _followingGameProcess;
        else
            _nextProcess = new PlayersPlayPhaseActionsInOrderGameProcess(game.getGameState().getPlayerOrder().getPlayOrder(_playOrder.getNextPlayer(), true), _consecutivePasses + 1, _followingGameProcess);
    }

    /**
     * This method if the same player should be asked again to choose an action or pass.
     * @param game the game
     */
    private void checkPlayerAgain(SwccgGame game) {
        _playOrder.getNextPlayer();
        _nextProcess = new PlayersPlayPhaseActionsInOrderGameProcess(game.getGameState().getPlayerOrder().getPlayOrder(_playOrder.getNextPlayer(), true), _consecutivePasses, _followingGameProcess);
    }

    /**
     * This method if the player chooses to revert game to a previous game state.
     * @param game the game
     * @param playerId the player
     */
    private void performRevert(final SwccgGame game, final String playerId) {
        final List<Integer> snapshotIds = new ArrayList<Integer>();
        final List<String> snapshotDescriptions = new ArrayList<String>();
        for (GameSnapshot gameSnapshot : game.getSnapshots()) {
            snapshotIds.add(gameSnapshot.getId());
            snapshotDescriptions.add(gameSnapshot.getDescription());
        }
        int numSnapshots = snapshotDescriptions.size();
        if (numSnapshots == 0) {
            checkPlayerAgain(game);
            return;
        }
        snapshotIds.add(-1);
        snapshotDescriptions.add("Do not revert");

        // Ask player to choose snapshot to revert back to
        game.getUserFeedback().sendAwaitingDecision(playerId,
                new MultipleChoiceAwaitingDecision("Choose game state to revert prior to", snapshotDescriptions.toArray(new String[0]), snapshotDescriptions.size() - 1) {
                    @Override
                    public void validDecisionMade(int index, String result) {
                        final int snapshotIdChosen = snapshotIds.get(index);
                        if (snapshotIdChosen == -1) {
                            checkPlayerAgain(game);
                            return;
                        }

                        game.getGameState().sendMessage(playerId + " attempts to revert game to a previous state");

                        // Confirm with the other player if it is acceptable to revert to the game state
                        final String opponent = game.getOpponent(playerId);
                        StringBuilder snapshotDescMsg = new StringBuilder("</br>");
                        for (int i=0; i<snapshotDescriptions.size() - 1; ++i) {
                            if (i == index) {
                                snapshotDescMsg.append("</br>").append(">>> Revert to here <<<");
                            }
                            if ((index - i) < 3) {
                                snapshotDescMsg.append("</br>").append(snapshotDescriptions.get(i));
                            }
                        }
                        snapshotDescMsg.append("</br>");

                        game.getUserFeedback().sendAwaitingDecision(opponent,
                                new YesNoDecision("Do you want to allow game to be reverted to the following game state?" + snapshotDescMsg) {
                                    @Override
                                    protected void yes() {
                                        game.getGameState().sendMessage(opponent + " allows game to revert to a previous state");
                                        game.requestRestoreSnapshot(snapshotIdChosen);
                                    }
                                    @Override
                                    protected void no() {
                                        game.getGameState().sendMessage(opponent + " denies attempt to revert game to a previous state");
                                        checkPlayerAgain(game);
                                    }
                                });
                    }
                });
    }


    @Override
    public GameProcess getNextProcess() {
        return _nextProcess;
    }
}
