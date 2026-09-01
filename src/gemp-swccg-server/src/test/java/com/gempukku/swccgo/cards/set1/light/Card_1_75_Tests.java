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
import java.util.ArrayList;
import java.util.Arrays;
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
                    put("tc3", "1_39");
                    put("tc4", "1_39");
                    put("squadron", "7_150");
                    put("sw42", "2_081");
                    put("sw43", "2_081");
                    put("sw44", "2_081");
                    put("xwlc3", "7_162");
                    put("xwlc4", "7_162");
                    put("htb3", "9_89");
                    put("htb4", "9_89");
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
                    put("htb", "9_89");
                    put("htb2", "9_89");
                    put("homeone", "9_74");
                    put("defiance", "9_67");
                }},
                new HashMap<>()
                {{
                    put("stalker", "3_152");
                    put("executor", "4_167");
                    put("tie", "1_304");
                    put("tie2", "1_304");
                }},
                40,
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
        assertCombinedAttackStateCleared(scn);
    }

    @Test
    public void CombinedAttackExample2EptAndTwoIntruderMissilesHitsStalkerNotExecutor() {
        /**
         * Google Doc / Rulebook Example 2 under the subtotal-add model:
         * LS Combined Attack combining Enhanced Proton Torpedoes + 2 Intruder Missiles.
         * Draws prepared as 1, 1, 1. Each firing is a complete total: Enhanced Proton
         * Torpedoes 1+1=2; each Intruder Missile +3. One B-wing pilot adds +1 to a draw,
         * so the addends are 2 + 5 + 4 = 11. That shared 11 is applied for each weapon
         * (do not add +1 or +3 again). Stalker armor 7: hit. Executor armor 12: miss.
         */
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var ept = scn.GetLSCard("ept");
        var im = scn.GetLSCard("im");
        var im2 = scn.GetLSCard("im2");
        var stalker = scn.GetDSCard("stalker");
        setupEptAndTwoIntruderMissiles(scn, false);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(8);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, stalker, ept, im, im2);
        // Destinies prepared immediately before each draw in fireOneShot.

        fireOneShot(scn, stalker, 1);
        assertFalse(stalker.isHit());
        fireOneShot(scn, stalker, 1);
        assertFalse(stalker.isHit());
        fireOneShot(scn, stalker, 1);
        finishCombinedAttack(scn);
        assertTrue("Stalker armor 7 must be hit by Combined Attack example 2 subtotals 2+5+4=11", stalker.isHit());
        assertEquals(7, scn.GetDefense(stalker));
        String log = gameLog(scn);
        assertTrue("Combined Attack adds firing subtotals 2 + 5 + 4 = 11. LOG:\n" + log, log.contains("2 + 5 + 4 = 11"));
        assertFalse("Do not apply Enhanced Proton Torpedoes +1 again at Combined Attack resolve", log.contains("11+1="));
        assertFalse("Do not apply Intruder Missile +3 again at Combined Attack resolve", log.contains("11+3="));
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
        scn.EnsureLSForcePile(8);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, executor, ept, im, im2);
        // Destinies prepared immediately before each draw in fireOneShot.

        fireOneShot(scn, executor, 1);
        fireOneShot(scn, executor, 1);
        fireOneShot(scn, executor, 1);
        finishCombinedAttack(scn);

        assertFalse("Executor armor 12 must not be hit by Combined Attack subtotals 2+5+4=11", executor.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack adds firing subtotals 2 + 5 + 4 = 11 vs Executor. LOG:\n" + log, log.contains("2 + 5 + 4 = 11"));
        assertFalse("Do not apply Enhanced Proton Torpedoes +1 again at Combined Attack resolve vs Executor", log.contains("11+1="));
        assertFalse("Do not apply Intruder Missile +3 again at Combined Attack resolve vs Executor", log.contains("11+3="));
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


    @Test
    public void CombinedAttackTwoHeavyTurbolaserBatteriesVsExecutorAppliesMinusOnePerWeapon() {
        // Two Heavy Turbolaser Batteries vs Executor (capital, armor 12).
        // Each firing: draw two destinies, then Heavy Turbolaser Battery -1 vs capital.
        // Draws 4,3 then 4,3. Subtotals 7-1=6 and 7-1=6. Combined Attack adds 6 + 6 = 12.
        // 12 is not > armor 12, miss. Skipping either firing's -1 is 13 > 12 and would hit.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var htb = scn.GetLSCard("htb");
        var htb2 = scn.GetLSCard("htb2");
        var executor = scn.GetDSCard("executor");
        setupTwoHeavyTurbolaserBatteriesOnHomeOne(scn, true);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(6);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, executor, htb, htb2);

        fireTwoDestinyShot(scn, executor, 4, 3);
        assertFalse("First Combined Attack Heavy Turbolaser Battery firing must not resolve a hit by itself", executor.isHit());
        assertEquals("First firing displayed total includes Heavy Turbolaser Battery -1 (7-1=6)", 6, lastTotalWeaponDestiny(scn));
        fireTwoDestinyShot(scn, executor, 4, 3);
        assertEquals("Second firing displayed total includes Heavy Turbolaser Battery -1 (7-1=6)", 6, lastTotalWeaponDestiny(scn));
        finishCombinedAttack(scn);

        assertEquals(12, scn.GetDefense(executor));
        assertFalse("Heavy Turbolaser Battery -1 in each subtotal must make 6+6=12 miss Executor armor 12", executor.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack adds firing subtotals 6 + 6 = 12. LOG:\n" + log, log.contains("6 + 6 = 12"));
        assertFalse("Do not subtract Heavy Turbolaser Battery -1 again at Combined Attack resolve", log.contains("12-1="));
    }

    @Test
    public void CombinedAttackTwoHeavyTurbolaserBatteriesVsTieAppliesMinusSix() {
        // Same two Heavy Turbolaser Batteries vs a TIE (not capital). Heavy Turbolaser Battery -6 per firing.
        // Draws 2,2 then 2,2. Subtotals max(0, 4-6)=0 and 0. Combined Attack adds 0 + 0 = 0, miss vs maneuver 3.
        // Skipping -6 on either firing is 4 > 3 and would hit.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var htb = scn.GetLSCard("htb");
        var htb2 = scn.GetLSCard("htb2");
        var tie = scn.GetDSCard("tie");
        setupTwoHeavyTurbolaserBatteriesOnHomeOne(scn, false);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(6);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, htb, htb2);

        fireTwoDestinyShot(scn, tie, 2, 2);
        assertFalse("First Combined Attack Heavy Turbolaser Battery firing must not resolve a hit by itself", tie.isHit());
        assertEquals("First firing displayed total includes Heavy Turbolaser Battery -6 (4-6 clamped to 0)", 0, lastTotalWeaponDestiny(scn));
        fireTwoDestinyShot(scn, tie, 2, 2);
        assertEquals("Second firing displayed total includes Heavy Turbolaser Battery -6 (clamped to 0)", 0, lastTotalWeaponDestiny(scn));
        finishCombinedAttack(scn);

        assertEquals(3, scn.GetDefense(tie));
        assertFalse("Heavy Turbolaser Battery -6 in each subtotal must make 0+0=0 miss TIE maneuver 3", tie.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack adds firing subtotals 0 + 0 = 0. LOG:\n" + log, log.contains("0 + 0 = 0"));
        assertFalse("Do not subtract Heavy Turbolaser Battery -6 again at Combined Attack resolve", log.contains("0-6="));
    }

    @Test
    public void CombinedAttackDefianceTargetingComputerHeavyTurbolaserEachDrawAndTotalOnce() {
        // Defiance +2 each draw, Targeting Computer -1 each draw while fire-twice,
        // Heavy Turbolaser Battery -1 per firing vs Executor (Targeting Computer twice subtracts twice).
        // Printed 1,1 then 1,1 (Targeting Computer) then 1,1 (second battery).
        // Targeting Computer firing 1: (1+2-1)+(1+2-1)=4, then -1 = 3.
        // Targeting Computer firing 2: same subtotal 3 (must subtract again).
        // Second Heavy Turbolaser Battery: (1+2)+(1+2)=6, then -1 = 5.
        // Combined Attack adds 3 + 3 + 5 = 11, miss vs armor 12.
        // Skipping both Targeting Computer firing subtracts is 4+4+5=13 and would hit.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var htb = scn.GetLSCard("htb");
        var htb2 = scn.GetLSCard("htb2");
        var executor = scn.GetDSCard("executor");
        setupTwoHeavyTurbolaserBatteriesOnDefianceWithTargetingComputer(scn);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(8);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, executor, htb, htb2);
        chooseWhetherToUseTargetingComputer(scn, htb, true);

        fireTwoDestinyShot(scn, executor, 1, 1);
        assertFalse("First Targeting Computer Combined Attack firing must not resolve a hit by itself", executor.isHit());
        assertEquals("First Targeting Computer firing subtotal includes Heavy Turbolaser Battery -1 (4-1=3)", 3, lastTotalWeaponDestiny(scn));
        fireTwoDestinyShot(scn, executor, 1, 1);
        assertFalse("Second Targeting Computer Combined Attack firing must not resolve a hit by itself", executor.isHit());
        assertEquals("Second Targeting Computer firing must subtract Heavy Turbolaser Battery -1 again (4-1=3)", 3, lastTotalWeaponDestiny(scn));
        fireTwoDestinyShot(scn, executor, 1, 1);
        assertEquals("Second Heavy Turbolaser Battery firing includes -1 (6-1=5). LOG:\n" + gameLog(scn), 5, lastTotalWeaponDestiny(scn));
        finishCombinedAttack(scn);

        assertWeaponDestinyDrawValues(scn, 2, 2, 2, 2, 3, 3);
        assertEquals(12, scn.GetDefense(executor));
        assertFalse("Targeting Computer twice must subtract Heavy Turbolaser Battery -1 twice so 3+3+5=11 misses Executor", executor.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack adds firing subtotals 3 + 3 + 5 = 11. LOG:\n" + log, log.contains("3 + 3 + 5 = 11"));
        assertFalse("Do not subtract Heavy Turbolaser Battery -1 again at Combined Attack resolve", log.contains("11-1="));
        assertCombinedAttackStateCleared(scn);
    }


    @Test
    public void CombinedAttackStarfighterMayUseOnlyOneTargetingComputerCopy() {
        // B-wing Attack Fighter may fire many weapons, but a starfighter may use only one
        // device per turn. Combined Attack clicking one Targeting Computer consumes that
        // starfighter device slot; later Combined Attack weapons must not offer another copy.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var tc = scn.GetLSCard("tc");
        var tc2 = scn.GetLSCard("tc2");
        var tc3 = scn.GetLSCard("tc3");
        var tc4 = scn.GetLSCard("tc4");
        var sw4 = scn.GetLSCard("sw4");
        var sw42 = scn.GetLSCard("sw42");
        var tie = scn.GetDSCard("tie");
        setupBwingWithFourTargetingComputers(scn);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(8);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, sw4, sw42);
        assertTargetingComputerTableChooser(scn, sw4);
        assertTrue(scn.LSHasCardChoiceAvailable(tc));
        assertTrue(scn.LSHasCardChoiceAvailable(tc2));
        assertTrue(scn.LSHasCardChoiceAvailable(tc3));
        assertTrue(scn.LSHasCardChoiceAvailable(tc4));
        scn.LSChooseCard(tc);

        fireOneShot(scn, tie, 1);
        fireOneShot(scn, tie, 1);

        assertFalse("Starfighter already used its one device; later Combined Attack weapons must not offer another Targeting Computer. "
                        + decisionDump(scn),
                targetingComputerChooserAvailable(scn, sw42));
        fireOneShot(scn, tie, 1);
        finishCombinedAttack(scn);
    }

    @Test
    public void CombinedAttackSquadronMayUseThreeTargetingComputerCopies() {
        // X-wing Assault Squadron may use three different devices per turn. Combined Attack
        // can click three Targeting Computers; the fourth weapon must not offer the leftover copy.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var tc = scn.GetLSCard("tc");
        var tc2 = scn.GetLSCard("tc2");
        var tc3 = scn.GetLSCard("tc3");
        var tc4 = scn.GetLSCard("tc4");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var xwlc3 = scn.GetLSCard("xwlc3");
        var xwlc4 = scn.GetLSCard("xwlc4");
        var tie = scn.GetDSCard("tie");
        setupSquadronWithFourTargetingComputers(scn);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(8);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, xwlc, xwlc2, xwlc3, xwlc4);

        assertTargetingComputerTableChooser(scn, xwlc);
        assertEquals("All four unused Targeting Computers are offered on the first Combined Attack weapon",
                4, scn.LSGetCardChoiceCount());
        scn.LSChooseCard(tc);
        fireOneShot(scn, tie, 1, 0);
        fireOneShot(scn, tie, 1, 0);

        assertTargetingComputerTableChooser(scn, xwlc2);
        assertFalse("Copy A already used this turn cannot be chosen again", scn.LSHasCardChoiceAvailable(tc));
        assertTrue("Squadron may still use a second Targeting Computer this turn. " + decisionDump(scn),
                scn.LSHasCardChoiceAvailable(tc2));
        scn.LSChooseCard(tc2);
        fireOneShot(scn, tie, 1, 0);
        fireOneShot(scn, tie, 1, 0);

        assertTargetingComputerTableChooser(scn, xwlc3);
        assertTrue("Squadron may still use a third Targeting Computer this turn", scn.LSHasCardChoiceAvailable(tc3));
        scn.LSChooseCard(tc3);
        fireOneShot(scn, tie, 1, 0);
        fireOneShot(scn, tie, 1, 0);

        assertFalse("Squadron may use only three devices per turn; the fourth Combined Attack weapon must not offer Targeting Computer. "
                        + decisionDump(scn),
                targetingComputerChooserAvailable(scn, xwlc4));
        fireOneShot(scn, tie, 1, 0);
        finishCombinedAttack(scn);
    }

    @Test
    public void CombinedAttackCapitalMayUseAllFourTargetingComputerCopies() {
        // Home One may use any number of devices per turn. Combined Attack can click all four
        // Targeting Computers, one per weapon. Each used copy cannot be chosen again.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var tc = scn.GetLSCard("tc");
        var tc2 = scn.GetLSCard("tc2");
        var tc3 = scn.GetLSCard("tc3");
        var tc4 = scn.GetLSCard("tc4");
        var htb = scn.GetLSCard("htb");
        var htb2 = scn.GetLSCard("htb2");
        var htb3 = scn.GetLSCard("htb3");
        var htb4 = scn.GetLSCard("htb4");
        var stalker = scn.GetDSCard("stalker");
        setupCapitalWithFourTargetingComputers(scn);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(20);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, stalker, htb, htb2, htb3, htb4);

        PhysicalCardImpl[] computers = { tc, tc2, tc3, tc4 };
        PhysicalCardImpl[] batteries = { htb, htb2, htb3, htb4 };
        for (int i = 0; i < computers.length; i++) {
            assertTargetingComputerTableChooser(scn, batteries[i]);
            for (int j = 0; j < computers.length; j++) {
                if (j < i) {
                    assertFalse("Used Targeting Computer copy " + (j + 1) + " cannot be chosen again this turn. "
                                    + decisionDump(scn),
                            scn.LSHasCardChoiceAvailable(computers[j]));
                }
                else {
                    assertTrue("Unused Targeting Computer copy " + (j + 1) + " must still be offered. "
                                    + decisionDump(scn),
                            scn.LSHasCardChoiceAvailable(computers[j]));
                }
            }
            scn.LSChooseCard(computers[i]);
            fireTwoDestinyShot(scn, stalker, 1, 1);
            fireTwoDestinyShot(scn, stalker, 1, 1);
        }
        finishCombinedAttack(scn);
    }

    @Test
    public void CombinedAttackClickedTargetingComputerCopyCannotBeChosenAgainThisTurn() {
        // One Rule: clicking a specific Targeting Computer in Combined Attack consumes that
        // copy for the turn. On a capital (unlimited device slots) Combined Attack must not
        // list that same copy for a later weapon, while unused copies remain choosable.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var tc = scn.GetLSCard("tc");
        var tc2 = scn.GetLSCard("tc2");
        var tc3 = scn.GetLSCard("tc3");
        var tc4 = scn.GetLSCard("tc4");
        var htb = scn.GetLSCard("htb");
        var htb2 = scn.GetLSCard("htb2");
        var stalker = scn.GetDSCard("stalker");
        setupCapitalWithFourTargetingComputers(scn);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(12);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, stalker, htb, htb2);
        assertTargetingComputerTableChooser(scn, htb);
        assertTrue(scn.LSHasCardChoiceAvailable(tc));
        scn.LSChooseCard(tc);
        fireTwoDestinyShot(scn, stalker, 1, 1);
        fireTwoDestinyShot(scn, stalker, 1, 1);

        assertTargetingComputerTableChooser(scn, htb2);
        assertFalse("Combined Attack must not offer the same Targeting Computer copy again this turn. "
                        + decisionDump(scn),
                scn.LSHasCardChoiceAvailable(tc));
        assertTrue("Using copy A does not prevent choosing copy B this turn", scn.LSHasCardChoiceAvailable(tc2));
        assertTrue("Copy C is still available this turn", scn.LSHasCardChoiceAvailable(tc3));
        assertTrue("Copy D is still available this turn", scn.LSHasCardChoiceAvailable(tc4));
        scn.LSChooseCard(tc2);
        fireTwoDestinyShot(scn, stalker, 1, 1);
        fireTwoDestinyShot(scn, stalker, 1, 1);
        finishCombinedAttack(scn);
        scn.PassAllResponses();
        passDarkSideWeaponsIfNeeded(scn);

        if (scn.AwaitingLSWeaponsSegmentActions()) {
            assertFalse("Copy A cannot Fire a weapon twice after Combined Attack used it this turn. "
                            + decisionDump(scn),
                    fireTwiceAvailable(scn, tc));
            assertFalse("Copy B cannot Fire a weapon twice after Combined Attack used it this turn",
                    fireTwiceAvailable(scn, tc2));
            assertTrue("Copy C was not used in Combined Attack and remains available on leftover Heavy Turbolaser Batteries. "
                            + decisionDump(scn),
                    fireTwiceAvailable(scn, tc3));
            assertTrue("Copy D remains available this turn", fireTwiceAvailable(scn, tc4));
        }
    }

    @Test
    public void CombinedAttackDoneDoesNotConsumeStarfighterDeviceUse() {
        // Done on the Combined Attack Targeting Computer chooser fires that weapon once and
        // does not use the device. The starfighter's one device use remains, so leftover
        // weapons can still Fire a weapon twice after Combined Attack.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var tc = scn.GetLSCard("tc");
        var sw4 = scn.GetLSCard("sw4");
        var sw42 = scn.GetLSCard("sw42");
        var tie = scn.GetDSCard("tie");
        setupBwingWithFourTargetingComputers(scn);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(8);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, sw4, sw42);
        assertTargetingComputerTableChooser(scn, sw4);
        scn.LSDecided("");
        fireOneShot(scn, tie, 1);

        assertTrue("Done must not consume the starfighter device; the next Combined Attack weapon still offers Targeting Computer. "
                        + decisionDump(scn),
                targetingComputerChooserAvailable(scn, sw42));
        assertTrue(scn.LSHasCardChoiceAvailable(tc));
        scn.LSDecided("");
        fireOneShot(scn, tie, 1);
        finishCombinedAttack(scn);
        scn.PassAllResponses();
        passDarkSideWeaponsIfNeeded(scn);

        assertTrue("After Combined Attack Done, Light Side should still have leftover SW-4 Ion Cannons. "
                        + decisionDump(scn),
                scn.AwaitingLSWeaponsSegmentActions());
        assertTrue("Done did not consume Targeting Computer; Fire a weapon twice remains available. "
                        + decisionDump(scn),
                fireTwiceAvailable(scn, tc));
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
        scn.PassCardPlayResponses();
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

    /**
     * After Combined Attack finishes, Combined Attack and Targeting Computer firing states must be empty
     * and taking a snapshot must not throw. Weapons-segment Pass snapshots the game; if those states are
     * still set the game is canceled.
     */
    private void assertCombinedAttackStateCleared(VirtualTableScenario scn) {
        assertTrue("Combined Attack must clear Combined Attack firing state",
                scn.gameState().getCombinedAttackFiringState() == null);
        assertTrue("Combined Attack must clear Targeting Computer separately-or-combined firing state",
                scn.gameState().getSeparatelyOrCombinedFiringState() == null);
        scn.game().takeSnapshot("after Combined Attack");
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
    /**
     * Two Heavy Turbolaser Batteries on Home One vs Executor (capital) or a TIE (not capital).
     */
    private void setupTwoHeavyTurbolaserBatteriesOnHomeOne(VirtualTableScenario scn, boolean vsExecutor) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var homeone = scn.GetLSCard("homeone");
        var htb = scn.GetLSCard("htb");
        var htb2 = scn.GetLSCard("htb2");
        var target = vsExecutor ? scn.GetDSCard("executor") : scn.GetDSCard("tie");
        scn.MoveCardsToLocation(system, homeone, target);
        scn.AttachCardsTo(homeone, htb, htb2);
    }

    /**
     * Two Heavy Turbolaser Batteries and a Targeting Computer on Defiance vs Executor.
     */
    private void setupTwoHeavyTurbolaserBatteriesOnDefianceWithTargetingComputer(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var defiance = scn.GetLSCard("defiance");
        var htb = scn.GetLSCard("htb");
        var htb2 = scn.GetLSCard("htb2");
        var tc = scn.GetLSCard("tc");
        var executor = scn.GetDSCard("executor");
        scn.MoveCardsToLocation(system, defiance, executor);
        scn.AttachCardsTo(defiance, htb, htb2, tc);
    }

    /**
     * Fire a Combined Attack weapon that draws two destinies (Heavy Turbolaser Battery).
     * Prepare each draw immediately before it so the same destiny value can be reused.
     */
    private void fireTwoDestinyShot(VirtualTableScenario scn, PhysicalCardImpl target, int destiny1, int destiny2) {
        if (scn.LSGetDecision() != null && scn.LSDecisionAvailable("Targeting Computer") && scn.LSDecisionAvailable("Done")) {
            // Second Combined Attack weapon on the same starship: skip leftover Targeting Computer.
            scn.LSDecided("");
        }
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(target)) {
            scn.LSChooseCard(target);
        }
        chooseForceAmountIfPrompted(scn, null);
        scn.PrepareLSDestiny(destiny1);
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        scn.PassDestinyDrawResponses();
        scn.PrepareLSDestiny(destiny2);
        scn.PassDestinyDrawResponses();
        passPostFiringResponses(scn);
    }

    private String gameLog(VirtualTableScenario scn) {
        return String.join("\n", scn.gameState().getLastMessages());
    }

    private int lastTotalWeaponDestiny(VirtualTableScenario scn) {
        Integer last = null;
        for (String msg : scn.gameState().getLastMessages()) {
            String lower = msg.toLowerCase();
            int idx = lower.lastIndexOf("total weapon destiny is ");
            if (idx >= 0) {
                String rest = msg.substring(idx + "total weapon destiny is ".length()).trim();
                last = (int) Float.parseFloat(rest.split("[^0-9.]")[0]);
            }
        }
        assertTrue("Expected a total weapon destiny log, got: " + gameLog(scn), last != null);
        return last;
    }

    /**
     * Last weapon destiny draw values in order, after each-draw modifiers (Defiance +2, Targeting Computer -1).
     */
    private void assertWeaponDestinyDrawValues(VirtualTableScenario scn, int... expected) {
        List<Integer> actual = new ArrayList<>();
        for (String msg : scn.gameState().getLastMessages()) {
            int idx = msg.indexOf(" as a ");
            int destIdx = msg.indexOf(" for weapon destiny");
            if (idx >= 0 && destIdx > idx) {
                actual.add((int) Float.parseFloat(msg.substring(idx + " as a ".length(), destIdx).trim()));
            }
        }
        assertTrue("Expected last weapon destiny draws " + Arrays.toString(expected) + " but saw " + actual,
                actual.size() >= expected.length);
        List<Integer> last = actual.subList(actual.size() - expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals("weapon destiny draw " + (i + 1) + " of " + last, expected[i], last.get(i).intValue());
        }
    }


    /**
     * B-wing Attack Fighter with four Targeting Computers and four SW-4 Ion Cannons vs two TIEs.
     */
    private void setupBwingWithFourTargetingComputers(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var bwing = scn.GetLSCard("bwing");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        scn.MoveCardsToLocation(system, bwing, tie, tie2);
        scn.AttachCardsTo(bwing,
                scn.GetLSCard("sw4"), scn.GetLSCard("sw42"), scn.GetLSCard("sw43"), scn.GetLSCard("sw44"),
                scn.GetLSCard("tc"), scn.GetLSCard("tc2"), scn.GetLSCard("tc3"), scn.GetLSCard("tc4"));
    }

    /**
     * X-wing Assault Squadron with four Targeting Computers and four X-wing Laser Cannons vs two TIEs.
     */
    private void setupSquadronWithFourTargetingComputers(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var squadron = scn.GetLSCard("squadron");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        scn.MoveCardsToLocation(system, squadron, tie, tie2);
        scn.AttachCardsTo(squadron,
                scn.GetLSCard("xwlc"), scn.GetLSCard("xwlc2"), scn.GetLSCard("xwlc3"), scn.GetLSCard("xwlc4"),
                scn.GetLSCard("tc"), scn.GetLSCard("tc2"), scn.GetLSCard("tc3"), scn.GetLSCard("tc4"));
    }

    /**
     * Home One with four Targeting Computers and four Heavy Turbolaser Batteries vs Stalker.
     */
    private void setupCapitalWithFourTargetingComputers(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var homeone = scn.GetLSCard("homeone");
        var stalker = scn.GetDSCard("stalker");
        scn.MoveCardsToLocation(system, homeone, stalker);
        scn.AttachCardsTo(homeone,
                scn.GetLSCard("htb"), scn.GetLSCard("htb2"), scn.GetLSCard("htb3"), scn.GetLSCard("htb4"),
                scn.GetLSCard("tc"), scn.GetLSCard("tc2"), scn.GetLSCard("tc3"), scn.GetLSCard("tc4"));
    }

    private boolean targetingComputerChooserAvailable(VirtualTableScenario scn, PhysicalCardImpl weapon) {
        return scn.LSGetDecision() != null
                && scn.LSDecisionAvailable("Targeting Computer")
                && scn.LSDecisionAvailable(weapon.getTitle())
                && scn.LSDecisionAvailable("Done");
    }

    private boolean fireTwiceAvailable(VirtualTableScenario scn, PhysicalCardImpl targetingComputer) {
        return scn.LSGetDecision() != null && scn.LSCardActionAvailable(targetingComputer, "Fire a weapon twice");
    }

    private void passDarkSideWeaponsIfNeeded(VirtualTableScenario scn) {
        if (scn.AwaitingDSWeaponsSegmentActions()) {
            scn.DSPass();
        }
    }

    private String decisionDump(VirtualTableScenario scn) {
        String ls = scn.LSGetDecision() == null ? "null" : scn.LSGetDecision().getText();
        String ds = scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText();
        return "LS=" + ls + " DS=" + ds;
    }

}
