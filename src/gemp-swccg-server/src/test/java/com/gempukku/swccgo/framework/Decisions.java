package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A set of functions within the test rig that pertain to decisions.  Decisions in Gemp are a catch-all term referring
 * to any point where the game simulation cannot continue until a player makes a choice.  Thus, awaiting passes,
 * choosing targets, selecting cards from a group, or initiating actions are all "decisions".
 *
 * These functions should give you everything you need to assert that a decision is or is not available, or give you
 * the tools to make a decision properly.  See Choices for selecting among multiple options, and see Actions for
 * top-level card actions.
 */
public interface Decisions extends GameProperties, TestBase  {

	/**
	 * @return Gets the Dark Side decision that Gemp is currently waiting on.  Will be null if DS is not currently
	 * pending any decision.
	 */
	default AwaitingDecision DSGetDecision() { return GetAwaitingDecision(DS); }
	/**
	 * @return Gets the Light Side decision that Gemp is currently waiting on.  Will be null if LS is not currently
	 * pending any decision.
	 */
	default AwaitingDecision LSGetDecision() { return GetAwaitingDecision(LS); }

	/**
	 * Gets a decision for a given player that Gemp is currently waiting on.  Will be null if that player is not
	 * currently pending any decision.
	 * @param playerID The player's decision to retrieve
	 * @return
	 */
	default AwaitingDecision GetAwaitingDecision(String playerID) { return userFeedback().getAwaitingDecision(playerID); }

	/**
	 * @return Gets the currently pending decision.  Defaults to returning the Dark Side decision if both players are
	 * currently pending (which only rarely happens in situations such as the starting popup or dismissing cards
	 * revealed to both players).
	 */
	default AwaitingDecision GetCurrentDecision() {
		var darkDecision = DSGetDecision();
		if(darkDecision != null)
			return darkDecision;
		return LSGetDecision();
	}

	/**
	 * Determines if the Dark Side player is currently presented with a decision which contains the given text.
	 * @param text The text snippet to search for.
	 * @return False if Dark Side has no current decisions or if the current decision does not contain the given text.
	 */
	default boolean DSDecisionAvailable(String text) { return DecisionAvailable(DS, text); }
	/**
	 * Determines if the Light Side player is currently presented with a decision which contains the given text.
	 * @param text The text snippet to search for.
	 * @return False if Light Side has no current decisions or if the current decision does not contain the given text.
	 */
	default boolean LSDecisionAvailable(String text) { return DecisionAvailable(LS, text); }

	/**
	 * Determines if the given player is currently presented with a decision which contains the given text.
	 * @param text The text snippet to search for.
	 * @return False if the given player has no current decisions or if the current decision does not contain the given text.
	 */
	default boolean DecisionAvailable(String playerID, String text)
	{
		AwaitingDecision ad = GetAwaitingDecision(playerID);
		if(ad == null)
			return false;
		String lowerText = text.toLowerCase();
		return ad.getText().toLowerCase().contains(lowerText);
	}

