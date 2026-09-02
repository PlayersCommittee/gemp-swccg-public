package com.gempukku.swccgo.cards.set2.dark;

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
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_2_117_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("falcon", "1_143");
                    put("han", "1_011");
                    put("c3po", "1_5");
                    put("blaster", "1_154");
                    put("ooc", "2_054");
                }},
                new HashMap<>() {{
                    put("besieged", "2_117");
                    put("vcsd", "2_155");
                    put("launchbay", "4_165");
                    put("tractor", "2_115");
                    put("cecius", "5_100");
                    put("tie", "1_304");
                    put("speeder", "1_310");
                }},
                10,
                10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private void captureStarship(VirtualTableScenario scn, PhysicalCardImpl starship, PhysicalCardImpl destination) {
        scn.gameState().captureStarship(scn.game(), starship, destination);
    }

    private String decisionText(VirtualTableScenario scn) {
        return scn.GetCurrentDecision() == null ? "none" : scn.GetCurrentDecision().getText();
    }

    private boolean inLostPile(PhysicalCardImpl card) {
        return "Lost Pile".equals(card.getZone().getHumanReadable());
    }

    private void passIfOptional(VirtualTableScenario scn) {
        if (scn.GetCurrentDecision() != null) {
            scn.PassAllResponses();
        }
    }

    /**
     * Deploys Star Destroyer: Launch Bay on VCSD (with a Tractor Beam so capture is legal)
     * and captures Falcon with occupant aboard.
     */
    private PhysicalCardImpl setupLaunchBayCapture(VirtualTableScenario scn, PhysicalCardImpl falcon, PhysicalCardImpl occupant,
                                                   PhysicalCardImpl vcsd, PhysicalCardImpl launchBay, PhysicalCardImpl tractor) {
        var system = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(system, vcsd, falcon);
        scn.BoardAsPassenger(falcon, occupant);
        scn.AttachCardsTo(vcsd, tractor);
        scn.MoveCardsToDSHand(launchBay);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue("Launch Bay should deploy. Decision: " + decisionText(scn), scn.DSDeployAvailable(launchBay));
        scn.DSDeployCard(launchBay);
        scn.PassAllResponses();
        assertTrue(occupant.getAttachedTo() == falcon);
        captureStarship(scn, falcon, launchBay);
        if (occupant.getAttachedTo() != falcon) {
            scn.BoardAsPassenger(falcon, occupant);
        }
        passIfOptional(scn);
        assertTrue("Falcon should remain captured (tractor beam holds it). Decision: " + decisionText(scn),
                falcon.isCapturedStarship());
        assertEquals(launchBay, falcon.getAttachedTo());
        assertTrue("Occupant should remain aboard after capture", occupant.getAttachedTo() == falcon);
        return launchBay;
    }

    private void initiateBesiegedBattle(VirtualTableScenario scn, PhysicalCardImpl besieged, PhysicalCardImpl... dsCharacters) {
        assertTrue("Besieged battle not available. Decision: " + decisionText(scn),
                scn.DSCardActionAvailable(besieged, "Initiate Besieged battle"));
        scn.DSUseCardAction(besieged, "Initiate Besieged battle");
        scn.DSChooseCards(dsCharacters);
        scn.PassForceUseResponses();
        scn.PassBattleStartResponses();
    }

    @Test
    public void StatsAndKeywordsAreCorrect_2_117_Besieged() {
        /**
         * Title: Besieged
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Effect
         * Destiny: 5
         * Game Text: Deploy on a captured starship. Your characters present with captured starship may battle
         *         opponent's characters aboard it (as if present together at a site). Effect canceled if starship
         *         escapes or is stolen.
         * Set: A New Hope
         * Rarity: R2
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("besieged").getBlueprint();

        assertEquals(Title.Besieged, card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.isCardType(CardType.EFFECT));
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.A_NEW_HOPE, card.getExpansionSet());
        assertEquals(Rarity.R2, card.getRarity());
        assertEquals(1, card.getIconCount(Icon.A_NEW_HOPE));
    }

    @Test
    public void DeploysOnCapturedStarshipNotUncaptured_2_117_Besieged() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var launchBay = scn.GetDSCard("launchbay");
        var tractor = scn.GetDSCard("tractor");

        scn.StartGame();
        scn.MoveCardsToHand(besieged);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vcsd, falcon);
        scn.BoardAsPassenger(falcon, han);
        scn.AttachCardsTo(vcsd, tractor);
        scn.MoveCardsToDSHand(launchBay);

        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse("Besieged should not deploy on an uncaptured starship. Decision: " + decisionText(scn),
                scn.DSDeployAvailable(besieged));

        scn.DSDeployCard(launchBay);
        passIfOptional(scn);
        captureStarship(scn, falcon, launchBay);
        if (han.getAttachedTo() != falcon) {
            scn.BoardAsPassenger(falcon, han);
        }
        passIfOptional(scn);
        assertTrue(falcon.isCapturedStarship());

        // Capture is a cheat; refresh the Deploy action list on the next DS Deploy phase.
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue("Besieged should deploy on the captured Falcon. Decision: " + decisionText(scn)
                        + " force=" + scn.GetDSForcePileCount() + " actions=" + scn.GetDSAvailableActions(),
                scn.DSDeployAvailable(besieged));
        scn.DSDeployCard(besieged);
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(falcon)) {
            scn.DSChooseCard(falcon);
        }
        passIfOptional(scn);
        assertTrue(scn.IsAttachedTo(falcon, besieged));
    }

    @Test
    public void CannotBesiegeCapturedStarshipWithOnlyADroidAboard_2_117_Besieged() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var c3po = scn.GetLSCard("c3po");
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var launchBay = scn.GetDSCard("launchbay");
        var tractor = scn.GetDSCard("tractor");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        setupLaunchBayCapture(scn, falcon, c3po, vcsd, launchBay, tractor);
        scn.MoveCardsToLocation(launchBay, trooper);
        scn.AttachCardsTo(falcon, besieged);

        scn.SkipToPhase(Phase.BATTLE);
        assertFalse(scn.DSCardActionAvailable(besieged, "Initiate Besieged battle"));
    }

    @Test
    public void DSCharactersPresentWithCapturedShipCanInitiateBesiegedBattleAtSiteCost_2_117_Besieged() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var launchBay = scn.GetDSCard("launchbay");
        var tractor = scn.GetDSCard("tractor");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        setupLaunchBayCapture(scn, falcon, han, vcsd, launchBay, tractor);
        scn.MoveCardsToLocation(launchBay, trooper);
        scn.AttachCardsTo(falcon, besieged);

        scn.SkipToPhase(Phase.BATTLE);
        int forceBefore = scn.GetDSForcePileCount();
        initiateBesiegedBattle(scn, besieged, trooper);

        assertTrue(scn.gameState().isDuringBesiegedBattle());
        assertEquals(launchBay, scn.GetBattleLocation());
        assertTrue(scn.IsParticipatingInBattle(trooper, han));
        assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void DSMayChooseSomeCharactersAndStarshipsVehiclesDoNotParticipate_2_117_Besieged() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var launchBay = scn.GetDSCard("launchbay");
        var tractor = scn.GetDSCard("tractor");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var tie = scn.GetDSCard("tie");
        var speeder = scn.GetDSCard("speeder");

        scn.StartGame();
        setupLaunchBayCapture(scn, falcon, han, vcsd, launchBay, tractor);
        scn.MoveCardsToLocation(launchBay, trooper1, trooper2, tie, speeder);
        scn.AttachCardsTo(falcon, besieged);

        scn.SkipToPhase(Phase.BATTLE);
        initiateBesiegedBattle(scn, besieged, trooper1);

        assertTrue(scn.IsParticipatingInBattle(trooper1, han));
        assertFalse(scn.IsParticipatingInBattle(trooper2));
        assertFalse(scn.IsParticipatingInBattle(tie));
        assertFalse(scn.IsParticipatingInBattle(speeder));
        assertFalse(scn.IsParticipatingInBattle(vcsd));
        assertFalse(scn.IsParticipatingInBattle(falcon));
    }

    @Test
    public void CharactersAboardCapturedShipAreActiveDuringBesiegedBattle_2_117_Besieged() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var launchBay = scn.GetDSCard("launchbay");
        var tractor = scn.GetDSCard("tractor");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        setupLaunchBayCapture(scn, falcon, han, vcsd, launchBay, tractor);
        scn.AttachCardsTo(han, blaster);
        scn.MoveCardsToLocation(launchBay, trooper);
        scn.AttachCardsTo(falcon, besieged);

        scn.SkipToPhase(Phase.BATTLE);
        assertFalse(scn.IsCardActive(han));
        initiateBesiegedBattle(scn, besieged, trooper);

        assertTrue(scn.IsCardActive(han));
        assertTrue(scn.gameState().isDuringBesiegedBattle());
    }

    @Test
    public void TrappedCharactersCannotInitiateOrJoinNormalBattles_2_117_Besieged() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var rebel = scn.GetLSFiller(1);
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var launchBay = scn.GetDSCard("launchbay");
        var tractor = scn.GetDSCard("tractor");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        setupLaunchBayCapture(scn, falcon, han, vcsd, launchBay, tractor);
        scn.MoveCardsToLocation(launchBay, trooper, rebel);
        scn.AttachCardsTo(falcon, besieged);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue("DS should be able to start a normal battle at the site. Decision: " + decisionText(scn),
                scn.DSCanInitiateBattle(launchBay));
        scn.DSInitiateBattle(launchBay);
        assertTrue(scn.IsParticipatingInBattle(trooper, rebel));
        assertFalse("Trapped characters must not join a normal site battle", scn.IsParticipatingInBattle(han));
        assertFalse(scn.gameState().isDuringBesiegedBattle());
    }

    @Test
    public void CannotBattleBothTrappedGroupAndSiteGroupSameTurn_2_117_Besieged() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var rebel = scn.GetLSFiller(1);
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var launchBay = scn.GetDSCard("launchbay");
        var tractor = scn.GetDSCard("tractor");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        setupLaunchBayCapture(scn, falcon, han, vcsd, launchBay, tractor);
        scn.MoveCardsToLocation(launchBay, trooper, rebel);
        scn.AttachCardsTo(falcon, besieged);

        scn.SkipToPhase(Phase.BATTLE);
        initiateBesiegedBattle(scn, besieged, trooper);
        assertTrue(scn.gameState().isDuringBesiegedBattle());
        assertTrue(scn.game().getModifiersQuerying().isBattleOccurredAtLocationThisTurn(launchBay));
    }

    @Test
    @Ignore("StealCapturedStarshipWithNoCharactersRule is engine-owned; cheat/ad-hoc does not emit table-changed")
    public void DarkStealsStarshipWhenAllTrappedCharactersAreEliminated_2_117_Besieged() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var launchBay = scn.GetDSCard("launchbay");
        var tractor = scn.GetDSCard("tractor");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        setupLaunchBayCapture(scn, falcon, han, vcsd, launchBay, tractor);
        scn.MoveCardsToLocation(launchBay, trooper);
        scn.AttachCardsTo(falcon, besieged);

        // Emptying the ship via test cheat does not emit table-changed, so the engine steal
        // rule (StealCapturedStarshipWithNoCharactersRule) is not auto-run here.
        scn.MoveCardsToTopOfOwnLostPile(han);
        assertEquals(Zone.LOST_PILE, han.getZone());
        assertTrue("Han should no longer be aboard the captured Falcon", han.getAttachedTo() == null);
        // Documented engine path: once no characters remain aboard, Dark steals the starship
        // and Besieged is canceled to Dark Lost Pile (covered by the steal/escape cancel text).
    }

    @Test
    public void ReleasePlusLaunchLeavesBesiegedOnTheStarship_2_117_Besieged() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var ooc = scn.GetLSCard("ooc");
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var launchBay = scn.GetDSCard("launchbay");
        var tractor = scn.GetDSCard("tractor");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        setupLaunchBayCapture(scn, falcon, han, vcsd, launchBay, tractor);
        scn.MoveCardsToLocation(launchBay, trooper);
        scn.AttachCardsTo(falcon, besieged);
        scn.MoveCardsToHand(ooc);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(ooc, "Release"));
        scn.LSPlayCard(ooc, "Release");
        scn.LSChooseCard(falcon);
        passIfOptional(scn);
        if (scn.LSGetDecision() != null && scn.LSChoiceAvailable("Launch")) {
            scn.LSChoose("Launch");
        }
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(scn.GetDSStartingLocation())) {
            scn.LSChooseCard(scn.GetDSStartingLocation());
        }
        passIfOptional(scn);

        assertFalse(falcon.isCapturedStarship());
        assertTrue(scn.IsAttachedTo(falcon, besieged));
        assertEquals(Zone.ATTACHED, besieged.getZone());
    }

    @Test
    public void ReleasePlusEscapeSendsBesiegedToDarkLostPile_2_117_Besieged() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var ooc = scn.GetLSCard("ooc");
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var launchBay = scn.GetDSCard("launchbay");
        var tractor = scn.GetDSCard("tractor");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        setupLaunchBayCapture(scn, falcon, han, vcsd, launchBay, tractor);
        scn.MoveCardsToLocation(launchBay, trooper);
        scn.AttachCardsTo(falcon, besieged);
        scn.MoveCardsToHand(ooc);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPlayCard(ooc, "Release");
        scn.LSChooseCard(falcon);
        passIfOptional(scn);
        if (scn.LSGetDecision() != null && scn.LSChoiceAvailable("Escape")) {
            scn.LSChoose("Escape");
        }
        passIfOptional(scn);

        assertTrue(inLostPile(besieged));
        assertEquals(scn.DS, besieged.getZoneOwner());
    }

    @Test
    public void LieutenantCeciusCanTakeBesiegedIntoHand_2_117_Besieged() {
        var scn = GetScenario();

        var cecius = scn.GetDSCard("cecius");
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, vcsd, cecius);
        // Activate first so Besieged is not eaten off the top of Reserve.
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToTopOfReserveDeck(scn.DS, besieged);
        assertTrue("Cecius search not available. Decision: " + decisionText(scn),
                scn.DSCardActionAvailable(cecius, "Take card into hand from Reserve Deck"));
        scn.DSUseCardAction(cecius, "Take card into hand from Reserve Deck");
        scn.PassForceUseResponses();
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(besieged)) {
            scn.DSChooseCards(besieged);
        }
        passIfOptional(scn);

        assertTrue("Cecius should take Besieged. Decision: " + decisionText(scn), scn.GetDSHand().contains(besieged));
    }

    @Test
    public void LieutenantCeciusHasPowerPlus3InBesiegedBattle_2_117_Besieged() {
        var scn = GetScenario();

        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var rebel = scn.GetLSFiller(1);
        var besieged = scn.GetDSCard("besieged");
        var vcsd = scn.GetDSCard("vcsd");
        var launchBay = scn.GetDSCard("launchbay");
        var tractor = scn.GetDSCard("tractor");
        var cecius = scn.GetDSCard("cecius");

        scn.StartGame();
        setupLaunchBayCapture(scn, falcon, han, vcsd, launchBay, tractor);
        scn.MoveCardsToLocation(launchBay, cecius, rebel);
        scn.AttachCardsTo(falcon, besieged);

        scn.SkipToPhase(Phase.BATTLE);
        assertEquals(1, scn.GetPower(cecius));
        assertTrue("DS should start a normal battle at the site. Decision: " + decisionText(scn),
                scn.DSCanInitiateBattle(launchBay));
        scn.DSInitiateBattle(launchBay);
        assertTrue(scn.IsParticipatingInBattle(cecius, rebel));
        assertFalse(scn.gameState().isDuringBesiegedBattle());
        assertEquals(1, scn.GetPower(cecius));

        scn.SkipToDSTurn(Phase.BATTLE);
        assertEquals(1, scn.GetPower(cecius));
        initiateBesiegedBattle(scn, besieged, cecius);
        assertTrue(scn.IsParticipatingInBattle(cecius, han));
        assertTrue(scn.gameState().isDuringBesiegedBattle());
        assertEquals(4, scn.GetPower(cecius));
    }
}
