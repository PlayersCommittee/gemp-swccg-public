package com.gempukku.swccgo.cards.set112.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_112_013_Tests {
    /**
     * Mercenary Pilot has two different battle-destiny texts: driving a transport, and piloting at a cloud sector.
     * Those are different game-text clauses. The same clause from two copies still does not stack.
     */
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("ywing", "1_147");
                    put("clouds", "5_85");
                    put("platform", "5_83");
                }},
                new HashMap<>() {{
                    put("pilot1", "112_13");
                    put("pilot2", "112_13");
                    put("crawler1", "1_309");
                    put("crawler2", "1_309");
                    put("tie", "1_304");
                    put("bespin", "5_164");
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
    public void MercenaryPilotStatsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("pilot1").getBlueprint();
        assertEquals("Mercenary Pilot", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
    }

    /**
     * Two Mercenary Pilots both driving transports add only one battle destiny.
     * The driving text is one clause and does not say Cumulatively.
     */
    @Test
    public void MercenaryPilotDrivingBattleDestinyDoesNotStackFromTwoCopies() {
        var scn = GetScenario();

        var pilot1 = scn.GetDSCard("pilot1");
        var pilot2 = scn.GetDSCard("pilot2");
        var crawler1 = scn.GetDSCard("crawler1");
        var crawler2 = scn.GetDSCard("crawler2");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, crawler1, crawler2, luke);
        scn.BoardAsPilot(crawler1, pilot1);
        scn.BoardAsPilot(crawler2, pilot2);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.SkipToPowerSegment();

        assertTrue(scn.DSDecisionAvailable("Do you want to draw 1 battle destiny?"));
        assertFalse(scn.DSDecisionAvailable("Do you want to draw 2 battle destiny?"));
    }

    /**
     * Issue 697: one copy driving a transport and one copy piloting at a related cloud sector
     * are different game-text clauses, so both add a battle destiny.
     * Battle is at Cloud City: Platform 327 (exterior) so the cloud-sector required trigger can fire.
     */
    @Test
    public void MercenaryPilotDrivingAndCloudSectorTextsBothAddBattleDestiny() {
        var scn = GetScenario();

        var pilot1 = scn.GetDSCard("pilot1");
        var pilot2 = scn.GetDSCard("pilot2");
        var crawler = scn.GetDSCard("crawler1");
        var tie = scn.GetDSCard("tie");
        var clouds = scn.GetLSCard("clouds");
        var bespin = scn.GetDSCard("bespin");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSCard("platform");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, crawler, luke);
        scn.BoardAsPilot(crawler, pilot1);
        scn.MoveCardsToLocation(clouds, tie);
        scn.BoardAsPilot(tie, pilot2);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.SkipToPowerSegment();

        assertTrue(scn.DSDecisionAvailable("Do you want to draw 2 battle destiny?"));
    }
}