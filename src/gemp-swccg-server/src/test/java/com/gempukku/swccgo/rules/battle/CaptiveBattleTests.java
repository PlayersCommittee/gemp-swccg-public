package com.gempukku.swccgo.rules.battle;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class CaptiveBattleTests {
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
					put("vader", "7_175");
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
	public void CaptivesAreNotPresentForDSToInitiateBattle() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, chewie);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToPhase(Phase.BATTLE);

		assertTrue(chewie.isCaptive());
		assertTrue(scn.CardsAtLocation(site, boba));
		assertFalse(scn.CardsAtLocation(site, chewie));
		assertFalse(scn.DSActionAvailable("battle"));
	}

	@Test
	public void CaptivesAreNotPresentForLSToInitiateBattle() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, chewie);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(chewie.isCaptive());
		assertTrue(scn.CardsAtLocation(site, boba));
		assertFalse(scn.CardsAtLocation(site, chewie));
		assertFalse(scn.LSActionAvailable("battle"));
	}

	@Test
	public void CaptivesDoNotContributePowerToBattle() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var trooper = scn.GetLSFiller(1);
		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, chewie, trooper);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToPhase(Phase.BATTLE);

		assertTrue(chewie.isCaptive());
		assertTrue(scn.CardsAtLocation(site, boba, trooper));
		assertFalse(scn.CardsAtLocation(site, chewie));
		assertTrue(scn.DSCanInitiateBattle());

		scn.DSInitiateBattle(site);
		scn.SkipToPowerSegment();

		assertEquals(boba.getBlueprint().getPower().intValue(), scn.GetDSTotalPower());
		assertEquals(trooper.getBlueprint().getPower().intValue(), scn.GetLSTotalPower());
	}

	@Test
	public void CaptivesCannotBeForfeitByLS() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var trooper = scn.GetLSFiller(1);
		var site = scn.GetLSStartingLocation();

		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(site, vader, chewie, trooper);
		scn.CaptureCardWith(vader, chewie);

		scn.SkipToPhase(Phase.BATTLE);

		assertTrue(chewie.isCaptive());
		assertTrue(scn.CardsAtLocation(site, vader, trooper));
		assertFalse(scn.CardsAtLocation(site, chewie));
		assertTrue(scn.DSCanInitiateBattle());

		scn.DSInitiateBattle(site);
		scn.SkipToDamageSegment(true);

		assertTrue(scn.DSWonBattle());
		//Vader drew destiny 1
		assertEquals(1, scn.GetUnpaidLSAttrition());
		// Vader 6 + destiny 1 > Rebel Trooper 1
		assertEquals(6, scn.GetUnpaidLSBattleDamage());
		assertTrue(scn.AwaitingLSAttritionPayment());
		assertTrue(scn.AwaitingLSBattleDamagePayment());

		assertTrue(scn.LSHasCardChoiceAvailable(trooper));
		assertFalse(scn.LSHasCardChoiceAvailable(chewie));
	}
}
