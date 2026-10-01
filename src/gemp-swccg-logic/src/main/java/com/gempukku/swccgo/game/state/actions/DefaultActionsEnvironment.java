package com.gempukku.swccgo.game.state.actions;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.BlowAwayState;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.timing.*;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;
import com.google.common.base.Objects;

import java.util.*;

/**
 * The implementation of the actions environment interface, which keeps track of action-related information for the game.
 */
public class DefaultActionsEnvironment implements ActionsEnvironment {
    private SwccgGame _swccgGame;
    private ActionStack _actionStack;
    private List<ActionProxy> _actionProxies = new LinkedList<ActionProxy>();
    private List<ActionProxy> _untilEndOfTurnActionProxies = new LinkedList<ActionProxy>();
    private Map<String, List<ActionProxy>> _untilEndOfPlayersNextTurnActionProxies = new HashMap<String, List<ActionProxy>>();
    private List<ActionProxy> _untilEndOfBattleActionProxies = new LinkedList<ActionProxy>();
    private List<ActionProxy> _untilEndOfDuelActionProxies = new LinkedList<ActionProxy>();
    private List<ActionProxy> _untilEndOfLightsaberCombatActionProxies = new LinkedList<ActionProxy>();
    private List<ActionProxy> _untilEndOfWeaponFiringActionProxies = new LinkedList<ActionProxy>();
    private Map<Integer, List<ActionProxy>> _untilEndOfDrawDestinyActionProxies = new HashMap<Integer, List<ActionProxy>>();
    private Map<Integer, List<ActionProxy>> _untilEndOfBlowAwayActionProxies = new HashMap<Integer, List<ActionProxy>>();
    private Set<EffectResult> _effectResults = new HashSet<EffectResult>();

    /**
     * Needed to generate snapshot.
     */
    public DefaultActionsEnvironment() {
    }

    @Override
    public void generateSnapshot(ActionsEnvironment selfSnapshot, SnapshotData snapshotData) {
        DefaultActionsEnvironment snapshot = (DefaultActionsEnvironment) selfSnapshot;

        // Set each field
        snapshot._swccgGame = _swccgGame;
        snapshot._actionStack = snapshotData.getDataForSnapshot(_actionStack);
        snapshot._actionProxies.addAll(_actionProxies);
        snapshot._untilEndOfTurnActionProxies.addAll(_untilEndOfTurnActionProxies);
        for (String playerId : _untilEndOfPlayersNextTurnActionProxies.keySet()) {
            List<ActionProxy> snapshotList = new LinkedList<ActionProxy>(_untilEndOfPlayersNextTurnActionProxies.get(playerId));
            snapshot._untilEndOfPlayersNextTurnActionProxies.put(playerId, snapshotList);
        }
        snapshot._untilEndOfBattleActionProxies.addAll(_untilEndOfBattleActionProxies);
        if (!_untilEndOfDuelActionProxies.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with UntilEndOfDuelActionProxies");
        }
        if (!_untilEndOfLightsaberCombatActionProxies.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with UntilEndOfLightsaberCombatActionProxies");
        }
        if (!_untilEndOfWeaponFiringActionProxies.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with UntilEndOfWeaponFiringActionProxies");
        }
        if (!_untilEndOfDrawDestinyActionProxies.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with UntilEndOfDrawDestinyActionProxies");
        }
        if (!_untilEndOfBlowAwayActionProxies.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with UntilEndOfBlowAwayActionProxies");
        }
        if (!_effectResults.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with EffectResults");
        }
    }

    /**
     * Creates the actions environment.
     * @param swccgGame the game
     * @param actionStack the action stack
     */
    public DefaultActionsEnvironment(SwccgGame swccgGame, ActionStack actionStack) {
        _swccgGame = swccgGame;
        _actionStack = actionStack;

        // Note: Can add action proxies need for the entire game here.
    }

    @Override
    public void emitEffectResult(EffectResult effectResult) {
        _effectResults.add(effectResult);
    }

    @Override
    public Set<EffectResult> consumeEffectResults() {
        Set<EffectResult> result = _effectResults;
        _effectResults = new HashSet<EffectResult>();
        return result;
    }

    @Override
    public void addUntilEndOfGameActionProxy(ActionProxy actionProxy) {
        _actionProxies.add(actionProxy);
    }

    /**
     * Adds an action proxy until the end of the turn.
     * @param actionProxy the action proxy
     */
    @Override
    public void addUntilEndOfTurnActionProxy(ActionProxy actionProxy) {
        _actionProxies.add(actionProxy);
        if (_untilEndOfTurnActionProxies == null) {
            _untilEndOfTurnActionProxies = new LinkedList<ActionProxy>();
        }
        _untilEndOfTurnActionProxies.add(actionProxy);
    }

    @Override
    public void addUntilEndOfPlayersNextTurnActionProxy(ActionProxy actionProxy, String playerId) {
        _actionProxies.add(actionProxy);
        List<ActionProxy> list = _untilEndOfPlayersNextTurnActionProxies.get(playerId);
        if (list == null) {
            list = new LinkedList<ActionProxy>();
            _untilEndOfPlayersNextTurnActionProxies.put(playerId, list);
        }
        list.add(actionProxy);
    }

