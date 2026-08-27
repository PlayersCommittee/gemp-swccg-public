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
				}},
				new HashMap<>()
				{{
					put("frustration", "4_142");
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
				throw new RuntimeException("Unhandled decision while draining end of turn: " + scn.GetCurrentDecision().getText());
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
	public void FrustrationIsPlayableDuringDSControlPhase() {
		var scn = GetScenario();

		var frustration = scn.GetDSCard("frustration");
		var trooper = scn.GetLSCard("trooper");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(trooper);

		scn.StartGame();

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.DSCardPlayAvailable(frustration));
	}

	@Test
	public void FrustrationCanTargetNonInterruptWithDeployCostLessThanLightIcons() {
		// Default systems (Dantooine + Tibrin) have 2 Light Side Force icons on table.
		// Rebel Trooper (deploy 1) is a valid target.
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
	}

	@Test
	public void FrustrationCannotTargetInterruptsOrCardsWithDeployCostAtLeastLightIcons() {
		// Default systems have 2 Light Side Force icons on table.
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
}
