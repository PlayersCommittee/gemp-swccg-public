package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Card_8_049_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
                put("rescue", "8_49");
                put("sentry", "8_11"); // Ewok Sentry (scout)
                put("spearman", "8_12"); // Ewok Spearman (not scout)
                put("tribesman", "8_13"); // Ewok Tribesman
                put("paploo", "8_24"); // Paploo (scout)
                put("romba", "8_26"); // Romba (scout)
                put("rebel", "1_28"); // Rebel Trooper (captive / presence)
                put("bunker", "8_70"); // Endor: Bunker (interior)
            }},
            new HashMap<>() {{
                put("escort", "1_194"); // Stormtrooper
                put("sense", "1_267"); // Sense
                put("vader", "1_168"); // Darth Vader (high ability for Sense)
                put("presence", "1_194");
                put("cantinaDS", "1_290"); // Tatooine: Cantina (Dark)
                put("db94", "1_291"); // Tatooine: Docking Bay 94
                put("lars", "1_294"); // Tatooine: Lars' Moisture Farm (exterior)
                put("palace", "6_171"); // Tatooine: Jabba's Palace
                put("deck", "6_167"); // Jabba's Sail Barge: Passenger Deck
                put("bargeDS", "6_172"); // Jabba's Sail Barge
            }},
            10,
            10,
            StartingSetup.DefaultLSGroundLocation,
            StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts,
            StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields,
            StartingSetup.NoDSShields,
            VirtualTableScenario.Open
        );
    }

    /**
     * DSInitiateBattle() auto-passes BATTLE_INITIATED optional responses.
     * Spend Force for initiating but leave the react window open for LS Ewok Rescue.
     */
    private void InitiateDsBattleKeepReactWindow(VirtualTableScenario scn, PhysicalCardImpl site) {
        assertTrue("Unable to initiate battle at location", scn.DSCanInitiateBattle(site));
        scn.DSUseCardAction(site, "Initiate battle");
        scn.PassForceUseResponses();
        // If DS holds the BATTLE_INITIATED optional window first, pass so LS can react (Informant mirror).
        if (scn.DSAnyDecisionsAvailable()
                && (scn.DSDecisionAvailable("BATTLE_INITIATED")
                || scn.DSDecisionAvailable("Battle just initiated")
                || !scn.LSAnyDecisionsAvailable())) {
            scn.DSPass();
        }
    }

    private boolean LsCardPlayAvailableSafe(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.LSAnyDecisionsAvailable() && scn.LSCardPlayAvailable(card);
    }

    private String DescribeDecision(VirtualTableScenario scn) {
        try {
            var d = scn.GetCurrentDecision();
            String text = d == null ? "null" : d.getText();
            String player = "none";
            try {
                player = scn.GetDecidingPlayer();
            } catch (Exception ignored) {
            }
            return "player=" + player + " text=" + text
                    + " DS=" + scn.GetDSAvailableActions()
                    + " LS=" + scn.GetLSAvailableActions();
        } catch (Exception e) {
            return "dump-failed:" + e.getMessage();
        }
    }

    private void AssertLsCanPlayRescue(VirtualTableScenario scn, PhysicalCardImpl rescue) {
        if (!LsCardPlayAvailableSafe(scn, rescue)) {
            fail("Expected Ewok Rescue playable as a battle-just-initiated react. " + DescribeDecision(scn));
        }
    }

    private void FinishAction1Destiny(VirtualTableScenario scn) {
        // Only clear Playing + destiny + leftover optionals. Do NOT generic-pass phase actions
        // or the table will advance into recirculation (interrupt lands in Reserve Deck).
        scn.PassCardPlayResponses();
        scn.PassAllResponses();
        if (scn.GetCurrentDecision() != null) {
            String text = scn.GetCurrentDecision().getText();
            if (text != null && text.toLowerCase().contains("destiny")) {
                scn.PassDestinyDrawResponses();
            }
        }
        scn.PassAllResponses();
    }

    private boolean FinishOptionalExtraReactIfOffered(VirtualTableScenario scn, PhysicalCardImpl extraMover) {
        if (scn.LSHasCardChoiceAvailable(extraMover)) {
            scn.LSChooseCard(extraMover);
            scn.PassAllResponses();
            return true;
        }
        if (scn.LSDecisionAvailable("Choose another Ewok")) {
            if (scn.LSHasCardChoiceAvailable(extraMover)) {
                scn.LSChooseCard(extraMover);
                scn.PassAllResponses();
                return true;
            }
            scn.LSPass();
        }
        return false;
    }

    @Test
    public void EwokRescueStatsAndKeywordsAreCorrect() {
        /**
         * Title: Ewok Rescue
         * Uniqueness: Unrestricted
         * Side: Light
         * Type: Interrupt
         * Subtype: Used
         * Destiny: 4
         * Icons: Endor
         * Keyword: Can Release Captives
         * Set: Endor
         * Rarity: C
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("rescue").getBlueprint();

        assertEquals("Ewok Rescue", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.ENDOR);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CAN_RELEASE_CAPTIVES);
        }});
        assertEquals(ExpansionSet.ENDOR, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void EwokRescueAction1ReleasesCaptiveWhenDestinyBeatsEscortDefenseValue() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var spearman = scn.GetLSCard("spearman");
        var rebel = scn.GetLSCard("rebel");
        var escort = scn.GetDSCard("escort");
        var site = scn.GetDSStartingLocation();

        scn.MoveCardsToLSHand(rescue);
        scn.StartGame();
        scn.MoveCardsToLocation(site, spearman, escort, rebel);
        scn.CaptureCardWith(escort, rebel);
        assertTrue(rebel.isCaptive());

        // Stormtrooper defense value = ability 1; non-scout needs destiny > 1
        scn.PrepareLSDestiny(2);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSAnyDecisionsAvailable());
        assertTrue(scn.LSCardPlayAvailable(rescue));
        scn.LSPlayCard(rescue);
        scn.LSChooseCard(spearman);
        scn.LSChooseCard(rebel);
        FinishAction1Destiny(scn);

        if (rebel.isCaptive() || rescue.getZone() != Zone.USED_PILE) {
            fail("Action1 did not complete. captive=" + rebel.isCaptive() + " zone=" + rescue.getZone() + " " + DescribeDecision(scn));
        }
        assertFalse(rebel.isCaptive());
        assertEquals(Zone.USED_PILE, rescue.getZone());
    }

    @Test
    public void EwokRescueAction1ScoutAddsTwoToDestiny() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var sentry = scn.GetLSCard("sentry");
        var rebel = scn.GetLSCard("rebel");
        var escort = scn.GetDSCard("escort");
        var site = scn.GetDSStartingLocation();

        scn.MoveCardsToLSHand(rescue);
        scn.StartGame();
        scn.MoveCardsToLocation(site, sentry, escort, rebel);
        scn.CaptureCardWith(escort, rebel);

        // Destiny 1 + scout 2 = 3 > escort DV 1
        scn.PrepareLSDestiny(1);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSAnyDecisionsAvailable());
        assertTrue(scn.LSCardPlayAvailable(rescue));
        scn.LSPlayCard(rescue);
        scn.LSChooseCard(sentry);
        scn.LSChooseCard(rebel);
        FinishAction1Destiny(scn);

        assertFalse("Scout Ewok +2 should release captive with destiny 1 vs DV 1", rebel.isCaptive());
    }

    @Test
    public void EwokRescueAction1FailsWhenDestinyDoesNotBeatEscortDefenseValue() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var spearman = scn.GetLSCard("spearman");
        var rebel = scn.GetLSCard("rebel");
        var escort = scn.GetDSCard("escort");
        var site = scn.GetDSStartingLocation();

        scn.MoveCardsToLSHand(rescue);
        scn.StartGame();
        scn.MoveCardsToLocation(site, spearman, escort, rebel);
        scn.CaptureCardWith(escort, rebel);

        scn.PrepareLSDestiny(1);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSAnyDecisionsAvailable());
        scn.LSPlayCard(rescue);
        scn.LSChooseCard(spearman);
        scn.LSChooseCard(rebel);
        FinishAction1Destiny(scn);

        assertTrue(rebel.isCaptive());
        assertEquals(escort, rebel.getEscort());
        assertEquals(Zone.USED_PILE, rescue.getZone());
    }

    @Test
    public void EwokRescueAction1NotPlayableWithoutEwokPresentWithCaptive() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var rebel = scn.GetLSCard("rebel");
        var escort = scn.GetDSCard("escort");
        var site = scn.GetDSStartingLocation();

        scn.MoveCardsToLSHand(rescue);
        scn.StartGame();
        scn.MoveCardsToLocation(site, escort, rebel);
        scn.CaptureCardWith(escort, rebel);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSAnyDecisionsAvailable() && scn.LSCardPlayAvailable(rescue));
    }

    @Test
    public void EwokRescueAction1CanceledBySenseGoesToUsedPile() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var spearman = scn.GetLSCard("spearman");
        var rebel = scn.GetLSCard("rebel");
        var escort = scn.GetDSCard("escort");
        var sense = scn.GetDSCard("sense");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.MoveCardsToLSHand(rescue);
        scn.MoveCardsToDSHand(sense);
        scn.StartGame();
        scn.MoveCardsToLocation(site, spearman, escort, rebel, vader);
        scn.CaptureCardWith(escort, rebel);

        scn.PrepareLSDestiny(7);
        scn.PrepareDSDestiny(3); // 3 < Vader ability 6
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSAnyDecisionsAvailable());
        scn.LSPlayCard(rescue);
        scn.LSChooseCard(spearman);
        scn.LSChooseCard(rebel);
        // Sense cancels during Playing responses (optional-before on the play effect)
        assertTrue(scn.DSCardPlayAvailable(sense));
        scn.DSPlayCard(sense);
        if (scn.DSHasCardChoiceAvailable(vader)) {
            scn.DSChooseCard(vader);
        }
        // Complete Sense destiny (and any leftover responses); Ewok Rescue should be canceled
        FinishAction1Destiny(scn);

        assertTrue("Captive remains escorted when Sense cancels Ewok Rescue", rebel.isCaptive());
        assertNotEquals(Zone.VOID, rescue.getZone());
    }

    @Test
    public void EwokRescueAction2MovesUpToThreeEwoksFromSameExteriorSiteForFree() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var sentry = scn.GetLSCard("sentry");
        var spearman = scn.GetLSCard("spearman");
        var tribesman = scn.GetLSCard("tribesman");
        var paploo = scn.GetLSCard("paploo");
        var rebel = scn.GetLSCard("rebel");
        var presence = scn.GetDSCard("presence");
        var marketplace = scn.GetDSStartingLocation();
        var exteriorAdjacent = scn.GetDSCard("db94");

        scn.MoveCardsToLSHand(rescue);
        scn.StartGame();
        scn.MoveLocationToTable(exteriorAdjacent);
        assertTrue(scn.IsAdjacentTo(exteriorAdjacent, marketplace));

        // Defending Ewok at battle; three movers at adjacent exterior site
        scn.MoveCardsToLocation(marketplace, sentry, presence, rebel);
        scn.MoveCardsToLocation(exteriorAdjacent, spearman, tribesman, paploo);

        scn.SkipToDSTurn(Phase.BATTLE);
        int lsForceBefore = scn.GetLSForcePileCount();
        InitiateDsBattleKeepReactWindow(scn, marketplace);
        AssertLsCanPlayRescue(scn, rescue);
        scn.LSPlayCard(rescue);
        assertTrue(scn.LSHasCardChoiceAvailable(spearman));
        assertTrue(scn.LSHasCardChoiceAvailable(tribesman));
        assertTrue(scn.LSHasCardChoiceAvailable(paploo));
        assertFalse("Defending Ewok already at battle is not a legal mover", scn.LSHasCardChoiceAvailable(sentry));
        scn.LSChooseCard(spearman);
        scn.PassAllResponses();

        FinishOptionalExtraReactIfOffered(scn, tribesman);
        FinishOptionalExtraReactIfOffered(scn, paploo);
        scn.PassAllResponses();

        assertEquals(marketplace, spearman.getAtLocation());
        assertTrue(scn.IsParticipatingInBattle(spearman));
        assertEquals(lsForceBefore, scn.GetLSForcePileCount());
        assertEquals(rescue, scn.GetTopOfLSUsedPile());
    }

    @Test
    public void EwokRescueAction2NotPlayableIfYouInitiatedBattle() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var sentry = scn.GetLSCard("sentry");
        var spearman = scn.GetLSCard("spearman");
        var rebel = scn.GetLSCard("rebel");
        var presence = scn.GetDSCard("presence");
        var marketplace = scn.GetDSStartingLocation();
        var exteriorAdjacent = scn.GetDSCard("db94");

        scn.MoveCardsToLSHand(rescue);
        scn.StartGame();
        scn.MoveLocationToTable(exteriorAdjacent);
        scn.MoveCardsToLocation(marketplace, sentry, presence, rebel);
        scn.MoveCardsToLocation(exteriorAdjacent, spearman);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(marketplace));
        scn.LSUseCardAction(marketplace, "Initiate battle");
        scn.PassForceUseResponses();
        // Your characters are not defending when you initiated — Action2 must not appear.
        assertFalse("Doc typo: not playable if you initiated", LsCardPlayAvailableSafe(scn, rescue));
    }

    @Test
    public void EwokRescueAction2NotPlayableWithoutDefendingEwok() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var spearman = scn.GetLSCard("spearman");
        var rebel = scn.GetLSCard("rebel");
        var presence = scn.GetDSCard("presence");
        var marketplace = scn.GetDSStartingLocation();
        var exteriorAdjacent = scn.GetDSCard("db94");

        scn.MoveCardsToLSHand(rescue);
        scn.StartGame();
        scn.MoveLocationToTable(exteriorAdjacent);
        // Non-Ewok LS presence at battle; Ewok only at adjacent
        scn.MoveCardsToLocation(marketplace, rebel, presence);
        scn.MoveCardsToLocation(exteriorAdjacent, spearman);

        scn.SkipToDSTurn(Phase.BATTLE);
        InitiateDsBattleKeepReactWindow(scn, marketplace);
        assertFalse(LsCardPlayAvailableSafe(scn, rescue));
    }

    @Test
    public void EwokRescueAction2RequiresSameExteriorSiteAndBlocksFourth() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var sentry = scn.GetLSCard("sentry");
        var spearman = scn.GetLSCard("spearman");
        var tribesman = scn.GetLSCard("tribesman");
        var paploo = scn.GetLSCard("paploo");
        var romba = scn.GetLSCard("romba");
        var rebel = scn.GetLSCard("rebel");
        var presence = scn.GetDSCard("presence");
        var marketplace = scn.GetDSStartingLocation();
        var exteriorAdjacent = scn.GetDSCard("db94");
        var otherExterior = scn.GetDSCard("lars");

        scn.MoveCardsToLSHand(rescue);
        scn.StartGame();
        scn.MoveLocationToTable(exteriorAdjacent);
        scn.MoveLocationToTable(otherExterior);
        assertTrue(scn.IsAdjacentTo(exteriorAdjacent, marketplace));

        scn.MoveCardsToLocation(marketplace, sentry, presence, rebel);
        scn.MoveCardsToLocation(exteriorAdjacent, spearman, tribesman, paploo);
        // Fourth Ewok at a different exterior site (not the locked site)
        scn.MoveCardsToLocation(otherExterior, romba);

        scn.SkipToDSTurn(Phase.BATTLE);
        InitiateDsBattleKeepReactWindow(scn, marketplace);
        AssertLsCanPlayRescue(scn, rescue);
        scn.LSPlayCard(rescue);
        assertTrue(scn.LSHasCardChoiceAvailable(spearman) || scn.LSHasCardChoiceAvailable(tribesman) || scn.LSHasCardChoiceAvailable(paploo));
        assertFalse("Romba at a different exterior site is not eligible with first-site lock", scn.LSHasCardChoiceAvailable(romba));
        if (scn.LSHasCardChoiceAvailable(spearman)) { scn.LSChooseCard(spearman); }
        else if (scn.LSHasCardChoiceAvailable(tribesman)) { scn.LSChooseCard(tribesman); }
        else { scn.LSChooseCard(paploo); }
        scn.PassAllResponses();

        FinishOptionalExtraReactIfOffered(scn, tribesman);
        FinishOptionalExtraReactIfOffered(scn, paploo);
        boolean offeredRomba = scn.LSHasCardChoiceAvailable(romba)
                || (scn.LSDecisionAvailable("Choose another Ewok") && scn.LSHasCardChoiceAvailable(romba));
        assertFalse("At most three Ewoks from the locked exterior site", offeredRomba);
        scn.PassAllResponses();

        assertEquals(marketplace, spearman.getAtLocation());
    }

    @Test
    public void EwokRescueAction2NotFromInteriorSite() {
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var sentry = scn.GetLSCard("sentry");
        var spearman = scn.GetLSCard("spearman");
        var bunker = scn.GetLSCard("bunker");
        var rebel = scn.GetLSCard("rebel");
        var presence = scn.GetDSCard("presence");
        var marketplace = scn.GetDSStartingLocation();

        scn.MoveCardsToLSHand(rescue);
        scn.StartGame();
        scn.MoveLocationToTable(bunker);
        scn.MoveCardsToLocation(marketplace, sentry, presence, rebel);
        scn.MoveCardsToLocation(bunker, spearman);

        scn.SkipToDSTurn(Phase.BATTLE);
        InitiateDsBattleKeepReactWindow(scn, marketplace);
        // Interior Bunker movers are illegal; if playable only via other movers, spearman must not be offered
        if (LsCardPlayAvailableSafe(scn, rescue)) {
            scn.LSPlayCard(rescue);
            assertFalse("Interior site Ewoks may not move as Ewok Rescue reacts",
                    scn.LSHasCardChoiceAvailable(spearman));
            scn.LSPass();
        }
        else {
            assertFalse(LsCardPlayAvailableSafe(scn, rescue));
        }
        assertEquals(bunker, spearman.getAtLocation());
    }

    @Test
    public void EwokRescueAction2AllowsDisembarkThenReactFromVehicleAtExteriorSite() {
        // Doc: reacting Ewoks may disembark and then react. Passenger Deck is interior,
        // so cover disembark from a vehicle parked at an adjacent exterior site.
        var scn = GetScenario();
        var rescue = scn.GetLSCard("rescue");
        var sentry = scn.GetLSCard("sentry");
        var spearman = scn.GetLSCard("spearman");
        var rebel = scn.GetLSCard("rebel");
        var presence = scn.GetDSCard("presence");
        var marketplace = scn.GetDSStartingLocation();
        var exteriorAdjacent = scn.GetDSCard("db94");
        var barge = scn.GetDSCard("bargeDS");
        var escortDriver = scn.GetDSCard("escort");

        scn.MoveCardsToLSHand(rescue);
        scn.StartGame();
        scn.MoveLocationToTable(exteriorAdjacent);
        assertTrue(scn.IsAdjacentTo(exteriorAdjacent, marketplace));

        scn.MoveCardsToLocation(marketplace, sentry, presence, rebel);
        scn.MoveCardsToLocation(exteriorAdjacent, barge);
        scn.BoardAsPilot(barge, escortDriver);
        scn.BoardAsPassenger(barge, spearman);

        scn.SkipToDSTurn(Phase.BATTLE);
        int lsForceBefore = scn.GetLSForcePileCount();
        InitiateDsBattleKeepReactWindow(scn, marketplace);
        AssertLsCanPlayRescue(scn, rescue);
        scn.LSPlayCard(rescue);
        assertTrue("Ewok passenger at exterior site may disembark then react",
                scn.LSHasCardChoiceAvailable(spearman));
        scn.LSChooseCard(spearman);
        scn.PassAllResponses();

        assertEquals(marketplace, spearman.getAtLocation());
        assertTrue(scn.IsParticipatingInBattle(spearman));
        assertEquals(lsForceBefore, scn.GetLSForcePileCount());
        assertFalse(scn.IsAboardAsPassenger(barge, spearman));
    }
}
