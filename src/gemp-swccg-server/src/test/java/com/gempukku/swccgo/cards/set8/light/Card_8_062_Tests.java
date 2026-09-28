package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_8_062_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("absolutely", "8_62"); // This Is Absolutely Right
                    put("falcon", "13_21"); // Han, Chewie, And The Falcon
                    put("han", "1_11"); // Han Solo
                    put("leia", "1_17"); // Leia Organa
                    put("platform", "8_76"); // Endor: Landing Platform (Docking Bay)
                }},
                new HashMap<>() {{
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
    public void AbsolutelyRightCannotPlayWithOnlyFalconAtBattleground() {
        var scn = GetScenario();

        var absolutely = scn.GetLSCard("absolutely");
        var falcon = scn.GetLSCard("falcon");
        var leia = scn.GetLSCard("leia");
        var platform = scn.GetLSCard("platform");

        scn.StartGame();
        scn.MoveCardsToLSHand(absolutely);
        scn.MoveLocationToTable(platform);
        scn.MoveCardsToLocation(platform, falcon, leia);

        scn.SkipToLSTurn();

        assertFalse(scn.LSCardPlayAvailable(absolutely, "Make Force drains +1 this turn"));
    }

    @Test
    public void AbsolutelyRightCanPlayWithCharacterHanAndLeiaPresent() {
        var scn = GetScenario();

        var absolutely = scn.GetLSCard("absolutely");
        var han = scn.GetLSCard("han");
        var leia = scn.GetLSCard("leia");
        var platform = scn.GetLSCard("platform");

        scn.StartGame();
        scn.MoveCardsToLSHand(absolutely);
        scn.MoveLocationToTable(platform);
        scn.MoveCardsToLocation(platform, han, leia);

        scn.SkipToLSTurn();

        assertTrue(scn.LSCardPlayAvailable(absolutely, "Make Force drains +1 this turn"));
    }
}
