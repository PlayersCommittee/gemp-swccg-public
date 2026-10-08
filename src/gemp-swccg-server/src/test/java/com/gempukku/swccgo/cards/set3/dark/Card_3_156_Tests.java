package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Hoth 3_156 Blizzard Scout 1, including move-as-react embark timing (#872).
 */
public class Card_3_156_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("cancelReact", "209_021");
                }},
                new HashMap<>() {{
                    put("scout", "3_156");
                    put("trooper", "1_194");
                    put("ice", "3_148");
                    put("ridge", "3_149");
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

    private void setupReactToBattle(VirtualTableScenario scn) {
        var luke = scn.GetLSCard("luke");
        var cancelReact = scn.GetLSCard("cancelReact");
        var ice = scn.GetDSCard("ice");
        var ridge = scn.GetDSCard("ridge");
        var scout = scn.GetDSCard("scout");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(ice);
        scn.MoveLocationToTable(ridge);
        scn.MoveCardsToLSHand(cancelReact);
        scn.MoveCardsToLocation(ice, luke, scn.GetDSFiller(1));
        scn.MoveCardsToLocation(ridge, scout, trooper);
        scn.DSActivateForceCheat(5);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(ice));
        scn.LSUseCardAction(ice, "Initiate battle");
        scn.PassForceUseResponses();
    }

    @Test
    public void BlizzardScout1ReactCanBeCanceledBeforePassengersEmbark() {
        var scn = GetScenario();
        setupReactToBattle(scn);

        var ice = scn.GetDSCard("ice");
        var ridge = scn.GetDSCard("ridge");
        var cancelReact = scn.GetLSCard("cancelReact");
        var scout = scn.GetDSCard("scout");
        var trooper = scn.GetDSCard("trooper");

        assertTrue(scn.DSCardActionAvailable(scout, "Move using landspeed as a 'react'"));
        scn.DSUseCardAction(scout, "Move using landspeed as a 'react'");
        scn.DSChooseCard(ice);
        scn.PassForceUseResponses();

        assertNull("Trooper must still be at the site until the react survives cancel", trooper.getAttachedTo());
        assertTrue(scn.CardsAtLocation(ridge, trooper, scout));
        assertFalse(scn.DSDecisionAvailable("Perform a movement before regular move of the 'react'"));
        assertTrue("Cancel 'react' should be offered before embark. Decision=" + scn.GetCurrentDecision().getText(),
                scn.LSCardPlayAvailable(cancelReact));
    }

    @Test
    public void BlizzardScout1ReactOffersEmbarkAfterCancelWindowPasses() {
        var scn = GetScenario();
        setupReactToBattle(scn);

        var ice = scn.GetDSCard("ice");
        var scout = scn.GetDSCard("scout");

        assertTrue(scn.DSCardActionAvailable(scout, "Move using landspeed as a 'react'"));
        scn.DSUseCardAction(scout, "Move using landspeed as a 'react'");
        scn.DSChooseCard(ice);
        scn.PassForceUseResponses();
        scn.LSPass();
        scn.DSPass();

        assertTrue("Embark should be offered after the cancel window. Decision=" + scn.GetCurrentDecision().getText(),
                scn.DSDecisionAvailable("Perform a movement before regular move of the 'react'"));
    }

    @Test
    public void BlizzardScout1ReactCancelLeavesTrooperOffVehicle() {
        var scn = GetScenario();
        setupReactToBattle(scn);

        var ice = scn.GetDSCard("ice");
        var ridge = scn.GetDSCard("ridge");
        var cancelReact = scn.GetLSCard("cancelReact");
        var scout = scn.GetDSCard("scout");
        var trooper = scn.GetDSCard("trooper");

        scn.DSUseCardAction(scout, "Move using landspeed as a 'react'");
        scn.DSChooseCard(ice);
        scn.PassForceUseResponses();

        assertTrue(scn.LSCardPlayAvailable(cancelReact));
        scn.LSPlayCard(cancelReact);
        scn.PassAllResponses();

        assertNull(trooper.getAttachedTo());
        assertTrue(scn.CardsAtLocation(ridge, trooper, scout));
        assertFalse(scn.CardsAtLocation(ice, scout));
    }
}
