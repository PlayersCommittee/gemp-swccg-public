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
		return new VirtualTableScenario(
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
				}},
				new HashMap<>()
				{{
					put("frustration", "4_142");
					put("frustration2", "4_142");
					put("badFeeling", "4_116");
					put("pondaBlaster", "7_323");
					put("bluffs", "2_150");
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
	}

	private void PeekFrustrationAtHand(VirtualTableScenario scn) {
		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(scn.GetDSCard("frustration"));
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}
	}

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

}
