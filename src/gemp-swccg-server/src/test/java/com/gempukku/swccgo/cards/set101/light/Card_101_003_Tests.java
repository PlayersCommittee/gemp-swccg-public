package com.gempukku.swccgo.cards.set101.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_101_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_019");
                    put("runLukeRun", "101_3");
                    put("cantina", "1_128"); // Tatooine: Cantina
                }},
                new HashMap<>() {{
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
    public void RunLukeRunKeepsPowerBonusWhenDeathStarAssaultSquadronAtBattleSite() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var runLukeRun = scn.GetLSCard("runLukeRun");
        var cantina = scn.GetLSCard("cantina");
        var trooper = scn.GetLSFiller(1);
        var dsas = scn.GetDSCard("dsas");
        var db = scn.GetDSCard("db");

        scn.StartGame();
        scn.MoveCardsToLSHand(runLukeRun);
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(cantina, luke);
        scn.MoveCardsToLocation(db, dsas, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(db));
        scn.DSUseCardAction(db, "Initiate battle");
        scn.PassForceUseResponses();

        assertTrue(scn.LSCardPlayAvailable(runLukeRun));
        scn.LSPlayCard(runLukeRun);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(db, luke));
        // Luke printed power 3; Run Luke, Run! +2 should remain despite DSAS Vader permanent pilot
        assertEquals(5, scn.GetPower(luke));
    }
}