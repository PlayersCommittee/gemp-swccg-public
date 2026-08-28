package com.gempukku.swccgo.cards.set1.light;

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

        startWeaponsSegment(scn);

        assertTrue(scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseWeaponIfPrompted(scn, ept);
        scn.LSChoose("Separately");
        fireOneShot(scn, stalker, 1);
        fireOneShot(scn, stalker, 1);

        passOptionalResponses(scn);
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

        startWeaponsSegment(scn);

        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(tc, "Fire a weapon twice"));
        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseWeaponIfPrompted(scn, ept);
        scn.LSChoose("Separately");

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

        startWeaponsSegment(scn);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseWeaponIfPrompted(scn, ept);
        scn.LSChoose("Combined");

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

        startWeaponsSegment(scn);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseWeaponIfPrompted(scn, ept);
        scn.LSChoose("Combined");

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

        startWeaponsSegment(scn);

        scn.LSUseCardAction(tc, "Fire a weapon twice");
        chooseWeaponIfPrompted(scn, ept);
        scn.LSChoose("Combined");

        fireOneShot(scn, stalker, 4);
        assertFalse(stalker.isHit());
        fireOneShot(scn, stalker, 4);
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

    private void startWeaponsSegment(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        passOptionalResponses(scn);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    private void chooseWeaponIfPrompted(VirtualTableScenario scn, PhysicalCardImpl weapon) {
        if (scn.LSDecisionAvailable("Choose weapon")) {
            scn.LSChooseCard(weapon);
        }
    }

    private void fireOneShot(VirtualTableScenario scn, PhysicalCardImpl target, int destiny) {
        // Combined second shots auto-target and may already be at Force/Fire responses.
        // Prepare destiny before passing those responses so the next draw is stubbed.
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(target)) {
            scn.LSChooseCard(target);
        }
        scn.PrepareLSDestiny(destiny);
        scn.PassForceUseResponses();
        scn.PassWeaponFireWithDestinyDraw();
    }

    private void passOptionalResponses(VirtualTableScenario scn) {
        if (scn.GetCurrentDecision() != null) {
            scn.PassAllResponses();
        }
    }
}
