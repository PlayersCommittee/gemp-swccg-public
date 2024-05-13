package com.gempukku.swccgo.logic.timing.actions.attack;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GameSnapshot;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * An effect that asks a player to choose an action or pass.
 */
class AttackPlayerPlaysNextActionEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that asks a player to choose an action or pass.
     * @param action the action performing this effect
     */
    AttackPlayerPlaysNextActionEffect(AttackWeaponsSegmentAction action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(final SwccgGame game) {
        final AttackWeaponsSegmentAction attackWeaponsSegmentAction = (AttackWeaponsSegmentAction) _action;

        final String playerId = attackWeaponsSegmentAction.getPlayOrder().getNextPlayer();

        // Get the top-level actions that the player can perform
        final List<Action> playableActions = game.getActionsEnvironment().getTopLevelActions(playerId);

        game.getUserFeedback().sendAwaitingDecision(playerId,
                new CardActionSelectionDecision(1, "Choose weapons segment action to play or Pass", playableActions, playerId.equals(game.getGameState().getCurrentPlayerId()), false, false, false, true) {
                    @Override
                    public void decisionMade(String result) throws DecisionResultInvalidException {
                        // Check if revert to previous game state was chosen
                        if ("revert".equalsIgnoreCase(result)) {
                            performRevert(game, playerId, attackWeaponsSegmentAction);
                        } else {
                            attackWeaponsSegmentAction.appendEffect(
                                    new AttackCheckIfWeaponsSegmentFinishedEffect(attackWeaponsSegmentAction));

                            final Action action = getSelectedAction(result);
                            if (action != null) {
                                // Take game snapshot before top-level action performed
                                String snapshotSourceCardInfo = action.getActionSource() != null ? (": " + GameUtils.getCardLink(action.getActionSource())) : "";
                                game.takeSnapshot(playerId + " (weapons segment): " + action.getText() + snapshotSourceCardInfo);

                                action.appendAfterEffect(new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        if (action.isChoosingTargetsComplete() || action.wasCarriedOut()) {
                                            attackWeaponsSegmentAction.setConsecutivePasses(0);
                                            attackWeaponsSegmentAction.setFirstAction(false);
                                        }
                                        // Action was aborted, check with same player again
                                        else {
                                            checkPlayerAgain(attackWeaponsSegmentAction);
                                        }
                                    }
                                });
                                // Add the action to the stack
                                game.getActionsEnvironment().addActionToStack(action);
                            } else {
                                playerPassed(game, playerId, attackWeaponsSegmentAction);
                            }
                        }
                    }
                }
        );
    }

    /**
     * This method is called when the player passes.
     * @param game the game
     * @param playerId the player that passed
     * @param attackWeaponsSegmentAction the weapon segment action
     */
    private void playerPassed(SwccgGame game, String playerId, AttackWeaponsSegmentAction attackWeaponsSegmentAction) {
        // Take game snapshot before player passed
        game.takeSnapshot(playerId + " (weapons segment): Pass");

        attackWeaponsSegmentAction.setConsecutivePasses(attackWeaponsSegmentAction.getConsecutivePasses() + 1);
        attackWeaponsSegmentAction.setFirstAction(false);
    }

    /**
     * This method if the same player should be asked again to choose an action or pass.
     * @param attackWeaponsSegmentAction the weapon segment action
     */
    private void checkPlayerAgain(AttackWeaponsSegmentAction attackWeaponsSegmentAction) {
        attackWeaponsSegmentAction.getPlayOrder().getNextPlayer();
    }

    /**
     * This method if the player chooses to revert game to a previous game state.
     * @param game the game
     * @param playerId the player
     * @param attackWeaponsSegmentAction the weapon segment action
     */
    private void performRevert(final SwccgGame game, final String playerId, final AttackWeaponsSegmentAction attackWeaponsSegmentAction) {
        final List<Integer> snapshotIds = new ArrayList<Integer>();
        final List<String> snapshotDescriptions = new ArrayList<String>();
        for (GameSnapshot gameSnapshot : game.getSnapshots()) {
            snapshotIds.add(gameSnapshot.getId());
            snapshotDescriptions.add(gameSnapshot.getDescription());
        }
        int numSnapshots = snapshotDescriptions.size();
        if (numSnapshots == 0) {
            checkPlayerAgain(attackWeaponsSegmentAction);
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
                            attackWeaponsSegmentAction.appendEffect(
                                    new AttackCheckIfWeaponsSegmentFinishedEffect(attackWeaponsSegmentAction));
                            checkPlayerAgain(attackWeaponsSegmentAction);
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
                                        attackWeaponsSegmentAction.appendEffect(
                                                new AttackCheckIfWeaponsSegmentFinishedEffect(attackWeaponsSegmentAction));
                                        checkPlayerAgain(attackWeaponsSegmentAction);
                                    }
                                });
                    }
                });
    }
}
