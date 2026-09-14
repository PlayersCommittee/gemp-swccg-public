package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_9_134_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("dsas", "7_303"); // Death Star Assault Squadron
                    put("vader", "1_168"); // Darth Vader
                    put("db", "1_291"); // Tatooine: Docking Bay 94
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.BHBMObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void YourDestinyDoesNotLoseForceWhenOnlyDSASAtBattleground() {
        var scn = GetScenario();

        var dsas = scn.GetDSCard("dsas");
        var db = scn.GetDSCard("db");

        scn.StartGame();
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(db, dsas);

        scn.SkipToDSTurn();
        scn.PassAllResponses();

        assertFalse(scn.AwaitingLSForceLossPayment());
    }

    @Test
    public void YourDestinyLosesForceWhenCharacterVaderPresentAtBattleground() {
        var scn = GetScenario();

        var vader = scn.GetDSCard("vader");
        var db = scn.GetDSCard("db");

        scn.StartGame();
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(db, vader);

        scn.SkipToDSTurn();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSForceLossPayment());
        scn.LSPayRemainingForceLossFromReserveDeck();
    }
}
