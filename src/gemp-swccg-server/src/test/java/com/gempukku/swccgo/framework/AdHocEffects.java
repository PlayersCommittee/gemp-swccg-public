package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.CardPileAction;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import static org.junit.Assert.assertTrue;

/**
 * Sometimes, there just isn't a card that has the precise action or effect that you want to use (especially when
 * developing new mechanics), so these functions permit the tester to staple actions, effects, and modifiers onto
 * physical cards as needed.  This is also useful when testing fundamental existing mechanics without any
 * confounding variables.
 *
 * Be warned: just because an action or effect has been added to the game does not automatically mean that Gemp will
 * respect it or be aware of it just yet.  In nearly all cases, after you have used any of these functions you will
 * need to execute a decision so that Gemp's game loop will process what's on the table (at which point it will
 * become aware of the changes you have made).  While awaiting a decision Gemp is effectively paused and thus needs
 * that moment to become aware of changes.
 */
public interface AdHocEffects extends TestBase, Decisions {


	/**
	 * Staples a given modifier to the game state for the duration of the game.  This can be a good way to test elusive
	 * game states that would otherwise require a lot of setup.
	 * @param mod The modifier to permanently add to the game.
	 */
	default void ApplyAdHocModifier(Modifier mod)
    {
        game().getModifiersEnvironment().addAlwaysOnModifier(mod);
    }

	/**
	 * Causes the given action to be stapled to the game state for the duration of the game.  The action will be
	 * available to the appropriate player at the appropriate time as if an invisible card were on the board.  The
	 * precise details (player, phase, effect) will need to be set in the action itself.
	 * @param action The action to permanently add to the game.
	 */
	default void ApplyAdHocAction(ActionProxy action)
    {
        game().getActionsEnvironment().addUntilEndOfGameActionProxy(action);
    }

	/**
	 * Causes the Dark Side player to execute an arbitrary effect.  Note that there are nuances to how and whether this
	 * ever works; in particular it seems to only work if there are 0 legal actions to take on the current decision.
	 * When it works, it can be useful for altering the game state in situations where you want e.g. the proper trigger
	 * or other side-effects to be respected.  It is finicky tho.
	 * @param effect The effect to execute.  Details are determined by the effect itself.
	 */
	default void DSExecuteAdHocEffect(PhysicalCardImpl source, StandardEffect effect) {
		ExecuteAdHocEffect(DS, source, effect);
	}
	/**
	 * Causes the LIght Side player to execute an arbitrary effect.  Note that there are nuances to how and whether this
	 * ever works; in particular it seems to only work if there are 0 legal actions to take on the current decision.
	 * When it works, it can be useful for altering the game state in situations where you want e.g. the proper trigger
	 * or other side-effects to be respected.  It is finicky tho.
	 * @param effect The effect to execute.  Details are determined by the effect itself.
	 */
	default void LSExecuteAdHocEffect(PhysicalCardImpl source, StandardEffect effect) {
		ExecuteAdHocEffect(LS, source, effect);
	}

	/**
	 * Causes the given player to execute an arbitrary effect.  Note that there are nuances to how and whether this
	 * ever works; in particular it seems to only work if there are 0 legal actions to take on the current decision.
	 * When it works, it can be useful for altering the game state in situations where you want e.g. the proper trigger
	 * or other side-effects to be respected.  It is finicky tho.
	 * @param playerId The player who will execute the effect.
	 * @param effect The effect to execute.  Details are determined by the effect itself.
	 */
	default void ExecuteAdHocEffect(String playerId, PhysicalCardImpl source, StandardEffect effect) {
        carryOutEffectInPhaseActionByPlayer(playerId, source, effect);
    }

	/**
	 * Low-level function used by other ad-hoc functions in the test rig.  Use one of the other helper functions instead.
	 * @param playerId Player to make the action available to.
	 * @param source Card which theoretically has this text "printed" on it.
	 * @param effect The effect the tester would like to execute.
	 */
	default void carryOutEffectInPhaseActionByPlayer(String playerId, PhysicalCardImpl source, StandardEffect effect) {
		var action = new TopLevelGameTextAction(source, playerId, source.getCardId());
		action.appendEffect(effect);
		carryOutEffectInPhaseActionByPlayer(playerId, action);
	}

	/**
	 * Low-level function used by other ad-hoc functions in the test rig.  Use one of the other helper functions instead.
	 * @param playerId Player to make the action available to.
	 * @param action Action to add and execute.
	 */
	default void carryOutEffectInPhaseActionByPlayer(String playerId, Action action) {
		var awaitingDecision = (CardActionSelectionDecision) userFeedback().getAwaitingDecision(playerId);
		awaitingDecision.addAction(action);

		PlayerDecided(playerId, "0");
	}


}
