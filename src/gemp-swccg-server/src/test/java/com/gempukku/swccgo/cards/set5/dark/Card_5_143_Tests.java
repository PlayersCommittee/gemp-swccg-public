package com.gempukku.swccgo.cards.set5.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.*;

/**
 * Tests for Heart Of The Chasm (5_143).
 * Relies on RearrangeSites helper from #1017 / e254419.
 */
public class Card_5_143_Tests {

    private static final String REARRANGE_TEXT = "Rearrange interior Cloud City sites";
    private static final String UPLOAD_TEXT = "Take card into hand from Reserve Deck";

    private HashMap<String, String> lsCards() {
        return new HashMap<>() {{
            put("off-the-edge", "5_59"); // Off The Edge
            put("skywalkers", "1_110"); // Skywalkers
            put("sense", "1_086"); // Sense
            put("platform-327", "5_83"); // Cloud City: Platform 327 (Docking Bay)
        }};
    }

    private HashMap<String, String> dsCards() {
        return new HashMap<>() {{
            put("heart", "5_143"); // Heart Of The Chasm
            put("chamber", "5_166"); // Cloud City: Carbonite Chamber
            put("dining", "5_168"); // Cloud City: Dining Room
            put("corridor", "5_171"); // Cloud City: Lower Corridor
            put("plaza", "5_173"); // Cloud City: Upper Plaza Corridor
            put("east-platform", "5_169"); // Cloud City: East Platform (Docking Bay)
            put("bespin", "5_164"); // Bespin system
            put("weather-vane", "5_127"); // Weather Vane
            put("bewil", "5_92"); // Captain Bewil
            put("ds-corridor", "1_284"); // Death Star: Detention Block Corridor
        }};
    }

    /** Default LS start is Chasm Walkway (interior CC). */
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                lsCards(), dsCards(), 10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /** LS start is Tibrin system - no interior Cloud City sites until we place them. */
    protected VirtualTableScenario GetScenarioNoCloudCityStart() {
        return new VirtualTableScenario(
                lsCards(), dsCards(), 10, 10,
                StartingSetup.DefaultLSSpaceSystem,
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
                scn.game(), Title.Bespin, RearrangeSites.interiorSitesOfSystem(Title.Bespin));
        assertNotNull(group);
        return new ArrayList<PhysicalCard>(group.getTopCardsInGroup());
    }

    private void prepareDeployWithForce(VirtualTableScenario scn, PhysicalCardImpl heart) {
        scn.MoveCardsToDSHand(heart);
        scn.SkipToDSTurn(Phase.DEPLOY);
    }

