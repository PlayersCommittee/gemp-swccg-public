package com.gempukku.swccgo.rules.attack;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NonCreatureAttackOnCreatureTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                }},
				new HashMap<>()
				{{
                    put("mynock", "4_110");
                    //put("mynock2", "4_110");
                    put("myo", "1_189"); //power 3, ability 1
                    put("ponda", "1_190"); //power 2, ability 1, pilot
                    put("cloudcar","5_178"); //cloud car - enclosed vehicle
                    put("kessel","1_288");
                    put("tiescout1","1_305"); //tie scout - starship
                    put("tiescout2","1_305");
                    put("tiescout3","1_305");
                    put("tiescout4","1_305");
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
	public void InitiateAttackTimingTest() {
        //Test1: self (creature owner) cannot attack during their turn in non-battle phase
        //Test2: self (creature owner) can attack during battle phase
        //Test3: opponent cannot attack during self battle phase
        //Test4: opponent cannot attack during their turn in non-battle phase
        //Test5: opponent can attack during battle phase
        //Test6: self cannot attack during opponent's battle phase

        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1);

        var mynock = scn.GetDSCard("mynock");
        var marketplace = scn.GetDSStartingLocation();
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveCardsToLocation(marketplace, mynock, stormtrooper, rebeltrooper);

        scn.DSActivateForceCheat(1); //both players with enough to initate attack (1 force)
        scn.LSActivateForceCheat(1);

        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(marketplace, "attack")); //test1

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCardActionAvailable(marketplace, "battle"));
        assertTrue(scn.DSCardActionAvailable(marketplace, "attack")); //test2

        scn.DSPass();
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertFalse(scn.LSCardActionAvailable(marketplace, "attack")); //test3

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(marketplace, "attack")); //test4

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.LSCardActionAvailable(marketplace, "battle"));
        assertTrue(scn.LSCardActionAvailable(marketplace, "attack")); //test5

        scn.LSPass();
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        assertFalse(scn.DSCardActionAvailable(marketplace, "attack")); //test6

    }

    @Test
    public void AttackTotalLessThanOrEqualToDefenseValueTest() {
        //Test1: attack total (5) equal to defense total (5) does not defeat creature
        var scn = GetScenario();

        var mynock = scn.GetDSCard("mynock");
        var marketplace = scn.GetDSStartingLocation();
        var myo = scn.GetDSCard("myo");
        var ponda = scn.GetDSCard("ponda");

        scn.StartGame();

        scn.MoveCardsToLocation(marketplace, mynock, myo, ponda);

        assertEquals(3,mynock.getBlueprint().getSpecialDefenseValue(), scn.epsilon);
        assertEquals(2,mynock.getBlueprint().getFerocity(), scn.epsilon);
        //defender total should be 3 + 2 = 5

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(marketplace,"attack");
        //scn.DSChooseCard(mynock); //only one target available, auto selected
        //attacker total (DS) should be 5
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertTrue(scn.CardsAtLocation(marketplace, mynock)); //Test1
    }

    @Test
    public void AttackTotalGreaterThanDefenseValueTest() {
        //Test1: attack total (6) greater than defense total (5) defeats creature
        //Test2: defeated creature goes to owner's lost pile
        var scn = GetScenario();

        var mynock = scn.GetDSCard("mynock");
        var marketplace = scn.GetDSStartingLocation();
        var myo = scn.GetDSCard("myo");
        var ponda = scn.GetDSCard("ponda");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveCardsToLocation(marketplace, mynock, myo, ponda, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(marketplace,"attack");
        //attacker total (DS) should be 6
        //defender total should be 5

        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertFalse(scn.CardsAtLocation(marketplace, mynock)); //Test1
        assertEquals(1,scn.GetDSLostPileCount()); //Test2
    }

    @Test
    public void AttackerMayDrawDestinyWithAbility4Test() {
        //Test1: ability of 4 allows attacker to drawing destiny to add to attack total
        //Test2: attack destiny (2) added to power (4) results in attack total (6) greater than defense total (5) and defeats creature
        var scn = GetScenario();

        var mynock = scn.GetDSCard("mynock");
        var marketplace = scn.GetDSStartingLocation();
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);
        var trooper4 = scn.GetDSFiller(4);

        scn.StartGame();

        scn.MoveCardsToLocation(marketplace, mynock, trooper1, trooper2, trooper3, trooper4);

        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(2);
        scn.DSUseCardAction(marketplace,"attack");
        //attacker total (DS) should be 4
        //defender total should be 5

        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();

        assertTrue(scn.DSDecisionAvailable("draw 1 destiny?")); //test1
        scn.DSChooseYes();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertFalse(scn.CardsAtLocation(marketplace, mynock)); //Test2
        assertEquals(1,scn.GetDSLostPileCount()); //Test2
    }

    @Test
    public void AttackDestinyAbilityCheckIncludesEnclosedVehiclePermPilotTest() {
        //Test1: enclosed vehicle permanent pilot ability counts toward meeting 4 ability threashold to draw attack destiny
        var scn = GetScenario();

        var mynock = scn.GetDSCard("mynock");
        var marketplace = scn.GetDSStartingLocation();
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);
        var cloudcar = scn.GetDSCard("cloudcar");

        scn.StartGame();

        scn.MoveCardsToLocation(marketplace, mynock, trooper1, trooper2, trooper3, cloudcar);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(marketplace,"attack");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();

        assertTrue(scn.DSDecisionAvailable("draw 1 destiny?")); //test1
    }

    @Test
    public void AttackDestinyAbilityCheckIncludesEnclosedVehiclePilotTest() {
        //Test1: pilot character's ability in enclosed vehicle counts toward meeting 4 ability threashold to draw attack destiny
        //ability = 1 (trooper) + 1 (trooper) + 1 (cloud car perm pilot) + 1 (ponda boba pilot in cloud car)

        var scn = GetScenario();

        var mynock = scn.GetDSCard("mynock");
        var marketplace = scn.GetDSStartingLocation();
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var ponda = scn.GetDSCard("ponda");
        var cloudcar = scn.GetDSCard("cloudcar");

        scn.StartGame();

        scn.MoveCardsToDSHand(ponda);

        scn.MoveCardsToLocation(marketplace, mynock, trooper1, trooper2, cloudcar);

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(ponda);
        scn.DSChooseCard(cloudcar);
        scn.DSChoose("Pilot");
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(cloudcar, ponda));

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(marketplace,"attack");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();

        assertTrue(scn.DSDecisionAvailable("draw 1 destiny?")); //test1
    }

    @Test
    public void AttackDestinyAbilityCheckExcludesEnclosedVehiclePassengerTest() {
        //Test1: passenger character's ability in enclosed vehicles does not count toward meeting 4 ability threashold to draw attack destiny
        //ability = 1 (trooper) + 1 (trooper) + 1 (cloud car perm pilot) + 0 (ponda boba passenger in cloud car)

        var scn = GetScenario();

        var mynock = scn.GetDSCard("mynock");
        var marketplace = scn.GetDSStartingLocation();
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var ponda = scn.GetDSCard("ponda");
        var cloudcar = scn.GetDSCard("cloudcar");

        scn.StartGame();

        scn.MoveCardsToDSHand(ponda);

        scn.MoveCardsToLocation(marketplace, mynock, trooper1, trooper2, cloudcar);

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(ponda);
        scn.DSChooseCard(cloudcar);
        scn.DSChoose("Passenger");
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(cloudcar, ponda));

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(marketplace,"attack");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();

        assertFalse(scn.DSDecisionAvailable("draw 1 destiny?")); //test1
    }

    @Test
    public void AttackDestinyAbilityCheckIncludesStarshipPermPilotTest() {
        //Test1: starship permanent pilot ability counts toward meeting 4 ability threashold to draw attack destiny
        var scn = GetScenario();

        var mynock = scn.GetDSCard("mynock");
        var kessel = scn.GetDSCard("kessel");
        var tiescout1 = scn.GetDSCard("tiescout1");
        var tiescout2 = scn.GetDSCard("tiescout2");
        var tiescout3 = scn.GetDSCard("tiescout3");
        var tiescout4 = scn.GetDSCard("tiescout4");

        scn.StartGame();

        scn.MoveLocationToTable(kessel);

        scn.MoveCardsToLocation(kessel, mynock, tiescout1, tiescout2, tiescout3, tiescout4);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(kessel,"attack");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();

        assertTrue(scn.DSDecisionAvailable("draw 1 destiny?")); //test1
    }

    @Test
    public void AttackDestinyAbilityCheckIncludesStarshipPilotTest() {
        //Test1: pilot character's ability in starship counts toward meeting 4 ability threashold to draw attack destiny
        //ability = 1 (tie perm pilot) + 1 (tie perm pilot) + 1 (tie perm pilot) + 1 (ponda boba pilot in tie)

        var scn = GetScenario();

        var mynock = scn.GetDSCard("mynock");
        var kessel = scn.GetDSCard("kessel");
        var ponda = scn.GetDSCard("ponda");
        var tiescout1 = scn.GetDSCard("tiescout1");
        var tiescout2 = scn.GetDSCard("tiescout2");
        var tiescout3 = scn.GetDSCard("tiescout3");

        scn.StartGame();

        scn.MoveLocationToTable(kessel);

        scn.MoveCardsToDSHand(ponda);

        scn.MoveCardsToLocation(kessel, mynock, tiescout1, tiescout2, tiescout3);

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(ponda);
        scn.DSChooseCard(tiescout1);
        scn.DSChoose("Pilot");
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(tiescout1, ponda));

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(kessel,"attack");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();

        assertTrue(scn.DSDecisionAvailable("draw 1 destiny?")); //test1
    }

    @Test
    public void AttackDestinyAbilityCheckExcludesStarshipPassengerTest() {
        //Test1: passenger character's ability in starship does not count toward meeting 4 ability threashold to draw attack destiny
        //ability = 1 (tie perm pilot) + 1 (tie perm pilot) + 1 (tie perm pilot) + 0 (ponda boba passenger in tie)

        var scn = GetScenario();

        var mynock = scn.GetDSCard("mynock");
        var kessel = scn.GetDSCard("kessel");
        var ponda = scn.GetDSCard("ponda");
        var tiescout1 = scn.GetDSCard("tiescout1");
        var tiescout2 = scn.GetDSCard("tiescout2");
        var tiescout3 = scn.GetDSCard("tiescout3");

        scn.StartGame();

        scn.MoveLocationToTable(kessel);

        scn.MoveCardsToDSHand(ponda);

        scn.MoveCardsToLocation(kessel, mynock, tiescout1, tiescout2, tiescout3);

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(ponda);
        scn.DSChooseCard(tiescout1);
        scn.DSChoose("Passenger");
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(tiescout1, ponda));

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(kessel,"attack");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();

        assertFalse(scn.DSDecisionAvailable("draw 1 destiny?")); //test1
    }

    //other tests:
    //can target one of multiple creatures
    //can target creatures owned by either player
    //can attack multiple times per turn
    //cards that participated in an attack cannot participate in second attack
    //cards that participated in battle can participate in attack
    //cards that participated in attack can participate in battle
}
