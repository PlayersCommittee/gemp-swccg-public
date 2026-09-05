package com.gempukku.swccgo.cards.set5.dark;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_5_158_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("xwing", "1_146");
                    put("bespin", "5_76");
                    put("clouds", "5_85");
                }},
                new HashMap<>() {{
                    put("sentry", "5_158");
                    put("tie", "1_304");
                    put("tie2", "1_304");
                    put("black2", "1_299");
                    put("ds612", "1_173");
                    put("ds613", "1_174");
                    put("hoth", "3_143");
                    put("big-one", "4_156");
                }},
                20,
                20,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void StatsAndKeywordsAreCorrect_5_158_TIESentryShips() {
        /**
         * Title: TIE Sentry Ships
         * Uniqueness: Unique
         * Side: Dark
         * Type: Interrupt
         * Subtype: Lost
         * Destiny: 5
         * Icons: Cloud City
         * Game Text: If opponent just initiated a Force drain at a system, cloud sector or asteroid sector,
         *      you may 'react' by deploying TIEs and pilots to that location (at normal use of the Force).
         * Lore: Several TIEs were assigned to patrol Cloud City prior to the Imperial occupation of Bespin.
         *      Their instructions were to herd any vessels attempting to escape toward the Executor.
         * Set: Cloud City
         * Rarity: C
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("sentry").getBlueprint();

        assertEquals("TIE Sentry Ships", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.CLOUD_CITY, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void DeploysTIEAsReactToSystemForceDrain_5_158_TIESentryShips() {
        var scn = GetScenario();

        var sentry = scn.GetDSCard("sentry");
        var tie = scn.GetDSCard("tie");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(system, xwing);
        scn.MoveCardsToDSHand(sentry, tie);

        scn.SkipToLSTurn(Phase.CONTROL);
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.LSForceDrainAvailable(system));
        scn.LSForceDrainAt(system);
        assertTrue(scn.DSCardPlayAvailable(sentry));
        scn.DSPlayCard(sentry);
        scn.PassAllResponses();
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(tie)) {
            scn.DSChooseCard(tie);
        }
        scn.PassAllResponses();

        assertAtLocation(system, tie);
        assertInZone(Zone.LOST_PILE, sentry);
        assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void AdditionalReactChoiceCanBeStoppedWithDone_5_158_TIESentryShips() {
        var scn = GetScenario();

        var sentry = scn.GetDSCard("sentry");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(system, xwing);
        scn.MoveCardsToDSHand(sentry, tie, tie2);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(system));
        scn.LSForceDrainAt(system);
        assertTrue(scn.DSCardPlayAvailable(sentry));
        scn.DSPlayCard(sentry);
        scn.PassAllResponses();
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(tie)) {
            scn.DSChooseCard(tie);
        }
        scn.PassAllResponses();

        assertTrue(scn.DSAnyDecisionsAvailable());
        assertTrue(scn.DSGetDecision().getText().toLowerCase().contains("done"));
        assertTrue(scn.DSHasCardChoiceAvailable(tie2));
        scn.DSPass();
        scn.PassAllResponses();

        assertAtLocation(system, tie);
        assertInZone(Zone.HAND, tie2);
        assertInZone(Zone.LOST_PILE, sentry);
    }

    @Test
    public void ExtraReactDoesNotOfferCardsThatCostMoreThanRemainingForce_5_158_TIESentryShips() {
        var scn = GetScenario();

        var sentry = scn.GetDSCard("sentry");
        var tie = scn.GetDSCard("tie");
        var ds612 = scn.GetDSCard("ds612");
        var ds613 = scn.GetDSCard("ds613");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(system, xwing);
        scn.MoveCardsToDSHand(sentry, tie, ds612, ds613);

        scn.SkipToLSTurn(Phase.CONTROL);
        int currentForce = scn.GetDSForcePileCount();
        if (currentForce > 2) {
            scn.DSUseForceCheat(currentForce - 2);
        } else if (currentForce < 2) {
            scn.DSActivateForceCheat(2 - currentForce);
        }

        assertTrue(scn.LSForceDrainAvailable(system));
        scn.LSForceDrainAt(system);
        assertTrue(scn.DSCardPlayAvailable(sentry));
        scn.DSPlayCard(sentry);
        scn.PassAllResponses();
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(tie)) {
            scn.DSChooseCard(tie);
        }
        scn.PassAllResponses();

        assertEquals(1, scn.GetDSForcePileCount());
        assertFalse("2-deploy pilot should not light up with 1 Force",
                scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(ds612));
        assertFalse("2-deploy pilot should not light up with 1 Force",
                scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(ds613));
        if (scn.DSAnyDecisionsAvailable()
                && scn.DSGetDecision().getText().toLowerCase().contains("done")) {
            scn.DSPass();
            scn.PassAllResponses();
        }

        assertAtLocation(system, tie);
        assertInZone(Zone.HAND, ds612);
        assertInZone(Zone.HAND, ds613);
        assertInZone(Zone.LOST_PILE, sentry);
    }

    @Test
    public void ExtraReactDoesNotOfferBlack2WhenRemainingForceCannotPayForRequiredPilot_5_158_TIESentryShips() {
        var scn = GetScenario();

        var sentry = scn.GetDSCard("sentry");
        var tie = scn.GetDSCard("tie");
        var black2 = scn.GetDSCard("black2");
        var ds612 = scn.GetDSCard("ds612");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(system, xwing);
        scn.MoveCardsToDSHand(sentry, tie, black2, ds612);

        scn.SkipToLSTurn(Phase.CONTROL);
        int currentForce = scn.GetDSForcePileCount();
        if (currentForce > 2) {
            scn.DSUseForceCheat(currentForce - 2);
        } else if (currentForce < 2) {
            scn.DSActivateForceCheat(2 - currentForce);
        }

        assertTrue(scn.LSForceDrainAvailable(system));
        scn.LSForceDrainAt(system);
        assertTrue(scn.DSCardPlayAvailable(sentry));
        scn.DSPlayCard(sentry);
        scn.PassAllResponses();
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(tie)) {
            scn.DSChooseCard(tie);
        }
        scn.PassAllResponses();

        assertEquals(1, scn.GetDSForcePileCount());
        assertFalse("Black 2 should not light up without enough Force for a required pilot",
                scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(black2));
        assertFalse("2-deploy pilot should not light up with 1 Force",
                scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(ds612));
        if (scn.DSAnyDecisionsAvailable()
                && scn.DSGetDecision().getText().toLowerCase().contains("done")) {
            scn.DSPass();
            scn.PassAllResponses();
        }

        assertAtLocation(system, tie);
        assertInZone(Zone.HAND, black2);
        assertInZone(Zone.HAND, ds612);
        assertInZone(Zone.LOST_PILE, sentry);
    }

    @Test
    public void ExtraReactStillOffersOneCostTIEWhenExactlyOneForceRemains_5_158_TIESentryShips() {
        var scn = GetScenario();

        var sentry = scn.GetDSCard("sentry");
        var tie = scn.GetDSCard("tie");
        var tie2 = scn.GetDSCard("tie2");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(system, xwing);
        scn.MoveCardsToDSHand(sentry, tie, tie2);

        scn.SkipToLSTurn(Phase.CONTROL);
        int currentForce = scn.GetDSForcePileCount();
        if (currentForce > 2) {
            scn.DSUseForceCheat(currentForce - 2);
        } else if (currentForce < 2) {
            scn.DSActivateForceCheat(2 - currentForce);
        }

        assertTrue(scn.LSForceDrainAvailable(system));
        scn.LSForceDrainAt(system);
        assertTrue(scn.DSCardPlayAvailable(sentry));
        scn.DSPlayCard(sentry);
        scn.PassAllResponses();
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(tie)) {
            scn.DSChooseCard(tie);
        }
        scn.PassAllResponses();

        assertEquals(1, scn.GetDSForcePileCount());
        assertTrue(scn.DSAnyDecisionsAvailable());
        assertTrue(scn.DSGetDecision().getText().toLowerCase().contains("done"));
        assertTrue(scn.DSHasCardChoiceAvailable(tie2));
        scn.DSPass();
        scn.PassAllResponses();

        assertAtLocation(system, tie);
        assertInZone(Zone.HAND, tie2);
        assertInZone(Zone.LOST_PILE, sentry);
    }

    @Test
    public void PlayableAsReactToCloudSectorForceDrain_5_158_TIESentryShips() {
        var scn = GetScenario();

        var sentry = scn.GetDSCard("sentry");
        var tie = scn.GetDSCard("tie");
        var xwing = scn.GetLSCard("xwing");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");

        scn.StartGame();

        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, xwing);
        scn.MoveCardsToDSHand(sentry, tie);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(clouds));
        scn.LSForceDrainAt(clouds);
        assertTrue(scn.DSCardPlayAvailable(sentry));
        scn.DSPlayCard(sentry);
        scn.PassAllResponses();
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(tie)) {
            scn.DSChooseCard(tie);
        }
        scn.PassAllResponses();

        assertAtLocation(clouds, tie);
        assertInZone(Zone.LOST_PILE, sentry);
    }

    @Test
    public void PlayableAsReactToAsteroidSectorForceDrain_5_158_TIESentryShips() {
        var scn = GetScenario();

        var sentry = scn.GetDSCard("sentry");
        var tie = scn.GetDSCard("tie");
        var xwing = scn.GetLSCard("xwing");
        var hoth = scn.GetDSCard("hoth");
        var bigOne = scn.GetDSCard("big-one");

        scn.StartGame();

        scn.MoveLocationToTable(hoth);
        scn.MoveLocationToTable(bigOne);
        scn.MoveCardsToLocation(bigOne, xwing);
        scn.MoveCardsToDSHand(sentry, tie);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(bigOne));
        scn.LSForceDrainAt(bigOne);
        assertTrue(scn.DSCardPlayAvailable(sentry));
        scn.DSPlayCard(sentry);
        scn.PassAllResponses();
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(tie)) {
            scn.DSChooseCard(tie);
        }
        scn.PassAllResponses();

        assertAtLocation(bigOne, tie);
        assertInZone(Zone.LOST_PILE, sentry);
    }

    @Test
    public void NotPlayableAsReactToBattleAtSystem_5_158_TIESentryShips() {
        var scn = GetScenario();

        var sentry = scn.GetDSCard("sentry");
        var tie = scn.GetDSCard("tie");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(system, xwing, tie);
        scn.MoveCardsToDSHand(sentry);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        assertFalse(scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(sentry));
    }

    @Test
    public void NotPlayableAsReactToForceDrainAtSite_5_158_TIESentryShips() {
        var scn = GetScenario();

        var sentry = scn.GetDSCard("sentry");
        var tie = scn.GetDSCard("tie");
        var site = scn.GetDSStartingLocation();
        var lsPresence = scn.GetLSFiller(1);

        scn.StartGame();

        scn.MoveCardsToLocation(site, lsPresence);
        scn.MoveCardsToDSHand(sentry, tie);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(site));
        scn.LSForceDrainAt(site);
        assertFalse(scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(sentry));
    }
}
