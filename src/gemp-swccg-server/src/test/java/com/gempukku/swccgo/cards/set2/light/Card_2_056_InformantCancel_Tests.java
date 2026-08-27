package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Mutual-cancel coverage for Sabotage vs Informant. Lives next to Card_2_056_Tests so PR 1002
 * can un-Ignore the Informant interaction without rewriting the rest of that suite.
 */
public class Card_2_056_InformantCancel_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
			new HashMap<>() {{
				put("sabotage", "2_56");
				put("spy", "7_5");
				put("lsTrooper", "1_28");
			}},
			new HashMap<>() {{
				put("informant", "2_134");
				put("dsSpy", "1_177");
				put("trooper", "1_194");
				put("mover", "1_194");
				put("cantina", "1_290");
				put("blaster", "1_317");
			}},
			10,
			10,
			StartingSetup.DefaultLSGroundLocation,
			StartingSetup.DefaultDSGroundLocation,
			StartingSetup.NoLSStartingInterrupts,
			StartingSetup.NoDSStartingInterrupts,
			StartingSetup.NoLSShields,
			StartingSetup.NoDSShields,
			VirtualTableScenario.Open
		);
	}

	@Test
	public void SabotageCancelsInformantBeingPlayedAsReact() {
		var scn = GetScenario();
		var sabotage = scn.GetLSCard("sabotage");
		var informant = scn.GetDSCard("informant");
		var marketplace = scn.GetDSStartingLocation();
		var cantina = scn.GetDSCard("cantina");
		var dsSpy = scn.GetDSCard("dsSpy");
		var trooper = scn.GetDSCard("trooper");
		var mover = scn.GetDSCard("mover");
		var lsTrooper = scn.GetLSCard("lsTrooper");

		scn.MoveCardsToLSHand(sabotage);
		scn.MoveCardsToDSHand(informant);
		scn.StartGame();
		scn.MoveLocationToTable(cantina);
		assertTrue(scn.IsAdjacentTo(cantina, marketplace));
		scn.MoveCardsToLocation(marketplace, dsSpy, trooper, lsTrooper);
		scn.MoveCardsToLocation(cantina, mover);
		scn.MakeCardGoUndercover(dsSpy);

		scn.SkipToLSTurn(Phase.BATTLE);
		assertTrue(scn.LSCanInitiateBattle(marketplace));
		scn.LSUseCardAction(marketplace, "Initiate battle");
		scn.PassForceUseResponses();
		if (scn.LSAnyDecisionsAvailable() && !scn.DSAnyDecisionsAvailable()
				&& scn.LSDecisionAvailable("Battle just initiated")) {
			scn.LSPass();
		}
		assertTrue(scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(informant));
		scn.DSPlayCard(informant);
		scn.DSChooseCard(mover);

		assertTrue(scn.LSCardPlayAvailable(sabotage));
		scn.LSPlayCard(sabotage);
		scn.PassAllResponses();

		assertEquals(cantina, mover.getAtLocation());
		assertFalse(scn.IsParticipatingInBattle(mover));
	}

	@Test
	public void InformantCancelsSabotageBeingPlayed() {
		var scn = GetScenario();
		var informant = scn.GetDSCard("informant");
		var sabotage = scn.GetLSCard("sabotage");
		var spy = scn.GetLSCard("spy");
		var blaster = scn.GetDSCard("blaster");
		var trooper = scn.GetDSCard("trooper");
		var site = scn.GetLSStartingLocation();

		scn.MoveCardsToLSHand(sabotage);
		scn.MoveCardsToDSHand(informant);
		scn.StartGame();
		scn.MoveCardsToLocation(site, spy, trooper);
		scn.AttachCardsTo(trooper, blaster);
		scn.MakeCardGoUndercover(spy);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSCardPlayAvailable(sabotage));
		scn.LSPlayCard(sabotage);
		if (scn.LSHasCardChoiceAvailable(blaster)) {
			scn.LSChooseCard(blaster);
		}
		assertTrue(scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(informant));
		scn.DSPlayCard(informant);
		scn.PassAllResponses();

		assertEquals(Zone.ATTACHED, blaster.getZone());
		assertEquals(trooper, blaster.getAttachedTo());
		assertEquals(informant, scn.GetTopOfDSUsedPile());
	}
}
