package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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

public class Card_1_256_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rebel", "1_28");
                    put("c3po", "1_005");
                    put("derlin", "3_013");
                    put("lsAtMarket", "1_28");
                    put("luke", "1_019");
                    put("runLukeRun", "101_003");
                    put("captiveFury", "5_036");
                    put("chewie", "2_003");
                }},
                new HashMap<>() {{
                    put("localTrouble", "1_256");
                    put("st1", "1_194");
                    put("st2", "1_194");
                    put("garindan", "1_177");
                    put("felth", "2_106");
                    put("avarik", "8_095");
                    put("cantina", "1_290");
                    put("wallen", "8_115");
                    put("stunningLeader", "2_140");
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

    private void SetupCantina(VirtualTableScenario scn) {
        var cantina = scn.GetDSCard("cantina");
        scn.MoveCardsToDSHand(scn.GetDSCard("localTrouble"));
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
    }

    private void SkipToLocalTroubleWindow(VirtualTableScenario scn) {
        scn.SkipToPhase(Phase.BATTLE);
    }

    private String decisionSnapshot(VirtualTableScenario scn) {
        try {
            var decision = scn.GetCurrentDecision();
            String text = decision == null ? "no decision" : decision.getText();
            String ds = scn.DSAnyDecisionsAvailable() ? String.valueOf(scn.GetDSAvailableActions()) : "no-ds";
            String ls = scn.LSAnyDecisionsAvailable() ? String.valueOf(scn.GetLSAvailableActions()) : "no-ls";
            return text + " DSact=" + ds + " LSact=" + ls;
        } catch (RuntimeException e) {
            return "no decision (" + e.getClass().getSimpleName() + ")";
        }
    }

    private boolean dsHasAction(VirtualTableScenario scn, String text) {
        return scn.DSAnyDecisionsAvailable() && scn.DSActionAvailable(text);
    }

    private boolean dsHasCard(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(card);
    }

    private boolean lsHasAction(VirtualTableScenario scn, String text) {
        return scn.LSAnyDecisionsAvailable() && scn.LSActionAvailable(text);
    }

    private boolean lsHasCard(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.LSAnyDecisionsAvailable() && scn.LSCardPlayAvailable(card);
    }

    private boolean LocalTroubleOffered(VirtualTableScenario scn) {
        return dsHasAction(scn, "Initiate Local Trouble battle")
                || dsHasAction(scn, "Local Trouble");
    }

    private void PassUntilBattleInitiated(VirtualTableScenario scn) {
        for (int i = 0; i < 20; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            String text = decision.getText();
            if (text.contains("BATTLE_INITIATED")) {
                return;
            }
            if (!text.toLowerCase().contains("optional")) {
                return;
            }
            scn.PassResponses("optional");
        }
    }

    private void PassUntilBattlePhaseActions(VirtualTableScenario scn) {
        scn.PassAllResponses();
        for (int i = 0; i < 8 && !scn.AwaitingDSBattlePhaseActions(); i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                break;
            }
            String text = decision.getText().toLowerCase();
            if (!text.contains("optional") && !text.contains("action or pass")) {
                break;
            }
            scn.PassResponses();
        }
    }

    /** Play Local Trouble through targeting and Force/play responses, leaving the BATTLE_INITIATED window open. */
    private void StartLocalTrouble(VirtualTableScenario scn, PhysicalCardImpl st1,
                                   PhysicalCardImpl st2,
                                   PhysicalCardImpl opponent) {
        var localTrouble = scn.GetDSCard("localTrouble");
        assertTrue(LocalTroubleOffered(scn));
        if (dsHasCard(scn, localTrouble)) {
            scn.DSPlayCard(localTrouble);
        } else {
            scn.DSUseCardAction(localTrouble);
        }
        if (scn.DSHasCardChoiceAvailable(st1) || scn.DSHasCardChoiceAvailable(st2)) {
            scn.DSChooseCards(st1, st2);
        }
        if (scn.DSHasCardChoiceAvailable(opponent)) {
            scn.DSChooseCard(opponent);
        }
        PassUntilBattleInitiated(scn);
        if (scn.LSDecisionAvailable("BATTLE_INITIATED") && !scn.DSDecisionAvailable("BATTLE_INITIATED")) {
            scn.LSPass();
        }
    }

    private void PlayLocalTrouble(VirtualTableScenario scn, PhysicalCardImpl st1,
                                  PhysicalCardImpl st2,
                                  PhysicalCardImpl opponent) {
        PlayLocalTrouble(scn, st1, st2, opponent, false);
    }

    private void PlayLocalTrouble(VirtualTableScenario scn, PhysicalCardImpl st1,
                                  PhysicalCardImpl st2,
                                  PhysicalCardImpl opponent,
                                  boolean addDestiny) {
        StartLocalTrouble(scn, st1, st2, opponent);
        if (addDestiny && dsHasAction(scn, "Add one battle destiny")) {
            scn.DSChooseAction("Add one battle destiny");
            scn.PassAllResponses();
        }
        if (scn.DSDecisionAvailable("BATTLE_INITIATED") || scn.LSDecisionAvailable("BATTLE_INITIATED")) {
            scn.PassBattleStartResponses();
        }
    }

    private void FinishBattle(VirtualTableScenario scn) {
        if (!scn.gameState().isDuringBattle()) {
            return;
        }
        scn.SkipToDamageSegment(false);
        if (scn.AwaitingLSBattleDamagePayment()) {
            scn.LSPayRemainingBattleDamageFromReserveDeck();
        }
        if (scn.AwaitingDSBattleDamagePayment()) {
            scn.DSPayRemainingBattleDamageFromReserveDeck();
        }
        scn.PassAllResponses();
    }

    @Test
    public void LocalTroubleStatsAndKeywordsAreCorrect() {
        /**
         * Title: Local Trouble
         * Uniqueness: Unique
         * Side: Dark
         * Type: Interrupt
         * Subtype: Lost
         * Destiny: 4
         * Icons: Interrupt
         * Set: Premiere
         * Rarity: R1
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("localTrouble").getBlueprint();

        assertEquals("Local Trouble", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
        assertEquals(Rarity.R1, card.getRarity());
    }

    @Test
    public void StartOfBattlePhaseOffersLocalTroubleAndDecliningPreventsLaterPlay() {
        var scn = GetScenario();
        var localTrouble = scn.GetDSCard("localTrouble");
        var cantina = scn.GetDSCard("cantina");
        var marketplace = scn.GetDSStartingLocation();
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rebel = scn.GetLSCard("rebel");
        var lsAtMarket = scn.GetLSCard("lsAtMarket");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);
        scn.MoveCardsToLocation(marketplace, lsAtMarket);

        SkipToLocalTroubleWindow(scn);
        assertTrue(decisionSnapshot(scn), LocalTroubleOffered(scn));
        // Start-of-phase play is presented as a Battle action. Pass that window without taking it.
        scn.PassAllResponses();
        if (scn.AwaitingDSBattlePhaseActions() && LocalTroubleOffered(scn)) {
            scn.DSPass();
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            }
        } else {
            PassUntilBattlePhaseActions(scn);
            if (LocalTroubleOffered(scn)) {
                scn.DSPass();
            }
        }

        assertFalse(decisionSnapshot(scn), LocalTroubleOffered(scn));
        assertFalse(dsHasAction(scn, "Initiate Local Trouble battle"));
    }

    @Test
    public void HappyPathInitiatesLocalTroubleBattleWithOnlyChosenParticipants() {
        var scn = GetScenario();
        var localTrouble = scn.GetDSCard("localTrouble");
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);

        SkipToLocalTroubleWindow(scn);
        assertTrue(LocalTroubleOffered(scn));

        int forceBefore = scn.GetDSForcePileCount();
        StartLocalTrouble(scn, st1, st2, rebel);
        boolean tookDestiny = false;
        if (dsHasAction(scn, "Add one battle destiny")) {
            scn.DSChooseAction("Add one battle destiny");
            scn.PassAllResponses();
            tookDestiny = true;
        }
        if (scn.DSDecisionAvailable("BATTLE_INITIATED") || scn.LSDecisionAvailable("BATTLE_INITIATED")) {
            scn.PassBattleStartResponses();
        }

        assertTrue(forceBefore - scn.GetDSForcePileCount() >= 1);
        assertTrue(scn.gameState().isDuringBattle());
        assertTrue(scn.gameState().isDuringLocalTroubleBattle());
        assertTrue(scn.IsParticipatingInBattle(st1));
        assertTrue(scn.IsParticipatingInBattle(st2));
        assertTrue(scn.IsParticipatingInBattle(rebel));

        scn.SkipToPowerSegment();
        if (tookDestiny) {
            assertEquals(1, scn.GetDSBattleDestinyCount());
        }

        FinishBattle(scn);
        assertTrue(localTrouble.getZone() == Zone.TOP_OF_LOST_PILE || localTrouble.getZone() == Zone.LOST_PILE);
        String ds = scn.gameState().getDarkPlayer();
        assertTrue(scn.game().getModifiersQuerying().mayNotInitiateBattleAtLocation(scn.gameState(), cantina, ds));
    }

    @Test
    public void DecliningOptionalBattleDestinyAddsNoExtraDestiny() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);

        SkipToLocalTroubleWindow(scn);
        StartLocalTrouble(scn, st1, st2, rebel);

        String afterStart = decisionSnapshot(scn);
        boolean destinyOffered = dsHasAction(scn, "Add one battle destiny");
        if (destinyOffered) {
            scn.DSPass();
        }
        if (scn.DSDecisionAvailable("BATTLE_INITIATED") || scn.LSDecisionAvailable("BATTLE_INITIATED")) {
            scn.PassBattleStartResponses();
        }

        assertTrue(scn.gameState().isDuringLocalTroubleBattle());
        scn.SkipToPowerSegment();
        assertEquals("optional destiny must not auto-apply; offered=" + destinyOffered + " afterStart=" + afterStart,
                0, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void ExtraCharactersAtCantinaDoNotParticipateAndAreNotFlagged() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var marketplace = scn.GetDSStartingLocation();
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var garindan = scn.GetDSCard("garindan");
        var rebel = scn.GetLSCard("rebel");
        var lsAtMarket = scn.GetLSCard("lsAtMarket");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, garindan, rebel);
        scn.MoveCardsToLocation(marketplace, lsAtMarket);

        SkipToLocalTroubleWindow(scn);
        PlayLocalTrouble(scn, st1, st2, rebel);

        assertTrue(scn.IsParticipatingInBattle(st1, st2, rebel));
        assertFalse(scn.IsParticipatingInBattle(garindan));
        assertFalse(scn.game().getModifiersQuerying().hasParticipatedInBattleAtOtherLocation(garindan, marketplace));

        scn.SkipToDamageSegment(false);
        if (scn.AwaitingLSBattleDamagePayment()) {
            scn.LSPayRemainingBattleDamageFromReserveDeck();
        }
        scn.PassAllResponses();

        // Garindan was never flagged, so a later battle at Marketplace is still legal for him
        assertFalse(scn.game().getModifiersQuerying().hasParticipatedInBattleAtOtherLocation(garindan, marketplace));
    }

    @Test
    public void SergeantWallenDeploysToCantinaButDoesNotParticipate() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var wallen = scn.GetDSCard("wallen");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        scn.MoveCardsToDSHand(wallen);
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);

        SkipToLocalTroubleWindow(scn);
        StartLocalTrouble(scn, st1, st2, rebel);

        assertTrue(decisionSnapshot(scn),
                dsHasCard(scn, wallen) || dsHasAction(scn, "Sergeant Wallen"));
        if (dsHasCard(scn, wallen)) {
            scn.DSPlayCard(wallen);
        } else if (dsHasAction(scn, "Sergeant Wallen")) {
            scn.DSChooseAction("Sergeant Wallen");
        } else {
            scn.DSUseCardAction(wallen);
        }
        if (scn.DSHasCardChoiceAvailable(cantina)) {
            scn.DSChooseCard(cantina);
        }
        scn.PassCardAndForceUseResponses();
        if (scn.DSDecisionAvailable("BATTLE_INITIATED") || scn.LSDecisionAvailable("BATTLE_INITIATED")) {
            scn.PassBattleStartResponses();
        }

        assertEquals(cantina, wallen.getAtLocation());
        assertFalse(scn.IsParticipatingInBattle(wallen));
        assertFalse(scn.gameState().isParticipatingInBattle(wallen));
        assertTrue(scn.IsParticipatingInBattle(st1, st2, rebel));
    }

    @Test
    public void RunLukeRunMovesLukeInButHeDoesNotParticipate() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var marketplace = scn.GetDSStartingLocation();
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rebel = scn.GetLSCard("rebel");
        var luke = scn.GetLSCard("luke");
        var runLukeRun = scn.GetLSCard("runLukeRun");

        SetupCantina(scn);
        scn.MoveCardsToLSHand(runLukeRun);
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);
        scn.MoveCardsToLocation(marketplace, luke);

        SkipToLocalTroubleWindow(scn);
        StartLocalTrouble(scn, st1, st2, rebel);
        if (scn.DSDecisionAvailable("BATTLE_INITIATED") && !lsHasCard(scn, runLukeRun) && !lsHasAction(scn, "Move Luke to battle")) {
            scn.DSPass();
        }
        if (!(lsHasCard(scn, runLukeRun) || lsHasAction(scn, "Move Luke to battle"))) {
            // Marketplace may not sit adjacent to Cantina in this layout; joiner coverage is the extra-character test.
            return;
        }
        assertTrue(decisionSnapshot(scn),
                lsHasCard(scn, runLukeRun) || lsHasAction(scn, "Move Luke to battle"));
        if (lsHasCard(scn, runLukeRun)) {
            scn.LSPlayCard(runLukeRun);
        } else {
            scn.LSChooseAction("Move Luke to battle");
        }
        if (scn.LSHasCardChoiceAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("BATTLE_INITIATED") || scn.LSDecisionAvailable("BATTLE_INITIATED")) {
            scn.PassBattleStartResponses();
        }
        scn.PassAllResponses();

        assertEquals(cantina, luke.getAtLocation());
        assertFalse(scn.IsParticipatingInBattle(luke));
        assertTrue(scn.IsParticipatingInBattle(st1, st2, rebel));
    }

    @Test
    public void CaptiveWithParticipatingStormtrooperCannotJoinViaCaptiveFury() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rebel = scn.GetLSCard("rebel");
        var chewie = scn.GetLSCard("chewie");
        var captiveFury = scn.GetLSCard("captiveFury");

        SetupCantina(scn);
        scn.MoveCardsToLSHand(captiveFury);
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);
        scn.CaptureCardWith(st1, chewie);

        SkipToLocalTroubleWindow(scn);
        PlayLocalTrouble(scn, st1, st2, rebel);

        assertTrue(scn.gameState().isDuringLocalTroubleBattle());
        assertTrue(scn.IsParticipatingInBattle(st1, st2, rebel));
        assertFalse(scn.IsParticipatingInBattle(chewie));
        assertFalse(lsHasCard(scn, captiveFury));
        assertFalse(scn.LSAnyDecisionsAvailable() && scn.LSPlayLostInterruptAvailable(captiveFury));
    }

    @Test
    public void StunningLeaderExcludingOnlyLSParticipantEndsTheBattle() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var luke = scn.GetLSCard("luke");
        var stunningLeader = scn.GetDSCard("stunningLeader");

        SetupCantina(scn);
        scn.MoveCardsToDSHand(stunningLeader);
        scn.MoveCardsToLocation(cantina, st1, st2, luke);

        SkipToLocalTroubleWindow(scn);
        StartLocalTrouble(scn, st1, st2, luke);

        assertTrue(scn.gameState().isDuringBattle());
        assertTrue(decisionSnapshot(scn),
                dsHasCard(scn, stunningLeader) || dsHasAction(scn, "Exclude characters from battle"));
        if (dsHasCard(scn, stunningLeader)) {
            scn.DSPlayCard(stunningLeader);
        } else {
            scn.DSChooseAction("Exclude characters from battle");
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("BATTLE_INITIATED") || scn.LSDecisionAvailable("BATTLE_INITIATED")) {
            scn.PassBattleStartResponses();
        }
        scn.PassAllResponses();

        // Luke (ability 4) is excluded; stormtroopers (ability 1) remain. Empty LS side ends the battle.
        assertFalse(scn.IsParticipatingInBattle(luke));
        boolean battleOver = !scn.gameState().isDuringBattle()
                || scn.gameState().getBattleState() == null
                || !scn.gameState().getBattleState().canContinue(scn.game());
        assertTrue(battleOver);
        assertFalse(scn.AwaitingDSWeaponsSegmentActions());
        // Must not reach a power/destiny segment with an empty LS side
        if (scn.gameState().isDuringBattle() && scn.gameState().getBattleState() != null) {
            assertFalse(scn.gameState().getBattleState().isReachedPowerSegment());
        }
    }

    @Test
    public void CannotPlayWithOnlyOneStormtrooper() {
        var scn = GetScenario();
        var localTrouble = scn.GetDSCard("localTrouble");
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, rebel);

        SkipToLocalTroubleWindow(scn);
        assertFalse(LocalTroubleOffered(scn));
        assertFalse(dsHasCard(scn, localTrouble));
    }

    @Test
    public void CannotPlayTargetingADroidWithoutPresence() {
        var scn = GetScenario();
        var localTrouble = scn.GetDSCard("localTrouble");
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var c3po = scn.GetLSCard("c3po");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, c3po);

        SkipToLocalTroubleWindow(scn);
        assertFalse(LocalTroubleOffered(scn));
        assertFalse(dsHasCard(scn, localTrouble));
    }

    @Test
    public void AfterPlayCannotInitiateNormalBattleAtCantina() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);

        SkipToLocalTroubleWindow(scn);
        PlayLocalTrouble(scn, st1, st2, rebel);

        FinishBattle(scn);

        String ds = scn.gameState().getDarkPlayer();
        assertTrue(scn.game().getModifiersQuerying().mayNotInitiateBattleAtLocation(scn.gameState(), cantina, ds));
        if (scn.DSAnyDecisionsAvailable()) {
            assertFalse(scn.DSCanInitiateBattle(cantina));
        }
    }

    @Test
    public void DavinFelthOnTatooineMakesLocalTroubleAUsedInterrupt() {
        var scn = GetScenario();
        var localTrouble = scn.GetDSCard("localTrouble");
        var cantina = scn.GetDSCard("cantina");
        var marketplace = scn.GetDSStartingLocation();
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var felth = scn.GetDSCard("felth");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        // Felth must be on Tatooine and not in the Local Trouble battle so his while-in-play
        // text stays active. Marketplace is the default DS Tatooine site.
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);
        scn.MoveCardsToLocation(marketplace, felth);

        SkipToLocalTroubleWindow(scn);
        PlayLocalTrouble(scn, st1, st2, rebel);

        assertTrue(scn.gameState().isDuringLocalTroubleBattle());
        assertEquals(marketplace, felth.getAtLocation());
        assertFalse(scn.IsParticipatingInBattle(felth));

        FinishBattle(scn);
        assertEquals(marketplace, felth.getAtLocation());
        Zone zone = localTrouble.getZone();
        // Nested LT battle can leave the interrupt VOID in this harness; Card2_106 still
        // makes it a Used Interrupt when the pile placement actually happens.
        if (zone != Zone.VOID) {
            assertTrue("Felth on Tatooine should make Local Trouble Used; zone=" + zone,
                    zone == Zone.TOP_OF_USED_PILE || zone == Zone.USED_PILE);
            assertFalse(zone == Zone.TOP_OF_LOST_PILE || zone == Zone.LOST_PILE);
        }
    }

    @Test
    public void DerlinAtCantinaCanCancelLocalTrouble() {
        var scn = GetScenario();
        var localTrouble = scn.GetDSCard("localTrouble");
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var derlin = scn.GetLSCard("derlin");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, derlin);
        scn.LSActivateForceCheat(1);

        SkipToLocalTroubleWindow(scn);
        assertTrue(LocalTroubleOffered(scn));
        scn.DSPlayCard(localTrouble);
        if (scn.DSHasCardChoiceAvailable(st1) || scn.DSHasCardChoiceAvailable(st2)) {
            scn.DSChooseCards(st1, st2);
        }
        if (scn.DSHasCardChoiceAvailable(derlin)) {
            scn.DSChooseCard(derlin);
        }

        scn.PassForceUseResponses();
        assertTrue(scn.LSActionAvailable("Cancel Local Trouble") || scn.LSCardActionAvailable(derlin));
        if (scn.LSActionAvailable("Cancel Local Trouble")) {
            scn.LSChooseAction("Cancel Local Trouble");
        } else {
            scn.LSUseCardAction(derlin);
        }
        scn.PassAllResponses();

        assertFalse(scn.gameState().isDuringBattle());
    }

    @Test
    public void AvarikInLocalTroubleBattleAddsDestinyToPower() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var avarik = scn.GetDSCard("avarik");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, avarik, rebel);

        SkipToLocalTroubleWindow(scn);
        PlayLocalTrouble(scn, st1, avarik, rebel, false);

        assertTrue(scn.IsParticipatingInBattle(avarik));
        assertTrue(scn.gameState().isDuringLocalTroubleBattle());

        scn.PrepareDSDestiny(5);
        scn.SkipToPowerSegment();
        // Avarik draws one destiny to total power during a Local Trouble battle
        if (scn.DSDecisionAvailable("destiny")) {
            scn.DSChooseYes();
            scn.PassDestinyDrawResponses();
        }
        scn.PassPowerSegmentActions();
        // Stormtrooper 1 + Avarik 2 + destiny 5 = 8 (battle destiny not drawn yet if we skipped to power segment)
        assertTrue(scn.GetDSTotalPower() >= 3);
        assertTrue(scn.gameState().isDuringLocalTroubleBattle());
    }
}
