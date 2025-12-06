package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Actions are top-level decisions that involve legal game operations available to players.  For example, deploying
 * a card, playing an interrupt, and activating a card ability are all Actions.
 */
public interface Actions extends Decisions, Choices {

	/**
	 * @return Gets the text descriptions of all current actions available to the Dark Side player. Will return an empty
	 * list if that player does not have a currently pending decision.
	 */
	default List<String> GetDSAvailableActions() { return GetAvailableActions(DS); }
	/**
	 * @return Gets the text descriptions of all current actions available to the Light Side player. Will return an empty
	 * list if that player does not have a currently pending decision.
	 */
	default List<String> GetLSAvailableActions() { return GetAvailableActions(LS); }
	/**
	 * @param playerID The player with a current decision
	 * @return Gets the text descriptions of all current actions available to the given player.  Will return an empty
	 * list if that player does not have a currently pending decision.
	 */
	default List<String> GetAvailableActions(String playerID) {
		AwaitingDecision decision = GetAwaitingDecision(playerID);
		if(decision == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(decision.getDecisionParameters().get("actionText"));
	}

	/**
	 * @return True if an action is available as part of the current decision, false if there are no actions or the
	 * Dark Side player has no pending decisions.
	 */
	default boolean DSAnyActionsAvailable() { return AnyActionsAvailable(DS); }

	/**
	 * @return True if an action is available as part of the current decision, false if there are no actions or the
	 * Light Side player has no pending decisions.
	 */
	default boolean LSAnyActionsAvailable() { return AnyActionsAvailable(LS); }

	/**
	 * Returns whether the given player has any action at all available as part of the currently pending decision.
	 * @param player The player to check for.
	 * @return True if an action is available as part of the current decision, false if there are no actions or the
	 * current player has no pending decisions.
	 */
	default boolean AnyActionsAvailable(String player) {
		List<String> actions = GetAvailableActions(player);
		return !actions.isEmpty();
	}

	/**
	 * Checks whether the Dark Side player has any action available containing the provided text.
	 * @param text The text to search for.
	 * @return True if an active decision has an action matching text, otherwise false.
	 */
	default boolean DSActionAvailable(String text) { return ActionAvailable(DS, null, text); }
	/**
	 * Checks whether any action on the given card can be performed by the Dark Side player.  This is a catch-all
	 * that will catch any kind of action for that card--deploy, transfer, play, activate, etc.
	 * @param card The card being searched for.
	 * @return True if there is an available action for that card, false otherwise.
	 */
	default boolean DSCardActionAvailable(PhysicalCardImpl card) { return ActionAvailable(DS, card, null); }
	/**
	 * Checks whether the Dark Side player has an action available containing the provided text.
	 * @param card The card ID to search for.
	 * @param text The text to search for.
	 * @return True if an active decision has an action matching text, otherwise false.
	 */
	default boolean DSCardActionAvailable(PhysicalCardImpl card, String text) { return ActionAvailable(DS, card, text); }
	/**
	 * Checks whether the Light Side player has an action available containing the provided text.
	 * @param text The text to search for.
	 * @return True if an active decision has an action matching text, otherwise false.
	 */
	default boolean LSActionAvailable(String text) { return ActionAvailable(LS, null, text); }
	/**
	 * Checks whether any action on the given card can be performed by the Light Side player.  This is a catch-all
	 * that will catch any kind of action for that card--deploy, transfer, play, activate, etc.
	 * @param card The card being searched for.
	 * @return True if there is an available action for that card, false otherwise.
	 */
	default boolean LSCardActionAvailable(PhysicalCardImpl card) { return ActionAvailable(LS, card, null); }
	/**
	 * Checks whether the Light Side player has an action available containing the provided text.
	 * @param card The card ID to search for.
	 * @param text The text to search for.
	 * @return True if an active decision has an action matching text, otherwise false.
	 */
	default boolean LSCardActionAvailable(PhysicalCardImpl card, String text) { return ActionAvailable(LS, card, text); }
	/**
	 * Checks whether the given player has an action available containing the provided text.
	 * @param playerId The player to check for.
	 * @param card The card ID to search for.
	 * @param text The text to search for.
	 * @return True if an active decision has an action matching card and/or text, otherwise false.
	 */
	default boolean ActionAvailable(String playerId, PhysicalCardImpl card, String text) {
		return GetCardActionId(playerId, card, text) != null;
	}

	/**
	 * Causes the Dark Side player to choose the given action whose description contains the given search text.
	 * @param option The text to search for.
	 */
	default void DSChooseAction(String option) { ChooseAction(DS, "actionText", option); }
	/**
	 * Causes the Light Side player to choose the given action whose description contains the given search text.
	 * @param option The text to search for.
	 */
	default void LSChooseAction(String option) { ChooseAction(LS, "actionText", option); }


	/**
	 * Causes the Dark Side player to execute an available action on the given card.
	 * @param card The card which is being used (played, deployed, activated, etc).
	 */
	default void DSUseCardAction(PhysicalCardImpl card) {
        assertTrue("No card action available",DSCardActionAvailable(card));
        DSDecided(GetCardActionId(DS, card));
    }
	/**
	 * Causes the Light Side player to execute an available action on the given card.
	 * @param card The card which is being used (played, deployed, activated, etc).
	 */
	default void LSUseCardAction(PhysicalCardImpl card) {
        assertTrue("No card action available",LSCardActionAvailable(card));
        LSDecided(GetCardActionId(LS, card));
    }


	/**
	 * Checks whether the given card can be played by the Dark Side player.  Technically this is a catch-all function
	 * that only looks for any action associated with the given card, but the use of this function communicates that
	 * the tester intended to check for a play action from hand.
	 * @param card The card being searched for.
	 * @return True if there is an available play action for that card, false otherwise.
	 */
	default boolean DSCardPlayAvailable(PhysicalCardImpl card) { return DSCardActionAvailable(card); }

	/**
	 * Checks whether the given card can be played by the Dark Side player.  Technically this is a catch-all function
	 * that only looks for any action associated with the given card, but the use of this function communicates that
	 * the tester intended to check for a play action from hand.
	 * @param card The card being searched for.
	 * @param text substring to match against possible action text
	 * @return True if there is an available play action for that card, false otherwise.
	 */
	default boolean DSCardPlayAvailable(PhysicalCardImpl card, String text) { return DSCardActionAvailable(card, text); }
	/**
	 * Checks whether the given card can be played by the Light Side player.  Technically this is a catch-all function
	 * that only looks for any action associated with the given card, but the use of this function communicates that
	 * the tester intended to check for a play action from hand.
	 * @param card The card being searched for.
	 * @return True if there is an available play action for that card, false otherwise.
	 */
	default boolean LSCardPlayAvailable(PhysicalCardImpl card) { return LSCardActionAvailable(card); }

	/**
	 * Checks whether the given card can be played by the Light Side player.  Technically this is a catch-all function
	 * that only looks for any action associated with the given card, but the use of this function communicates that
	 * the tester intended to check for a play action from hand.
	 * @param card The card being searched for.
	 * @param text substring to match against possible action text
	 * @return True if there is an available play action for that card, false otherwise.
	 */
	default boolean LSCardPlayAvailable(PhysicalCardImpl card, String text) { return LSCardActionAvailable(card,text); }

	/**
	 * Causes the Dark Side player to select the given card and execute its legal action (i.e. plays that card from hand).
	 * Technically this is a catch-all that will activate any action on this card, but the use of this function
	 * communicates that the tester intended to play it from hand.
	 * @param card The card to play.
	 */
	default void DSPlayCard(PhysicalCardImpl card) {
        assertTrue("No card play available",DSCardPlayAvailable(card));
        DSDecided(GetCardActionId(DS, card));
    }

	/**
	 * Causes the Dark Side player to select the given card and execute its legal action (i.e. plays that card from hand).
	 * Technically this is a catch-all that will activate any action on this card, but the use of this function
	 * communicates that the tester intended to play it from hand.
	 * @param card The card to play.
	 * @param text Substring of the action text for the action to play
	 */
	default void DSPlayCard(PhysicalCardImpl card, String text) {
        assertTrue("Specified card play unavailable",DSCardPlayAvailable(card, text));
        DSDecided(GetCardActionId(DS, card, text));
    }

	/**
	 * Causes the Light Side player to select the given card and execute its legal action (i.e. plays that card from hand).
	 * Technically this is a catch-all that will activate any action on this card, but the use of this function
	 * communicates that the tester intended to play it from hand.
	 * @param card The card to play.
	 */
	default void LSPlayCard(PhysicalCardImpl card) {
        assertTrue("No card play available",LSCardPlayAvailable(card));
        LSDecided(GetCardActionId(LS, card));
    }

	/**
	 * Causes the Light Side player to select the given card and execute its legal action (i.e. plays that card from hand).
	 * Technically this is a catch-all that will activate any action on this card, but the use of this function
	 * communicates that the tester intended to play it from hand.
	 * @param card The card to play.
	 * @param text Substring of the action text for the action to play
	 */
	default void LSPlayCard(PhysicalCardImpl card, String text) {
        assertTrue("Specified card play unavailable",LSCardPlayAvailable(card, text));
        LSDecided(GetCardActionId(LS, card, text));
    }


	/**
	 * Checks whether the given Lost Interrupt can be played by the Dark Side player.  This may only be useful for
	 * cards which have both a USED and LOST operation and may fail if checking against vanilla LOST Interrupts.
	 * @param card The interrupt being searched for.
	 * @return True if there is an available LOST play action for that card, false otherwise.
	 */
	default boolean DSPlayLostInterruptAvailable(PhysicalCardImpl card) { return DSCardActionAvailable(card, "LOST: "); }
	/**
	 * Checks whether the given Lost Interrupt can be played by the Light Side player.  This may only be useful for
	 * cards which have both a USED and LOST operation and may fail if checking against vanilla LOST Interrupts.
	 * @param card The interrupt being searched for.
	 * @return True if there is an available LOST play action for that card, false otherwise.
	 */
	default boolean LSPlayLostInterruptAvailable(PhysicalCardImpl card) { return LSCardActionAvailable(card, "LOST: "); }
	/**
	 * Checks whether the given Used Interrupt can be played by the Dark Side player.  This may only be useful for
	 * cards which have both a USED and LOST operation and may fail if checking against vanilla USED Interrupts.
	 * @param card The interrupt being searched for.
	 * @return True if there is an available USED play action for that card, false otherwise.
	 */
	default boolean DSPlayUsedInterruptAvailable(PhysicalCardImpl card) { return DSCardActionAvailable(card, "USED: "); }
	/**
	 * Checks whether the given Used Interrupt can be played by the Light Side player.  This may only be useful for
	 * cards which have both a USED and LOST operation and may fail if checking against vanilla USED Interrupts.
	 * @param card The interrupt being searched for.
	 * @return True if there is an available USED play action for that card, false otherwise.
	 */
	default boolean LSPlayUsedInterruptAvailable(PhysicalCardImpl card) { return LSCardActionAvailable(card, "USED: "); }


	/**
	 * Causes the Dark Side player to select the given Lost Interrupt and execute its legal action (i.e. plays that
	 * card from hand). This may only be useful for cards which have both a USED and LOST operation and may fail if
	 * using it for vanilla LOST Interrupts.
	 * @param card The card to play.
	 * this card.
	 */
	default void DSPlayLostInterrupt(PhysicalCardImpl card) {
        assertTrue("Lost interrupt play unavailable",DSPlayLostInterruptAvailable(card));
        DSDecided(GetCardActionId(DS, card, "LOST: "));
    }
	/**
	 * Causes the Light Side player to select the given Lost Interrupt and execute its legal action (i.e. plays that
	 * card from hand). This may only be useful for cards which have both a USED and LOST operation and may fail if
	 * using it for vanilla LOST Interrupts.
	 * @param card The card to play.
	 */
	default void LSPlayLostInterrupt(PhysicalCardImpl card) {
        assertTrue("Lost interrupt play unavailable",LSPlayLostInterruptAvailable(card));
        LSDecided(GetCardActionId(LS, card, "LOST: "));
    }

	/**
	 * Causes the Dark Side player to select the given Used Interrupt and execute its legal action (i.e. plays that
	 * card from hand). This may only be useful for cards which have both a USED and LOST operation and may fail if
	 * using it for vanilla USED Interrupts.
	 * @param card The card to play.
	 */
	default void DSPlayUsedInterrupt(PhysicalCardImpl card) {
        assertTrue("Used interrupt play unavailable",DSPlayUsedInterruptAvailable(card));
        DSDecided(GetCardActionId(DS, card, "USED: "));
    }
	/**
	 * Causes the Light Side player to select the given Used Interrupt and execute its legal action (i.e. plays that
	 * card from hand). This may only be useful for cards which have both a USED and LOST operation and may fail if
	 * using it for vanilla USED Interrupts.
	 * @param card The card to play.
	 */
	default void LSPlayUsedInterrupt(PhysicalCardImpl card) {
        assertTrue("Used interrupt play unavailable",LSPlayUsedInterruptAvailable(card));
        LSDecided(GetCardActionId(LS, card, "USED: "));
    }


	/**
	 * Checks whether the given card can be deployed by the Dark Side player.
	 * @param card The card being searched for.
	 * @return True if there is an available Deploy action for that card, false otherwise.
	 */
	default boolean DSDeployAvailable(PhysicalCardImpl card) { return DSCardActionAvailable(card, "Deploy"); }
	/**
	 * Checks whether the given card can be deployed by the Light Side player.
	 * @param card The card being searched for.
	 * @return True if there is an available Deploy action for that card, false otherwise.
	 */
	default boolean LSDeployAvailable(PhysicalCardImpl card) { return LSCardActionAvailable(card, "Deploy"); }

	/**
	 * Causes the Dark Side player to perform  a legal deployment action of the given card (i.e. plays that card from hand).
	 * @param card The card to deploy.
	 */
	default void DSDeployCard(PhysicalCardImpl card) {
		String id = GetCardActionId(DS, card, "Deploy");
		if(id == null)
			throw new RuntimeException("Card '" + card.getBlueprint().getTitle() + "' is not an available deploy action.");

		DSDecided(id);
	}
	/**
	 * Causes the Light Side player to perform  a legal deployment action of the given card (i.e. plays that card from hand).
	 * @param card The card to deploy.
	 */
	default void LSDeployCard(PhysicalCardImpl card) {
		String id = GetCardActionId(LS, card, "Deploy");
		if(id == null)
			throw new RuntimeException("Card '" + card.getBlueprint().getTitle() + "' is not an available deploy action.");

		LSDecided(id);
	}

	/**
	 * Causes the Dark Side player to perform  a legal deployment action of the given location (i.e. plays that card
	 * from hand).  The location will be placed on the left automatically if necessary
	 * @param location The location to deploy.
	 */
	default void DSDeployLocation(PhysicalCardImpl location) {
        assertTrue(DSDeployAvailable(location));
		DSDeployCard(location);
		if(DSDecisionAvailable("On which side")) {
			DSChoose("Left");
		}
	}
	/**
	 * Causes the Light Side player to perform  a legal deployment action of the given location (i.e. plays that card
	 * from hand).  The location will be placed on the left automatically if necessary
	 * @param location The location to deploy.
	 */
	default void LSDeployLocation(PhysicalCardImpl location) {
        assertTrue(LSDeployAvailable(location));
		LSDeployCard(location);
		if(LSDecisionAvailable("On which side")) {
			LSChoose("Left");
		}
	}


	/**
	 * Checks whether the given card can be transferred by the Dark Side player.
	 * @param card The card being searched for.
	 * @return True if there is an available Transfer action for that card, false otherwise.
	 */
	default boolean DSTransferAvailable(PhysicalCardImpl card) { return DSCardActionAvailable(card, "Transfer"); }
	/**
	 * Checks whether the given card can be transferred by the Light Side player.
	 * @param card The card being searched for.
	 * @return True if there is an available Transfer action for that card, false otherwise.
	 */
	default boolean LSTransferAvailable(PhysicalCardImpl card) { return LSCardActionAvailable(card, "Transfer"); }

	/**
	 * Causes the Dark Side player to initiate a Transfer action on the given card.  Follow-up decisions will need to be
	 * made regarding the target.
	 * @param card The card to transfer.
	 */
	default void DSTransferCard(PhysicalCardImpl card) {
        assertTrue(DSTransferAvailable(card));
        DSDecided(GetCardActionId(DS, card, "Transfer"));
    }
	/**
	 * Causes the Light Side player to initiate a Transfer action on the given card.  Follow-up decisions will need to be
	 * made regarding the target.
	 * @param card The card to transfer.
	 */
	default void LSTransferCard(PhysicalCardImpl card) {
        assertTrue(LSTransferAvailable(card));
        LSDecided(GetCardActionId(LS, card, "Transfer "));
    }


	/**
	 * Checks whether the given card can use a move action for the Dark Side player.
	 * @param card The card being checked.
	 * @return True if there is an available Move action for that card, false otherwise.
	 */
	default boolean DSMoveAvailable(PhysicalCardImpl card) { return DSCardActionAvailable(card, "Move"); }
	/**
	 * Checks whether the given card can use a move action for the Light Side player.
	 * @param card The card being checked.
	 * @return True if there is an available Move action for that card, false otherwise.
	 */
	default boolean LSMoveAvailable(PhysicalCardImpl card) { return LSCardActionAvailable(card, "Move"); }

	/**
	 * Causes the Dark Side player to perform a legal move action on the given card (i.e. moves that card using its
	 * landspeed or hyperspeed).
	 * @param card The card to move.
	 * @param location Which site to move this card to
	 */
	default void DSMoveCard(PhysicalCardImpl card, PhysicalCardImpl location) {
		String id = GetCardActionId(DS, card, "Move");
		if(id == null)
			throw new RuntimeException("Card '" + card.getBlueprint().getTitle() + "' does not have an available move action.");

		DSDecided(id);
		DSChooseCard(location);
	}

	/**
	 * Causes the Light Side player to perform a legal move action on the given card (i.e. moves that card using its
	 * landspeed or hyperspeed).
	 * @param card The card to move.
	 * @param location Which site to move this card to
	 */
	default void LSMoveCard(PhysicalCardImpl card, PhysicalCardImpl location) {
		String id = GetCardActionId(LS, card, "Move");
		if(id == null)
			throw new RuntimeException("Card '" + card.getBlueprint().getTitle() + "' does not have an available move action.");

		LSDecided(id);
		LSChooseCard(location);
	}


	/**
	 * Checks whether the Dark Side player has a legal Force Drain action available to make at the given site.
	 * @param site The site to check for a legal Force Drain action.
	 * @return True if a Force Drain can be performed at that site, false otherwise.
	 */
	default boolean DSForceDrainAvailable(PhysicalCardImpl site) { return DSCardActionAvailable(site, "Force drain"); }
	/**
	 * Checks whether the Light Side player has a legal Force Drain action available to make at the given site.
	 * @param site The site to check for a legal Force Drain action.
	 * @return True if a Force Drain can be performed at that site, false otherwise.
	 */
	default boolean LSForceDrainAvailable(PhysicalCardImpl site) { return LSCardActionAvailable(site, "Force drain"); }

	/**
	 * Causes the Dark Side player to initiate a legal Force Drain action at the given site.
	 * @param site The location to initiate the Force Drain at.
	 */
	default void DSForceDrainAt(PhysicalCardImpl site) {
        assertTrue(DSForceDrainAvailable(site));
        DSDecided(GetCardActionId(DS, site, "Force drain"));
	}

	/**
	 * Causes the Dark Side player to initiate a legal Force Drain action at the given site.
	 * @param site The location to initiate the Force Drain at.
	 */
	default void LSForceDrainAt(PhysicalCardImpl site) {
        assertTrue(LSForceDrainAvailable(site));
        LSDecided(GetCardActionId(LS, site, "Force drain"));
	}


	/**
	 * Searches the currently available actions on the current decision for the given player and returns the ID of an
	 * action which contains the provided text in its description.  This is a lower-level function used by the test rig
	 * which is unlikely to be needed for tests themselves.
	 * @param playerId The player which must have a currently active decision.
	 * @param text Constrains the result to only actions whose description contains the provided text
	 * @return The action ID of a matching action (which can be passed as a decision answer).  Returns null if no
	 * actions matched.
	 */
	default String GetCardActionId(String playerId, String text) { return GetCardActionId(playerId, null, text); }
	/**
	 * Searches the currently available actions on the current decision for the given player and returns the ID of an
	 * action which was sourced by the provided card's ID.  This is a lower-level function used by the test rig
	 * which is unlikely to be needed for tests themselves.
	 * @param playerId The player which must have a currently active decision.
	 * @param card Constrains the result to only actions which are source from this card.
	 * @return The action ID of a matching action (which can be passed as a decision answer).  Returns null if no
	 * actions matched.
	 */
	default String GetCardActionId(String playerId, PhysicalCardImpl card) { return GetCardActionId(playerId, card, null); }

	/**
	 * Searches the currently available actions on the current decision for the given player.  If card is provided, the
	 * card's ID must be the source of one of the given actions.  If text is provided, the action description must match
	 * the given text.  If both are provided, both are checked. This is a lower-level function used by the test rig
	 * which is unlikely to be needed for tests themselves.
	 * @param playerId The player which must have a currently active decision.
	 * @param card If provided, constrains the result to only actions which are source from this card.
	 * @param text If provided, constrains the result to only actions whose description contains the provided text
	 * @return The action ID of a matching action (which can be passed as a decision answer).  Returns null if no
	 * actions matched.
	 */
	default String GetCardActionId(String playerId, PhysicalCardImpl card, String text) {
		String id = card != null ? String.valueOf(card.getCardId()) : null;
		String[] cardIds = GetADParam(playerId, "cardId");
		String[] actionTexts = GetADParam(playerId, "actionText");

		for (int i = 0; i < cardIds.length; i++) {
			if ((id == null || cardIds[i].equals(id)) && (text == null || actionTexts[i].contains(text))) {
				return GetADParam(playerId, "actionId")[i];
			}
		}
		return null;
	}

}
