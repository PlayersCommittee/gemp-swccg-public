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
                    put("bomber", "9_66");
                    put("im", "7_159");
                    put("im2", "7_159");
                    put("missiles", "9_87");
                    put("missiles2", "9_87");
                    put("sw4", "2_081");
                    put("htb", "9_89");
                    put("htb2", "9_89");
                    put("homeone", "9_74");
                    put("defiance", "9_67");
                    put("falcon", "1_143");
                    put("corvette", "1_140");
                    put("qlc", "1_159");
                    put("bomber3", "9_66");
                    put("ept2", "9_88");
                    put("ept3", "9_88");
                }},
                new HashMap<>()
                {{
                    put("stalker", "3_152");
                    put("executor", "4_167");
                    put("tie", "1_304");
                    put("tie2", "1_304");
                    put("bca", "1_235");
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
         * Gergall 2015 Example 2 / option A: Combined Attack (1_75) combining
         * Enhanced Proton Torpedoes (9_88) + 2 Intruder Missiles (7_159).
         * Draws 1, 2, 3. Draw mods stay per draw. TOTAL mods apply once to the grand total:
         * Enhanced Proton Torpedoes +1 vs capital once, Intruder Missile +3 once (same title,
         * not +6). Total 1+2+3+1+3=10. Stalker armor 7: hit. Do not add +1 or +3 again at resolve.
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
        fireOneShot(scn, stalker, 2);
        assertFalse(stalker.isHit());
        fireOneShot(scn, stalker, 3);
        finishCombinedAttack(scn);
        assertTrue("Stalker armor 7 must be hit by Combined Attack Gergall Example 2 total 10", stalker.isHit());
        assertEquals(7, scn.GetDefense(stalker));
        String log = gameLog(scn);
        assertTrue("Combined Attack destinies 1 + 2 + 3 = 6. LOG:\n" + log, log.contains("1 + 2 + 3 = 6"));
        assertTrue("Grand total is 10 after Enhanced Proton Torpedoes +1 and Intruder Missile +3 once. LOG:\n" + log,
                log.contains("Total weapon destiny 10"));
        assertFalse("Do not apply Enhanced Proton Torpedoes +1 again at Combined Attack resolve", log.contains("10+1="));
        assertFalse("Do not apply Intruder Missile +3 twice (same title once, not +6)", log.contains("2 + 5 + 4 = 11"));
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
        fireOneShot(scn, executor, 2);
        fireOneShot(scn, executor, 3);
        finishCombinedAttack(scn);

        assertFalse("Executor armor 12 must not be hit by Combined Attack Gergall Example 2 total 10", executor.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack destinies 1 + 2 + 3 = 6 vs Executor. LOG:\n" + log, log.contains("1 + 2 + 3 = 6"));
        assertTrue("Grand total is 10 vs Executor. LOG:\n" + log, log.contains("Total weapon destiny 10"));
        assertFalse("Do not apply Enhanced Proton Torpedoes +1 again at Combined Attack resolve vs Executor", log.contains("10+1="));
        assertFalse("Do not apply Intruder Missile +3 twice vs Executor", log.contains("2 + 5 + 4 = 11"));
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
    public void CombinedAttackTwoHeavyTurbolaserBatteriesVsExecutorAppliesMinusOneOnce() {
        // Two Heavy Turbolaser Batteries (9_89) vs Executor (capital, armor 12).
        // Draws 4,3 then 4,3. Draw sum 7+7=14. Heavy Turbolaser Battery -1 vs capital applies
        // ONCE to the grand total (same title), total 13 > 12 hit.
        // Wrong subtotal-per-firing model is 6+6=12 miss.
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
        assertEquals("First firing displayed total is draws only (4+3=7), not Heavy Turbolaser Battery -1 yet", 7, lastTotalWeaponDestiny(scn));
        fireTwoDestinyShot(scn, executor, 4, 3);
        assertEquals("Second firing displayed total is draws only (4+3=7)", 7, lastTotalWeaponDestiny(scn));
        finishCombinedAttack(scn);

        assertEquals(12, scn.GetDefense(executor));
        assertTrue("Heavy Turbolaser Battery -1 once on 14 is 13 > Executor armor 12", executor.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack destinies 7 + 7 = 14. LOG:\n" + log, log.contains("7 + 7 = 14"));
        assertTrue("Grand total 13 after Heavy Turbolaser Battery -1 once. LOG:\n" + log, log.contains("Total weapon destiny 13"));
        assertFalse("Do not subtract Heavy Turbolaser Battery -1 per firing (6 + 6 = 12)", log.contains("6 + 6 = 12"));
    }

    @Test
    public void CombinedAttackTwoHeavyTurbolaserBatteriesVsTieAppliesMinusSixOnce() {
        // Same two Heavy Turbolaser Batteries (9_89) vs a TIE (not capital). Heavy Turbolaser Battery -6
        // once on the grand total. Draws 4,3 then 4,3: 7+7=14, minus 6 once = 8 > maneuver 3 hit.
        // Wrong subtotal-per-firing model is (7-6)+(7-6)=2 miss.
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

        fireTwoDestinyShot(scn, tie, 4, 3);
        assertFalse("First Combined Attack Heavy Turbolaser Battery firing must not resolve a hit by itself", tie.isHit());
        assertEquals("First firing displayed total is draws only (4+3=7), not Heavy Turbolaser Battery -6 yet", 7, lastTotalWeaponDestiny(scn));
        fireTwoDestinyShot(scn, tie, 4, 3);
        assertEquals("Second firing displayed total is draws only (4+3=7)", 7, lastTotalWeaponDestiny(scn));
        finishCombinedAttack(scn);

        assertEquals(3, scn.GetDefense(tie));
        assertTrue("Heavy Turbolaser Battery -6 once on 14 is 8 > TIE maneuver 3", tie.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack destinies 7 + 7 = 14. LOG:\n" + log, log.contains("7 + 7 = 14"));
        assertTrue("Grand total 8 after Heavy Turbolaser Battery -6 once. LOG:\n" + log, log.contains("Total weapon destiny 8"));
        assertFalse("Do not subtract Heavy Turbolaser Battery -6 per firing", log.contains("0 + 0 = 0"));
    }

    @Test
    public void CombinedAttackDefianceTargetingComputerHeavyTurbolaserEachDrawAndTotalOnce() {
        // Defiance (9_67) +2 each draw, Targeting Computer (1_39) -1 each draw while fire-twice,
        // Heavy Turbolaser Battery (9_89) -1 once on the Combined Attack grand total vs Executor.
        // Printed 1,1 then 1,1 (Targeting Computer) then 1,1 (second battery).
        // Draws: (1+2-1)+(1+2-1)=4 and 4, then (1+2)+(1+2)=6. Draw sum 4+4+6=14.
        // Heavy Turbolaser Battery -1 once (same title, including Targeting Computer twice on one copy): 13 > 12 hit.
        // Wrong subtotal-per-firing model is 3+3+5=11 miss.
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
        assertEquals("First Targeting Computer firing is draws only (4), not Heavy Turbolaser Battery -1 yet", 4, lastTotalWeaponDestiny(scn));
        fireTwoDestinyShot(scn, executor, 1, 1);
        assertFalse("Second Targeting Computer Combined Attack firing must not resolve a hit by itself", executor.isHit());
        assertEquals("Second Targeting Computer firing is draws only (4); total -1 is not per firing", 4, lastTotalWeaponDestiny(scn));
        fireTwoDestinyShot(scn, executor, 1, 1);
        assertEquals("Second Heavy Turbolaser Battery firing is draws only (6). LOG:\n" + gameLog(scn), 6, lastTotalWeaponDestiny(scn));
        finishCombinedAttack(scn);

        assertWeaponDestinyDrawValues(scn, 2, 2, 2, 2, 3, 3);
        assertEquals(12, scn.GetDefense(executor));
        assertTrue("Targeting Computer twice still applies Heavy Turbolaser Battery -1 once: 14-1=13 hits Executor", executor.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack destinies 4 + 4 + 6 = 14. LOG:\n" + log, log.contains("4 + 4 + 6 = 14"));
        assertTrue("Grand total 13 after Heavy Turbolaser Battery -1 once. LOG:\n" + log, log.contains("Total weapon destiny 13"));
        assertFalse("Do not subtract Heavy Turbolaser Battery -1 per Targeting Computer firing (3 + 3 + 5 = 11)", log.contains("3 + 3 + 5 = 11"));
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

    @Test
    public void CombinedAttackTwoConcussionMissilesAddsPlusOneOnceVsStarfighter() {
        // Two Concussion Missiles (9_87) vs a TIE (starfighter, maneuver 3).
        // Draws 1 and 1. Draw sum 2. Concussion Missiles +1 if targeting a starfighter
        // applies ONCE to the grand total (same title): 3 is not > 3, miss.
        // Wrong subtotal-per-firing model is (1+1)+(1+1)=4 > 3 hit.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var missiles = scn.GetLSCard("missiles");
        var missiles2 = scn.GetLSCard("missiles2");
        var tie = scn.GetDSCard("tie");
        setupTwoConcussionMissiles(scn);
        scn.MoveCardsToHand(ca);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, missiles, missiles2);

        fireOneShot(scn, tie, 1);
        assertFalse("First Combined Attack Concussion Missiles firing must not resolve a hit by itself", tie.isHit());
        fireOneShot(scn, tie, 1);
        finishCombinedAttack(scn);

        assertEquals(3, scn.GetDefense(tie));
        assertFalse("Concussion Missiles +1 once on 2 is 3, not > TIE maneuver 3", tie.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack destinies 1 + 1 = 2. LOG:\n" + log, log.contains("1 + 1 = 2"));
        assertTrue("Grand total 3 after Concussion Missiles +1 once. LOG:\n" + log, log.contains("Total weapon destiny 3"));
        assertFalse("Do not add Concussion Missiles +1 per copy (that would be 4)", log.contains("1 + 1 = 2") && log.contains("Total weapon destiny 4"));
    }

    @Test
    public void CombinedAttackConcussionMissilesAndQuadLaserCannonPlusOnesStack() {
        // Falcon (1_143) with Concussion Missiles (9_87) and Corellian Corvette (1_140)
        // with Quad Laser Cannon (1_159) Combined Attack a TIE (starfighter, maneuver 3).
        // Different titles: Concussion Missiles +1 and Quad Laser Cannon +1 both apply (+2).
        // Draws 1 and 1. Draw sum 2. Total 4 > 3 hit.
        // Collapsing different titles to one +1 is 3, not > 3, miss.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var missiles = scn.GetLSCard("missiles");
        var qlc = scn.GetLSCard("qlc");
        var tie = scn.GetDSCard("tie");
        setupFalconConcussionMissilesAndCorvetteQuadLaser(scn);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(6);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, missiles, qlc);

        fireOneShot(scn, tie, 1);
        assertFalse("First Combined Attack destiny must not resolve a hit by itself", tie.isHit());
        fireOneShot(scn, tie, 1, 1);
        finishCombinedAttack(scn);

        assertEquals(3, scn.GetDefense(tie));
        assertTrue("Concussion Missiles +1 and Quad Laser Cannon +1 stack to +2: 2+2=4 > TIE maneuver 3", tie.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack destinies 1 + 1 = 2. LOG:\n" + log, log.contains("1 + 1 = 2"));
        assertTrue("Total modifiers +2 from different titles. LOG:\n" + log, log.contains("Total modifiers +2"));
        assertTrue("Grand total 4 after stacked +2. LOG:\n" + log, log.contains("Total weapon destiny 4"));
        assertFalse("Do not collapse different titles to one +1 (that would be 3)", log.contains("Total weapon destiny 3"));
    }

    @Test
    public void CombinedAttackEptAndIntruderMissileStackAfterIonCannonTargetsCapital() {
        // First fire SW-4 Ion Cannon (2_081) on a B-wing at Stalker (ordinary, not Combined Attack).
        // Destiny 1 does not beat armor 7, so Stalker is not ionized, but it was targeted this turn.
        // Then Combined Attack Enhanced Proton Torpedoes (9_88) + Intruder Missile (7_159)
        // at the same Stalker. Different titles: Enhanced Proton Torpedoes +1 vs capital and
        // Intruder Missile +3 stack = +4. Draws 1 and 3. Draw sum 4. Total 8 > 7 hit.
        // Enhanced Proton Torpedoes only +1 is 5 miss; Intruder Missile only +3 is 7 miss.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var sw4 = scn.GetLSCard("sw4");
        var ept = scn.GetLSCard("ept");
        var im = scn.GetLSCard("im");
        var stalker = scn.GetDSCard("stalker");
        setupIonCannonThenEptAndIntruderMissileVsStalker(scn);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(8);

        scn.StartBattleAndSkipToWeaponsSegment();
        assertTrue(scn.LSCardActionAvailable(sw4, "Fire"));
        scn.LSUseCardAction(sw4, "Fire");
        fireOneShot(scn, stalker, 1);
        scn.PassAllResponses();
        passDarkSideWeaponsIfNeeded(scn);
        assertFalse("SW-4 Ion Cannon destiny 1 must not ionize Stalker armor 7", stalker.isHit());
        assertEquals(7, scn.GetDefense(stalker));

        playCombinedAttack(scn, stalker, ept, im);
        fireOneShot(scn, stalker, 1);
        assertFalse("First Combined Attack destiny must not resolve a hit by itself", stalker.isHit());
        fireOneShot(scn, stalker, 3);
        finishCombinedAttack(scn);

        assertEquals(7, scn.GetDefense(stalker));
        assertTrue("Enhanced Proton Torpedoes +1 and Intruder Missile +3 stack to +4: 4+4=8 > Stalker armor 7", stalker.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack destinies 1 + 3 = 4. LOG:\n" + log, log.contains("1 + 3 = 4"));
        assertTrue("Total modifiers +4 from different titles. LOG:\n" + log, log.contains("Total modifiers +4"));
        assertTrue("Grand total 8 after stacked +4. LOG:\n" + log, log.contains("Total weapon destiny 8"));
        assertFalse("Do not keep only Enhanced Proton Torpedoes +1 (that would be 5)", log.contains("Total weapon destiny 5"));
        assertFalse("Do not keep only Intruder Missile +3 (that would be 7)", log.contains("Total weapon destiny 7"));
    }

    @Test
    public void CombinedAttackThreeEnhancedProtonTorpedoesAppliesMinusOneOnceVsStarfighter() {
        // Three B-wing Bombers (9_66) each with Enhanced Proton Torpedoes (9_88)
        // Combined Attack a TIE (not capital). Enhanced Proton Torpedoes -1 vs non-capital
        // applies once (same title), not -3. B-wing Bomber ion-cannon-draw +3 does not apply
        // (they are firing Enhanced Proton Torpedoes, not ion cannons).
        // Draws 2, 2, 1. Draw sum 5. Total 4 > 3 hit. Wrong -1 per copy (-3) is 2 miss.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var ept = scn.GetLSCard("ept");
        var ept2 = scn.GetLSCard("ept2");
        var ept3 = scn.GetLSCard("ept3");
        var tie = scn.GetDSCard("tie");
        setupThreeBwingBombersWithEnhancedProtonTorpedoes(scn);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(8);

        scn.StartBattleAndSkipToWeaponsSegment();
        playCombinedAttack(scn, tie, ept, ept2, ept3);

        fireOneShot(scn, tie, 2);
        assertFalse("First Combined Attack destiny must not resolve a hit by itself", tie.isHit());
        fireOneShot(scn, tie, 2);
        fireOneShot(scn, tie, 1);
        finishCombinedAttack(scn);

        assertWeaponDestinyDrawValues(scn, 2, 2, 1);
        assertEquals(3, scn.GetDefense(tie));
        assertTrue("Enhanced Proton Torpedoes -1 once on 5 is 4 > TIE maneuver 3", tie.isHit());
        String log = gameLog(scn);
        assertTrue("Combined Attack destinies 2 + 2 + 1 = 5. LOG:\n" + log, log.contains("2 + 2 + 1 = 5"));
        assertTrue("Total modifiers -1 once. LOG:\n" + log, log.contains("Total modifiers -1"));
        assertTrue("Grand total 4 after Enhanced Proton Torpedoes -1 once. LOG:\n" + log, log.contains("Total weapon destiny 4"));
        assertFalse("Do not subtract Enhanced Proton Torpedoes -1 per copy (that would be -3)", log.contains("Total modifiers -3"));
        assertFalse("B-wing Bomber ion-cannon-draw +3 must not apply to Enhanced Proton Torpedoes", log.contains("5 + 5 + 4"));
    }

    @Test
    public void BoringConversationAnywayCancelsCombinedAttackBeforeWeaponsFire() {
        // Sense (1_267) / Boring Conversation Anyway (1_235) cancel Combined Attack (1_75)
        // before any weapon fires. Weapons may still fire normally afterward.
        // This test uses Boring Conversation Anyway because it names Combined Attack and
        // does not need a Sense destiny draw against a highest-ability character.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var bca = scn.GetDSCard("bca");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var tie = scn.GetDSCard("tie");
        setupTwoXwingLasers(scn);
        scn.MoveCardsToHand(ca);
        scn.MoveCardsToHand(bca);

        scn.StartBattleAndSkipToWeaponsSegment();
        if (scn.LSCardPlayAvailable(ca)) {
            scn.LSPlayCard(ca);
        }
        else {
            scn.LSUseCardAction(ca);
        }
        if (scn.LSHasCardChoiceAvailable(tie)) {
            scn.LSChooseCard(tie);
        }
        if (scn.LSGetDecision() != null) {
            scn.LSChooseCards(xwlc, xwlc2);
        }

        // Light Side must pass Combined Attack optional responses so Dark Side can cancel.
        // Do not use PassResponses here: that helper passes the last actor, and after Dark Side
        // plays Boring Conversation Anyway it will not pass Light Side's leftover Combined Attack window.
        int lsPass = 0;
        while (lsPass++ < 8 && scn.LSDecisionAvailable("Combined Attack") && scn.LSDecisionAvailable("Optional")) {
            scn.LSPass();
        }
        assertTrue("Boring Conversation Anyway must be able to cancel Combined Attack before weapons fire. "
                        + decisionDump(scn),
                scn.DSCardActionAvailable(bca) || scn.DSPlayUsedInterruptAvailable(bca)
                        || scn.DSActionAvailable("Combined Attack")
                        || scn.DSActionAvailable("Boring Conversation Anyway"));
        if (scn.DSPlayUsedInterruptAvailable(bca)) {
            scn.DSPlayCard(bca);
        }
        else if (scn.DSCardActionAvailable(bca)) {
            scn.DSUseCardAction(bca);
        }
        else if (scn.DSActionAvailable("Combined Attack")) {
            scn.DSChooseAction("Combined Attack");
        }
        else {
            scn.DSChooseAction("Boring Conversation Anyway");
        }
        int afterCancel = 0;
        while (afterCancel++ < 12) {
            if (scn.AwaitingLSWeaponsSegmentActions()) {
                break;
            }
            if (scn.AwaitingDSWeaponsSegmentActions()) {
                scn.DSPass();
                continue;
            }
            if (scn.DSDecisionAvailable("Playing") || scn.DSDecisionAvailable("Optional")
                    || scn.DSDecisionAvailable("PUT_IN_CARD_PILE") || scn.DSDecisionAvailable("PLACE_IN_CARD_PILE")) {
                scn.DSPass();
                continue;
            }
            if (scn.LSDecisionAvailable("Playing") || scn.LSDecisionAvailable("Optional")
                    || scn.LSDecisionAvailable("PUT_IN_CARD_PILE") || scn.LSDecisionAvailable("PLACE_IN_CARD_PILE")) {
                scn.LSPass();
                continue;
            }
            break;
        }

        assertFalse("Canceled Combined Attack must not hit the TIE", tie.isHit());
        assertTrue("After Boring Conversation Anyway cancels Combined Attack, Light Side should still be in weapons segment. "
                        + decisionDump(scn),
                scn.AwaitingLSWeaponsSegmentActions());
        assertTrue("X-wing Laser Cannon must still be fireable after Combined Attack is canceled. " + decisionDump(scn),
                scn.LSCardActionAvailable(xwlc));
        assertCombinedAttackStateCleared(scn);
    }

    @Test
    public void CombinedAttackCannotChooseAlreadyFiredWeapon() {
        // Already-fired weapons cannot be chosen for Combined Attack (1_75).
        // Fire one X-wing Laser Cannon (7_162) first; Combined Attack still needs two remaining
        // legal starship weapons and must not list the already-fired cannon.
        var scn = GetScenario();
        var ca = scn.GetLSCard("ca");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var sw4 = scn.GetLSCard("sw4");
        var tie = scn.GetDSCard("tie");
        setupTwoXwingLasersAndBwingIon(scn);
        scn.MoveCardsToHand(ca);
        scn.EnsureLSForcePile(8);

        scn.StartBattleAndSkipToWeaponsSegment();
        assertTrue(scn.LSCardActionAvailable(xwlc, "Fire"));
        scn.LSUseCardAction(xwlc, "Fire");
        fireOneShot(scn, tie, 1, 0);
        scn.PassAllResponses();
        passDarkSideWeaponsIfNeeded(scn);

        assertTrue("Combined Attack remains playable with two leftover weapons. " + decisionDump(scn),
                scn.LSCardPlayAvailable(ca) || scn.LSCardActionAvailable(ca));
        if (scn.LSCardPlayAvailable(ca)) {
            scn.LSPlayCard(ca);
        }
        else {
            scn.LSUseCardAction(ca);
        }
        if (scn.LSHasCardChoiceAvailable(tie)) {
            scn.LSChooseCard(tie);
        }
        assertFalse("Already-fired X-wing Laser Cannon cannot be chosen for Combined Attack. " + decisionDump(scn),
                scn.LSHasCardChoiceAvailable(xwlc));
        assertTrue("Unused X-wing Laser Cannon remains choosable", scn.LSHasCardChoiceAvailable(xwlc2));
        assertTrue("Unused SW-4 Ion Cannon remains choosable", scn.LSHasCardChoiceAvailable(sw4));
        scn.LSChooseCards(xwlc2, sw4);
        scn.PassCardPlayResponses();
        fireOneShot(scn, tie, 4, 0);
        fireOneShot(scn, tie, 4);
        finishCombinedAttack(scn, 1);
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

    /**
     * Two X-wing Laser Cannons and a B-wing SW-4 Ion Cannon vs a TIE, for the already-fired Combined Attack test.
     */
    private void setupTwoXwingLasersAndBwingIon(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var xwing = scn.GetLSCard("xwing");
        var xwing2 = scn.GetLSCard("xwing2");
        var bwing = scn.GetLSCard("bwing");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var sw4 = scn.GetLSCard("sw4");
        var tie = scn.GetDSCard("tie");
        scn.MoveCardsToLocation(system, xwing, xwing2, bwing, tie);
        scn.AttachCardsTo(xwing, xwlc);
        scn.AttachCardsTo(xwing2, xwlc2);
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
        var bomber = scn.GetLSCard("bomber");
        var bwing2 = scn.GetLSCard("bwing2");
        var ept = scn.GetLSCard("ept");
        var im = scn.GetLSCard("im");
        var im2 = scn.GetLSCard("im2");
        var target = vsExecutor ? scn.GetDSCard("executor") : scn.GetDSCard("stalker");
        scn.MoveCardsToLocation(system, ywing, bomber, bwing2, target);
        scn.AttachCardsTo(ywing, ept);
        scn.AttachCardsTo(bomber, im);
        scn.AttachCardsTo(bwing2, im2);
    }

    /**
     * Two B-wing Bombers with Concussion Missiles vs a TIE.
     */
    private void setupTwoConcussionMissiles(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var bomber = scn.GetLSCard("bomber");
        var bwing2 = scn.GetLSCard("bwing2");
        var missiles = scn.GetLSCard("missiles");
        var missiles2 = scn.GetLSCard("missiles2");
        var tie = scn.GetDSCard("tie");
        scn.MoveCardsToLocation(system, bomber, bwing2, tie);
        scn.AttachCardsTo(bomber, missiles);
        scn.AttachCardsTo(bwing2, missiles2);
    }

    /**
     * Falcon with Concussion Missiles and Corellian Corvette with Quad Laser Cannon vs a TIE.
     * Rebel Pilot (1_27) aboard Falcon so it can fire.
     */
    private void setupFalconConcussionMissilesAndCorvetteQuadLaser(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var falcon = scn.GetLSCard("falcon");
        var corvette = scn.GetLSCard("corvette");
        var missiles = scn.GetLSCard("missiles");
        var qlc = scn.GetLSCard("qlc");
        var tie = scn.GetDSCard("tie");
        var pilot = scn.GetLSCard("pilot");
        scn.MoveCardsToLocation(system, falcon, corvette, tie);
        scn.BoardAsPilot(falcon, pilot);
        scn.AttachCardsTo(falcon, missiles);
        scn.AttachCardsTo(corvette, qlc);
    }

    /**
     * B-wing SW-4 Ion Cannon, Y-wing Enhanced Proton Torpedoes, and B-wing Bomber
     * Intruder Missile vs Stalker. Fire the ion cannon first (not Combined Attack), then
     * Combined Attack the other two weapons at the same capital.
     */
    private void setupIonCannonThenEptAndIntruderMissileVsStalker(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var bwing = scn.GetLSCard("bwing");
        var ywing = scn.GetLSCard("ywing");
        var bomber = scn.GetLSCard("bomber");
        var sw4 = scn.GetLSCard("sw4");
        var ept = scn.GetLSCard("ept");
        var im = scn.GetLSCard("im");
        var stalker = scn.GetDSCard("stalker");
        scn.MoveCardsToLocation(system, bwing, ywing, bomber, stalker);
        scn.AttachCardsTo(bwing, sw4);
        scn.AttachCardsTo(ywing, ept);
        scn.AttachCardsTo(bomber, im);
    }

    /**
     * Three B-wing Bombers each with Enhanced Proton Torpedoes vs a TIE.
     */
    private void setupThreeBwingBombersWithEnhancedProtonTorpedoes(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var bomber = scn.GetLSCard("bomber");
        var bwing2 = scn.GetLSCard("bwing2");
        var bomber3 = scn.GetLSCard("bomber3");
        var ept = scn.GetLSCard("ept");
        var ept2 = scn.GetLSCard("ept2");
        var ept3 = scn.GetLSCard("ept3");
        var tie = scn.GetDSCard("tie");
        scn.MoveCardsToLocation(system, bomber, bwing2, bomber3, tie);
        scn.AttachCardsTo(bomber, ept);
        scn.AttachCardsTo(bwing2, ept2);
        scn.AttachCardsTo(bomber3, ept3);
    }

    /**
     * Pass Light Side optional responses so Dark Side can cancel Combined Attack.
     */
    private void passLightSideOptionalResponses(VirtualTableScenario scn) {
        int safety = 0;
        while (safety++ < 6 && scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null) {
            String text = scn.LSGetDecision().getText().toLowerCase();
            if (!(text.contains("optional") || text.contains("playing"))) {
                break;
            }
            if (scn.DSGetDecision() != null && scn.DSCardActionAvailable(scn.GetDSCard("bca"))) {
                break;
            }
            scn.LSPass();
        }
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
