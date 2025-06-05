package com.gempukku.swccgo.rules.state;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class CaptiveTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("chewie", "200_5");
					put("protector", "10_3"); //Chewbacca persona
				}},
				new HashMap<>()
				{{
					put("boba", "5_91");
					put("tube", "1_308");
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
	public void CaptivesAreNotActive() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, chewie);
		scn.CaptureCardWith(boba, chewie);

		assertTrue(chewie.isCaptive());
		assertFalse(scn.IsCardActive(chewie));
	}

	@Test
	public void CaptivesAreNotConsideredAtTheirLocation() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, chewie);
		scn.CaptureCardWith(boba, chewie);

		assertTrue(chewie.isCaptive());
		assertFalse(scn.GetCardsAtLocation(site).contains(chewie));
	}

	@Test
	public void CaptivesEnforceUniqueness() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var protector = scn.GetLSCard("protector");
		var site = scn.GetLSStartingLocation();
		scn.MoveCardsToLSHand(protector);

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, chewie);
		scn.CaptureCardWith(boba, chewie);

		scn.LSActivateForceCheat(2);

		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(chewie.isCaptive());
		assertTrue(scn.AwaitingLSDeployPhaseActions());
		assertEquals(5, protector.getBlueprint().getDeployCost(), scn.epsilon);
		assertEquals(5, scn.GetLSForcePileCount());
		assertFalse(scn.LSDeployAvailable(protector));
	}

	@Test
	public void CaptivesTakeUpAPassengerSlotIfEscortIsInAVehicle() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");
		var tube = scn.GetDSCard("tube");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, tube, chewie);
		scn.CaptureCardWith(boba, chewie);

		assertEquals(4, scn.GetPassengerCapacity(tube));

		scn.BoardAsPassenger(tube, boba);
		assertTrue(chewie.isCaptive());
		assertTrue(scn.IsAboardAsPassenger(tube, boba));

		//4 minus 1 for Boba and minus 1 for his captive
		assertEquals(2, scn.GetPassengerCapacity(tube));
	}

}
