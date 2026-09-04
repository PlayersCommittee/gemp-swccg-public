package com.gempukku.swccgo.cards.set7.light;

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

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_7_104_Tests {
    private static final String STAY_SHARP_BONUS_TEXT = "Add 2 to total weapon destiny";
    private static final String CONCENTRATE_ALL_FIRE_REDRAW_TEXT = "Cancel destiny and cause re-draw";

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("stay_sharp", "7_104");
                    put("htb", "9_089");
                    put("cruiser", "9_079");
                    put("han", "1_011");
                    put("concentrate_all_fire", "9_3");
                }},
                new HashMap<>()
                {{
                    put("destroyer", "1_302");
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
    public void StaySharpStatsAndKeywordsAreCorrect() {
        /**
         * Title: Stay Sharp!
         * Uniqueness: Unique
         * Side: Light
         * Type: Interrupt
         * Subtype: Used
         * Destiny: 4
         * Icons: Special Edition
         * Game Text: During your control phase, fire one of your starship weapons (for free). If Han or any gunner
         *      is aboard that starship, may add 2 to the total weapon destiny. 'Hit' target is lost. OR If you just
         *      fired a weapon in battle, add that weapon's destiny number to your total power.
         * Lore: 'Ha haaaaaa!'
         * Set: Special Edition
         * Rarity: U
         */
        var scn = GetScenario();

        var card = scn.GetLSCard("stay_sharp").getBlueprint();

        assertEquals("Stay Sharp!", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertTrue(card.getCardTypes().contains(CardType.INTERRUPT));
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getIconCount(Icon.SPECIAL_EDITION));
        assertEquals(ExpansionSet.SPECIAL_EDITION, card.getExpansionSet());
        assertEquals(Rarity.U, card.getRarity());
    }

    @Test
    public void StaySharpHeavyTurbolaserBatteryCannotStackPlusFourToHitDuringControlPhase() {
        // Heavy Turbolaser Battery draws two weapon destinies. Stay Sharp may add 2 to the
        // total weapon destiny once, not +2 after each draw. Destinies 1 and 3 vs a capital:
        // 1 + 3 - 1 = 3. Plus 2 = 5, which does not beat Imperial-Class Star Destroyer
        // armor 6 (hit if total destiny > defense value). Plus 4 would be 7 and would hit.
        // Always take Stay Sharp +2 when it is offered so a second illegal accept would
        // make this test fail by hitting.
        var scn = GetScenario();
        var staySharp = scn.GetLSCard("stay_sharp");
        var htb = scn.GetLSCard("htb");
        var destroyer = scn.GetDSCard("destroyer");

        setupStaySharpHeavyTurbolaserBatteryTable(scn, false);
        assertEquals(6, scn.GetDefense(destroyer));

        playStaySharpAndChooseHeavyTurbolaserBattery(scn, staySharp, htb);
        StaySharpFiringResult result = resolveHeavyTurbolaserBatteryFiring(scn, destroyer, true, true, false, true);

        assertEquals("Stay Sharp +2 should be offered once after it is accepted", 1, result.staySharpOffered);
        assertTargetWasNotHitByStaySharpPlusFour(scn, destroyer);
    }

    @Test
    public void StaySharpPlusTwoStillOfferedOnSecondDrawIfDeclinedOnFirst() {
        // Declining Stay Sharp on the first Heavy Turbolaser Battery destiny draw must
        // still offer it on the second draw. Accepting that later offer is still only +2
        // total, so the Star Destroyer is not hit with the same 1+3-1 destiny math.
        var scn = GetScenario();
        var staySharp = scn.GetLSCard("stay_sharp");
        var htb = scn.GetLSCard("htb");
        var destroyer = scn.GetDSCard("destroyer");

        setupStaySharpHeavyTurbolaserBatteryTable(scn, false);

        playStaySharpAndChooseHeavyTurbolaserBattery(scn, staySharp, htb);
        StaySharpFiringResult result = resolveHeavyTurbolaserBatteryFiring(scn, destroyer, false, true, false, true);

        assertEquals("Stay Sharp +2 should be offered on the first draw, then again after a decline", 2, result.staySharpOffered);
        assertTargetWasNotHitByStaySharpPlusFour(scn, destroyer);
    }

    @Test
    public void StaySharpPlusTwoSurvivesConcentrateAllFireRedrawAfterAccept() {
        // Accept Stay Sharp +2 after the first Heavy Turbolaser Battery destiny, then use
        // Concentrate All Fire to cancel and redraw that destiny. The +2 is a total weapon
        // destiny modifier until end of weapon firing, so it must still apply after the
        // redraw and must not be offered again. Final destinies 2 then 4 vs capital:
        // 2 + 4 - 1 = 5 miss; +2 = 7 hits Imperial-Class Star Destroyer armor 6 (need total
        // destiny > 6). If the modifier vanished with the canceled draw, the Destroyer
        // would miss. Stacking to +4 would also hit, so offer-once is asserted separately.
        var scn = GetScenario();
        var staySharp = scn.GetLSCard("stay_sharp");
        var htb = scn.GetLSCard("htb");
        var destroyer = scn.GetDSCard("destroyer");

        setupStaySharpHeavyTurbolaserBatteryTable(scn, true);
        assertEquals(6, scn.GetDefense(destroyer));

        playStaySharpAndChooseHeavyTurbolaserBattery(scn, staySharp, htb);
        StaySharpFiringResult result = resolveHeavyTurbolaserBatteryFiring(scn, destroyer, true, true, true, true);

        assertEquals("Stay Sharp +2 should be accepted once and not offered again after Concentrate All Fire redraw", 1, result.staySharpOffered);
        assertEquals("Stay Sharp +2 should be accepted once", 1, result.staySharpAccepted);
        assertEquals("Concentrate All Fire should cancel and redraw one weapon destiny", 1, result.concentrateAllFireTaken);
        assertTargetWasHitByStaySharpPlusTwoAfterRedraw(scn, destroyer);
    }

    @Test
    public void StaySharpStillOfferedAfterConcentrateAllFireRedrawIfNotYetAccepted() {
        // Skip Stay Sharp on the first Heavy Turbolaser Battery destiny by taking Concentrate
        // All Fire instead. After the redraw, Stay Sharp must still be offered until it is
        // accepted once. Same 2+4-1 destiny math: miss without +2, hit with +2.
        var scn = GetScenario();
        var staySharp = scn.GetLSCard("stay_sharp");
        var htb = scn.GetLSCard("htb");
        var destroyer = scn.GetDSCard("destroyer");

        setupStaySharpHeavyTurbolaserBatteryTable(scn, true);

        playStaySharpAndChooseHeavyTurbolaserBattery(scn, staySharp, htb);
        StaySharpFiringResult result = resolveHeavyTurbolaserBatteryFiring(scn, destroyer, true, true, true, false);

        assertTrue("Stay Sharp +2 should still be offered after Concentrate All Fire redraws a destiny",
                result.staySharpOfferedAfterConcentrateAllFire >= 1);
        assertEquals("Stay Sharp +2 should be accepted once", 1, result.staySharpAccepted);
        assertEquals("Concentrate All Fire should cancel and redraw one weapon destiny", 1, result.concentrateAllFireTaken);
        assertTrue("Stay Sharp +2 must not be offered again after it is accepted",
                result.staySharpOffered <= 2);
        assertTargetWasHitByStaySharpPlusTwoAfterRedraw(scn, destroyer);
    }

    /**
     * Puts a Mon Calamari Star Cruiser with Han aboard and Heavy Turbolaser Battery attached
     * at the Light Side system, facing an Imperial-Class Star Destroyer.
     * Without Concentrate All Fire, stacks weapon destinies 1 then 3 so the total is 3
     * before Stay Sharp. With Concentrate All Fire in play, stacks 1 (canceled), then
     * redraw 2 and second draw 4 so the final total is 5 before Stay Sharp.
     */
    private void setupStaySharpHeavyTurbolaserBatteryTable(VirtualTableScenario scn, boolean includeConcentrateAllFire) {
        var staySharp = scn.GetLSCard("stay_sharp");
        var htb = scn.GetLSCard("htb");
        var cruiser = scn.GetLSCard("cruiser");
        var han = scn.GetLSCard("han");
        var destroyer = scn.GetDSCard("destroyer");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(system, cruiser, destroyer, han);
        scn.BoardAsPilot(cruiser, han);
        scn.AttachCardsTo(cruiser, htb);
        scn.MoveCardsToLSHand(staySharp);
        if (includeConcentrateAllFire) {
            scn.MoveCardsToLSSideOfTable(scn.GetLSCard("concentrate_all_fire"));
        }

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue("Han must be piloting the Star Cruiser for Stay Sharp +2", scn.IsAboardAsPilot(cruiser, han));
        assertTrue("Han must remain in play aboard the Star Cruiser", han.getZone().isInPlay());

        if (includeConcentrateAllFire) {
            // Last Prepare is drawn first. First draw 1 is canceled; redraw 2 then second draw 4.
            scn.PrepareLSDestiny(4);
            scn.PrepareLSDestiny(2);
            scn.PrepareLSDestiny(1);
        }
        else {
            // Second draw is 3, first draw is 1 (placed on top last). 1+3-1 = 3 vs capital.
            scn.PrepareLSDestiny(3);
            scn.PrepareLSDestiny(1);
        }
    }

    /**
     * Plays Stay Sharp during the control phase and chooses Heavy Turbolaser Battery.
     */
    private void playStaySharpAndChooseHeavyTurbolaserBattery(VirtualTableScenario scn,
            PhysicalCardImpl staySharp, PhysicalCardImpl htb) {
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardPlayAvailable(staySharp));
        scn.LSPlayCard(staySharp);
        assertTrue(scn.LSHasCardChoiceAvailable(htb));
        scn.LSChooseCard(htb);
    }

    /**
     * Finishes firing Heavy Turbolaser Battery after Stay Sharp has been played.
     * Takes or skips Stay Sharp and Concentrate All Fire optionals according to the flags.
     * preferStaySharpFirst true means accept Stay Sharp before Concentrate All Fire when both
     * are showing. False means take Concentrate All Fire first (skipping Stay Sharp that window).
     */
    private StaySharpFiringResult resolveHeavyTurbolaserBatteryFiring(VirtualTableScenario scn,
            PhysicalCardImpl destroyer, boolean takeFirstStaySharpOffer, boolean takeLaterStaySharpOffers,
            boolean takeConcentrateAllFireOnce, boolean preferStaySharpFirst) {
        StaySharpFiringResult result = new StaySharpFiringResult();
        java.util.List<String> seen = new java.util.ArrayList<>();
        for (int i = 0; i < 80; i++) {
            seen.add(describeDecisions(scn));
            if (scn.AwaitingLSControlPhaseActions() && i > 0) {
                if (result.staySharpOffered == 0) {
                    throw new AssertionError("Back at control phase with no Stay Sharp offers. Decisions: " + seen);
                }
                return result;
            }

            boolean staySharpNow = scn.LSAnyDecisionsAvailable() && staySharpBonusActionAvailable(scn);
            boolean concentrateNow = scn.LSAnyDecisionsAvailable() && concentrateAllFireRedrawActionAvailable(scn);

            if (staySharpNow && concentrateNow) {
                result.staySharpOffered++;
                if (result.concentrateAllFireTaken > 0) {
                    result.staySharpOfferedAfterConcentrateAllFire++;
                }
                if (takeConcentrateAllFireOnce && result.concentrateAllFireTaken == 0 && !preferStaySharpFirst) {
                    scn.LSChooseAction(CONCENTRATE_ALL_FIRE_REDRAW_TEXT);
                    result.concentrateAllFireTaken++;
                    continue;
                }
                boolean takeStaySharp = (result.staySharpOffered == 1) ? takeFirstStaySharpOffer : takeLaterStaySharpOffers;
                if (takeStaySharp) {
                    scn.LSChooseAction(STAY_SHARP_BONUS_TEXT);
                    result.staySharpAccepted++;
                    continue;
                }
                if (takeConcentrateAllFireOnce && result.concentrateAllFireTaken == 0) {
                    scn.LSChooseAction(CONCENTRATE_ALL_FIRE_REDRAW_TEXT);
                    result.concentrateAllFireTaken++;
                    continue;
                }
                scn.LSDecline();
                continue;
            }

            if (staySharpNow) {
                result.staySharpOffered++;
                if (result.concentrateAllFireTaken > 0) {
                    result.staySharpOfferedAfterConcentrateAllFire++;
                }
                boolean takeStaySharp = (result.staySharpOffered == 1) ? takeFirstStaySharpOffer : takeLaterStaySharpOffers;
                if (takeStaySharp) {
                    scn.LSChooseAction(STAY_SHARP_BONUS_TEXT);
                    result.staySharpAccepted++;
                }
                else {
                    scn.LSDecline();
                }
                continue;
            }

            if (concentrateNow && takeConcentrateAllFireOnce && result.concentrateAllFireTaken == 0) {
                scn.LSChooseAction(CONCENTRATE_ALL_FIRE_REDRAW_TEXT);
                result.concentrateAllFireTaken++;
                continue;
            }

            if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(destroyer)) {
                scn.LSChooseCard(destroyer);
                continue;
            }

            // Pass one player at a time. PassResponses("optional") would also decline
            // Light Side's Stay Sharp prompt if both players have an optional window.
            if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
                continue;
            }
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
                continue;
            }

            throw new AssertionError("No pending decision while firing Heavy Turbolaser Battery after "
                    + i + " steps; Stay Sharp offers seen: " + result.staySharpOffered + "; seen=" + seen);
        }
        throw new AssertionError("Did not finish firing Heavy Turbolaser Battery. Stay Sharp offers seen: "
                + result.staySharpOffered + "; last decision: " + describeDecisions(scn));
    }

    /**
     * True if Light Side currently has the Stay Sharp optional to add 2 to total weapon destiny.
     */
    private boolean staySharpBonusActionAvailable(VirtualTableScenario scn) {
        return actionTextContains(scn, STAY_SHARP_BONUS_TEXT);
    }

    /**
     * True if Light Side currently has Concentrate All Fire's cancel and redraw optional.
     */
    private boolean concentrateAllFireRedrawActionAvailable(VirtualTableScenario scn) {
        return actionTextContains(scn, CONCENTRATE_ALL_FIRE_REDRAW_TEXT);
    }

    /**
     * True if any Light Side action label contains the given text.
     */
    private boolean actionTextContains(VirtualTableScenario scn, String needle) {
        if (!scn.LSAnyDecisionsAvailable()) {
            return false;
        }
        var params = scn.LSGetDecision().getDecisionParameters();
        String[] actionTexts = params.get("actionText");
        if (actionTexts == null) {
            return false;
        }
        for (String text : actionTexts) {
            if (text != null && text.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Light and Dark pending decision text, including any Light Side action labels.
     */
    private String describeDecisions(VirtualTableScenario scn) {
        String ls = "LS=none";
        if (scn.LSAnyDecisionsAvailable()) {
            var decision = scn.LSGetDecision();
            String[] actionTexts = decision.getDecisionParameters().get("actionText");
            ls = "LS=" + decision.getText() + " actions=" + java.util.Arrays.toString(actionTexts);
        }
        String ds = "DS=none";
        if (scn.DSAnyDecisionsAvailable()) {
            ds = "DS=" + scn.DSGetDecision().getText();
        }
        return ls + " | " + ds + " | phase=" + scn.GetCurrentPhase();
    }

    /**
     * The Star Destroyer is hit only if Stay Sharp stacked to +4. A miss (Stay Sharp +2)
     * leaves it in play and not hit.
     */
    private void assertTargetWasNotHitByStaySharpPlusFour(VirtualTableScenario scn, PhysicalCardImpl destroyer) {
        assertFalse("Imperial-Class Star Destroyer must not be hit; that would mean Stay Sharp stacked to +4",
                destroyer.isHit());
        assertEquals("Imperial-Class Star Destroyer must still be at the system after a miss",
                Zone.AT_LOCATION, destroyer.getZone());
        assertEquals(0, scn.GetDSLostPileCount());
    }

    /**
     * With destinies 2 and 4 vs capital, Stay Sharp +2 makes total 7, which hits armor 6.
     * Without the +2 the total is 5 and would miss, so a miss here means the bonus was lost.
     */
    private void assertTargetWasHitByStaySharpPlusTwoAfterRedraw(VirtualTableScenario scn, PhysicalCardImpl destroyer) {
        assertTrue("Imperial-Class Star Destroyer must be hit; Stay Sharp +2 should still apply after Concentrate All Fire redraw",
                destroyer.isHit() || destroyer.getZone() == Zone.LOST_PILE || scn.GetDSLostPileCount() > 0);
    }

    /**
     * Counts Stay Sharp and Concentrate All Fire choices while Heavy Turbolaser Battery is firing.
     */
    private static class StaySharpFiringResult {
        int staySharpOffered;
        int staySharpAccepted;
        int staySharpOfferedAfterConcentrateAllFire;
        int concentrateAllFireTaken;
    }
}