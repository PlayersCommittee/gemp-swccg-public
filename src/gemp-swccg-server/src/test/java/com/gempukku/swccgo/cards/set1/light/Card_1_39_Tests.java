package com.gempukku.swccgo.cards.set1.light;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_39_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("tc", "1_39");
                    put("karie", "9_18");
                    put("pilot", "1_27");
                    put("red7", "7_146");
                    put("xwing", "1_146");
                    put("ywing", "1_147");
                    put("xwlc", "7_162");
                    put("sw4", "2_081");
                    put("homeone", "9_74");
                    put("htb", "9_89");
                    put("ept", "9_88");
                    put("defiance", "9_67");
                    put("dack", "3_4");
                    put("verrack", "9_7");
                    put("cec", "7_56");
                    put("corellia", "2_61");
                    put("falcon", "1_143");
                    put("gunner", "3_17");
                    put("qlc", "1_159");
                    put("tennumb", "9_29");
                    put("blue5", "9_63");
                    put("missiles", "9_87");
                }},
                new HashMap<>()
                {{
                    put("stalker", "3_152");
                    put("tie", "1_304");
                    put("tie2", "1_304");
                    put("vsd", "2_155");
                    put("executor", "4_167");
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
    public void TargetingComputerStatsAndKeywordsAreCorrect() {
        /**
         * Title: Targeting Computer
         * Uniqueness: Unrestricted
         * Side: Light
         * Type: Device
         * Destiny: 3
         * Game Text: Use 2 Force to deploy on any starship. Adds 1 to starship's maneuver. If this starship is using a
         *      weapon during a battle, you may fire that weapon twice, separately or combined. Subtract 1 from each
         *      destiny draw when firing.
         * Lore: Specially designed for use on Rebel starfighters. Assists pilots on torpedo runs. Automatically locks
         *      on pre-programmed target points.
         * Set: Premiere
         * Rarity: U1
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("tc").getBlueprint();

        assertEquals("Targeting Computer", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertTrue(card.isCardType(CardType.DEVICE));
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(Rarity.U1, card.getRarity());
    }

    @Test
    public void TargetingComputerDeploysOnStarshipForTwoForceAndAddsOneManeuver() {
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var red7 = scn.GetLSCard("red7");
        var karie = scn.GetLSCard("karie");
        scn.MoveCardsToHand(tc);

        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(system, red7);
        scn.BoardAsPilot(red7, karie);

        assertEquals(4, scn.GetManeuver(red7));

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(tc));
        scn.LSDeployCard(tc);
        assertTrue(scn.LSHasCardChoiceAvailable(red7));
        scn.LSChooseCard(red7);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(red7, tc));
        assertEquals(2, scn.GetDeployCost(tc));
        assertEquals(5, scn.GetManeuver(red7));
    }

    @Test
    public void TargetingComputerCannotBeUsedOutsideBattle() {
        var scn = GetScenario();
        setupRed7(scn, true);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("tc"), "Fire a weapon twice"));

        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("tc"), "Fire a weapon twice"));
    }

    @Test
    public void TargetingComputerIsLimitedToOneUsagePerDevicePerBattle() {
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var ept = scn.GetLSCard("ept");
        var stalker = scn.GetDSCard("stalker");
        setupRed7(scn, true);

        scn.StartBattleAndSkipToWeaponsSegment();

        assertTrue(scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");
        fireOneShot(scn, stalker, 1);
        fireOneShot(scn, stalker, 1);

        scn.PassAllResponses();
        // After the weapon has been fired twice, LS may have no remaining weapons actions
        // and the engine auto-passes to DS. Either way the device must not be usable again.
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            assertFalse(scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
        }
        else {
            assertTrue("Expected a weapons segment after TC usage. Current decision: "
                            + (scn.GetCurrentDecision() == null ? "null" : scn.GetCurrentDecision().getText()),
                    scn.AwaitingDSWeaponsSegmentActions());
        }
    }

    @Test
    public void TargetingComputerFiresTwiceSeparatelyPayingForceTwiceAndCanRetarget() {
        // Red 7 fires Proton Torpedoes for free, so use a generic X-wing (permanent pilot) + EPT.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var ept = scn.GetLSCard("ept");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        setupXwing(scn);

        scn.StartBattleAndSkipToWeaponsSegment();

        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");

        // Without Karie, dest 7 vs TIE: 7 -1 TC -1 EPT (non-capital) = 5 vs maneuver 3 = hit.
        fireOneShot(scn, tie, 7);
        assertTrue(tie.isHit());
        assertFalse(tie2.isHit());

        fireOneShot(scn, tie2, 7);
        assertTrue(tie2.isHit());

        // EPT costs 1 Force per firing; both shots of the overall action must pay.
        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void TargetingComputerCombinedAddsDrawsThenAppliesTotalModifiersOnce() {
        /**
         * AR Example 1 structure (Karie + EPT + TC vs Stalker armor 7):
         * Each draw: printed + Karie +1 + TC -1. Combined sum, then EPT +1 vs capital, then compare to DV.
         * AR lists destinies 4 then 2 "for a total of 7" then +1 = 8 vs 7. Rule-text math for 4 then 2 is
         * (4+1-1)+(2+1-1)=6, then +1 total = 7 vs 7, which does not hit (need > DV). Destinies 4 then 3
         * produce the intended hit: (4+1-1)+(3+1-1)=7, +1 total = 8 vs 7.
         */
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var ept = scn.GetLSCard("ept");
        var stalker = scn.GetDSCard("stalker");
        setupRed7(scn, true);

        scn.StartBattleAndSkipToWeaponsSegment();

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");

        fireOneShot(scn, stalker, 4);
        assertFalse("First combined shot must not resolve a hit by itself", stalker.isHit());

        fireOneShot(scn, stalker, 3);
        assertTrue(stalker.isHit());
        assertEquals(7, scn.GetDefense(stalker));
    }

    @Test
    public void TargetingComputerSubtractsOneFromEachDrawNotFromCombinedTotal() {
        // Without Karie: destinies 4 and 4 combined.
        // Per-draw TC -1: (4-1)+(4-1)=6, EPT +1 vs capital = 7 vs Stalker 7 = miss.
        // If -1 were applied once to the total: 4+4-1+1 = 8 vs 7 = hit.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var ept = scn.GetLSCard("ept");
        var stalker = scn.GetDSCard("stalker");
        setupRed7(scn, false);

        scn.StartBattleAndSkipToWeaponsSegment();

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");

        fireOneShot(scn, stalker, 4);
        fireOneShot(scn, stalker, 4);

        assertFalse("TC -1 must apply to each draw, not once to the combined total", stalker.isHit());
    }

    @Test
    public void TargetingComputerCombinedCanHitWhenSeparateShotsWouldMiss() {
        // With Karie, destinies 4 and 4:
        // Separately each shot is (4+1-1)+1 EPT = 5 vs 7 miss.
        // Combined: (4+1-1)+(4+1-1)+1 EPT = 9 vs 7 hit.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var ept = scn.GetLSCard("ept");
        var stalker = scn.GetDSCard("stalker");
        setupRed7(scn, true);

        scn.StartBattleAndSkipToWeaponsSegment();

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");

        fireOneShot(scn, stalker, 4);
        assertFalse(stalker.isHit());
        fireOneShot(scn, stalker, 4);
        assertTrue(stalker.isHit());
    }


    @Test
    public void TargetingComputerFiresXwingLaserCannonTwiceSeparately() {
        // 7_162 X-wing Laser Cannon: may use X=0..3 Force; destiny + X > DV to hit.
        // With X=0 vs TIE maneuver 3: dest 5, TC -1 => 4 > 3 hit. Retarget second TIE.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var xwlc = scn.GetLSCard("xwlc");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        setupXwingLaser(scn);

        scn.StartBattleAndSkipToWeaponsSegment();

        assertTrue(scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");

        fireOneShot(scn, tie, 5, 0);
        assertTrue(tie.isHit());
        assertFalse(tie2.isHit());

        fireOneShot(scn, tie2, 5, 0);
        assertTrue(tie2.isHit());
    }

    @Test
    public void TargetingComputerFiresXwingLaserCannonTwiceCombined() {
        // Combined X=0: first dest 4 is (4-1)=3 stored, not yet a hit vs TIE 3.
        // Second dest 4: (4-1)=3. Combined 6 vs 3 hits the single target.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var xwlc = scn.GetLSCard("xwlc");
        var tie = scn.GetDSCard("tie");
        setupXwingLaser(scn);

        scn.StartBattleAndSkipToWeaponsSegment();

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");

        fireOneShot(scn, tie, 4, 0);
        assertFalse("First combined X-wing Laser Cannon shot must not resolve a hit by itself", tie.isHit());

        fireOneShot(scn, tie, 4, 0);
        assertTrue(tie.isHit());
    }

    @Test
    public void TargetingComputerCombinedXwingLaserCannonX3LosesTieNotMerelyHit() {
        // Combined twice at X=3: destinies 4 then 4, TC -1 each => 3+3=6, then +X=3 > TIE DV 3.
        // XWLC must lose the TIE (not HitCardEffect).
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var xwlc = scn.GetLSCard("xwlc");
        var tie = scn.GetDSCard("tie");
        setupXwingLaser(scn);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(6);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");

        fireOneShot(scn, tie, 4, 3);
        assertFalse("First combined XWLC shot must not resolve a hit or loss by itself", tie.isHit());
        assertEquals(Zone.AT_LOCATION, tie.getZone());

        fireOneShot(scn, tie, 4, 3);
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassAllResponses();
        assertTrue("Combined X-wing Laser Cannon at X=3 must lose the TIE, not merely hit",
                tie.getZone() == Zone.LOST_PILE || tie.getZone() == Zone.TOP_OF_LOST_PILE);
        assertFalse(tie.getZone() == Zone.AT_LOCATION);
    }

    @Test
    public void TargetingComputerFiresSw4IonCannonTwiceSeparatelyAndIonizes() {
        // 2_081 SW-4 Ion Cannon (LS ion cannon; Premiere 1_318 is DS Star Destroyer only).
        // Cost 1 Force. Destiny > DV ionizes (armor/maneuver=0, hyperspeed=0); does not "hit".
        // dest 5, TC -1 => 4 > TIE 3 ionize.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var sw4 = scn.GetLSCard("sw4");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        setupYwingIon(scn);

        scn.StartBattleAndSkipToWeaponsSegment();

        int forceBefore = scn.GetLSForcePileCount();
        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");

        fireOneShot(scn, tie, 5);
        assertTrue(scn.IsIonized(tie));
        assertFalse(tie.isHit());
        assertFalse(scn.IsIonized(tie2));

        fireOneShot(scn, tie2, 5);
        assertTrue(scn.IsIonized(tie2));
        assertFalse(tie2.isHit());
        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void TargetingComputerFiresSw4IonCannonTwiceCombinedAndIonizes() {
        // Combined: dest 4 then 4. Each firing (4-1)=3. Combined 6 > TIE 3 ionizes; not a hit.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var sw4 = scn.GetLSCard("sw4");
        var tie = scn.GetDSCard("tie");
        setupYwingIon(scn);

        scn.StartBattleAndSkipToWeaponsSegment();

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");

        fireOneShot(scn, tie, 4);
        assertFalse(scn.IsIonized(tie));
        assertFalse(tie.isHit());

        fireOneShot(scn, tie, 4);
        assertTrue(scn.IsIonized(tie));
        assertFalse("Ion Cannon must ionize, not hit", tie.isHit());
    }

    @Test
    public void TargetingComputerSeparatelySkipsUnpaidSecondShotWithoutRewindingFirst() {
        // Appendix B option A: SW-4 costs 1 Force per shot. With 1 Force remaining,
        // shot 1 pays and ionizes; shot 2 does not initiate; TC is still used.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var sw4 = scn.GetLSCard("sw4");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        setupYwingIon(scn);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.DrainLSForcePileTo(1);
        assertEquals(1, scn.GetLSForcePileCount());

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");

        fireOneShot(scn, tie, 5);
        assertTrue(scn.IsIonized(tie));
        assertFalse(tie.isHit());
        assertFalse(scn.IsIonized(tie2));
        assertEquals(0, scn.GetLSForcePileCount());

        scn.PassAllResponses();
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            assertFalse(scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
        }
        else {
            assertTrue(scn.AwaitingDSWeaponsSegmentActions() || scn.GetCurrentDecision() != null);
        }
        assertFalse("Second TIE must not be ionized when shot 2 cannot pay", scn.IsIonized(tie2));
    }

    @Test
    public void TargetingComputerCombinedAppliesCompletedFiringsWhenSecondShotCannotPay() {
        // Appendix B option A combined: dest 5 stored as (5-1)=4, shot 2 cannot pay.
        // Combined total of completed firings 4 > TIE 3 still ionizes. Action does not fully fail.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var sw4 = scn.GetLSCard("sw4");
        var tie = scn.GetDSCard("tie");
        setupYwingIon(scn);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.DrainLSForcePileTo(1);
        assertEquals(1, scn.GetLSForcePileCount());

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");

        fireOneShot(scn, tie, 5);
        scn.PassAllResponses();

        assertTrue("Completed combined firing must still apply vs the target", scn.IsIonized(tie));
        assertFalse(tie.isHit());
        assertEquals(0, scn.GetLSForcePileCount());
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            assertFalse(scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
        }
    }

    @Test
    public void TargetingComputerFiresHeavyTurbolaserFromHomeOneSeparately() {
        // Capital setup (AR example 2 structure): Home One 9_74 + Heavy Turbolaser Battery 9_89.
        // HTB uses 2 Force, draws 2 destiny, total -1 vs capital. dest 7,7: (7-1)+(7-1)=12, then -1 = 11 vs Stalker 7 hit.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var htb = scn.GetLSCard("htb");
        var stalker = scn.GetDSCard("stalker");
        setupHomeOne(scn);

        scn.StartBattleAndSkipToWeaponsSegment();

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");

        fireTwoDestinyShot(scn, stalker, 7, 7);
        assertTrue(stalker.isHit());
    }

    @Test
    public void TargetingComputerDontFireLeavesDeviceUnusedSoItCanBeUsedLaterThisBattle() {
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var tie = scn.GetDSCard("tie");
        setupXwingLaser(scn);

        scn.StartBattleAndSkipToWeaponsSegment();

        assertTrue(scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Don't Fire (Cancel)");

        assertTrue("Don't Fire must return to weapons-segment actions, got: "
                        + (scn.GetCurrentDecision() == null ? "null" : scn.GetCurrentDecision().getText()),
                scn.AwaitingLSWeaponsSegmentActions());
        assertTrue("Don't Fire must not consume Targeting Computer",
                scn.LSCardActionAvailable(tc, "Fire a weapon twice"));

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");
        fireOneShot(scn, tie, 5, 0);
        assertTrue(tie.isHit());
    }

    @Test
    public void DefianceHeavyTurbolaserVsVictoryClassAddsTwoToEachDraw() {
        // HTB draws two destinies vs VSD (capital): HTB -1 total, not -6 vs starfighter. No Targeting Computer.
        // Printed destinies 2 and 2. No bonus: 2+2-1=3 vs armor 5 miss.
        // +1 each would be (2+1)+(2+1)-1=5 vs 5 miss (equal fails).
        // +2 each is (2+2)+(2+2)-1=7 vs 5 hit. +2 per draw is required; a hypothetical +1 cannot pass.
        var scn = GetScenario();
        var htb = scn.GetLSCard("htb");
        var vsd = scn.GetDSCard("vsd");
        setupDefiance(scn, false);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        assertTrue(scn.LSCardActionAvailable(htb, "Fire"));
        scn.LSUseCardAction(htb, "Fire");
        fireTwoDestinyShot(scn, vsd, 2, 2);

        assertEquals(5, scn.GetDefense(vsd));
        assertTrue("Each HTB draw from Defiance must include +2 (2+2 + 2+2 -1 vs VSD 5)", vsd.isHit());
        assertWeaponDestinyDrawValues(scn, 4, 4);
        assertEquals("total weapon destiny must be 7 so +1 each (5 vs 5 miss) cannot pass",
                7, lastTotalWeaponDestiny(scn));
        assertFalse("Ordinary non-TC shots stay unlabeled", gameLog(scn).contains("firing 1"));
    }

    @Test
    public void DefianceTargetingComputerCombinedAppliesMinusOneAndPlusTwoPerDraw() {
        // TC -1 and Defiance +2 per draw (net +1 vs printed). HTB two draws per firing.
        // Printed 2,2 then 2,2: each firing (2-1+2)*2=6. Combined 12, HTB -1 vs capital = 11 vs VSD 5 hit.
        // Without Defiance +2: (2-1)*2 per firing, combined 4, -1 = 3 vs 5 miss.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var vsd = scn.GetDSCard("vsd");
        setupDefiance(scn, true);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");

        fireTwoDestinyShot(scn, vsd, 2, 2);
        assertFalse("First combined HTB firing must not resolve a hit by itself", vsd.isHit());

        fireTwoDestinyShot(scn, vsd, 2, 2);
        assertTrue("TC -1 and Defiance +2 must both apply per draw", vsd.isHit());
        assertWeaponDestinyDrawValues(scn, 3, 3, 3, 3);

        String log = gameLog(scn);
        assertTrue(log.contains("twice: combined"));
        assertTrue(log.contains("Targeting Computer Combined firing 1"));
        assertTrue(log.contains("Targeting Computer Combined firing 2"));
        assertFalse("No parentheses around the weapon name", log.contains("firing 1 ("));
        assertFalse(log.contains("firing 2 ("));
    }

    @Test
    public void TargetingComputerFiresHeavyTurbolaserFromHomeOneCombined() {
        // Combined HTB vs Stalker: each firing is one weapon destiny (sum of two TC-modified draws).
        // dest 4,4 => (4-1)+(4-1)=6 stored, not a hit yet. Second firing 4,4 => 6. Combined 12, total -1 = 11 vs 7 hit.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var htb = scn.GetLSCard("htb");
        var stalker = scn.GetDSCard("stalker");
        setupHomeOne(scn);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");

        fireTwoDestinyShot(scn, stalker, 4, 4);
        assertFalse("First combined HTB firing must not resolve a hit by itself", stalker.isHit());

        fireTwoDestinyShot(scn, stalker, 4, 4);
        assertTrue(stalker.isHit());
    }


    @Test
    public void DefianceDackVerrackHeavyTurbolaserEachBonusesRequiredVsExecutor() {
        // Dack passenger +1 each, Verrack aboard +2 each vs capital, Defiance +2 each, HTB -1 total vs capital.
        // Printed 2 and 2: each draw 2+2+1+2=7, total 14-1=13 vs Executor armor 12 hit.
        // Missing Dack: 12-1=11 vs 12 miss. Missing Verrack or Defiance is even lower.
        var scn = GetScenario();
        var htb = scn.GetLSCard("htb");
        var executor = scn.GetDSCard("executor");
        setupDefianceGunners(scn, false);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        assertTrue(scn.LSCardActionAvailable(htb, "Fire"));
        scn.LSUseCardAction(htb, "Fire");
        fireTwoDestinyShot(scn, executor, 2, 2);

        assertEquals(12, scn.GetDefense(executor));
        assertWeaponDestinyDrawValues(scn, 7, 7);
        assertEquals("total must be 13 so dropping Dack's +1 each (11 vs 12) cannot pass",
                13, lastTotalWeaponDestiny(scn));
        assertTrue("Dack + Verrack + Defiance each-draw bonuses are all required to hit Executor", executor.isHit());
    }

    @Test
    public void TargetingComputerAttachedDoesNotSubtractFromNormalHeavyTurbolaserFire() {
        // Playtest: Verrack on Defiance fires HTB at Executor with Targeting Computer attached but unused.
        // Printed 3 (HTB) and 6 (Concentrate All Fire): each +2 Defiance +2 Verrack vs capital.
        // Correct: 7 and 10, total 17-1=16 vs armor 12.
        // Unused TC -1 would make 6 and 9, total 14.
        var scn = GetScenario();
        var htb = scn.GetLSCard("htb");
        var executor = scn.GetDSCard("executor");
        setupDefianceVerrack(scn, true);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        assertTrue(scn.LSCardActionAvailable(htb, "Fire"));
        scn.LSUseCardAction(htb, "Fire");
        fireTwoDestinyShot(scn, executor, 3, 6);

        assertEquals(12, scn.GetDefense(executor));
        assertWeaponDestinyDrawValues(scn, 7, 10);
        assertEquals("unused Targeting Computer must not subtract 1 per draw (that would total 14)",
                16, lastTotalWeaponDestiny(scn));
        assertTrue(executor.isHit());
        assertFalse("Ordinary non-TC shots stay unlabeled", gameLog(scn).contains("firing 1"));
    }

    @Test
    public void DefianceDackVerrackTargetingComputerSeparatelyEachBonusesRequiredVsExecutor() {
        // TC -1 each. Printed 3 and 3: each draw 3+2+1+2-1=7, total 14-1=13 vs 12 hit.
        // Missing Dack: 11 vs 12 miss.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var executor = scn.GetDSCard("executor");
        setupDefianceGunners(scn, true);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");
        fireTwoDestinyShot(scn, executor, 3, 3);

        assertWeaponDestinyDrawValues(scn, 7, 7);
        assertEquals(13, lastTotalWeaponDestiny(scn));
        assertTrue("Separately HTB still needs Dack, Verrack, and Defiance each-draw bonuses", executor.isHit());
    }

    @Test
    public void DefianceDackVerrackTargetingComputerCombinedEachBonusesRequiredVsExecutor() {
        // Combined HTB: two draws per firing, twice, then HTB -1 once.
        // Printed 0 x4 with TC -1: each draw 0+2+1+2-1=4, combined 16-1=15 vs 12 hit.
        // Missing Dack: 12-1=11 vs 12 miss.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var executor = scn.GetDSCard("executor");
        setupDefianceGunners(scn, true);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");
        fireTwoDestinyShot(scn, executor, 0, 0);
        assertFalse("First combined HTB firing must not resolve a hit by itself", executor.isHit());
        fireTwoDestinyShot(scn, executor, 0, 0);

        assertWeaponDestinyDrawValues(scn, 4, 4, 4, 4);
        assertEquals("combined total 15 so missing Dack's +1 on all four draws (11 vs 12) cannot pass",
                15, lastTotalWeaponDestiny(scn));
        assertTrue(executor.isHit());
    }

    @Test
    public void FalconCecKarieRogueGunnerQuadLaserEachBonusesRequiredVsVictoryClass() {
        // CEC +2 each on Quad Laser Cannon, Karie +1 each aboard, Rogue Gunner +1 each as passenger.
        // Vs capital: Quad Laser's +1 vs starfighter does not apply. Printed 2: 2+2+1+1=6 vs VSD 5 hit.
        // Missing Karie or Rogue Gunner: 5 vs 5 miss. Missing CEC: 4 vs 5 miss.
        var scn = GetScenario();
        var qlc = scn.GetLSCard("qlc");
        var vsd = scn.GetDSCard("vsd");
        setupFalconCec(scn, false);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        assertTrue(scn.LSCardActionAvailable(qlc, "Fire"));
        scn.LSUseCardAction(qlc, "Fire");
        fireOneShot(scn, vsd, 2);

        assertEquals(5, scn.GetDefense(vsd));
        assertWeaponDestinyDrawValues(scn, 6);
        assertEquals(6, lastTotalWeaponDestiny(scn));
        assertTrue("CEC, Karie, and Rogue Gunner each-draw bonuses are all required to hit VSD", vsd.isHit());
    }

    @Test
    public void FalconCecKarieRogueGunnerTargetingComputerSeparatelyEachBonusesRequiredVsVictoryClass() {
        // TC -1 each. Printed 3: 3+2+1+1-1=6 vs VSD 5 hit. Missing a +1 character: 5 vs 5 miss.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var vsd = scn.GetDSCard("vsd");
        setupFalconCec(scn, true);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");
        fireOneShot(scn, vsd, 3);

        assertWeaponDestinyDrawValues(scn, 6);
        assertEquals(6, lastTotalWeaponDestiny(scn));
        assertTrue(vsd.isHit());
    }

    @Test
    public void FalconCecKarieRogueGunnerTargetingComputerCombinedEachBonusesRequiredVsVictoryClass() {
        // Combined two draws, then Quad Laser total +1 vs starfighter does not apply vs capital.
        // Printed 0 and 0 with TC -1: each 0+2+1+1-1=3, combined 6 vs 5 hit.
        // Missing a +1 character: 2+2=4 vs 5 miss.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var vsd = scn.GetDSCard("vsd");
        setupFalconCec(scn, true);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");
        fireOneShot(scn, vsd, 0);
        assertFalse("First combined Quad Laser firing must not resolve a hit by itself", vsd.isHit());
        fireOneShot(scn, vsd, 0);

        assertWeaponDestinyDrawValues(scn, 3, 3);
        assertEquals(6, lastTotalWeaponDestiny(scn));
        assertTrue(vsd.isHit());
    }

    @Test
    public void TenNumbBlueSquadron5ConcussionMissilesTotalBonusRequiredVsTie() {
        // Blue Squadron 5 +2 each draw. Ten Numb +2 total on a B-wing he pilots. Missiles +1 total vs starfighter.
        // One draw: EACH vs TOTAL look the same. Printed 0 as 2, total 0+2+2+1=5 vs TIE 3 hit.
        // Without Ten Numb: 3 vs 3 miss.
        var scn = GetScenario();
        var missiles = scn.GetLSCard("missiles");
        var tie = scn.GetDSCard("tie");
        setupTenNumbBlue5(scn, false);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        assertTrue(scn.LSCardActionAvailable(missiles, "Fire"));
        scn.LSUseCardAction(missiles, "Fire");
        fireOneShot(scn, tie, 0);

        assertEquals(3, scn.GetDefense(tie));
        assertWeaponDestinyDrawValues(scn, 2);
        assertEquals("total 5 so Ten Numb's +2 is required (3 vs 3 miss)", 5, lastTotalWeaponDestiny(scn));
        assertTrue(tie.isHit());
    }

    @Test
    public void TenNumbBlueSquadron5TargetingComputerSeparatelyTotalBonusRequiredVsTie() {
        // TC -1 each. Printed 0 as 1, then Ten Numb +2 total and missiles +1 total: 1+2+1=4 vs TIE 3 hit.
        // Without Ten Numb: 2 vs 3 miss. One draw still cannot tell EACH from TOTAL.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var tie = scn.GetDSCard("tie");
        setupTenNumbBlue5(scn, true);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");
        fireOneShot(scn, tie, 0);

        assertWeaponDestinyDrawValues(scn, 1);
        assertEquals(4, lastTotalWeaponDestiny(scn));
        assertTrue(tie.isHit());
    }

    @Test
    public void TenNumbBlueSquadron5TargetingComputerCombinedAppliesTotalOnceNotPerDraw() {
        // Combined is the EACH vs TOTAL distinguisher.
        // Printed 0 and 0: each draw 0+2 Blue -1 TC = 1 (Ten Numb must NOT be on the draw).
        // Combined 1+1=2, then Ten Numb +2 total once and missiles +1 once = 5 vs TIE 3 hit.
        // If Ten Numb were wrongly each-draw, draws would log as 3 and 3 and the total would be 7.
        // Without Ten Numb: 3 vs 3 miss.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var tie = scn.GetDSCard("tie");
        setupTenNumbBlue5(scn, true);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Combined");
        fireOneShot(scn, tie, 0);
        assertFalse("First combined Concussion Missiles firing must not resolve a hit by itself", tie.isHit());
        fireOneShot(scn, tie, 0);

        assertWeaponDestinyDrawValues(scn, 1, 1);
        assertEquals("total 5 means Ten Numb +2 applied once; EACH would log 3+3 and total 7",
                5, lastTotalWeaponDestiny(scn));
        assertTrue(tie.isHit());
    }

    private void setupRed7(VirtualTableScenario scn, boolean includeKarie) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var red7 = scn.GetLSCard("red7");
        var ept = scn.GetLSCard("ept");
        var tc = scn.GetLSCard("tc");
        var stalker = scn.GetDSCard("stalker");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");

        scn.MoveCardsToLocation(system, red7, stalker, tie, tie2);
        if (includeKarie) {
            scn.BoardAsPilot(red7, scn.GetLSCard("karie"));
        }
        else {
            scn.BoardAsPilot(red7, scn.GetLSCard("pilot"));
        }
        scn.AttachCardsTo(red7, ept, tc);
    }

    private void setupXwing(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var xwing = scn.GetLSCard("xwing");
        var ept = scn.GetLSCard("ept");
        var tc = scn.GetLSCard("tc");
        var stalker = scn.GetDSCard("stalker");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");

        scn.MoveCardsToLocation(system, xwing, stalker, tie, tie2);
        scn.AttachCardsTo(xwing, ept, tc);
    }

    private void chooseSeparatelyOrCombined(VirtualTableScenario scn, String mode) {
        assertFalse("These setups attach one legal weapon; GEMP must auto-select it instead of prompting",
                scn.LSDecisionAvailable("Choose weapon"));
        assertTrue("Expected Fire a weapon twice prompt, got: "
                        + (scn.GetCurrentDecision() == null ? "null" : scn.GetCurrentDecision().getText()),
                scn.LSDecisionAvailable("Fire a weapon twice"));
        assertTrue(scn.LSChoiceAvailable("Separately"));
        assertTrue(scn.LSChoiceAvailable("Combined"));
        assertTrue(scn.LSChoiceAvailable("Don't Fire (Cancel)"));
        scn.LSChoose(mode);
    }

    private void setupXwingLaser(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var xwing = scn.GetLSCard("xwing");
        var xwlc = scn.GetLSCard("xwlc");
        var tc = scn.GetLSCard("tc");
        var stalker = scn.GetDSCard("stalker");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        scn.MoveCardsToLocation(system, xwing, stalker, tie, tie2);
        scn.AttachCardsTo(xwing, xwlc, tc);
    }

    private void setupYwingIon(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var ywing = scn.GetLSCard("ywing");
        var sw4 = scn.GetLSCard("sw4");
        var tc = scn.GetLSCard("tc");
        var stalker = scn.GetDSCard("stalker");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        scn.MoveCardsToLocation(system, ywing, stalker, tie, tie2);
        scn.AttachCardsTo(ywing, sw4, tc);
    }


    private void setupDefianceVerrack(VirtualTableScenario scn, boolean withTc) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var defiance = scn.GetLSCard("defiance");
        var htb = scn.GetLSCard("htb");
        var executor = scn.GetDSCard("executor");
        scn.MoveCardsToLocation(system, defiance, executor);
        scn.BoardAsPassenger(defiance, scn.GetLSCard("verrack"));
        if (withTc) {
            scn.AttachCardsTo(defiance, htb, scn.GetLSCard("tc"));
        }
        else {
            scn.AttachCardsTo(defiance, htb);
        }
    }

    private void setupDefianceGunners(VirtualTableScenario scn, boolean withTc) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var defiance = scn.GetLSCard("defiance");
        var htb = scn.GetLSCard("htb");
        var executor = scn.GetDSCard("executor");
        scn.MoveCardsToLocation(system, defiance, executor);
        scn.BoardAsPassenger(defiance, scn.GetLSCard("dack"), scn.GetLSCard("verrack"));
        if (withTc) {
            scn.AttachCardsTo(defiance, htb, scn.GetLSCard("tc"));
        }
        else {
            scn.AttachCardsTo(defiance, htb);
        }
    }

    private void setupFalconCec(VirtualTableScenario scn, boolean withTc) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var falcon = scn.GetLSCard("falcon");
        var qlc = scn.GetLSCard("qlc");
        var vsd = scn.GetDSCard("vsd");
        var corellia = scn.GetLSCard("corellia");
        scn.MoveLocationToTable(corellia);
        scn.AttachCardsTo(corellia, scn.GetLSCard("cec"));
        scn.MoveCardsToLocation(system, falcon, vsd);
        scn.BoardAsPilot(falcon, scn.GetLSCard("karie"));
        scn.BoardAsPassenger(falcon, scn.GetLSCard("gunner"));
        if (withTc) {
            scn.AttachCardsTo(falcon, qlc, scn.GetLSCard("tc"));
        }
        else {
            scn.AttachCardsTo(falcon, qlc);
        }
    }

    private void setupTenNumbBlue5(VirtualTableScenario scn, boolean withTc) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var blue5 = scn.GetLSCard("blue5");
        var missiles = scn.GetLSCard("missiles");
        var tie = scn.GetDSCard("tie");
        scn.MoveCardsToLocation(system, blue5, tie);
        scn.BoardAsPilot(blue5, scn.GetLSCard("tennumb"));
        if (withTc) {
            scn.AttachCardsTo(blue5, missiles, scn.GetLSCard("tc"));
        }
        else {
            scn.AttachCardsTo(blue5, missiles);
        }
    }

    private void setupDefiance(VirtualTableScenario scn, boolean withTc) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var defiance = scn.GetLSCard("defiance");
        var htb = scn.GetLSCard("htb");
        var vsd = scn.GetDSCard("vsd");
        scn.MoveCardsToLocation(system, defiance, vsd);
        if (withTc) {
            scn.AttachCardsTo(defiance, htb, scn.GetLSCard("tc"));
        }
        else {
            scn.AttachCardsTo(defiance, htb);
        }
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
                continue;
            }
            idx = msg.indexOf("Total destiny: ");
            if (idx >= 0) {
                String rest = msg.substring(idx + "Total destiny: ".length()).trim();
                last = (int) Float.parseFloat(rest.split("[^0-9.]")[0]);
            }
        }
        assertTrue("Expected a total weapon destiny log, got: " + gameLog(scn), last != null);
        return last;
    }

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

    private void setupHomeOne(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var homeone = scn.GetLSCard("homeone");
        var htb = scn.GetLSCard("htb");
        var tc = scn.GetLSCard("tc");
        var stalker = scn.GetDSCard("stalker");
        var tie = scn.GetDSCard("tie");
        scn.MoveCardsToLocation(system, homeone, stalker, tie);
        scn.AttachCardsTo(homeone, htb, tc);
    }

    private void fireOneShot(VirtualTableScenario scn, PhysicalCardImpl target, int destiny) {
        fireOneShot(scn, target, destiny, null);
    }

    private void fireOneShot(VirtualTableScenario scn, PhysicalCardImpl target, int destiny, Integer forceToUse) {
        // Combined second shots auto-target and may already be at Force/Fire responses.
        // Prepare destiny before passing those responses so the next draw is stubbed.
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(target)) {
            scn.LSChooseCard(target);
        }
        chooseForceAmountIfPrompted(scn, forceToUse);
        scn.PrepareLSDestiny(destiny);
        scn.PassForceUseResponses();
        scn.PassWeaponFireWithDestinyDraw();
        passPostFiringResponses(scn);
    }

    private void fireTwoDestinyShot(VirtualTableScenario scn, PhysicalCardImpl target, int destiny1, int destiny2) {
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(target)) {
            scn.LSChooseCard(target);
        }
        chooseForceAmountIfPrompted(scn, null);
        scn.PassForceUseResponses();
        // Prepare first draw before Fire responses (combined shot 2 may already be in that window).
        // Prepare each subsequent draw immediately before it so the same destiny value can be reused
        // (LS destiny pack has only one card per 0-7).
        scn.PrepareLSDestiny(destiny1);
        scn.PassResponses("Fire ");
        scn.PassDestinyDrawResponses();
        scn.PrepareLSDestiny(destiny2);
        scn.PassDestinyDrawResponses();
        scn.PassResponses("ABOUT_TO_BE_HIT");
        scn.PassResponses("HIT -");
        scn.PassResponses("FIRED_WEAPON");
        passPostFiringResponses(scn);
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
        // Ion Cannon resets attributes then ionizes; do not PassAllResponses or combined shot 2's Fire window is consumed.
        scn.PassResponses("ATTRIBUTE_RESET_OR_MODIFIED");
        scn.PassResponses("Ionized");
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassResponses("ABOUT_TO_BE_HIT");
        scn.PassResponses("HIT -");
        scn.PassResponses("FIRED_WEAPON");
    }
}
