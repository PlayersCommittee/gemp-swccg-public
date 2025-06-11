package com.gempukku.swccgo.rules.state;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.*;

public class CaptiveTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("chewie", "200_5");
					put("protector", "10_3"); //Chewbacca persona
					put("han", "1_11");
					put("leia", "1_17");
					put("loves_you", "6_75");
					put("wioslea", "1_33"); //purchases droids
				}},
				new HashMap<>()
				{{
					put("boba", "5_91");
					put("tube", "1_308");
					put("4lom", "4_91");
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

	@Test
	public void CaptiveJoinsBattleWhenReleasedMidBattle() {
		var scn = GetScenario();

		var han = scn.GetLSCard("han");
		var leia = scn.GetLSCard("leia");
		var loves_you = scn.GetLSCard("loves_you");
		scn.MoveCardsToHand(loves_you);

		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, leia, han, stormtrooper);
		scn.CaptureCardWith(stormtrooper, han);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertFalse(han.isFrozen());
		assertTrue(han.isCaptive());
		assertEquals(stormtrooper, han.getAttachedTo());
		assertEquals(stormtrooper, han.getEscort());
		assertAtLocation(site, leia, stormtrooper);
		assertInHand(loves_you);

		scn.LSInitiateBattle(site);
		scn.PassBattleStartResponses();

		assertTrue(scn.IsParticipatingInBattle(leia, stormtrooper));
		assertFalse(scn.IsParticipatingInBattle(han));
		assertTrue(scn.LSCardPlayAvailable(loves_you));
		scn.LSPlayCardAndPassResponses(loves_you, han);
		scn.LSChooseRally();

		assertFalse(han.isFrozen());
		assertFalse(han.isCaptive());
		assertAtLocation(site, han);
		assertTrue(scn.IsParticipatingInBattle(han));
	}
	
	//See https://github.com/PlayersCommittee/gemp-swccg-public/issues/849
	@Ignore("https://github.com/PlayersCommittee/gemp-swccg-public/issues/849")
	@Test
	public void CaptivesAreAutomaticallyFreedIfEscortIsPurchased() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var han = scn.GetLSCard("han");
		var leia = scn.GetLSCard("leia");
		var wioslea = scn.GetLSCard("wioslea");

		var site = scn.GetLSStartingLocation();

		var fourlom = scn.GetDSCard("4lom");

		scn.StartGame();

		scn.MoveCardsToLocation(site, fourlom, chewie, han, leia, wioslea);
		scn.CaptureCardWith(fourlom, chewie);
		scn.CaptureCardWith(fourlom, han);
		scn.CaptureCardWith(fourlom, leia);

		assertTrue(chewie.isCaptive());
		assertEquals(fourlom, chewie.getEscort());
		assertEquals(fourlom, chewie.getAttachedTo());

		assertTrue(han.isCaptive());
		assertEquals(fourlom, han.getEscort());
		assertEquals(fourlom, han.getAttachedTo());

		assertTrue(leia.isCaptive());
		assertEquals(fourlom, leia.getEscort());
		assertEquals(fourlom, leia.getAttachedTo());

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSCardActionAvailable(wioslea));
		assertEquals(scn.DS, fourlom.getOwner());

		scn.PrepareLSDestiny(7);
		scn.LSUseCardAction(wioslea);
		scn.LSChooseCard(fourlom);
		scn.PassAllResponses();

		assertEquals(scn.LS, fourlom.getOwner());

		assertTrue(scn.LSDecisionAvailable("Choose character to release"));
		assertFalse(scn.LSHasCardChoicesAvailable(han));
		scn.DSChooseCard(han);
		scn.LSChooseRally();
		scn.PassAllResponses();

		assertTrue(scn.LSDecisionAvailable("Choose character to release"));
		assertFalse(scn.LSHasCardChoicesAvailable(chewie));
		scn.DSChooseCard(chewie);
		scn.LSChooseRally();
		scn.PassAllResponses();

		scn.LSChooseRally();
		scn.PassAllResponses();

		assertFalse(chewie.isCaptive());
		assertNull(chewie.getAttachedTo());
		assertNull(chewie.getEscort());
		assertAtLocation(site, chewie);

		assertFalse(han.isCaptive());
		assertNull(han.getAttachedTo());
		assertNull(han.getEscort());
		assertAtLocation(site, han);

		assertFalse(leia.isCaptive());
		assertNull(leia.getAttachedTo());
		assertNull(leia.getEscort());
		assertAtLocation(site, leia);
	}

}
