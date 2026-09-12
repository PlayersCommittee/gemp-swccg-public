package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Oppressive Enforcement (7_234): "Your Immediate Effects may deploy for free."
 * Same MayDeployFreeModifier as Wise Advice (issue 990).
 */
public class Card_7_234_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("rebelReinforcements", "1_106");
				}},
				new HashMap<>()
				{{
					put("oppressiveEnforcement", "7_234");
					put("tentacle", "2_127");
					put("stormtrooper", "1_194");
					put("cantina", "1_290");
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

	private List<String> grabActionTexts(VirtualTableScenario scn) {
		List<String> grabActions = new ArrayList<>();
		for (String text : scn.GetDSAvailableActions()) {
			if (text != null && text.toLowerCase().contains("grab")) {
				grabActions.add(text);
			}
		}
		return grabActions;
	}

	private void StartLsInterruptSoTentacleCanRespond(VirtualTableScenario scn, boolean oeOnTable) {
		var cantina = scn.GetDSCard("cantina");
		var stormtrooper = scn.GetDSCard("stormtrooper");
		var rebelReinforcements = scn.GetLSCard("rebelReinforcements");
		var tentacle = scn.GetDSCard("tentacle");
		var oe = scn.GetDSCard("oppressiveEnforcement");

		scn.MoveCardsToLSHand(rebelReinforcements);
		scn.MoveCardsToDSHand(tentacle);

		scn.StartGame();
		scn.MoveLocationToTable(cantina);
		scn.MoveCardsToLocation(cantina, stormtrooper);
		scn.LSActivateForceCheat(2);
		scn.DSActivateForceCheat(8);

		if (oeOnTable) {
			scn.MoveCardsToDSSideOfTable(oe);
			assertEquals(Zone.SIDE_OF_TABLE, oe.getZone());
		}

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue("rebelReinforcements not playable; LS=" + scn.GetLSAvailableActions() + " DS=" + scn.GetDSAvailableActions(),
				scn.LSCardPlayAvailable(rebelReinforcements) || scn.LSCardActionAvailable(rebelReinforcements));
		scn.LSPlayCard(rebelReinforcements);
		scn.DSPass();
		scn.LSPass();
	}

	@Test
	public void OppressiveEnforcementOffersFreeAndPrintedImmediateEffectActions() {
		var scn = GetScenario();
		var tentacle = scn.GetDSCard("tentacle");

		StartLsInterruptSoTentacleCanRespond(scn, true);

		assertTrue(scn.DSCardActionAvailable(tentacle) || scn.DSCardPlayAvailable(tentacle));
		List<String> grabs = grabActionTexts(scn);
		assertEquals("grab actions: " + grabs, 2, grabs.size());
		assertTrue("first action should be free: " + grabs, grabs.get(0).toLowerCase().contains("for free"));
		assertTrue("second action should state 1 Force: " + grabs, grabs.get(1).contains("for 1 Force"));
		assertTrue(scn.DSCardActionAvailable(tentacle, "for free"));
		assertTrue(scn.DSCardActionAvailable(tentacle, "for 1 Force"));
	}

	@Test
	public void OppressiveEnforcementForceZeroOffersOnlyFreeImmediateEffectAction() {
		var scn = GetScenario();
		var tentacle = scn.GetDSCard("tentacle");
		var oe = scn.GetDSCard("oppressiveEnforcement");
		var cantina = scn.GetDSCard("cantina");
		var stormtrooper = scn.GetDSCard("stormtrooper");
		var rebelReinforcements = scn.GetLSCard("rebelReinforcements");

		scn.MoveCardsToLSHand(rebelReinforcements);
		scn.MoveCardsToDSHand(tentacle);

		scn.StartGame();
		scn.MoveLocationToTable(cantina);
		scn.MoveCardsToLocation(cantina, stormtrooper);
		scn.MoveCardsToDSSideOfTable(oe);

		// SkipTo runs activate phases (refills Force). Drain DS Force after SkipTo, before the grab window.
		scn.SkipToLSTurn(Phase.CONTROL);
		if (scn.GetDSForcePileCount() > 0) {
			scn.DSUseForceCheat(scn.GetDSForcePileCount());
		}
		assertEquals(0, scn.GetDSForcePileCount());
		if (scn.GetLSForcePileCount() < 1) {
			scn.LSActivateForceCheat(2);
		}

		assertTrue(scn.LSCardPlayAvailable(rebelReinforcements) || scn.LSCardActionAvailable(rebelReinforcements));
		scn.LSPlayCard(rebelReinforcements);
		scn.DSPass();
		scn.LSPass();

		assertEquals(0, scn.GetDSForcePileCount());
		List<String> grabs = grabActionTexts(scn);
		assertEquals("expected free-only grab when Force=0; grabs=" + grabs, 1, grabs.size());
		assertTrue("only action should be free: " + grabs, grabs.get(0).toLowerCase().contains("for free"));
		assertTrue(scn.DSCardActionAvailable(tentacle, "for free"));
		assertFalse(scn.DSCardActionAvailable(tentacle, "for 1 Force"));
	}
}
