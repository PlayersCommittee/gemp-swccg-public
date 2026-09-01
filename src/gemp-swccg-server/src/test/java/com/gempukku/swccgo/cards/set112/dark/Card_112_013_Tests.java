package com.gempukku.swccgo.cards.set112.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Mercenary Pilot (112_013).
 * Cloud-sector destiny is an optional once-per-turn popup. Driving a transport still adds destiny automatically.
 * See the Testing Rig helpers in VirtualTableScenario for battle and optional-action control.
 */
public class Card_112_013_Tests {
    /**
     * Dark Side Mercenary Pilot, TIE Fighters, and Cloud City locations.
     * Light Side only supplies opponents to battle and one related exterior site.
     */
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("platform327", "5_83");
                }},
                new HashMap<>() {{
                    put("pilot1", "112_13");
                    put("pilot2", "112_13");
                    put("tie1", "1_304");
                    put("tie2", "1_304");
                    put("crawler", "1_309");
                    put("trooper1", "1_194");
                    put("trooper2", "1_194");
                    put("bespin", "5_164");
                    put("cloudCity", "5_165");
                    put("eastPlatform", "5_169");
                }},
                20,
                20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void MercenaryPilotStatsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("pilot1").getBlueprint();
        assertEquals("Mercenary Pilot", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
    }

    /**
     * Mercenary Pilot piloting at a cloud sector is offered an optional add-one-battle-destiny
     * during battle at a related exterior site. Accepting it adds one destiny.
     */
    @Test
    public void MercenaryPilotPilotingAtCloudSectorIsOfferedOptionalBattleDestinyAtRelatedExteriorSite() {
        var scn = GetScenario();
        var board = PlaceCloudSectorPilotAndRelatedExteriorBattle(scn, false);

        scn.SkipToDSTurn(Phase.BATTLE);
        InitiateBattleKeepingStartResponses(scn, board.eastPlatform);

        String debug = "decision=" + (scn.DSGetDecision() == null ? "none" : scn.DSGetDecision().getText())
                + " ls=" + (scn.LSGetDecision() == null ? "none" : scn.LSGetDecision().getText())
                + " actions=" + scn.GetDSAvailableActions();
        assertTrue(debug, scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(board.pilot1, "Add one battle destiny"));
        scn.DSUseCardAction(board.pilot1, "Add one battle destiny");

        scn.SkipToPowerSegment();
        assertEquals(1, scn.GetDSBattleDestinyCount());
        assertTrue(scn.DSDecisionAvailable("Do you want to draw 1 battle destiny?"));
    }

    /**
     * The same Mercenary Pilot cannot offer the cloud-sector destiny more than once per turn.
     * After it is used in one battle, a later battle this turn at another related exterior site has no option.
     */
    @Test
    public void MercenaryPilotCannotAddCloudSectorBattleDestinyMoreThanOncePerTurn() {
        var scn = GetScenario();
        var board = PlaceCloudSectorPilotAndRelatedExteriorBattle(scn, true);

        scn.SkipToDSTurn(Phase.BATTLE);
        InitiateBattleKeepingStartResponses(scn, board.eastPlatform);
        assertTrue(scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(board.pilot1, "Add one battle destiny"));
        scn.DSUseCardAction(board.pilot1, "Add one battle destiny");
        EndCurrentBattle(scn);

        assertTrue(scn.DSCanInitiateBattle(board.platform327));
        InitiateBattleKeepingStartResponses(scn, board.platform327);
        String oncePerTurnDebug = "decision=" + (scn.DSGetDecision() == null ? "none" : scn.DSGetDecision().getText())
                + " actions=" + scn.GetDSAvailableActions();
        assertFalse(oncePerTurnDebug, scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(board.pilot1, "Add one battle destiny"));

        scn.SkipToPowerSegment();
        assertEquals(0, scn.GetDSBattleDestinyCount());
        assertFalse(scn.DSDecisionAvailable("Do you want to draw 1 battle destiny?"));
    }

    /**
     * A different Mercenary Pilot still has its own once-per-turn, even if the first copy already used the option.
     * Second battle at another related exterior site: the unused copy is offered.
     */
    @Test
    public void DifferentMercenaryPilotCanAddCloudSectorBattleDestinyAfterFirstCopyUsedItsOncePerTurn() {
        var scn = GetScenario();
        var board = PlaceCloudSectorPilotAndRelatedExteriorBattle(scn, true);

        scn.MoveCardsToLocation(board.cloudCity, board.tie2);
        scn.BoardAsPilot(board.tie2, board.pilot2);

        scn.SkipToDSTurn(Phase.BATTLE);
        InitiateBattleKeepingStartResponses(scn, board.eastPlatform);
        assertTrue(scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(board.pilot1, "Add one battle destiny"));
        assertTrue(scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(board.pilot2, "Add one battle destiny"));
        scn.DSUseCardAction(board.pilot1, "Add one battle destiny");
        if (scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(board.pilot2, "Add one battle destiny")) {
            scn.DSDecline();
        }
        EndCurrentBattle(scn);

        InitiateBattleKeepingStartResponses(scn, board.platform327);
        String twoCopyDebug = "decision=" + (scn.DSGetDecision() == null ? "none" : scn.DSGetDecision().getText())
                + " actions=" + scn.GetDSAvailableActions();
        assertFalse(twoCopyDebug, scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(board.pilot1, "Add one battle destiny"));
        assertTrue(twoCopyDebug, scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(board.pilot2, "Add one battle destiny"));
        scn.DSUseCardAction(board.pilot2, "Add one battle destiny");

        scn.SkipToPowerSegment();
        assertEquals(1, scn.GetDSBattleDestinyCount());
        assertTrue(scn.DSDecisionAvailable("Do you want to draw 1 battle destiny?"));
    }

    /**
     * Driving a transport still automatically adds one battle destiny. There is no optional popup for that text.
     */
    @Test
    public void MercenaryPilotDrivingTransportAutomaticallyAddsBattleDestinyWithoutPopup() {
        var scn = GetScenario();

        var pilot1 = scn.GetDSCard("pilot1");
        var crawler = scn.GetDSCard("crawler");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, crawler, luke);
        scn.BoardAsPilot(crawler, pilot1);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSUseCardAction(site, "Initiate battle");
        scn.PassForceUseResponses();
        assertFalse(scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(pilot1, "Add one battle destiny"));
        scn.PassBattleStartResponses();

        scn.SkipToPowerSegment();
        assertEquals(1, scn.GetDSBattleDestinyCount());
        assertTrue(scn.DSDecisionAvailable("Do you want to draw 1 battle destiny?"));
    }

    /**
     * Puts Mercenary Pilot in a TIE at Bespin: Cloud City, with Dark Side and Light Side presence at related exterior sites.
     * @param includeSecondSite true if a second related exterior site should also be ready for battle this turn
     */
    private CloudBoard PlaceCloudSectorPilotAndRelatedExteriorBattle(VirtualTableScenario scn, boolean includeSecondSite) {
        var board = new CloudBoard();
        board.pilot1 = scn.GetDSCard("pilot1");
        board.pilot2 = scn.GetDSCard("pilot2");
        board.tie1 = scn.GetDSCard("tie1");
        board.tie2 = scn.GetDSCard("tie2");
        board.trooper1 = scn.GetDSCard("trooper1");
        board.trooper2 = scn.GetDSCard("trooper2");
        board.bespin = scn.GetDSCard("bespin");
        board.cloudCity = scn.GetDSCard("cloudCity");
        board.eastPlatform = scn.GetDSCard("eastPlatform");
        board.platform327 = scn.GetLSCard("platform327");
        board.luke = scn.GetLSCard("luke");
        board.leia = scn.GetLSCard("leia");

        scn.StartGame();
        scn.MoveLocationToTable(board.bespin);
        scn.MoveLocationToTable(board.cloudCity);
        scn.MoveLocationToTable(board.eastPlatform);
        scn.MoveLocationToTable(board.platform327);

        scn.MoveCardsToLocation(board.cloudCity, board.tie1);
        scn.BoardAsPilot(board.tie1, board.pilot1);
        scn.MoveCardsToLocation(board.eastPlatform, board.trooper1, board.luke);
        if (includeSecondSite) {
            scn.MoveCardsToLocation(board.platform327, board.trooper2, board.leia);
        }
        return board;
    }

    /**
     * Starts a Dark Side battle but leaves the battle-initiated optional window open so Mercenary Pilot can be clicked.
     */
    private void InitiateBattleKeepingStartResponses(VirtualTableScenario scn, PhysicalCardImpl location) {
        assertTrue("Unable to initiate battle at location", scn.DSCanInitiateBattle(location));
        scn.DSUseCardAction(location, "Initiate battle");
        scn.PassForceUseResponses();
        // Opponent is asked first; pass Light Side so Dark Side sees Mercenary Pilot's optional.
        if (scn.LSDecisionAvailable("BATTLE_INITIATED")) {
            scn.LSPass();
        }
    }

    /**
     * Ends the current battle without drawing destiny so another battle can start this turn.
     * Leftover battle damage is paid from Reserve Deck.
     */
    private void EndCurrentBattle(VirtualTableScenario scn) {
        if (!scn.IsReachedPowerSegment()) {
            scn.SkipToPowerSegment();
        }
        scn.SkipBattleDestinyDraws(false);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        for (int i = 0; i < 25; i++) {
            if (scn.AwaitingDSBattlePhaseActions()) {
                return;
            }
            if (scn.AwaitingDSBattleDamagePayment()) {
                scn.DSPayRemainingBattleDamageFromReserveDeck();
                continue;
            }
            if (scn.AwaitingLSBattleDamagePayment()) {
                scn.LSPayRemainingBattleDamageFromReserveDeck();
                continue;
            }
            if (scn.DSDecisionAvailable("battle destiny?")) {
                scn.DSChooseNo();
                scn.PassDestinyDrawResponses();
                continue;
            }
            if (scn.LSDecisionAvailable("battle destiny?")) {
                scn.LSChooseNo();
                scn.PassDestinyDrawResponses();
                continue;
            }
            if (scn.LSAnyDecisionsAvailable() && !scn.DSAnyDecisionsAvailable()) {
                scn.LSPass();
                continue;
            }
            if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
                continue;
            }
            return;
        }
        throw new RuntimeException("Could not finish battle. decision=" +
                (scn.GetCurrentDecision() == null ? "none" : scn.GetCurrentDecision().getText()));
    }

    private static class CloudBoard {
        PhysicalCardImpl pilot1;
        PhysicalCardImpl pilot2;
        PhysicalCardImpl tie1;
        PhysicalCardImpl tie2;
        PhysicalCardImpl trooper1;
        PhysicalCardImpl trooper2;
        PhysicalCardImpl bespin;
        PhysicalCardImpl cloudCity;
        PhysicalCardImpl eastPlatform;
        PhysicalCardImpl platform327;
        PhysicalCardImpl luke;
        PhysicalCardImpl leia;
    }
}
