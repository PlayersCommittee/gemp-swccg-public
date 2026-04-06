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
                    put("ponda", "1_190"); //power 2, ability 1
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

}
