package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_75_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("ca", "1_75");
                    put("tc", "1_39");
                    put("pilot", "1_27");
                    put("pilot2", "1_27");
                    put("xwing", "1_146");
                    put("xwing2", "1_146");
                    put("ywing", "1_147");
                    put("xwlc", "7_162");
                    put("xwlc2", "7_162");
                    put("ept", "9_88");
                    put("bwing", "7_140");
                    put("bwing2", "9_66");
                    put("im", "7_159");
                    put("im2", "7_159");
                }},
                new HashMap<>()
                {{
                    put("stalker", "3_152");
                    put("executor", "4_167");
                    put("tie", "1_304");
                    put("tie2", "1_304");
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

    @Test
    public void CombinedAttackStatsAndKeywordsAreCorrect() {
        /**
         * Title: Combined Attack
         * Uniqueness: Unrestricted
         * Side: Light
         * Type: Interrupt
         * Subtype: Lost
         * Destiny: 4
         * Game Text: During a battle, target opponent's starship present with two (or more) of your starship weapons.
         *      Add all weapon destiny draws together. Apply that total separately for each weapon in an order of your choosing.
         * Lore: Efficient cooperation allowed the Rebels to coordinate the attack of their small starfighters effectively
         *      at the Battle of Yavin.
         * Set: Premiere
         * Rarity: C2
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("ca").getBlueprint();

        assertEquals("Combined Attack", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertTrue(card.isCardType(CardType.INTERRUPT));
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(Rarity.C2, card.getRarity());
    }

    @Test
    public void CombinedAttackCannotBePlayedOutsideBattle() {
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        setupTwoXwingLasers(scn);
        scn.MoveCardsToHand(ca);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(ca));

        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(ca));
    }

    @Test
    public void CombinedAttackCannotBePlayedWithFewerThanTwoLegalStarshipWeapons() {
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        setupOneXwingLaser(scn);
        scn.MoveCardsToHand(ca);

        startWeaponsSegment(scn);
        assertFalse("Combined Attack requires two or more legal starship weapons", scn.LSCardPlayAvailable(ca));
    }

    @Test
    public void CombinedAttackAddsDestinyDrawsAndAppliesTotalToEachWeapon() {
        // Two X-wing Laser Cannons (X=0) vs TIE maneuver 3.
        // Destinies 2 then 3: separately 2 miss and 3 miss; Combined Attack draw-sum 5 > 3 hits.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var tie = scn.GetDSCard("tie");
        setupTwoXwingLasers(scn);
        scn.MoveCardsToHand(ca);

        startWeaponsSegment(scn);
        assertTrue(scn.LSCardPlayAvailable(ca));
        playCombinedAttack(scn, tie, xwlc, xwlc2);

        fireOneShot(scn, tie, 2, 0);
        assertFalse("First Combined Attack destiny must not resolve a hit by itself", tie.isHit());

        fireOneShot(scn, tie, 3, 0);
        finishCombinedAttack(scn);
        assertTrue(tie.isHit());
    }

    @Test
    public void CombinedAttackExample2EptAndTwoIntruderMissilesHitsStalkerNotExecutor() {
        /**
         * Google Doc / Rulebook Example 2 (fleshed out):
         * LS Combined Attack combining Enhanced Proton Torpedoes + 2 Intruder Missiles.
         * Draws 1, 2, 3. Draw-sum 6.
         * Total modifiers: +1 from Torpedoes (vs capital) and +3 from ONE Intruder Missile
         * (not +6 from two; cumulative / per-weapon).
         * Per-weapon AR: EPT 6+1=7 vs Stalker armor 7 miss; IM 6+3=9 vs 7 hit.
         * Executor armor 12: 7 and 9 both miss.
         * Grand-total teaching number 1+2+3+1+3=10 also hits Stalker and misses Executor.
         */
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var ept = scn.GetLSCard("ept");
        var im = scn.GetLSCard("im");
        var im2 = scn.GetLSCard("im2");
        var stalker = scn.GetDSCard("stalker");
        setupEptAndTwoIntruderMissiles(scn, false);
        scn.MoveCardsToHand(ca);

        startWeaponsSegment(scn);
        playCombinedAttack(scn, stalker, ept, im, im2);
        stackCombinedAttackDestinies(scn, 1, 2, 3);

        fireOneShot(scn, stalker, 1);
        assertFalse(stalker.isHit());
        fireOneShot(scn, stalker, 2);
        assertFalse(stalker.isHit());
        fireOneShot(scn, stalker, 3);
        finishCombinedAttack(scn);
        assertTrue("Stalker armor 7 must be hit by Combined Attack example 2", stalker.isHit());
        assertEquals(7, scn.GetDefense(stalker));
    }

    @Test
    public void CombinedAttackExample2DoesNotHitExecutorArmor12() {
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var ept = scn.GetLSCard("ept");
        var im = scn.GetLSCard("im");
        var im2 = scn.GetLSCard("im2");
        var executor = scn.GetDSCard("executor");
        setupEptAndTwoIntruderMissiles(scn, true);
        scn.MoveCardsToHand(ca);

        startWeaponsSegment(scn);
        playCombinedAttack(scn, executor, ept, im, im2);
        stackCombinedAttackDestinies(scn, 1, 2, 3);

        fireOneShot(scn, executor, 1);
        fireOneShot(scn, executor, 2);
        fireOneShot(scn, executor, 3);
        finishCombinedAttack(scn);

        assertFalse("Executor armor 12 must not be hit by destinies 1+2+3 plus EPT/IM totals", executor.isHit());
    }

    @Test
    public void TargetingComputerBothShotsGoIntoCombinedAttackPool() {
        // Two XWLCs, one ship has Targeting Computer. Use TC inside CA.
        // Destinies 4 and 4 (TC weapon, two draws, each -1) plus 1 (second weapon) = 7 vs TIE 3.
        // TC weapon is still one weapon when applying the shared total.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var tc = scn.GetLSCard("tc");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var tie = scn.GetDSCard("tie");
        setupTwoXwingLasersWithTcOnFirst(scn);
        scn.MoveCardsToHand(ca);

        startWeaponsSegment(scn);
        playCombinedAttack(scn, tie, xwlc, xwlc2);
        chooseUseTargetingComputer(scn, true);

        // TC -1 on each draw from the TC starship: dest 4,4 then 1 from the other weapon => (4-1)+(4-1)+1 = 7 > 3.
        fireOneShot(scn, tie, 4, 0);
        assertFalse(tie.isHit());
        fireOneShot(scn, tie, 4, 0);
        assertFalse("TC second draw is still in the Combined Attack pool, not a standalone TC resolve", tie.isHit());
        fireOneShot(scn, tie, 1, 0);
        finishCombinedAttack(scn);
        assertTrue(tie.isHit());

        passOptionalResponses(scn);
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            assertFalse("Both TC shots were inside Combined Attack; TC cannot be used again this battle",
                    scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
        }
    }

    @Test
    public void TargetingComputerCannotSplitOneShotInsideCombinedAttackAndOneOutside() {
        // Forum p=1111897: both TC shots consecutive and both inside Combined Attack.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var tc = scn.GetLSCard("tc");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        setupTwoXwingLasersWithTcOnFirst(scn);
        scn.MoveCardsToHand(ca);

        startWeaponsSegment(scn);
        playCombinedAttack(scn, tie, xwlc, xwlc2);
        chooseUseTargetingComputer(scn, true);

        fireOneShot(scn, tie, 4, 0);
        fireOneShot(scn, tie, 4, 0);
        fireOneShot(scn, tie, 4, 0);
        finishCombinedAttack(scn);
        assertTrue(tie.isHit());

        passOptionalResponses(scn);
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            assertFalse("Cannot fire the leftover TC shot outside Combined Attack",
                    scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
            assertFalse("XWLC already used its Combined Attack / once-per-battle firing",
                    scn.LSCardActionAvailable(xwlc));
        }
        assertFalse("Second TIE must not be hit by a split leftover TC shot", tie2.isHit());
    }



    private void setupTwoXwingLasers(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var xwing = scn.GetLSCard("xwing");
        var xwing2 = scn.GetLSCard("xwing2");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        scn.MoveCardsToLocation(system, xwing, xwing2, tie, tie2);
        scn.AttachCardsTo(xwing, xwlc);
        scn.AttachCardsTo(xwing2, xwlc2);
    }

    private void setupOneXwingLaser(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var xwing = scn.GetLSCard("xwing");
        var xwlc = scn.GetLSCard("xwlc");
        var tie = scn.GetDSCard("tie");
        scn.MoveCardsToLocation(system, xwing, tie);
        scn.AttachCardsTo(xwing, xwlc);
    }

    private void setupTwoXwingLasersWithTcOnFirst(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var xwing = scn.GetLSCard("xwing");
        var xwing2 = scn.GetLSCard("xwing2");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var tc = scn.GetLSCard("tc");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        scn.MoveCardsToLocation(system, xwing, xwing2, tie, tie2);
        scn.AttachCardsTo(xwing, xwlc, tc);
        scn.AttachCardsTo(xwing2, xwlc2);
    }

    private void setupEptAndTwoIntruderMissiles(VirtualTableScenario scn, boolean vsExecutor) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var ywing = scn.GetLSCard("ywing");
        var bwing = scn.GetLSCard("bwing");
        var bwing2 = scn.GetLSCard("bwing2");
        var ept = scn.GetLSCard("ept");
        var im = scn.GetLSCard("im");
        var im2 = scn.GetLSCard("im2");
        var target = vsExecutor ? scn.GetDSCard("executor") : scn.GetDSCard("stalker");
        scn.MoveCardsToLocation(system, ywing, bwing, bwing2, target);
        scn.BoardAsPilot(bwing, scn.GetLSCard("pilot"));
        scn.BoardAsPilot(bwing2, scn.GetLSCard("pilot2"));
        scn.AttachCardsTo(ywing, ept);
        scn.AttachCardsTo(bwing, im);
        scn.AttachCardsTo(bwing2, im2);
    }

    private void startWeaponsSegment(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        passOptionalResponses(scn);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    private void playCombinedAttack(VirtualTableScenario scn, PhysicalCardImpl target, PhysicalCardImpl... weapons) {
        var ca = scn.GetLSCard("ca");
        if (scn.LSCardPlayAvailable(ca)) {
            scn.LSPlayCard(ca);
        }
        else {
            scn.LSUseCardAction(ca);
        }
        if (scn.LSHasCardChoiceAvailable(target)) {
            scn.LSChooseCard(target);
        }
        if (weapons.length > 0 && scn.LSGetDecision() != null) {
            scn.LSChooseCards(weapons);
        }
        passOptionalResponses(scn);
    }

    private void chooseUseTargetingComputer(VirtualTableScenario scn, boolean use) {
        if (scn.LSGetDecision() == null || scn.LSGetDecision().getText() == null) {
            return;
        }
        String text = scn.LSGetDecision().getText();
        if (text.contains("Targeting Computer") || text.contains("fire") && text.contains("twice")) {
            scn.LSChoose(use ? "Yes" : "No");
        }
    }

    private void fireOneShot(VirtualTableScenario scn, PhysicalCardImpl target, int destiny) {
        fireOneShot(scn, target, destiny, null);
    }

    private void fireOneShot(VirtualTableScenario scn, PhysicalCardImpl target, int destiny, Integer forceToUse) {
        scn.PrepareLSDestiny(destiny);
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(target)) {
            scn.LSChooseCard(target);
        }
        chooseForceAmountIfPrompted(scn, forceToUse);
        scn.PassForceUseResponses();
        scn.PassWeaponFireWithDestinyDraw();
        passPostFiringResponses(scn);
    }

    private void stackCombinedAttackDestinies(VirtualTableScenario scn, int... destinies) {
        // Last prepared card sits on top, so stack in reverse.
        for (int i = destinies.length - 1; i >= 0; i--) {
            scn.PrepareLSDestiny(destinies[i]);
        }
    }

    private void chooseForceAmountIfPrompted(VirtualTableScenario scn, Integer forceToUse) {
        if (scn.LSGetDecision() == null || scn.LSGetDecision().getText() == null) {
            return;
        }
        String text = scn.LSGetDecision().getText().toLowerCase();
        if (!(text.contains("amount") || text.contains("how much") || text.contains("force to use") || text.contains("x ="))) {
            return;
        }
        int amount = forceToUse != null ? forceToUse : scn.LSGetChoiceMin();
        scn.LSDecided(amount);
    }

    private void passOptionalResponses(VirtualTableScenario scn) {
        if (scn.GetCurrentDecision() != null) {
            scn.PassAllResponses();
        }
    }

    private void passPostFiringResponses(VirtualTableScenario scn) {
        scn.PassResponses("ATTRIBUTE_RESET_OR_MODIFIED");
        scn.PassResponses("Ionized");
        scn.PassResponses("ABOUT_TO_BE_HIT");
        scn.PassResponses("HIT -");
        scn.PassResponses("FIRED_WEAPON");
        scn.PassResponses("PLACE_IN_CARD_PILE");
    }

    private void finishCombinedAttack(VirtualTableScenario scn) {
        scn.PassResponses("PLACE_IN_CARD_PILE");
        scn.PassAllResponses();
        scn.PassResponses("ABOUT_TO_BE_HIT");
        scn.PassResponses("HIT -");
        scn.PassResponses("FIRED_WEAPON");
        scn.PassAllResponses();
    }
}
