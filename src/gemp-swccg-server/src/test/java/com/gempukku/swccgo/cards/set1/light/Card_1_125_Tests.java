package com.gempukku.swccgo.cards.set1.light;

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

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_125_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trashCompactor", "1_125"); // Death Star: Trash Compactor
                    put("han", "1_011"); // Han Solo
                    put("captainHan", "5_001"); // Captain Han Solo
                    put("falcon", "1_143"); // Millennium Falcon
                    put("xwing", "1_146"); // X-wing
                    put("ccDockingBay", "5_083"); // Cloud City: Platform 327 (Docking Bay)
                }},
                new HashMap<>() {{
                    put("deathStar", "2_143"); // Death Star system
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
     * Fails if the action was empty-ship only (the stale-action / not-enough-Force path).
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
    public void TrashCompactorStatsAndKeywordsAreCorrect() {
        /**
         * Title: Death Star: Trash Compactor
         * Uniqueness: Unique
         * Side: Light
         * Type: Location
         * Subtype: Site
         * Icons: Interior site, Mobile
         * Game Text: You may deploy here without presence. If you control, Force drain +1 here.
         * Set: Premiere
         * Rarity: U1
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("trashCompactor").getBlueprint();
        assertEquals("Death Star: Trash Compactor", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
        assertEquals(Rarity.U1, card.getRarity());
        assertTrue(card.hasIcon(Icon.INTERIOR_SITE));
        assertTrue(card.hasIcon(Icon.MOBILE));
    }

    @Test
    public void TrashCompactorStillAllowsHanDeployToTrashCompactorSite() {
        // Site-only ignore presence still works at Trash Compactor; not at Death Star system
        var scn = GetScenario();

        var trashCompactor = scn.GetLSCard("trashCompactor");
        var han = scn.GetLSCard("han");
        var deathStar = scn.GetDSCard("deathStar");

        scn.StartGame();
        scn.MoveLocationToTable(deathStar);
        scn.MoveLocationToTable(trashCompactor);
        scn.MoveCardsToLSHand(han);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(5);

        assertTrue(scn.LSDeployAvailable(han));
        scn.LSDeployCard(han);
        assertTrue(scn.LSHasCardChoiceAvailable(trashCompactor));
        assertFalse(scn.LSHasCardChoiceAvailable(deathStar));
    }

    @Test
    public void TrashCompactorDoesNotAllowPermanentPilotXwingDeployToDeathStarSystem() {
        // Permanent-pilot ships stay correctly blocked at Death Star system
        var scn = GetScenario();

        var trashCompactor = scn.GetLSCard("trashCompactor");
        var xwing = scn.GetLSCard("xwing");
        var deathStar = scn.GetDSCard("deathStar");
        var lsSystem = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathStar);
        scn.MoveLocationToTable(trashCompactor);
        scn.MoveCardsToLSHand(xwing);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(5);

        assertTrue(scn.LSDeployAvailable(xwing));
        scn.LSDeployCard(xwing);

        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(lsSystem));
        assertFalse(scn.LSHasCardChoiceAvailable(deathStar));
    }

    @Test
    public void TrashCompactorDoesNotAllowFalconAndPilotSimultaneousDeployToDeathStarSystem() {
        // #736 real bug path: empty Falcon + Captain Han from hand must not unlock Death Star system
        var scn = GetScenario();

        var trashCompactor = scn.GetLSCard("trashCompactor");
        var captainHan = scn.GetLSCard("captainHan");
        var falcon = scn.GetLSCard("falcon");
        var ccDockingBay = scn.GetLSCard("ccDockingBay");
        var deathStar = scn.GetDSCard("deathStar");

        setupFalconAndPilotReadyToDeploy(scn, deathStar, trashCompactor, ccDockingBay);

        beginFalconSimultaneousDeployWithPilot(scn, falcon, captainHan);

        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(ccDockingBay));
        assertFalse(scn.LSHasCardChoiceAvailable(deathStar));
        assertFalse(scn.LSHasCardChoiceAvailable(trashCompactor));
    }

    @Test
    public void TrashCompactorStillAllowsFalconAndPilotSimultaneousDeployToLegalSite() {
        // Legal contrast: Falcon + Captain Han may still deploy together to a Cloud City docking bay
        var scn = GetScenario();

        var trashCompactor = scn.GetLSCard("trashCompactor");
        var captainHan = scn.GetLSCard("captainHan");
        var falcon = scn.GetLSCard("falcon");
        var ccDockingBay = scn.GetLSCard("ccDockingBay");
        var deathStar = scn.GetDSCard("deathStar");

        setupFalconAndPilotReadyToDeploy(scn, deathStar, trashCompactor, ccDockingBay);

        beginFalconSimultaneousDeployWithPilot(scn, falcon, captainHan);

        assertTrue(scn.LSHasCardChoiceAvailable(ccDockingBay));
        assertFalse(scn.LSHasCardChoiceAvailable(deathStar));
        scn.LSChooseCard(ccDockingBay);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ccDockingBay, falcon));
        assertTrue(scn.IsAboardAsPilot(falcon, captainHan));
    }
}
