package com.gempukku.swccgo.rules.forcedrain;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ForceDrainTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("nabrun", "1_097");
				}},
				new HashMap<>()
				{{
					put("helrot", "1_243");
					put("plaza", "7_270"); // Cloud City: Downtown Plaza; 2 icons for each
					put("bluffs", "2_150"); //Tatooine: Bluffs; 0 icons for each
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
	public void DSCanForceDrainFor0AtControlledSiteWith0LSIcons() {
		var scn = GetScenario();

		var bluffs = scn.GetDSCard("bluffs");

		var stormtrooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveLocationToTable(bluffs);
		scn.MoveCardsToLocation(bluffs, stormtrooper);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.DSForceDrainAvailable(bluffs));
		assertFalse(scn.IsActiveForceDrain());

		assertEquals(0, scn.GetLSIconsOnLocation(bluffs));
		scn.DSForceDrainAt(bluffs);
		assertEquals(0, scn.GetForceDrainTotal());
		assertTrue(scn.IsActiveForceDrain());

		scn.PassForceDrainStartResponses();
		assertFalse(scn.LSDecisionAvailable("Choose Force to lose"));
		scn.PassForceDrainEndResponses();

		assertFalse(scn.IsActiveForceDrain());
		assertEquals(11, scn.GetLSReserveDeckCount());
	}

	@Test
	public void DSCanForceDrainFor1AtControlledSiteWith1LSIcon() {
		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, stormtrooper);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.DSForceDrainAvailable(site));
		assertFalse(scn.IsActiveForceDrain());

		assertEquals(1, scn.GetLSIconsOnLocation(site));
		scn.DSForceDrainAt(site);
		assertTrue(scn.IsActiveForceDrain());

		scn.PassForceDrainStartResponses();
		assertEquals(1, scn.GetForceDrainTotal());
		assertEquals(1, scn.GetForceDrainRemaining());
		assertEquals(0, scn.GetForceDrainPaidSoFar());

		assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
		assertEquals(11, scn.GetLSReserveDeckCount());
		assertEquals(0, scn.GetLSLostPileCount());

		scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
		assertEquals(0, scn.GetForceDrainRemaining());
		scn.PassForceDrainEndResponses();

		assertEquals(10, scn.GetLSReserveDeckCount());
		assertEquals(1, scn.GetLSLostPileCount());
		assertFalse(scn.IsActiveForceDrain());
	}

	@Test
	public void DSCanForceDrainFor2AtControlledSiteWith2LSIcons() {
		var scn = GetScenario();

		var plaza = scn.GetDSCard("plaza");

		var stormtrooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveLocationToTable(plaza);
		scn.MoveCardsToLocation(plaza, stormtrooper);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.DSForceDrainAvailable(plaza));
		assertFalse(scn.IsActiveForceDrain());

		assertEquals(2, scn.GetLSIconsOnLocation(plaza));
		scn.DSForceDrainAt(plaza);
		assertTrue(scn.IsActiveForceDrain());

		scn.PassForceDrainStartResponses();

		assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
		assertEquals(11, scn.GetLSReserveDeckCount());
		assertEquals(0, scn.GetLSLostPileCount());
		assertEquals(2, scn.GetForceDrainTotal());
		assertEquals(2, scn.GetForceDrainRemaining());
		assertEquals(0, scn.GetForceDrainPaidSoFar());

		scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
		scn.PassForceDrainPendingResponses();
		assertEquals(10, scn.GetLSReserveDeckCount());
		assertEquals(1, scn.GetLSLostPileCount());
		assertEquals(2, scn.GetForceDrainTotal());
		assertEquals(1, scn.GetForceDrainRemaining());
		assertEquals(1, scn.GetForceDrainPaidSoFar());
		assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));

		scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
		assertEquals(9, scn.GetLSReserveDeckCount());
		assertEquals(2, scn.GetLSLostPileCount());
		assertEquals(0, scn.GetForceDrainRemaining());

		scn.PassForceDrainEndResponses();
		assertFalse(scn.IsActiveForceDrain());
	}

	@Test
	public void LSCanForceDrainFor0AtControlledSiteWith0DSIcons() {
		var scn = GetScenario();

		var trooper = scn.GetLSFiller(1);

		var bluffs = scn.GetDSCard("bluffs");

		scn.StartGame();

		scn.MoveLocationToTable(bluffs);
		scn.MoveCardsToLocation(bluffs, trooper);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(scn.LSForceDrainAvailable(bluffs));
		assertFalse(scn.IsActiveForceDrain());

		assertEquals(0, scn.GetDSIconsOnLocation(bluffs));
		scn.LSForceDrainAt(bluffs);
		assertTrue(scn.IsActiveForceDrain());
		assertEquals(0, scn.GetForceDrainTotal());
		assertEquals(0, scn.GetForceDrainRemaining());
		assertEquals(0, scn.GetForceDrainPaidSoFar());

		scn.PassForceDrainStartResponses();
		assertFalse(scn.DSDecisionAvailable("Choose Force to lose"));
		scn.PassForceDrainEndResponses();

		assertFalse(scn.IsActiveForceDrain());
		assertEquals(9, scn.GetDSReserveDeckCount());
	}

	@Test
	public void LSCanForceDrainFor1AtControlledSiteWith1DSIcon() {
		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var trooper = scn.GetLSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, trooper);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(scn.LSForceDrainAvailable(site));
		assertFalse(scn.IsActiveForceDrain());

		assertEquals(1, scn.GetDSIconsOnLocation(site));
		scn.LSForceDrainAt(site);
		assertTrue(scn.IsActiveForceDrain());

		scn.PassForceDrainStartResponses();
		assertEquals(1, scn.GetForceDrainTotal());
		assertEquals(1, scn.GetForceDrainRemaining());
		assertEquals(0, scn.GetForceDrainPaidSoFar());

		assertTrue(scn.DSDecisionAvailable("Choose Force to lose"));
		assertEquals(10, scn.GetDSReserveDeckCount());
		assertEquals(0, scn.GetDSLostPileCount());

		scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
		assertEquals(0, scn.GetForceDrainRemaining());
		scn.PassForceDrainEndResponses();

		assertEquals(9, scn.GetDSReserveDeckCount());
		assertEquals(1, scn.GetDSLostPileCount());
		assertFalse(scn.IsActiveForceDrain());
	}

	@Test
	public void LSCanForceDrainFor2AtControlledSiteWith2DSIcons() {
		var scn = GetScenario();

		var trooper = scn.GetLSFiller(1);

		var plaza = scn.GetDSCard("plaza");

		scn.StartGame();

		scn.MoveLocationToTable(plaza);
		scn.MoveCardsToLocation(plaza, trooper);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(scn.LSForceDrainAvailable(plaza));
		assertFalse(scn.IsActiveForceDrain());

		assertEquals(2, scn.GetDSIconsOnLocation(plaza));
		scn.LSForceDrainAt(plaza);
		assertTrue(scn.IsActiveForceDrain());

		scn.PassForceDrainStartResponses();

		assertTrue(scn.DSDecisionAvailable("Choose Force to lose"));
		assertEquals(9, scn.GetDSReserveDeckCount());
		assertEquals(0, scn.GetLSLostPileCount());
		assertEquals(2, scn.GetForceDrainTotal());
		assertEquals(2, scn.GetForceDrainRemaining());
		assertEquals(0, scn.GetForceDrainPaidSoFar());

		scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
		scn.PassForceDrainPendingResponses();
		assertEquals(8, scn.GetDSReserveDeckCount());
		assertEquals(1, scn.GetDSLostPileCount());
		assertEquals(2, scn.GetForceDrainTotal());
		assertEquals(1, scn.GetForceDrainRemaining());
		assertEquals(1, scn.GetForceDrainPaidSoFar());
		assertTrue(scn.DSDecisionAvailable("Choose Force to lose"));

		scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
		assertEquals(7, scn.GetDSReserveDeckCount());
		assertEquals(2, scn.GetDSLostPileCount());
		assertEquals(0, scn.GetForceDrainRemaining());

		scn.PassForceDrainEndResponses();
		assertFalse(scn.IsActiveForceDrain());
	}

	@Test
	public void DSCannotInitiateForceDrainAtSiteLSOccupies() {
		var scn = GetScenario();

		var trooper = scn.GetLSFiller(1);
		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, stormtrooper, trooper);

		scn.SkipToPhase(Phase.CONTROL);

		assertTrue(scn.CardsAtLocation(site, stormtrooper));
		assertTrue(scn.CardsAtLocation(site, trooper));
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertFalse(scn.DSForceDrainAvailable(site));
	}

	@Test
	public void LSCannotInitiateForceDrainAtSiteDSOccupies() {
		var scn = GetScenario();

		var trooper = scn.GetLSFiller(1);
		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, stormtrooper, trooper);

		scn.SkipToLSTurn(Phase.CONTROL);

		assertTrue(scn.CardsAtLocation(site, stormtrooper));
		assertTrue(scn.CardsAtLocation(site, trooper));
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertFalse(scn.LSForceDrainAvailable(site));
	}

	@Test
	public void DSCannotInitiateForceDrainAtSiteWithCharacterWhoDrainedThatTurn() {
		var scn = GetScenario();

		var site1 = scn.GetLSStartingLocation();
		var site2 = scn.GetDSStartingLocation();

		var stormtrooper1 = scn.GetDSFiller(1);
		var stormtrooper2 = scn.GetDSFiller(2);
		var helrot = scn.GetDSCard("helrot");
		scn.MoveCardsToHand(helrot);

		scn.StartGame();

		scn.MoveCardsToLocation(site1, stormtrooper1);
		scn.MoveCardsToLocation(site2, stormtrooper2);

		scn.SkipToPhase(Phase.CONTROL);

		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.DSForceDrainAvailable(site1));
		assertTrue(scn.DSForceDrainAvailable(site2));

		scn.DSForceDrainAt(site1);
		scn.PassForceDrainStartResponses();
		scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
		scn.PassForceDrainEndResponses();

		scn.LSPass();
		assertTrue(scn.AwaitingDSControlPhaseActions());
		//Playing Elis Helrot to move the stormtrooper from site 1 to 2
		scn.PrepareDSDestiny(0);
		scn.DSPlayCard(helrot);
		scn.DSChooseCard(site1);
		scn.DSChooseCard(site2);
		scn.DSChooseCard(stormtrooper1);
		scn.PassDestinyDrawResponses();
		scn.DSChooseYes();
		scn.PassAllResponses();

		scn.LSPass();
		assertTrue(scn.AwaitingDSControlPhaseActions());
		// Since stormtrooper 1 already drained at site 1 this turn, he cannot drain at site 2
		assertFalse(scn.DSForceDrainAvailable(site2));
	}

	@Test
	public void LSCannotInitiateForceDrainAtSiteWithCharacterWhoDrainedThatTurn() {
		var scn = GetScenario();

		var site1 = scn.GetLSStartingLocation();
		var site2 = scn.GetDSStartingLocation();

		var trooper1 = scn.GetLSFiller(1);
		var trooper2 = scn.GetLSFiller(2);
		var nabrun = scn.GetLSCard("nabrun");
		scn.MoveCardsToHand(nabrun);

		scn.StartGame();

		scn.MoveCardsToLocation(site1, trooper1);
		scn.MoveCardsToLocation(site2, trooper2);

		scn.SkipToLSTurn(Phase.CONTROL);

		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(scn.LSForceDrainAvailable(site1));
		assertTrue(scn.LSForceDrainAvailable(site2));

		scn.LSForceDrainAt(site1);
		scn.PassForceDrainStartResponses();
		scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
		scn.PassForceDrainEndResponses();

		scn.DSPass();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		//Playing Nabrun Leids to move the trooper from site 1 to 2
		scn.PrepareLSDestiny(0);
		scn.LSPlayCard(nabrun);
		scn.LSChooseCard(site1);
		scn.LSChooseCard(site2);
		scn.LSChooseCard(trooper1);
		scn.PassDestinyDrawResponses();
		scn.LSChooseYes();
		scn.PassAllResponses();

		scn.DSPass();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		// Since trooper 1 already drained at site 1 this turn, he cannot drain at site 2
		assertFalse(scn.LSForceDrainAvailable(site2));
	}
}
