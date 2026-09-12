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
                }},
                new HashMap<>() {{
                    put("nsp", "6_157");
                    put("dsLando", "5_099");
                }},
                40,
                40,
                StartingSetup.LSStartingLocation("6_082"), // Jabba's Palace: Entrance Cavern
                StartingSetup.DSStartingLocation("5_166"), // Cloud City: Carbonite Chamber
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetSpaceScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("falcon", "1_143");
                    put("gold1", "9_068");
                    put("hcf", "13_021");
                }},
                new HashMap<>() {{
                    put("nsp", "6_157");
                }},
                40,
                40,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private void playNoneShallPassAfterDeploy(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl rebel,
                                              com.gempukku.swccgo.game.PhysicalCardImpl site,
                                              com.gempukku.swccgo.game.PhysicalCardImpl nsp) {
        scn.LSDeployCard(rebel);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        scn.LSChooseCard(site);
        scn.PassForceUseResponses();
        assertTrue(scn.DSPlayUsedInterruptAvailable(nsp));
        scn.DSPlayUsedInterrupt(nsp);
        if (scn.DSDecisionAvailable("Choose Rebel")) {
            scn.DSChooseCard(rebel);
        }
        scn.PassAllResponses();
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
        assertTrue(scn.AwaitingLSDeployPhaseActions());
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
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(lukeJedi));
        assertFalse(scn.LSDeployAvailable(luke));
    }

    @Test
    public void PersonaTurnLimitBlocksGoldSquadron1AfterMillenniumFalcon() {
        var scn = GetSpaceScenario();

        var falcon = scn.GetLSCard("falcon");
        var gold1 = scn.GetLSCard("gold1");
        var system = scn.GetLSStartingLocation();

        scn.MoveCardsToLSHand(falcon, gold1);
        scn.StartGame();

        scn.LSActivateForceCheat(20);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(falcon));
        scn.LSDeployCardAndPassResponses(falcon, system);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(gold1));
    }

    @Test
    public void PersonaTurnLimitBlocksGoldSquadron1AfterHanChewieAndTheFalcon() {
        var scn = GetSpaceScenario();

        var hcf = scn.GetLSCard("hcf");
        var gold1 = scn.GetLSCard("gold1");
        var system = scn.GetLSStartingLocation();

        scn.MoveCardsToLSHand(hcf, gold1);
        scn.StartGame();

        scn.LSActivateForceCheat(20);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(hcf));
        scn.LSDeployCardAndPassResponses(hcf, system);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
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

        assertTrue(scn.LSDeployAvailable(lando));
        scn.LSDeployCardAndPassResponses(lando, lsSite);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(tamtel));

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(10);
        assertTrue(scn.DSDeployAvailable(dsLando));
        scn.DSDeployCardAndPassResponses(dsLando, dsSite);
        assertTrue(scn.CardsAtLocation(dsSite, dsLando));
    }
}
