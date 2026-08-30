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
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("stay_sharp", "7_104");
                    put("htb", "9_089");
                    put("cruiser", "9_079");
                    put("han", "1_011");
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
         *      is aboard that starship, may add 2 to destiny draw. 'Hit' target is lost. OR If you just fired a
         *      weapon in battle, add that weapon's destiny number to your total power.
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
        // destiny total once, not +2 after each draw. Destinies 1 and 3 vs a capital:
        // 1 + 3 - 1 = 3. Plus 2 = 5, which does not beat Imperial-Class Star Destroyer
        // armor 6. Plus 4 would be 7 and would hit. Always take Stay Sharp +2 when it
        // is offered so a second illegal accept would make this test fail by hitting.
        var scn = GetScenario();
        var staySharp = scn.GetLSCard("stay_sharp");
        var htb = scn.GetLSCard("htb");
        var destroyer = scn.GetDSCard("destroyer");

        setupStaySharpHeavyTurbolaserBatteryTable(scn);
        assertEquals(6, scn.GetDefense(destroyer));

        playStaySharpAndChooseHeavyTurbolaserBattery(scn, staySharp, htb);
        int offered = resolveHeavyTurbolaserBatteryFiringWithStaySharp(scn, destroyer, true, true);

        assertEquals("Stay Sharp +2 should be offered once after it is accepted", 1, offered);
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

        setupStaySharpHeavyTurbolaserBatteryTable(scn);

        playStaySharpAndChooseHeavyTurbolaserBattery(scn, staySharp, htb);
        int offered = resolveHeavyTurbolaserBatteryFiringWithStaySharp(scn, destroyer, false, true);

        assertEquals("Stay Sharp +2 should be offered on the first draw, then again after a decline", 2, offered);
        assertTargetWasNotHitByStaySharpPlusFour(scn, destroyer);
    }

    /**
     * Puts a Mon Calamari Star Cruiser with Han aboard and Heavy Turbolaser Battery attached
     * at the Light Side system, facing an Imperial-Class Star Destroyer. Stacks weapon destinies
     * 1 then 3 so the Heavy Turbolaser Battery total is 3 before Stay Sharp.
     */
    private void setupStaySharpHeavyTurbolaserBatteryTable(VirtualTableScenario scn) {
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

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue("Han must be piloting the Star Cruiser for Stay Sharp +2", scn.IsAboardAsPilot(cruiser, han));
        assertTrue("Han must remain in play aboard the Star Cruiser", han.getZone().isInPlay());

        // Second draw is 3, first draw is 1 (placed on top last). 1+3-1 = 3 vs capital.
        scn.PrepareLSDestiny(3);
        scn.PrepareLSDestiny(1);
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
     * Whenever "Add 2 to weapon destiny" is offered, takes it on the first offer if
     * takeFirstOffer is true, and on later offers if takeLaterOffers is true.
     * Returns how many times that optional was offered.
     */
    private int resolveHeavyTurbolaserBatteryFiringWithStaySharp(VirtualTableScenario scn,
            PhysicalCardImpl destroyer, boolean takeFirstOffer, boolean takeLaterOffers) {
        int offered = 0;
        java.util.List<String> seen = new java.util.ArrayList<>();
        for (int i = 0; i < 60; i++) {
            seen.add(describeDecisions(scn));
            if (scn.AwaitingLSControlPhaseActions() && i > 0) {
                if (offered == 0) {
                    throw new AssertionError("Back at control phase with no Stay Sharp offers. Decisions: " + seen);
                }
                return offered;
            }

            if (scn.LSAnyDecisionsAvailable() && staySharpBonusActionAvailable(scn)) {
                offered++;
                boolean take = (offered == 1) ? takeFirstOffer : takeLaterOffers;
                if (take) {
                    scn.LSChooseAction("Add 2 to weapon destiny");
                }
                else {
                    scn.LSDecline();
                }
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
                    + i + " steps; Stay Sharp offers seen: " + offered + "; seen=" + seen);
        }
        throw new AssertionError("Did not finish firing Heavy Turbolaser Battery. Stay Sharp offers seen: "
                + offered + "; last decision: " + describeDecisions(scn));
    }

    /**
     * True if Light Side currently has the Stay Sharp optional to add 2 to weapon destiny.
     */
    private boolean staySharpBonusActionAvailable(VirtualTableScenario scn) {
        if (!scn.LSAnyDecisionsAvailable()) {
            return false;
        }
        var params = scn.LSGetDecision().getDecisionParameters();
        String[] actionTexts = params.get("actionText");
        if (actionTexts == null) {
            return false;
        }
        for (String text : actionTexts) {
            if (text != null && text.contains("Add 2 to weapon destiny")) {
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
}
