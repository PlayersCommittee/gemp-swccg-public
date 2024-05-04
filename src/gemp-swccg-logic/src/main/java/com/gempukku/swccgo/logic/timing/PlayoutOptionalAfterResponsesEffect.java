package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.PlayOrder;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An effect for playing out the optional "after" responses to an effect result.
 */
public class PlayoutOptionalAfterResponsesEffect extends AbstractSuccessfulEffect {
    private PlayOrder _playOrder;
    private int _passCount;
    private Collection<? extends EffectResult> _effectResults;
    private Action _thatAction;

    /**
     * Creates an effect for playing out the optional "after" responses to an effect result.
     * @param action the action performing this effect.
     * @param playOrder the player order
     * @param passCount the number of consecutive passes
     * @param effectResults the effect results
     */
    public PlayoutOptionalAfterResponsesEffect(Action action, PlayOrder playOrder, int passCount, Collection<? extends EffectResult> effectResults) {
        super(action);
        _playOrder = playOrder;
        _passCount = passCount;
        _effectResults = effectResults;
        _thatAction = action;
    }

    @Override
    public void doPlayEffect(final SwccgGame game) {
        final String activePlayer = _playOrder.getNextPlayer();

        final Map<TriggerAction, EffectResult> optionalAfterTriggers = game.getActionsEnvironment().getOptionalAfterTriggers(activePlayer, _effectResults);

        // Also get optional after actions (which generally involve playing a card as a response)
        final List<Action> optionalAfterActions = game.getActionsEnvironment().getOptionalAfterActions(activePlayer, _effectResults);

        List<Action> possibleActions = new LinkedList<Action>(optionalAfterTriggers.keySet());
        possibleActions.addAll(optionalAfterActions);

        // Check if there should be no delay at all when no actions
        boolean noDelay = game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS;
        if (!noDelay) {
            for (EffectResult effectResult : _effectResults) {
                if (effectResult.getType() == EffectResult.Type.FORCE_ACTIVATED
                        || effectResult.getType() == EffectResult.Type.FORCE_USED
                        || effectResult.getType() == EffectResult.Type.ABOUT_TO_DRAW_CARD_FROM_FORCE_PILE
                        || effectResult.getType() == EffectResult.Type.ABOUT_TO_LOSE_OR_FORFEIT_DURING_DAMAGE_SEGMENT
                        || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_FORFEITED_TO_FROM_TABLE
                        || effectResult.getType() == EffectResult.Type.FORCE_LOST
                        || effectResult.getType() == EffectResult.Type.BATTLE_WEAPONS_SEGMENT_COMPLETED
                        || effectResult.getType() == EffectResult.Type.BATTLE_RESULT_DETERMINED
                        || effectResult.getType() == EffectResult.Type.BATTLE_ENDING
                        || effectResult.getType() == EffectResult.Type.BATTLE_ENDED
                        || effectResult.getType() == EffectResult.Type.START_OF_TURN
                        || effectResult.getType() == EffectResult.Type.START_OF_PHASE
                        || effectResult.getType() == EffectResult.Type.END_OF_PHASE
                        || effectResult.getType() == EffectResult.Type.END_OF_TURN) {
                    noDelay = true;
                    break;
                }
            }
        }

        if (!possibleActions.isEmpty() || !noDelay) {
            String actionDesc = TurnProcedure.getActionDecisionTextFromEffectResults(game, possibleActions, _effectResults, "Optional responses");
            // Ask the player which action to perform. The player may choose an action or pass.
            game.getUserFeedback().sendAwaitingDecision(activePlayer,
                    new CardActionSelectionDecision(1, actionDesc, possibleActions, activePlayer.equals(game.getGameState().getCurrentPlayerId()), false, false, true, false) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            final Action action = getSelectedAction(result);
                            if (action != null) {
                                // Add the action to the action stack, mark (if a trigger action) as used, and play
                                // out optional "after" responses again for other player with passCount set to 0.
                                action.appendAfterEffect(new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        if (action.isChoosingTargetsComplete() || action.wasCarriedOut()) {
                                            if (action instanceof TriggerAction) {
                                                optionalAfterTriggers.get(action).triggerUsed((TriggerAction) action);
                                            }
                                            _thatAction.insertEffect(new PlayoutOptionalAfterResponsesEffect(_thatAction, _playOrder, 0, _effectResults));
                                        }
                                        else {
                                            // Action was aborted, check with same player again
                                            _playOrder.getNextPlayer();
                                            _thatAction.insertEffect(new PlayoutOptionalAfterResponsesEffect(_thatAction, _playOrder, 0, _effectResults));
                                        }
                                    }
                                });
                                game.getActionsEnvironment().addActionToStack(action);
                            }
                            else {
                                // Player passed, so check if all players have consecutively passed. If not, then play
                                // out optional "after" responses again for other player with passCount incremented.
                                if ((_passCount + 1) < _playOrder.getPlayerCount()) {
                                    _thatAction.insertEffect(new PlayoutOptionalAfterResponsesEffect(_thatAction, _playOrder, _passCount + 1, _effectResults));
                                }
                            }
                        }
                    }
            );
        }
        else {
            // Player had no possible actions, so treat as player passed and check if all players have consecutively
            // passed. If not, then play out optional "after" responses again for other player with passCount incremented.
            if ((_passCount + 1) < _playOrder.getPlayerCount()) {
                _thatAction.insertEffect(new PlayoutOptionalAfterResponsesEffect(_thatAction, _playOrder, _passCount + 1, _effectResults));
            }
        }
    }
}