	/**
	 * @return True if the Dark Side player is currently deciding on a top-level phase action to use during the Activate phase, or else false.
	 */
	default boolean AwaitingDSActivatePhaseActions() { return DSDecisionAvailable("Choose Activate action or Pass"); }
	/**
	 * @return True if the Light Side player is currently deciding on a top-level phase action to use during the Activate phase, or else false.
	 */
	default boolean AwaitingLSActivatePhaseActions() { return LSDecisionAvailable("Choose Activate action or Pass"); }
	/**
	 * @return True if the Dark Side player is currently deciding on a top-level phase action to use during the Control phase, or else false.
	 */
	default boolean AwaitingDSControlPhaseActions() { return DSDecisionAvailable("Choose Control action or Pass"); }
	/**
	 * @return True if the Light Side player is currently deciding on a top-level phase action to use during the Control phase, or else false.
	 */
	default boolean AwaitingLSControlPhaseActions() { return LSDecisionAvailable("Choose Control action or Pass"); }
	/**
	 * @return True if the Dark Side player is currently deciding on a top-level phase action to use during the Deploy phase, or else false.
	 */
	default boolean AwaitingDSDeployPhaseActions() { return DSDecisionAvailable("Choose Deploy action or Pass"); }
	/**
	 * @return True if the Light Side player is currently deciding on a top-level phase action to use during the Deploy phase, or else false.
	 */
	default boolean AwaitingLSDeployPhaseActions() { return LSDecisionAvailable("Choose Deploy action or Pass"); }
	/**
	 * @return True if the Dark Side player is currently deciding on a top-level phase action to use during the Battle phase, or else false.
	 */
	default boolean AwaitingDSBattlePhaseActions() { return DSDecisionAvailable("Choose Battle action or Pass"); }
	/**
	 * @return True if the Light Side player is currently deciding on a top-level phase action to use during the Battle phase, or else false.
	 */
	default boolean AwaitingLSBattlePhaseActions() { return LSDecisionAvailable("Choose Battle action or Pass"); }
	/**
	 * @return True if the Dark Side player is currently deciding on a top-level phase action to use during the Move phase, or else false.
	 */
	default boolean AwaitingDSMovePhaseActions() { return DSDecisionAvailable("Choose Move action or Pass"); }
	/**
	 * @return True if the Light Side player is currently deciding on a top-level phase action to use during the Move phase, or else false.
	 */
	default boolean AwaitingLSMovePhaseActions() { return LSDecisionAvailable("Choose Move action or Pass"); }
	/**
	 * @return True if the Dark Side player is currently deciding on a top-level phase action to use during the Draw phase, or else false.
	 */
	default boolean AwaitingDSDrawPhaseActions() { return DSDecisionAvailable("Choose Draw action or Pass"); }
	/**
	 * @return True if the Light Side player is currently deciding on a top-level phase action to use during the Draw phase, or else false.
	 */
	default boolean AwaitingLSDrawPhaseActions() { return LSDecisionAvailable("Choose Draw action or Pass"); }

	/**
	 * @return Returns true if Dark Side is currently presented with any decision at all.
	 */
	default boolean DSAnyDecisionsAvailable() { return AnyDecisionsAvailable(DS); }
	/**
	 * @return Returns true if Light Side is currently presented with any decision at all.
	 */
	default boolean LSAnyDecisionsAvailable() { return AnyDecisionsAvailable(LS); }

	/**
	 * Returns whether the given player is currently presented with any decision at all.
	 * @param player The player to check for pending decisions
	 * @return True if the given player has a pending decision, else false.
	 */
	default boolean AnyDecisionsAvailable(String player) {
		var ad = GetAwaitingDecision(player);
		return ad != null;
	}

	/**
	 * Wrapper for DSPass for the situations where an optional action is actually being offered which we want to decline.
	 */
	default void DSDecline() { DSPass(); }
	/**
	 * Wrapper for LSPass for the situations where an optional action is actually being offered which we want to decline.
	 */
	default void LSDecline() { LSPass(); }

	/**
	 * Causes the Dark Side player to pass the current decision.
	 */
	// If this seems out of place organization-wise, it's because of the chain of inheritance between the various test interfaces.
	default void DSPass() {
		if(DSAnyDecisionsAvailable()) {
			PlayerDecided(DS, "");
		}
	}
	/**
	 * Causes the Light Side player to pass the current decision.
	 */
	// If this seems out of place organization-wise, it's because of the chain of inheritance between the various test interfaces.
	default void LSPass() {
		if(LSAnyDecisionsAvailable()) {
			PlayerDecided(LS, "");
		}
	}

	/**
	 * Causes the given player to pass whatever decision they are currently being presented with, if any.
	 * @param player The player who is currently pending a decision.
	 */
	default void PlayerPass(String player) {
		if(AnyDecisionsAvailable(player)) {
			PlayerDecided(player, "");
		}
	}

	/**
	 * Causes the given player to choose an option which contains the given text.  See {@link Choices} for more specific
	 * uses of this helper function.
	 * @param playerID The player who should make the current decision
	 * @param option The text to search for in all available choices
	 */
	default void ChooseOption(String playerID, String option) { ChooseAction(playerID, "results", option); }

	/**
	 * Causes the given player to choose an action which is part of the current decision.  This is usually
	 * used as a low-level function; see {@link Actions} for more specific uses of this helper function.
	 * @param playerID The player who should make the current decision.
	 * @param option The search text to look for in available actions
	 */
	default void ChooseAction(String playerID, String option) {
		ChooseAction(playerID, "actionText", option);
	}

