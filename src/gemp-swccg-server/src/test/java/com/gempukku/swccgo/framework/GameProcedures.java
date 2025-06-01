package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;

import static org.junit.Assert.assertTrue;

/**
 * These functions are for progressing through the game itself.  For instance, if your test is really about battle
 * attrition and the phases before that point are just in the way, here you will find functions to skip past phases
 * so that your test can remain mostly clean of irrelevant procedure.
 *
 * Do be warned that these functions assume a best-case scenario that can be safely passed through; if a card has a
 * required decision that does not have an obvious "decline" option, then these functions will fail and you will have
 * to do it manually.  If you actually do need such a pestering card on the table, it is advised that you only place
 * it at the last possible second rather than putting it down early and requiring you to do all the manual procedure.
 */
public interface GameProcedures extends Actions, Decisions, GameProperties {

	/**
	 * Causes the Dark Side player to activate the maximum amount of available force, causes Light Side to let the same
	 * amount pass without a react, and then causes both players to pass Activate phase actions.
	 * @return The total amount of force that was activated
	 * @throws DecisionResultInvalidException
	 */
	default int DSActivateMaxForceAndPass() throws DecisionResultInvalidException {
		if(AwaitingDSActivatePhaseActions() && DSActionAvailable("Activate Force")) {
			DSChooseAction("Activate Force");
			int max = DSGetChoiceMax();
			DSDecided(max);
			LSDecided(max);
			PassResponses();
			return max;
		}

		return -1;
	}

	/**
	 * Causes the Light Side player to activate the maximum amount of available force, causes Dark Side to let the same
	 * amount pass without a react, and then causes both players to pass Activate phase actions.
	 * @return The total amount of force that was activated
	 * @throws DecisionResultInvalidException
	 */
	default int LSActivateMaxForceAndPass() throws DecisionResultInvalidException {
		if(AwaitingLSActivatePhaseActions() && LSActionAvailable("Activate Force")) {
			LSChooseAction("Activate Force");
			int max = LSGetChoiceMax();
			LSDecided(max);
			DSDecided(max);
			PassResponses();
			return max;
		}

		return -1;
	}


	/**
	 * Forces the Dark Side player to move the given amount of cards from their Reserve Deck to their Force Pile.
	 * This is done out of turn and does not require that it be the appropriate phase.
	 * @param amount
	 */
	default void DSActivateForceCheat(int amount) { ActivateForceCheat(DS, amount); }
	/**
	 * Forces the Light Side player to move the given amount of cards from their Reserve Deck to their Force Pile.
	 * This is done out of turn and does not require that it be the appropriate phase.
	 * @param amount
	 */
	default void LSActivateForceCheat(int amount) { ActivateForceCheat(LS, amount); }

	/**
	 * Forces the given player to move the given amount of cards from their Reserve Deck to their Force Pile.
	 * This is done out of turn and does not require that it be the appropriate phase.
	 * @param player
	 * @param amount
	 */
	default void ActivateForceCheat(String player, int amount) {
		for(int i = 0; i < amount; ++i) {
			gameState().playerActivatesForce(player, false, false);
		}
	}


	/**
	 * Forces the Dark Side player to move the given amount of cards from their Force Pile to their Used Pile.
	 * This is done out of turn and does not require that it be the appropriate phase.
	 * @param amount
	 */
	default void DSUseForceCheat(int amount) { UseForceCheat(DS, amount); }
	/**
	 * Forces the Light Side player to move the given amount of cards from their Force Pile to their Used Pile.
	 * This is done out of turn and does not require that it be the appropriate phase.
	 * @param amount
	 */
	default void LSUseForceCheat(int amount) { UseForceCheat(LS, amount); }

	/**
	 * Forces the given player to move the given amount of cards from their Force Pile to their Used Pile.
	 * This is done out of turn and does not require that it be the appropriate phase.
	 * @param player
	 * @param amount
	 */
	default void UseForceCheat(String player, int amount) {
		for(int i = 0; i < amount; ++i) {
			gameState().playerUsesForce(player, false, false);
		}
	}



	/**
	 * During the Deploy phase, causes Dark Side to deploy the given card to the given location and automatically pass
	 * any force use optional responses and deployment responses for both players.
	 * @param card The card to be deployed
	 * @param location Which location the card should be deployed to (should be in play already)
	 * @throws DecisionResultInvalidException
	 */
	default void DSDeployCardAndPassResponses(PhysicalCardImpl card, PhysicalCardImpl location) throws DecisionResultInvalidException {
		DSDeployCard(card);
		assertTrue(DSDecisionAvailable("Choose where to deploy"));
		DSChooseCard(location);

		PassResponses("Force - Optional responses");
		PassResponses("Optional response");
	}

