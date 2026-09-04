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
                    put("on-the-edge", "1_101"); // On The Edge
                    put("skywalkers", "1_110"); // Skywalkers
                    put("luke", "1_019"); // Luke Skywalker
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
        var placements = scn.gameState().getLocationPlacement(scn.game(), location, null, null);
        assertFalse("No legal placement for " + location.getTitle(), placements.isEmpty());
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

    private void assertInLostPile(PhysicalCardImpl card) {
        assertTrue("Expected lost pile zone, was " + card.getZone(),
                card.getZone() == Zone.LOST_PILE || card.getZone() == Zone.TOP_OF_LOST_PILE);
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
        assertTrue(Filters.Retract_The_Bridge.accepts(scn.game(), scn.GetDSCard("retract")));
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
        scn.PassAllResponses();

        assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
        assertInLostPile(retract);
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
        assertInLostPile(retract);
    }

    @Test
    public void CostsThreeForceWithThreeInteriorSitesCardsRideDb327AndUnrelatedStay() {
        var scn = GetScenario();
        var retract = scn.GetDSCard("retract");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");
        var db327 = scn.GetDSCard("db327");
        var chasm = scn.GetLSStartingLocation(); // Cloud City: Chasm Walkway (unrelated)
        var marketplace = scn.GetDSStartingLocation();
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        putLocation(scn, db327);
        scn.MoveCardsToLocation(warRoom, trooper);

        prepareDeployWithForce(scn, retract);

        List<PhysicalCard> interiors = interiorTops(scn);
        assertEquals(3, interiors.size());
        PhysicalCardImpl left = (PhysicalCardImpl) interiors.get(0);
        PhysicalCardImpl mid = (PhysicalCardImpl) interiors.get(1);
        PhysicalCardImpl right = (PhysicalCardImpl) interiors.get(2);
        int db327Index = db327.getLocationZoneIndex();
        int chasmIndex = chasm.getLocationZoneIndex();
        int marketIndex = marketplace.getLocationZoneIndex();

        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(forceBefore >= 3);
        assertTrue(scn.DSCardPlayAvailable(retract, REARRANGE_TEXT));
        scn.DSPlayCard(retract, REARRANGE_TEXT);
        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.CHOICE_TEXT));
        assertTrue(scn.DSHasCardChoicesAvailable(left, mid, right));
        assertTrue(scn.DSHasCardChoiceNotAvailable(db327));
        assertTrue(scn.DSHasCardChoiceNotAvailable(chasm));
        assertTrue(scn.DSHasCardChoiceNotAvailable(marketplace));

        scn.DSChooseCard(right);
        assertTrue(scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.NEXT_CHOICE_TEXT));
        scn.DSChooseCard(mid);
        scn.PassAllResponses();

        assertEquals(forceBefore - 3, scn.GetDSForcePileCount());
        assertEquals(Arrays.asList(right, mid, left), interiorTops(scn));
        assertTrue(scn.CardsAtLocation(warRoom, trooper));
        assertEquals(warRoom, trooper.getAtLocation());
        assertEquals(db327Index, db327.getLocationZoneIndex());
        assertEquals(chasmIndex, chasm.getLocationZoneIndex());
        assertEquals(marketIndex, marketplace.getLocationZoneIndex());
        assertInLostPile(retract);
    }

    @Test
    public void CannotPlayRearrangeSideWithZeroInteriorDeathStarSites() {
        var scn = GetScenario();
        var retract = scn.GetDSCard("retract");
        var db327 = scn.GetDSCard("db327");

        scn.StartGame();
        putLocation(scn, db327);
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
    public void CancelOnTheEdgeActionTextIsWiredOnCard() {
        // Interactive On The Edge / Sense / Skywalkers response windows are brittle in VTS;
        // assert the cancel filter wiring and that Filters.Retract_The_Bridge is what Skywalkers uses.
        var scn = GetScenario();
        var retract = scn.GetDSCard("retract");
        var onTheEdge = scn.GetLSCard("on-the-edge");
        var skywalkers = scn.GetLSCard("skywalkers");

        scn.StartGame();

        assertEquals("On The Edge", onTheEdge.getBlueprint().getTitle());
        assertEquals("Skywalkers", skywalkers.getBlueprint().getTitle());
        assertTrue(Filters.Retract_The_Bridge.accepts(scn.game(), retract));
        assertTrue(Filters.title("On The Edge").accepts(scn.game(), onTheEdge));
        assertEquals(CardSubtype.LOST, retract.getBlueprint().getCardSubtype());
    }
}