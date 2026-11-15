package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_091_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("badFeeling", "1_91"); // I've Got A Bad Feeling About This
                    put("falcon", "13_21"); // Han, Chewie, And The Falcon
                    put("han", "1_11"); // Han Solo
                    put("db", "1_129"); // Tatooine: Docking Bay 94
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
    public void BadFeelingDoublesNotTriplesWithOnlyFalconInBattle() {
        var scn = GetScenario();

        var badFeeling = scn.GetLSCard("badFeeling");
        var falcon = scn.GetLSCard("falcon");
        var db = scn.GetLSCard("db");
        var ds1 = scn.GetDSFiller(1);
        var ds2 = scn.GetDSFiller(2);
        var ds3 = scn.GetDSFiller(3);
        var ds4 = scn.GetDSFiller(4);
        var ds5 = scn.GetDSFiller(5);

        scn.StartGame();
        scn.MoveCardsToLSHand(badFeeling);
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(db, falcon, ds1, ds2, ds3, ds4, ds5);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(db);

        assertTrue(scn.LSCardPlayAvailable(badFeeling, "Double opponent's battle damage"));
        assertFalse(scn.LSCardPlayAvailable(badFeeling, "Triple opponent's battle damage"));
    }

    @Test
    public void BadFeelingTriplesWithCharacterHanPresentInBattle() {
        var scn = GetScenario();

        var badFeeling = scn.GetLSCard("badFeeling");
        var han = scn.GetLSCard("han");
        var db = scn.GetLSCard("db");
        var ds1 = scn.GetDSFiller(1);
        var ds2 = scn.GetDSFiller(2);
        var ds3 = scn.GetDSFiller(3);
        var ds4 = scn.GetDSFiller(4);
        var ds5 = scn.GetDSFiller(5);

        scn.StartGame();
        scn.MoveCardsToLSHand(badFeeling);
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(db, han, ds1, ds2, ds3, ds4, ds5);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(db);

        assertTrue(scn.LSCardPlayAvailable(badFeeling, "Triple opponent's battle damage"));
    }
}
