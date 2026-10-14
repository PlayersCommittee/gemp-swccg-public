package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_7_138_Tests {
    private static final StartingSetup MwyhlObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("mwyhl", "7_138");
                put("dagobah", "4_084");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("yoda", "4_002");
                    put("jungle", "4_086");
                }},
                new HashMap<>() {{
                    put("cave", "4_158");
                }},
                15,
                15,
                MwyhlObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void MindWhatYouHaveLearnedDoesNotDeployYodaToDagobahCaveWithoutPresenceOrForceIcons() {
        var scn = GetScenario();

        var mwyhl = scn.GetLSCard("mwyhl");
        var yoda = scn.GetLSCard("yoda");
        var cave = scn.GetDSCard("cave");
        var jungle = scn.GetLSCard("jungle");

        scn.StartGame();
        scn.MoveLocationToTable(cave);
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLSHand(yoda);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(4);
        scn.MoveCardsToTopOfLSReserveDeck(yoda);

        assertTrue("actions=" + scn.GetLSAvailableActions(),
                scn.LSCardActionAvailable(mwyhl, "Deploy card to Dagobah from Reserve Deck"));
        scn.LSUseCardAction(mwyhl, "Deploy card to Dagobah from Reserve Deck");
        if (scn.LSDecisionAvailable("Choose card from Reserve Deck")
                || scn.LSDecisionAvailable("Choose card to deploy")) {
            scn.LSChooseCard(yoda);
        }
        scn.PassAllResponses();

        if (scn.LSHasCardChoiceAvailable(jungle) || scn.LSHasCardChoiceAvailable(cave)) {
            assertTrue(scn.LSHasCardChoiceAvailable(jungle));
            assertFalse(scn.LSHasCardChoiceAvailable(cave));
            scn.LSChooseCard(jungle);
            scn.PassAllResponses();
        }

        assertAtLocation(jungle, yoda);
        assertFalse(scn.CardsAtLocation(cave, yoda));
    }
}
