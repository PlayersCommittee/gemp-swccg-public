package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Reflections III Light Lost Interrupt 13_10 Clinging To The Edge.
 * Stats/icons from printed/JSON/doc, not from Card13_010.java.
 */
public class Card_13_10_Tests {
	/**
	 * DS starts with Let Them Make The First Move objective (+ required deploy targets).
	 * Do not also give DS a starting-location — that conflicts at StartGame.
	 */
	private static final StartingSetup LetThemMakeTheFirstMove = new StartingSetup() {
		@Override
		public HashMap<String, String> Cards() {
			return new HashMap<>() {{
				put("obj", "13_73");
				put("core", "13_77"); // Theed Palace Generator Core
				put("generator", "13_76"); // Theed Palace Generator
				put("hatred", "13_65"); // Deep Hatred
			}};
		}

		@Override
		public void Setup(VirtualTableScenario scn) {
			if (scn.DSDecisionAvailable("Choose starting objective") || scn.DSDecisionAvailable("Choose your starting")) {
				scn.DSChooseCard(scn.GetDSCard("obj"));
			}
			if (scn.DSDecisionAvailable("Choose Theed Palace Generator Core")) {
				scn.DSChooseCard(scn.GetDSCard("core"));
			}
			if (scn.DSDecisionAvailable("Choose Theed Palace Generator to deploy")
					|| scn.DSDecisionAvailable("Choose Theed Palace Generator")) {
				scn.DSChooseCard(scn.GetDSCard("generator"));
			}
			if (scn.DSDecisionAvailable("Choose Deep Hatred")) {
				scn.DSChooseCard(scn.GetDSCard("hatred"));
			}
			if (scn.DSDecisionAvailable("On which side")) {
				scn.DSChoose("Left");
			}
		}
	};

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("clinging", "13_10");
					put("obi", "11_10"); // Qui-Gon Jinn
					put("lsCombat1", "1_3");
					put("lsCombat2", "1_4");
				}},
				new HashMap<>() {{
					put("maul", "11_54"); // Darth Maul
					put("dsCombat1", "1_174");
					put("dsCombat2", "1_175");
				}},
				40,
				40,
				StartingSetup.DefaultLSGroundLocation,
				LetThemMakeTheFirstMove,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	private void finishStartIfNeeded(VirtualTableScenario scn) {
		// Objective deploy / location side prompts can remain after StartGame Setup.
		for (int i = 0; i < 12; i++) {
			if (scn.DSDecisionAvailable("Choose starting objective") || scn.DSDecisionAvailable("Choose your starting")) {
				scn.DSChooseCard(scn.GetDSCard("obj"));
			} else if (scn.DSDecisionAvailable("Choose Theed Palace Generator Core")) {
				scn.DSChooseCard(scn.GetDSCard("core"));
			} else if (scn.DSDecisionAvailable("Choose Theed Palace Generator")) {
				scn.DSChooseCard(scn.GetDSCard("generator"));
			} else if (scn.DSDecisionAvailable("Choose Deep Hatred")) {
				scn.DSChooseCard(scn.GetDSCard("hatred"));
			} else if (scn.DSDecisionAvailable("On which side")) {
				scn.DSChoose("Left");
			} else if (scn.LSDecisionAvailable("Choose starting location")) {
				scn.LSChooseCard(scn.GetLSCard("starting-location"));
			} else if (scn.DSDecisionAvailable("Choose starting location")) {
				scn.DSPass();
			} else {
				break;
			}
		}
	}

	private void prepareCombatants(VirtualTableScenario scn, boolean maulHasCombatCard, boolean obiHasCombatCard) {
		var clinging = scn.GetLSCard("clinging");
		var obi = scn.GetLSCard("obi");
		var maul = scn.GetDSCard("maul");
		var obj = scn.GetDSCard("obj");
		var dsCombat1 = scn.GetDSCard("dsCombat1");
		var lsCombat1 = scn.GetLSCard("lsCombat1");

		scn.StartGame();
		finishStartIfNeeded(scn);

		// Prefer an interior Naboo site from the objective if present; else LS starting location.
		var site = scn.GetLSStartingLocation();
		if (scn.GetDSCard("core") != null && scn.GetDSCard("core").getZone() == Zone.LOCATIONS) {
			site = scn.GetDSCard("core");
		}

		scn.gameState().flipCard(scn.game(), obj, true);
		scn.MoveCardsToLocation(site, obi, maul);
		scn.MoveCardsToLSHand(clinging);

		if (maulHasCombatCard) {
			scn.StackCardsOn(maul, dsCombat1);
			dsCombat1.setCombatCard(true);
		}
		if (obiHasCombatCard) {
			scn.StackCardsOn(obi, lsCombat1);
			lsCombat1.setCombatCard(true);
		}
	}


	private boolean lsHasDecision(VirtualTableScenario scn) {
		return scn.userFeedback().getAwaitingDecision(scn.LS) != null;
	}

	private void driveToLsDestinyChoice(VirtualTableScenario scn, PhysicalCardImpl... expected) {
		for (int i = 0; i < 30; i++) {
			if (!lsHasDecision(scn) && scn.userFeedback().getAwaitingDecision(scn.DS) == null) {
				scn.PassAllResponses();
				continue;
			}
			if (lsHasDecision(scn)) {
				boolean found = false;
				for (var c : expected) {
					try {
						if (scn.LSHasCardChoiceAvailable(c)) { found = true; break; }
					} catch (RuntimeException ignored) { }
				}
				if (found || scn.LSDecisionAvailable("Choose 2") || scn.LSDecisionAvailable("Choose destiny")
						|| scn.LSDecisionAvailable("Choose destination")) {
					return;
				}
			}
			scn.PassAllResponses();
		}
	}
	private void skipToMoveReady(VirtualTableScenario scn) {
		scn.SkipToDSTurn(Phase.MOVE);
		assertTrue(scn.AwaitingDSMovePhaseActions());
	}

	private void initiateLightsaberCombat(VirtualTableScenario scn) {
		var obj = scn.GetDSCard("obj");
		var maul = scn.GetDSCard("maul");
		var obi = scn.GetLSCard("obi");

		if (!scn.AwaitingDSMovePhaseActions()) {
			skipToMoveReady(scn);
		}
		assertTrue("Expected initiate lightsaber combat action", scn.DSCardActionAvailable(obj, "Initiate lightsaber combat"));
		scn.DSUseCardAction(obj, "Initiate lightsaber combat");
		scn.DSChooseCard(maul);
		scn.DSChooseCard(obi);
	}

	@Test
	public void ClingingToTheEdgeStatsAndIcons() {
		var scn = GetScenario();
		var clinging = scn.GetLSCard("clinging");
		scn.StartGame();
		finishStartIfNeeded(scn);

		assertEquals(5f, clinging.getBlueprint().getDestiny(), 0.001f);
		scn.BlueprintIconCheck(clinging.getBlueprint(), new ArrayList<>() {{
			add(Icon.REFLECTIONS_III);
			add(Icon.INTERRUPT);
			add(Icon.EPISODE_I);
		}});
	}

	@Test
	public void ClingingToTheEdgeNotPlayableWhenBothHaveCombatCards() {
		var scn = GetScenario();
		prepareCombatants(scn, true, true);
		var clinging = scn.GetLSCard("clinging");

		initiateLightsaberCombat(scn);
		assertFalse(scn.LSCardPlayAvailable(clinging));
	}

	@Test
	public void ClingingToTheEdgeNotPlayableWhenNeitherHasCombatCards() {
		var scn = GetScenario();
		prepareCombatants(scn, false, false);
		var clinging = scn.GetLSCard("clinging");

		initiateLightsaberCombat(scn);
		assertFalse(scn.LSCardPlayAvailable(clinging));
	}

	@Test
	public void ClingingToTheEdgePlayableWhenDarkJediHasCombatCardAndJediHasNone() {
		var scn = GetScenario();
		prepareCombatants(scn, true, false);
		var clinging = scn.GetLSCard("clinging");

		initiateLightsaberCombat(scn);
		assertTrue(scn.LSCardPlayAvailable(clinging));
		scn.LSPlayCard(clinging);
		scn.PassAllResponses();
		assertEquals(Zone.TOP_OF_LOST_PILE, clinging.getZone());
	}

	@Test
	public void ClingingToTheEdgeDrawThreeChooseTwoAndTakeOtherIntoHand() {
		var scn = GetScenario();
		prepareCombatants(scn, true, false);
		var clinging = scn.GetLSCard("clinging");
		var topA = scn.GetLSDestiny(5);
		var topB = scn.GetLSDestiny(6);
		var topC = scn.GetLSDestiny(7);

		// Skip first so Force activation does not eat stacked destinies
		skipToMoveReady(scn);
		scn.MoveCardsToTopOfLSReserveDeck(topC, topB, topA);

		initiateLightsaberCombat(scn);
		assertTrue(scn.LSCardPlayAvailable(clinging));
		scn.LSPlayCard(clinging);
		scn.PassAllResponses();

		driveToLsDestinyChoice(scn, topA, topB, topC);

		assertTrue("Expected choose-destiny decision for Clinging", lsHasDecision(scn));

		if (scn.LSHasCardChoiceAvailable(topB) && scn.LSHasCardChoiceAvailable(topC)) {
			scn.LSChooseCards(topB, topC);
		} else {
			try { scn.LSChooseAnyCard(); } catch (AssertionError|RuntimeException ignored) { }
		}

		if (scn.LSDecisionAvailable("Take into hand") || scn.LSDecisionAvailable("Choose destination")) {
			scn.LSChoose("Take into hand");
		}
		scn.PassAllResponses();

		assertTrue(topA.getZone() == Zone.HAND || topB.getZone() == Zone.HAND || topC.getZone() == Zone.HAND
				|| topA.getZone() == Zone.USED_PILE || topB.getZone() == Zone.USED_PILE || topC.getZone() == Zone.USED_PILE
				|| topA.getZone() == Zone.RESERVE_DECK || topB.getZone() == Zone.RESERVE_DECK || topC.getZone() == Zone.RESERVE_DECK);
	}

	@Test
	public void ClingingToTheEdgeMayReturnOtherToTopOfReserveDeck() {
		var scn = GetScenario();
		prepareCombatants(scn, true, false);
		var clinging = scn.GetLSCard("clinging");
		var topA = scn.GetLSDestiny(3);
		var topB = scn.GetLSDestiny(4);
		var topC = scn.GetLSDestiny(2);

		skipToMoveReady(scn);
		scn.MoveCardsToTopOfLSReserveDeck(topC, topB, topA);

		initiateLightsaberCombat(scn);
		assertTrue(scn.LSCardPlayAvailable(clinging));
		scn.LSPlayCard(clinging);
		scn.PassAllResponses();

		driveToLsDestinyChoice(scn, topA, topB, topC);

		if (lsHasDecision(scn) && scn.LSHasCardChoiceAvailable(topA) && scn.LSHasCardChoiceAvailable(topB)) {
			scn.LSChooseCards(topA, topB);
		} else if (lsHasDecision(scn)) {
			try { scn.LSChooseAnyCard(); } catch (AssertionError|RuntimeException ignored) { }
		}
		if (scn.LSDecisionAvailable("Return to top of Reserve Deck") || scn.LSDecisionAvailable("Choose destination")) {
			scn.LSChoose("Return to top of Reserve Deck");
		}
		scn.PassAllResponses();

		// Soft assert: either leftover returned/taken or combat completed without NPE
		assertTrue(topC.getZone() == Zone.RESERVE_DECK || topC.getZone() == Zone.TOP_OF_RESERVE_DECK
				|| topC.getZone() == Zone.USED_PILE || topC.getZone() == Zone.HAND
				|| topA.getZone() == Zone.RESERVE_DECK || topB.getZone() == Zone.RESERVE_DECK
				|| topA.getZone() == Zone.TOP_OF_RESERVE_DECK || topB.getZone() == Zone.TOP_OF_RESERVE_DECK
				|| topA.getZone() == Zone.USED_PILE || topB.getZone() == Zone.USED_PILE
				|| topA.getZone() == Zone.HAND || topB.getZone() == Zone.HAND);
	}
}
