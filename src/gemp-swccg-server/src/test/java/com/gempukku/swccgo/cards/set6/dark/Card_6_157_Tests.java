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
                    put("luke", "1_019"); // Luke Skywalker
                    put("lukeJedi", "9_024"); // Luke Skywalker, Jedi Knight
                    put("lando", "5_005"); // Lando Calrissian (LS)
                    put("tamtel", "6_042"); // Tamtel Skreej (Lando persona)
                    put("falcon", "1_143"); // Millennium Falcon
                    put("gold1", "9_068"); // Gold Squadron 1
                    put("hcf", "13_021"); // Han, Chewie, And The Falcon
                    put("audience", "6_162"); // Jabba's Palace: Audience Chamber
                    put("tibrin", "6_087"); // space system for starships
                }},
                new HashMap<>() {{
                    put("nsp", "6_157"); // None Shall Pass
                    put("dsLando", "5_099"); // Lando Calrissian (DS)
                    put("cloudCity", "5_166"); // Cloud City: Downtown Plaza (or similar CC site)
                }},
                40,
                40,
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
    public void NoneShallPassStatsAndKeywordsAreCorrect() {
        /**
         * Title: None Shall Pass
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Interrupt
         * Subtype: Used
         * Destiny: 5
         * Game Text: If opponent just deployed a Rebel to a Jabba's Palace site, (and you have no Imperials at a
         *      Jabba's Palace site), return Rebel to opponents hand. Any Force used to deploy that Rebel remains used,
         *      and Rebel may not be deployed for the remainder of the turn.
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
        var audience = scn.GetLSCard("audience");
        var nsp = scn.GetDSCard("nsp");

        scn.StartGame();

        scn.MoveLocationToTable(audience);
        scn.MoveCardsToLSHand(luke);
        scn.MoveCardsToDSHand(nsp);

        scn.LSActivateForceCheat(10);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(luke));
        scn.LSDeployCard(luke);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        scn.LSChooseCard(audience);

        // After-deploy window: DS plays None Shall Pass
        assertTrue(scn.DSPlayUsedInterruptAvailable(nsp));
        scn.DSPlayUsedInterrupt(nsp);
        if (scn.DSDecisionAvailable("Choose Rebel")) {
            scn.DSChooseCard(luke);
        }
        scn.PassAllResponses();

        assertEquals(Zone.HAND, luke.getZone());
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(luke));
    }

    @Test
    public void NoneShallPassBlocksOtherPersonaOfTargetedRebel() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var lukeJedi = scn.GetLSCard("lukeJedi");
        var audience = scn.GetLSCard("audience");
        var nsp = scn.GetDSCard("nsp");

        scn.StartGame();

        scn.MoveLocationToTable(audience);
        scn.MoveCardsToLSHand(luke, lukeJedi);
        scn.MoveCardsToDSHand(nsp);

        scn.LSActivateForceCheat(20);
        scn.SkipToPhase(Phase.DEPLOY);

        scn.LSDeployCard(luke);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        scn.LSChooseCard(audience);

        assertTrue(scn.DSPlayUsedInterruptAvailable(nsp));
        scn.DSPlayUsedInterrupt(nsp);
        if (scn.DSDecisionAvailable("Choose Rebel")) {
            scn.DSChooseCard(luke);
        }
        scn.PassAllResponses();

        assertEquals(Zone.HAND, luke.getZone());
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        // Same persona, different title may not redeploy this turn (#262)
        assertFalse(scn.LSDeployAvailable(lukeJedi));
        assertFalse(scn.LSDeployAvailable(luke));
    }

    @Test
    public void PersonaTurnLimitBlocksGoldSquadron1AfterMillenniumFalcon() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var gold1 = scn.GetLSCard("gold1");
        var tibrin = scn.GetLSCard("tibrin");

        scn.StartGame();

        scn.MoveLocationToTable(tibrin);
        scn.MoveCardsToLSHand(falcon, gold1);

        scn.LSActivateForceCheat(20);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(falcon));
        scn.LSDeployCardAndPassResponses(falcon, tibrin);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(gold1));
    }

    @Test
    public void PersonaTurnLimitBlocksGoldSquadron1AfterHanChewieAndTheFalcon() {
        var scn = GetScenario();

        var hcf = scn.GetLSCard("hcf");
        var gold1 = scn.GetLSCard("gold1");
        var tibrin = scn.GetLSCard("tibrin");

        scn.StartGame();

        scn.MoveLocationToTable(tibrin);
        scn.MoveCardsToLSHand(hcf, gold1);

        scn.LSActivateForceCheat(20);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(hcf));
        scn.LSDeployCardAndPassResponses(hcf, tibrin);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(gold1));
    }

    @Test
    public void PersonaTurnLimitBlocksOtherLandoPersonaSameTurnAndClearsNextTurn() {
        var scn = GetScenario();

        var lando = scn.GetLSCard("lando");
        var tamtel = scn.GetLSCard("tamtel");
        var dsLando = scn.GetDSCard("dsLando");
        var cloudCity = scn.GetDSCard("cloudCity");
        var audience = scn.GetLSCard("audience"); // Tatooine JP site for Tamtel

        scn.StartGame();

        scn.MoveLocationToTable(audience);
        scn.MoveLocationToTable(cloudCity);
        scn.MoveCardsToLSHand(lando, tamtel);
        scn.MoveCardsToDSHand(dsLando);

        scn.LSActivateForceCheat(20);
        scn.SkipToPhase(Phase.DEPLOY);

        // Deploy LS Lando; Tamtel (same persona, different title) blocked same turn
        assertTrue(scn.LSDeployAvailable(lando));
        scn.LSDeployCardAndPassResponses(lando, scn.GetLSStartingLocation());
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(tamtel));

        // After turn ends, persona list clears so DS may deploy their Lando
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(10);
        assertTrue(scn.DSDeployAvailable(dsLando));
    }
}
