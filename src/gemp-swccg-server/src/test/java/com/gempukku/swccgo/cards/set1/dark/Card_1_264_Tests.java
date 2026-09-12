package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Physical Choke battle-won option needs a present Dark Jedi. Dark_Jedi already requires character,
 * and is wired through personaPresentInBattle. These tests cover the Rebel Trooper mode still working
 * and document that DSAS alone does not unlock the top-level Rebel Trooper path incorrectly.
 */
public class Card_1_264_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rebelTrooper", "1_28"); // Rebel Trooper (approx; may adjust)
                }},
                new HashMap<>() {{
                    put("choke", "1_264"); // Physical Choke
                    put("dsas", "7_303"); // Death Star Assault Squadron
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
    public void PhysicalChokeRebelTrooperModeStillAvailableWithDSASOnTable() {
        var scn = GetScenario();

        var choke = scn.GetDSCard("choke");
        var dsas = scn.GetDSCard("dsas");
        var db = scn.GetDSCard("db");
        var trooper = scn.GetLSCard("rebelTrooper");

        scn.StartGame();
        scn.MoveCardsToDSHand(choke);
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(db, dsas, trooper);

        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(choke, "Choke a Rebel Trooper"));
        // battle-won Dark Jedi mode is not a top-level action; DSAS must not invent one
        assertFalse(scn.DSCardPlayAvailable(choke, "Choke an opponent's character"));
    }
}
