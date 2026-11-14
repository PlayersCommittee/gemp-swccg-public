package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tydirium: May deploy (and your characters may deploy aboard) even without presence or Force icons.
 */
public class Card_8_079_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tydirium", "8_079"); // Tydirium
                    put("han", "1_011"); // Han Solo (pilot)
                    put("trashCompactor", "1_125"); // Death Star: Trash Compactor
                }},
                new HashMap<>() {{
                    put("deathStar", "2_143"); // Death Star system (no Light Force icons)
                    put("coruscantDb", "7_276"); // Coruscant: Docking Bay (no Light Force icons)
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

    @Test
    public void TydiriumMayDeployAloneToDockingBayWithoutPresenceOrForceIcons() {
        var scn = GetScenario();
        var tydirium = scn.GetLSCard("tydirium");
        var dockingBay = scn.GetDSCard("coruscantDb");

        scn.StartGame();
        scn.MoveLocationToTable(dockingBay);
        scn.MoveCardsToLSHand(tydirium);
        scn.LSActivateForceCheat(10);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(tydirium));
        scn.LSDeployCard(tydirium);
        assertTrue(scn.LSHasCardChoiceAvailable(dockingBay));
        scn.LSChooseCard(dockingBay);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(dockingBay, tydirium));
    }

    @Test
    public void TydiriumMayNotDeployAloneToSystem() {
        // Unpiloted starship still needs a pilot deploying with it to go to a system.
        var scn = GetScenario();
        var tydirium = scn.GetLSCard("tydirium");
        var deathStar = scn.GetDSCard("deathStar");
        var dockingBay = scn.GetDSCard("coruscantDb");

        scn.StartGame();
        scn.MoveLocationToTable(deathStar);
        scn.MoveLocationToTable(dockingBay);
        scn.MoveCardsToLSHand(tydirium);
        scn.LSActivateForceCheat(10);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(tydirium));
        scn.LSDeployCard(tydirium);
        assertTrue(scn.LSHasCardChoiceAvailable(dockingBay));
        assertFalse(scn.LSHasCardChoiceAvailable(deathStar));
    }

    @Test
    public void TydiriumMayDeployWithPilotToSystemWithoutPresenceOrForceIcons() {
        var scn = GetScenario();
        var tydirium = scn.GetLSCard("tydirium");
        var han = scn.GetLSCard("han");
        var deathStar = scn.GetDSCard("deathStar");

        scn.StartGame();
        scn.MoveLocationToTable(deathStar);
        scn.MoveCardsToLSHand(tydirium, han);
        scn.LSActivateForceCheat(12);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(tydirium));
        scn.LSDeployCard(tydirium);
        assertTrue(scn.LSDecisionAvailable("simultaneously deploy aboard"));
        assertTrue(scn.LSHasCardChoiceAvailable(han));
        scn.LSChooseCard(han);
        assertTrue(scn.LSHasCardChoiceAvailable(deathStar));
        scn.LSChooseCard(deathStar);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(deathStar, tydirium));
        assertTrue(scn.IsAboardAsPilot(tydirium, han));
    }

    @Test
    public void TydiriumMayDeployWithPilotToSystemWhileTrashCompactorOnTable() {
        // Trash Compactor's site-only ignore must not interfere with Tydirium's own ability.
        var scn = GetScenario();
        var tydirium = scn.GetLSCard("tydirium");
        var han = scn.GetLSCard("han");
        var deathStar = scn.GetDSCard("deathStar");
        var trashCompactor = scn.GetLSCard("trashCompactor");

        scn.StartGame();
        scn.MoveLocationToTable(deathStar);
        scn.MoveLocationToTable(trashCompactor);
        scn.MoveCardsToLSHand(tydirium, han);
        scn.LSActivateForceCheat(12);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(tydirium));
        scn.LSDeployCard(tydirium);
        assertTrue(scn.LSDecisionAvailable("simultaneously deploy aboard"));
        assertTrue(scn.LSHasCardChoiceAvailable(han));
        scn.LSChooseCard(han);
        assertTrue(scn.LSHasCardChoiceAvailable(deathStar));
        scn.LSChooseCard(deathStar);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(deathStar, tydirium));
        assertTrue(scn.IsAboardAsPilot(tydirium, han));
    }

    @Test
    public void YourCharacterMayDeployAboardTydiriumAtSystemWithoutPresenceOrForceIcons() {
        var scn = GetScenario();
        var tydirium = scn.GetLSCard("tydirium");
        var han = scn.GetLSCard("han");
        var deathStar = scn.GetDSCard("deathStar");

        scn.StartGame();
        scn.MoveLocationToTable(deathStar);
        scn.MoveCardsToLocation(deathStar, tydirium);
        scn.MoveCardsToLSHand(han);
        scn.LSActivateForceCheat(10);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(han));
        scn.LSDeployCard(han);
        assertTrue(scn.LSHasCardChoiceAvailable(tydirium));
        scn.LSChooseCard(tydirium);
        assertTrue(scn.LSChoiceAvailable("Pilot"));
        scn.LSChoose("Pilot");
        scn.PassAllResponses();

        assertTrue(scn.IsAboardAsPilot(tydirium, han));
    }
}
