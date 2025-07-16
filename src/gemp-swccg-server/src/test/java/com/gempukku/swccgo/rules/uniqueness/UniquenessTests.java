package com.gempukku.swccgo.rules.uniqueness;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class UniquenessTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{

				}},
				new HashMap<>() {{
					put("pitdroid1", "11_060"); //non-unique
					put("pitdroid2", "11_060");
					put("pitdroid3", "11_060");
					put("pitdroid4", "11_060");
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
	public void NonuniqueCharacterDeploy() {
		//test coverage:
		//non-unique card can be deployed without limit (4+ copies)

		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var pitdroid1 = scn.GetDSCard("pitdroid1");
		var pitdroid2 = scn.GetDSCard("pitdroid2");
		var pitdroid3 = scn.GetDSCard("pitdroid3");
		var pitdroid4 = scn.GetDSCard("pitdroid4");

		assertEquals(Uniqueness.UNRESTRICTED, pitdroid1.getBlueprint().getUniqueness());

		var pitdroidDeployCost = pitdroid1.getBlueprint().getDeployCost();

		scn.StartGame();

		scn.MoveCardsToDSHand(pitdroid1, pitdroid2, pitdroid3, pitdroid4);

		scn.DSActivateMaxForceAndPass();
		scn.SkipToPhase(Phase.DEPLOY);
		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.DEPLOY); //generate extra force

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertTrue(scn.GetDSForcePileCount() >= (pitdroidDeployCost * 4)); //enough force to deploy 4 copies
		assertTrue(scn.DSCardPlayAvailable(pitdroid1));
		scn.DSDeployCardAndPassResponses(pitdroid1, site);
		scn.LSPass();
		assertTrue(scn.CardsAtLocation(site, pitdroid1)); //successfully deployed

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertTrue(scn.DSCardPlayAvailable(pitdroid2));
		scn.DSDeployCardAndPassResponses(pitdroid2, site);
		scn.LSPass();
		assertTrue(scn.CardsAtLocation(site, pitdroid2)); //successfully deployed

		scn.DSDeployCardAndPassResponses(pitdroid3, site);
		scn.LSPass();
		assertTrue(scn.CardsAtLocation(site, pitdroid3)); //successfully deployed

		scn.DSDeployCardAndPassResponses(pitdroid4, site);
		scn.LSPass();
		assertTrue(scn.CardsAtLocation(site, pitdroid4)); //successfully deployed
	}

}