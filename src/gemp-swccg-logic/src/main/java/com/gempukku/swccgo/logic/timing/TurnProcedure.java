package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.communication.UserFeedback;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.PlayOrder;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.ActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.timing.processes.GameProcess;
import com.gempukku.swccgo.logic.timing.processes.pregame.PlayStartingEffectsGameProcess;

import java.util.*;

/**
 * This class represents the turn procedure of the game. Each step in the turn of the game is a GameProcess.
 * During each GameProcess actions on the action stack are processed. When the action stack is empty, that GameProcess
 * is complete. Each GameProcess specifies the next GameProcess to be performed.
 */
public class TurnProcedure implements Snapshotable<TurnProcedure> {
    private SwccgGame _game;
    private UserFeedback _userFeedback;
    private ActionStack _actionStack;
    private GameProcess _gameProcess;
    private boolean _playedGameProcess;
    private GameStats _gameStats;

    /**
     * Needed to generate snapshot.
     */
    public TurnProcedure() {
    }

    @Override
    public void generateSnapshot(TurnProcedure selfSnapshot, SnapshotData snapshotData) {
        TurnProcedure snapshot = selfSnapshot;

        // Set each field
        snapshot._game = _game;
        snapshot._userFeedback = _userFeedback;
        snapshot._actionStack = snapshotData.getDataForSnapshot(_actionStack);
        snapshot._gameProcess = _gameProcess;
        snapshot._playedGameProcess = _playedGameProcess;
        snapshot._gameStats = _gameStats;
    }

    /**
     * Creates the turn procedure for the game.
     * @param game the game
     * @param userFeedback the user feedback
     * @param actionStack the action stack
     */
    public TurnProcedure(SwccgGame game, final UserFeedback userFeedback, ActionStack actionStack) {
        _game = game;
        _userFeedback = userFeedback;
        _actionStack = actionStack;
        _gameStats = new GameStats();
    }

    /**
     * Gets the game stats
     * @return the game stats
     */
    public GameStats getGameStats() {
        return _gameStats;
    }

    /**
     * Performs the process of the game until there is a winner, or until response from a player is needed.
     */
    public void carryOutPendingActionsUntilDecisionNeeded() {
        int numSinceDecision = 0;

        if (_gameProcess == null) {
            // Take game snapshot for start of game
            _game.takeSnapshot("Start of game");
            // Create the first game process of the game, which is "Playing Starting Effects"
            _gameProcess = new PlayStartingEffectsGameProcess(_game);
        }

        // Continue processing until response from a player is needed, or until the game has a winner, or a snapshot is to be restored.
        while (!_userFeedback.hasPendingDecisions() && _game.getWinner() == null && !_game.isRestoreSnapshotPending()) {
            numSinceDecision++;

            // If any EffectResults need to be processed, then add an action to the stack to have them processed.
            Set<EffectResult> effectResults = _game.getActionsEnvironment().consumeEffectResults();
            if (!effectResults.isEmpty()) {
                _actionStack.stackAction(new PlayOutEffectResults(effectResults));
            }
            else {
                // If the action stack is empty, get the game process to be processed.
                if (_actionStack.isEmpty()) {
                    // If the game process has already been processed, then get the next game process to be
                    // processed.
                    if (_playedGameProcess) {
                        _gameProcess = _gameProcess.getNextProcess();
                        _playedGameProcess = false;
                    }
                    // If the game process has not yet been processed, then process it.
                    else {
                        _gameProcess.process(_game);
                        _playedGameProcess = true;
                    }
                }
                // If the action stack is not empty, the get the next effect to be processed.
                else {
                    // Get the next effect to be processed. If no effect remains, the getNextEffect method
                    // also removes the action from the action stack.
                    Effect effect = _actionStack.getNextEffect(_game);
                    if (effect != null) {
                        // If the effect does not have a type, then nothing triggers from this effect, so just perform
                        // the effect. If the effect does have a type, then add a PlayOutEffect to the action stack, which
                        // performs triggers for responses to the effect.
                        if (effect.getType() == null)
                            effect.playEffect(_game);
                        else
                            _actionStack.stackAction(new PlayOutEffect(effect));
                    }
                }
            }

            // If any game stats changed, send the game stats to the User Interface.
            if (_gameStats.updateGameStats(_game))
                _game.getGameState().sendGameStats(_gameStats);

            // Check if a winner of the game can be declared.
            _game.checkLifeForceDepleted();

            // Check if an unusually large number loops since user decision, which means game is probably in a loop
            if (numSinceDecision >= 5000) {
                _game.getGameState().sendMessage("There's been " + numSinceDecision + " actions/effects since last user decision. Game is probably looping, so ending game.");
                _actionStack.dumpStack(_game);
                effectResults = _game.getActionsEnvironment().consumeEffectResults();
                int numEffectResult = 1;
                for (EffectResult effectResult : effectResults) {
                    _game.getGameState().sendMessage("EffectResult " + (numEffectResult++) + ": " + effectResult.getType().name() + " Text: " + effectResult.getText(_game));
                }
                throw new UnsupportedOperationException("There's been " + numSinceDecision + " actions/effects since last user decision. Game is probably looping, so ending game.");
            }
        }
    }

