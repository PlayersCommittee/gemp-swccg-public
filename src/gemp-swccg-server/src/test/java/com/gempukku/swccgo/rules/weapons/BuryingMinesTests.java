package com.gempukku.swccgo.rules.weapons;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Appendix C Mining Droid Rules — Burying Mines (issue #231).
 * Reuses under-site face-down stacking (same model as Tatooine: Bluffs).
 */
public class BuryingMinesTests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trooper", "1_027");
                }},
                new HashMap<>() {{
                    put("lin", "1_186");
                    put("infantryMine", "3_160");
                    put("dud", "1_249");
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
    public void BuryingMinesCanBuryCardFromHandUnderExteriorPlanetSiteDuringDeploy() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var lin = scn.GetDSCard("lin");
        var dud = scn.GetDSCard("dud");

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin);
        scn.MoveCardsToDSHand(dud);

        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.DSCardActionAvailable(site, "Bury card under site"));
        scn.DSUseCardAction(site, "Bury card under site");
        assertTrue(dud.isBuriedMine());
        assertEquals(Zone.STACKED_FACE_DOWN, dud.getZone());
        assertEquals(site, dud.getStackedOn());
    }

    @Test
    public void BuryingMinesCannotBuryWithoutMiningDroidPresent() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var dud = scn.GetDSCard("dud");

        scn.StartGame();
        scn.MoveCardsToDSHand(dud);
        scn.SkipToPhase(Phase.DEPLOY);

        assertFalse(scn.DSCardActionAvailable(site, "Bury card under site"));
    }

    @Test
    public void BuryingMinesCanBuryRealMineAsBuriedMine() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var lin = scn.GetDSCard("lin");
        var mine = scn.GetDSCard("infantryMine");

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin);
        scn.MoveCardsToDSHand(mine);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.DSCardActionAvailable(site, "Bury card under site"));
        scn.DSUseCardAction(site, "Bury card under site");
        assertTrue(mine.isBuriedMine());
        assertEquals(Zone.STACKED_FACE_DOWN, mine.getZone());
        assertEquals(site, mine.getStackedOn());
    }
}
