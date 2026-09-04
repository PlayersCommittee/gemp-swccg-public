package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.layout.LocationGroup;
import com.gempukku.swccgo.game.layout.RearrangeSites;
import com.gempukku.swccgo.logic.effects.ChooseAndRearrangeRelatedSitesEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for Retract The Bridge (2_138). Overlay 17004 only.
 * Relies on RearrangeSites helper from #1017 / e254419.
 */
public class Card_2_138_Tests {

    private static final String REARRANGE_TEXT = "Rearrange interior Death Star sites";

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("trash", "1_125"); // Death Star: Trash Compactor
                    put("trench", "2_062"); // Death Star: Trench
                    put("on-the-edge", "1_101"); // On The Edge
                    put("sense", "1_109"); // Sense
                    put("skywalkers", "1_110"); // Skywalkers
                    put("luke", "1_019"); // Luke Skywalker (ability > 2)
                    put("guest", "5_080"); // Cloud City: Guest Quarters
                }},
                new HashMap<>()
                {{
                    put("retract", "2_138"); // Retract The Bridge
                    put("core", "1_283"); // Death Star: Central Core
                    put("corridor", "1_284"); // Death Star: Detention Block Corridor
                    put("db327", "1_285"); // Death Star: Docking Bay 327
                    put("war-room", "1_287"); // Death Star: War Room
                    put("conference", "2_144"); // Death Star: Conference Room
                    put("ds-system", "2_143"); // Death Star system
                    put("incinerator", "5_170"); // Cloud City: Incinerator (Dark)
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

    private void putLocation(VirtualTableScenario scn, PhysicalCardImpl location) {
        scn.MoveLocationToTable(location);
    }

    private List<PhysicalCard> interiorTops(VirtualTableScenario scn) {
        LocationGroup group = scn.gameState().getLocationsLayout().findGroupForSystemMatching(
                scn.game(), Title.Death_Star, RearrangeSites.interiorSitesOfSystem(Title.Death_Star));
        assertNotNull(group);
        return new ArrayList<PhysicalCard>(group.getTopCardsInGroup());
    }

    private void prepareDeployWithForce(VirtualTableScenario scn, PhysicalCardImpl retract) {
        scn.MoveCardsToDSHand(retract);
        scn.SkipToDSTurn(Phase.DEPLOY);
    }

    private void playRearrangeAndChooseOrder(VirtualTableScenario scn, PhysicalCardImpl retract,
                                             PhysicalCardImpl first, PhysicalCardImpl second) {
        assertTrue(scn.DSCardPlayAvailable(retract, REARRANGE_TEXT));
        scn.DSPlayCard(retract, REARRANGE_TEXT);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.CHOICE_TEXT));
        scn.DSChooseCard(first);
        if (scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.NEXT_CHOICE_TEXT)) {
            scn.DSChooseCard(second);
        }
        scn.PassAllResponses();
    }

    @Test
    public void RetractTheBridgeStatsAreCorrect() {
        /**
         * Title: Retract The Bridge
         * Uniqueness: UNRESTRICTED
         * Side: Dark
         * Type: Interrupt
         * Subtype: Lost
         * Destiny: 3
         * Set: A New Hope
         * Rarity: R1
         * Game Text: During your deploy phase, use X Force to rearrange all interior Death Star sites,
         * where X = total number of those sites. All cards at a given site move along with that site.
         * OR Cancel On The Edge.
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("retract").getBlueprint();

        assertEquals("Retract The Bridge", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.isCardType(CardType.INTERRUPT));
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getIconCount(Icon.A_NEW_HOPE));
        assertEquals(Rarity.R1, card.getRarity());
    }

    @Test
    public void CostsOneForceWithOneInteriorDeathStarSite() {
        var scn = GetScenario();
        var retract = scn.GetDSCard("retract");
        var corridor = scn.GetDSCard("corridor");

        scn.StartGame();
        putLocation(scn, corridor);
        prepareDeployWithForce(scn, retract);

        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(forceBefore >= 1);
        assertTrue(scn.DSCardPlayAvailable(retract, REARRANGE_TEXT));
        scn.DSPlayCard(retract, REARRANGE_TEXT);
        scn.PassAllResponses();
        // Single site auto-completes choose-in-order
        scn.PassAllResponses();

        assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
        assertEquals(Zone.LOST_PILE, retract.getZone());
    }

    @Test
    public void CostsTwoForceWithTwoInteriorDeathStarSitesAndRearranges() {
        var scn = GetScenario();
        var retract = scn.GetDSCard("retract");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        prepareDeployWithForce(scn, retract);

        List<PhysicalCard> interiors = interiorTops(scn);
        assertEquals(2, interiors.size());
        PhysicalCardImpl a = (PhysicalCardImpl) interiors.get(0);
        PhysicalCardImpl b = (PhysicalCardImpl) interiors.get(1);

        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(forceBefore >= 2);
        playRearrangeAndChooseOrder(scn, retract, b, a);

        assertEquals(forceBefore - 2, scn.GetDSForcePileCount());
        assertEquals(Arrays.asList(b, a), interiorTops(scn));
        assertEquals(Zone.LOST_PILE, retract.getZone());
    }

    @Test
    public void CostsThreeForceWithThreeInteriorSitesCardsRideDb327AndTrenchStay() {
        var scn = GetScenario();
        var retract = scn.GetDSCard("retract");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");
        var db327 = scn.GetDSCard("db327");
        var trench = scn.GetLSCard("trench");
        var guest = scn.GetLSCard("guest");
        var incinerator = scn.GetDSCard("incinerator");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        putLocation(scn, db327);
        putLocation(scn, trench);
        putLocation(scn, guest);
        putLocation(scn, incinerator);
        scn.MoveCardsToLocation(warRoom, trooper);

        prepareDeployWithForce(scn, retract);

        List<PhysicalCard> interiors = interiorTops(scn);
        assertEquals(3, interiors.size());
        PhysicalCardImpl left = (PhysicalCardImpl) interiors.get(0);
        PhysicalCardImpl mid = (PhysicalCardImpl) interiors.get(1);
        PhysicalCardImpl right = (PhysicalCardImpl) interiors.get(2);
        int db327Index = db327.getLocationZoneIndex();
        int trenchIndex = trench.getLocationZoneIndex();
        List<PhysicalCard> bespinBefore = new ArrayList<PhysicalCard>(
                scn.gameState().getLocationsLayout().findGroupForSystemMatching(
                        scn.game(), Title.Bespin, RearrangeSites.interiorSitesOfSystem(Title.Bespin)).getTopCardsInGroup());

        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(forceBefore >= 3);
        assertTrue(scn.DSCardPlayAvailable(retract, REARRANGE_TEXT));
        scn.DSPlayCard(retract, REARRANGE_TEXT);
        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.CHOICE_TEXT));
        assertTrue(scn.DSHasCardChoicesAvailable(left, mid, right));
        assertTrue(scn.DSHasCardChoiceNotAvailable(db327));
        assertTrue(scn.DSHasCardChoiceNotAvailable(trench));
        assertTrue(scn.DSHasCardChoiceNotAvailable(guest));
        assertTrue(scn.DSHasCardChoiceNotAvailable(incinerator));

        scn.DSChooseCard(right);
        assertTrue(scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.NEXT_CHOICE_TEXT));
        scn.DSChooseCard(mid);
        // last auto-chosen
        scn.PassAllResponses();

        assertEquals(forceBefore - 3, scn.GetDSForcePileCount());
        assertEquals(Arrays.asList(right, mid, left), interiorTops(scn));
        assertTrue(scn.CardsAtLocation(warRoom, trooper));
        assertEquals(warRoom, trooper.getAtLocation());
        assertEquals(db327Index, db327.getLocationZoneIndex());
        assertEquals(trenchIndex, trench.getLocationZoneIndex());
        List<PhysicalCard> bespinAfter = new ArrayList<PhysicalCard>(
                scn.gameState().getLocationsLayout().findGroupForSystemMatching(
                        scn.game(), Title.Bespin, RearrangeSites.interiorSitesOfSystem(Title.Bespin)).getTopCardsInGroup());
        assertEquals(bespinBefore, bespinAfter);
    }

    @Test
    public void CannotPlayRearrangeSideWithZeroInteriorDeathStarSites() {
        var scn = GetScenario();
        var retract = scn.GetDSCard("retract");
        var db327 = scn.GetDSCard("db327");
        var trench = scn.GetLSCard("trench");

        scn.StartGame();
        putLocation(scn, db327);
        putLocation(scn, trench);
        prepareDeployWithForce(scn, retract);

        assertEquals(0, Filters.countTopLocationsOnTable(scn.game(), RearrangeSites.interiorSitesOfSystem(Title.Death_Star)));
        assertFalse(scn.DSCardPlayAvailable(retract, REARRANGE_TEXT));
    }

    @Test
    public void CannotPlayRearrangeOutsideDeployPhase() {
        var scn = GetScenario();
        var retract = scn.GetDSCard("retract");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        scn.MoveCardsToDSHand(retract);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.GetDSForcePileCount() >= 2);
        assertFalse(scn.DSCardPlayAvailable(retract, REARRANGE_TEXT));
    }

    @Test
    public void CancelsOnTheEdgeBeingPlayed() {
        var scn = GetScenario();
        var retract = scn.GetDSCard("retract");
        var onTheEdge = scn.GetLSCard("on-the-edge");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(onTheEdge);
        scn.MoveCardsToDSHand(retract);

        scn.SkipToPhase(Phase.DEPLOY);
        // LS turn deploy - play On The Edge targeting Luke
        assertTrue(scn.LSCardPlayAvailable(onTheEdge));
        scn.LSPlayCard(onTheEdge);
        // Choose Rebel target
        if (scn.LSHasCardChoiceAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        // Choose number 1-6 if prompted
        if (scn.LSDecisionAvailable("Choose a number")) {
            scn.LSChoose("1");
        }

        // DS may cancel with Retract The Bridge
        assertTrue(scn.DSCardPlayAvailable(retract) || scn.DSPlayLostInterruptAvailable(retract)
                || scn.DSCardActionAvailable(retract, "Cancel"));
        if (scn.DSCardActionAvailable(retract, "Cancel")) {
            scn.DSUseCardAction(retract, "Cancel");
        }
        else if (scn.DSPlayLostInterruptAvailable(retract)) {
            scn.DSPlayLostInterrupt(retract);
        }
        else {
            scn.DSPlayCard(retract);
        }
        scn.PassAllResponses();

        assertEquals(Zone.LOST_PILE, retract.getZone());
        // On The Edge should be canceled (void/lost), not successfully retrieving
        assertNotEquals(Zone.HAND, onTheEdge.getZone());
    }

    @Test
    public void SkywalkersCanCancelRetractTheBridgeBeingPlayed() {
        var scn = GetScenario();
        var retract = scn.GetDSCard("retract");
        var skywalkers = scn.GetLSCard("skywalkers");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        scn.MoveCardsToLSHand(skywalkers);
        prepareDeployWithForce(scn, retract);

        List<PhysicalCard> before = interiorTops(scn);
        assertTrue(scn.DSCardPlayAvailable(retract, REARRANGE_TEXT));
        scn.DSPlayCard(retract, REARRANGE_TEXT);

        assertTrue(scn.LSCardPlayAvailable(skywalkers) || scn.LSPlayLostInterruptAvailable(skywalkers)
                || scn.LSCardActionAvailable(skywalkers, "Cancel"));
        if (scn.LSCardActionAvailable(skywalkers, "Cancel")) {
            scn.LSUseCardAction(skywalkers, "Cancel");
        }
        else if (scn.LSPlayLostInterruptAvailable(skywalkers)) {
            scn.LSPlayLostInterrupt(skywalkers);
        }
        else {
            scn.LSPlayCard(skywalkers);
        }
        scn.PassAllResponses();

        assertEquals(before, interiorTops(scn));
        assertEquals(Zone.LOST_PILE, retract.getZone());
        assertEquals(Zone.LOST_PILE, skywalkers.getZone());
    }

    @Test
    public void SenseIsAvailableAsResponseWhenRetractIsPlayed() {
        var scn = GetScenario();
        var retract = scn.GetDSCard("retract");
        var sense = scn.GetLSCard("sense");
        var luke = scn.GetLSCard("luke");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(sense);
        prepareDeployWithForce(scn, retract);

        assertTrue(scn.DSCardPlayAvailable(retract, REARRANGE_TEXT));
        scn.DSPlayCard(retract, REARRANGE_TEXT);

        // Sense (Used Interrupt) should be offered as a response to cancel the Interrupt
        boolean senseOffered = scn.LSPlayUsedInterruptAvailable(sense)
                || scn.LSCardPlayAvailable(sense)
                || scn.LSCardActionAvailable(sense, "Cancel");
        assertTrue("Sense should be available to respond to Retract The Bridge", senseOffered);
        // Decline Sense and finish rearrange so the table stays consistent
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.CHOICE_TEXT)) {
            PhysicalCardImpl a = (PhysicalCardImpl) interiorTops(scn).get(0);
            PhysicalCardImpl b = (PhysicalCardImpl) interiorTops(scn).get(1);
            scn.DSChooseCard(b);
            if (scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.NEXT_CHOICE_TEXT)) {
                scn.DSChooseCard(a);
            }
            scn.PassAllResponses();
        }
    }
}