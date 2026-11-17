package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Issue #998: Endor: Dark Forest LS text "If your Ewok present, Force drain +1 here"
 * must count Wuta (V) as well as original Wuta.
 */
public class Card_8_161_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("wuta", "8_33");
                    put("wutaV", "225_10");
                }},
                new HashMap<>() {{
                    put("darkForest", "8_161");
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
    public void EndorDarkForestStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("darkForest").getBlueprint();
        assertEquals("Endor: Dark Forest", card.getTitle());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertTrue(card.hasKeyword(Keyword.FOREST));
    }

    @Test
    public void EndorDarkForestForceDrainPlusOneWhenOriginalWutaPresent() {
        assertLsDrainWithEwok("wuta");
    }

    @Test
    public void EndorDarkForestForceDrainPlusOneWhenWutaVPresent() {
        // #998: Wuta (V) is species Ewok; Dark Forest must add the LS drain.
        assertLsDrainWithEwok("wutaV");
    }

    private void assertLsDrainWithEwok(String ewokKey) {
        var scn = GetScenario();
        var forest = scn.GetDSCard("darkForest");
        var ewok = scn.GetLSCard(ewokKey);

        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(forest, ewok);

        scn.SkipToLSTurn(Phase.CONTROL);
        for (int i = 0; i < 12 && !scn.AwaitingLSControlPhaseActions(); i++) {
            if (scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(2, scn.GetDSIconsOnLocation(forest));
        assertTrue(scn.LSForceDrainAvailable(forest));
        scn.LSForceDrainAt(forest);
        scn.PassForceDrainStartResponses();
        // 2 Dark icons + 1 for "your Ewok present"
        assertEquals("Force drain at Dark Forest with " + ewokKey + " present", 3, scn.GetForceDrainTotal());
    }
}
