package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Card_4_142_Tests {
	protected VirtualTableScenario GetScenario() {
		// Invasion is an Objective; if it stays in Reserve at StartGame, GEMP deploys it
		// instead of the default starting location. Park it out of play so existing tests
		// still start with Dantooine + Tibrin, then cheat it onto table after StartGame.
		var scn = new VirtualTableScenario(
				new HashMap<>()
				{{
					put("trooper", "1_28");
					put("luke", "1_19");
					put("protector", "5_63");
					put("walkway", "5_79");
					put("mosEisley", "1_133");
					put("rifle", "2_79");
					put("electropole", "14_64");
					put("kfc", "1_15");
					put("jpLeia", "6_32");
					put("ihabfat", "4_052");
					put("bowcaster", "2_77");
					put("chewieBowcaster", "8_86");
					put("jediLightsaber", "1_155");
					put("farm", "1_132");
					put("obiHut", "1_134");
					put("pilot", "1_27");
					put("yoda", "4_2");
					put("daughter", "8_8");
					put("landoBlaster", "7_160");
					put("savrip", "1_55");
					put("c3po", "1_5");
					put("artoo", "6_3");
					put("artooThreepio", "10_2");
					put("quads", "1_159");
					put("cec", "7_56");
					put("corellia", "2_61");
					put("luke2", "1_19");
					put("squadronAssignments", "9_39");
					put("red5", "2_71");
					put("shocking", "5_68");
					put("tk422", "7_48");
					put("tk422b", "7_48");
					put("lsPistol", "7_157");
					put("admiralsOrder", "9_4");
					put("insight", "13_49");
					put("revolution", "1_62");
				}},
				new HashMap<>()
				{{
					put("frustration", "4_142");
					put("frustration2", "4_142");
					put("badFeeling", "4_116");
					put("pondaBlaster", "7_323");
					put("bluffs", "2_150");
					put("naboo", "12_169");
					put("invasion", "14_113");
					put("nabooSwamp", "12_171");
					put("kissWookiee", "3_127");
					put("stormtrooper", "1_194");
					put("dsPistol", "7_319");
					put("cave", "4_158");
				}},
				10,
				10,
				StartingSetup.DefaultLSSpaceSystem,
				StartingSetup.DefaultDSSpaceSystem,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
		scn.MoveOutOfPlay(scn.GetDSCard("invasion"));
		return scn;
	}

	/**
	 * Cheat Invasion onto table after StartGame so ObjectiveDeployedTriggerAction
	 * (Naboo / Flagship / Swamp / Droid Racks from Reserve) does not fire.
	 * While-active modifiers such as CancelForceIconsModifier only apply once
	 * objective deployment is marked complete.
	 */
	private void PutInvasionInPlayWithoutStartingDeploy(VirtualTableScenario scn) {
		var invasion = scn.GetDSCard("invasion");
		scn.MoveCardsToDSSideOfTable(invasion);
		invasion.setObjectiveDeploymentComplete(true);
		scn.gameState().reapplyAffectingForCard(scn.game(), invasion);
		scn.PassAllResponses();
	}

	/**
	 * Play Frustration in Dark's Control phase, pass responses, and dismiss the peek window
	 * so Dark can choose a target.
	 */
	private void PeekFrustrationAtHand(VirtualTableScenario scn) {
		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(scn.GetDSCard("frustration"));
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}
	}

	/**
	 * Same as PeekFrustrationAtHand, then target the Rebel Trooper.
	 */
	private void PlayFrustrationTargetingTrooper(VirtualTableScenario scn) {
		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(scn.GetDSCard("frustration"));
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}
		scn.DSChooseCard(scn.GetLSCard("trooper"));
		scn.PassAllResponses();
	}

	/**
	 * Run through Light's turn and to the end of Dark's next turn, including the
	 * lose-from-hand prompt if it fires.
	 * SkipToTurn can stall at DRAW when an End of Turn required trigger is pending,
	 * because SkipToPhase returns immediately when the current phase is already DRAW.
	 * Drain remaining end-of-turn decisions after DS's next Draw phase instead.
	 */
	private void AdvanceThroughEndOfDSNextTurn(VirtualTableScenario scn) {
		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.DRAW);
		scn.PassDrawActions();
		DrainPendingDecisions(scn, 30);
	}

	/**
	 * Click through leftover end-of-turn prompts until the next action window or nothing is pending.
	 */
	private void DrainPendingDecisions(VirtualTableScenario scn, int maxAttempts) {
		for (int i = 0; i < maxAttempts; i++) {
			if (!scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
				return;
			}
			String text = scn.GetCurrentDecision().getText().toLowerCase();
			// Next turn/phase action window means end-of-turn processing is finished.
			if (text.contains("action or pass")) {
				return;
			}
			if (text.contains("lose") || text.contains("required") || text.contains("from hand") || text.contains("choose card")) {
				if (scn.DSAnyDecisionsAvailable()) {
					scn.DSDecided(0);
				}
				else {
					scn.LSDecided(0);
				}
			}
			else if (text.contains("optional")) {
				scn.PassAllResponses();
			}
			else if (text.contains("recirculated")) {
				scn.PassResponses("RECIRCULATED");
			}
			else if (text.contains("draw")) {
				scn.PassDrawActions();
			}
			else if (text.contains("about_to_lose") || text.contains("put_in_card_pile") || text.contains("forfeited")) {
				scn.PassCardLeavingTable();
			}
			else {
				throw new RuntimeException("Unhandled decision while draining end of turn: " + scn.GetCurrentDecision().getText());
			}
		}
	}

	/**
	 * Advance to the end of DS next turn and stop when LS is choosing a matching card to lose from hand.
	 * Do not auto-pick that choice.
	 */
	private void AdvanceUntilLsChooseLoseFromHand(VirtualTableScenario scn) {
		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.DRAW);
		scn.PassDrawActions();
		for (int i = 0; i < 30; i++) {
			if (scn.LSAnyDecisionsAvailable()) {
				String lsText = scn.LSGetDecision().getText().toLowerCase();
				if (lsText.contains("choose card")) {
					return;
				}
			}
			if (!scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
				return;
			}
			String text = scn.GetCurrentDecision().getText().toLowerCase();
			if (text.contains("action or pass")) {
				return;
			}
			if (text.contains("lose") || text.contains("required")) {
				if (scn.DSAnyDecisionsAvailable()) {
					scn.DSDecided(0);
				}
				else {
					scn.LSDecided(0);
				}
			}
			else if (text.contains("optional")) {
				scn.PassAllResponses();
			}
			else if (text.contains("recirculated")) {
				scn.PassResponses("RECIRCULATED");
			}
			else if (text.contains("draw")) {
				scn.PassDrawActions();
			}
			else if (text.contains("about_to_lose") || text.contains("put_in_card_pile") || text.contains("forfeited")) {
				scn.PassCardLeavingTable();
			}
			else {
				throw new RuntimeException("Unhandled decision while waiting for LS hand choice: " + scn.GetCurrentDecision().getText());
			}
		}
	}


	/**
	 * Build a one-line dump of the current prompt for error messages when a test hits an unexpected choice.
	 */
	private String decisionSnapshot(VirtualTableScenario scn) {
		StringBuilder sb = new StringBuilder();
		sb.append("phase=").append(scn.GetCurrentPhase());
		if (scn.DSAnyDecisionsAvailable()) {
			sb.append(" DS[").append(scn.DSGetDecision().getText()).append("]");
			try {
				sb.append(" DSactions=").append(scn.GetDSAvailableActions());
			}
			catch (Exception ignored) {
			}
		}
		if (scn.LSAnyDecisionsAvailable()) {
			sb.append(" LS[").append(scn.LSGetDecision().getText()).append("]");
			try {
				sb.append(" LSactions=").append(scn.GetLSAvailableActions());
			}
			catch (Exception ignored) {
			}
		}
		if (!scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
			sb.append(" no pending decisions");
		}
		return sb.toString();
	}

	/**
	 * True if Light can play or use this card as a response right now.
	 */
	private boolean lsPlayAvailable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
		return scn.LSAnyDecisionsAvailable() && (scn.LSCardPlayAvailable(card) || scn.LSCardActionAvailable(card));
	}

	/**
	 * True if Dark can play or use this card as a response right now.
	 */
	private boolean dsPlayAvailable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
		return scn.DSAnyDecisionsAvailable() && (scn.DSCardPlayAvailable(card) || scn.DSCardActionAvailable(card));
	}

	@Test
	public void FrustrationStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Frustration
		 * Uniqueness: Unique
		 * Side: Dark
		 * Type: Interrupt
		 * Subtype: Lost
		 * Destiny: 3
		 * Icons: Dagobah
		 * Game Text: During your control phase, peek at opponent's hand and target one non-Interrupt card you find
		 * 		there that has a deploy cost < total number of Light Side Force icons on table. Opponent must deploy
		 * 		a card of that title by the end of your next turn, or lose a card of that title from hand (if possible).
		 * Lore: "Rrraaaarrr!"
		 * Set: Dagobah
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("frustration").getBlueprint();

		assertEquals("Frustration", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertEquals(3, card.getDestiny(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(CardSubtype.LOST, card.getCardSubtype());
		assertEquals(1, card.getIconCount(Icon.DAGOBAH));
		assertEquals(ExpansionSet.DAGOBAH, card.getExpansionSet());
		assertEquals(Rarity.R, card.getRarity());
	}

	@Test
	public void FrustrationTargetsPrintedCostBelowLightIconsButNotInterruptsOrTooExpensive() {
		// Default systems (Dantooine + Tibrin) have 2 Light Side Force icons on table.
		// Rebel Trooper (deploy 1) is a valid target.
		// Luke (deploy 3) is not < 2. Protector is an Interrupt.
		var scn = GetScenario();

		var frustration = scn.GetDSCard("frustration");
		var trooper = scn.GetLSCard("trooper");
		var luke = scn.GetLSCard("luke");
		var protector = scn.GetLSCard("protector");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(trooper, luke, protector);

		scn.StartGame();

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(frustration);
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}

		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
		assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
		assertTrue(scn.DSHasCardChoiceNotAvailable(protector));
	}

	@Test
	public void FrustrationDoesNotLoseFromHandIfOpponentDeploysThatTitleInTime() {
		var scn = GetScenario();

		var frustration = scn.GetDSCard("frustration");
		var trooper = scn.GetLSCard("trooper");
		var walkway = scn.GetLSCard("walkway");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(trooper);

		scn.StartGame();

		PlayFrustrationTargetingTrooper(scn);

		// Site is added after targeting so it does not change the Light icon count used to target
		scn.MoveLocationToTable(walkway);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(trooper));
		scn.LSDeployCardAndPassResponses(trooper, walkway);
		assertEquals(Zone.AT_LOCATION, trooper.getZone());

		// Advance through the playing player's next turn; trooper should not be lost
		AdvanceThroughEndOfDSNextTurn(scn);
		assertEquals(Zone.AT_LOCATION, trooper.getZone());
		assertEquals(0, scn.GetLSLostPileCount());
	}

	@Test
	public void FrustrationLosesThatTitleFromHandIfNotDeployedAndDoesNotCrashIfNoneInHand() {
		var scn = GetScenario();

		var frustration = scn.GetDSCard("frustration");
		var trooper = scn.GetLSCard("trooper");
		var luke = scn.GetLSCard("luke");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(trooper, luke);

		scn.StartGame();

		PlayFrustrationTargetingTrooper(scn);

		assertEquals(Zone.HAND, trooper.getZone());

		// Do not deploy the trooper. End of DS next turn should lose it from hand.
		AdvanceThroughEndOfDSNextTurn(scn);

		assertEquals(Zone.TOP_OF_LOST_PILE, trooper.getZone());
		assertEquals(1, scn.GetLSLostPileCount());
		assertEquals(Zone.HAND, luke.getZone());
	}

	@Test
	public void FrustrationDoesNotCrashIfTargetedTitleIsNoLongerInHand() {
		var scn = GetScenario();

		var frustration = scn.GetDSCard("frustration");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(trooper);

		scn.StartGame();

		PlayFrustrationTargetingTrooper(scn);

		// Remove the targeted title from hand before the deadline. Used pile is not consumed by Activate.
		scn.MoveCardsToTopOfLSUsedPile(trooper);
		assertNotEquals(Zone.HAND, trooper.getZone());

		AdvanceThroughEndOfDSNextTurn(scn);

		assertNotEquals(Zone.HAND, trooper.getZone());
		assertEquals(0, scn.GetLSLostPileCount());
	}

	@Test
	public void ProtectorCanCancelFrustration() {
		var scn = GetScenario();

		var frustration = scn.GetDSCard("frustration");
		var trooper = scn.GetLSCard("trooper");
		var protector = scn.GetLSCard("protector");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(trooper, protector);

		scn.StartGame();

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(frustration);

		assertTrue(scn.LSCardActionAvailable(protector) || scn.LSCardPlayAvailable(protector));
		scn.LSPlayCard(protector);
		scn.PassAllResponses();

		// Frustration's effect should not have targeted/set the delayed obligation
		assertEquals(Zone.HAND, trooper.getZone());

		AdvanceThroughEndOfDSNextTurn(scn);

		assertEquals(Zone.HAND, trooper.getZone());
		assertEquals(Zone.TOP_OF_LOST_PILE, protector.getZone());
	}

	@Test
	public void FrustrationKeepsFirstObligationWhenSameCopyIsRetrievedAndReplayed() {
		var scn = GetScenario();

		var frustration = scn.GetDSCard("frustration");
		var trooper = scn.GetLSCard("trooper");
		var kfc = scn.GetLSCard("kfc");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(trooper, kfc);

		scn.StartGame();

		PlayFrustrationTargetingTrooper(scn);
		// Retrieve the same unique copy (Brangus Glee style)
		scn.MoveCardsToDSHand(frustration);

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(frustration));
		scn.DSPlayCard(frustration);
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}
		scn.DSChooseCard(kfc);
		scn.PassAllResponses();

		scn.SkipToPhase(Phase.DRAW);
		scn.PassDrawActions();
		DrainPendingDecisions(scn, 30);

		assertEquals(Zone.TOP_OF_LOST_PILE, trooper.getZone());
		assertEquals(Zone.HAND, kfc.getZone());

		scn.SkipToDSTurn(Phase.DRAW);
		scn.PassDrawActions();
		DrainPendingDecisions(scn, 30);

		assertEquals(Zone.TOP_OF_LOST_PILE, kfc.getZone());
	}

	@Test
	public void FrustrationCanTargetHuntingRifleUsingLowerPrintedCost() {
		// Default systems have 2 Light Side Force icons. Rifle is 1 or 3; lowest 1 is < 2.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var rifle = scn.GetLSCard("rifle");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(rifle);

		scn.StartGame();
		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceAvailable(rifle));
	}

	@Test
	public void FrustrationCannotTargetElectropoleWithNoPrintedDeployCost() {
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var electropole = scn.GetLSCard("electropole");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(electropole, trooper);

		scn.StartGame();
		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceNotAvailable(electropole));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationCanTargetPrintedZeroDeployCost() {
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var kfc = scn.GetLSCard("kfc");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(kfc);

		scn.StartGame();
		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceAvailable(kfc));
	}

	@Test
	public void FrustrationAppliesGlobalDeployCostModifierFromBadFeelingHaveI() {
		// Default systems 2 LS icons + Mos Eisley 2 = 4. Luke is 3, or 5 with Bad Feeling Have I.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var frustration2 = scn.GetDSCard("frustration2");
		var badFeeling = scn.GetDSCard("badFeeling");
		var luke = scn.GetLSCard("luke");
		var trooper = scn.GetLSCard("trooper");
		var mosEisley = scn.GetLSCard("mosEisley");

		scn.MoveCardsToDSHand(frustration, frustration2);
		scn.MoveCardsToLSHand(luke, trooper);

		scn.StartGame();
		scn.MoveLocationToTable(mosEisley);

		PeekFrustrationAtHand(scn);
		assertTrue(scn.DSHasCardChoiceAvailable(luke));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
		scn.DSChooseCard(trooper);
		scn.PassAllResponses();

		scn.MoveCardsToDSSideOfTable(badFeeling);
		scn.PassAllResponses();

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(frustration2));
		scn.DSPlayCard(frustration2);
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}

		assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationCountsJediMasterTowardLightForceIconsOnTable() {
		// Default systems have 2 LS location icons. Rebel Pilot is constructor deploy 2.
		// Without Yoda, 2 is not < 2 so Pilot is not a target. With Dagobah Yoda on table,
		// Jedi Master adds 1 (total 3), so deploy 2 becomes targetable. Luke (3) is still not < 3.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var frustration2 = scn.GetDSCard("frustration2");
		var pilot = scn.GetLSCard("pilot");
		var luke = scn.GetLSCard("luke");
		var trooper = scn.GetLSCard("trooper");
		var yoda = scn.GetLSCard("yoda");

		scn.MoveCardsToDSHand(frustration, frustration2);
		scn.MoveCardsToLSHand(pilot, luke, trooper);

		scn.StartGame();
		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceNotAvailable(pilot));
		assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
		scn.DSChooseCard(trooper);
		scn.PassAllResponses();

		scn.MoveCardsToLocation(scn.GetLSStartingLocation(), yoda);

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(frustration2));
		scn.DSPlayCard(frustration2);
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}

		assertTrue(scn.DSHasCardChoiceAvailable(pilot));
		assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationCountsDaughterOfSkywalkerLightForceIconTowardIconsOnTable() {
		// Daughter Of Skywalker (8_8) uses IconModifier to give +1 LIGHT_FORCE to her same exterior site.
		// Tatooine: Bluffs (2_150) is exterior with 0 printed Force icons, so default systems stay at 2
		// until Daughter arrives and bumps the total to 3 (Pilot deploy 2 becomes targetable).
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var frustration2 = scn.GetDSCard("frustration2");
		var pilot = scn.GetLSCard("pilot");
		var luke = scn.GetLSCard("luke");
		var trooper = scn.GetLSCard("trooper");
		var daughter = scn.GetLSCard("daughter");
		var bluffs = scn.GetDSCard("bluffs");

		scn.MoveCardsToDSHand(frustration, frustration2);
		scn.MoveCardsToLSHand(pilot, luke, trooper);

		scn.StartGame();
		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceNotAvailable(pilot));
		assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
		scn.DSChooseCard(trooper);
		scn.PassAllResponses();

		scn.MoveLocationToTable(bluffs);
		scn.MoveCardsToLocation(bluffs, daughter);

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(frustration2));
		scn.DSPlayCard(frustration2);
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}

		assertTrue(scn.DSHasCardChoiceAvailable(pilot));
		assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationCannotTargetCardThatDeploysFreeEncodedAsZero() {
		// JP Leia is constructor deploy 0 plus DeploysFreeModifier. Free is not a cost.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var jpLeia = scn.GetLSCard("jpLeia");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(jpLeia, trooper);

		scn.StartGame();
		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceNotAvailable(jpLeia));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationCannotTargetLocationWithNoPrintedDeployCost() {
		// AbstractLocation.getDeployCost() is hardcoded 0. That is not a printed cost.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var mosEisley = scn.GetLSCard("mosEisley");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(mosEisley, trooper);

		scn.StartGame();
		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceNotAvailable(mosEisley));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationRevertUndoesObligation() {
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(trooper);

		scn.StartGame();
		PlayFrustrationTargetingTrooper(scn);
		assertEquals(Zone.HAND, trooper.getZone());

		scn.IssueRevert("Start of Dark Side Player's control phase #1");
		frustration = scn.GetPostRevertCard(frustration);
		trooper = scn.GetPostRevertCard(trooper);

		assertEquals(Zone.HAND, frustration.getZone());
		assertEquals(Zone.HAND, trooper.getZone());

		AdvanceThroughEndOfDSNextTurn(scn);
		trooper = scn.GetPostRevertCard(trooper);
		assertEquals(Zone.HAND, trooper.getZone());
	}

	@Test
	public void FrustrationRevertAfterFirstPlayKeepsFirstObligation() {
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var frustration2 = scn.GetDSCard("frustration2");
		var trooper = scn.GetLSCard("trooper");
		var kfc = scn.GetLSCard("kfc");

		scn.MoveCardsToDSHand(frustration, frustration2);
		scn.MoveCardsToLSHand(trooper, kfc);

		scn.StartGame();
		PlayFrustrationTargetingTrooper(scn);

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(frustration2));
		scn.DSPlayCard(frustration2);
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}
		scn.DSChooseCard(kfc);
		scn.PassAllResponses();

		scn.IssueRevert("Start of Dark Side Player's control phase #2");
		frustration2 = scn.GetPostRevertCard(frustration2);
		trooper = scn.GetPostRevertCard(trooper);
		kfc = scn.GetPostRevertCard(kfc);

		assertEquals(Zone.HAND, frustration2.getZone());
		assertEquals(Zone.HAND, trooper.getZone());
		assertEquals(Zone.HAND, kfc.getZone());

		scn.SkipToPhase(Phase.DRAW);
		scn.PassDrawActions();
		DrainPendingDecisions(scn, 30);

		trooper = scn.GetPostRevertCard(trooper);
		kfc = scn.GetPostRevertCard(kfc);
		assertEquals(Zone.TOP_OF_LOST_PILE, trooper.getZone());
		assertEquals(Zone.HAND, kfc.getZone());
	}

	@Test
	public void IHaveABadFeelingAboutThisCannotRetargetFrustrationBecauseTargetIsChosenInResultStep() {
		// ScompLink Extra Card Data: IHABFAT may not retarget something that isn't chosen until
		// the result step (e.g. Twi'lek Advisor taking a card into hand from Reserve Deck).
		// Frustration currently chooses its target in the result step (after peek), not as a
		// targeting-step primary target, so Light Side cannot play IHABFAT as a response.
		var scn = GetScenario();

		var frustration = scn.GetDSCard("frustration");
		var ihabfat = scn.GetLSCard("ihabfat");
		var trooper = scn.GetLSCard("trooper");
		var kfc = scn.GetLSCard("kfc");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(ihabfat, trooper, kfc);

		scn.StartGame();

		scn.LSActivateForceCheat(3);

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(frustration);

		// Optional-response window is open; peek has not happened and there are no primary target groups.
		assertFalse(scn.LSCardPlayAvailable(ihabfat));

		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}
		scn.DSChooseCard(trooper);
		scn.PassAllResponses();

		AdvanceThroughEndOfDSNextTurn(scn);
		assertEquals(Zone.TOP_OF_LOST_PILE, trooper.getZone());
	}

	@Test
	public void FrustrationCannotTargetWeaponsWhoseCostIsUndefinedUntilDeploy() {
		// Default systems 2 LS icons + Mos Eisley 2 + Lars' Moisture Farm 2 + Obi-Wan's Hut 2 = 8.
		// Fake GEMP printed 7 would be legal (7 < 8). X is undefined until deploy, so Bowcaster and Jedi Lightsaber are not targets.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var bowcaster = scn.GetLSCard("bowcaster");
		var jediLightsaber = scn.GetLSCard("jediLightsaber");
		var trooper = scn.GetLSCard("trooper");
		var mosEisley = scn.GetLSCard("mosEisley");
		var farm = scn.GetLSCard("farm");
		var obiHut = scn.GetLSCard("obiHut");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(bowcaster, jediLightsaber, trooper);

		scn.StartGame();
		scn.MoveLocationToTable(mosEisley);
		scn.MoveLocationToTable(farm);
		scn.MoveLocationToTable(obiHut);

		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceNotAvailable(bowcaster));
		assertTrue(scn.DSHasCardChoiceNotAvailable(jediLightsaber));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationCanTargetChewbaccasBowcasterUsingCostFour() {
		// Default systems 2 LS icons: cost 4 is not < 2, so Chewie's Bowcaster is not a target.
		// Mos Eisley 2 + Chasm Walkway 1 = 5 total; 4 < 5, so it becomes targetable.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var frustration2 = scn.GetDSCard("frustration2");
		var chewieBowcaster = scn.GetLSCard("chewieBowcaster");
		var trooper = scn.GetLSCard("trooper");
		var mosEisley = scn.GetLSCard("mosEisley");
		var walkway = scn.GetLSCard("walkway");

		scn.MoveCardsToDSHand(frustration, frustration2);
		scn.MoveCardsToLSHand(chewieBowcaster, trooper);

		scn.StartGame();
		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceNotAvailable(chewieBowcaster));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
		scn.DSChooseCard(trooper);
		scn.PassAllResponses();

		scn.MoveLocationToTable(mosEisley);
		scn.MoveLocationToTable(walkway);

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(frustration2));
		scn.DSPlayCard(frustration2);
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}

		assertTrue(scn.DSHasCardChoiceAvailable(chewieBowcaster));
	}

	@Test
	public void FrustrationIgnoresFreeOptionOnLandosBlasterRifleAndUsesCostThree() {
		// Default systems have 2 Light icons.
		// Lando's Blaster Rifle is free on Lando or 3 on other warrior. Free is not a number; remaining cost 3 is not < 2.
		// Ponda's blaster is free or 2; 2 is not < 2. Dark card, so it lives in DS extras and is moved into LS hand.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var landoBlaster = scn.GetLSCard("landoBlaster");
		var pondaBlaster = scn.GetDSCard("pondaBlaster");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(landoBlaster, pondaBlaster, trooper);

		scn.StartGame();
		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceNotAvailable(landoBlaster));
		assertTrue(scn.DSHasCardChoiceNotAvailable(pondaBlaster));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationCanTargetMantellianSavripPrintedThreeWithoutC3PO() {
		// Savrip printed cost 3. Default systems 2 LS icons + Mos Eisley 2 = 4. 3 < 4, so Savrip is a target.
		// C-3PO is not on table, so the free option is not active.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var savrip = scn.GetLSCard("savrip");
		var trooper = scn.GetLSCard("trooper");
		var mosEisley = scn.GetLSCard("mosEisley");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(savrip, trooper);

		scn.StartGame();
		scn.MoveLocationToTable(mosEisley);

		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceAvailable(savrip));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationCannotTargetMantellianSavripWhenC3POMakesItDeployFree() {
		// Same 4 Light icons. C-3PO on table makes Savrip deploy free. Free is not a deploy cost, so Savrip is not a target.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var savrip = scn.GetLSCard("savrip");
		var trooper = scn.GetLSCard("trooper");
		var c3po = scn.GetLSCard("c3po");
		var mosEisley = scn.GetLSCard("mosEisley");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(savrip, trooper);

		scn.StartGame();
		scn.MoveLocationToTable(mosEisley);
		scn.MoveCardsToLocation(scn.GetLSStartingLocation(), c3po);

		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceNotAvailable(savrip));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationDeployingArtooAndThreepioSatisfiesArtooTitleObligation() {
		// Target JP Artoo 6_3 (titles ["Artoo"]). Deploying combo 10_2 (titles ["Artoo","Threepio"])
		// satisfies the obligation because sameTitleAs matches overlapping combo titles.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var artoo = scn.GetLSCard("artoo");
		var artooThreepio = scn.GetLSCard("artooThreepio");
		var mosEisley = scn.GetLSCard("mosEisley");
		var walkway = scn.GetLSCard("walkway");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(artoo, artooThreepio);

		scn.StartGame();
		// Artoo printed 4. Default systems 2 + Mos Eisley 2 + walkway 1 = 5, so 4 < 5 at peek.
		scn.MoveLocationToTable(mosEisley);
		scn.MoveLocationToTable(walkway);

		PeekFrustrationAtHand(scn);
		assertTrue(scn.DSHasCardChoiceAvailable(artoo));
		scn.DSChooseCard(artoo);
		scn.PassAllResponses();

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(artooThreepio));
		scn.LSDeployCardAndPassResponses(artooThreepio, walkway);
		assertEquals(Zone.AT_LOCATION, artooThreepio.getZone());

		AdvanceThroughEndOfDSNextTurn(scn);
		assertEquals(Zone.AT_LOCATION, artooThreepio.getZone());
		assertEquals(Zone.HAND, artoo.getZone());
		assertEquals(0, scn.GetLSLostPileCount());
	}

	@Test
	public void FrustrationLosesArtooAndThreepioThatEnteredHandAfterArtooRemoved() {
		// Target Artoo, remove it from hand, combo of that title enters hand, combo is lost at deadline.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var artoo = scn.GetLSCard("artoo");
		var artooThreepio = scn.GetLSCard("artooThreepio");
		var mosEisley = scn.GetLSCard("mosEisley");
		var walkway = scn.GetLSCard("walkway");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(artoo);

		scn.StartGame();
		scn.MoveLocationToTable(mosEisley);
		scn.MoveLocationToTable(walkway);

		PeekFrustrationAtHand(scn);
		scn.DSChooseCard(artoo);
		scn.PassAllResponses();

		scn.MoveCardsToTopOfLSUsedPile(artoo);
		scn.MoveCardsToLSHand(artooThreepio);

		AdvanceThroughEndOfDSNextTurn(scn);

		assertEquals(Zone.TOP_OF_LOST_PILE, artooThreepio.getZone());
		assertEquals(1, scn.GetLSLostPileCount());
	}

	@Test
	public void FrustrationOwnerChoosesBetweenArtooAndComboToLoseFromHand() {
		// Both JP Artoo and combo Artoo & Threepio in hand at deadline. LS chooses which matching title to lose.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var artoo = scn.GetLSCard("artoo");
		var artooThreepio = scn.GetLSCard("artooThreepio");
		var mosEisley = scn.GetLSCard("mosEisley");
		var walkway = scn.GetLSCard("walkway");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(artoo, artooThreepio);

		scn.StartGame();
		scn.MoveLocationToTable(mosEisley);
		scn.MoveLocationToTable(walkway);

		PeekFrustrationAtHand(scn);
		scn.DSChooseCard(artoo);
		scn.PassAllResponses();

		AdvanceUntilLsChooseLoseFromHand(scn);

		assertTrue(scn.LSHasCardChoiceAvailable(artoo));
		assertTrue(scn.LSHasCardChoiceAvailable(artooThreepio));
		scn.LSChooseCard(artooThreepio);
		DrainPendingDecisions(scn, 30);

		assertEquals(Zone.TOP_OF_LOST_PILE, artooThreepio.getZone());
		assertEquals(Zone.HAND, artoo.getZone());
		assertEquals(1, scn.GetLSLostPileCount());
	}

	@Test
	public void FrustrationDeployingArtooSatisfiesArtooAndThreepioTitleObligation() {
		// Vice versa: target combo 10_2, deploying standalone JP Artoo 6_3 satisfies the overlapping title.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var artoo = scn.GetLSCard("artoo");
		var artooThreepio = scn.GetLSCard("artooThreepio");
		var mosEisley = scn.GetLSCard("mosEisley");
		var walkway = scn.GetLSCard("walkway");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(artoo, artooThreepio);

		scn.StartGame();
		// Combo printed 3. Default systems 2 + Mos Eisley 2 = 4, so 3 < 4 at peek.
		scn.MoveLocationToTable(mosEisley);

		PeekFrustrationAtHand(scn);
		assertTrue(scn.DSHasCardChoiceAvailable(artooThreepio));
		scn.DSChooseCard(artooThreepio);
		scn.PassAllResponses();

		// Walkway after targeting so icon count at peek stays 4; needed as a deploy site for Artoo.
		scn.MoveLocationToTable(walkway);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(artoo));
		scn.LSDeployCardAndPassResponses(artoo, walkway);
		assertEquals(Zone.AT_LOCATION, artoo.getZone());

		AdvanceThroughEndOfDSNextTurn(scn);
		assertEquals(Zone.AT_LOCATION, artoo.getZone());
		assertEquals(Zone.HAND, artooThreepio.getZone());
		assertEquals(0, scn.GetLSLostPileCount());
	}


	@Test
	public void FrustrationCountsNabooSystemLightIconsWhenInvasionIsNotOnTable() {
		// Default systems 2 LS icons + Dark Naboo system 12_169 printed 2 LS = 4.
		// Rebel Pilot deploy 2 is < 4. Luke deploy 3 is < 4. Trooper deploy 1 is < 4.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var naboo = scn.GetDSCard("naboo");
		var pilot = scn.GetLSCard("pilot");
		var luke = scn.GetLSCard("luke");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(pilot, luke, trooper);

		scn.StartGame();
		scn.MoveLocationToTable(naboo);

		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceAvailable(pilot));
		assertTrue(scn.DSHasCardChoiceAvailable(luke));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationDoesNotCountNabooSystemLightIconsCanceledByInvasion() {
		// Invasion 14_113: opponent's Force icons at Naboo system are canceled (system only).
		// Default systems stay at 2 LS. Pilot deploy 2 is not < 2. Luke 3 is not. Trooper 1 is.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var naboo = scn.GetDSCard("naboo");
		var pilot = scn.GetLSCard("pilot");
		var luke = scn.GetLSCard("luke");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(pilot, luke, trooper);

		scn.StartGame();
		scn.MoveLocationToTable(naboo);
		PutInvasionInPlayWithoutStartingDeploy(scn);

		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceNotAvailable(pilot));
		assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationStillCountsNabooSwampLightIconsWhenInvasionCancelsSystemIcons() {
		// Invasion cancels only Naboo system LS icons. Swamp 12_171 still has 1 LS.
		// Default 2 + swamp 1 = 3. Pilot 2 is < 3. Luke 3 is not. Trooper 1 is.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var naboo = scn.GetDSCard("naboo");
		var nabooSwamp = scn.GetDSCard("nabooSwamp");
		var pilot = scn.GetLSCard("pilot");
		var luke = scn.GetLSCard("luke");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(pilot, luke, trooper);

		scn.StartGame();
		scn.MoveLocationToTable(naboo);
		scn.MoveLocationToTable(nabooSwamp);
		PutInvasionInPlayWithoutStartingDeploy(scn);

		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceAvailable(pilot));
		assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}


	/**
	 * Corellian Engineering Corporation (7_56) gives your Quad Laser Cannons DeploysFreeModifier
	 * while attached to Corellia. Frustration must treat that always-free deploy as not a cost.
	 */
	@Test
	public void FrustrationCanTargetQuadLaserCannonsWithoutCEC() {
		// Quads printed cost 2 via DefinedByGameTextDeployCostModifier.
		// Default systems 2 LS icons + Corellia 1 = 3, so 2 is less than 3.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var quads = scn.GetLSCard("quads");
		var trooper = scn.GetLSCard("trooper");
		var corellia = scn.GetLSCard("corellia");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(quads, trooper);

		scn.StartGame();
		scn.MoveLocationToTable(corellia);

		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceAvailable(quads));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationCannotTargetQuadLaserCannonsWhenCECIsAttachedToCorellia() {
		// Same 3 Light icons. CEC on Corellia makes Quads deploy free. Free is not a deploy cost.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var quads = scn.GetLSCard("quads");
		var trooper = scn.GetLSCard("trooper");
		var cec = scn.GetLSCard("cec");
		var corellia = scn.GetLSCard("corellia");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(quads, trooper, cec);

		scn.StartGame();
		scn.MoveLocationToTable(corellia);
		scn.LSActivateForceCheat(5);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(cec) || scn.LSCardPlayAvailable(cec));
		scn.LSDeployCardAndPassResponses(cec, corellia);
		assertEquals(Zone.ATTACHED, cec.getZone());

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(frustration) || scn.DSCardActionAvailable(frustration));
		scn.DSPlayCard(frustration);
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}

		assertTrue(scn.DSHasCardChoiceNotAvailable(quads));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}


	@Test
	public void FrustrationSquadronAssignmentsSimultaneousLukeDeploySatisfiesTitle() {
		// Squadron Assignments 9_39: reveal a pilot from hand, take matching unpiloted starfighter
		// from Reserve (Red 5 2_71), and deploy both simultaneously. Two Lukes 1_19 in hand;
		// deploying one Luke this way must satisfy Frustration on the Luke title.
		// Mos Eisley 1_133 is on table so printed 3 is < 4 LS icons.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var luke = scn.GetLSCard("luke");
		var luke2 = scn.GetLSCard("luke2");
		var red5 = scn.GetLSCard("red5");
		var squadronAssignments = scn.GetLSCard("squadronAssignments");
		var mosEisley = scn.GetLSCard("mosEisley");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(luke, luke2);

		scn.StartGame();
		scn.MoveLocationToTable(mosEisley);
		scn.MoveCardsToLSSideOfTable(squadronAssignments);
		// Keep Red 5 out of Reserve until LS Deploy so Activate does not draw it as Force.
		scn.MoveOutOfPlay(red5);

		PeekFrustrationAtHand(scn);
		if (!scn.DSHasCardChoiceAvailable(luke) && !scn.DSHasCardChoiceAvailable(luke2)) {
			throw new RuntimeException("Expected Luke as Frustration target, decision: " + decisionSnapshot(scn));
		}
		scn.DSChooseCard(luke);
		scn.PassAllResponses();

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSActivateForceCheat(8);
		scn.MoveCardsToTopOfLSReserveDeck(red5);
		if (!scn.LSCardActionAvailable(squadronAssignments) && !scn.LSActionAvailable("Reveal")) {
			throw new RuntimeException("Squadron Assignments action not available: " + decisionSnapshot(scn));
		}
		if (scn.LSCardActionAvailable(squadronAssignments)) {
			scn.LSUseCardAction(squadronAssignments);
		}
		else {
			scn.LSChooseAction("Reveal");
		}

		for (int i = 0; i < 25; i++) {
			if (!scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
				break;
			}
			String dsText = scn.DSAnyDecisionsAvailable() ? scn.DSGetDecision().getText().toLowerCase() : "";
			String lsText = scn.LSAnyDecisionsAvailable() ? scn.LSGetDecision().getText().toLowerCase() : "";
			if (lsText.contains("action or pass") || dsText.contains("action or pass")) {
				break;
			}
			if (lsText.contains("verify") || dsText.contains("verify")) {
				if (scn.LSAnyDecisionsAvailable()) {
					scn.LSPass();
				}
				if (scn.DSAnyDecisionsAvailable()) {
					scn.DSPass();
				}
				continue;
			}
			if (lsText.contains("force - optional") || dsText.contains("force - optional")
					|| lsText.contains("optional response") || dsText.contains("optional response")) {
				scn.PassForceUseResponses();
				scn.PassAllResponses();
				continue;
			}
			if (scn.LSAnyDecisionsAvailable()) {
				if (scn.LSHasCardChoiceAvailable(luke) && luke.getZone() == Zone.HAND) {
					scn.LSChooseCard(luke);
					continue;
				}
				if (scn.LSHasCardChoiceAvailable(luke2) && luke2.getZone() == Zone.HAND) {
					scn.LSChooseCard(luke2);
					continue;
				}
				if (scn.LSHasCardChoiceAvailable(red5)) {
					scn.LSChooseCard(red5);
					continue;
				}
				if ((lsText.contains("where to deploy") || lsText.contains("choose location")
						|| lsText.contains("choose system") || lsText.contains("choose where"))
						&& scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation())) {
					scn.LSChooseCard(scn.GetLSStartingLocation());
					continue;
				}
				throw new RuntimeException("Unhandled Squadron Assignments decision: " + decisionSnapshot(scn));
			}
			scn.PassAllResponses();
		}

		var deployedLuke = luke.getZone() != Zone.HAND ? luke : luke2;
		var remainingLuke = deployedLuke == luke ? luke2 : luke;
		if (deployedLuke.getZone() == Zone.HAND) {
			throw new RuntimeException("Neither Luke left hand after Squadron Assignments; luke=" + luke.getZone()
					+ " luke2=" + luke2.getZone() + " red5=" + red5.getZone() + " decision=" + decisionSnapshot(scn));
		}
		assertEquals(Zone.HAND, remainingLuke.getZone());

		AdvanceThroughEndOfDSNextTurn(scn);
		assertEquals(Zone.HAND, remainingLuke.getZone());
		assertEquals(0, scn.GetLSLostPileCount());
	}

	@Test
	public void FrustrationShockingInformationCausesFourForceLossAndLosesFrustration() {
		// Shocking Information 5_68 (Cloud City Used Interrupt): if opponent is about to scan/look
		// through your hand (unless Monnok), opponent continues but loses 4 Force plus the card
		// allowing the scan. Play it in the BEFORE peek window, not after PassAllResponses.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var shocking = scn.GetLSCard("shocking");
		var trooper = scn.GetLSCard("trooper");
		var kfc = scn.GetLSCard("kfc");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(shocking, trooper, kfc);

		scn.StartGame();

		scn.SkipToPhase(Phase.CONTROL);
		int dsForceBefore = scn.GetDSLifeForceRemaining();
		scn.DSPlayCard(frustration);

		// Pass the "Playing Frustration" window; Shocking Information is the later BEFORE-peek window.
		if (!lsPlayAvailable(scn, shocking)) {
			scn.PassCardPlayResponses();
		}
		if (!lsPlayAvailable(scn, shocking)) {
			throw new RuntimeException("Shocking Information not offered on Frustration peek: " + decisionSnapshot(scn));
		}
		scn.LSPlayCard(shocking);
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}
		if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(trooper)) {
			scn.DSChooseCard(trooper);
		}

		for (int i = 0; i < 20; i++) {
			if (scn.GetDSLifeForceRemaining() <= dsForceBefore - 4) {
				break;
			}
			if (!scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
				break;
			}
			String text = scn.GetCurrentDecision().getText().toLowerCase();
			if (text.contains("action or pass")) {
				break;
			}
			if (scn.AwaitingDSForceLossPayment()) {
				scn.DSPayRemainingForceLossFromReserveDeck();
				continue;
			}
			if (text.contains("optional") || text.contains("about to lose") || text.contains("lose force")
					|| text.contains("about_to_lose") || text.contains("put_in_card_pile") || text.contains("forfeited")) {
				scn.PassAllResponses();
				scn.PassCardLeavingTable();
				continue;
			}
			if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
				scn.DSDismissRevealedCards();
				continue;
			}
			if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(trooper)) {
				scn.DSChooseCard(trooper);
				continue;
			}
			throw new RuntimeException("Unhandled after Shocking Information: " + decisionSnapshot(scn)
					+ " forceBefore=" + dsForceBefore + " forceNow=" + scn.GetDSLifeForceRemaining());
		}

		assertEquals("DS should lose 4 Force to Shocking Information", dsForceBefore - 4, scn.GetDSLifeForceRemaining());
		assertTrue("Frustration should be in Lost Pile after Shocking Information",
				frustration.getZone() == Zone.LOST_PILE || frustration.getZone() == Zone.TOP_OF_LOST_PILE);
	}

	@Test
	public void FrustrationBounceToHandWithIdJustAsSoonKissAWookieeStillSatisfies() {
		// I'd Just As Soon Kiss A Wookiee 3_127: use 3 Force to return opponent's just deployed
		// character to hand. LS deploys the targeted Rebel Trooper 1_28, DS bounces it, then at
		// end of DS next turn the trooper is in HAND and is not lost (the deploy already satisfied).
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var kissWookiee = scn.GetDSCard("kissWookiee");
		var trooper = scn.GetLSCard("trooper");
		var walkway = scn.GetLSCard("walkway");

		scn.MoveCardsToDSHand(frustration, kissWookiee);
		scn.MoveCardsToLSHand(trooper);

		scn.StartGame();
		scn.DSActivateForceCheat(3);

		PlayFrustrationTargetingTrooper(scn);
		scn.MoveLocationToTable(walkway);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(trooper));
		scn.LSDeployCard(trooper);
		if (!(scn.LSDecisionAvailable("Choose where to deploy") || scn.LSDecisionAvailable("Choose location where to deploy")
				|| scn.LSHasCardChoiceAvailable(walkway))) {
			throw new RuntimeException("Expected deploy-site choice for Trooper: " + decisionSnapshot(scn));
		}
		scn.LSChooseCard(walkway);

		// Pass Force-use optional responses; the just-deployed window is next.
		if (!dsPlayAvailable(scn, kissWookiee)) {
			scn.PassForceUseResponses();
		}
		if (!dsPlayAvailable(scn, kissWookiee)) {
			throw new RuntimeException("I'd Just As Soon Kiss A Wookiee not offered after Trooper deploy: "
					+ decisionSnapshot(scn));
		}
		scn.DSPlayCard(kissWookiee);
		scn.PassAllResponses();

		assertEquals(Zone.HAND, trooper.getZone());

		AdvanceThroughEndOfDSNextTurn(scn);
		assertEquals(Zone.HAND, trooper.getZone());
		assertEquals(0, scn.GetLSLostPileCount());
	}

	@Test
	public void FrustrationUndercoverTk422DeploySatisfies() {
		// TK-422 7_48 deploys only as an Undercover spy at same site as an Imperial.
		// Printed 3, so Mos Eisley 1_133 is on table (4 LS icons). Stormtrooper 1_194 is
		// cheated to Mos Eisley. A second TK-422 stays in hand; if justDeployed fired for
		// undercover, that copy is not lost.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var tk422 = scn.GetLSCard("tk422");
		var tk422b = scn.GetLSCard("tk422b");
		var stormtrooper = scn.GetDSCard("stormtrooper");
		var mosEisley = scn.GetLSCard("mosEisley");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(tk422, tk422b);

		scn.StartGame();
		scn.MoveLocationToTable(mosEisley);
		scn.MoveCardsToLocation(mosEisley, stormtrooper);
		scn.LSActivateForceCheat(5);

		PeekFrustrationAtHand(scn);
		if (!scn.DSHasCardChoiceAvailable(tk422) && !scn.DSHasCardChoiceAvailable(tk422b)) {
			throw new RuntimeException("Expected TK-422 as Frustration target: " + decisionSnapshot(scn));
		}
		scn.DSChooseCard(tk422);
		scn.PassAllResponses();

		scn.SkipToLSTurn(Phase.DEPLOY);
		if (!scn.LSDeployAvailable(tk422)) {
			throw new RuntimeException("TK-422 deploy not available: " + decisionSnapshot(scn));
		}
		scn.LSDeployCard(tk422);
		if (scn.LSHasCardChoiceAvailable(mosEisley)) {
			scn.LSChooseCard(mosEisley);
		}
		else if (scn.LSDecisionAvailable("Choose where to deploy") || scn.LSDecisionAvailable("Choose location where to deploy")) {
			scn.LSChooseCard(mosEisley);
		}
		else {
			throw new RuntimeException("Expected Mos Eisley as TK-422 deploy site: " + decisionSnapshot(scn));
		}
		scn.PassAllResponses();

		assertEquals(Zone.HAND, tk422b.getZone());
		AdvanceThroughEndOfDSNextTurn(scn);
		assertEquals(Zone.HAND, tk422b.getZone());
		assertEquals(0, scn.GetLSLostPileCount());
	}

	@Test
	public void FrustrationDsDeployingSameTitleDoesNotSatisfyLsObligation() {
		// Light Disruptor Pistol 7_157 in LS hand, Dark Disruptor Pistol 7_319 deployed by DS
		// onto Stormtrooper 1_194. Same title "Disruptor Pistol". DS deploying that title does
		// not satisfy LS's obligation. Mos Eisley so the pistol's cost is < LS icons.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var lsPistol = scn.GetLSCard("lsPistol");
		var dsPistol = scn.GetDSCard("dsPistol");
		var stormtrooper = scn.GetDSCard("stormtrooper");
		var mosEisley = scn.GetLSCard("mosEisley");

		scn.MoveCardsToDSHand(frustration, dsPistol);
		scn.MoveCardsToLSHand(lsPistol);

		scn.StartGame();
		scn.MoveLocationToTable(mosEisley);
		scn.MoveCardsToLocation(mosEisley, stormtrooper);
		scn.DSActivateForceCheat(3);

		PeekFrustrationAtHand(scn);
		if (!scn.DSHasCardChoiceAvailable(lsPistol)) {
			throw new RuntimeException("Expected LS Disruptor Pistol as Frustration target: " + decisionSnapshot(scn));
		}
		scn.DSChooseCard(lsPistol);
		scn.PassAllResponses();

		scn.SkipToPhase(Phase.DEPLOY);
		if (!scn.DSDeployAvailable(dsPistol)) {
			throw new RuntimeException("DS Disruptor Pistol deploy not available: " + decisionSnapshot(scn));
		}
		scn.DSDeployCard(dsPistol);
		if (scn.DSHasCardChoiceAvailable(stormtrooper)) {
			scn.DSChooseCard(stormtrooper);
		}
		else if (scn.DSDecisionAvailable("Choose where to deploy") || scn.DSDecisionAvailable("Choose target")) {
			scn.DSChooseCard(stormtrooper);
		}
		else {
			throw new RuntimeException("Expected Stormtrooper as DS pistol deploy target: " + decisionSnapshot(scn));
		}
		scn.PassAllResponses();

		AdvanceThroughEndOfDSNextTurn(scn);
		assertEquals(Zone.TOP_OF_LOST_PILE, lsPistol.getZone());
		assertEquals(1, scn.GetLSLostPileCount());
	}

	@Test
	public void FrustrationCannotTargetAdmiralsOrderOrDefensiveShieldThatDeployFree() {
		// I'll Take The Leader 9_4 is an AbstractAdmiralsOrder. Reflections III Defensive Shield
		// Your Insight Serves You Well 13_49 is an AbstractDefensiveShield. Both always play for free.
		// Free is not a deploy cost, same pattern as JP Leia. Rebel Trooper remains a legal target.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var admiralsOrder = scn.GetLSCard("admiralsOrder");
		var insight = scn.GetLSCard("insight");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(admiralsOrder, insight, trooper);

		scn.StartGame();
		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceNotAvailable(admiralsOrder));
		assertTrue(scn.DSHasCardChoiceNotAvailable(insight));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationDoesNotCountDagobahCaveLightIconsCanceledByPresenceAfterRevolution() {
		// Dagobah: Cave 4_158 is a Dark site with 2 printed Dark Force icons. Dark game text:
		// "If opponent has presence here, your Force Icons here are canceled."
		// Revolution 1_62 rotates the location so icons and game texts switch direction: those
		// 2 Dark icons become 2 Light icons, and the cancel-icons text sits on the Light side.
		// Default starting systems already provide 2 Light icons.
		// Without a Dark Stormtrooper at Cave, total is 4 Light icons, so Rebel Pilot deploy 2
		// is a legal Frustration target (2 < 4). With Stormtrooper 1_194 at the Revolution'd Cave,
		// Dark is opponent presence for Light, those 2 Light icons are canceled, total stays 2,
		// so Pilot is not a target (2 is not < 2) while Rebel Trooper deploy 1 still is.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var frustration2 = scn.GetDSCard("frustration2");
		var cave = scn.GetDSCard("cave");
		var revolution = scn.GetLSCard("revolution");
		var stormtrooper = scn.GetDSCard("stormtrooper");
		var pilot = scn.GetLSCard("pilot");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration, frustration2);
		scn.MoveCardsToLSHand(pilot, trooper);

		scn.StartGame();
		scn.MoveLocationToTable(cave);
		scn.AttachCardsTo(cave, revolution);
		scn.gameState().reapplyAffectingForCard(scn.game(), revolution);
		scn.gameState().reapplyAffectingForCard(scn.game(), cave);
		scn.PassAllResponses();

		PeekFrustrationAtHand(scn);
		if (!scn.DSHasCardChoiceAvailable(pilot)) {
			throw new RuntimeException("Expected Rebel Pilot as Frustration target after Revolution on Dagobah Cave (rotation may not have applied): " + decisionSnapshot(scn));
		}
		assertTrue(scn.DSHasCardChoiceAvailable(pilot));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
		scn.DSChooseCard(trooper);
		scn.PassAllResponses();

		scn.MoveCardsToLocation(cave, stormtrooper);

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(frustration2));
		scn.DSPlayCard(frustration2);
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}

		assertTrue(scn.DSHasCardChoiceNotAvailable(pilot));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}


}