	/**
	 * During the Deploy phase, causes Light Side to deploy the given card to the given location and automatically pass
	 * any force use optional responses and deployment responses for both players.
	 * @param card The card to be deployed
	 * @param location Which location the card should be deployed to (should be in play already)
	 * @throws DecisionResultInvalidException
	 */
	default void LSDeployCardAndPassResponses(PhysicalCardImpl card, PhysicalCardImpl location) throws DecisionResultInvalidException {
		LSDeployCard(card);
		assertTrue(LSDecisionAvailable("Choose where to deploy"));
		LSChooseCard(location);

		PassResponses("Force - Optional responses");
		PassResponses("Optional response");
	}

	/**
	 * Causes both players to pass during the Activate phase.
	 * @throws DecisionResultInvalidException
	 */
	default void PassActivateActions() throws DecisionResultInvalidException { PassResponses(); }
	/**
	 * Causes both players to pass during the Control phase.
	 * @throws DecisionResultInvalidException
	 */
	default void PassControlActions() throws DecisionResultInvalidException { PassResponses(); }
	/**
	 * Causes both players to pass during the Deploy phase.
	 * @throws DecisionResultInvalidException
	 */
	default void PassDeployActions() throws DecisionResultInvalidException { PassResponses(); }
	/**
	 * Causes both players to pass during the Move phase.
	 * @throws DecisionResultInvalidException
	 */
	default void PassMoveActions() throws DecisionResultInvalidException { PassResponses(); }
	/**
	 * Causes both players to pass during the Battle phase.
	 * @throws DecisionResultInvalidException
	 */
	default void PassBattleActions() throws DecisionResultInvalidException { PassResponses(); }
	/**
	 * Causes both players to pass during the Draw phase.
	 * @throws DecisionResultInvalidException
	 */
	default void PassDrawActions() throws DecisionResultInvalidException { PassResponses(); }

	/**
	 * @return True if the Dark Side player is currently deciding what to do with a captured character.
	 */
	default boolean DSCaptureDecisionAvailable() { return DSDecisionAvailable("Choose option for capturing "); }

	/**
	 * Causes the Dark Side player to choose to let the recently-captured captive "escape" (go to the used pile).
	 * @throws DecisionResultInvalidException Thrown if the Dark Side player is not actually deciding a captive's fate.
	 */
	default void DSChooseEscape() throws DecisionResultInvalidException { DSChoose("Escape"); }
	/**
	 * Causes the Dark Side player to choose to "seize" the recently-captured captive (attaching it to the captor).
	 * @throws DecisionResultInvalidException Thrown if the Dark Side player is not actually deciding a captive's fate.
	 */
	default void DSChooseSeize() throws DecisionResultInvalidException { DSChoose("Seize"); }

	/**
	 * @return True if the Light Side player is currently deciding what to do with a released captive.
	 */
	default boolean LSReleaseDecisionAvailable() { return LSDecisionAvailable("Choose release option for "); }

	/**
	 * Causes the Light Side player to choose to let the recently-released captive "escape" (go to the used pile).
	 * @throws DecisionResultInvalidException Thrown if the Light Side player is not actually deciding a captive's fate.
	 */
	default void LSChooseEscape() throws DecisionResultInvalidException { LSChoose("Escape"); }
	/**
	 * Causes the Light Side player to choose to let the recently-released captive "rally" (move to the current location).
	 * @throws DecisionResultInvalidException Thrown if the Light Side player is not actually deciding a captive's fate.
	 */
	default void LSChooseRally() throws DecisionResultInvalidException { LSChoose("Rally"); }

	/**
	 * When a card leaves the table, there are various responses.  This causes all players to pass all of them.
	 * @throws DecisionResultInvalidException
	 */
	default void PassCardLeavingTable() throws DecisionResultInvalidException {
		PassResponses("FORFEITED_TO_LOST_PILE_FROM_TABLE");
		PassResponses("PUT_IN_CARD_PILE_FROM_OFF_TABLE");
	}