	/**
	 * Causes the given player to choose an option from the given parameters of a particular choice.  This is usually
	 * used as a low-level function; see {@link Actions} for more specific uses of this helper function.
	 * @param playerID The player who should make the current decision.
	 * @param paramName The parameter on the current decision which should be searched.
	 * @param option The search text to look for in the parameter list.
	 */
	default void ChooseAction(String playerID, String paramName, String option) {
		List<String> choices = GetADParamAsList(playerID, paramName);
		for(String choice : choices){
			if(option == null && choice == null // This only happens when a rule is the source of an action
					|| choice.toLowerCase().contains(option.toLowerCase())) {
				PlayerDecided(playerID, String.valueOf(choices.indexOf(choice)));
				return;
			}
		}
		//couldn't find an exact match, so maybe it's a direct index:
		PlayerDecided(playerID, option);
	}

	/**
	 * Gets all of the options for a particular parameter of the current awaiting Dark Side decision.
	 * Converts the array to a list first.
	 * @param paramName The parameter options to return.
	 * @return A list of strings, which are all options for the given parameter.  This list is indexed according to the
	 * options on the current decision, so option 0 is tied to the first choice on the decision, option 1 is the second
	 * choice on the decision, etc.
	 */
	default List<String> DSGetADParamAsList(String paramName) { return GetADParamAsList(DS, paramName); }
	/**
	 * Gets all of the options for a particular parameter of the current awaiting Light Side decision.
	 * Converts the array to a list first.
	 * @param paramName The parameter options to return.
	 * @return A list of strings, which are all options for the given parameter.  This list is indexed according to the
	 * options on the current decision, so option 0 is tied to the first choice on the decision, option 1 is the second
	 * choice on the decision, etc.
	 */
	default List<String> LSGetADParamAsList(String paramName) { return GetADParamAsList(LS, paramName); }

	/**
	 * Gets all of the options for a particular parameter of the current awaiting decision.  Converts the array to a list first.
	 * @param playerID The player making the current decision.
	 * @param paramName The parameter options to return.
	 * @return A list of strings, which are all options for the given parameter.  This list is indexed according to the
	 * options on the current decision, so option 0 is tied to the first choice on the decision, option 1 is the second
	 * choice on the decision, etc.
	 */
	default List<String> GetADParamAsList(String playerID, String paramName) {
		var paramList = GetADParam(playerID, paramName);
		if(paramList == null)
			return null;

		return Arrays.asList(paramList);
	}

	/**
	 * Gets all entries for the given parameter on the Dark Side player's currently awaiting decision.
	 * @param paramName The parameter to search for.
	 * @return All the parameter values indexed according to the corresponding choice.
	 */
	default String[] DSGetADParam(String paramName) { return GetADParam(DS, paramName); }
	/**
	 * Gets all entries for the given parameter on the Light Side player's currently awaiting decision.
	 * @param paramName The parameter to search for.
	 * @return All the parameter values indexed according to the corresponding choice.
	 */
	default String[] LSGetADParam(String paramName) { return GetADParam(LS, paramName); }
	/**
	 * Gets the first entry for the given parameter on the Dark Side player's currently awaiting decision.
	 * @param paramName The parameter to search for.
	 * @return The first parameter value of the first choice which is part of the current decision.
	 */
	default String DSGetFirstADParam(String paramName) { return GetADParam(DS, paramName)[0]; }
	/**
	 * Gets the first entry for the given parameter on the Light Side player's currently awaiting decision.
	 * @param paramName The parameter to search for.
	 * @return The first parameter value of the first choice which is part of the current decision.
	 */
	default String LSGetFirstADParam(String paramName) { return GetADParam(LS, paramName)[0]; }

	/**
	 * Gets the number of times a value shows up in a parameter list for the current decision.  For example, if you
	 * want to know how many choices in a card decision are selectable, you can call this with "selectable" and "true",
	 * which will filter out any of the options which mark selectable as false.
	 * @param playerID The player's decision to inspect
	 * @param paramName The parameters to inspect
	 * @param value The exact value to look for
	 * @return How many entries in the parameter list exactly matched the search text.
	 */
	default int GetADParamEqualsCount(String playerID, String paramName, String value) {
		return (int) Arrays.stream(GetADParam(playerID, paramName)).filter(s -> s.equals(value)).count();
	}

