package com.gempukku.swccgo.rules.state;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CrashedTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("speeder", "104_003"); //rebel snowspeeder (enclosed vehicle)
					put("skiff", "6_088"); //(open vehicle)
					put("ronto", "7_155"); //(creature vehicle)
					put("pilot", "1_027"); //rebel pilot
					put("lars", "1_132"); //tatooine: lars' moisture farm
					put("cannon", "3_074"); //dual laser cannon
				}},
				new HashMap<>()
				{{
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

	//see AR: Vehicles - crashed (p92-93)
	//see AR: Starships (or Vehicles) - unpiloted (p91-92)

	@Test
	public void CrashedEnclosedVehicleCannotUseLandspeed() {
		var scn = GetScenario();

		var speeder = scn.GetLSCard("speeder");
		var lars = scn.GetLSCard("lars");

		scn.StartGame();

		scn.MoveLocationToTable(lars); //exterior location adjacent to DS starting location
		scn.MoveCardsToLocation(lars, speeder);

		scn.CrashCard(speeder);

		scn.SkipToLSTurn(Phase.MOVE);
		assertFalse(scn.LSCardActionAvailable(speeder, "landspeed"));
	}

	@Test
	public void CrashedOpenVehicleCannotUseLandspeed() {
		var scn = GetScenario();

		var skiff = scn.GetLSCard("skiff");
		var lars = scn.GetLSCard("lars");
		var pilot = scn.GetLSCard("pilot");

		scn.StartGame();

		scn.MoveLocationToTable(lars); //exterior location adjacent to DS starting location
		scn.MoveCardsToLocation(lars, skiff);

		scn.MoveCardsToLSHand(pilot);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(pilot);
		scn.LSChooseCard(skiff);
		scn.LSChoose("Driver");
		scn.PassAllResponses();

		scn.CrashCard(skiff);

		scn.SkipToPhase(Phase.MOVE);
		assertFalse(scn.LSCardActionAvailable(skiff, "landspeed"));
	}

	@Test
	public void CrashedCreatureVehicleCanUseLandspeed() {
		//creature vehicles are never unpiloted
		var scn = GetScenario();

		var ronto = scn.GetLSCard("ronto");
		var lars = scn.GetLSCard("lars");

		scn.StartGame();

		scn.MoveLocationToTable(lars); //exterior location adjacent to DS starting location
		scn.MoveCardsToLocation(lars, ronto);

		scn.CrashCard(ronto);

		scn.SkipToLSTurn(Phase.MOVE);
		assertFalse(scn.LSCardActionAvailable(ronto, "landspeed"));
	}

	@Test
	public void CrashedEnclosedVehicleRequires1ForceToEmbark() {
		var scn = GetScenario();

		var speeder = scn.GetLSCard("speeder");
		var site = scn.GetDSStartingLocation();
		var trooper = scn.GetLSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, speeder, trooper);

		scn.CrashCard(speeder);

		scn.SkipToLSTurn(Phase.MOVE);
		assertTrue(scn.LSCardActionAvailable(trooper, "Embark"));
		assertEquals(0, scn.GetLSUsedPileCount());
		scn.LSUseCardAction(trooper, "Embark");
		scn.PassAllResponses();
		assertTrue(scn.IsAboardAsPassenger(speeder, trooper));
		assertEquals(1, scn.GetLSUsedPileCount());
	}

	@Test
	public void CrashedOpenVehicleEmbarkIsFree() {
		var scn = GetScenario();

		var skiff = scn.GetLSCard("skiff");
		var site = scn.GetDSStartingLocation();
		var trooper = scn.GetLSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, skiff, trooper);

		scn.CrashCard(skiff);

		scn.SkipToLSTurn(Phase.MOVE);
		assertTrue(scn.LSCardActionAvailable(trooper, "Embark"));
		assertEquals(0, scn.GetLSUsedPileCount());
		scn.LSUseCardAction(trooper, "Embark");
		scn.LSChoose("Passenger");
		scn.PassAllResponses();
		assertTrue(scn.IsAboardAsPassenger(skiff, trooper));
		assertEquals(0, scn.GetLSUsedPileCount());
	}

	@Test
	public void CrashedEnclosedVehicleRequires1ForceToDisembark() {
		var scn = GetScenario();

		var speeder = scn.GetLSCard("speeder");
		var site = scn.GetDSStartingLocation();
		var trooper = scn.GetLSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, speeder, trooper);
		scn.MoveCardsToLSHand(trooper);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(trooper);
		scn.LSChooseCard(speeder);
		scn.PassAllResponses();
		assertTrue(scn.IsAboardAsPassenger(speeder, trooper));

		scn.CrashCard(speeder);

		scn.SkipToPhase(Phase.MOVE);
		assertTrue(scn.LSCardActionAvailable(trooper, "Disembark"));
		assertEquals(1, scn.GetLSUsedPileCount());
		scn.LSUseCardAction(trooper, "Disembark");
		scn.PassAllResponses();
		assertFalse(scn.IsAboardAsPassenger(speeder, trooper));
		assertEquals(2, scn.GetLSUsedPileCount());
	}

	@Test
	public void CrashedOpenVehicleDisembarkIsFree() {
		var scn = GetScenario();

		var skiff = scn.GetLSCard("skiff");
		var site = scn.GetDSStartingLocation();
		var trooper = scn.GetLSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, skiff, trooper);
		scn.MoveCardsToLSHand(trooper);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(trooper);
		scn.LSChooseCard(skiff);
		scn.LSChoose("Passenger");
		scn.PassAllResponses();
		assertTrue(scn.IsAboardAsPassenger(skiff, trooper));

		scn.CrashCard(skiff);

		scn.SkipToPhase(Phase.MOVE);
		assertTrue(scn.LSCardActionAvailable(trooper, "Disembark"));
		assertEquals(1, scn.GetLSUsedPileCount());
		scn.LSUseCardAction(trooper, "Disembark");
		scn.PassAllResponses();
		assertFalse(scn.IsAboardAsPassenger(skiff, trooper));
		assertEquals(1, scn.GetLSUsedPileCount());
	}

	@Test
	public void CrashedEnclosedVehiclePassengerCapacityEnforced() {
		var scn = GetScenario();

		var speeder = scn.GetLSCard("speeder");
		var site = scn.GetDSStartingLocation();
		var trooper = scn.GetLSFiller(1);
		var trooper2 = scn.GetLSFiller(2);

		scn.StartGame();

		scn.MoveCardsToLocation(site, speeder);
		scn.MoveCardsToLSHand(trooper, trooper2);

		scn.CrashCard(speeder);

		assertEquals(1, scn.GetPassengerCapacity(speeder));

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(trooper);
		assertTrue(scn.LSHasCardChoiceAvailable(site));
		assertTrue(scn.LSHasCardChoiceAvailable(speeder));
		scn.LSChooseCard(speeder);
		scn.PassAllResponses();
		assertTrue(scn.IsAboardAsPassenger(speeder, trooper));

		assertEquals(0, scn.GetPassengerCapacity(speeder)); //passenger slot occupied

		scn.DSPass();
		scn.LSDeployCard(trooper2);
		assertTrue(scn.LSHasCardChoiceAvailable(site));
		assertFalse(scn.LSHasCardChoiceAvailable(speeder));
	}

	@Test
	public void CrashedEnclosedVehiclePermanentAboardProvidesPresence_ForceDrain() {
		//demonstrates fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/977
		var scn = GetScenario();

		var speeder = scn.GetLSCard("speeder");
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, speeder);

		scn.CrashCard(speeder);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSCardActionAvailable(site, "Force drain"));
	}

	@Test
	public void CrashedEnclosedVehiclePermanentAboardProvidesPresence_Battle() {
		//demonstrates fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/977
		var scn = GetScenario();

		var speeder = scn.GetLSCard("speeder");

		var stormtrooper = scn.GetDSFiller(1);
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, speeder, stormtrooper);

		scn.CrashCard(speeder);

		scn.SkipToPhase(Phase.BATTLE);
		assertTrue(scn.DSCardActionAvailable(site, "Initiate battle"));
	}

	@Test
	public void CrashedEnclosedVehiclePermanentAboardAbilityDoesNotCountTowardBattleDestiny() {
		//put 3 LS ability + crashed speeder with an ability 1 perm pilot and confirm battle destiny could not be drawn
		var scn = GetScenario();

		var speeder = scn.GetLSCard("speeder");
		var trooper1 = scn.GetLSFiller(1);
		var trooper2 = scn.GetLSFiller(2);
		var trooper3 = scn.GetLSFiller(3);

		var stormtrooper = scn.GetDSFiller(1);
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, speeder, stormtrooper, trooper1, trooper2, trooper3);

		assertEquals(4, scn.GetLSAbilityAtLocation(site));
		scn.CrashCard(speeder);
		assertEquals(4, scn.GetLSAbilityAtLocation(site));

		scn.SkipToPhase(Phase.BATTLE);
		assertTrue(scn.DSCardActionAvailable(site, "Initiate battle"));
		scn.DSInitiateBattle(site);
		scn.SkipToEndOfPowerSegment(true); //draw destiny if option provided
		assertEquals(0, scn.GetLSUsedPileCount());
	}

	@Test
	public void CrashedOpenVehicleDriverAbilityCountsTowardBattleDestiny() {
		//put 2 LS ability + crashed skiff with an ability 2 driver and confirm battle destiny could be drawn
		var scn = GetScenario();

		var skiff = scn.GetLSCard("skiff");
		var trooper1 = scn.GetLSFiller(1);
		var trooper2 = scn.GetLSFiller(2);
		var pilot = scn.GetLSCard("pilot");

		var stormtrooper = scn.GetDSFiller(1);
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, skiff, stormtrooper, trooper1, trooper2);
		scn.MoveCardsToLSHand(pilot);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(pilot);
		scn.LSChooseCard(skiff);
		scn.LSChoose("Driver");
		scn.PassAllResponses();

		assertEquals(4, scn.GetLSAbilityAtLocation(site));
		scn.CrashCard(skiff);
		assertEquals(4, scn.GetLSAbilityAtLocation(site));

		scn.SkipToPhase(Phase.BATTLE);
		assertTrue(scn.LSCardActionAvailable(site, "Initiate battle"));
		scn.LSInitiateBattle(site);
		assertEquals(3, scn.GetLSUsedPileCount());
		scn.SkipToEndOfPowerSegment(true); //draw destiny if option provided
		assertEquals(4, scn.GetLSUsedPileCount());
	}

	@Test
	public void CrashedOpenVehiclePassengerAbilityCountsTowardBattleDestiny() {
		//put 2 LS ability + crashed skiff with an ability 2 passenger and confirm battle destiny could be drawn
		var scn = GetScenario();

		var skiff = scn.GetLSCard("skiff");
		var trooper1 = scn.GetLSFiller(1);
		var trooper2 = scn.GetLSFiller(2);
		var pilot = scn.GetLSCard("pilot");

		var stormtrooper = scn.GetDSFiller(1);
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, skiff, stormtrooper, trooper1, trooper2);
		scn.MoveCardsToLSHand(pilot);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(pilot);
		scn.LSChooseCard(skiff);
		scn.LSChoose("Passenger");
		scn.PassAllResponses();

		assertEquals(4, scn.GetLSAbilityAtLocation(site));
		scn.CrashCard(skiff);
		assertEquals(4, scn.GetLSAbilityAtLocation(site));

		scn.SkipToPhase(Phase.BATTLE);
		assertTrue(scn.LSCardActionAvailable(site, "Initiate battle"));
		scn.LSInitiateBattle(site);
		assertEquals(3, scn.GetLSUsedPileCount());
		scn.SkipToEndOfPowerSegment(true); //draw destiny if option provided
		assertEquals(4, scn.GetLSUsedPileCount());
	}

	@Test
	public void CrashedEnclosedVehiclePermanentAboardPower0() {
		//put trooper + crashed speeder with an ability 1 perm pilot vs stormtrooper and confirm battle tie (power 1 vs power 1)
		var scn = GetScenario();

		var speeder = scn.GetLSCard("speeder");
		var trooper1= scn.GetLSFiller(1);

		var stormtrooper = scn.GetDSFiller(1);
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, speeder, stormtrooper, trooper1);

		scn.CrashCard(speeder);

		scn.SkipToPhase(Phase.BATTLE);
		assertTrue(scn.DSCardActionAvailable(site, "Initiate battle"));
		scn.DSInitiateBattle(site);
		scn.PassAllResponses();
		assertEquals(1, scn.GetLSTotalPower()); //power 1 trooper + power 0 speeder
		scn.SkipToDamageSegment(false);
		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSBattlePhaseActions()); //no force loss, must have been a tie
	}

	@Test
	public void CrashedOpenVehicleDriverAboardPower0() {
		//put trooper + crashed skiff with driver vs stormtrooper and confirm battle tie (power 1 vs power 1)
		var scn = GetScenario();

		var skiff = scn.GetLSCard("skiff");
		var trooper1= scn.GetLSFiller(1);
		var pilot = scn.GetLSCard("pilot");

		var stormtrooper = scn.GetDSFiller(1);
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, skiff, stormtrooper, trooper1);
		scn.MoveCardsToLSHand(pilot);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(pilot);
		scn.LSChooseCard(skiff);
		scn.LSChoose("Driver");
		scn.PassAllResponses();
		assertTrue(scn.IsAboardAsPilot(skiff, pilot));

		scn.CrashCard(skiff);

		scn.SkipToPhase(Phase.BATTLE);
		assertTrue(scn.LSCardActionAvailable(site, "Initiate battle"));
		scn.LSInitiateBattle(site);
		scn.PassAllResponses();
		assertEquals(1, scn.GetLSTotalPower()); //power 1 trooper + power 0 skiff
		scn.SkipToDamageSegment(false);
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSBattlePhaseActions()); //no force loss, must have been a tie
	}

	@Test
	public void CrashedEnclosedVehiclePermanentAboardCannotFireWeapon() {
		var scn = GetScenario();

		var speeder = scn.GetLSCard("speeder");
		var trooper = scn.GetLSFiller(1);
		var cannon = scn.GetLSCard("cannon");

		var stormtrooper = scn.GetDSFiller(1);
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, speeder, stormtrooper, trooper);
		scn.AttachCardsTo(speeder, cannon);

		scn.LSActivateForceCheat(1); //enough to fire

		scn.CrashCard(speeder);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.PassAllResponses();
		scn.DSPass();
		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
		assertFalse(scn.LSCardActionAvailable(cannon, "Fire"));
	}

	///other tests to add:
	//card orientation:
		// crashed card is inverted
		// crashed + hit is ? (sideways?)
		// hit + crashed is ? (sideways?)

	//movement
		//cannot use sector movement (crashed cloud car?)
		//cannot use docking bay transit?

	//gametext suspended (ideas?)
		//forfeit bonus on sandcrawler?
		//attrition immunity on Rogue X

	//gametext not suspended (ideas?)
		//deploy reduction on Sail Barge?
			//suspect isGameTextCanceled is preventing the deploy reduction from working correctly?

}
