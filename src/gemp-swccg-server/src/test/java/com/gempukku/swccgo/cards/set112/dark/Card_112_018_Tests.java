package com.gempukku.swccgo.cards.set112.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class Card_112_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("rebel", "1_028");
                }},
                new HashMap<>()
                {{
                    put("racing_skiff", "112_018");
                    put("mercenary_pilot", "112_013");
                    put("east_platform", "5_169");
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
    public void RacingSkiffMayReactToSiteWithNonUniqueAlienDriver() {
        // Printed: May deploy with a driver (must be Barada, Klaatu, or a non-unique alien) as a 'react'.
        var scn = GetScenario();

        var rebel = scn.GetLSCard("rebel");
        var skiff = scn.GetDSCard("racing_skiff");
        var pilot = scn.GetDSCard("mercenary_pilot");
        var eastPlatform = scn.GetDSCard("east_platform");

        scn.StartGame();

        scn.MoveLocationToTable(eastPlatform);
        scn.MoveCardsToLocation(eastPlatform, rebel);
        scn.MoveCardsToDSHand(skiff, pilot);
        scn.DSActivateForceCheat(8);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(eastPlatform));
        scn.LSForceDrainAt(eastPlatform);

        assertTrue(scn.DSCardPlayAvailable(skiff));
        scn.DSPlayCard(skiff);
        scn.PassAllResponses();

        if (scn.DSDecisionAvailable("Do you want to simultaneously deploy a driver")) {
            scn.DSChooseYes();
        }
        assertTrue(scn.DSHasCardChoiceAvailable(pilot));
        scn.DSChooseCard(pilot);

        assertTrue(scn.DSHasCardChoiceAvailable(eastPlatform));
        scn.DSChooseCard(eastPlatform);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(eastPlatform, skiff));
        assertTrue(scn.IsAboardAsPilot(skiff, pilot));
    }
}