	/**
	 * When a Force Drain begins, there are various responses.  This causes all players to pass all of them.
	 * @throws DecisionResultInvalidException
	 */
	default void PassForceDrainStartResponses() throws DecisionResultInvalidException {
		PassResponses("FORCE_DRAIN_INITIATED");
		PassResponses("FORCE_LOSS_INITIATED");
		PassForceDrainPendingResponses();
	}

	/**
	 * During a Force Drain, this response occurs between each paid Force.
	 * @throws DecisionResultInvalidException
	 */
	default void PassForceDrainPendingResponses() throws DecisionResultInvalidException {
		PassResponses("ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE");
	}
	/**
	 * After a Force Drain, one last response occurs once the targeted player has paid all Force costs.
	 * @throws DecisionResultInvalidException
	 */
	default void PassForceDrainEndResponses() throws DecisionResultInvalidException {
		PassResponses("FORCE_DRAIN_COMPLETED");
	}

	/**
	 * When we don't care about the responses being presented and we just want to skip forward to the next actual action.
	 * @throws DecisionResultInvalidException
	 */
	default void PassAllResponses() throws DecisionResultInvalidException {
		for(int i = 0; i < 20; ++i) {
			if(!GetCurrentDecision().getText().toLowerCase().contains("optional response"))
				return;

			PassResponses("optional");
		}
	}

	/**
	 * Causes both players to pass, first the player with a current decision and then the other.
	 * @throws DecisionResultInvalidException Throws this error if the decision wasn't passable.
	 */
	default void PassResponses() throws DecisionResultInvalidException {
		var decider = GetDecidingPlayer();
		var offPlayer = GetNextDecider();

		PlayerPass(decider);

		if(AnyDecisionsAvailable(offPlayer)) {
			PlayerPass(offPlayer);
		}
	}

	/**
	 * Causes both players to pass any decisions that contain the provided text.  First the current decider will pass,
	 * and then the other.
	 * @param text
	 * @throws DecisionResultInvalidException Throws this error if the decision can't be passed.
	 */
	default void PassResponses(String text) throws DecisionResultInvalidException {
		var decider = GetDecidingPlayer();
		var offPlayer = GetNextDecider();

		if(DecisionAvailable(decider, text)) {
			PlayerPass(decider);

			if(DecisionAvailable(offPlayer, text)) {
				PlayerPass(offPlayer);
			}
		}
	}

	/**
	 * When a card is played, there are various responses.  This causes both players to pass all of them.
	 * @throws DecisionResultInvalidException
	 */
	default void PassCardAndForceUseResponses() throws DecisionResultInvalidException {
		PassCardPlayResponses();
		PassForceUseResponses();
	}

	/**
	 * When a card is played, there are various responses.  This passes just the responses to the card being played.
	 * @throws DecisionResultInvalidException
	 */
	default void PassCardPlayResponses() throws DecisionResultInvalidException { PassResponses("Playing <div"); }
	/**
	 * When a card is played, there are various responses.  This passes just the responses to the Force paid during that
	 * card's deployment.
	 * @throws DecisionResultInvalidException
	 */
	default void PassForceUseResponses() throws DecisionResultInvalidException { PassResponses(" Force - Optional responses"); }


	/**
	 * Skips to the Battle phase.
	 * @throws DecisionResultInvalidException
	 */
    default void SkipToBattle() throws DecisionResultInvalidException { SkipToPhase(Phase.BATTLE); }

	/**
	 * Causes players to spam pass until the provided target phase is current.  This process attempts to choose the
	 * first option of any required triggers, but may be brittle if there are any reacts that interrupt the pass-fest.
	 * Only 20 rounds of passing will be attempted to avoid infinite loops.
	 * @param target The phase the tester actually wants to be in
	 * @throws DecisionResultInvalidException
	 */
    default void SkipToPhase(Phase target) throws DecisionResultInvalidException {
        for(int attempts = 1; attempts <= 20; attempts++)
        {
            Phase current = gameState().getCurrentPhase();
            if(current == target)
                break;

            if(current == Phase.ACTIVATE) {
				if(GetCurrentDecision().getText().toLowerCase().contains("optional")) {
					PassResponses("optional");
				}
				else if(gameState().getCurrentPlayerId().equals(LS)) {
					LSActivateMaxForceAndPass();
				}
				else {
					DSActivateMaxForceAndPass();
				}
            }
            else {
				if(GetCurrentDecision().getText().toLowerCase().contains("required")) {
					PassResponses("required");
				}
				// If we have resulted in a situation where the off-player is waiting to perform
				// a phase action, we do this once to ensure we fall back into a cadence of the
				// current player passing first, then the off player.
				else if(GetCurrentDecision().getText().toLowerCase().contains("action")) {
					PassResponses("action");
				}
                else {
                    PassResponses();
                }
            }

            if(attempts == 20)
            {
                throw new DecisionResultInvalidException("Could not arrive at target '" + target + "' after 20 attempts!");
            }
        }
    }