    /**
     * An action for playing out an effect that has a type. Effects with a type can be responded to before being performed,
     * so this action adds effects for processing any required and optional "before" triggers.
     */
    private class PlayOutEffect extends SystemQueueAction {
        private Effect _effect;
        private boolean _initialized;

        /**
         * Creates a PlayOutEffect action.
         * @param effect the effect to play out
         */
        private PlayOutEffect(Effect effect) {
            _effect = effect;
        }

        @Override
        public Effect nextEffect(SwccgGame game) {
            if (!_initialized) {
                _initialized = true;
                appendEffect(new PlayoutRequiredBeforeResponsesEffect(this, new HashSet<String>(), _effect));

                // Opponent of player that performed the effect gets the first response or current player if system-generated effect.
                String playerCausingEffect = _effect.getAction().getPerformingPlayer();
                String playerWithFirstResponse = playerCausingEffect != null ? game.getOpponent(playerCausingEffect) : game.getGameState().getCurrentPlayerId();
                appendEffect(new PlayoutOptionalBeforeResponsesEffect(this, new HashSet<String>(), _game.getGameState().getPlayerOrder().getPlayOrder(playerWithFirstResponse, true), 0, _effect));

                appendEffect(new PlayEffect(this, _effect));
            }

            return getNextEffect();
        }
    }

    /**
     * An effect that performs the specified effect.
     */
    private class PlayEffect extends PassthruEffect {
        private Effect _effect;

        /**
         * Creates a PlayEffect effect.
         * @param action the action performing this effect
         * @param effect the effect to be performed by this effect.
         */
        private PlayEffect(Action action, Effect effect) {
            super(action);
            _effect = effect;
        }

        @Override
        protected void doPlayEffect(SwccgGame game) {
            _effect.playEffect(game);
        }
    }

    /**
     * An effect for playing out the required "before" responses to an effect.
     */
    private class PlayoutRequiredBeforeResponsesEffect extends PassthruEffect {
        private Set<String> _triggersUsed;
        private Effect _effect;

        /**
         * Creates an effect that plays out the required "before" responses to an effect.
         * @param action the action performing this effect
         * @param triggersUsed the triggers that have been already used
         * @param effect the effect being responded to
         */
        private PlayoutRequiredBeforeResponsesEffect(Action action, Set<String> triggersUsed, Effect effect) {
            super(action);
            _triggersUsed = triggersUsed;
            _effect = effect;
        }

