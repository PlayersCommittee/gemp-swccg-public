package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ResetAbilityModifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for 4_137 Apology Accepted.
 * Doc scenarios A–D + Mouse-style edges (ability gate, phase window, Used pile).
 */
public class Card_4_137_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("luke", "1_19");
					put("cantina", "1_128");
					put("obi", "1_11");
				}},
				new HashMap<>() {{
					put("apology", "4_137");
					put("trooper", "1_194");
					put("trooper2", "1_194");
					put("tatDb", "1_291");
				}},
				20,
				20,
				StartingSetup.DefaultLSSpaceSystem,
				StartingSetup.DefaultDSSpaceSystem,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void ApologyAcceptedStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetDSCard("apology").getBlueprint();
		assertEquals(Title.Apology_Accepted, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(CardSubtype.USED, card.getCardSubtype());
		assertEquals(6, card.getDestiny(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.DAGOBAH);
			add(Icon.INTERRUPT);
		}});
		assertEquals(ExpansionSet.DAGOBAH, card.getExpansionSet());
		assertEquals(Rarity.C, card.getRarity());
	}

	/**
	 * True if DS currently has a decision that exposes Apology Accepted as a playable action.
	 * Guards null decisions / missing action params (framework NPEs otherwise).
	 */
	private boolean ApologyPlayAvailable(VirtualTableScenario scn) {
		var apology = scn.GetDSCard("apology");
		if (scn.DSGetDecision() == null) {
			return false;
		}
		try {
			return scn.DSCardPlayAvailable(apology);
		} catch (NullPointerException | IndexOutOfBoundsException ex) {
			return false;
		}
	}

	private void SafePassOptionalResponses(VirtualTableScenario scn) {
		for (int i = 0; i < 20; i++) {
			var decision = scn.GetCurrentDecision();
			if (decision == null) {
				return;
			}
			String text = decision.getText();
			if (text == null || !text.toLowerCase().contains("optional response")) {
				return;
			}
			scn.PassResponses("optional");
		}
	}

	/**
	 * DS loses a site battle with Stormtrooper surviving; pay damage from Reserve.
	 * Leaves game in DS battle-phase action window when possible.
	 */
	private void SetupLostBattleWithSurvivingTrooper(VirtualTableScenario scn) {
		var luke = scn.GetLSCard("luke");
		var cantina = scn.GetLSCard("cantina");
		var apology = scn.GetDSCard("apology");
		var trooper = scn.GetDSCard("trooper");

		scn.StartGame();
		scn.MoveCardsToDSHand(apology);
		scn.MoveLocationToTable(cantina);
		scn.MoveCardsToLocation(cantina, luke, trooper);

		scn.SkipToDSTurn(Phase.BATTLE);
		assertTrue("DS should be able to initiate battle at Cantina", scn.DSCanInitiateBattle(cantina));
		scn.DSInitiateBattle(cantina);
		scn.PassBattleStartResponses();
		scn.PassWeaponsSegmentActions();
		scn.SkipToDamageSegment(false);

		assertTrue("DS should be awaiting battle damage", scn.AwaitingDSBattleDamagePayment());
		scn.DSPayRemainingBattleDamageFromReserveDeck();
		scn.PassDamageSegmentActions();
		SafePassOptionalResponses(scn);
	}

	/**
	 * Advance through battle-phase actions until Apology is offered at end-of-battle-phase
	 * optional responses, or MOVE begins without it.
	 */
	private boolean AdvanceToApologyWindow(VirtualTableScenario scn) {
		for (int i = 0; i < 40; i++) {
			if (ApologyPlayAvailable(scn)) {
				return true;
			}
			Phase phase = scn.gameState().getCurrentPhase();
			if (phase == Phase.MOVE || phase == Phase.DRAW || phase == Phase.CONTROL) {
				return ApologyPlayAvailable(scn);
			}
			var decision = scn.GetCurrentDecision();
			if (decision == null) {
				return false;
			}
			try {
				String text = decision.getText() != null ? decision.getText().toLowerCase() : "";
				if (text.contains("optional")) {
					// Do not auto-pass if Apology is somehow only listed under a broader optional window
					if (ApologyPlayAvailable(scn)) {
						return true;
					}
					scn.PassResponses("optional");
				} else if (text.contains("required")) {
					scn.PassResponses("required");
				} else if (text.contains("action")) {
					scn.PassResponses("action");
				} else {
					scn.PassResponses();
				}
			} catch (RuntimeException ex) {
				return ApologyPlayAvailable(scn);
			}
		}
		return ApologyPlayAvailable(scn);
	}

	@Test
	public void ApologyAcceptedPlayableAfterLostBattleAtEndOfBattlePhase() {
		var scn = GetScenario();
		var apology = scn.GetDSCard("apology");
		var trooper = scn.GetDSCard("trooper");

		SetupLostBattleWithSurvivingTrooper(scn);
		assertTrue("Trooper should still be on table (survived)", trooper.getZone().isInPlay());
		assertTrue("Apology Accepted should be playable at end of battle phase", AdvanceToApologyWindow(scn));

		scn.DSPlayCard(apology);
		assertTrue(scn.DSDecisionAvailable("Choose Imperial") || scn.DSHasCardChoiceAvailable(trooper));
		scn.DSChooseCard(trooper);
		SafePassOptionalResponses(scn);
		if (scn.DSGetDecision() != null && scn.DSDecisionAvailable("Choose amount of Force to activate")) {
			int amount = Math.min(2, Math.max(1, scn.GetDSReserveDeckCount()));
			scn.DSDecided(String.valueOf(amount));
		}
		SafePassOptionalResponses(scn);

		assertTrue("Trooper should be lost", trooper.getZone() == Zone.TOP_OF_LOST_PILE || trooper.getZone() == Zone.LOST_PILE);
		assertEquals(Zone.TOP_OF_USED_PILE, apology.getZone());
	}

	@Test
	public void ApologyAcceptedAbility6ImperialNotEligible() {
		var scn = GetScenario();
		var apology = scn.GetDSCard("apology");
		var trooper = scn.GetDSCard("trooper");

		SetupLostBattleWithSurvivingTrooper(scn);
		scn.ApplyAdHocModifier(new ResetAbilityModifier(apology, Filters.sameCardId(trooper), 6));
		SafePassOptionalResponses(scn);
		assertFalse("Ability 6 Imperial is not eligible", AdvanceToApologyWindow(scn));
	}

	@Test
	public void ApologyAcceptedAbility5ImperialEligible() {
		var scn = GetScenario();
		var apology = scn.GetDSCard("apology");
		var trooper = scn.GetDSCard("trooper");

		SetupLostBattleWithSurvivingTrooper(scn);
		scn.ApplyAdHocModifier(new ResetAbilityModifier(apology, Filters.sameCardId(trooper), 5));
		SafePassOptionalResponses(scn);
		assertTrue("Ability 5 Imperial is eligible", AdvanceToApologyWindow(scn));
	}

	@Test
	public void ApologyAcceptedNotPlayableWithoutLostBattle() {
		var scn = GetScenario();
		var apology = scn.GetDSCard("apology");
		var trooper = scn.GetDSCard("trooper");
		var cantina = scn.GetLSCard("cantina");

		scn.StartGame();
		scn.MoveCardsToDSHand(apology);
		scn.MoveLocationToTable(cantina);
		scn.MoveCardsToLocation(cantina, trooper);

		scn.SkipToPhase(Phase.BATTLE);
		assertFalse(AdvanceToApologyWindow(scn));
	}

	@Test
	public void ApologyAcceptedForfeitZeroAllowsPlayWithNoReserve() {
		var scn = GetScenario();
		var apology = scn.GetDSCard("apology");
		var trooper = scn.GetDSCard("trooper");

		SetupLostBattleWithSurvivingTrooper(scn);
		scn.ApplyAdHocModifier(new ResetForfeitModifier(apology, Filters.sameCardId(trooper), 0));
		while (scn.GetDSReserveDeckCount() > 0) {
			scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSReserveDeck());
		}
		SafePassOptionalResponses(scn);
		assertTrue("Forfeit 0 allows play with empty Reserve", AdvanceToApologyWindow(scn));
		scn.DSPlayCard(apology);
		scn.DSChooseCard(trooper);
		SafePassOptionalResponses(scn);
		assertEquals(Zone.TOP_OF_USED_PILE, apology.getZone());
	}

	@Test
	public void ApologyAcceptedCannotPlayWithNoReserveWhenForfeitPositive() {
		var scn = GetScenario();
		var apology = scn.GetDSCard("apology");
		var trooper = scn.GetDSCard("trooper");

		SetupLostBattleWithSurvivingTrooper(scn);
		while (scn.GetDSReserveDeckCount() > 0) {
			scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSReserveDeck());
		}
		SafePassOptionalResponses(scn);
		assertFalse("Cannot play when no Reserve and forfeit > 0 with battle damage requiring activation",
				AdvanceToApologyWindow(scn));
	}
}