    @Override
    public void removeEndOfTurnActionProxies() {
        _actionProxies.removeAll(_untilEndOfTurnActionProxies);
        _untilEndOfTurnActionProxies.clear();

        // Move actionProxies in "until end of player's next turn" list for the next player
        // to the "until end of turn" list since that player's turn is about to start
        String nextPlayer = _swccgGame.getOpponent(_swccgGame.getGameState().getCurrentPlayerId());
        List<ActionProxy> nextTurnActionProxies = _untilEndOfPlayersNextTurnActionProxies.get(nextPlayer);
        if (nextTurnActionProxies != null) {
            _untilEndOfTurnActionProxies.addAll(nextTurnActionProxies);
            _untilEndOfPlayersNextTurnActionProxies.remove(nextPlayer);
        }
    }

    @Override
    public void addUntilEndOfBattleActionProxy(ActionProxy actionProxy) {
        if (!_swccgGame.getGameState().isDuringBattle()) {
            throw new UnsupportedOperationException("Adding until end of battle action proxy outside of battle");
        }
        _actionProxies.add(actionProxy);
        _untilEndOfBattleActionProxies.add(actionProxy);
    }

    @Override
    public void removeEndOfBattleActionProxies() {
        _actionProxies.removeAll(_untilEndOfBattleActionProxies);
        _untilEndOfBattleActionProxies.clear();
    }

    @Override
    public void addUntilEndOfDuelActionProxy(ActionProxy actionProxy) {
        if (!_swccgGame.getGameState().isDuringDuel()) {
            throw new UnsupportedOperationException("Adding until end of duel action proxy outside of duel");
        }
        _actionProxies.add(actionProxy);
        _untilEndOfDuelActionProxies.add(actionProxy);
    }

    @Override
    public void removeEndOfDuelActionProxies() {
        _actionProxies.removeAll(_untilEndOfDuelActionProxies);
        _untilEndOfDuelActionProxies.clear();
    }

    /**
     * Adds an action proxy until the end of the current lightsaber combat.
     * @param actionProxy the action proxy
     */
    @Override
    public void addUntilEndOfLightsaberCombatActionProxy(ActionProxy actionProxy) {
        if (!_swccgGame.getGameState().isDuringLightsaberCombat()) {
            throw new UnsupportedOperationException("Adding until end of lightsaber combat action proxy outside of lightsaber combat");
        }
        _actionProxies.add(actionProxy);
        _untilEndOfLightsaberCombatActionProxies.add(actionProxy);
    }

    /**
     * Removes any action proxies that are supposed to expire at the end of the current lightsaber combat.
     */
    @Override
    public void removeEndOfLightsaberCombatActionProxies() {
        _actionProxies.removeAll(_untilEndOfLightsaberCombatActionProxies);
        _untilEndOfLightsaberCombatActionProxies.clear();
    }

    @Override
    public void addUntilEndOfWeaponFiringActionProxy(ActionProxy actionProxy) {
        _actionProxies.add(actionProxy);
        _untilEndOfWeaponFiringActionProxies.add(actionProxy);
    }

    @Override
    public void removeEndOfWeaponFiringActionProxies() {
        _actionProxies.removeAll(_untilEndOfWeaponFiringActionProxies);
        _untilEndOfWeaponFiringActionProxies.clear();
    }

    @Override
    public void addUntilEndOfDrawDestinyActionProxy(ActionProxy actionProxy) {
        DrawDestinyState currentDrawDestinyState = _swccgGame.getGameState().getTopDrawDestinyState();
        if (currentDrawDestinyState == null) {
            throw new UnsupportedOperationException("Adding until end of draw destiny action proxy outside of draw destiny.");
        }
        List<ActionProxy> list = _untilEndOfDrawDestinyActionProxies.get(currentDrawDestinyState.getId());
        if (list == null) {
            list = new LinkedList<ActionProxy>();
            _untilEndOfDrawDestinyActionProxies.put(currentDrawDestinyState.getId(), list);
        }
        list.add(actionProxy);
        _actionProxies.add(actionProxy);
    }

    @Override
    public void removeEndOfDrawDestinyActionProxies() {
        DrawDestinyState currentDrawDestinyState = _swccgGame.getGameState().getTopDrawDestinyState();
        Integer key = currentDrawDestinyState.getId();
        List<ActionProxy> list = _untilEndOfDrawDestinyActionProxies.get(key);
        if (list != null) {
            _actionProxies.removeAll(list);
            _untilEndOfDrawDestinyActionProxies.remove(key);
        }
    }

    @Override
    public void addUntilEndOfBlowAwayActionProxy(ActionProxy actionProxy) {
        BlowAwayState currentBlowAwayState = _swccgGame.getGameState().getTopBlowAwayState();
        if (currentBlowAwayState == null) {
            throw new UnsupportedOperationException("Adding until end of blow away action proxy outside of blow away.");
        }
        List<ActionProxy> list = _untilEndOfBlowAwayActionProxies.get(currentBlowAwayState.getId());
        if (list == null) {
            list = new LinkedList<ActionProxy>();
            _untilEndOfBlowAwayActionProxies.put(currentBlowAwayState.getId(), list);
        }
        list.add(actionProxy);
        _actionProxies.add(actionProxy);
    }

