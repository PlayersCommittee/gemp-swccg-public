package com.gempukku.swccgo.game;

import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This interface represents the way to get the actions that can be (or must be) performed at the current game state, as
 * well as a way to "schedule" actions to automatically trigger until a given game state is reached, and add an Action or
 * EffectResult on the stack to be performed.
 */
public interface ActionsEnvironment extends Snapshotable<ActionsEnvironment> {

    /**
     * Gets the top-level actions that can be performed by the specified player.
     * @param playerId the player
     * @return the actions
     */
    List<Action> getTopLevelActions(String playerId);

    /**
     * Gets the required trigger actions that must be performed before the specified effect.
     * @param effect the effect
     * @return the trigger actions
     */
    List<TriggerAction> getRequiredBeforeTriggers(Effect effect);

    /**
     * Gets the optional trigger actions that may be performed by the specified player before the specified effect.
     * @param playerId the player
     * @param effect the effect.
     * @return the trigger actions
     */
    List<TriggerAction> getOptionalBeforeTriggers(String playerId, Effect effect);

    /**
     * Gets the optional play card actions that may be performed by the specified player before the specified effect.
     * @param playerId the player
     * @param effect the effect
     * @return the play card actions
     */
    List<Action> getOptionalBeforeActions(String playerId, Effect effect);

    /**
     * Gets the trigger actions (and effect result triggering each action) that must be performed after the specified
     * effect results.
     * @param effectResults the effect results
     * @return the map of trigger actions (and effect result triggering each action)
     */
    Map<TriggerAction, EffectResult> getRequiredAfterTriggers(Collection<? extends EffectResult> effectResults);

    /**
     * Gets the trigger actions (and effect result triggering each action) that may be performed by the specified player
     * after the specified effect results.
     * @param effectResults the effect results
     * @return the map of trigger actions (and effect result triggering each action)
     */
    Map<TriggerAction, EffectResult> getOptionalAfterTriggers(String playerId, Collection<? extends EffectResult> effectResults);

    /**
     * Gets the optional play card actions that may be performed by the specified player after the specified effect results.
     * @param playerId the player
     * @param effectResults the effect results
     * @return the play card actions
     */
    List<Action> getOptionalAfterActions(String playerId, Collection<? extends EffectResult> effectResults);

    /**
     * Adds an action proxy until the end of the game.
     * @param actionProxy the action proxy
     */
    void addUntilEndOfGameActionProxy(ActionProxy actionProxy);

    /**
     * Adds an action proxy until the end of the turn.
     * @param actionProxy the action proxy
     */
    void addUntilEndOfTurnActionProxy(ActionProxy actionProxy);

    /**
     * Adds an action proxy until the end of the specified player's next turn.
     * @param actionProxy the action proxy
     * @param playerId the player
     */
    void addUntilEndOfPlayersNextTurnActionProxy(ActionProxy actionProxy, String playerId);

    /**
     * Adds an action proxy until the end of the current battle.
     * @param actionProxy the action proxy
     */
    void addUntilEndOfBattleActionProxy(ActionProxy actionProxy);

    /**
     * Adds an action proxy until the end of the current duel.
     * @param actionProxy the action proxy
     */
    void addUntilEndOfDuelActionProxy(ActionProxy actionProxy);

    /**
     * Adds an action proxy until the end of the current lightsaber combat.
     * @param actionProxy the action proxy
     */
    void addUntilEndOfLightsaberCombatActionProxy(ActionProxy actionProxy);

   /**
     * Adds an action proxy until the end of the current weapon firing.
     * @param actionProxy the action proxy
     */
    void addUntilEndOfWeaponFiringActionProxy(ActionProxy actionProxy);

    /**
     * Adds an action proxy until the end of the current draw destiny process.
     * @param actionProxy the action proxy
     */
    void addUntilEndOfDrawDestinyActionProxy(ActionProxy actionProxy);

    /**
     * Adds an action proxy until the end of the current blow away process.
     * @param actionProxy the action proxy
     */
    void addUntilEndOfBlowAwayActionProxy(ActionProxy actionProxy);

    /**
     * Removes any action proxies that are supposed to expire at the end of the current turn.
     */
    void removeEndOfTurnActionProxies();

    /**
     * Removes any action proxies that are supposed to expire at the end of the current battle.
     */
    void removeEndOfBattleActionProxies();

    /**
     * Removes any action proxies that are supposed to expire at the end of the current duel.
     */
    void removeEndOfDuelActionProxies();

    /**
     * Removes any action proxies that are supposed to expire at the end of the current lightsaber combat.
     */
    void removeEndOfLightsaberCombatActionProxies();

    /**
     * Removes any action proxies that are supposed to expire at the end of the current weapon firing.
     */
    void removeEndOfWeaponFiringActionProxies();

    /**
     * Removes any action proxies that are supposed to expire at the end of the current draw destiny process.
     */
    void removeEndOfDrawDestinyActionProxies();

    /**
     * Removes any action proxies that are supposed to expire at the end of the current blow away process.
     */
    void removeEndOfBlowAwayActionProxies();

    /**
     * Adds the action to the action stack.
     * @param action the action
     */
    void addActionToStack(Action action);

    /**
     * Adds the effect result to the effect results to be triggered.
     * @param effectResult the effect result
     */
    void emitEffectResult(EffectResult effectResult);

    /**
     * Removes and returns all of effect results from the set of effect results to be triggered.
     * @return the effect results to be triggered
     */
    Set<EffectResult> consumeEffectResults();
}
