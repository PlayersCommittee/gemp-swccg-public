package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_219_007_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("ebt", "222_20"); //Rebel

				}},
				new HashMap<>()
				{{
					put("pryce", "219_7");
					put("ig88", "109_11"); //EPP IG-88
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


	@Test
	public void PryceActionAfterRebelSeizedTest() {
		var scn = GetScenario();

		var ebt = scn.GetLSCard("ebt");


		var ig88 = scn.GetDSCard("ig88");
		var pryce = scn.GetDSCard("pryce");


		var site = scn.GetDSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, ebt, ig88, pryce);

		assertEquals(Phase.ACTIVATE, scn.GetCurrentPhase());
		scn.DSActivateMaxForceAndPass();

		scn.PrepareDSDestiny(7);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.PassAllResponses();

		assertTrue(scn.DSCardActionAvailable(ig88));

		scn.DSChooseAction("Fire riot gun");
		scn.DSChooseCardIDFromSelection(ebt);

		scn.PassAllResponses();
		scn.DSChooseOption("Seize");
		scn.LSPass();

		assertTrue(scn.DSCardActionAvailable(pryce));
	}


	@Test @Ignore
	public void PryceActionAfterRebelEscapesTest() {
		var scn = GetScenario();

		var ebt = scn.GetLSCard("ebt");


		var ig88 = scn.GetDSCard("ig88");
		var pryce = scn.GetDSCard("pryce");


		var site = scn.GetDSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, ebt, ig88, pryce);

		assertEquals(Phase.ACTIVATE, scn.GetCurrentPhase());
		scn.DSActivateMaxForceAndPass();

		scn.PrepareDSDestiny(7);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.PassAllResponses();

		assertTrue(scn.DSCardActionAvailable(ig88));

		scn.DSChooseAction("Fire riot gun");
		scn.DSChooseCardIDFromSelection(ebt);

		scn.PassAllResponses();
		scn.DSChooseOption("Escape");
		scn.PassResponses();
		scn.LSPass();

		assertTrue(scn.DSCardActionAvailable(pryce));
	}
}