    @Override
    public void removeEndOfBlowAwayActionProxies() {
        BlowAwayState currentBlowAwayState = _swccgGame.getGameState().getTopBlowAwayState();
        Integer key = currentBlowAwayState.getId();
        List<ActionProxy> list = _untilEndOfBlowAwayActionProxies.get(key);
        if (list != null) {
            _actionProxies.removeAll(list);
            _untilEndOfBlowAwayActionProxies.remove(key);
        }
    }

    @Override
    public List<TriggerAction> getRequiredBeforeTriggers(Effect effect) {
        List<TriggerAction> actionList = new LinkedList<TriggerAction>();

        GatherRequiredBeforeTriggers gatherActions = new GatherRequiredBeforeTriggers(effect);
        _swccgGame.getGameState().iterateCardsWithRequiredActions(gatherActions);
        actionList.addAll(gatherActions.getActions());

        // If during start of game, also collect actions from 'outside of deck' cards
        if (_swccgGame.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS) {
            GatherOutsideOfDeckRequiredBeforeTriggers gatherOutsideOfDeckActions = new GatherOutsideOfDeckRequiredBeforeTriggers(effect);
            _swccgGame.getGameState().iterateOutsideOfDeckCardsWithRequiredActions(gatherOutsideOfDeckActions);
            actionList.addAll(gatherOutsideOfDeckActions.getActions());
        }

        // Required triggers from Interrupt card itself being played
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            RespondablePlayingCardEffect playCardEffect = (RespondablePlayingCardEffect) effect;
            if (!playCardEffect.isCanceled()) {
                PhysicalCard cardPlayed = playCardEffect.getCard();
                if (cardPlayed != null && cardPlayed.getBlueprint().getCardCategory() == CardCategory.INTERRUPT) {
                    List<TriggerAction> actions = cardPlayed.getBlueprint().getRequiredInterruptPlayedTriggers(_swccgGame, effect, cardPlayed);
                    if (actions != null) {
                        actionList.addAll(actions);
                    }
                }
            }
        }

        // Get action proxies
        for (ActionProxy actionProxy : _actionProxies) {
            List<TriggerAction> actions = actionProxy.getRequiredBeforeTriggers(_swccgGame, effect);
            if (actions != null) {
                actionList.addAll(actions);
            }
        }