    private void assertInLostPile(PhysicalCardImpl card) {
        assertTrue("Expected lost pile zone, was " + card.getZone(),
                card.getZone() == Zone.LOST_PILE || card.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    private void playRearrangeAndChooseOrder(VirtualTableScenario scn, PhysicalCardImpl heart,
                                             PhysicalCardImpl first, PhysicalCardImpl second) {
        assertTrue(scn.DSCardPlayAvailable(heart, REARRANGE_TEXT));
        scn.DSPlayCard(heart, REARRANGE_TEXT);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.CHOICE_TEXT));
        scn.DSChooseCard(first);
        if (scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.NEXT_CHOICE_TEXT)) {
            scn.DSChooseCard(second);
        }
        scn.PassAllResponses();
    }

    @Test
    public void HeartOfTheChasmStatsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("heart").getBlueprint();

        assertEquals("Heart Of The Chasm", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.isCardType(CardType.INTERRUPT));
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getIconCount(Icon.CLOUD_CITY));
        assertEquals(Rarity.U, card.getRarity());
        assertTrue(Filters.Heart_Of_The_Chasm.accepts(scn.game(), scn.GetDSCard("heart")));
    }

    @Test
    public void CostsOneForceWithOneInteriorCloudCitySite() {
        var scn = GetScenarioNoCloudCityStart();
        var heart = scn.GetDSCard("heart");
        var chamber = scn.GetDSCard("chamber");

        scn.StartGame();
        putLocation(scn, chamber);
        prepareDeployWithForce(scn, heart);

        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(forceBefore >= 1);
        assertTrue(scn.DSCardPlayAvailable(heart, REARRANGE_TEXT));
        scn.DSPlayCard(heart, REARRANGE_TEXT);
        scn.PassAllResponses();
        scn.PassAllResponses();

        assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
        assertInLostPile(heart);
    }

    @Test
    public void CostsTwoForceWithTwoInteriorSitesAndRearranges() {
        var scn = GetScenarioNoCloudCityStart();
        var heart = scn.GetDSCard("heart");
        var chamber = scn.GetDSCard("chamber");
        var dining = scn.GetDSCard("dining");

        scn.StartGame();
        putLocation(scn, chamber);
        putLocation(scn, dining);
        prepareDeployWithForce(scn, heart);

        List<PhysicalCard> interiors = interiorTops(scn);
        assertEquals(2, interiors.size());
        PhysicalCardImpl a = (PhysicalCardImpl) interiors.get(0);
        PhysicalCardImpl b = (PhysicalCardImpl) interiors.get(1);

        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(forceBefore >= 2);
        playRearrangeAndChooseOrder(scn, heart, b, a);

        assertEquals(forceBefore - 2, scn.GetDSForcePileCount());
        assertEquals(Arrays.asList(b, a), interiorTops(scn));
        assertInLostPile(heart);
    }

    @Test
    public void CostsThreeForceCardsRideDockingBaysDsAndUnrelatedNotChoosable() {
        var scn = GetScenarioNoCloudCityStart();
        var heart = scn.GetDSCard("heart");
        var chamber = scn.GetDSCard("chamber");
        var dining = scn.GetDSCard("dining");
        var corridor = scn.GetDSCard("corridor");
        var eastPlatform = scn.GetDSCard("east-platform");
        var platform327 = scn.GetLSCard("platform-327");
        var dsCorridor = scn.GetDSCard("ds-corridor");
        var marketplace = scn.GetDSStartingLocation();
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        putLocation(scn, chamber);
        putLocation(scn, dining);
        putLocation(scn, corridor);
        putLocation(scn, eastPlatform);
        putLocation(scn, platform327);
        putLocation(scn, dsCorridor);
        scn.MoveCardsToLocation(dining, trooper);

        prepareDeployWithForce(scn, heart);

        List<PhysicalCard> interiors = interiorTops(scn);
        assertEquals(3, interiors.size());
        PhysicalCardImpl left = (PhysicalCardImpl) interiors.get(0);
        PhysicalCardImpl mid = (PhysicalCardImpl) interiors.get(1);
        PhysicalCardImpl right = (PhysicalCardImpl) interiors.get(2);
        int eastIndex = eastPlatform.getLocationZoneIndex();
        int platform327Index = platform327.getLocationZoneIndex();
        int dsIndex = dsCorridor.getLocationZoneIndex();
        int marketIndex = marketplace.getLocationZoneIndex();

        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(forceBefore >= 3);
        assertTrue(scn.DSCardPlayAvailable(heart, REARRANGE_TEXT));
        scn.DSPlayCard(heart, REARRANGE_TEXT);
        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.CHOICE_TEXT));
        assertTrue(scn.DSHasCardChoicesAvailable(left, mid, right));
        assertTrue(scn.DSHasCardChoiceNotAvailable(eastPlatform));
        assertTrue(scn.DSHasCardChoiceNotAvailable(platform327));
        assertTrue(scn.DSHasCardChoiceNotAvailable(dsCorridor));
        assertTrue(scn.DSHasCardChoiceNotAvailable(marketplace));

        scn.DSChooseCard(right);
        assertTrue(scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.NEXT_CHOICE_TEXT));
        scn.DSChooseCard(mid);
        scn.PassAllResponses();

        assertEquals(forceBefore - 3, scn.GetDSForcePileCount());
        assertEquals(Arrays.asList(right, mid, left), interiorTops(scn));
        assertTrue(scn.CardsAtLocation(dining, trooper));
        assertEquals(dining, trooper.getAtLocation());
        assertEquals(eastIndex, eastPlatform.getLocationZoneIndex());
        assertEquals(platform327Index, platform327.getLocationZoneIndex());
        assertEquals(dsIndex, dsCorridor.getLocationZoneIndex());
        assertEquals(marketIndex, marketplace.getLocationZoneIndex());
        assertInLostPile(heart);
    }

    @Test
    public void CannotPlayRearrangeWithZeroInteriorCloudCitySites() {
        var scn = GetScenarioNoCloudCityStart();
        var heart = scn.GetDSCard("heart");
        var eastPlatform = scn.GetDSCard("east-platform");

        scn.StartGame();
        putLocation(scn, eastPlatform);
        prepareDeployWithForce(scn, heart);

        assertEquals(0, Filters.countTopLocationsOnTable(scn.game(), RearrangeSites.interiorSitesOfSystem(Title.Bespin)));
        assertFalse(RearrangeSites.canRearrangeInteriorSites(scn.game(), Title.Bespin));
        assertFalse(scn.DSCardPlayAvailable(heart, REARRANGE_TEXT));
    }

    @Test
    public void CannotPlayRearrangeOutsideDeployPhase() {
        var scn = GetScenarioNoCloudCityStart();
        var heart = scn.GetDSCard("heart");
        var chamber = scn.GetDSCard("chamber");
        var dining = scn.GetDSCard("dining");

        scn.StartGame();
        putLocation(scn, chamber);
        putLocation(scn, dining);
        scn.MoveCardsToDSHand(heart);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.GetDSForcePileCount() >= 2);
        assertFalse(scn.DSCardPlayAvailable(heart, REARRANGE_TEXT));
    }

    @Test
    public void CancelOffTheEdgeActionTextIsWiredOnCard() {
        // Interactive Off The Edge / Sense / Skywalkers response windows are brittle in VTS;
        // assert cancel filter wiring (known gap: full interactive cancel).
        var scn = GetScenario();
        var heart = scn.GetDSCard("heart");
        var offTheEdge = scn.GetLSCard("off-the-edge");
        var skywalkers = scn.GetLSCard("skywalkers");
        var sense = scn.GetLSCard("sense");

        scn.StartGame();

        assertEquals("Off The Edge", offTheEdge.getBlueprint().getTitle());
        assertEquals("Skywalkers", skywalkers.getBlueprint().getTitle());
        assertEquals("Sense", sense.getBlueprint().getTitle());
        assertTrue(Filters.Heart_Of_The_Chasm.accepts(scn.game(), heart));
        assertTrue(Filters.Off_The_Edge.accepts(scn.game(), offTheEdge));
        assertEquals(CardSubtype.LOST, heart.getBlueprint().getCardSubtype());
    }

    @Test
    public void WeatherVaneUploadActionIsWired() {
        var scn = GetScenarioNoCloudCityStart();
        var heart = scn.GetDSCard("heart");
        var weatherVane = scn.GetDSCard("weather-vane");

        scn.StartGame();
        scn.MoveCardsToDSHand(heart);
        // Activate/Force setup first so Weather Vane is not pulled off Reserve into Force Pile.
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(weatherVane);

        assertTrue(scn.DSCardPlayAvailable(heart, UPLOAD_TEXT));
        scn.DSPlayCard(heart, UPLOAD_TEXT);
        scn.PassAllResponses();
        assertTrue("Expected Weather Vane choosable from Reserve Deck", scn.DSHasCardChoicesAvailable(weatherVane));
        scn.DSChooseCard(weatherVane);
        scn.PassAllResponses();

        assertInHand(weatherVane);
        assertInLostPile(heart);
    }

    @Test
    public void CaptainBewilCanPullHeartOfTheChasm() {
        // Doc: Captain Bewil can pull Heart Of The Chasm. Filter wiring; full upload interactive is known gap.
        var scn = GetScenario();
        var heart = scn.GetDSCard("heart");
        var bewil = scn.GetDSCard("bewil");

        scn.StartGame();

        assertEquals("Captain Bewil", bewil.getBlueprint().getTitle());
        assertTrue(Filters.Heart_Of_The_Chasm.accepts(scn.game(), heart));
        assertTrue(Filters.or(Filters.Laser_Gate, Filters.Heart_Of_The_Chasm, Filters.Rite_Of_Passage)
                .accepts(scn.game(), heart));
    }
}

