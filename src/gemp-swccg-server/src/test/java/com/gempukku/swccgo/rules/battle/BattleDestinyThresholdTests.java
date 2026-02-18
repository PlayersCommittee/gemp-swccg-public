package com.gempukku.swccgo.rules.battle;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BattleDestinyThresholdTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("doikk", "2_7"); //Doikk Na'ts (Used for increasing standard threshold or increasing hard ability requirements)
					put("kitonak", "6_23"); //Kitonak (Used to make Doikk work)
					put("lando", "13_27"); //Lando, Scoundrel (Used for adding battle destiny)
					put("chewie", "109_1"); //Chewie With Blaster Rifle (Used for adding battle destiny)
					put("c3p0", "1_5"); //C-3PO (Used to make Chewie work)
					put("falcon", "1_143"); //Millennium Falcon
					put("xwing1","1_146");
					put("xwing2","1_146");
					put("xwing3","1_146");
					put("xwing4","1_146");
					put("xwing5","1_146");
					put("xwing6","1_146");
					put("yavin","1_135"); //a system
				}},
				new HashMap<>()
				{{
					put("xizor", "10_45"); //Prince Xizor (Used for setting hard 6 ability requirement)
					put("vaderShuttle", "7_309"); //Vader's Personal Shuttle
					put("vader", "1_168"); //Darth Vader
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

	/// GROUP 1: SIMPLE ABILITY TESTS
	@Test
	public void Basic4AbilityThresholdNotMet() {
		//Ability required to draw destiny: 4
		//LS ability: 3
		//Result: 0 destiny
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var rebelTrooper3 = scn.GetLSFiller(3);

		var trooper1 = scn.GetDSFiller(1);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, rebelTrooper3, trooper1);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(0,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void Basic4AbilityThresholdSuccessfullyMet() {
		//Ability required to draw destiny: 4
		//LS ability: 4
		//Result: 1 destiny
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var rebelTrooper3 = scn.GetLSFiller(3);
		var rebelTrooper4 = scn.GetLSFiller(4);

		var trooper1 = scn.GetDSFiller(1);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, rebelTrooper3, rebelTrooper4, trooper1);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(1,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void IncreasedThresholdTo5NotMet() {
		//Ability required to draw destiny: 5
		//LS ability: 4
		//Result: 0 destiny
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var doikk = scn.GetLSCard("doikk");
		var kitonak = scn.GetLSCard("kitonak");

		var trooper1 = scn.GetDSFiller(1);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, doikk, kitonak, trooper1);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(0,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void IncreasedThresholdTo5SuccessfullyMet() {
		//Ability required to draw destiny: 5
		//LS ability: 5
		//Result: 1 destiny
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var rebelTrooper3 = scn.GetLSFiller(3);
		var doikk = scn.GetLSCard("doikk");
		var kitonak = scn.GetLSCard("kitonak");

		var trooper1 = scn.GetDSFiller(1);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, rebelTrooper3, doikk, kitonak, trooper1);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(1,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void Special6AbilityRequirementNotMet() {
		//Ability required to draw destiny: 6
		//LS ability: 5
		//Result: 0 destiny
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var rebelTrooper3 = scn.GetLSFiller(3);
		var rebelTrooper4 = scn.GetLSFiller(4);
		var rebelTrooper5 = scn.GetLSFiller(5);

		var xizor = scn.GetDSCard("xizor");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, rebelTrooper3, rebelTrooper4, rebelTrooper5, xizor);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(0,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void Special6AbilityRequirementSuccessfullyMet() {
		//Ability required to draw destiny: 6
		//LS ability: 6
		//Result: 1 destiny
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var rebelTrooper3 = scn.GetLSFiller(3);
		var rebelTrooper4 = scn.GetLSFiller(4);
		var rebelTrooper5 = scn.GetLSFiller(5);
		var rebelTrooper6 = scn.GetLSFiller(6);

		var xizor = scn.GetDSCard("xizor");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, rebelTrooper3, rebelTrooper4, rebelTrooper5, rebelTrooper6, xizor);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(1,scn.GetLSBattleDestinyCount());
	}

	@Test @Ignore
	public void IncreasedSpecialRequirementTo7NotMet() {
		//Ability required to draw destiny: 7
		//LS ability: 6
		//Result: 0 destiny
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var rebelTrooper3 = scn.GetLSFiller(3);
		var rebelTrooper4 = scn.GetLSFiller(4);
		var doikk = scn.GetLSCard("doikk");
		var kitonak = scn.GetLSCard("kitonak");

		var xizor = scn.GetDSCard("xizor");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, rebelTrooper3, rebelTrooper4, doikk, kitonak, xizor);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(0,scn.GetLSBattleDestinyCount()); ///FAILS - Actual: 1
	}

	@Test
	public void IncreasedSpecialRequirementTo7SuccessfullyMet() {
		//Ability required to draw destiny: 7
		//LS ability: 7
		//Result: 1 destiny
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var rebelTrooper3 = scn.GetLSFiller(3);
		var rebelTrooper4 = scn.GetLSFiller(4);
		var rebelTrooper5 = scn.GetLSFiller(5);
		var doikk = scn.GetLSCard("doikk");
		var kitonak = scn.GetLSCard("kitonak");

		var xizor = scn.GetDSCard("xizor");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, rebelTrooper3, rebelTrooper4, rebelTrooper5, doikk, kitonak, xizor);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(1,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void SpecialGreaterThan5AbilityRequirementNotMet() {
		//Ability required to draw destiny: > 5
		//LS ability: 5
		//Result: 0 destiny
		var scn = GetScenario();

		var xwing1 = scn.GetLSCard("xwing1");
		var xwing2 = scn.GetLSCard("xwing2");
		var xwing3 = scn.GetLSCard("xwing3");
		var xwing4 = scn.GetLSCard("xwing4");
		var xwing5 = scn.GetLSCard("xwing5");

		var vaderShuttle = scn.GetDSCard("vaderShuttle");
		var vader = scn.GetDSCard("vader");

		var yavin = scn.GetLSCard("yavin");

		scn.StartGame();

		scn.MoveLocationToTable(yavin);
		scn.MoveCardsToLocation(yavin, xwing1, xwing2, xwing3, xwing4, xwing5, vaderShuttle);

		scn.BoardAsPassenger(vaderShuttle,vader);
		assertTrue(scn.IsAboardAsPassenger(vaderShuttle,vader));

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(yavin);

		assertEquals(0,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void SpecialGreaterThan5AbilityRequirementSuccessfullyMet() {
		//Ability required to draw destiny: > 5
		//LS ability: 6
		//Result: 1 destiny
		var scn = GetScenario();

		var xwing1 = scn.GetLSCard("xwing1");
		var xwing2 = scn.GetLSCard("xwing2");
		var xwing3 = scn.GetLSCard("xwing3");
		var xwing4 = scn.GetLSCard("xwing4");
		var xwing5 = scn.GetLSCard("xwing5");
		var xwing6 = scn.GetLSCard("xwing6");

		var vaderShuttle = scn.GetDSCard("vaderShuttle");
		var vader = scn.GetDSCard("vader");

		var yavin = scn.GetLSCard("yavin");

		scn.StartGame();

		scn.MoveLocationToTable(yavin);
		scn.MoveCardsToLocation(yavin, xwing1, xwing2, xwing3, xwing4, xwing5, xwing6, vaderShuttle);

		scn.BoardAsPassenger(vaderShuttle,vader);
		assertTrue(scn.IsAboardAsPassenger(vaderShuttle,vader));

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(yavin);

		assertEquals(1,scn.GetLSBattleDestinyCount());
	}

	/// GROUP 2: DESTINY ADDER TESTS

	@Test
	public void AddDestinyWithBasic4AbilityThresholdNotMet() {
		//Ability required to draw destiny: 4
		//LS ability: 3
		//Result: 0 destiny + 1 added destiny
		var scn = GetScenario();

		var lando = scn.GetLSCard("lando");

		var trooper = scn.GetDSFiller(1);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, lando, trooper);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(1,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void AddDestinyWithBasic4AbilityThresholdSuccessfullyMet() {
		//Ability required to draw destiny: 4
		//LS ability: 4
		//Result: 1 destiny + 1 added destiny
		var scn = GetScenario();

		var lando = scn.GetLSCard("lando");
		var rebelTrooper1 = scn.GetLSFiller(1);

		var trooper = scn.GetDSFiller(1);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, lando, rebelTrooper1, trooper);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(2,scn.GetLSBattleDestinyCount());
	}

	@Test @Ignore
	public void AddDestinyWithIncreasedThresholdTo5NotMet() {
		//Ability required to draw destiny: 5
		//LS ability: 4
		//Result: 0 destiny + 1 added destiny
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var c3p0 = scn.GetLSCard("c3p0");
		var doikk = scn.GetLSCard("doikk");
		var kitonak = scn.GetLSCard("kitonak");

		var trooper = scn.GetDSFiller(1);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, chewie, c3p0, doikk, kitonak, trooper);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(1,scn.GetLSBattleDestinyCount()); ///FAILS - Actual: 0
	}

	@Test
	public void AddDestinyWithIncreasedThresholdTo5SuccessfullyMet() {
		//Ability required to draw destiny: 5
		//LS ability: 5
		//Result: 1 destiny + 1 added destiny
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var c3p0 = scn.GetLSCard("c3p0");
		var doikk = scn.GetLSCard("doikk");
		var kitonak = scn.GetLSCard("kitonak");
		var rebelTrooper1 = scn.GetLSFiller(1);

		var trooper = scn.GetDSFiller(1);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, chewie, c3p0, doikk, kitonak, rebelTrooper1, trooper);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(2,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void AddDestinyWithSpecial6AbilityRequirementNotMet() {
		//Ability required to draw destiny: 6
		//LS ability: 5
		//Result: 0 destiny + 0 added destiny
		var scn = GetScenario();

		var lando = scn.GetLSCard("lando");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);

		var xizor = scn.GetDSCard("xizor");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, lando, rebelTrooper1, rebelTrooper2, xizor);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(0,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void AddDestinyWithSpecial6AbilityRequirementSuccessfullyMet() {
		//Ability required to draw destiny: 6
		//LS ability: 6
		//Result: 1 destiny + 1 added destiny
		var scn = GetScenario();

		var lando = scn.GetLSCard("lando");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var rebelTrooper3 = scn.GetLSFiller(3);

		var xizor = scn.GetDSCard("xizor");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, lando, rebelTrooper1, rebelTrooper2, rebelTrooper3, xizor);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(2,scn.GetLSBattleDestinyCount());
	}

	@Test @Ignore
	public void AddDestinyWithIncreasedSpecialRequirementTo7NotMet() {
		//Ability required to draw destiny: 7
		//LS ability: 6
		//Result: 0 destiny + 0 added destiny
		var scn = GetScenario();

		var lando = scn.GetLSCard("lando");
		var doikk = scn.GetLSCard("doikk");
		var kitonak = scn.GetLSCard("kitonak");
		var rebelTrooper1 = scn.GetLSFiller(1);

		var xizor = scn.GetDSCard("xizor");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, lando, doikk, kitonak, rebelTrooper1, xizor);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(0,scn.GetLSBattleDestinyCount()); ///FAILS - Actual: 2
	}

	@Test
	public void AddDestinyWithIncreasedSpecialRequirementTo7SuccessfullyMet() {
		//Ability required to draw destiny: 7
		//LS ability: 7
		//Result: 1 destiny + 1 added destiny
		var scn = GetScenario();

		var lando = scn.GetLSCard("lando");
		var doikk = scn.GetLSCard("doikk");
		var kitonak = scn.GetLSCard("kitonak");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);

		var xizor = scn.GetDSCard("xizor");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, lando, doikk, kitonak, rebelTrooper1, rebelTrooper2, xizor);
		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertEquals(2,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void AddDestinyWithSpecialGreaterThan5AbilityRequirementNotMet() {
		//Ability required to draw destiny: > 5
		//LS ability: 5
		//Result: 0 destiny + 0 added destiny
		var scn = GetScenario();

		var falcon = scn.GetLSCard("falcon");
		var lando = scn.GetLSCard("lando");
		var xwing1 = scn.GetLSCard("xwing1");
		var xwing2 = scn.GetLSCard("xwing2");

		var vaderShuttle = scn.GetDSCard("vaderShuttle");
		var vader = scn.GetDSCard("vader");

		var yavin = scn.GetLSCard("yavin");

		scn.StartGame();

		scn.MoveLocationToTable(yavin);
		scn.MoveCardsToLocation(yavin, falcon, xwing1, xwing2, vaderShuttle);

		scn.BoardAsPilot(falcon,lando);
		scn.BoardAsPassenger(vaderShuttle,vader);
		assertTrue(scn.IsAboardAsPilot(falcon,lando));
		assertTrue(scn.IsAboardAsPassenger(vaderShuttle,vader));

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(yavin);

		assertEquals(0,scn.GetLSBattleDestinyCount());
	}

	@Test
	public void AddDestinyWithSpecialGreaterThan5AbilityRequirementSuccessfullyMet() {
		//Ability required to draw destiny: > 5
		//LS ability: 6
		//Result: 1 destiny + 1 added destiny
		var scn = GetScenario();

		var falcon = scn.GetLSCard("falcon");
		var lando = scn.GetLSCard("lando");
		var xwing1 = scn.GetLSCard("xwing1");
		var xwing2 = scn.GetLSCard("xwing2");
		var xwing3 = scn.GetLSCard("xwing3");

		var vaderShuttle = scn.GetDSCard("vaderShuttle");
		var vader = scn.GetDSCard("vader");

		var yavin = scn.GetLSCard("yavin");

		scn.StartGame();

		scn.MoveLocationToTable(yavin);
		scn.MoveCardsToLocation(yavin, falcon, xwing1, xwing2, xwing3, vaderShuttle);

		scn.BoardAsPilot(falcon,lando);
		scn.BoardAsPassenger(vaderShuttle,vader);
		assertTrue(scn.IsAboardAsPilot(falcon,lando));
		assertTrue(scn.IsAboardAsPassenger(vaderShuttle,vader));

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(yavin);

		assertEquals(2,scn.GetLSBattleDestinyCount());
	}
}
