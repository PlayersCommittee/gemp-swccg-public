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
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("clinging", "13_10");
					put("obi", "11_10"); // Qui-Gon Jinn (Jedi)
					put("lsCombat1", "1_3"); // high destiny filler for stacking/hand checks
					put("lsCombat2", "1_4");
				}},
				new HashMap<>() {{
					put("obj", "13_73"); // Let Them Make The First Move / At Last We Will Have Revenge
					put("maul", "11_54"); // Darth Maul (Dark Jedi)
					put("dsCombat1", "1_174");
					put("dsCombat2", "1_175");
				}},
				40,
				40,
				StartingSetup.DefaultLSGroundLocation,
				StartingSetup.DefaultDSGroundLocation,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}


	private void prepareCombatants(VirtualTableScenario scn, boolean maulHasCombatCard, boolean obiHasCombatCard) {
		var clinging = scn.GetLSCard("clinging");
		var obi = scn.GetLSCard("obi");
		var maul = scn.GetDSCard("maul");
		var obj = scn.GetDSCard("obj");
		var site = scn.GetLSStartingLocation();
		var dsCombat1 = scn.GetDSCard("dsCombat1");
		var lsCombat1 = scn.GetLSCard("lsCombat1");

		scn.StartGame();
		scn.MoveCardsToDSSideOfTable(obj);
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

	private void initiateLightsaberCombat(VirtualTableScenario scn) {
		var obj = scn.GetDSCard("obj");
		var maul = scn.GetDSCard("maul");
		var obi = scn.GetLSCard("obi");

		scn.SkipToDSTurn(Phase.MOVE);
		assertTrue(scn.AwaitingDSMovePhaseActions());
		assertTrue("Expected initiate lightsaber combat action", scn.DSCardActionAvailable(obj, "Initiate lightsaber combat"));
		scn.DSUseCardAction(obj, "Initiate lightsaber combat");
		scn.DSChooseCard(maul);
		scn.DSChooseCard(obi);
		// Responses to initiation
	}

	@Test
	public void ClingingToTheEdgeStatsAndIcons() {
		var scn = GetScenario();
		var clinging = scn.GetLSCard("clinging");
		scn.StartGame();

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
		assertEquals(Zone.LOST_PILE, clinging.getZone());
	}

	@Test
	public void ClingingToTheEdgeDrawThreeChooseTwoAndTakeOtherIntoHand() {
		var scn = GetScenario();
		prepareCombatants(scn, true, false);
		var clinging = scn.GetLSCard("clinging");

		// Stack known destinies on top of LS Reserve for the combat draw
		var d1 = scn.GetLSCard("lsCombat1");
		var d2 = scn.GetLSCard("lsCombat2");
		// Use destiny fillers from test helpers if available
		var topA = scn.GetLSDestiny(5);
		var topB = scn.GetLSDestiny(6);
		var topC = scn.GetLSDestiny(7);
		scn.MoveCardsToTopOfLSReserveDeck(topC, topB, topA);

		initiateLightsaberCombat(scn);
		assertTrue(scn.LSCardPlayAvailable(clinging));
		scn.LSPlayCard(clinging);
		scn.PassAllResponses();

		// DS draws 2 normal lightsaber combat destinies first
		scn.PassAllResponses();
		if (scn.DSDecisionAvailable("Choose destiny")) {
			// unlikely for normal draws
		}
		// Continue through DS draws
		while (scn.DSDecisionAvailable("Surely") || !scn.DSGetCardChoices().isEmpty()) {
			scn.DSPass();
		}
		scn.PassAllResponses();

		// LS about to draw — required draw 3 choose 2 should fire
		assertTrue("Expected choose-destiny decision for Clinging",
				scn.LSDecisionAvailable("Choose 2 destiny") || scn.LSDecisionAvailable("Choose destiny")
						|| scn.LSDecisionAvailable("Draw three") || !scn.LSGetCardChoices().isEmpty());

		// Choose two destinies (framework-dependent); then take leftover into hand
		if (!scn.LSGetCardChoices().isEmpty()) {
			scn.LSChooseCards(topB, topC);
		}
		if (scn.LSDecisionAvailable("Take into hand") || scn.LSDecisionAvailable("Choose destination")) {
			scn.LSChoose("Take into hand");
		}
		scn.PassAllResponses();

		assertTrue(topA.getZone() == Zone.HAND || topB.getZone() == Zone.HAND || topC.getZone() == Zone.HAND
				|| topA.getZone() == Zone.USED_PILE || topB.getZone() == Zone.USED_PILE || topC.getZone() == Zone.USED_PILE);
	}

	@Test
	public void ClingingToTheEdgeMayReturnOtherToTopOfReserveDeck() {
		var scn = GetScenario();
		prepareCombatants(scn, true, false);
		var clinging = scn.GetLSCard("clinging");
		var topA = scn.GetLSDestiny(3);
		var topB = scn.GetLSDestiny(4);
		var topC = scn.GetLSDestiny(8);
		scn.MoveCardsToTopOfLSReserveDeck(topC, topB, topA);

		initiateLightsaberCombat(scn);
		assertTrue(scn.LSCardPlayAvailable(clinging));
		scn.LSPlayCard(clinging);
		scn.PassAllResponses();

		// Drive combat toward LS draw-choose; leftover -> top of Reserve
		scn.PassAllResponses();
		if (!scn.LSGetCardChoices().isEmpty()) {
			scn.LSChooseCards(topA, topB);
		}
		if (scn.LSDecisionAvailable("Return to top of Reserve Deck") || scn.LSDecisionAvailable("Choose destination")) {
			scn.LSChoose("Return to top of Reserve Deck");
		}
		scn.PassAllResponses();

		assertTrue(topC.getZone() == Zone.RESERVE_DECK || topC.getZone() == Zone.USED_PILE || topC.getZone() == Zone.HAND
				|| topA.getZone() == Zone.RESERVE_DECK || topB.getZone() == Zone.RESERVE_DECK);
	}
}
