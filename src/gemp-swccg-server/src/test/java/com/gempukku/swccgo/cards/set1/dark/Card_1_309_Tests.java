package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class Card_1_309_Tests {
    /**
     * Sandcrawler adds 1 to forfeit of each Jawa at the same exterior site.
     * That bonus does not say Cumulatively, so extra copies still add only 1.
     */
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("crawler1", "1_309");
                    put("crawler2", "1_309");
                    put("crawler3", "1_309");
                    put("jawa", "1_182");
                    put("driver1", "1_169");
                    put("driver2", "1_169");
                    put("driver3", "1_169");
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
    public void SandcrawlerStatsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("crawler1").getBlueprint();
        assertEquals("Sandcrawler", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
    }

    /**
     * One Sandcrawler makes a Jawa forfeit 2 (printed 1 plus 1).
     * Three Sandcrawlers at the same exterior site still make that Jawa forfeit 2, not 4.
     */
    @Test
    public void SandcrawlerJawaForfeitBonusDoesNotStackFromMultipleCopies() {
        var scn = GetScenario();

        var crawler1 = scn.GetDSCard("crawler1");
        var crawler2 = scn.GetDSCard("crawler2");
        var crawler3 = scn.GetDSCard("crawler3");
        var jawa = scn.GetDSCard("jawa");
        var driver1 = scn.GetDSCard("driver1");
        var driver2 = scn.GetDSCard("driver2");
        var driver3 = scn.GetDSCard("driver3");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, crawler1, crawler2, crawler3, jawa);
        scn.BoardAsPilot(crawler1, driver1);
        scn.BoardAsPilot(crawler2, driver2);
        scn.BoardAsPilot(crawler3, driver3);

        scn.SkipToPhase(Phase.CONTROL);

        // Jawa printed forfeit 1, plus 1 from Sandcrawler, not plus 3 from three copies
        assertEquals(2, scn.GetForfeit(jawa));
    }
}