        return actionList;
    }

    @Override
    public List<TriggerAction> getOptionalBeforeTriggers(String playerId, Effect effect) {
        List<TriggerAction> actionList = new LinkedList<TriggerAction>();

        // Gather actions from owners cards and locations
        GatherOptionalBeforeTriggers gatherActions = new GatherOptionalBeforeTriggers(playerId, effect);
        _swccgGame.getGameState().iterateCardsWithOptionalActions(gatherActions, playerId, false);
        actionList.addAll(gatherActions.getActions());

        // Gather actions from opponent's cards
        GatherOpponentsCardOptionalBeforeTriggers gatherOpponentsActions = new GatherOpponentsCardOptionalBeforeTriggers(playerId, effect);
        _swccgGame.getGameState().iterateOpponentsCardsWithOptionalActions(gatherOpponentsActions, playerId);
        actionList.addAll(gatherOpponentsActions.getActions());

        return actionList;
    }

    @Override
    public List<Action> getOptionalBeforeActions(String playerId, Effect effect) {
        List<Action> allActions = new LinkedList<Action>();

        GatherOptionalBeforeActions gatherActions = new GatherOptionalBeforeActions(playerId, effect);
        _swccgGame.getGameState().iterateCardsWithOptionalActions(gatherActions, playerId, true);

        allActions.addAll(gatherActions.getActions());

        return allActions;
    }

    @Override
    public Map<TriggerAction, EffectResult> getRequiredAfterTriggers(Collection<? extends EffectResult> effectResults) {
        Map<TriggerAction, EffectResult> allActions = new HashMap<TriggerAction, EffectResult>();

        GatherRequiredAfterTriggers gatherActions = new GatherRequiredAfterTriggers(effectResults);
        _swccgGame.getGameState().iterateCardsWithRequiredActions(gatherActions);
        allActions.putAll(gatherActions.getActions());

        // If during start of game, also collect actions from 'outside of deck' cards
        if (_swccgGame.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS) {
            GatherOutsideOfDeckRequiredAfterTriggers gatherOutsideOfDeckActions = new GatherOutsideOfDeckRequiredAfterTriggers(effectResults);
            _swccgGame.getGameState().iterateOutsideOfDeckCardsWithRequiredActions(gatherOutsideOfDeckActions);
            allActions.putAll(gatherOutsideOfDeckActions.getActions());
        }

        // Required triggers from card drawn as destiny
        for (EffectResult effectResult : effectResults) {
            if (effectResult.isAcceptingResponses()) {
                if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
                    DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
                    if (!destinyDrawnResult.isCanceled()) {
                        PhysicalCard cardDrawn = destinyDrawnResult.getCard();
                        if (cardDrawn != null) {
                            List<TriggerAction> actions = cardDrawn.getBlueprint().getRequiredDrawnAsDestinyTriggers(_swccgGame, effectResult, cardDrawn);
                            if (actions != null) {
                                for (TriggerAction action : actions) {
                                    if (action.isRepeatableTrigger() || !effectResult.wasTriggerUsed(action)) {
                                        // If singleton trigger, then only have one with same trigger id (regardless of effect result)
                                        if (action.isSingletonTrigger()) {
                                            boolean foundMatch = false;
                                            for (TriggerAction existingAction : allActions.keySet()) {
                                                if (existingAction.getTriggerIdentifier(false).equals(action.getTriggerIdentifier(false))) {
                                                    foundMatch = true;
                                                    break;
                                                }
                                            }
                                            if (!foundMatch) {
                                                allActions.put(action, effectResult);
                                            }
                                        }
                                        else {
                                            allActions.put(action, effectResult);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Get action proxies
        for (ActionProxy actionProxy : _actionProxies) {
            for (EffectResult effectResult : effectResults) {
                if (effectResult.isAcceptingResponses()) {
                    List<TriggerAction> actions = actionProxy.getRequiredAfterTriggers(_swccgGame, effectResult);
                    if (actions != null) {
                        for (TriggerAction action : actions) {
                            if (action.isRepeatableTrigger() || !effectResult.wasTriggerUsed(action)) {
                                // If singleton trigger, then only have one with same trigger id (regardless of effect result)
                                if (action.isSingletonTrigger()) {
                                    boolean foundMatch = false;
                                    for (TriggerAction existingAction : allActions.keySet()) {
                                        if (existingAction.getTriggerIdentifier(false).equals(action.getTriggerIdentifier(false))) {
                                            foundMatch = true;
                                            break;
                                        }
                                    }
                                    if (!foundMatch) {
                                        allActions.put(action, effectResult);
                                    }
                                }
                                else {
                                    allActions.put(action, effectResult);
                                }
                            }
                        }
                    }
                }
            }
        }

        return allActions;
    }

    @Override
    public Map<TriggerAction, EffectResult> getOptionalAfterTriggers(String playerId, Collection<? extends EffectResult> effectResults) {

        // Gather actions from owners cards and locations
        GatherOptionalAfterTriggers gatherActions = new GatherOptionalAfterTriggers(playerId, effectResults);
        _swccgGame.getGameState().iterateCardsWithOptionalActions(gatherActions, playerId, false);

        // Gather actions from opponents cards
        GatherOpponentsCardOptionalAfterTriggers gatherOpponentsActions = new GatherOpponentsCardOptionalAfterTriggers(playerId, effectResults);
        _swccgGame.getGameState().iterateOpponentsCardsWithOptionalActions(gatherOpponentsActions, playerId);

        Map<TriggerAction, EffectResult> allActions = new HashMap<TriggerAction, EffectResult>();
        allActions.putAll(gatherActions.getActions());
        allActions.putAll(gatherOpponentsActions.getActions());

        if (effectResults != null) {

            // Optional triggers from card drawn as destiny
            for (EffectResult effectResult : effectResults) {
                if (effectResult.isAcceptingResponses()) {
                    if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
                        DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
                        if (!destinyDrawnResult.isCanceled()) {
                            PhysicalCard cardDrawn = destinyDrawnResult.getCard();
                            if (cardDrawn != null) {
                                if (effectResult.getPerformingPlayerId().equals(playerId)) {
                                    List<TriggerAction> actions = cardDrawn.getBlueprint().getOptionalDrawnAsDestinyTriggers(playerId, _swccgGame, effectResult, cardDrawn);
                                    if (actions != null) {
                                        for (TriggerAction action : actions) {
                                            if (action.isRepeatableTrigger() || !effectResult.wasTriggerUsed(action)) {
                                                // If singleton trigger, then only have one with same trigger id (regardless of effect result)
                                                if (action.isSingletonTrigger()) {
                                                    boolean foundMatch = false;
                                                    for (TriggerAction existingAction : allActions.keySet()) {
                                                        if (existingAction.getTriggerIdentifier(false).equals(action.getTriggerIdentifier(false))) {
                                                            foundMatch = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!foundMatch) {
                                                        allActions.put(action, effectResult);
                                                    }
                                                } else {
                                                    allActions.put(action, effectResult);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Get action proxies
            for (ActionProxy actionProxy : _actionProxies) {
                for (EffectResult effectResult : effectResults) {
                    if (effectResult.isAcceptingResponses()) {
                        List<TriggerAction> actions = actionProxy.getOptionalAfterTriggers(playerId, _swccgGame, effectResult);
                        if (actions != null) {
                            for (TriggerAction action : actions) {
                                action.setAllowAbort(true);
                                if (action.isRepeatableTrigger() || !effectResult.wasTriggerUsed(action)) {
                                    PhysicalCard card = action.getActionAttachedToCard();
                                    if (card != null && (!card.getZone().isInPlay() && card.getZone() != Zone.STACKED)) {
                                        action.setOptionalOffTableCardAction(true);
                                    }
                                    // If singleton trigger, then only have one with same trigger id (regardless of effect result)
                                    if (action.isSingletonTrigger()) {
                                        boolean foundMatch = false;
                                        for (TriggerAction existingAction : allActions.keySet()) {
                                            if (Objects.equal(existingAction.getTriggerIdentifier(false), action.getTriggerIdentifier(false))
                                                    && Objects.equal(existingAction.getText(), action.getText())) {
                                                foundMatch = true;
                                                break;
                                            }
                                        }
                                        if (!foundMatch) {
                                            allActions.put(action, effectResult);
                                        }
                                    }
                                    else {
                                        allActions.put(action, effectResult);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // For drawing destiny gather any triggers from the card performing the destiny draw when calculating total destiny
            for (EffectResult effectResult : effectResults) {
                if (effectResult.isAcceptingResponses()) {
                    if (effectResult.getType() == EffectResult.Type.DRAWING_DESTINY_COMPLETE) {
                        DrawDestinyEffect drawDestinyEffect = _swccgGame.getGameState().getTopDrawDestinyState().getDrawDestinyEffect();
                        if (drawDestinyEffect != null && playerId.equals(drawDestinyEffect.getPlayerDrawingDestiny())
                                && drawDestinyEffect.getAction().getActionSource() != null) {
                            List<TriggerAction> actions = drawDestinyEffect.getOptionalTotalDestinyTriggers(playerId, _swccgGame, effectResult, drawDestinyEffect.getAction().getActionSource());
                            if (actions != null) {
                                for (TriggerAction action : actions) {
                                    action.setAllowAbort(true);
                                    if (action.isRepeatableTrigger() || !effectResult.wasTriggerUsed(action)) {
                                        // If singleton trigger, then only have one with same trigger id (regardless of effect result)
                                        if (action.isSingletonTrigger()) {
                                            boolean foundMatch = false;
                                            for (TriggerAction existingAction : allActions.keySet()) {
                                                if (Objects.equal(existingAction.getTriggerIdentifier(false), action.getTriggerIdentifier(false))
                                                        && Objects.equal(existingAction.getText(), action.getText())) {
                                                    foundMatch = true;
                                                    break;
                                                }
                                            }
                                            if (!foundMatch) {
                                                allActions.put(action, effectResult);
                                            }
                                        }
                                        else {
                                            allActions.put(action, effectResult);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Action gatheredAction : allActions.keySet())
            gatheredAction.setPerformingPlayer(playerId);

        return allActions;
    }

    @Override
    public List<Action> getOptionalAfterActions(String playerId, Collection<? extends EffectResult> effectResults) {
        GatherOptionalAfterActions gatherAfterActions = new GatherOptionalAfterActions(playerId, effectResults);
        _swccgGame.getGameState().iterateCardsWithOptionalActions(gatherAfterActions, playerId, true);

        List<Action> allActions = new LinkedList<Action>();

        for (Action action : gatherAfterActions.getActions()) {
            action.setPerformingPlayer(playerId);
            allActions.add(action);
        }

        return allActions;
    }

    @Override
    public List<Action> getTopLevelActions(String playerId) {
        List<Action> allActions = new LinkedList<Action>();

        // Gather actions from owner's cards and locations
        GatherTopLevelActions visitor = new GatherTopLevelActions(playerId);
        _swccgGame.getGameState().iterateCardsWithOptionalActions(visitor, playerId, true);
        allActions.addAll(visitor.getActions());

        // Gather actions from opponents cards
        GatherOpponentsCardPhaseActionsVisitor gatherOpponentsActions = new GatherOpponentsCardPhaseActionsVisitor(playerId);
        _swccgGame.getGameState().iterateOpponentsCardsWithOptionalActions(gatherOpponentsActions, playerId);
        allActions.addAll(gatherOpponentsActions.getActions());

        // Gather actions from card piles
        GatherCardPilePhaseActionsVisitor cardPileVisitor = new GatherCardPilePhaseActionsVisitor(playerId);
        _swccgGame.getGameState().iterateCardsWithCardPileActions(cardPileVisitor, playerId);
        allActions.addAll(cardPileVisitor.getActions());

        return allActions;
    }

    @Override
    public void addActionToStack(Action action) {
        _actionStack.stackAction(action);
    }


    /**
     * A card visitor that gathers the required "before" responses to an effect.
     */
    private class GatherRequiredBeforeTriggers extends CompletePhysicalCardVisitor {
        private Effect _effect;
        private List<TriggerAction> _actions = new LinkedList<TriggerAction>();

        /**
         * Creates a card visitor that gathers the required "before" responses to the specified effect.
         * @param effect the effect
         */
        private GatherRequiredBeforeTriggers(Effect effect) {
            _effect = effect;
        }

        @Override
        public void doVisitPhysicalCard(PhysicalCard physicalCard) {
            // Checking if game text is cancelled is done within the getRequiredBeforeTriggers method.
            List<TriggerAction> actions = physicalCard.getBlueprint().getRequiredBeforeTriggers(_swccgGame, _effect, physicalCard);
            if (actions != null) {
                _actions.addAll(actions);
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public List<TriggerAction> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the required "before" responses to an effect from 'outside of deck' cards.
     */
    private class GatherOutsideOfDeckRequiredBeforeTriggers extends CompletePhysicalCardVisitor {
        private Effect _effect;
        private List<TriggerAction> _actions = new LinkedList<TriggerAction>();

        /**
         * Creates a card visitor that gathers the required "before" responses to the specified effect from 'outside of deck' cards.
         * @param effect the effect
         */
        private GatherOutsideOfDeckRequiredBeforeTriggers(Effect effect) {
            _effect = effect;
        }

        @Override
        public void doVisitPhysicalCard(PhysicalCard physicalCard) {
            List<TriggerAction> actions = physicalCard.getBlueprint().getRequiredOutsideOfDeckBeforeTriggers(_swccgGame, _effect, physicalCard);
            if (actions != null) {
                _actions.addAll(actions);
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public List<TriggerAction> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the optional "before" responses for the specified player to an effect.
     */
    private class GatherOptionalBeforeTriggers extends CompletePhysicalCardVisitor {
        private String _playerId;
        private Effect _effect;
        private List<TriggerAction> _actions = new LinkedList<TriggerAction>();

        /**
         * Creates a card visitor that gathers the optional "before" responses for the specified player to the specified
         * effect.
         * @param playerId the player
         * @param effect the effect
         */
        private GatherOptionalBeforeTriggers(String playerId, Effect effect) {
            _playerId = playerId;
            _effect = effect;
        }

        @Override
        protected void doVisitPhysicalCard(PhysicalCard physicalCard) {
            // Checking if game text is cancelled is done within the getOptionalBeforeTriggers method.
            List<TriggerAction> actions = physicalCard.getBlueprint().getOptionalBeforeTriggers(_playerId, _swccgGame, _effect, physicalCard);
            if (actions != null) {
                for (Action action : actions) {
                    action.setAllowAbort(true);
                }
                _actions.addAll(actions);
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public List<TriggerAction> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the optional "before" responses (from opponent's cards) for the specified player to
     * an effect.
     */
    private class GatherOpponentsCardOptionalBeforeTriggers extends CompletePhysicalCardVisitor {
        private String _playerId;
        private Effect _effect;
        private List<TriggerAction> _actions = new LinkedList<TriggerAction>();

        /**
         * Creates a card visitor that gathers the optional "before" responses (from opponent's cards) for the specified
         * player to the specified effect.
         * @param playerId the player
         * @param effect the effect
         */
        private GatherOpponentsCardOptionalBeforeTriggers(String playerId, Effect effect) {
            _playerId = playerId;
            _effect = effect;
        }

        @Override
        public void doVisitPhysicalCard(PhysicalCard physicalCard) {
            // Checking if game text is cancelled is done within the getOptionalAfterTriggers method.
            List<TriggerAction> actions = physicalCard.getBlueprint().getOpponentsCardOptionalBeforeTriggers(_playerId, _swccgGame, _effect, physicalCard);
            if (actions != null) {
                for (Action action : actions) {
                    action.setAllowAbort(true);
                }
                _actions.addAll(actions);
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public List<TriggerAction> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the optional "before" response actions for the specified player to an effect.
     */
    private class GatherOptionalBeforeActions extends CompletePhysicalCardVisitor {
        private String _playerId;
        private Effect _effect;
        private List<Action> _actions = new LinkedList<Action>();

        /**
         * Creates a card visitor that gathers the optional "before" response actions for the specified player to an effect.
         * @param playerId the player
         * @param effect the effect
         */
        private GatherOptionalBeforeActions(String playerId, Effect effect) {
            _playerId = playerId;
            _effect = effect;
        }

        @Override
        public void doVisitPhysicalCard(PhysicalCard physicalCard) {
                List<Action> actions = physicalCard.getBlueprint().getOptionalBeforeActions(_playerId, _swccgGame, _effect, physicalCard);
                if (actions != null) {
                    for (Action action : actions) {
                        action.setAllowAbort(true);
                    }
                    _actions.addAll(actions);
                }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public List<Action> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the required "after" responses to any of the specified effect results from 'outside of deck' cards
     */
    private class GatherOutsideOfDeckRequiredAfterTriggers extends CompletePhysicalCardVisitor {
        private Collection<? extends EffectResult> _effectResults;
        private Map<TriggerAction, EffectResult> _actions = new HashMap<TriggerAction, EffectResult>();

        /**
         * Creates a card visitor that gathers the required "after" responses to an effect result from 'outside of deck' cards.
         * @param effectResults the effect results
         */
        private GatherOutsideOfDeckRequiredAfterTriggers(Collection<? extends EffectResult> effectResults) {
            _effectResults = effectResults;
        }

        @Override
        protected void doVisitPhysicalCard(PhysicalCard physicalCard) {
            // Checking if game text is cancelled is done within the getRequiredAfterTriggers method.
            for (EffectResult effectResult : _effectResults) {
                if (effectResult.isAcceptingResponses()) {
                    List<TriggerAction> actions = physicalCard.getBlueprint().getRequiredOutsideOfDeckAfterTriggers(_swccgGame, effectResult, physicalCard);
                    if (actions != null) {
                        for (TriggerAction action : actions) {
                            if (action.isRepeatableTrigger() || !effectResult.wasTriggerUsed(action)) {
                                // If singleton trigger, then only have one with same trigger id (regardless of effect result)
                                if (action.isSingletonTrigger()) {
                                    boolean foundMatch = false;
                                    for (TriggerAction existingAction : _actions.keySet()) {
                                        if (Objects.equal(existingAction.getTriggerIdentifier(false), action.getTriggerIdentifier(false))
                                                && Objects.equal(existingAction.getText(), action.getText())) {
                                            foundMatch = true;
                                            break;
                                        }
                                    }
                                    if (!foundMatch) {
                                        _actions.put(action, effectResult);
                                    }
                                }
                                else {
                                    _actions.put(action, effectResult);
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public Map<TriggerAction, EffectResult> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the required "after" responses to any of the specified effect results.
     */
    private class GatherRequiredAfterTriggers extends CompletePhysicalCardVisitor {
        private Collection<? extends EffectResult> _effectResults;
        private Map<TriggerAction, EffectResult> _actions = new HashMap<TriggerAction, EffectResult>();

        /**
         * Creates a card visitor that gathers the required "after" responses to an effect result.
         * @param effectResults the effect results
         */
        private GatherRequiredAfterTriggers(Collection<? extends EffectResult> effectResults) {
            _effectResults = effectResults;
        }

        @Override
        protected void doVisitPhysicalCard(PhysicalCard physicalCard) {
            // Checking if game text is cancelled is done within the getRequiredAfterTriggers method.
            for (EffectResult effectResult : _effectResults) {
                if (effectResult.isAcceptingResponses()) {
                    List<TriggerAction> actions = physicalCard.getBlueprint().getRequiredAfterTriggers(_swccgGame, effectResult, physicalCard);
                    if (actions != null) {
                        for (TriggerAction action : actions) {
                            if (action.isRepeatableTrigger() || !effectResult.wasTriggerUsed(action)) {
                                // If singleton trigger, then only have one with same trigger id (regardless of effect result)
                                if (action.isSingletonTrigger()) {
                                    boolean foundMatch = false;
                                    for (TriggerAction existingAction : _actions.keySet()) {
                                        if (Objects.equal(existingAction.getTriggerIdentifier(false), action.getTriggerIdentifier(false))
                                                && Objects.equal(existingAction.getText(), action.getText())) {
                                            foundMatch = true;
                                            break;
                                        }
                                    }
                                    if (!foundMatch) {
                                        _actions.put(action, effectResult);
                                    }
                                }
                                else {
                                    _actions.put(action, effectResult);
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public Map<TriggerAction, EffectResult> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the optional "after" responses for the specified player to any of the specified effect
     * results.
     */
    private class GatherOptionalAfterTriggers extends CompletePhysicalCardVisitor {
        private String _playerId;
        private Collection<? extends EffectResult> _effectResults;
        private Map<TriggerAction, EffectResult> _actions = new HashMap<TriggerAction, EffectResult>();

        /**
         * Creates a card visitor that gathers the optional "after" responses for the specified player to any of the specified
         * effect results.
         * @param playerId the player
         * @param effectResults the effect result
         */
        private GatherOptionalAfterTriggers(String playerId, Collection<? extends EffectResult> effectResults) {
            _playerId = playerId;
            _effectResults = effectResults;
        }

        @Override
        protected void doVisitPhysicalCard(PhysicalCard physicalCard) {
            // Checking if game text is cancelled is done within the getOptionalAfterTriggers method.
            for (EffectResult effectResult : _effectResults) {
                if (effectResult.isAcceptingResponses()) {
                    List<TriggerAction> actions = physicalCard.getBlueprint().getOptionalAfterTriggers(_playerId, _swccgGame, effectResult, physicalCard);
                    if (actions != null) {
                        for (TriggerAction action : actions) {
                            action.setAllowAbort(true);
                            if (action.isRepeatableTrigger() || !effectResult.wasTriggerUsed(action)) {
                                // If singleton trigger, then only have one with same trigger id (regardless of effect result)
                                if (action.isSingletonTrigger()) {
                                    boolean foundMatch = false;
                                    for (TriggerAction existingAction : _actions.keySet()) {
                                        if (Objects.equal(existingAction.getTriggerIdentifier(false), action.getTriggerIdentifier(false))
                                                && Objects.equal(existingAction.getText(), action.getText())) {
                                            foundMatch = true;
                                            break;
                                        }
                                    }
                                    if (!foundMatch) {
                                        _actions.put(action, effectResult);
                                    }
                                }
                                else {
                                    _actions.put(action, effectResult);
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public Map<TriggerAction, EffectResult> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the optional "after" responses (from opponent's cards) for the specified player to any
     * of the specified effect results.
     */
    private class GatherOpponentsCardOptionalAfterTriggers extends CompletePhysicalCardVisitor {
        private String _playerId;
        private Collection<? extends EffectResult> _effectResults;
        private Map<TriggerAction, EffectResult> _actions = new HashMap<TriggerAction, EffectResult>();

        /**
         * Creates a card visitor that gathers the optional "after" responses (from opponent's cards) for the specified
         * player to any of the specified effect results.
         * @param playerId the player
         * @param effectResults the effect results
         */
        private GatherOpponentsCardOptionalAfterTriggers(String playerId, Collection<? extends EffectResult> effectResults) {
            _playerId = playerId;
            _effectResults = effectResults;
        }

        @Override
        protected void doVisitPhysicalCard(PhysicalCard physicalCard) {
            // Checking if game text is cancelled is done within the getOpponentsCardOptionalAfterTriggers method.
            for (EffectResult effectResult : _effectResults) {
                if (effectResult.isAcceptingResponses()) {
                    List<TriggerAction> actions = physicalCard.getBlueprint().getOpponentsCardOptionalAfterTriggers(_playerId, _swccgGame, effectResult, physicalCard);
                    if (actions != null) {
                        for (TriggerAction action : actions) {
                            action.setAllowAbort(true);
                            if (action.isRepeatableTrigger() || !effectResult.wasTriggerUsed(action)) {
                                // If singleton trigger, then only have one with same trigger id (regardless of effect result)
                                if (action.isSingletonTrigger()) {
                                    boolean foundMatch = false;
                                    for (TriggerAction existingAction : _actions.keySet()) {
                                        if (Objects.equal(existingAction.getTriggerIdentifier(false), action.getTriggerIdentifier(false))
                                                && Objects.equal(existingAction.getText(), action.getText())) {
                                            foundMatch = true;
                                            break;
                                        }
                                    }
                                    if (!foundMatch) {
                                        _actions.put(action, effectResult);
                                    }
                                }
                                else {
                                    _actions.put(action, effectResult);
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public Map<TriggerAction, EffectResult> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the optional "after" response actions for the specified player to any of the specified
     * effect results.
     */
    private class GatherOptionalAfterActions extends CompletePhysicalCardVisitor {
        private String _playerId;
        private Collection<? extends EffectResult> _effectResults;
        private List<Action> _actions = new LinkedList<Action>();

        /**
         * Creates a card visitor that gathers the optional "after" response actions for the specified player to any of
         * the specified effect results.
         * @param playerId the player
         * @param effectResults the effect results
         */
        private GatherOptionalAfterActions(String playerId, Collection<? extends EffectResult> effectResults) {
            _playerId = playerId;
            _effectResults = effectResults;
        }

        @Override
        protected void doVisitPhysicalCard(PhysicalCard physicalCard) {
            for (EffectResult effectResult : _effectResults) {
                List<Action> actions = physicalCard.getBlueprint().getOptionalAfterActions(_playerId, _swccgGame, effectResult, physicalCard);
                if (actions != null) {
                    for (Action action : actions) {
                        action.setAllowAbort(true);
                    }
                    _actions.addAll(actions);
                }
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public List<Action> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the top-level actions for the specified player.
     */
    private class GatherTopLevelActions extends CompletePhysicalCardVisitor {
        private String _playerId;
        private List<Action> _actions = new LinkedList<Action>();

        /**
         * Creates a card visitor that gathers the top-level actions for the specified player.
         * @param playerId the player
         */
        public GatherTopLevelActions(String playerId) {
            _playerId = playerId;
        }

        @Override
        protected void doVisitPhysicalCard(PhysicalCard physicalCard) {
            if (_swccgGame.getGameState().isDuringAttackRun()) {
                // Checking if game text is cancelled is done within the getTopLevelAttackRunActions method.
                List<Action> actions = physicalCard.getBlueprint().getTopLevelAttackRunActions(_playerId, _swccgGame, physicalCard);
                if (actions != null) {
                    for (Action action : actions) {
                        action.setAllowAbort(true);
                    }
                    _actions.addAll(actions);
                }
            }
            else {
                // Checking if game text is cancelled is done within the getTopLevelActions method.
                List<Action> actions = physicalCard.getBlueprint().getTopLevelActions(_playerId, _swccgGame, physicalCard);
                if (actions != null) {
                    for (Action action : actions) {
                        action.setAllowAbort(true);
                    }
                    _actions.addAll(actions);
                }
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public List<Action> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the top-level actions for the specified player (from opponent's cards).
     */
    private class GatherOpponentsCardPhaseActionsVisitor extends CompletePhysicalCardVisitor {
        private String _playerId;
        private List<Action> _actions = new LinkedList<Action>();

        /**
         * Creates a card visitor that gathers the top-level actions for the specified player (from opponent's cards).
         * @param playerId the player
         */
        public GatherOpponentsCardPhaseActionsVisitor(String playerId) {
            _playerId = playerId;
        }

        @Override
        protected void doVisitPhysicalCard(PhysicalCard physicalCard) {
            if (!_swccgGame.getGameState().isDuringAttackRun()) {
                // Checking if game text is cancelled is done within the getOpponentsCardTopLevelActions method.
                List<Action> actions = physicalCard.getBlueprint().getOpponentsCardTopLevelActions(_playerId, _swccgGame, physicalCard);
                if (actions != null) {
                    for (Action action : actions) {
                        action.setAllowAbort(true);
                    }
                    _actions.addAll(actions);
                }
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public List<Action> getActions() {
            return _actions;
        }
    }

    /**
     * A card visitor that gathers the top-level card pile actions for the specified player.
     */
    private class GatherCardPilePhaseActionsVisitor extends CompletePhysicalCardVisitor {
        private String _playerId;
        private List<Action> _actions = new LinkedList<Action>();

        /**
         * Creates a card visitor that gathers the top-level card pile actions for the specified player.
         * @param playerId the player
         */
        public GatherCardPilePhaseActionsVisitor(String playerId) {
            _playerId = playerId;
        }

        @Override
        protected void doVisitPhysicalCard(PhysicalCard physicalCard) {
            if (!_swccgGame.getGameState().isDuringAttackRun()) {
                List<Action> cardPileActions = physicalCard.getBlueprint().getCardPilePhaseActions(_playerId, _swccgGame, physicalCard);
                if (cardPileActions != null) {
                    for (Action action : cardPileActions) {
                        if (action != null) {
                            _actions.add(action);
                        }
                    }
                }
            }
        }

        /**
         * Gets the actions.
         * @return the actions
         */
        public List<Action> getActions() {
            return _actions;
        }
    }
}
