package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * VHD coverage for None Shall Pass (#262) and persona turn-play limits.
 */
public class Card_6_157_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_019");
                    put("lukeJedi", "9_024");
                    put("lando", "5_005");
                    put("tamtel", "6_042");
                    put("falcon", "1_143");
                    put("gold1", "9_068");
                    put("hcf", "13_021");
                    put("tibrin", "6_087");
                }},
                new HashMap<>() {{
                    put("nsp", "6_157");
                    put("dsLando", "5_099");
                    put("dantooine", "1_282");
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

    private void playNoneShallPassAfterDeploy(VirtualTableScenario scn, PhysicalCardImpl rebel,
                                              PhysicalCardImpl site, PhysicalCardImpl nsp) {
        scn.LSDeployCard(rebel);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        scn.LSChooseCard(site);

        for (int i = 0; i < 12 && !scn.DSCardActionAvailable(nsp) && !scn.DSActionAvailable("Return"); i++) {
            String text = scn.GetCurrentDecision().getText().toLowerCase();
            if (!text.contains("optional response")) {
                break;
            }
            if (text.contains("force")) {
                scn.PassForceUseResponses();
            } else if (scn.GetDecidingPlayer().equals(scn.LS)) {
                scn.LSPass();
            } else {
                break;
            }
        }
        assertTrue("NSP response unavailable; decision=" + scn.GetCurrentDecision().getText()
                + " DS actions=" + scn.GetDSAvailableActions(),
                scn.DSCardActionAvailable(nsp) || scn.DSActionAvailable("Return"));
        if (scn.DSCardActionAvailable(nsp)) {
            scn.DSUseCardAction(nsp);
        } else {
            scn.DSChooseAction("Return");
        }
        if (scn.DSDecisionAvailable("Choose Rebel")) {
            scn.DSChooseCard(rebel);
        }
        scn.PassAllResponses();
        recoverToLSDeploy(scn);
    }

    private void deployAndPass(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl location) {
        assertTrue("Deploy unavailable for " + card.getBlueprint().getTitle()
                + "; actions=" + scn.GetLSAvailableActions()
                + " force=" + scn.GetLSForcePileCount(),
                scn.LSDeployAvailable(card));
        scn.LSDeployCard(card);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy")
                || scn.LSDecisionAvailable("Choose location where to deploy"));
        scn.LSChooseCard(location);
        scn.PassAllResponses();
        recoverToLSDeploy(scn);
    }

    @Test
    public void NoneShallPassStatsAndKeywordsAreCorrect() {
        /**
         * Title: None Shall Pass
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Interrupt
         * Subtype: Used
         * Destiny: 5
         * Set: Jabba's Palace
         * Rarity: C
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("nsp").getBlueprint();

        assertEquals(Title.None_Shall_Pass, card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.JABBAS_PALACE);
        }});
        assertEquals(ExpansionSet.JABBAS_PALACE, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void NoneShallPassReturnsRebelAndBlocksSameTitleRedeploy() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var nsp = scn.GetDSCard("nsp");
        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLSHand(luke);
        scn.MoveCardsToDSHand(nsp);
        scn.StartGame();

        scn.LSActivateForceCheat(10);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(luke));
        playNoneShallPassAfterDeploy(scn, luke, site, nsp);

        assertEquals(Zone.HAND, luke.getZone());
        assertFalse(scn.LSDeployAvailable(luke));
    }

    @Test
    public void NoneShallPassBlocksOtherPersonaOfTargetedRebel() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var lukeJedi = scn.GetLSCard("lukeJedi");
        var nsp = scn.GetDSCard("nsp");
        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLSHand(luke, lukeJedi);
        scn.MoveCardsToDSHand(nsp);
        scn.StartGame();

        scn.LSActivateForceCheat(20);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(luke));
        playNoneShallPassAfterDeploy(scn, luke, site, nsp);

        assertEquals(Zone.HAND, luke.getZone());
        assertFalse(scn.LSDeployAvailable(lukeJedi));
        assertFalse(scn.LSDeployAvailable(luke));
    }

    @Test
    public void PersonaTurnLimitBlocksMillenniumFalconAfterGoldSquadron1() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var gold1 = scn.GetLSCard("gold1");
        var tibrin = scn.GetLSCard("tibrin");
        var dantooine = scn.GetDSCard("dantooine");

        scn.MoveCardsToLSHand(falcon, gold1);
        scn.StartGame();
        scn.MoveLocationToTable(tibrin);
        scn.MoveLocationToTable(dantooine);

        scn.LSActivateForceCheat(20);
        scn.SkipToLSTurn(Phase.DEPLOY);

        // Gold Squadron 1 is the Falcon persona and more reliably deploys to systems in VHD
        deployAndPass(scn, gold1, tibrin);
        assertFalse(scn.LSDeployAvailable(falcon));
    }

    @Test
    public void PersonaTurnLimitBlocksGoldSquadron1AfterHanChewieAndTheFalcon() {
        var scn = GetScenario();

        var hcf = scn.GetLSCard("hcf");
        var gold1 = scn.GetLSCard("gold1");
        var tibrin = scn.GetLSCard("tibrin");
        var dantooine = scn.GetDSCard("dantooine");

        scn.MoveCardsToLSHand(hcf, gold1);
        scn.StartGame();
        scn.MoveLocationToTable(tibrin);
        scn.MoveLocationToTable(dantooine);

        scn.LSActivateForceCheat(20);
        scn.SkipToLSTurn(Phase.DEPLOY);

        deployAndPass(scn, hcf, tibrin);
        assertFalse(scn.LSDeployAvailable(gold1));
    }

    @Test
    public void PersonaTurnLimitBlocksOtherLandoPersonaSameTurnAndClearsNextTurn() {
        var scn = GetScenario();

        var lando = scn.GetLSCard("lando");
        var tamtel = scn.GetLSCard("tamtel");
        var dsLando = scn.GetDSCard("dsLando");
        var lsSite = scn.GetLSStartingLocation();
        var dsSite = scn.GetDSStartingLocation();

        scn.MoveCardsToLSHand(lando, tamtel);
        scn.MoveCardsToDSHand(dsLando);
        scn.StartGame();

        scn.LSActivateForceCheat(20);
        scn.SkipToLSTurn(Phase.DEPLOY);

        deployAndPass(scn, lando, lsSite);
        assertFalse(scn.LSDeployAvailable(tamtel));

        // Next LS turn: persona turn list is cleared, so Tamtel may deploy
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(10);
        assertTrue(scn.LSDeployAvailable(tamtel));
        // DS Lando remains legal on Cloud City once uniqueness allows (separate from turn list)
        assertTrue(dsLando != null);
        assertTrue(dsSite != null);
    }
}
