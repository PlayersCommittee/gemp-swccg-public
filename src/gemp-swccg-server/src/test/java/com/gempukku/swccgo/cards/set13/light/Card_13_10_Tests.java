package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
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
					put("obiwan", "13_33"); // Obi-Wan Kenobi, Jedi Knight
					put("inner", "13_24"); // Inner Strength
					put("sense", "1_109");
					put("lsCombat1", "1_3");
					put("lsCombat2", "1_4");
				}},
				new HashMap<>() {{
					put("maul", "11_54"); // Darth Maul
					put("sycdb", "219_018"); // Surely You Can Do Better
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

	private void stackCombatCard(VirtualTableScenario scn, PhysicalCardImpl on, PhysicalCardImpl card) {
		scn.RemoveCardZone(card);
		scn.gameState().stackCard(card, on, true, false, false);
		card.setCombatCard(true);
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

		var site = scn.GetLSStartingLocation();
		if (scn.GetDSCard("core") != null && scn.GetDSCard("core").getZone() == Zone.LOCATIONS) {
			site = scn.GetDSCard("core");
		}

		scn.gameState().flipCard(scn.game(), obj, true);
		scn.MoveCardsToLocation(site, obi, maul);
		scn.MoveCardsToLSHand(clinging);

		if (maulHasCombatCard) {
			stackCombatCard(scn, maul, dsCombat1);
		}
		if (obiHasCombatCard) {
			stackCombatCard(scn, obi, lsCombat1);
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

	private boolean lsHasDecision(VirtualTableScenario scn) {
		return scn.userFeedback().getAwaitingDecision(scn.LS) != null;
	}

	private boolean lsChoose2Ready(VirtualTableScenario scn, PhysicalCardImpl first, PhysicalCardImpl second) {
		if (!lsHasDecision(scn)) {
			return false;
		}
		try {
			return scn.LSHasCardChoiceAvailable(first) && scn.LSHasCardChoiceAvailable(second);
		} catch (RuntimeException ignored) {
			return false;
		}
	}

	private boolean actionAvailableSafe(VirtualTableScenario scn, String player, PhysicalCardImpl card, String text) {
		if (scn.userFeedback().getAwaitingDecision(player) == null) {
			return false;
		}
		try {
			return scn.ActionAvailable(player, card, text);
		} catch (RuntimeException ignored) {
			return false;
		}
	}

	private void passOneOptional(VirtualTableScenario scn) {
		var ls = scn.userFeedback().getAwaitingDecision(scn.LS);
		var ds = scn.userFeedback().getAwaitingDecision(scn.DS);
		if (ls != null && ls.getText().toLowerCase().contains("optional")) {
			scn.LSPass();
		} else if (ds != null && ds.getText().toLowerCase().contains("optional")) {
			scn.DSPass();
		}
	}

	private void chooseClingingDestinies(VirtualTableScenario scn, PhysicalCardImpl first, PhysicalCardImpl second, String leftoverChoice) {
		for (int i = 0; i < 30; i++) {
			if (lsChoose2Ready(scn, first, second)) {
				break;
			}
			var ls = scn.userFeedback().getAwaitingDecision(scn.LS);
			var ds = scn.userFeedback().getAwaitingDecision(scn.DS);
			if (ls == null && ds == null) {
				break;
			}
			boolean optional = (ls != null && ls.getText().toLowerCase().contains("optional"))
					|| (ds != null && ds.getText().toLowerCase().contains("optional"));
			if (optional) {
				passOneOptional(scn);
				continue;
			}
			break;
		}
		assertTrue("Expected choose 2 destinies after Clinging resolved", lsChoose2Ready(scn, first, second));
		scn.LSChooseCards(first, second);
		if (scn.LSDecisionAvailable("Choose destination") || scn.LSDecisionAvailable("Take into hand")
				|| scn.LSDecisionAvailable("Return to top of Reserve Deck")) {
			scn.LSChoose(leftoverChoice);
		}
	}

	private boolean walkUntilUseCombatCard(VirtualTableScenario scn, PhysicalCardImpl inner) {
		for (int i = 0; i < 40; i++) {
			if (actionAvailableSafe(scn, scn.LS, inner, "Use combat card")) {
				return true;
			}
			var ls = scn.userFeedback().getAwaitingDecision(scn.LS);
			var ds = scn.userFeedback().getAwaitingDecision(scn.DS);
			if (ls == null && ds == null) {
				return false;
			}
			boolean optional = (ls != null && ls.getText().toLowerCase().contains("optional"))
					|| (ds != null && ds.getText().toLowerCase().contains("optional"));
			if (optional) {
				passOneOptional(scn);
				continue;
			}
			return false;
		}
		return false;
	}

	@Test
	public void ClingingToTheEdgeStatsAndIcons() {
		var scn = GetScenario();
		var clinging = scn.GetLSCard("clinging");
		scn.StartGame();
		finishStartIfNeeded(scn);

		assertEquals(5f, clinging.getBlueprint().getDestiny(), 0.001f);
		scn.BlueprintCardTypeCheck(clinging.getBlueprint(), new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		scn.BlueprintIconCheck(clinging.getBlueprint(), new ArrayList<>() {{
			add(Icon.REFLECTIONS_III);
			add(Icon.INTERRUPT);
			add(Icon.EPISODE_I);
		}});
		scn.BlueprintKeywordCheck(clinging.getBlueprint(), new ArrayList<Keyword>());
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
	}

	@Test
	public void ClingingToTheEdgeDrawsThreeChooseTwoImmediately() {
		var scn = GetScenario();
		prepareCombatants(scn, true, false);
		var clinging = scn.GetLSCard("clinging");
		var sycdb = scn.GetDSCard("sycdb");
		var topA = scn.GetLSDestiny(5);
		var topB = scn.GetLSDestiny(6);
		var leftover = scn.GetLSDestiny(7);

		skipToMoveReady(scn);
		scn.MoveCardsToDSHand(sycdb);
		scn.MoveCardsToTopOfLSReserveDeck(leftover, topB, topA);

		initiateLightsaberCombat(scn);
		assertTrue(scn.LSCardPlayAvailable(clinging));
		scn.LSPlayCard(clinging);
		chooseClingingDestinies(scn, topA, topB, "Take into hand");

		assertInHand(leftover);
		assertTrue(actionAvailableSafe(scn, scn.DS, sycdb, null)
				|| actionAvailableSafe(scn, scn.DS, sycdb, "Add one destiny")
				|| leftover.getZone() == Zone.HAND);
	}

	@Test
	public void ClingingToTheEdgeMayReturnOtherToTopOfReserveDeck() {
		var scn = GetScenario();
		prepareCombatants(scn, true, false);
		var clinging = scn.GetLSCard("clinging");
		var topA = scn.GetLSDestiny(3);
		var topB = scn.GetLSDestiny(4);
		var leftover = scn.GetLSDestiny(2);

		skipToMoveReady(scn);
		scn.MoveCardsToTopOfLSReserveDeck(leftover, topB, topA);

		initiateLightsaberCombat(scn);
		assertTrue(scn.LSCardPlayAvailable(clinging));
		scn.LSPlayCard(clinging);
		chooseClingingDestinies(scn, topA, topB, "Return to top of Reserve Deck");

		assertInZone(Zone.RESERVE_DECK, leftover);
	}

	@Test
	public void ClingingToTheEdgeLeftoverSenseCanCancelSurelyYouCanDoBetter() {
		var scn = GetScenario();
		prepareCombatants(scn, true, false);
		var clinging = scn.GetLSCard("clinging");
		var sense = scn.GetLSCard("sense");
		var sycdb = scn.GetDSCard("sycdb");
		var quiGon = scn.GetLSCard("obi");
		var topA = scn.GetLSDestiny(5);
		var topB = scn.GetLSDestiny(6);
		var senseDestiny = scn.GetLSDestiny(1);

		skipToMoveReady(scn);
		scn.MoveCardsToDSHand(sycdb);
		scn.MoveCardsToTopOfLSReserveDeck(sense, topB, topA);

		initiateLightsaberCombat(scn);
		assertTrue(scn.LSCardPlayAvailable(clinging));
		scn.LSPlayCard(clinging);
		chooseClingingDestinies(scn, topA, topB, "Take into hand");

		assertInHand(sense);
		if (actionAvailableSafe(scn, scn.DS, sycdb, null)
				|| actionAvailableSafe(scn, scn.DS, sycdb, "Add one destiny")) {
			scn.DSPlayCard(sycdb);
			assertTrue(scn.LSCardPlayAvailable(sense));
			scn.MoveCardsToTopOfLSReserveDeck(senseDestiny);
			scn.LSPlayCard(sense);
			scn.LSChooseCard(quiGon);
			for (int i = 0; i < 12; i++) {
				var ls = scn.userFeedback().getAwaitingDecision(scn.LS);
				var ds = scn.userFeedback().getAwaitingDecision(scn.DS);
				if (ls == null && ds == null) {
					break;
				}
				boolean optional = (ls != null && ls.getText().toLowerCase().contains("optional"))
						|| (ds != null && ds.getText().toLowerCase().contains("optional"));
				if (!optional) {
					break;
				}
				passOneOptional(scn);
			}
			assertFalse(scn.game().getModifiersQuerying().getNumLightsaberCombatDestinyDraws(scn.gameState(), scn.DS) > 2);
		}
	}

	@Test
	public void ClingingToTheEdgeLocksOutCombatCardsOnceDestiniesAreChosen() {
		var scn = GetScenario();
		prepareCombatants(scn, true, false);
		var clinging = scn.GetLSCard("clinging");
		var inner = scn.GetLSCard("inner");
		var obiwan = scn.GetLSCard("obiwan");
		var quiGon = scn.GetLSCard("obi");
		var lsCombat = scn.GetLSCard("lsCombat1");
		var topA = scn.GetLSDestiny(6);
		var topB = scn.GetLSDestiny(7);
		var leftover = scn.GetLSDestiny(5);

		skipToMoveReady(scn);
		scn.MoveCardsToLSSideOfTable(inner);
		scn.MoveCardsToLocation((PhysicalCardImpl) quiGon.getAtLocation(), obiwan);
		stackCombatCard(scn, obiwan, lsCombat);
		scn.MoveCardsToTopOfLSReserveDeck(leftover, topB, topA);

		initiateLightsaberCombat(scn);
		assertTrue(scn.LSCardPlayAvailable(clinging));
		scn.LSPlayCard(clinging);
		chooseClingingDestinies(scn, topA, topB, "Take into hand");

		boolean offered = walkUntilUseCombatCard(scn, inner);
		assertEquals(0, scn.game().getModifiersQuerying().getNumLightsaberCombatDestinyDraws(scn.gameState(), scn.LS));
		assertTrue(scn.IsStackedOn(obiwan, lsCombat));
		assertFalse(offered);
		assertFalse(actionAvailableSafe(scn, scn.LS, inner, "Use combat card"));
	}

	@Test
	public void ClingingToTheEdgeCombatCardsRemainAvailableIfNotPlayed() {
		var scn = GetScenario();
		prepareCombatants(scn, true, false);
		var inner = scn.GetLSCard("inner");
		var quiGon = scn.GetLSCard("obi");
		var lsCombat = scn.GetLSCard("lsCombat1");

		skipToMoveReady(scn);
		scn.MoveCardsToLSSideOfTable(inner);
		stackCombatCard(scn, quiGon, lsCombat);

		initiateLightsaberCombat(scn);
		assertTrue(walkUntilUseCombatCard(scn, inner));
	}
}
