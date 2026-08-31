package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

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
                    put("tc2", "1_39");
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
                    put("sw4", "2_081");
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

        scn.StartBattleAndSkipToWeaponsSegment();
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

        scn.StartBattleAndSkipToWeaponsSegment();
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

        scn.StartBattleAndSkipToWeaponsSegment();
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

        scn.StartBattleAndSkipToWeaponsSegment();
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

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, xwlc, xwlc2);
        chooseWhetherToUseTargetingComputer(scn, xwlc, true);

        // TC -1 on each draw from the TC starship: dest 4,4 then 1 from the other weapon => (4-1)+(4-1)+1 = 7 > 3.
        fireOneShot(scn, tie, 4, 0);
        assertFalse(tie.isHit());
        fireOneShot(scn, tie, 4, 0);
        assertFalse("TC second draw is still in the Combined Attack pool, not a standalone TC resolve", tie.isHit());
        fireOneShot(scn, tie, 1, 0);
        finishCombinedAttack(scn);
        assertTrue(tie.isHit());

        scn.PassAllResponses();
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

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, xwlc, xwlc2);
        chooseWhetherToUseTargetingComputer(scn, xwlc, true);

        fireOneShot(scn, tie, 4, 0);
        fireOneShot(scn, tie, 4, 0);
        fireOneShot(scn, tie, 4, 0);
        finishCombinedAttack(scn);
        assertTrue(tie.isHit());

        scn.PassAllResponses();
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            assertFalse("Cannot fire the leftover TC shot outside Combined Attack",
                    scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
            assertFalse("XWLC already used its Combined Attack / once-per-battle firing",
                    scn.LSCardActionAvailable(xwlc));
        }
        assertFalse("Second TIE must not be hit by a split leftover TC shot", tie2.isHit());
    }

    @Test
    public void TargetingComputerInsideCombinedAttackOffersOptionalUseWithoutSeparatelyCombinedCancel() {
        // Combined Attack already chose the target. Click a Targeting Computer card to fire
        // that Combined Attack weapon twice into the pool. No Separately / Combined / Don't Fire.
        // Choosing the card consumes Targeting Computer and both destinies join the pool.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var tc = scn.GetLSCard("tc");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var tie = scn.GetDSCard("tie");
        setupTwoXwingLasersWithTcOnFirst(scn);
        scn.MoveCardsToHand(ca);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, xwlc, xwlc2);
        chooseWhetherToUseTargetingComputer(scn, xwlc, true);
        if (scn.LSGetDecision() != null) {
            assertFalse("Using Targeting Computer inside Combined Attack must not then ask Separately vs Combined",
                    scn.LSChoiceAvailable("Separately"));
            assertFalse("Using Targeting Computer inside Combined Attack must not offer Don't Fire (Cancel)",
                    scn.LSChoiceAvailable("Don't Fire"));
            assertFalse("Must not present the standalone Fire a weapon twice three-way after opting in",
                    scn.LSDecisionAvailable("Fire a weapon twice"));
        }

        fireOneShot(scn, tie, 4, 0);
        fireOneShot(scn, tie, 4, 0);
        fireOneShot(scn, tie, 1, 0);
        finishCombinedAttack(scn);
        assertTrue(tie.isHit());

        scn.PassAllResponses();
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            assertFalse("Choosing Targeting Computer inside Combined Attack consumes it",
                    scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
        }
    }

    @Test
    public void TargetingComputerInsideCombinedAttackCanBeSkippedAndRemainsUnused() {
        // Done on the Targeting Computer table chooser fires the Combined Attack weapon once
        // and does not consume Targeting Computer. Done must not cancel Combined Attack.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var tie = scn.GetDSCard("tie");
        setupTwoXwingLasersWithTcOnFirst(scn);
        scn.MoveCardsToHand(ca);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, xwlc, xwlc2);
        chooseWhetherToUseTargetingComputer(scn, xwlc, false);

        // One shot from the Targeting Computer ship (no second Targeting Computer firing) plus the other weapon.
        fireOneShot(scn, tie, 4, 0);
        fireOneShot(scn, tie, 1, 0);
        finishCombinedAttack(scn);
        assertTrue("4 + 1 without Targeting Computer -1 still hits TIE 3", tie.isHit());
        // Unused is proven by only two destinies (no extra Targeting Computer shot) succeeding as 4+1.
    }

    @Test
    public void TargetingComputerInsideCombinedAttackCanChooseAmongMultipleComputersOrNone() {
        // Two unused Targeting Computers on the firing starship: click one specific card, or Done for none.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var tc = scn.GetLSCard("tc");
        var tc2 = scn.GetLSCard("tc2");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var xwing = scn.GetLSCard("xwing");
        var xwing2 = scn.GetLSCard("xwing2");
        var tie = scn.GetDSCard("tie");
        setupTwoXwingLasersWithTwoTargetingComputersOnFirst(scn);
        scn.MoveCardsToHand(ca);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, xwlc, xwlc2);
        assertTargetingComputerTableChooser(scn, xwlc);
        assertTrue(scn.LSHasCardChoiceAvailable(tc));
        assertTrue(scn.LSHasCardChoiceAvailable(tc2));
        assertEquals("Only unused Targeting Computers on the firing starship", 2, scn.LSGetCardChoiceCount());
        assertFalse("Weapons are not Targeting Computer choices", scn.LSHasCardChoiceAvailable(xwlc));
        assertFalse("Weapons are not Targeting Computer choices", scn.LSHasCardChoiceAvailable(xwlc2));
        assertFalse("Starships are not Targeting Computer choices", scn.LSHasCardChoiceAvailable(xwing));
        assertFalse("The other starship is not a Targeting Computer choice", scn.LSHasCardChoiceAvailable(xwing2));
        // Pick the second Targeting Computer specifically (not auto-first).
        scn.LSChooseCard(tc2);

        fireOneShot(scn, tie, 4, 0);
        fireOneShot(scn, tie, 4, 0);
        fireOneShot(scn, tie, 1, 0);
        finishCombinedAttack(scn);
        assertTrue(tie.isHit());
    }

    @Test
    public void CombinedAttackXwingLaserCannonX3LosesTieNotMerelyHit() {
        // Playtest: X-wing XWLC (X=3) + B-wing SW-4 Ion Cannon vs TIE.
        // Combined destinies are high enough to succeed even without X, so a mere hit is the
        // wrong result. XWLC at X=3 must LOSE the TIE (destiny + X > DV). Apply Ion Cannon
        // first so a leftover WeaponFiringState cannot skip restoring X.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var xwlc = scn.GetLSCard("xwlc");
        var sw4 = scn.GetLSCard("sw4");
        var tie = scn.GetDSCard("tie");
        setupXwingLaserAndBwingIon(scn);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(6);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, xwlc, sw4);

        int forceBeforeShots = scn.GetLSForcePileCount();
        fireOneShot(scn, tie, 4, 3);
        assertEquals("XWLC Combined Attack shot must use 3 Force (X=3)",
                forceBeforeShots - 3, scn.GetLSForcePileCount());
        assertFalse("First Combined Attack destiny must not resolve a hit or loss by itself", tie.isHit());
        assertEquals(Zone.AT_LOCATION, tie.getZone());

        fireOneShot(scn, tie, 4);
        finishCombinedAttack(scn, 1);

        assertTrue("X-wing Laser Cannon at X=3 must lose the TIE, not merely hit",
                tie.getZone() == Zone.LOST_PILE || tie.getZone() == Zone.TOP_OF_LOST_PILE);
        assertFalse("TIE must not remain at the location as merely hit", tie.getZone() == Zone.AT_LOCATION);
    }


    private void setupXwingLaserAndBwingIon(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var xwing = scn.GetLSCard("xwing");
        var bwing = scn.GetLSCard("bwing");
        var xwlc = scn.GetLSCard("xwlc");
        var sw4 = scn.GetLSCard("sw4");
        var tie = scn.GetDSCard("tie");
        scn.MoveCardsToLocation(system, xwing, bwing, tie);
        scn.AttachCardsTo(xwing, xwlc);
        scn.AttachCardsTo(bwing, sw4);
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

    /**
     * Two X-wing Laser Cannons vs two TIEs, with two unused Targeting Computers on the first X-wing.
     */
    private void setupTwoXwingLasersWithTwoTargetingComputersOnFirst(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var xwing = scn.GetLSCard("xwing");
        var xwing2 = scn.GetLSCard("xwing2");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var tc = scn.GetLSCard("tc");
        var tc2 = scn.GetLSCard("tc2");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        scn.MoveCardsToLocation(system, xwing, xwing2, tie, tie2);
        scn.AttachCardsTo(xwing, xwlc, tc, tc2);
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
        scn.PassAllResponses();
    }

    /**
     * Combined Attack optional Targeting Computer table chooser: click a Targeting Computer
     * card to fire the named Combined Attack weapon twice into the pool, or Done to skip.
     * The prompt names that firing weapon (for example X-wing Laser Cannon) so the player
     * knows which Combined Attack weapon is about to fire. Not the standalone Fire a weapon
     * twice three-way, and not MultipleChoice buttons.
     */
    private void chooseWhetherToUseTargetingComputer(VirtualTableScenario scn, PhysicalCardImpl weapon, boolean useTargetingComputer) {
        assertTargetingComputerTableChooser(scn, weapon);
        if (useTargetingComputer) {
            scn.LSChooseCard(scn.GetLSCard("tc"));
        }
        else {
            // Done skips this Targeting Computer choice only (does not cancel Combined Attack).
            scn.LSDecided("");
        }
    }

    /**
     * Combined Attack Targeting Computer prompt is a table-card chooser, not buttons.
     * The decision text must name Targeting Computer, the firing weapon's title, and Done.
     */
    private void assertTargetingComputerTableChooser(VirtualTableScenario scn, PhysicalCardImpl weapon) {
        String decisionText = scn.LSGetDecision() == null ? "null" : scn.LSGetDecision().getText();
        assertTrue("Expected optional Targeting Computer prompt naming " + weapon.getTitle() + ", got: " + decisionText,
                scn.LSDecisionAvailable("Targeting Computer")
                        && scn.LSDecisionAvailable(weapon.getTitle())
                        && scn.LSDecisionAvailable("Done"));
        assertFalse("Combined Attack must not ask Separately vs Combined", scn.LSChoiceAvailable("Separately"));
        assertFalse("Combined Attack must not offer Don't Fire (Cancel)", scn.LSChoiceAvailable("Don't Fire"));
        assertFalse("Must not use MultipleChoice Targeting Computer buttons",
                scn.LSChoiceAvailable("Do not use Targeting Computer"));
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

    private void passPostFiringResponses(VirtualTableScenario scn) {
        scn.PassResponses("ATTRIBUTE_RESET_OR_MODIFIED");
        scn.PassResponses("Ionized");
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassResponses("ABOUT_TO_BE_HIT");
        scn.PassResponses("HIT -");
        scn.PassResponses("FIRED_WEAPON");
        scn.PassResponses("PLACE_IN_CARD_PILE");
    }

    private void chooseResultApplyOrderIfPrompted(VirtualTableScenario scn) {
        chooseResultApplyOrderIfPrompted(scn, 0);
    }

    private void chooseResultApplyOrderIfPrompted(VirtualTableScenario scn, int applyFirstChoiceIndex) {
        int safety = 0;
        while (safety++ < 8 && scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null) {
            String text = scn.LSGetDecision().getText().toLowerCase();
            if (!(text.contains("applies first") || text.contains("weapon result"))) {
                break;
            }
            // ChooseArbitraryCardsEffect uses temp0/temp1 indexes, not in-play card IDs.
            List<String> ids = scn.LSGetCardChoices();
            if (ids == null || ids.isEmpty()) {
                break;
            }
            int pick = applyFirstChoiceIndex;
            if (pick < 0 || pick >= ids.size()) {
                pick = ids.size() - 1;
            }
            scn.PlayerDecided("Light Side Player", ids.get(pick));
        }
    }

    private void finishCombinedAttack(VirtualTableScenario scn) {
        finishCombinedAttack(scn, 0);
    }

    private void finishCombinedAttack(VirtualTableScenario scn, int applyFirstChoiceIndex) {
        chooseResultApplyOrderIfPrompted(scn, applyFirstChoiceIndex);
        scn.PassResponses("PLACE_IN_CARD_PILE");
        scn.PassAllResponses();
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassResponses("ABOUT_TO_BE_HIT");
        scn.PassResponses("HIT -");
        scn.PassResponses("FIRED_WEAPON");
        chooseResultApplyOrderIfPrompted(scn);
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassAllResponses();
        chooseResultApplyOrderIfPrompted(scn);
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassAllResponses();
    }
}
