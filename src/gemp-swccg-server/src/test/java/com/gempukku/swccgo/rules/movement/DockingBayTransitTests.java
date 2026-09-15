package com.gempukku.swccgo.rules.movement;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DockingBayTransitTests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("tat_db", "1_129"); //docking bay 94
                    put("yavin4_db", "1_136");
                    put("remote","2_028");
                    put("atgar", "3_072");
                    put("mrbc", "3_077");
                    put("mining_droid","1_018");
                    put("timer_mine", "1_162");

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


    //Regular - Docking Bay Transit
    //Docking Bay Transit allows you to relocate any or all of
    //your characters, vehicles, and any weapons that deploy
    //on a site and can be moved using their own game text
    //(such as Medium Repeating Blaster Cannon) as a
    //group from one docking bay to any other docking bay
    //on table (by the symbolic use of starships for hire) for
    //an expenditure of Force as listed on the docking bay
    //card(s). If a vehicle that has characters aboard moves
    //using Docking Bay Transit, this is not considered a
    //move for those characters aboard. See movement -
    //carrying cards.
    //If a docking bay has no cost listed, docking bay transit
    //is still possible, and the cost is considered zero
    //(although the docking bay at the other end may modify
    //this cost).

    @Test
    public void CharacterMayUseDockingBayTransit() {
        //test1: a character at a docking bay may use transit to move to another docking bay
        //test2: docking bay transit cost is paid
        //test3: successfully transfers selected character
        var scn = GetScenario();

        var rebeltrooper1 = scn.GetLSFiller(1);

        var tat_db = scn.GetLSCard("tat_db"); //transit from here = 1
        var yavin4_db = scn.GetLSCard("yavin4_db"); //transit from here = free

        var expectedTransitCost = 1; //transit from tat_db = 1

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);
        scn.MoveLocationToTable(yavin4_db);

        scn.MoveCardsToLocation(tat_db, rebeltrooper1);

        scn.SkipToLSTurn(Phase.MOVE);
        var preTransitForcePile = scn.GetLSForcePileCount();
        assertTrue(preTransitForcePile >= expectedTransitCost);

        assertTrue(scn.LSCardActionAvailable(tat_db, "transit"));
        scn.LSUseCardAction(tat_db, "transit");

        assertTrue(scn.LSDecisionAvailable("transit to"));
        assertTrue(scn.LSHasCardChoiceAvailable(yavin4_db));
        scn.LSChooseCard(yavin4_db);

        assertTrue(scn.LSHasCardChoiceAvailable(rebeltrooper1)); //test1
        scn.LSChooseCard(rebeltrooper1);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //DOCKING_BAY_TRANSITING - Optional responses
        scn.LSPass();

        scn.DSPass(); //DOCKING_BAY_TRANSITED - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertEquals(expectedTransitCost, preTransitForcePile - scn.GetLSForcePileCount()); //test2
        assertTrue(scn.CardsAtLocation(yavin4_db, rebeltrooper1)); //test3
    }

    @Test
    public void MovesLikeCharacterMayUseDockingBayTransit() {
        //test1: a card that "moves like a character" may use transit to move to another docking bay
        //test2: docking bay transit cost is paid
        //test3: successfully transfers selected card
        var scn = GetScenario();

        var remote = scn.GetLSCard("remote");

        var tat_db = scn.GetLSCard("tat_db"); //transit from here = 1
        var yavin4_db = scn.GetLSCard("yavin4_db"); //transit from here = free

        var expectedTransitCost = 1; //transit from tat_db = 1

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);
        scn.MoveLocationToTable(yavin4_db);

        scn.MoveCardsToLocation(tat_db, remote);

        scn.SkipToLSTurn(Phase.MOVE);
        var preTransitForcePile = scn.GetLSForcePileCount();
        assertTrue(preTransitForcePile >= expectedTransitCost);

        assertTrue(scn.LSCardActionAvailable(tat_db, "transit"));
        scn.LSUseCardAction(tat_db, "transit");

        assertTrue(scn.LSDecisionAvailable("transit to"));
        assertTrue(scn.LSHasCardChoiceAvailable(yavin4_db));
        scn.LSChooseCard(yavin4_db);

        assertTrue(scn.LSHasCardChoiceAvailable(remote)); //test1
        scn.LSChooseCard(remote);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //DOCKING_BAY_TRANSITING - Optional responses
        scn.LSPass();

        scn.DSPass(); //DOCKING_BAY_TRANSITED - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertEquals(expectedTransitCost, preTransitForcePile - scn.GetLSForcePileCount()); //test2
        assertTrue(scn.CardsAtLocation(yavin4_db, remote)); //test3
    }

    @Test
    public void WeaponsMayNotUseDockingBayTransit() {
        //test1: normal weapons that deploy to a site may not use transit to move to another docking bay
        var scn = GetScenario();

        var atgar = scn.GetLSCard("atgar");

        var tat_db = scn.GetLSCard("tat_db"); //transit from here = 1
        var yavin4_db = scn.GetLSCard("yavin4_db"); //transit from here = free

        var expectedTransitCost = 1; //transit from tat_db = 1

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);
        scn.MoveLocationToTable(yavin4_db);

        scn.MoveCardsToLSHand(atgar);
        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.LSDeployCard(atgar);
        scn.LSChooseCard(tat_db);

        scn.SkipToPhase(Phase.MOVE);
        var preTransitForcePile = scn.GetLSForcePileCount();
        assertTrue(preTransitForcePile >= expectedTransitCost);

        assertFalse(scn.LSCardActionAvailable(tat_db, "transit")); //test1
    }

    @Test
    public void TimerMineMayNotUseDockingBayTransit() {
        //shows https://github.com/PlayersCommittee/gemp-swccg-public/issues/452 fixed
        //test1: timer mine deployed at docking bay cannot use transit
        //test2: timer mine remains at docking bay if other cards transit
        var scn = GetScenario();


        var mining_droid = scn.GetLSCard("mining_droid");
        var timer_mine = scn.GetLSCard("timer_mine");

        var tat_db = scn.GetLSCard("tat_db"); //transit from here = 1
        var yavin4_db = scn.GetLSCard("yavin4_db"); //transit from here = free

        var expectedTransitCost = 1; //transit from tat_db = 1

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);
        scn.MoveLocationToTable(yavin4_db);

        scn.MoveCardsToLocation(tat_db, mining_droid);
        scn.MoveCardsToLSHand(timer_mine);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(timer_mine);
        scn.LSChooseCard(tat_db);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(tat_db, timer_mine));

        scn.SkipToPhase(Phase.MOVE);
        var preTransitForcePile = scn.GetLSForcePileCount();
        assertTrue(preTransitForcePile >= expectedTransitCost);

        assertTrue(scn.LSCardActionAvailable(tat_db, "transit"));
        scn.LSUseCardAction(tat_db, "transit");

        assertTrue(scn.LSDecisionAvailable("transit to"));
        assertTrue(scn.LSHasCardChoiceAvailable(yavin4_db));
        scn.LSChooseCard(yavin4_db);

        assertTrue(scn.LSHasCardChoiceAvailable(mining_droid));
        assertFalse(scn.LSHasCardChoiceAvailable(timer_mine)); //test1
        scn.LSChooseCards(mining_droid);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //DOCKING_BAY_TRANSITING - Optional responses
        scn.LSPass();

        scn.DSPass(); //DOCKING_BAY_TRANSITED - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertEquals(expectedTransitCost, preTransitForcePile - scn.GetLSForcePileCount());
        assertTrue(scn.CardsAtLocation(yavin4_db, mining_droid));
        assertTrue(scn.CardsAtLocation(tat_db, timer_mine)); //test2
    }

    //@Ignore
    @Test
    public void WeaponsWithMoveKeywordMayUseDockingBayTransit() {
        //test1: a weapon that deploys on a site and can be moved using its own game text may use transit to move to another docking bay
        //test2: docking bay transit cost is paid
        //test3: successfully transfers selected card
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");

        var tat_db = scn.GetLSCard("tat_db"); //transit from here = 1
        var yavin4_db = scn.GetLSCard("yavin4_db"); //transit from here = free

        var expectedTransitCost = 1; //transit from tat_db = 1

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);
        scn.MoveLocationToTable(yavin4_db);

        scn.MoveCardsToLSHand(mrbc);
        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.LSDeployCard(mrbc);
        scn.LSChooseCard(tat_db);

        scn.SkipToPhase(Phase.MOVE);
        var preTransitForcePile = scn.GetLSForcePileCount();
        assertTrue(preTransitForcePile >= expectedTransitCost);

        assertTrue(scn.LSCardActionAvailable(tat_db, "transit"));
        scn.LSUseCardAction(tat_db, "transit");

        assertTrue(scn.LSDecisionAvailable("transit to"));
        assertTrue(scn.LSHasCardChoiceAvailable(yavin4_db));
        scn.LSChooseCard(yavin4_db);

        assertTrue(scn.LSHasCardChoiceAvailable(mrbc)); //test1
        scn.LSChooseCard(mrbc);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //DOCKING_BAY_TRANSITING - Optional responses
        scn.LSPass();

        scn.DSPass(); //DOCKING_BAY_TRANSITED - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertEquals(expectedTransitCost, preTransitForcePile - scn.GetLSForcePileCount()); //test2
        assertTrue(scn.IsAttachedTo(yavin4_db, mrbc)); //test3
    }

    //add lots of other docking bay transit checks
    //  check regular move requirements:
    //      if moved first (regular) cannot use DB transit
    //      after use DB transit, cannot move (regular)

    // force cost:
    //  to costs
    //  from costs

    // limitations
    //  may not move here

}