	/**
	 * Regardless of the current player, skips to the Activate phase of the next player's turn.
	 * @throws DecisionResultInvalidException
	 */
	default void SkipToNextTurn() throws DecisionResultInvalidException {
		SkipToNextTurn(game().getOpponent(gameState().getCurrentPlayerId()));
	}

	/**
	 * Skips to the Light Side player's next turn.  If Light Side is the current player, this will skip over an entire
	 * Dark Side turn.
	 * @throws DecisionResultInvalidException
	 */
	default void SkipToLSTurn() throws DecisionResultInvalidException { SkipToNextTurn(LS);	}

	/**
	 * Skips to the Light Side player's next turn.  If Light Side is the current player, this will skip over an entire
	 * Dark Side turn.  After making it to the LS player, passes to the given phase.
	 * @param phase The phase to skip to
	 * @throws DecisionResultInvalidException
	 */
	default void SkipToLSTurn(Phase phase) throws DecisionResultInvalidException {
		SkipToLSTurn();
		SkipToPhase(phase);
	}
	/**
	 * Skips to the Dark Side player's next turn.  If Dark Side is the current player, this will skip over an entire
	 * Light Side turn.  After making it to the DS player, passes to the given phase.
	 * @throws DecisionResultInvalidException
	 */
	default void SkipToDSTurn(Phase phase) throws DecisionResultInvalidException {
		SkipToDSTurn();
		SkipToPhase(phase);
	}

	/**
	 * Skips to the Dark Side player's next turn.  If Dark Side is the current player, this will skip over an entire
	 * Light Side turn.
	 * @throws DecisionResultInvalidException
	 */
	default void SkipToDSTurn() throws DecisionResultInvalidException { SkipToNextTurn(DS);	}

	/**
	 * Skips to a given player's next turn.  If they are the current player, this will skip over their opponent's turn.
	 * @param player The player whose turn it should be once we stop.
	 * @throws DecisionResultInvalidException
	 */
	default void SkipToNextTurn(String player) throws DecisionResultInvalidException {
		SkipToTurn(player, gameState().getPlayersLatestTurnNumber(player) + 1);
	}

	/**
	 * Skips forward in time by causing both players to pass until it is the given player's turn.  All the same
	 * caveats that affect SkipToPhase apply here.  This will attempt to move at most 20 turns in the future to avoid
	 * infinite loops.
	 * @param player Who should be the current player once we stop.
	 * @param targetTurn What number turn that player should be on.
	 * @throws DecisionResultInvalidException
	 */
	default void SkipToTurn(String player, int targetTurn) throws DecisionResultInvalidException {
		for(int attempts = 1; attempts <= 20; attempts++)
		{
			String currentPlayer = gameState().getCurrentPlayerId();
			int currentTurn = gameState().getPlayersLatestTurnNumber(currentPlayer);

			if(player.equals(currentPlayer) && currentTurn == targetTurn)
				break;

			SkipToPhase(Phase.DRAW);
			PassDrawActions();
			PassResponses("RECIRCULATED");

			if(attempts == 20)
			{
				throw new DecisionResultInvalidException("Could not arrive at target turn '" + targetTurn + "' for '"
						+ player + "'after 20 attempts!");
			}
		}
	}

	/**
	 * After the Dark Side player has peeked at a set of cards from a normally-hidden pile, they then dismiss those cards.
	 * @throws DecisionResultInvalidException
	 */
	default void DSDismissRevealedCards() throws DecisionResultInvalidException { DSPass(); }
	/**
	 * After the Light Side player has peeked at a set of cards from a normally-hidden pile, they then dismiss those cards.
	 * @throws DecisionResultInvalidException
	 */
	default void LSDismissRevealedCards() throws DecisionResultInvalidException { LSPass(); }
//    default void DismissRevealedCards() throws DecisionResultInvalidException {
//        DSDismissRevealedCards();
//        LSDismissRevealedCards();
//    }
//
//

}
