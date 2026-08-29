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

import java.util.HashMap;

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
                }},
                new HashMap<>()
                {{
                    put("stalker", "3_152");
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
        assertTrue("Expected Separately/Combined prompt, got: "
                        + (scn.GetCurrentDecision() == null ? "null" : scn.GetCurrentDecision().getText()),
                scn.LSDecisionAvailable("separately or combined"));
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
