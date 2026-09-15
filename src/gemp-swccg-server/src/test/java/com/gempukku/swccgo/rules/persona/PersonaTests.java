package com.gempukku.swccgo.rules.persona;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Engine persona uniqueness: per player, a unique persona already played this turn
 * may not be deployed again (other titles). Opponent may still play that persona
 * (AR: each player may have the same persona on table; Ice Storm global per-turn
 * uniqueness is same title, both sides).
 */
public class PersonaTests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tamtel", "6_042");
                    put("leia", "1_17");
                    put("boushh", "110_001");
                }},
                new HashMap<>() {{
                    put("dsLando", "5_099");
                }},
                40,
                40,
                StartingSetup.LSStartingLocation("6_082"),
                StartingSetup.DSStartingLocation("5_166"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private void recoverToLSDeploy(VirtualTableScenario scn) {
        for (int i = 0; i < 20 && !scn.AwaitingLSDeployPhaseActions(); i++) {
            String text = scn.GetCurrentDecision().getText().toLowerCase();
            if (text.contains("optional")) {
                scn.PassAllResponses();
            } else if (scn.LSAnyActionsAvailable() || scn.LSDecisionAvailable("Pass")) {
                scn.LSPass();
            } else if (scn.DSAnyActionsAvailable() || scn.DSDecisionAvailable("Pass")) {
                scn.DSPass();
            } else {
                break;
            }
        }
        assertTrue("Expected LS deploy phase; decision=" + scn.GetCurrentDecision().getText(),
                scn.AwaitingLSDeployPhaseActions());
    }

    private void deployAndPass(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl location) {
        recoverToLSDeploy(scn);
        assertTrue(scn.LSDeployAvailable(card));
        scn.LSDeployCard(card);
        if (scn.LSHasCardChoiceAvailable(location)) {
            scn.LSChooseCard(location);
        } else if (scn.LSDecisionAvailable("Choose")) {
            scn.LSChooseAnyCard();
        }
        scn.PassAllResponses();
        recoverToLSDeploy(scn);
    }

    @Test
    public void OpponentMayDeploySamePersonaDifferentTitleSameTurn() {
        // AR: both players may have the same persona on table (different titles).
        // Ice Storm global per-turn uniqueness is same title. Persona recording is per player,
        // so LS Tamtel this turn does not block DS Lando (Comlink react during LS battle).
        var scn = GetScenario();
        var tamtel = scn.GetLSCard("tamtel");
        var dsLando = scn.GetDSCard("dsLando");
        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLSHand(tamtel);
        scn.MoveCardsToDSHand(dsLando);
        scn.StartGame();
        scn.LSActivateForceCheat(20);
        scn.SkipToLSTurn(Phase.DEPLOY);

        deployAndPass(scn, tamtel, site);

        assertTrue(scn.game().getModifiersQuerying().isPersonaPlayedThisTurn(scn.LS, Persona.LANDO));
        assertFalse(scn.game().getModifiersQuerying().isPersonaPlayedThisTurn(scn.DS, Persona.LANDO));
        assertFalse("DS Lando must not be blocked by LS Tamtel this turn",
                scn.game().getModifiersQuerying().isPlayingCardTitleTurnLimitReached(scn.gameState(), dsLando));
    }

    @Test
    public void PersonaPlayedThisTurnBlocksOtherTitleAfterUniqueLeavesTable() {
        var scn = GetScenario();
        var leia = scn.GetLSCard("leia");
        var boushh = scn.GetLSCard("boushh");
        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLSHand(leia, boushh);
        scn.StartGame();
        scn.LSActivateForceCheat(20);
        scn.SkipToLSTurn(Phase.DEPLOY);

        deployAndPass(scn, leia, site);
        scn.MoveCardsToLSHand(leia);
        recoverToLSDeploy(scn);

        assertEquals(Zone.HAND, leia.getZone());
        assertFalse(scn.LSDeployAvailable(leia));
        assertFalse(scn.LSDeployAvailable(boushh));
    }
}
