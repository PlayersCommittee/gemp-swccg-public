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
                    put("tc2", "1_39");
                    put("tc3", "1_39");
                    put("tc4", "1_39");
                    put("bwing", "7_140");
                    put("squadron", "7_150");
                    put("sw42", "2_081");
                    put("sw43", "2_081");
                    put("sw44", "2_081");
                    put("xwlc2", "7_162");
                    put("xwlc3", "7_162");
                    put("xwlc4", "7_162");
                    put("htb2", "9_89");
                    put("htb3", "9_89");
                    put("htb4", "9_89");
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

        assertFireTwiceStateCleared(scn);
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
    public void DefianceKarieDackVerrackTargetingComputerSeparatelyDrawsAreEightNotNineVsExecutor() {
        // Playtest: Targeting Computer fires Heavy Turbolaser Battery twice separately at Executor.
        // Defiance +2 each (FiredBy), Karie Neth +1, Dack Ralter +1, Captain Verrack +2 vs capital = +6.
        // Targeting Computer -1 each while using Fire a weapon twice. Printed 3 and 3 must be 8 and 8, not 9 and 9.
        // Heavy Turbolaser Battery -1 vs capital still applies to the total: 16-1=15 vs armor 12.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var executor = scn.GetDSCard("executor");
        setupDefianceKarieGunners(scn);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");
        fireTwoDestinyShot(scn, executor, 3, 3);

        assertWeaponDestinyDrawValues(scn, 8, 8);
        assertEquals("Targeting Computer -1 must apply to each draw; 9+9-1 vs capital would total 17",
                15, lastTotalWeaponDestiny(scn));
        assertTrue(executor.isHit());
    }

    @Test
    public void TargetingComputerSeparatelyFireTwiceClearsFiringStateAfterDefianceHitsCapital() {
        // Playtest crash: Targeting Computer fires Heavy Turbolaser Battery twice separately
        // (TIE, then a capital). Defiance required response reduces the capital power by 5.
        // Weapons-segment Pass then aborted because separately-or-combined firing state was still set.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var tie = scn.GetDSCard("tie");
        var vsd = scn.GetDSCard("vsd");
        setupDefiance(scn, true);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), tie);

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(4);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseSeparatelyOrCombined(scn, "Separately");

        fireTwoDestinyShot(scn, tie, 7, 7);
        assertTrue(tie.isHit());

        int vsdPowerBefore = scn.GetPower(vsd);
        fireTwoDestinyShot(scn, vsd, 7, 7);
        assertTrue(vsd.isHit());
        scn.PassResponses("required");
        scn.PassResponses("power by 5");
        scn.PassAllResponses();
        assertEquals("Defiance required response must reduce the capital power by 5", vsdPowerBefore - 5, scn.GetPower(vsd));

        assertFireTwiceStateCleared(scn);
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


    @Test
    public void TargetingComputerFourCopiesMayDeployOnBwingEvenIfOnlyOneCanBeUsed() {
        /**
         * Advanced Rulebook Devices: any number of Targeting Computers may deploy on a starship
         * even if that ship cannot use all of them this turn. A starfighter may use only one device
         * per turn. Cumulative Rule: copies of Targeting Computer do not stack +1 maneuver.
         */
        var scn = GetScenario();
        var bwing = scn.GetLSCard("bwing");
        var tc = scn.GetLSCard("tc");
        var tc2 = scn.GetLSCard("tc2");
        var tc3 = scn.GetLSCard("tc3");
        var tc4 = scn.GetLSCard("tc4");
        scn.MoveCardsToHand(tc, tc2, tc3, tc4);

        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(system, bwing);

        // Open format: Dark Side goes first. Skip to Light Side deploy, then move Reserve
        // into the Force Pile so four Targeting Computers (2 Force each) can deploy.
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);
        assertTrue("Expected Light Side deploy actions, got: " + decisionDump(scn),
                scn.AwaitingLSDeployPhaseActions());

        PhysicalCardImpl[] computers = { tc, tc2, tc3, tc4 };
        for (PhysicalCardImpl computer : computers) {
            assertTrue("Targeting Computer must be deployable on the B-wing. " + decisionDump(scn),
                    scn.LSGetDecision() != null && scn.LSDeployAvailable(computer));
            scn.LSDeployCard(computer);
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(bwing)) {
                scn.LSChooseCard(bwing);
            }
            if (scn.GetCurrentDecision() != null) {
                scn.PassAllResponses();
            }
            // Deploy phase alternates players the same way weapons segment does.
            if (scn.AwaitingDSDeployPhaseActions()) {
                scn.DSPass();
            }
            assertTrue(scn.IsAttachedTo(bwing, computer));
        }

        assertEquals("Cumulative Rule: four Targeting Computers still add only +1 maneuver, not +4",
                3, scn.GetManeuver(bwing));
    }

    @Test
    public void TargetingComputerStarfighterMayUseOnlyOneCopyPerTurn() {
        // B-wing Attack Fighter may fire many weapons each turn, so leftover SW-4 Ion Cannons
        // stay fireable after one Targeting Computer. The starfighter may still use only one
        // device per turn, so the other three Targeting Computers cannot Fire a weapon twice.
        var scn = GetScenario();
        var bwing = scn.GetLSCard("bwing");
        var tc = scn.GetLSCard("tc");
        var tc2 = scn.GetLSCard("tc2");
        var tc3 = scn.GetLSCard("tc3");
        var tc4 = scn.GetLSCard("tc4");
        var sw4 = scn.GetLSCard("sw4");
        var sw42 = scn.GetLSCard("sw42");
        var tie = scn.GetDSCard("tie");
        setupBwingWithFourTargetingComputers(scn);

        assertEquals("Unused Targeting Computers still give +1 maneuver (continuous text is not using the device)",
                3, scn.GetManeuver(bwing));

        scn.StartBattleAndSkipToWeaponsSegment();
        scn.EnsureLSForcePile(6);

        assertTrue(fireTwiceAvailable(scn, tc));
        assertTrue(fireTwiceAvailable(scn, tc2));
        assertTrue(fireTwiceAvailable(scn, tc3));
        assertTrue(fireTwiceAvailable(scn, tc4));

        useFireAWeaponTwice(scn, tc, sw4, "Separately");
        fireOneShot(scn, tie, 1);
        fireOneShot(scn, tie, 1);
        passDarkSideWeaponsIfNeeded(scn);

        assertTrue("B-wing still has unused SW-4 Ion Cannons, so Light Side stays in weapons segment. "
                        + decisionDump(scn),
                scn.AwaitingLSWeaponsSegmentActions());
        assertFalse("That Targeting Computer copy cannot be used again this turn. " + decisionDump(scn),
                fireTwiceAvailable(scn, tc));
        assertFalse("Starfighter may use only one device per turn. " + decisionDump(scn),
                fireTwiceAvailable(scn, tc2));
        assertFalse("Starfighter may use only one device per turn", fireTwiceAvailable(scn, tc3));
        assertFalse("Starfighter may use only one device per turn", fireTwiceAvailable(scn, tc4));
        assertTrue("Leftover SW-4 Ion Cannon can still Fire (device limit is not a weapon limit). "
                        + decisionDump(scn),
                scn.LSCardActionAvailable(sw42, "Fire"));
        assertEquals("After using one Targeting Computer, maneuver is still +1, not +4 and not lost",
                3, scn.GetManeuver(bwing));
    }

    @Test
    public void TargetingComputerSquadronMayUseThreeCopiesPerTurn() {
        // X-wing Assault Squadron is a Decipher squadron-class starship (Filters.squadron).
        // Squadrons may use three different devices per turn. After three Targeting Computers
        // are used, the fourth cannot Fire a weapon twice this turn.
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var tc2 = scn.GetLSCard("tc2");
        var tc3 = scn.GetLSCard("tc3");
        var tc4 = scn.GetLSCard("tc4");
        var xwlc = scn.GetLSCard("xwlc");
        var xwlc2 = scn.GetLSCard("xwlc2");
        var xwlc3 = scn.GetLSCard("xwlc3");
        var tie = scn.GetDSCard("tie");
        setupSquadronWithFourTargetingComputers(scn);

        scn.EnsureLSForcePile(4);
        scn.StartBattleAndSkipToWeaponsSegment();

        assertTrue(fireTwiceAvailable(scn, tc));
        assertTrue(fireTwiceAvailable(scn, tc2));
        assertTrue(fireTwiceAvailable(scn, tc3));
        assertTrue(fireTwiceAvailable(scn, tc4));

        useFireAWeaponTwice(scn, tc, xwlc, "Separately");
        fireOneShot(scn, tie, 1, 0);
        fireOneShot(scn, tie, 1, 0);
        passDarkSideWeaponsIfNeeded(scn);
        assertTrue("Squadron may use a second Targeting Computer this turn. " + decisionDump(scn),
                fireTwiceAvailable(scn, tc2));
        assertFalse("The Targeting Computer copy already used cannot Fire a weapon twice again this turn",
                fireTwiceAvailable(scn, tc));

        useFireAWeaponTwice(scn, tc2, xwlc2, "Separately");
        fireOneShot(scn, tie, 1, 0);
        fireOneShot(scn, tie, 1, 0);
        passDarkSideWeaponsIfNeeded(scn);
        assertTrue("Squadron may use a third Targeting Computer this turn. " + decisionDump(scn),
                fireTwiceAvailable(scn, tc3));

        useFireAWeaponTwice(scn, tc3, xwlc3, "Separately");
        fireOneShot(scn, tie, 1, 0);
        fireOneShot(scn, tie, 1, 0);
        passDarkSideWeaponsIfNeeded(scn);

        if (scn.AwaitingLSWeaponsSegmentActions()) {
            assertFalse("Squadron may use only three devices per turn. " + decisionDump(scn),
                    fireTwiceAvailable(scn, tc4));
        }
        else {
            assertTrue("Expected weapons segment after three Targeting Computer uses. " + decisionDump(scn),
                    scn.AwaitingDSWeaponsSegmentActions());
        }
    }

    @Test
    public void TargetingComputerCapitalMayUseAllFourCopiesPerTurn() {
        // Home One is a capital starship and may use any number of devices per turn.
        // Using copy A does not prevent using copy B, C, or D this turn.
        var scn = GetScenario();
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

        scn.EnsureLSForcePile(16);
        scn.StartBattleAndSkipToWeaponsSegment();

        PhysicalCardImpl[] computers = { tc, tc2, tc3, tc4 };
        PhysicalCardImpl[] batteries = { htb, htb2, htb3, htb4 };
        for (int i = 0; i < computers.length; i++) {
            assertTrue("Capital may use Targeting Computer copy " + (i + 1) + " this turn. "
                            + decisionDump(scn),
                    fireTwiceAvailable(scn, computers[i]));
            useFireAWeaponTwice(scn, computers[i], batteries[i], "Separately");
            fireTwoDestinyShot(scn, stalker, 1, 1);
            fireTwoDestinyShot(scn, stalker, 1, 1);
            passDarkSideWeaponsIfNeeded(scn);
            assertFalse("Used Targeting Computer copy " + (i + 1) + " cannot Fire a weapon twice again this turn. "
                            + decisionDump(scn),
                    fireTwiceAvailable(scn, computers[i]));
        }
    }

    @Test
    public void TargetingComputerEachCopyCanBeUsedOnlyOncePerTurn() {
        // One Rule: an individual Targeting Computer copy can only be used once per turn.
        // OncePerBattle is not enough if a second battle the same turn would re-offer it.
        // No clean Decipher card initiates a second battle the same turn, so this checks the
        // same-copy limit in one battle on a capital (device slots are unlimited there).
        var scn = GetScenario();
        var tc = scn.GetLSCard("tc");
        var tc2 = scn.GetLSCard("tc2");
        var tc3 = scn.GetLSCard("tc3");
        var tc4 = scn.GetLSCard("tc4");
        var htb = scn.GetLSCard("htb");
        var stalker = scn.GetDSCard("stalker");
        setupCapitalWithFourTargetingComputers(scn);

        scn.EnsureLSForcePile(8);
        scn.StartBattleAndSkipToWeaponsSegment();

        assertTrue(fireTwiceAvailable(scn, tc));
        useFireAWeaponTwice(scn, tc, htb, "Separately");
        fireTwoDestinyShot(scn, stalker, 1, 1);
        fireTwoDestinyShot(scn, stalker, 1, 1);
        passDarkSideWeaponsIfNeeded(scn);

        assertTrue("After using copy A, Light Side should still have weapons actions. " + decisionDump(scn),
                scn.AwaitingLSWeaponsSegmentActions());
        assertFalse("Copy A cannot Fire a weapon twice again this turn. " + decisionDump(scn),
                fireTwiceAvailable(scn, tc));
        assertTrue("Using copy A does not prevent using copy B this turn. " + decisionDump(scn),
                fireTwiceAvailable(scn, tc2));
        assertTrue("Copy C is still available this turn", fireTwiceAvailable(scn, tc3));
        assertTrue("Copy D is still available this turn", fireTwiceAvailable(scn, tc4));
    }


    /**
     * B-wing Attack Fighter with four Targeting Computers and four SW-4 Ion Cannons vs two TIEs.
     * The B-wing may fire many weapons during battle, so leftover cannons stay fireable after
     * one Targeting Computer is used.
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
     * Squadrons may use three different devices per turn.
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
     * Capitals may use any number of devices per turn. Stalker is the proven Home One target.
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

    /**
     * Starts Fire a weapon twice on the given Targeting Computer. If several weapons can fire,
     * choose the given weapon. Then pick Separately or Combined.
     */
    private void useFireAWeaponTwice(VirtualTableScenario scn, PhysicalCardImpl targetingComputer,
                                     PhysicalCardImpl weapon, String mode) {
        assertTrue("Fire a weapon twice must be available on that Targeting Computer. " + decisionDump(scn),
                fireTwiceAvailable(scn, targetingComputer));
        scn.LSUseCardAction(targetingComputer, "Fire a weapon twice");
        if (scn.LSGetDecision() != null && scn.LSDecisionAvailable("Choose weapon")) {
            scn.LSChooseCard(weapon);
        }
        assertTrue("Expected Fire a weapon twice prompt, got: " + decisionDump(scn),
                scn.LSGetDecision() != null && scn.LSDecisionAvailable("Fire a weapon twice"));
        assertTrue(scn.LSChoiceAvailable("Separately"));
        assertTrue(scn.LSChoiceAvailable("Combined"));
        assertTrue(scn.LSChoiceAvailable("Don't Fire (Cancel)"));
        scn.LSChoose(mode);
    }

    /**
     * Weapons segment alternates players. After Light Side uses a Targeting Computer, Dark Side
     * is asked next even if Light Side still has weapons or devices. Pass Dark Side to get back
     * to Light Side's remaining Targeting Computers.
     */
    private void passDarkSideWeaponsIfNeeded(VirtualTableScenario scn) {
        if (scn.AwaitingDSWeaponsSegmentActions()) {
            scn.DSPass();
        }
    }

    /**
     * True if Light Side currently has Fire a weapon twice on that Targeting Computer copy.
     * False (does not throw) when Light Side has no decision.
     */
    private boolean fireTwiceAvailable(VirtualTableScenario scn, PhysicalCardImpl targetingComputer) {
        return scn.LSGetDecision() != null && scn.LSCardActionAvailable(targetingComputer, "Fire a weapon twice");
    }

    /**
     * Light Side and Dark Side current decision text plus Light Side action list, for assertion messages.
     */
    private String decisionDump(VirtualTableScenario scn) {
        String ls = scn.LSGetDecision() == null ? "null" : scn.LSGetDecision().getText();
        String ds = scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText();
        String actions = "n/a";
        try {
            if (scn.LSGetDecision() != null) {
                actions = String.valueOf(scn.GetLSAvailableActions());
            }
        }
        catch (RuntimeException ignored) {
            actions = "(no actionText)";
        }
        String phase = String.valueOf(scn.gameState().getCurrentPhase());
        String player = String.valueOf(scn.gameState().getCurrentPlayerId());
        String soc = scn.gameState().getSeparatelyOrCombinedFiringState() == null ? "none" : "active";
        int force = scn.GetLSForcePileCount();
        String logTail = "";
        java.util.List<String> msgs = scn.gameState().getLastMessages();
        int from = Math.max(0, msgs.size() - 12);
        logTail = String.join(" | ", msgs.subList(from, msgs.size()));
        return "player=" + player + " phase=" + phase + " force=" + force + " soc=" + soc
                + " LS=" + ls + " actions=" + actions + " DS=" + ds + " log=" + logTail;
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

    /**
     * Defiance with Karie Neth piloting, Dack Ralter and Captain Verrack as passengers,
     * Heavy Turbolaser Battery and Targeting Computer, facing Executor.
     */
    private void setupDefianceKarieGunners(VirtualTableScenario scn) {
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        var defiance = scn.GetLSCard("defiance");
        var htb = scn.GetLSCard("htb");
        var executor = scn.GetDSCard("executor");
        scn.MoveCardsToLocation(system, defiance, executor);
        scn.BoardAsPilot(defiance, scn.GetLSCard("karie"));
        scn.BoardAsPassenger(defiance, scn.GetLSCard("dack"), scn.GetLSCard("verrack"));
        scn.AttachCardsTo(defiance, htb, scn.GetLSCard("tc"));
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

    /**
     * After Targeting Computer fire-twice finishes, separately-or-combined firing state must be empty
     * and taking a snapshot must not throw. Weapons-segment Pass snapshots the game; if that state is
     * still set the game is canceled.
     */
    private void assertFireTwiceStateCleared(VirtualTableScenario scn) {
        scn.game().takeSnapshot("after Targeting Computer");
        assertTrue("Targeting Computer fire-twice must clear separately-or-combined firing state",
                scn.gameState().getSeparatelyOrCombinedFiringState() == null);
    }

    /**
     * Defiance with Heavy Turbolaser Battery (and Targeting Computer when withTc) facing a Victory-class Star Destroyer.
     */
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
        if (scn.AwaitingLSWeaponsSegmentActions() || scn.AwaitingDSWeaponsSegmentActions()) {
            return;
        }
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
        if (scn.AwaitingLSWeaponsSegmentActions() || scn.AwaitingDSWeaponsSegmentActions()) {
            return;
        }
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
        // Defiance required: Reduce a capital starship power by 5 after it is hit.
        scn.PassResponses("required");
        scn.PassResponses("power by 5");
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
