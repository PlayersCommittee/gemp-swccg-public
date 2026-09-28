package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_5_141_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("focused", "5_141"); // Focused Attack
                    put("dsas", "7_303"); // Death Star Assault Squadron
                    put("vader", "1_168"); // Darth Vader
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
    public void FocusedAttackCannotPlayWithOnlyDSASInBattle() {
        var scn = GetScenario();

        var trooper = scn.GetLSFiller(1);
        var focused = scn.GetDSCard("focused");
        var dsas = scn.GetDSCard("dsas");
        var db = scn.GetDSCard("db");

        scn.StartGame();
        scn.MoveCardsToDSHand(focused);
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(db, dsas, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(db);

        assertFalse(scn.DSCardPlayAvailable(focused));
    }

    @Test
    public void FocusedAttackCanPlayWithCharacterVaderInBattle() {
        var scn = GetScenario();

        var trooper = scn.GetLSFiller(1);
        var focused = scn.GetDSCard("focused");
        var vader = scn.GetDSCard("vader");
        var db = scn.GetDSCard("db");

        scn.StartGame();
        scn.MoveCardsToDSHand(focused);
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(db, vader, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(db);

        assertTrue(scn.DSCardPlayAvailable(focused));
    }
}
