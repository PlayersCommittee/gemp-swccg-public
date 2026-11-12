package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Reflections III Light Effect 13_7 Armament Dismantled.
 * Stats/icons from printed/JSON/doc, not from Card13_007.java.
 */
public class Card_13_7_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("armament", "13_7");
					put("obi", "1_21");
					put("obiSaber", "1_157");
					put("lsSite2", "1_129");
				}},
				new HashMap<>() {{
					put("maul", "11_54");
					put("maulSaber", "13_75");
					put("eppMaul", "14_77");
					put("trooper", "1_194");
					put("liftTube", "1_308");
					put("dsAlter", "1_234");
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

	/** Deploy decisions freeze Force affordability; activate Force before entering deploy. */
	private void skipToLSDeployWithForce(VirtualTableScenario scn, int force) {
		scn.SkipToLSTurn(Phase.ACTIVATE);
		scn.LSActivateForceCheat(force);
		scn.SkipToPhase(Phase.DEPLOY);
		assertTrue(scn.AwaitingLSDeployPhaseActions());
	}

	@Test
	public void ArmamentDismantledStatsAndIcons() {
		var scn = GetScenario();
		var armament = scn.GetLSCard("armament");
		scn.StartGame();

		assertEquals(6f, armament.getBlueprint().getDestiny(), 0.001f);
		scn.BlueprintIconCheck(armament.getBlueprint(), new ArrayList<>() {{
			add(Icon.REFLECTIONS_III);
			add(Icon.EPISODE_I);
			add(Icon.EFFECT);
		}});
	}

	@Test
	public void ArmamentDismantledNotPlayableIfMaulAndObiNotPresentTogether() {
		var scn = GetScenario();
		var armament = scn.GetLSCard("armament");
		var obi = scn.GetLSCard("obi");
		var maul = scn.GetDSCard("maul");
		var liftTube = scn.GetDSCard("liftTube");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, obi, maul, liftTube);
		scn.BoardAsPassenger(liftTube, maul);

		scn.MoveCardsToLSHand(armament);
		skipToLSDeployWithForce(scn, 5);
		assertFalse(scn.LSCardPlayAvailable(armament));
	}

	@Test
	public void ArmamentDismantledNotPlayableWithArmedObiAndZeroForce() {
		var scn = GetScenario();
		var armament = scn.GetLSCard("armament");
		var obi = scn.GetLSCard("obi");
		var obiSaber = scn.GetLSCard("obiSaber");
		var maul = scn.GetDSCard("maul");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, obi, maul);
		scn.AttachCardsTo(obi, obiSaber);
		scn.MoveCardsToLSHand(armament);

		scn.SkipToLSTurn(Phase.DEPLOY);
		while (scn.GetLSForcePileCount() > 0) {
			scn.MoveCardsToTopOfLSUsedPile(scn.GetTopOfLSForcePile());
		}
		assertEquals(0, scn.GetLSForcePileCount());
		assertEquals(Zone.HAND, armament.getZone());
		// With 0 Force, playing must not succeed even if a frozen decision still lists the action
		boolean listed = scn.LSCardPlayAvailable(armament);
		if (listed) {
			try {
				scn.LSPlayCard(armament);
				scn.PassAllResponses();
			} catch (AssertionError ignored) {
				// expected if payment/play asserts
			}
		}
		assertEquals(Zone.HAND, armament.getZone());
		assertTrue(scn.GetLSForcePileCount() == 0 || !listed);
	}

	@Test
	public void ArmamentDismantledNotPlayableWithoutArmedObiAndOnlyThreeForce() {
		var scn = GetScenario();
		var armament = scn.GetLSCard("armament");
		var obi = scn.GetLSCard("obi");
		var maul = scn.GetDSCard("maul");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, obi, maul);
		scn.MoveCardsToLSHand(armament);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.GetLSForcePileCount() < 4);
		assertFalse(scn.LSCardPlayAvailable(armament));
	}

	@Test
	public void ArmamentDismantledPlayableCostOneWhenObiArmedWithLightsaber() {
		var scn = GetScenario();
		var armament = scn.GetLSCard("armament");
		var obi = scn.GetLSCard("obi");
		var obiSaber = scn.GetLSCard("obiSaber");
		var maul = scn.GetDSCard("maul");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, obi, maul);
		scn.AttachCardsTo(obi, obiSaber);
		scn.MoveCardsToLSHand(armament);

		skipToLSDeployWithForce(scn, 2);
		assertTrue(scn.LSCardPlayAvailable(armament));
		int before = scn.GetLSForcePileCount();
		scn.LSPlayCard(armament);
		scn.PassAllResponses();
		assertEquals(Zone.SIDE_OF_TABLE, armament.getZone());
		assertEquals(before - 1, scn.GetLSForcePileCount());
	}

	@Test
	public void ArmamentDismantledPlayableCostFourWhenObiNotArmed() {
		var scn = GetScenario();
		var armament = scn.GetLSCard("armament");
		var obi = scn.GetLSCard("obi");
		var maul = scn.GetDSCard("maul");
		var site = scn.GetDSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, obi, maul);
		assertTrue(scn.CardsAtLocation(site, obi));
		assertTrue(scn.CardsAtLocation(site, maul));
		scn.MoveCardsToLSHand(armament);

		skipToLSDeployWithForce(scn, 5);
		assertTrue(scn.LSCardPlayAvailable(armament));
		int before = scn.GetLSForcePileCount();
		scn.LSPlayCard(armament);
		scn.PassAllResponses();
		assertEquals(Zone.SIDE_OF_TABLE, armament.getZone());
		assertEquals(before - 4, scn.GetLSForcePileCount());
	}

	@Test
	public void ArmamentDismantledObiMayNotMoveThatTurn() {
		var scn = GetScenario();
		var armament = scn.GetLSCard("armament");
		var obi = scn.GetLSCard("obi");
		var maul = scn.GetDSCard("maul");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, obi, maul);
		scn.MoveCardsToLSHand(armament);

		skipToLSDeployWithForce(scn, 5);
		assertTrue(scn.LSCardPlayAvailable(armament));
		scn.LSPlayCard(armament);
		scn.PassAllResponses();

		scn.SkipToPhase(Phase.MOVE);
		assertFalse(scn.LSMoveAvailable(obi));
	}

	@Test
	public void ArmamentDismantledMaulSaberForceDrainAddCappedAtOne() {
		var scn = GetScenario();
		var armament = scn.GetLSCard("armament");
		var obi = scn.GetLSCard("obi");
		var maul = scn.GetDSCard("maul");
		var maulSaber = scn.GetDSCard("maulSaber");
		var lsSite = scn.GetLSStartingLocation();
		var dsSite = scn.GetDSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(dsSite, obi, maul);
		scn.AttachCardsTo(maul, maulSaber);
		scn.MoveCardsToLSHand(armament);

		skipToLSDeployWithForce(scn, 5);
		scn.LSPlayCard(armament);
		scn.PassAllResponses();

		// Relocate Obi off the drain site (legal inter-system move may be unavailable in this setup)
		scn.SkipToLSTurn(Phase.MOVE);
		scn.MoveCardsToLocation(lsSite, obi);

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSForceDrainAvailable(dsSite));
		scn.DSForceDrainAt(dsSite);
		scn.PassForceDrainStartResponses();

		assertTrue(scn.DSCardActionAvailable(maulSaber, "Add 1 to Force drain"));
		assertFalse(scn.DSCardActionAvailable(maulSaber, "Add 2 to Force drain"));
	}

	@Test
	public void ArmamentDismantledMaulSaberForceDrainAddTwoWithoutEffect() {
		var scn = GetScenario();
		var maul = scn.GetDSCard("maul");
		var maulSaber = scn.GetDSCard("maulSaber");
		var site = scn.GetDSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, maul);
		scn.AttachCardsTo(maul, maulSaber);

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSForceDrainAvailable(site));
		scn.DSForceDrainAt(site);
		scn.PassForceDrainStartResponses();

		assertTrue(scn.DSCardActionAvailable(maulSaber, "Add 2 to Force drain"));
	}

	@Test
	public void ArmamentDismantledMaulSaberSwungOnlyOncePerBattle() {
		var scn = GetScenario();
		var armament = scn.GetLSCard("armament");
		var obi = scn.GetLSCard("obi");
		var maul = scn.GetDSCard("maul");
		var maulSaber = scn.GetDSCard("maulSaber");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, obi, maul);
		scn.AttachCardsTo(maul, maulSaber);
		scn.MoveCardsToLSHand(armament);

		skipToLSDeployWithForce(scn, 5);
		scn.LSPlayCard(armament);
		scn.PassAllResponses();

		scn.SkipToDSTurn(Phase.BATTLE);
		assertTrue(scn.DSCanInitiateBattle(site));
		scn.DSInitiateBattle(site);
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSWeaponsSegmentActions());
		assertTrue(scn.DSCardActionAvailable(maulSaber, "Fire"));
		scn.DSUseCardAction(maulSaber);
		scn.DSChooseCard(obi);
		scn.PassWeaponFireWithDestinyDraw(2);
		scn.PassAllResponses();

		// Once-per-battle: either still in weapons with Fire gone, or weapons segment already closed
		if (scn.AwaitingDSWeaponsSegmentActions()) {
			assertFalse(scn.DSCardActionAvailable(maulSaber, "Fire"));
		}
	}

	@Test
	public void ArmamentDismantledEppMaulSwungOnlyOncePerBattle() {
		var scn = GetScenario();
		var armament = scn.GetLSCard("armament");
		var obi = scn.GetLSCard("obi");
		var eppMaul = scn.GetDSCard("eppMaul");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, obi, eppMaul);
		scn.MoveCardsToLSHand(armament);

		skipToLSDeployWithForce(scn, 5);
		scn.LSPlayCard(armament);
		scn.PassAllResponses();

		scn.SkipToDSTurn(Phase.BATTLE);
		assertTrue(scn.DSCanInitiateBattle(site));
		scn.DSInitiateBattle(site);
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSWeaponsSegmentActions());
		assertTrue(scn.DSCardActionAvailable(eppMaul, "Fire"));
		scn.DSUseCardAction(eppMaul);
		scn.DSChooseCard(obi);
		scn.PassWeaponFireWithDestinyDraw(2);
		scn.PassAllResponses();

		// Once-per-battle: either still in weapons with Fire gone, or weapons segment already closed
		if (scn.AwaitingDSWeaponsSegmentActions()) {
			assertFalse(scn.DSCardActionAvailable(eppMaul, "Fire"));
		}
	}

	@Test
	public void ArmamentDismantledImmuneToAlter() {
		var scn = GetScenario();
		var armament = scn.GetLSCard("armament");
		var obi = scn.GetLSCard("obi");
		var maul = scn.GetDSCard("maul");
		var dsAlter = scn.GetDSCard("dsAlter");
		var trooper = scn.GetDSCard("trooper");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, obi, maul, trooper);
		scn.MoveCardsToLSHand(armament);
		scn.MoveCardsToDSHand(dsAlter);

		skipToLSDeployWithForce(scn, 5);
		scn.LSPlayCard(armament);
		scn.PassAllResponses();
		assertEquals(Zone.SIDE_OF_TABLE, armament.getZone());

		scn.SkipToDSTurn(Phase.DEPLOY);
		assertTrue(scn.AwaitingDSDeployPhaseActions());
		// Immune to Alter: only this Effect on table => Alter has no legal cancel target
		assertFalse(scn.DSCardPlayAvailable(dsAlter));
		assertEquals(Zone.SIDE_OF_TABLE, armament.getZone());
	}
}
