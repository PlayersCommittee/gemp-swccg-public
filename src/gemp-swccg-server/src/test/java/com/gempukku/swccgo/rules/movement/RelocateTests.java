package com.gempukku.swccgo.rules.movement;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RelocateTests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127");
                    put("eisley", "1_133");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("tie", "1_304");
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
    public void RelocateDoesNotDisembarkACharacterFromAStarship() {
        var scn = GetScenario();

        var tatooine = scn.GetLSCard("tatooine");
        var eisley = scn.GetLSCard("eisley");
        var vader = scn.GetDSCard("vader");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(eisley);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, vader);

        assertFalse(Filters.canBeRelocatedToLocation(eisley, 2).accepts(scn.game(), vader));
        assertFalse(Filters.canBeRelocated(false).accepts(scn.game(), vader));
    }

    @Test
    public void RelocateStillAllowsACharacterAtASite() {
        var scn = GetScenario();

        var eisley = scn.GetLSCard("eisley");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(eisley);
        scn.MoveCardsToLocation(site, vader);

        assertTrue(Filters.canBeRelocatedToLocation(eisley, 0).accepts(scn.game(), vader)
                || Filters.canBeRelocatedToLocation(eisley, true, 0).accepts(scn.game(), vader));
    }
}