        @Override
        protected void doPlayEffect(SwccgGame game) {
            final List<TriggerAction> requiredBeforeTriggers = game.getActionsEnvironment().getRequiredBeforeTriggers(_effect);
            // Remove triggers already used (unless repeatable)
            final Iterator<TriggerAction> triggersIterator = requiredBeforeTriggers.iterator();
            while (triggersIterator.hasNext()) {
                TriggerAction curTriggerAction = triggersIterator.next();
                if (!curTriggerAction.isRepeatableTrigger() && _triggersUsed.contains(curTriggerAction.getTriggerIdentifier(false)))
                    triggersIterator.remove();
            }

            // If only one action, add it to the action stack
            // Or if all the actions are the same just add the first one to the action stack.
            if (requiredBeforeTriggers.size() == 1 || areAllTriggerActionsTheSame(requiredBeforeTriggers)) {
                // Add the action to the action stack, mark trigger as used, and play out required
                // "before" responses again in case there are more triggers after this trigger is processed.
                _game.getActionsEnvironment().addActionToStack(requiredBeforeTriggers.get(0));
                _triggersUsed.add(requiredBeforeTriggers.get(0).getTriggerIdentifier(false));
                _action.insertEffect(new PlayoutRequiredBeforeResponsesEffect(_action, _triggersUsed, _effect));
            }
            // Otherwise, ask the player whose turn it is, which action to perform first.
            else if (requiredBeforeTriggers.size() > 1) {
                List<Action> actions = new LinkedList<Action>(requiredBeforeTriggers);
                String actionDesc =  _effect.getText(game) + " - Required responses";
                _game.getUserFeedback().sendAwaitingDecision(_game.getGameState().getCurrentPlayerId(),
                        new ActionSelectionDecision(actionDesc, actions) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                TriggerAction action = (TriggerAction) getSelectedAction(result);
                                if (action != null) {
                                    // Add the action to the action stack, mark trigger as used, and play out required
                                    // "before" responses again in case there are more triggers after this trigger is processed.
                                    _game.getActionsEnvironment().addActionToStack(action);
                                    _triggersUsed.add(action.getTriggerIdentifier(false));
                                    _action.insertEffect(new PlayoutRequiredBeforeResponsesEffect(_action, _triggersUsed, _effect));
                                }
                            }
                        });
            }
        }
    }

    /**
     * An effect for playing out the optional "before" responses to an effect.
     */
    private class PlayoutOptionalBeforeResponsesEffect extends PassthruEffect {
        private Set<String> _triggersUsed;
        private PlayOrder _playOrder;
        private int _passCount;
        private Effect _effect;
        private Action _thatAction;

        /**
         * Creates an effect that plays out the required "before" responses to an effect.
         * @param action the action performing this effect
         * @param triggersUsed the triggers that have been already used
         * @param passCount the number of consecutive passes
         * @param effect the effect being responded to
         */
        private PlayoutOptionalBeforeResponsesEffect(Action action, Set<String> triggersUsed, PlayOrder playOrder, int passCount, Effect effect) {
            super(action);
            _triggersUsed = triggersUsed;
            _playOrder = playOrder;
            _passCount = passCount;
            _effect = effect;
            _thatAction = action;
        }

        @Override
        public void doPlayEffect(SwccgGame game) {
            final String activePlayer = _playOrder.getNextPlayer();

            final List<TriggerAction> optionalBeforeTriggers = game.getActionsEnvironment().getOptionalBeforeTriggers(activePlayer, _effect);
            // Remove triggers already used (unless repeatable)
            final Iterator<TriggerAction> triggersIterator = optionalBeforeTriggers.iterator();
            while (triggersIterator.hasNext()) {
                TriggerAction curTriggerAction = triggersIterator.next();
                if (!curTriggerAction.isRepeatableTrigger() && _triggersUsed.contains(curTriggerAction.getTriggerIdentifier(false)))
                    triggersIterator.remove();
            }

            // Also get optional before actions (which generally involve playing a card as a response)
            final List<Action> optionalBeforeActions = _game.getActionsEnvironment().getOptionalBeforeActions(activePlayer, _effect);

            List<Action> possibleActions = new LinkedList<Action>(optionalBeforeTriggers);
            possibleActions.addAll(optionalBeforeActions);

            boolean noDelay = game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS;

            if (!possibleActions.isEmpty() || !noDelay) {
                // Ask the player which action to perform. The player may choose an action or pass.
                String effectText = _effect.getText(game);
                if (_effect.getType() == Type.PLAYING_CARD_EFFECT) {
                    PhysicalCard card = ((RespondablePlayingCardEffect) _effect).getCard();
                    effectText = (card.getBlueprint().isCardTypeDeployed() ? "Deploying " : "Playing ") + GameUtils.getCardLink(card);
                }
                String actionDesc = effectText  + " - Optional responses";
                _game.getUserFeedback().sendAwaitingDecision(activePlayer,
                        new CardActionSelectionDecision(1, actionDesc, possibleActions, activePlayer.equals(game.getGameState().getCurrentPlayerId()), false, false, true, false) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                final Action action = getSelectedAction(result);
                                if (action != null) {
                                    // Add the action to the action stack, mark (if a trigger action) as used, and play
                                    // out optional "before" responses again for other player with passCount set to 0.
                                    action.appendAfterEffect(new PassthruEffect(action) {
                                         @Override
                                         protected void doPlayEffect(SwccgGame game) {
                                             if (action.isChoosingTargetsComplete() || action.wasCarriedOut()) {
                                                 if (action instanceof TriggerAction) {
                                                     TriggerAction triggerAction = (TriggerAction) action;
                                                     _triggersUsed.add(triggerAction.getTriggerIdentifier(false));
                                                 }
                                                 _thatAction.insertEffect(new PlayoutOptionalBeforeResponsesEffect(_thatAction, _triggersUsed, _playOrder, 0, _effect));
                                             } else {
                                                 // Action was aborted, check with same player again
                                                 _playOrder.getNextPlayer();
                                                 _thatAction.insertEffect(new PlayoutOptionalBeforeResponsesEffect(_thatAction, _triggersUsed, _playOrder, 0, _effect));
                                             }
                                         }
                                     });
                                    _game.getActionsEnvironment().addActionToStack(action);
                                } else {
                                    // Player passed, so check if all players have consecutively passed. If not, then play
                                    // out optional "before" responses again for other player with passCount incremented.
                                    if ((_passCount + 1) < _playOrder.getPlayerCount()) {
                                        _thatAction.insertEffect(new PlayoutOptionalBeforeResponsesEffect(_thatAction, _triggersUsed, _playOrder, _passCount + 1, _effect));
                                    }
                                }
                            }
                        });
            }
            else {
                // Player had no possible actions, so treat as player passed and check if all players have consecutively
                // passed. If not, then play out optional "before" responses again for other player with passCount incremented.
                if ((_passCount + 1) < _playOrder.getPlayerCount()) {
                    _thatAction.insertEffect(new PlayoutOptionalBeforeResponsesEffect(_thatAction, _triggersUsed, _playOrder, _passCount + 1, _effect));
                }
            }
        }
    }

    /**
     * An effect for removing modifiers that expire when a specified condition is fulfilled.
     */
    public class CheckForExpiredModifiersEffect extends PassthruEffect {
        private Set<EffectResult> _effectResults;

        private CheckForExpiredModifiersEffect(Action action, Set<EffectResult> effectResults) {
            super(action);
            _effectResults = effectResults;
        }

        @Override
        public void doPlayEffect(SwccgGame game) {
            for (EffectResult effectResult : _effectResults) {
                if (TriggerConditions.isTableChanged(game, effectResult)) {
                    game.getModifiersEnvironment().removeExpiredModifiers();
                    break;
                }
            }
        }
    }



    /**
     * Determines the action decision text to show based on the effect results.
     * @param game the game
     * @param possibleActions the possible actions
     * @param effectResults the effect results
     * @param textToAppend the text to append
     * @return true if all actions are the same, otherwise false
     */
    public static String getActionDecisionTextFromEffectResults(SwccgGame game, Collection<? extends Action> possibleActions, Collection<? extends EffectResult> effectResults, String textToAppend) {
        String text = null;
        boolean allSingletonActions = true;
        for (Action action : possibleActions) {
            if (action instanceof TriggerAction) {
                TriggerAction triggerAction = (TriggerAction) action;
                if (!triggerAction.isSingletonTrigger()) {
                    allSingletonActions = false;
                    break;
                }
            }
            else {
                allSingletonActions = false;
                break;
            }
        }
        if (!allSingletonActions) {
            for (EffectResult effectResult : effectResults) {
                String curText = effectResult.getText(game);
                if (text == null) {
                    text = curText;
                } else if (!text.equals(effectResult.getText(game))) {
                    text = null;
                    break;
                }
            }
        }

        //If we are running inside the test rig, we want more information on what procedure we are currently
        // on than just "optional response".
        if(game.isTestEnvironment() && text == null && effectResults.size() == 1) {
            text = effectResults.stream().findFirst().get().getType().toString();
        }
        return (text != null ? (text + " - ") : "") + textToAppend;
    }

    /**
     * Determines if all the trigger actions are the same. This is verified if all the actions have the same trigger id
     * (NOT FOR NOW: using blueprint id instead of card id). This will see if all the cards are the same printed card having the same
     * action to perform.
     * @param actions the actions to compare
     * @return true if all actions are the same, otherwise false
     */
    public static boolean areAllTriggerActionsTheSame(Collection<TriggerAction> actions) {
        if (actions.isEmpty())
            return false;

        Iterator<TriggerAction> actionIterator = actions.iterator();
        String triggerId = actionIterator.next().getTriggerIdentifier(false);

        while (actionIterator.hasNext()) {
            if (!actionIterator.next().getTriggerIdentifier(false).equals(triggerId))
                return false;
        }
        return true;
    }

    /**
     * Determines if order does not matter for the effect results.
     * @param effectResults the triggers to complete
     * @return true if order does not matter, otherwise false
     */
    public static boolean doesOrderNotMatterForEffectResult(Collection<EffectResult> effectResults) {
        boolean anyOrderIsFine = true;
        for (EffectResult effectResult : effectResults) {
            if (effectResult.getType() != EffectResult.Type.BLOWN_AWAY_CALCULATE_FORCE_LOSS_STEP) {
                anyOrderIsFine = false;
                break;
            }
        }
        return anyOrderIsFine;
    }
}
