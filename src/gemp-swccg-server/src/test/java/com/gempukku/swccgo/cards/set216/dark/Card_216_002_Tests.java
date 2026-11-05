package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * VHD for Alien Mob (216_002) diamond uniqueness on system-changing movement (#1030).
 *
 * Covered: hyperspeed, without-hyperspeed (mobile system to/from planet), docking bay transit.
 * Same-system landspeed / sector / land / takeoff loops are out of scope for #1030.
 */
public class Card_216_002_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
				}},
				new HashMap<>() {{
					put("alienMob1", "216_002");
					put("alienMob2", "216_002");
					put("cruiser", "7_304"); // Jabba's Space Cruiser
					put("rodian", "2_104"); // alien pilot
					put("nalHutta", "6_168");
					put("bespin", "5_164");
					put("downtownPlaza", "7_270"); // Cloud City: Downtown Plaza (Bespin)
					put("eastPlatform", "5_169"); // Cloud City: East Platform (Docking Bay)
					put("tatDb", "1_291"); // Tatooine: Docking Bay 94
					put("deathStar", "2_143"); // mobile system for without-hyperspeed
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

	@Test
	public void AlienMobStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetDSCard("alienMob1").getBlueprint();
		assertEquals("Alien Mob", card.getTitle());
		assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
	}

	@Test
	public void AlienMobAboardStarshipCannotHyperspeedToSystemWithAlienMobAtRelatedSite() {
		// Replay path: ship + Mob at Nal Hutta must not hyperspeed to Bespin
		// while another Alien Mob is at a Cloud City site.
		var scn = GetScenario();

		var alienMob1 = scn.GetDSCard("alienMob1");
		var alienMob2 = scn.GetDSCard("alienMob2");
		var cruiser = scn.GetDSCard("cruiser");
		var rodian = scn.GetDSCard("rodian");
		var nalHutta = scn.GetDSCard("nalHutta");
		var bespin = scn.GetDSCard("bespin");
		var downtownPlaza = scn.GetDSCard("downtownPlaza");

		scn.StartGame();

		scn.MoveLocationToTable(nalHutta);
		scn.MoveLocationToTable(bespin);
		scn.MoveLocationToTable(downtownPlaza);

		scn.MoveCardsToLocation(nalHutta, cruiser);
		scn.BoardAsPilot(cruiser, rodian);
		scn.BoardAsPassenger(cruiser, alienMob1);
		scn.MoveCardsToLocation(downtownPlaza, alienMob2);

		scn.DSActivateMaxForceAndPass();
		scn.SkipToPhase(Phase.MOVE);

		assertTrue(scn.DSMoveAvailable(cruiser));
		scn.DSUseCardAction(cruiser, "Move using hyperspeed");
		assertFalse(scn.DSHasCardChoiceAvailable(bespin));
	}

	@Test
	public void AlienMobAboardStarshipCanHyperspeedToSystemWithoutAlienMob() {
		var scn = GetScenario();

		var alienMob1 = scn.GetDSCard("alienMob1");
		var cruiser = scn.GetDSCard("cruiser");
		var rodian = scn.GetDSCard("rodian");
		var nalHutta = scn.GetDSCard("nalHutta");
		var bespin = scn.GetDSCard("bespin");

		scn.StartGame();

		scn.MoveLocationToTable(nalHutta);
		scn.MoveLocationToTable(bespin);

		scn.MoveCardsToLocation(nalHutta, cruiser);
		scn.BoardAsPilot(cruiser, rodian);
		scn.BoardAsPassenger(cruiser, alienMob1);

		scn.DSActivateMaxForceAndPass();
		scn.SkipToPhase(Phase.MOVE);

		assertTrue(scn.DSMoveAvailable(cruiser));
		scn.DSUseCardAction(cruiser, "Move using hyperspeed");
		assertTrue(scn.DSHasCardChoiceAvailable(bespin));
	}

	@Test
	public void AlienMobAboardStarshipCannotMoveWithoutHyperspeedToSystemWithAlienMobAtRelatedSite() {
		// Without-hyperspeed path: ship + Mob at Death Star orbiting Bespin must not
		// move to Bespin while another Alien Mob is at a Cloud City site.
		var scn = GetScenario();

		var alienMob1 = scn.GetDSCard("alienMob1");
		var alienMob2 = scn.GetDSCard("alienMob2");
		var cruiser = scn.GetDSCard("cruiser");
		var rodian = scn.GetDSCard("rodian");
		var deathStar = scn.GetDSCard("deathStar");
		var bespin = scn.GetDSCard("bespin");
		var downtownPlaza = scn.GetDSCard("downtownPlaza");

		scn.StartGame();

		scn.MoveLocationToTable(bespin);
		scn.MoveLocationToTable(deathStar);
		scn.MoveLocationToTable(downtownPlaza);
		deathStar.setSystemOrbited("Bespin");

		scn.MoveCardsToLocation(deathStar, cruiser);
		scn.BoardAsPilot(cruiser, rodian);
		scn.BoardAsPassenger(cruiser, alienMob1);
		scn.MoveCardsToLocation(downtownPlaza, alienMob2);

		scn.DSActivateMaxForceAndPass();
		scn.SkipToPhase(Phase.MOVE);

		// Only destination is Bespin; uniqueness removes it, so the without-hyperspeed action is omitted.
		assertFalse("Without-hyperspeed must be unavailable when destination system already has Alien Mob",
				scn.DSCardActionAvailable(cruiser, "Move without using hyperspeed"));
	}

	@Test
	public void AlienMobAboardStarshipCanMoveWithoutHyperspeedToSystemWithoutAlienMob() {
		var scn = GetScenario();

		var alienMob1 = scn.GetDSCard("alienMob1");
		var cruiser = scn.GetDSCard("cruiser");
		var rodian = scn.GetDSCard("rodian");
		var deathStar = scn.GetDSCard("deathStar");
		var bespin = scn.GetDSCard("bespin");

		scn.StartGame();

		scn.MoveLocationToTable(bespin);
		scn.MoveLocationToTable(deathStar);
		deathStar.setSystemOrbited("Bespin");

		scn.MoveCardsToLocation(deathStar, cruiser);
		scn.BoardAsPilot(cruiser, rodian);
		scn.BoardAsPassenger(cruiser, alienMob1);

		scn.DSActivateMaxForceAndPass();
		scn.SkipToPhase(Phase.MOVE);

		assertTrue(scn.DSCardActionAvailable(cruiser, "Move without using hyperspeed"));
		scn.DSUseCardAction(cruiser, "Move without using hyperspeed");
		assertTrue(scn.DSHasCardChoiceAvailable(bespin));
	}

	@Test
	public void AlienMobCannotDockingBayTransitToSystemWithAlienMobAtRelatedSite() {
		// System-changing relocate path: Mob at Tatooine DB must not transit to a
		// Cloud City docking bay while another Alien Mob is at a related Bespin site.
		var scn = GetScenario();

		var alienMob1 = scn.GetDSCard("alienMob1");
		var alienMob2 = scn.GetDSCard("alienMob2");
		var tatDb = scn.GetDSCard("tatDb");
		var eastPlatform = scn.GetDSCard("eastPlatform");
		var downtownPlaza = scn.GetDSCard("downtownPlaza");
		var bespin = scn.GetDSCard("bespin");

		scn.StartGame();

		scn.MoveLocationToTable(bespin);
		scn.MoveLocationToTable(downtownPlaza);
		scn.MoveLocationToTable(eastPlatform);
		scn.MoveLocationToTable(tatDb);

		scn.MoveCardsToLocation(tatDb, alienMob1);
		scn.MoveCardsToLocation(downtownPlaza, alienMob2);

		scn.DSActivateMaxForceAndPass();
		scn.SkipToPhase(Phase.MOVE);

		// Only other docking bay is in the Bespin system; uniqueness removes it, so transit is omitted.
		assertFalse("Docking bay transit must be unavailable into a system that already has Alien Mob",
				scn.DSCardActionAvailable(tatDb, "transit"));
	}

	@Test
	public void AlienMobDeployUniquenessStillBlocksSameSystem() {
		var scn = GetScenario();

		var alienMob1 = scn.GetDSCard("alienMob1");
		var alienMob2 = scn.GetDSCard("alienMob2");
		var downtownPlaza = scn.GetDSCard("downtownPlaza");
		var tatDb = scn.GetDSCard("tatDb");
		var bespin = scn.GetDSCard("bespin");

		scn.StartGame();

		scn.MoveLocationToTable(bespin);
		scn.MoveLocationToTable(downtownPlaza);
		scn.MoveLocationToTable(tatDb);

		scn.MoveCardsToLocation(downtownPlaza, alienMob1);
		scn.MoveCardsToDSHand(alienMob2);

		scn.DSActivateForceCheat(10);
		scn.SkipToPhase(Phase.DEPLOY);

		assertTrue("Alien Mob should still deploy to another system", scn.DSCardPlayAvailable(alienMob2));
		scn.DSDeployCard(alienMob2);
		assertFalse("Cannot deploy Alien Mob to same system", scn.DSHasCardChoiceAvailable(downtownPlaza));
		assertTrue("Can deploy Alien Mob to another system", scn.DSHasCardChoiceAvailable(tatDb));
	}
}
