package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Issue #974: Fire Extinguisher deploys on Artoo-Detoo In Red 5 (permanent astromech is R2-D2).
 */
public class Card_2_025_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("extinguisher", "2_25");
                    put("artooRed5", "111_2");
                    put("r2", "2_14");
                    put("luke", "1_19");
                    put("walkway", "5_79");
                }},
                new HashMap<>(),
                10,
                10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void FireExtinguisherStatsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("extinguisher").getBlueprint();
        assertEquals("Fire Extinguisher", card.getTitle());
    }

    @Test
    public void FireExtinguisherDeploysOnArtooDetooInRed5() {
        var scn = GetScenario();
        var extinguisher = scn.GetLSCard("extinguisher");
        var artooRed5 = scn.GetLSCard("artooRed5");
        var luke = scn.GetLSCard("luke");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, artooRed5);
        scn.BoardAsPilot(artooRed5, luke);
        scn.MoveCardsToLSHand(extinguisher);
        scn.LSActivateForceCheat(6);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(extinguisher, "Deploy"));
        scn.LSDeployCard(extinguisher);
        assertTrue(scn.LSHasCardChoiceAvailable(artooRed5));
        scn.LSChooseCard(artooRed5);
        scn.PassAllResponses();
        assertEquals(artooRed5, extinguisher.getAttachedTo());
    }

    @Test
    public void FireExtinguisherStillDeploysOnAstromechCharacter() {
        var scn = GetScenario();
        var extinguisher = scn.GetLSCard("extinguisher");
        var r2 = scn.GetLSCard("r2");
        var walkway = scn.GetLSCard("walkway");

        scn.StartGame();
        scn.MoveLocationToTable(walkway);
        scn.MoveCardsToLocation(walkway, r2);
        scn.MoveCardsToLSHand(extinguisher);
        scn.LSActivateForceCheat(6);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(extinguisher, "Deploy"));
        scn.LSDeployCard(extinguisher);
        assertTrue(scn.LSHasCardChoiceAvailable(r2));
        scn.LSChooseCard(r2);
        scn.PassAllResponses();
        assertEquals(r2, extinguisher.getAttachedTo());
    }
}
