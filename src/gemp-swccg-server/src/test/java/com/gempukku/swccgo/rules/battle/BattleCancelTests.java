package com.gempukku.swccgo.rules.battle;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BattleCancelTests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("trap","5_055"); //It's A Trap
                }},
                new HashMap<>()
                {{
                    put("combatReadiness","8_136");
                }},
                15,
                15,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    //also see weapons/HitTests.java HitCharacterLostIfBattleCancelledBeforeWeaponsSegment

    @Test
    public void BattleJustInitiatedActionsCannotBePlayedAfterBattleIsCanceled() {
        //test1: after It's A Trap! cancels battle, unable to play Combat Readiness as response to battle just initiated

        var scn = GetScenario();

        var rebelTrooper = scn.GetLSFiller(1);
        var trap = scn.GetLSCard("trap");

        var trooper = scn.GetDSFiller(1);
        var combatReadiness = scn.GetDSCard("combatReadiness");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.DSPass(); //Choose starting interrupt

        scn.MoveCardsToLocation(site, trooper, rebelTrooper);

        scn.MoveCardsToLSHand(trap);
        scn.MoveCardsToDSHand(combatReadiness);

        scn.LSActivateForceCheat(3); // 3 to play It's A Trap!

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        assertTrue(scn.LSDecisionAvailable("Battle just initiated"));
        assertTrue(scn.LSCardPlayAvailable(trap));
        scn.LSPlayCard(trap);

        scn.DSPass(); //Use 3 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //Playing •It's A Trap! - Optional responses
        scn.LSPass();

        scn.DSPass(); //BATTLE_CANCELED - Optional responses
        scn.LSPass();

        scn.DSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.LSPass();

        assertFalse(scn.DSCardPlayAvailable(combatReadiness)); //test1
        assertTrue(scn.DSDecisionAvailable("BATTLE_INITIATED"));
        scn.DSPass();
        scn.LSPass();

        assertTrue(scn.AwaitingLSBattlePhaseActions()); //battle finished
    }
}
