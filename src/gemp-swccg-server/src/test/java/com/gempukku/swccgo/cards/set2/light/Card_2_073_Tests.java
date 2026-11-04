package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Deploying without presence or Force icons (system, docking bay, spy pilot).
 */
public class Card_2_073_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tydirium", "8_079"); // Tydirium: may deploy (and characters aboard) even without presence or Force icons
                    put("tantive", "2_073"); // Tantive IV: may be deployed even without presence or Force icons
                    put("han", "1_011"); // Han Solo (non-spy pilot)
                    put("webb", "3_011"); // Jeroen Webb (spy pilot)
                    put("falcon", "1_143"); // Millennium Falcon (empty starfighter, no permanent pilot)
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
    public void TantiveIVMayDeployAloneToSystemWithoutPresenceOrForceIcons() {
        var scn = GetScenario();
        var tantive = scn.GetLSCard("tantive");
        var deathStar = scn.GetDSCard("deathStar");

        scn.StartGame();
        scn.MoveLocationToTable(deathStar);
        scn.MoveCardsToLSHand(tantive);
        scn.LSActivateForceCheat(12);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(tantive));
        scn.LSDeployCard(tantive);
        assertTrue(scn.LSHasCardChoiceAvailable(deathStar));
        scn.LSChooseCard(deathStar);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(deathStar, tantive));
    }
}
