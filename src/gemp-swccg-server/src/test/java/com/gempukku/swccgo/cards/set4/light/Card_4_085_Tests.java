package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_4_085_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("bogClearing", "4_085"); // Dagobah: Bog Clearing
                    put("captainHan", "5_001"); // Captain Han Solo
                    put("falcon", "1_143"); // Millennium Falcon
                    put("ccDockingBay", "5_083"); // Cloud City: Platform 327 (Docking Bay)
                }},
                new HashMap<>() {{
                }},
                20,
                20,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /**
     * Activate Force before Deploy so the Falcon Deploy action is built with
     * enough Force for Falcon + Captain Han (7). Cheating after SkipTo leaves
     * a stale empty-ship-only action.
     */
    private void setupFalconAndPilotReadyToDeploy(VirtualTableScenario scn, PhysicalCardImpl... extraLocations) {
        var falcon = scn.GetLSCard("falcon");
        var captainHan = scn.GetLSCard("captainHan");
        scn.StartGame();
        for (PhysicalCardImpl location : extraLocations) {
            scn.MoveLocationToTable(location);
        }
        scn.MoveCardsToLSHand(falcon, captainHan);
        scn.LSActivateForceCheat(10);
        scn.SkipToLSTurn(Phase.DEPLOY);
    }

    /**
     * Drive the real UI: Deploy Falcon → Yes to simultaneous pilot → choose pilot.
     */
    private void beginFalconSimultaneousDeployWithPilot(VirtualTableScenario scn, PhysicalCardImpl falcon, PhysicalCardImpl pilot) {
        assertTrue(scn.LSDeployAvailable(falcon));
        scn.LSDeployCard(falcon);
        assertTrue(scn.LSDecisionAvailable("simultaneously deploy a pilot"));
        scn.LSChooseYes();
        assertTrue(scn.LSHasCardChoiceAvailable(pilot));
        scn.LSChooseCard(pilot);
    }

    @Test
    public void BogClearingStatsAndKeywordsAreCorrect() {
        /**
         * Title: Dagobah: Bog Clearing
         * Uniqueness: Unique
         * Side: Light
         * Type: Location
         * Subtype: Site
         * Icons: Dagobah, Exterior site, Planet
         * Light Force Icons: 1
         * Dark Force Icons: 0
         * Game Text: Dark: If you occupy, Force generation +1 for you here.
         *            Light: Your starfighters may deploy here and immune to Awwww, Cannot Get Your Ship Out here.
         * Set: Dagobah
         * Rarity: R
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("bogClearing").getBlueprint();
        assertEquals("Dagobah: Bog Clearing", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.LIGHT_FORCE);
        }});
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(0, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.DAGOBAH, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void BogClearingDoesNotAllowCaptainHanToRideFalconThere() {
        // #760: Falcon may land here, but Captain Han must not deploy to Dagobah with it
        var scn = GetScenario();

        var bogClearing = scn.GetLSCard("bogClearing");
        var captainHan = scn.GetLSCard("captainHan");
        var falcon = scn.GetLSCard("falcon");
        var ccDockingBay = scn.GetLSCard("ccDockingBay");

        setupFalconAndPilotReadyToDeploy(scn, bogClearing, ccDockingBay);

        beginFalconSimultaneousDeployWithPilot(scn, falcon, captainHan);

        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(ccDockingBay));
        assertFalse(scn.LSHasCardChoiceAvailable(bogClearing));

        scn.LSChooseCard(ccDockingBay);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ccDockingBay, falcon));
        assertTrue(scn.IsAboardAsPilot(falcon, captainHan));
    }

    @Test
    public void BogClearingStillAllowsEmptyFalconToDeployThere() {
        // Legal contrast: empty Falcon may still land at Bog Clearing
        var scn = GetScenario();

        var bogClearing = scn.GetLSCard("bogClearing");
        var captainHan = scn.GetLSCard("captainHan");
        var falcon = scn.GetLSCard("falcon");

        setupFalconAndPilotReadyToDeploy(scn, bogClearing);

        assertTrue(scn.LSDeployAvailable(falcon));
        scn.LSDeployCard(falcon);
        assertTrue(scn.LSDecisionAvailable("simultaneously deploy a pilot"));
        scn.LSChooseNo();
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(bogClearing));
        scn.LSChooseCard(bogClearing);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(bogClearing, falcon));
        assertFalse(scn.IsAboardAsPilot(falcon, captainHan));
    }
}
