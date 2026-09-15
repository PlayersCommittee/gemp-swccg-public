package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Issue #999 / #911: Chewbacca's Bowcaster should fire for free while on Chewie.
 * Printed: "using 3 Force (for free if Chewie firing)."
 */
public class Card_8_086_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("chewie", "8_2");
                    put("bowcaster", "8_86");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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

    private void drainLsForce(VirtualTableScenario scn) {
        int leftover = scn.GetLSForcePileCount();
        if (leftover > 0) {
            scn.LSUseForceCheat(leftover);
        }
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void ChewbaccasBowcasterStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("bowcaster").getBlueprint();

        assertEquals(Title.Chewbaccas_Bowcaster, card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ENDOR);
            add(Icon.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BOWCASTER);
        }});
        assertEquals(ExpansionSet.ENDOR, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void ChewbaccasBowcasterFiresForFreeOnChewieAtZeroForce() {
        var scn = GetScenario();
        var chewie = scn.GetLSCard("chewie");
        var bowcaster = scn.GetLSCard("bowcaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, chewie, trooper);
        scn.AttachCardsTo(chewie, bowcaster);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        drainLsForce(scn);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue("Chewie firing must be free (printed 3 Force otherwise)",
                scn.LSCardActionAvailable(bowcaster, "Fire"));
    }

    /**
     * Table shaped like TacoBill$c63dcch5ipzik9df: Chewbacca Of Kashyyyk (V),
     * Chewbacca's Bowcaster, Mara Jade, Chirpa's Hut and Back Door.
     */
    protected VirtualTableScenario GetReplayScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("chewie", "227_5");
                    put("bowcaster", "8_86");
                    put("back_door", "8_69");
                }},
                new HashMap<>() {{
                    put("mara", "226_8");
                    put("trooper", "1_194");
                    put("force_field", "200_119");
                    put("potf", "1_227");
                }},
                20,
                20,
                StartingSetup.LSStartingLocation("8_71"),
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private float bowcasterFireCost(VirtualTableScenario scn, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getFireWeaponCost(
                scn.gameState(), scn.GetLSCard("bowcaster"), scn.GetLSCard("chewie"), target, 3);
    }

    private boolean bowcasterFiresForFreeModifierPresent(VirtualTableScenario scn) {
        return !scn.game().getModifiersQuerying().getModifiersAffectingCard(
                scn.gameState(), ModifierType.FIRES_FOR_FREE, scn.GetLSCard("bowcaster")).isEmpty();
    }

    private void finishBattleFromWeapons(VirtualTableScenario scn) {
        scn.SkipToDamageSegment(false);
        scn.PassAllResponses();
        for (int i = 0; i < 30; i++) {
            if (scn.AwaitingDSBattleDamagePayment()) {
                if (scn.GetDSForcePileCount() > 0) {
                    scn.DSPayBattleDamageFromForcePile();
                }
                else if (scn.GetDSReserveDeckCount() > 0) {
                    scn.DSPayBattleDamageFromReserveDeck();
                }
                else {
                    break;
                }
                scn.PassAllResponses();
                continue;
            }
            if (scn.AwaitingLSBattleDamagePayment()) {
                if (scn.GetLSForcePileCount() > 0) {
                    scn.LSPayBattleDamageFromForcePile();
                }
                else if (scn.GetLSReserveDeckCount() > 0) {
                    scn.LSPayBattleDamageFromReserveDeck();
                }
                else {
                    break;
                }
                scn.PassAllResponses();
                continue;
            }
            break;
        }
        scn.PassAllResponses();
        if (scn.AwaitingLSDamageSegmentActions() || scn.AwaitingDSDamageSegmentActions()) {
            scn.PassDamageSegmentActions();
        }
    }

    private void assertBowcasterStillFree(VirtualTableScenario scn, PhysicalCardImpl target, String when) {
        assertTrue("Fires for free modifier missing " + when, bowcasterFiresForFreeModifierPresent(scn));
        assertEquals("Bowcaster fire cost should be 0 " + when, 0f, bowcasterFireCost(scn, target), scn.epsilon);
    }

    @Test
    public void ChewbaccasBowcasterStillFreeAfterReplayShapedBattlesAndLandspeed() {
        var scn = GetReplayScenario();
        var chewie = scn.GetLSCard("chewie");
        var bowcaster = scn.GetLSCard("bowcaster");
        var backDoor = scn.GetLSCard("back_door");
        var mara = scn.GetDSCard("mara");
        var trooper = scn.GetDSCard("trooper");
        var hut = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(backDoor);
        scn.MoveCardsToLocation(hut, chewie, mara);
        scn.AttachCardsTo(chewie, bowcaster);
        scn.MoveCardsToLocation(backDoor, trooper);
        scn.MoveCardsToDSHand(scn.GetDSCard("force_field"));

        scn.SkipToLSTurn(Phase.BATTLE);
        assertBowcasterStillFree(scn, mara, "before first battle");
        scn.LSInitiateBattle(hut);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(bowcaster, "Fire"));
        scn.LSUseCardAction(bowcaster, "Fire");
        scn.LSChooseCard(mara);
        scn.PassAllResponses();
        if (scn.DSActionAvailable("Cancel weapon targeting")) {
            scn.DSChooseAction("Cancel weapon targeting");
            scn.PassAllResponses();
        }
        scn.DSPass();
        assertBowcasterStillFree(scn, mara, "after first Hut fire");
        finishBattleFromWeapons(scn);
        assertBowcasterStillFree(scn, mara, "after first Hut battle ends");

        scn.MoveCardsToLocation(backDoor, chewie);
        assertBowcasterStillFree(scn, mara, "after walking to Back Door");
        scn.MoveCardsToLocation(hut, chewie);
        assertBowcasterStillFree(scn, mara, "after walking back to Hut");
    }

    @Test
    public void ChewbaccasBowcasterStillFreeOnTurn6WithSnapshotsAndCardInfoQueries() {
        var scn = GetReplayScenario();
        var chewie = scn.GetLSCard("chewie");
        var bowcaster = scn.GetLSCard("bowcaster");
        var mara = scn.GetDSCard("mara");
        var hut = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(hut, chewie, mara);
        scn.AttachCardsTo(chewie, bowcaster);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(hut);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        for (int snap = 0; snap < 8; snap++) {
            scn.game().takeSnapshot("weapons query " + snap);
            scn.game().getModifiersQuerying().getModifiersAffecting(scn.gameState(), bowcaster);
            scn.game().getModifiersQuerying().getModifiersAffecting(scn.gameState(), chewie);
            assertBowcasterStillFree(scn, mara, "first battle snapshot " + snap);
        }
        assertTrue(scn.LSCardActionAvailable(bowcaster, "Fire"));
        finishBattleFromWeapons(scn);
        drainLsForce(scn);
        assertBowcasterStillFree(scn, mara, "after weapons-segment snapshots and battle end");
    }

    @Test
    public void ChewbaccasBowcasterStillFreeAfterPresenceOfTheForceIconQueries() {
        var scn = GetReplayScenario();
        var chewie = scn.GetLSCard("chewie");
        var bowcaster = scn.GetLSCard("bowcaster");
        var potf = scn.GetDSCard("potf");
        var mara = scn.GetDSCard("mara");
        var hut = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(hut, chewie, mara);
        scn.AttachCardsTo(chewie, bowcaster);
        scn.AttachCardsTo(hut, potf);

        scn.SkipToLSTurn(Phase.ACTIVATE);
        for (int turn = 0; turn < 5; turn++) {
            scn.gameState().getPlayersTotalForceGeneration(scn.LS);
            scn.gameState().getPlayersTotalForceGeneration(scn.DS);
            scn.GetIconCount(hut, Icon.LIGHT_FORCE);
            scn.GetIconCount(hut, Icon.DARK_FORCE);
            assertBowcasterStillFree(scn, mara, "after icon query turn " + turn);
            if (turn < 4) {
                scn.SkipToLSTurn(Phase.ACTIVATE);
            }
        }

        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(hut);
        drainLsForce(scn);
        assertTrue(scn.LSCardActionAvailable(bowcaster, "Fire"));
        assertBowcasterStillFree(scn, mara, "after POTF icon queries then battle");
    }

    /**
     * Play six Light Side turns for real: activate, deploy from hand, battle, fire, pass.
     * No SkipToTurn, no MoveCardsToLocation, no AttachCardsTo, no UseForceCheat.
     */
    @Test
    public void ChewbaccasBowcasterFiresForFreeAcrossSixPlayedTurns() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("chewie", "227_5");
                    put("bowcaster", "8_86");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                30,
                30,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var chewie = scn.GetLSCard("chewie");
        var bowcaster = scn.GetLSCard("bowcaster");
        var trooper = scn.GetDSCard("trooper");
        var hut = scn.GetLSStartingLocation();

        scn.MoveCardsToLSHand(chewie, bowcaster);
        scn.MoveCardsToDSHand(trooper);
        scn.StartGame();
        while (scn.GetCurrentPhase() == Phase.PLAY_STARTING_CARDS) {
            if (scn.LSDecisionAvailable("On which side")) {
                scn.LSChoose("Left");
            }
            else if (scn.DSDecisionAvailable("On which side")) {
                scn.DSChoose("Left");
            }
            else {
                scn.PassResponses();
            }
        }

        if (scn.AwaitingLSActivatePhaseActions()) {
            passRestOfTurn(scn);
        }
        assertTrue("Expected DS Activate, was " + scn.GetCurrentPlayer() + " " + scn.GetCurrentPhase()
                        + " " + scn.GetCurrentDecision().getText(),
                scn.AwaitingDSActivatePhaseActions());

        scn.DSActivateMaxForceAndPass();
        scn.PassControlActions();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSDeployAvailable(trooper));
        PhysicalCardImpl battleSite = hut;
        scn.DSDeployCardAndPassResponses(trooper, battleSite);
        passRestOfTurn(scn);

        scn.LSActivateMaxForceAndPass();
        scn.PassControlActions();
        scn.PassDeployActions();
        scn.PassMoveActions();
        scn.PassBattleActions();
        passRestOfTurn(scn);

        scn.DSActivateMaxForceAndPass();
        passRestOfTurn(scn);

        scn.LSActivateMaxForceAndPass();
        scn.PassControlActions();
        waitForLsDeployActions(scn);
        assertTrue("Chewie should be deployable after banking a turn of Force, was "
                        + decisionContext(scn) + " force=" + scn.GetLSForcePileCount(),
                scn.LSDeployAvailable(chewie));
        scn.LSDeployCard(chewie);
        if (scn.LSDecisionAvailable("Choose where to deploy") || scn.LSDecisionAvailable("Choose location")) {
            scn.LSChooseCard(battleSite);
        }
        settleCardPlay(scn);
        waitForLsDeployActions(scn);

        assertTrue("Bowcaster should deploy from hand onto Chewie, was " + decisionContext(scn),
                scn.LSDeployAvailable(bowcaster));
        scn.LSDeployCard(bowcaster);
        if (scn.LSHasCardChoiceAvailable(chewie)) {
            scn.LSChooseCard(chewie);
        }
        settleCardPlay(scn);
        assertTrue("Bowcaster must be on Chewie", scn.IsAttachedTo(chewie, bowcaster));
        scn.PassDeployActions();
        scn.PassMoveActions();

        playBattleAndConfirmFreeFire(scn, battleSite, bowcaster, trooper, "LS turn 2");
        passRestOfTurn(scn);

        for (int lsTurn = 3; lsTurn <= 6; lsTurn++) {
            assertTrue("DS should be activating before LS turn " + lsTurn + ", was " + decisionContext(scn),
                    scn.AwaitingDSActivatePhaseActions());
            scn.DSActivateMaxForceAndPass();
            passRestOfTurn(scn);

            assertTrue("LS turn " + lsTurn + " should be at Activate, was " + decisionContext(scn),
                    scn.AwaitingLSActivatePhaseActions());
            scn.LSActivateMaxForceAndPass();
            scn.PassControlActions();
            scn.PassDeployActions();
            scn.PassMoveActions();
            playBattleAndConfirmFreeFire(scn, battleSite, bowcaster, trooper, "LS turn " + lsTurn);
            passRestOfTurn(scn);
        }
    }

    private void playBattleAndConfirmFreeFire(VirtualTableScenario scn, PhysicalCardImpl site,
            PhysicalCardImpl bowcaster, PhysicalCardImpl target, String when) {
        assertTrue("Should be able to battle at " + when + ", was " + decisionContext(scn),
                scn.LSCanInitiateBattle(site));
        scn.LSInitiateBattle(site);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue("Fire offered " + when, scn.LSCardActionAvailable(bowcaster, "Fire"));
        assertBowcasterStillFree(scn, target, when);
        // Pass weapons rather than completing the shot so the Stormtrooper stays for later turns.
        finishBattleFromWeapons(scn);
    }

    private void settleCardPlay(VirtualTableScenario scn) {
        scn.PassAllResponses();
        if (scn.AnyDecisionsAvailable(scn.DS)) {
            scn.DSPass();
        }
        scn.PassAllResponses();
    }

    private void waitForLsDeployActions(VirtualTableScenario scn) {
        for (int i = 0; i < 20; i++) {
            settleCardPlay(scn);
            if (scn.AwaitingLSDeployPhaseActions()) {
                return;
            }
            if (scn.GetCurrentDecision() != null
                    && scn.GetCurrentDecision().getText().toLowerCase().contains("action")) {
                return;
            }
        }
        throw new RuntimeException("Never reached LS Deploy actions. " + decisionContext(scn));
    }

    private String decisionContext(VirtualTableScenario scn) {
        var decision = scn.GetCurrentDecision();
        return scn.GetCurrentPlayer() + " " + scn.GetCurrentPhase()
                + (decision == null ? " (no decision)" : " " + decision.getText());
    }

    private void passRestOfTurn(VirtualTableScenario scn) {
        String player = scn.GetCurrentPlayer();
        for (int i = 0; i < 20; i++) {
            if (!player.equals(scn.GetCurrentPlayer())) {
                return;
            }
            Phase phase = scn.GetCurrentPhase();
            if (phase == Phase.DRAW) {
                scn.PassDrawActions();
                scn.PassResponses("RECIRCULATED");
                return;
            }
            if (phase == Phase.ACTIVATE) {
                if (scn.GetCurrentPlayer().equals(scn.DS)) {
                    scn.DSActivateMaxForceAndPass();
                }
                else {
                    scn.LSActivateMaxForceAndPass();
                }
                continue;
            }
            if (scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
                scn.PassAllResponses();
                continue;
            }
            if (scn.GetCurrentDecision().getText().toLowerCase().contains("action")) {
                scn.PassResponses("action");
                continue;
            }
            try {
                scn.PassResponses();
            } catch (RuntimeException e) {
                throw new RuntimeException("passRestOfTurn failed on: " + decisionContext(scn), e);
            }
        }
        throw new RuntimeException("Did not reach Draw / recirculate. Stuck on: " + decisionContext(scn));
    }
}
