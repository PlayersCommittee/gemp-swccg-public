package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_6_154_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("hiddenWeapons", "6_154");
                    put("slaveI", "109_8"); // Boba Fett In Slave I
                    put("boba", "5_091"); // Boba Fett (character)
                    put("db", "1_291"); // Tatooine: Docking Bay 94
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
    public void HiddenWeaponsCannotPlayWithBobaFettInSlaveIAtDockingBay() {
        var scn = GetScenario();

        var trooper = scn.GetLSFiller(1);
        var hiddenWeapons = scn.GetDSCard("hiddenWeapons");
        var slaveI = scn.GetDSCard("slaveI");
        var db = scn.GetDSCard("db");

        scn.StartGame();
        scn.MoveCardsToDSHand(hiddenWeapons);
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(db, slaveI, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(db);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardPlayAvailable(hiddenWeapons));
    }

    @Test
    public void HiddenWeaponsCanPlayWithCharacterBobaFettPresent() {
        var scn = GetScenario();

        var trooper = scn.GetLSFiller(1);
        var hiddenWeapons = scn.GetDSCard("hiddenWeapons");
        var boba = scn.GetDSCard("boba");
        var db = scn.GetDSCard("db");

        scn.StartGame();
        scn.MoveCardsToDSHand(hiddenWeapons);
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(db, boba, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(db);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardPlayAvailable(hiddenWeapons));
    }
}