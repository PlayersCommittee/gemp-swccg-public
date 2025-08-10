package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.PhysicalCardImpl;

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
public interface GameProcedures extends Actions, Decisions, GameProperties, PileProperties {

	/**
	 * Causes the Dark Side player to activate the maximum amount of available force, causes Light Side to let the same
	 * amount pass without a react, and then causes both players to pass Activate phase actions.
	 * @return The total amount of force that was activated
	 */
	default int DSActivateMaxForceAndPass() {
		if(AwaitingDSActivatePhaseActions() && DSActionAvailable("Activate Force")) {
			DSChooseAction("Activate Force");
			int max = Math.min(GetDSReserveDeckCount() - 1, DSGetChoiceMax());
			DSDecided(max);
			if(max == 0) {
				DSPass();
				if(DSDecisionAvailable("You have not activated Force. Do you want to Pass?")) {
					DSChooseYes();
				}
			}

			if(LSDecisionAvailable("Choose amount of Force to allow opponent to activate without you performing a top-level action")) {
				LSDecided(max);
			}
			PassActivateActions();
			return max;
		}

		return -1;
	}

	/**
	 * Causes the Light Side player to activate the maximum amount of available force, causes Dark Side to let the same
	 * amount pass without a react, and then causes both players to pass Activate phase actions.
	 * @return The total amount of force that was activated
	 */
	default int LSActivateMaxForceAndPass() {
		if(AwaitingLSActivatePhaseActions() && LSActionAvailable("Activate Force")) {
			LSChooseAction("Activate Force");
			int max = Math.min(GetLSReserveDeckCount() - 1, LSGetChoiceMax());
			LSDecided(max);
			if(max == 0) {
				LSPass();
				if(LSDecisionAvailable("You have not activated Force. Do you want to Pass?")) {
					LSChooseYes();
				}
			}

			if(DSDecisionAvailable("Choose amount of Force to allow opponent to activate without you performing a top-level action")) {
				DSDecided(max);
			}
			PassActivateActions();
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
	 */
	default void DSDeployCardAndPassResponses(PhysicalCardImpl card, PhysicalCardImpl location) {
		DSDeployCard(card);
		assertTrue(DSDecisionAvailable("Choose where to deploy") || DSDecisionAvailable("Choose location where to deploy"));
		DSChooseCard(location);

		PassAllResponses();
	}

	/**
	 * During the Deploy phase, causes Light Side to deploy the given card to the given location and automatically pass
	 * any force use optional responses and deployment responses for both players.
	 * @param card The card to be deployed
	 * @param location Which location the card should be deployed to (should be in play already)
	 */
	default void LSDeployCardAndPassResponses(PhysicalCardImpl card, PhysicalCardImpl location) {
		LSDeployCard(card);
		assertTrue(LSDecisionAvailable("Choose where to deploy"));
		LSChooseCard(location);

		PassAllResponses();
	}

	/**
	 * Causes Dark Side to play the given card and automatically pass optional responses and deployment responses for both players.
	 * @param card The card to be played
	 */
	default void DSPlayCardAndPassResponses(PhysicalCardImpl card) { DSPlayCardAndPassResponses(card, null, null); }

	/**
	 * Causes Dark Side to play the given card and automatically pass optional responses and deployment responses for both players.
	 * @param card The card to be played
	 * @param text The text substring to match against action text of the card to be played
	 */
	default void DSPlayCardAndPassResponses(PhysicalCardImpl card, String text) { DSPlayCardAndPassResponses(card, text, null); }

	/**
	 * Causes Dark Side to play the given card and automatically pass optional responses and deployment responses for both players.
	 * @param card The card to be played
	 * @param target The card this card will target
	 */
	default void DSPlayCardAndPassResponses(PhysicalCardImpl card, PhysicalCardImpl target) { DSPlayCardAndPassResponses(card, null, target); }

	/**
	 * Causes Dark Side to play the given card and automatically pass optional responses and deployment responses for both players.
	 * @param card The card to be played
	 * @param text The text substring to match against action text of the card to be played
	 * @param target The card this card will target
	 */
	default void DSPlayCardAndPassResponses(PhysicalCardImpl card, String text, PhysicalCardImpl target) {
		assertTrue(DSCardPlayAvailable(card,text));
		DSPlayCard(card,text);
		if(target != null) {
			DSChooseCard(target);
		}
		PassAllResponses();
	}

	/**
	 * Causes Light Side to play the given card and automatically pass optional responses and deployment responses for both players.
	 * @param card The card to be played
	 */
	default void LSPlayCardAndPassResponses(PhysicalCardImpl card) { LSPlayCardAndPassResponses(card, null, null); }

	/**
	 * Causes Light Side to play the given card and automatically pass optional responses and deployment responses for both players.
	 * @param card The card to be played
	 * @param text The text substring to match against action text of the card to be played
	 */
	default void LSPlayCardAndPassResponses(PhysicalCardImpl card, String text) { LSPlayCardAndPassResponses(card, text, null); }

	/**
	 * Causes Light Side to play the given card and automatically pass optional responses and deployment responses for both players.
	 * @param card The card to be played
	 * @param target The card this card will target
	 */
	default void LSPlayCardAndPassResponses(PhysicalCardImpl card, PhysicalCardImpl target) { LSPlayCardAndPassResponses(card, null, target); }

	/**
	 * Causes Light Side to play the given card and automatically pass optional responses and deployment responses for both players.
	 * @param card The card to be played
	 * @param text The text substring to match against action text of the card to be played
	 * @param target The card this card will target
	 */
	default void LSPlayCardAndPassResponses(PhysicalCardImpl card, String text, PhysicalCardImpl target) {
		assertTrue(LSCardPlayAvailable(card,text));
		LSPlayCard(card,text);
		if(target != null) {
			LSChooseCard(target);
		}
		PassAllResponses();
	}

	/**
	 * Causes both players to pass during the Activate phase.
	 */
	default void PassActivateActions() { PassResponses("activate"); }
	/**
	 * Causes both players to pass during the Control phase.
	 */
	default void PassControlActions() { PassResponses("control"); }
	/**
	 * Causes both players to pass during the Deploy phase.
	 */
	default void PassDeployActions() { PassResponses("deploy"); }
	/**
	 * Causes both players to pass during the Move phase.
	 */
	default void PassMoveActions() { PassResponses("move"); }
	/**
	 * Causes both players to pass during the Battle phase.
	 */
	default void PassBattleActions() { PassResponses("battle"); }
	/**
	 * Causes both players to pass during the Draw phase.
	 */
	default void PassDrawActions() { PassResponses("draw"); }

	/**
	 * @return True if the Dark Side player is currently deciding what to do with a captured character.
	 */
	default boolean DSCaptureDecisionAvailable() { return DSDecisionAvailable("Choose option for capturing "); }

	/**
	 * Causes the Dark Side player to choose to let the recently-captured captive "escape" (go to the used pile).
	 */
	default void DSChooseEscape() { DSChoose("Escape"); }
	/**
	 * Causes the Dark Side player to choose to "seize" the recently-captured captive (attaching it to the captor).
	 */
	default void DSChooseSeizeCaptive() { DSChoose("Seize"); }

	/**
	 * @return True if the Light Side player is currently deciding what to do with a released captive.
	 */
	default boolean LSReleaseDecisionAvailable() { return LSDecisionAvailable("Choose release option for "); }

	/**
	 * Causes the Light Side player to choose to let the recently-released captive "escape" (go to the used pile).
	 */
	default void LSChooseEscape() { LSChoose("Escape"); }
	/**
	 * Causes the Light Side player to choose to let the recently-released captive "rally" (move to the current location).
	 */
	default void LSChooseRally() { LSChoose("Rally"); }

	/**
	 * When a card leaves the table, there are various responses.  This causes all players to pass all of them.
	 */
	default void PassCardLeavingTable() {
		PassResponses("ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE");
		PassResponses("FORFEITED_TO_LOST_PILE_FROM_TABLE");
		PassResponses("PUT_IN_CARD_PILE_FROM_OFF_TABLE");
	}

	/**
	 * When a Force Drain begins, there are various responses.  This causes all players to pass all of them.
	 */
	default void PassForceDrainStartResponses() {
		PassResponses("FORCE_DRAIN_INITIATED");
		PassResponses("FORCE_LOSS_INITIATED");
		PassForceDrainPendingResponses();
	}

	/**
	 * During a Force Drain, this response occurs between each paid Force.
	 */
	default void PassForceDrainPendingResponses() {
		PassResponses("ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE");
	}
	/**
	 * After a Force Drain, one last response occurs once the targeted player has paid all Force costs.
	 */
	default void PassForceDrainEndResponses() {
		PassResponses("FORCE_DRAIN_COMPLETED");
	}

	/**
	 * When we don't care about the responses being presented and we just want to skip forward to the next actual action.
	 */
	default void PassAllResponses() {
		for(int i = 0; i < 20; ++i) {
			if(!GetCurrentDecision().getText().toLowerCase().contains("optional response"))
				return;

			PassResponses("optional");
		}
	}

	/**
	 * Causes both players to pass, first the player with a current decision and then the other.
	 */
	default void PassResponses() {
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
	 * @param text Text which must be contained inside the decision
	 */
	default void PassResponses(String text) {
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
	 */
	default void PassCardAndForceUseResponses() {
		PassCardPlayResponses();
		PassForceUseResponses();
	}

	/**
	 * When a card is played, there are various responses.  This passes just the responses to the card being played.
	 */
	default void PassCardPlayResponses() { PassResponses("Playing <div"); }
	/**
	 * When a card is played, there are various responses.  This passes just the responses to the Force paid during that
	 * card's deployment.
	 */
	default void PassForceUseResponses() { PassResponses(" Force - Optional responses"); }


	/**
	 * Skips to the Battle phase.
	 */
    default void SkipToBattle() { SkipToPhase(Phase.BATTLE); }

	/**
	 * Causes players to spam pass until the provided target phase is current.  This process attempts to choose the
	 * first option of any required triggers, but may be brittle if there are any reacts that interrupt the pass-fest.
	 * Only 20 rounds of passing will be attempted to avoid infinite loops.
	 * @param target The phase the tester actually wants to be in
	 */
    default void SkipToPhase(Phase target) {
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
                throw new RuntimeException("Could not arrive at target '" + target + "' after 20 attempts!");
            }
        }
    }

	/**
	 * Regardless of the current player, skips to the Activate phase of the next player's turn.
	 */
	default void SkipToNextTurn() {
		SkipToNextTurn(game().getOpponent(gameState().getCurrentPlayerId()));
	}

	/**
	 * Skips to the Light Side player's next turn.  If Light Side is the current player, this will skip over an entire
	 * Dark Side turn.
	 */
	default void SkipToLSTurn() { SkipToNextTurn(LS);	}

	/**
	 * Skips to the Light Side player's next turn.  If Light Side is the current player, this will skip over an entire
	 * Dark Side turn.  After making it to the LS player, passes to the given phase.
	 * @param phase The phase to skip to
	 */
	default void SkipToLSTurn(Phase phase) {
		SkipToLSTurn();
		SkipToPhase(phase);
	}
	/**
	 * Skips to the Dark Side player's next turn.  If Dark Side is the current player, this will skip over an entire
	 * Light Side turn.  After making it to the DS player, passes to the given phase.
	 */
	default void SkipToDSTurn(Phase phase) {
		SkipToDSTurn();
		SkipToPhase(phase);
	}

	/**
	 * Skips to the Dark Side player's next turn.  If Dark Side is the current player, this will skip over an entire
	 * Light Side turn.
	 */
	default void SkipToDSTurn() { SkipToNextTurn(DS);	}

	/**
	 * Skips to a given player's next turn.  If they are the current player, this will skip over their opponent's turn.
	 * @param player The player whose turn it should be once we stop.
	 */
	default void SkipToNextTurn(String player) {
		SkipToTurn(player, gameState().getPlayersLatestTurnNumber(player) + 1);
	}

	/**
	 * Skips forward in time by causing both players to pass until it is the given player's turn.  All the same
	 * caveats that affect SkipToPhase apply here.  This will attempt to move at most 20 turns in the future to avoid
	 * infinite loops.
	 * @param player Who should be the current player once we stop.
	 * @param targetTurn What number turn that player should be on.
	 */
	default void SkipToTurn(String player, int targetTurn) {
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
				throw new RuntimeException("Could not arrive at target turn '" + targetTurn + "' for '"
						+ player + "'after 20 attempts!");
			}
		}
	}

	/**
	 * After the Dark Side player has peeked at a set of cards from a normally-hidden pile, they then dismiss those cards.
	 */
	default void DSDismissRevealedCards() { DSPass(); }
	/**
	 * After the Light Side player has peeked at a set of cards from a normally-hidden pile, they then dismiss those cards.
	 */
	default void LSDismissRevealedCards() { LSPass(); }
//    default void DismissRevealedCards() {
//        DSDismissRevealedCards();
//        LSDismissRevealedCards();
//    }
//
//

}
