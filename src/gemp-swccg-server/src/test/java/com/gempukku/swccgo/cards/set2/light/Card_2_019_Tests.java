package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class Card_2_019_Tests {
    /**
     * Rebel Tech says Cumulatively adds 1 to total of Attack Run when at your war room.
     * Three copies should add 3.
     */
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tech1", "2_19");
                    put("tech2", "2_19");
                    put("tech3", "2_19");
                    put("warRoom", "1_139");
                    put("attackRun", "2_42");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void RebelTechStatsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tech1").getBlueprint();
        assertEquals("Rebel Tech", card.getTitle());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
    }

    /**
     * Three Rebel Techs at your war room each add 1 to Attack Run because the text says Cumulatively.
     */
    @Test
    public void RebelTechAttackRunBonusStacksCumulativelyFromMultipleCopies() {
        var scn = GetScenario();

        var tech1 = scn.GetLSCard("tech1");
        var tech2 = scn.GetLSCard("tech2");
        var tech3 = scn.GetLSCard("tech3");
        var warRoom = scn.GetLSCard("warRoom");
        var attackRun = scn.GetLSCard("attackRun");

        scn.StartGame();
        scn.MoveLocationToTable(warRoom);
        scn.MoveCardsToLocation(warRoom, tech1, tech2, tech3);
        scn.MoveCardsToLSSideOfTable(attackRun);

        float total = scn.game().getModifiersQuerying().getEpicEventCalculationTotal(scn.gameState(), attackRun, 0);
        assertEquals(3, total, scn.epsilon);
    }
}
