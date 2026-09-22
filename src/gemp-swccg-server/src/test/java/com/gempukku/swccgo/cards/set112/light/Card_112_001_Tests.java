package com.gempukku.swccgo.cards.set112.light;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Coverage for issue #176: failed Objective deploy must restore converted location visibility.
 */
public class Card_112_001_Tests {

    /**
     * Agents In The Court with locations to deploy, but no unique alien with species for Rep,
     * so the Objective fails after converting DS Audience Chamber.
     */
    private static final StartingSetup AgentsInTheCourtMissingRep = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("agents", "112_1"); // Agents In The Court / No Love For The Empire
                put("hutt", "112_9"); // Tatooine: Hutt Trade Route (Desert)
                put("chamber_ls", "6_81"); // Jabba's Palace: Audience Chamber (Light)
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            // Hutt Trade Route / JP site may auto-select when unique; handle side / choose prompts if shown.
            if (scn.LSDecisionAvailable("On which side")) {
                scn.LSChoose("Left");
            }
            if (scn.LSDecisionAvailable("Choose Jabba's Palace site")) {
                scn.LSChooseCard(scn.GetLSCard("chamber_ls"));
            }
            if (scn.LSDecisionAvailable("Choose Hutt Trade Route")) {
                scn.LSChooseCard(scn.GetLSCard("hutt"));
            }
            // Failed Rep search shows verify dialogs to both players.
            for (int i = 0; i < 10; i++) {
                boolean progressed = false;
                if (scn.LSDecisionAvailable("Verify")) {
                    scn.LSPass();
                    progressed = true;
                }
                if (scn.DSDecisionAvailable("Verify")) {
                    scn.DSPass();
                    progressed = true;
                }
                if (scn.LSDecisionAvailable("Choose Yarna")) {
                    scn.LSPass();
                    progressed = true;
                }
                if (scn.LSDecisionAvailable("On which side")) {
                    scn.LSChoose("Left");
                    progressed = true;
                }
                if (!progressed) {
                    break;
                }
            }
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                }},
                10,
                10,
                AgentsInTheCourtMissingRep,
                StartingSetup.DSStartingLocation("6_162"), // DS Audience Chamber first
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void AgentsInTheCourtStatsAndKeywordsAreCorrect() {
        /**
         * Title: Agents In The Court / No Love For The Empire
         * Side: Light
         * Type: Objective
         * Destiny: 0
         * Set: Jabba's Palace Sealed Deck (Premium)
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("agents").getBlueprint();

        assertEquals(Title.Agents_In_The_Court, card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getIconCount(Icon.PREMIUM));
    }

    @Test
    public void AgentsInTheCourtFailedMissingRepRestoresConvertedAudienceChamberToVisibleTop() {
        // Real path for #176: DS Audience Chamber on table -> LS Objective converts it then fails
        // missing Rep -> converting site and Objective undone, DS Audience Chamber visible again (not void top).
        var scn = GetScenario();

        PhysicalCardImpl agents = scn.GetLSCard("agents");
        PhysicalCardImpl hutt = scn.GetLSCard("hutt");
        PhysicalCardImpl chamberLs = scn.GetLSCard("chamber_ls");
        PhysicalCardImpl chamberDs = scn.GetDSCard("starting-location");

        scn.StartGame();

        // Objective illegal -> out of play; locations put back to Reserve Deck
        assertEquals(Zone.OUT_OF_PLAY, agents.getZone());
        assertEquals(Zone.RESERVE_DECK, hutt.getZone());
        assertEquals(Zone.RESERVE_DECK, chamberLs.getZone());

        // Converted DS site must be promoted back to visible top of the location stack
        assertEquals(Zone.LOCATIONS, chamberDs.getZone());
        assertFalse(Zone.CONVERTED_LOCATIONS.equals(chamberDs.getZone()));

        assertTrue("DS Audience Chamber should be among top locations", scn.gameState().getTopLocations().contains(chamberDs));
        assertTrue("DS Audience Chamber should be in layout top order", scn.gameState().getLocationsInOrder().contains(chamberDs));
    }
}
