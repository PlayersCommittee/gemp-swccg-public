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

public class FrozenTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("han", "1_11");
					put("leia", "1_17");
					put("loves_you", "6_75");
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
	public void FrozenCaptivesAreNotActive() {
		var scn = GetScenario();

		var han = scn.GetLSCard("han");
		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, han);
		scn.FreezeCard(han);

		assertTrue(han.isCaptive());
		assertFalse(scn.IsCardActive(han));
	}

	@Test
	public void FrozenCaptivesHaveZeroedStats() {
		var scn = GetScenario();

		var han = scn.GetLSCard("han");
		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, han);
		scn.FreezeCard(han);

		assertEquals(0, scn.GetPower(han));
		assertEquals(0, scn.GetAbility(han));
		assertEquals(0, scn.GetLandspeed(han));
	}

	@Test
	public void FrozenCaptivesCanBeTakenIntoCustodyByWarriorAtSameSiteDuringMovePhase() {
		var scn = GetScenario();

		var han = scn.GetLSCard("han");
		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, stormtrooper, han);
		scn.FreezeCard(han);

		scn.SkipToPhase(Phase.MOVE);

		assertTrue(han.isCaptive());
		assertTrue(han.isFrozen());
		assertNull(han.getEscort());
		assertEquals(0, stormtrooper.getCardsEscorting().size());

		assertTrue(scn.DSActionAvailable("Take unattended frozen captive into custody"));
		scn.DSChooseAction("Take unattended frozen captive into custody");
		scn.PassAllResponses();

		assertEquals(stormtrooper, han.getEscort());
		assertEquals(stormtrooper, han.getAttachedTo());
		assertEquals(1, stormtrooper.getCardsEscorting().size());
		assertTrue(stormtrooper.getCardsEscorting().contains(han));
		assertEquals(1, stormtrooper.getCardsAttached().size());
		assertTrue(stormtrooper.getCardsAttached().contains(han));
	}

	@Test
	public void FrozenCaptivesCanBeTakenIntoCustodyByBountyHunterAtSameSiteDuringMovePhase() {
		var scn = GetScenario();

		var han = scn.GetLSCard("han");
		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, han);
		scn.FreezeCard(han);

		scn.SkipToPhase(Phase.MOVE);

		assertTrue(han.isCaptive());
		assertTrue(han.isFrozen());
		assertNull(han.getEscort());
		assertEquals(0, boba.getCardsEscorting().size());
		assertNull(han.getAttachedTo());
		assertEquals(0, boba.getCardsAttached().size());

		assertTrue(scn.DSActionAvailable("Take unattended frozen captive into custody"));
		scn.DSChooseAction("Take unattended frozen captive into custody");
		scn.PassAllResponses();

		assertEquals(boba, han.getEscort());
		assertEquals(boba, han.getAttachedTo());
		assertEquals(1, boba.getCardsEscorting().size());
		assertTrue(boba.getCardsEscorting().contains(han));
		assertEquals(1, boba.getCardsAttached().size());
		assertTrue(boba.getCardsAttached().contains(han));
	}

	@Ignore("Target clearing has been reverted; this will fail until that is restored.")
	@Test
	public void FrozenCaptivesCanBeLeftUnattendedByEscortAtSameSiteDuringMovePhase() {
		var scn = GetScenario();

		var han = scn.GetLSCard("han");
		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, han);
		scn.FreezeCard(han);
		scn.CaptureCardWith(boba, han);

		scn.SkipToPhase(Phase.MOVE);

		assertTrue(han.isCaptive());
		assertTrue(han.isFrozen());
		assertEquals(boba, han.getEscort());
		assertEquals(boba, han.getAttachedTo());
		assertEquals(1, boba.getCardsEscorting().size());
		assertTrue(boba.getCardsEscorting().contains(han));
		assertEquals(1, boba.getCardsAttached().size());
		assertTrue(boba.getCardsAttached().contains(han));
		assertTrue(han.getTargetedCards(scn.gameState()).containsValue(boba));

		assertTrue(scn.DSActionAvailable("Leave frozen captive unattended"));
		scn.DSChooseAction("Leave frozen captive unattended");
		scn.PassAllResponses();

		assertNull(han.getEscort());
		assertNull(han.getAttachedTo());
		assertFalse(han.getTargetedCards(scn.gameState()).containsValue(boba));
		assertAtLocation(site, han);
		assertTrue(han.isCaptive());
		assertTrue(han.isFrozen());

		assertEquals(0, boba.getCardsEscorting().size());
		assertEquals(0, boba.getCardsAttached().size());

		scn.LSPass();
		assertFalse(scn.DSActionAvailable("Leave frozen captive unattended"));
	}

	@Ignore("Target clearing has been reverted; this will fail until that is restored.")
	@Test
	public void FrozenCaptivesIfReleasedAfterBeingUnattendedDoNotStillTargetLastEscort() {
		var scn = GetScenario();

		var rebel = scn.GetLSFiller(1);
		var han = scn.GetLSCard("han");
		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, han, rebel);
		scn.FreezeCard(han);
		scn.CaptureCardWith(boba, han);

		scn.SkipToPhase(Phase.MOVE);

		assertTrue(han.isCaptive());
		assertTrue(han.isFrozen());
		assertEquals(boba, han.getEscort());
		assertEquals(boba, han.getAttachedTo());
		assertEquals(1, boba.getCardsEscorting().size());
		assertTrue(boba.getCardsEscorting().contains(han));
		assertEquals(1, boba.getCardsAttached().size());
		assertTrue(boba.getCardsAttached().contains(han));
		assertTrue(han.getTargetedCards(scn.gameState()).containsValue(boba));

		assertTrue(scn.DSActionAvailable("Leave frozen captive unattended"));
		scn.DSChooseAction("Leave frozen captive unattended");
		scn.PassAllResponses();

		assertNull(han.getEscort());
		assertNull(han.getAttachedTo());
		assertFalse(han.getTargetedCards(scn.gameState()).containsValue(boba));
		assertAtLocation(site, han);
		assertTrue(han.isCaptive());
		assertTrue(han.isFrozen());

		assertEquals(0, boba.getCardsEscorting().size());
		assertEquals(0, boba.getCardsAttached().size());

		//Now that Boba has left Han unattended, we will free him and see if he still targets Boba
		scn.LSPass();
		scn.DSPass();
		scn.MoveCardsToHand(boba);
		scn.SkipToLSTurn(Phase.MOVE);

		assertTrue(scn.LSActionAvailable("Release an unattended frozen captive"));
		scn.LSChooseAction("Release an unattended frozen captive");
		scn.LSChooseCard(han);

		assertFalse(han.isCaptive());
		assertFalse(han.isFrozen());
		assertNull(han.getEscort());
		assertNull(han.getAttachedTo());
		assertFalse(han.getTargetedCards(scn.gameState()).containsValue(boba));
		assertAtLocation(site, han);
	}

	@Test
	public void FrozenCaptiveJoinsBattleWhenReleasedMidBattle() {
		var scn = GetScenario();

		var han = scn.GetLSCard("han");
		var leia = scn.GetLSCard("leia");
		var loves_you = scn.GetLSCard("loves_you");
		scn.MoveCardsToHand(loves_you);

		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, leia, han, stormtrooper);
		scn.FreezeCard(han);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(han.isFrozen());
		assertTrue(han.isCaptive());
		assertNull(han.getAttachedTo());
		assertNull(han.getEscort());
		assertAtLocation(site, han, leia);
		assertInHand(loves_you);

		scn.LSInitiateBattle(site);
		scn.PassBattleStartResponses();

		assertTrue(scn.IsParticipatingInBattle(leia, stormtrooper));
		assertFalse(scn.IsParticipatingInBattle(han));
		assertTrue(scn.LSCardPlayAvailable(loves_you));
		scn.LSPlayCardAndPassResponses(loves_you, han);

		assertFalse(han.isFrozen());
		assertFalse(han.isCaptive());
		assertAtLocation(site, han);
		assertTrue(scn.IsParticipatingInBattle(han));
	}
}