	/**
	 * Gets all of the options for a particular parameter of the current awaiting decision as an array of strings.
	 * @param playerID The player making the current decision.
	 * @param paramName The parameter options to return.
	 * @return An array of strings, which are all options for the given parameter.  This list is indexed according to the
	 * options on the current decision, so option 0 is tied to the first choice on the decision, option 1 is the second
	 * choice on the decision, etc.
	 */
	default String[] GetADParam(String playerID, String paramName) {
		var decision = userFeedback().getAwaitingDecision(playerID);
		return decision.getDecisionParameters().get(paramName);
	}

	/**
	 * Returns the entire map of parameters and their options for the given player's current decision.  This is only
	 * needed if you need to do some complex checking or traversal of the decision that can't somehow be covered by
	 * other helper functions.
	 * @param playerID The player currently making a decision
	 * @return The parameter map.  Keys are the names of each parameter, values are an array of all the options.  Each
	 * array is indexed according to the decision choice, so index 0 on all keys will correspond to the first option, etc.
	 */
	default Map<String, String[]> GetAwaitingDecisionParams(String playerID) {
		var decision = userFeedback().getAwaitingDecision(playerID);
		return decision.getDecisionParameters();
	}


	/**
	 * Issues a request by the Dark Side player to revert.  This should only be used during any top-level phase
	 * action decision or it will fail.  You should also only use this to inspect the list of options and choose
	 * one, then pass it into the IssueRevert function.
	 */
	default void DSRequestRevert() { DSDecided("revert"); }
	/**
	 * Issues a request by the Light Side player to revert.  This should only be used during any top-level phase
	 * action decision or it will fail.  You should also only use this to inspect the list of options and choose
	 * one, then pass it into the IssueRevert function.
	 */
	default void LSRequestRevert() { LSDecided("revert"); }

	/**
	 * Causes the current player to request a revert, then causes the opponent to accept this revert.  The Virtual
	 * Table will then have its internal state properly reset to prevent stale references to cards from the old
	 * pre-revert state.
	 * @param target Which particular phase point to return to.  Use DSRequestRevert or LSRequestRevert to inspect the
	 *               available options to pass in here.
	 */
	default void IssueRevert(String target) {
		var decider = GetDecidingPlayer();
		var offPlayer = GetNextDecider();

		PlayerDecided(decider, "revert");
		ChooseOption(decider, target);
		ChooseOption(offPlayer, "Yes");
		ResetGameState();
	}

	/**
	 * Causes Dark Side to decide to use the given answer.  Integers usually indicate an index between multiple choices,
	 * but they may be literal integers if the player is asked to choose a number.
	 * @param answer The integer answer to return to the server
	 */
	default void DSDecided(int answer) { PlayerDecided(DS, String.valueOf(answer));}

	/**
	 * Causes Dark Side to decide to use the given answer.  Answers may take different forms depending on the exact
	 * nature of the decision at hand.
	 * @param answer The answer to return to the server
	 */
	default void DSDecided(String answer) { PlayerDecided(DS, answer);}

	/**
	 * Causes Light Side to decide to use the given answer.  Integers usually indicate an index between multiple choices,
	 * but they may be literal integers if the player is asked to choose a number.
	 * @param answer The integer answer to return to the server
	 */
	default void LSDecided(int answer) { PlayerDecided(LS, String.valueOf(answer));}
	/**
	 * Causes Light Side to decide to use the given answer.  Answers may take different forms depending on the exact
	 * nature of the decision at hand.
	 * @param answer The answer to return to the server
	 */
	default void LSDecided(String answer) { PlayerDecided(LS, answer);}

	// As this is actually related to the heart of the table simulation, this is left to be implemented on the main Scenario class.
	void PlayerDecided(String player, String answer);

	//
//    public boolean DSHasOptionalTriggerAvailable() { return DSDecisionAvailable("Optional"); }
//    public boolean LSHasOptionalTriggerAvailable() { return LSDecisionAvailable("Optional"); }
//
//    public void DSAcceptOptionalTrigger() { PlayerDecided(DS, "0"); }
//    public void DSDeclineOptionalTrigger() { PlayerDecided(DS, ""); }
//    public void LSAcceptOptionalTrigger() { PlayerDecided(LS, "0"); }
//    public void LSDeclineOptionalTrigger() { PlayerDecided(LS, ""); }
//    public void DSDeclineChoosing() { PlayerDecided(DS, ""); }
//    public void LSDeclineChoosing() { PlayerDecided(LS, ""); }



}
