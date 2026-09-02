package com.gempukku.swccgo.rules.layout;

import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.layout.LocationGroup;
import com.gempukku.swccgo.game.layout.RearrangeSites;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.effects.RearrangeRelatedSitesEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the shared rearrange-sites helper. Later cards can call this; those
 * cards are not implemented here.
 */
public class RearrangeSitesTests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("trash", "1_125"); // Death Star: Trash Compactor
                    put("ls-db327", "1_124"); // Death Star: Docking Bay 327 (Light)
                    put("trench", "2_062"); // Death Star: Trench
                    put("ls-incinerator", "5_081"); // Cloud City: Incinerator (Light)
                    put("guest", "5_080"); // Cloud City: Guest Quarters
                    put("captive", "1_005"); // C-3PO
                }},
                new HashMap<>()
                {{
                    put("core", "1_283"); // Death Star: Central Core (Dark)
                    put("corridor", "1_284"); // Death Star: Detention Block Corridor
                    put("db327", "1_285"); // Death Star: Docking Bay 327 (Dark)
                    put("war-room", "1_287"); // Death Star: War Room
                    put("conference", "2_144"); // Death Star: Conference Room
                    put("ds-system", "2_143"); // Death Star system
                    put("expand", "1_215"); // Expand The Empire (deploys on a site)
                    put("presence", "1_227"); // Presence Of The Force (deploys on a site)
                    put("incinerator", "5_170"); // Cloud City: Incinerator (Dark)
                    put("cloud-car", "5_178"); // Cloud Car
                    put("escort", "1_166"); // Colonel Wullf Yularen
                    put("droid", "1_163"); // LIN-V8K (Mining Droid)
                    put("bolt", "1_205"); // Restraining Bolt
                    put("hallway", "14_112"); // Naboo: Theed Palace Hallway (Dark)
                    put("generator", "13_076"); // Naboo: Theed Palace Generator (Dark)
                    put("generator-core", "13_077"); // Naboo: Theed Palace Generator Core (Dark)
                    put("throne", "12_174"); // Naboo: Theed Palace Throne Room (Dark)
                    put("courtyard", "12_172"); // Naboo: Theed Palace Courtyard (Dark)
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

    /** Puts a location onto the table in the first legal layout slot. */
    private void putLocation(VirtualTableScenario scn, PhysicalCardImpl location) {
        scn.MoveLocationToTable(location);
    }

    /** Puts a character (or other card) at a site without paying deploy cost. */
    private void putAtSite(VirtualTableScenario scn, PhysicalCardImpl site, PhysicalCardImpl... cards) {
        scn.MoveCardsToLocation(site, cards);
    }

    /**
     * Attaches an Effect to a site. Access Denied and Restricted Access are not
     * implemented as cards, so tests use other "deploys on a site" Effects and
     * point EFFECT_TARGET_1 at the neighboring site. That is the same attach
     * plus target shape those between-sites cards use. Laser Gate is also
     * unimplemented and is not named in engine code.
     */
    private void attachEffectToSite(VirtualTableScenario scn, PhysicalCardImpl effect, PhysicalCardImpl site) {
        scn.AttachCardsTo(site, effect);
    }

    /**
     * Marks an attached Effect as sitting between two sites: it stays attached
     * to one site and targets the other.
     */
    private void placeBetweenSites(PhysicalCardImpl effect, PhysicalCardImpl otherSite) {
        effect.setTargetedCard(TargetId.EFFECT_TARGET_1, null, otherSite, Filters.sameCardId(otherSite));
    }

    /** Left-to-right top locations currently on the table. */
    private List<PhysicalCard> locationRow(VirtualTableScenario scn) {
        return scn.gameState().getLocationsInOrder();
    }

    /** Current interior-only tops for a system, left to right. */
    private List<PhysicalCard> interiorTops(VirtualTableScenario scn, String systemName) {
        LocationGroup group = scn.gameState().getLocationsLayout().findGroupForSystemMatching(
                scn.game(), systemName, RearrangeSites.interiorSitesOfSystem(systemName));
        assertNotNull(group);
        return new ArrayList<PhysicalCard>(group.getTopCardsInGroup());
    }

    /** Death Star top locations in table order (sites, docking bay, system, trench). */
    private List<PhysicalCard> deathStarRow(VirtualTableScenario scn) {
        List<PhysicalCard> result = new ArrayList<PhysicalCard>();
        for (PhysicalCard loc : locationRow(scn)) {
            if (Title.Death_Star.equals(loc.getPartOfSystem()) || Title.Death_Star.equals(loc.getTitle())) {
                result.add(loc);
            }
        }
        return result;
    }

    /** Copy of a list in reverse left-to-right order. */
    private List<PhysicalCard> reversed(List<PhysicalCard> list) {
        List<PhysicalCard> copy = new ArrayList<PhysicalCard>(list);
        Collections.reverse(copy);
        return copy;
    }

    /** After a rearrange, each top location's zone index matches left-to-right table order. */
    private void assertIndexesMatchRow(VirtualTableScenario scn) {
        List<PhysicalCard> row = locationRow(scn);
        for (int i = 0; i < row.size(); ++i) {
            assertEquals(i, row.get(i).getLocationZoneIndex());
            for (PhysicalCard converted : scn.gameState().getConvertedLocationsUnderTopLocation(row.get(i))) {
                assertEquals(i, converted.getLocationZoneIndex());
            }
        }
    }

    @Test
    public void ThreeInteriorDeathStarSitesReorderAndCharactersRide() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        putAtSite(scn, warRoom, trooper);

        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        assertEquals(3, interiors.size());
        assertTrue(scn.CardsAtLocation(warRoom, trooper));

        List<PhysicalCard> newOrder = reversed(interiors);
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));

        assertEquals(newOrder, interiorTops(scn, Title.Death_Star));
        assertTrue(scn.CardsAtLocation(warRoom, trooper));
        assertEquals(warRoom, trooper.getAtLocation());
        assertIndexesMatchRow(scn);
    }

    @Test
    public void MixedSideThreeInteriorDeathStarSitesReorder() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var trash = scn.GetLSCard("trash");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, trash);
        putAtSite(scn, trash, trooper);

        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        assertEquals(3, interiors.size());
        assertTrue(interiors.contains(corridor));
        assertTrue(interiors.contains(warRoom));
        assertTrue(interiors.contains(trash));

        List<PhysicalCard> newOrder = reversed(interiors);
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));

        assertEquals(newOrder, interiorTops(scn, Title.Death_Star));
        assertTrue(scn.CardsAtLocation(trash, trooper));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void ConvertedStackStaysUnderTheSameTop() {
        var scn = GetScenario();

        var chasm = scn.GetLSStartingLocation();
        var guest = scn.GetLSCard("guest");
        var incinerator = scn.GetDSCard("incinerator");
        var lsIncinerator = scn.GetLSCard("ls-incinerator");

        scn.StartGame();

        putLocation(scn, guest);
        putLocation(scn, incinerator);
        putLocation(scn, lsIncinerator);

        assertEquals(lsIncinerator, scn.gameState().getLocationAtTopOfConvertedLocation(incinerator));
        assertEquals(Arrays.asList(incinerator), scn.gameState().getConvertedLocationsUnderTopLocation(lsIncinerator));
        assertEquals(Zone.CONVERTED_LOCATIONS, incinerator.getZone());

        List<PhysicalCard> interiors = interiorTops(scn, Title.Bespin);
        assertTrue(interiors.contains(chasm));
        assertTrue(interiors.contains(guest));
        assertTrue(interiors.contains(lsIncinerator));
        assertFalse(interiors.contains(incinerator));

        List<PhysicalCard> newOrder = reversed(interiors);
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Bespin, newOrder));

        assertEquals(newOrder, interiorTops(scn, Title.Bespin));
        assertEquals(lsIncinerator, scn.gameState().getLocationAtTopOfConvertedLocation(incinerator));
        assertEquals(Arrays.asList(incinerator), scn.gameState().getConvertedLocationsUnderTopLocation(lsIncinerator));
        assertEquals(lsIncinerator.getLocationZoneIndex(), incinerator.getLocationZoneIndex());
        assertIndexesMatchRow(scn);
    }

    @Test
    public void VehicleAndPilotStayEmbarked() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");
        var cloudCar = scn.GetDSCard("cloud-car");
        var pilot = scn.GetDSFiller(1);

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        putAtSite(scn, corridor, cloudCar);
        scn.BoardAsPilot(cloudCar, pilot);

        assertTrue(scn.CardsAtLocation(corridor, cloudCar));
        assertTrue(scn.IsAboardAsPilot(cloudCar, pilot));

        List<PhysicalCard> newOrder = reversed(interiorTops(scn, Title.Death_Star));
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));

        assertTrue(scn.CardsAtLocation(corridor, cloudCar));
        assertTrue(scn.IsAboardAsPilot(cloudCar, pilot));
        assertEquals(corridor, cloudCar.getAtLocation());
        assertIndexesMatchRow(scn);
    }

    @Test
    public void AccessDeniedRestrictedAccessStayBetweenSites() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");
        var expand = scn.GetDSCard("expand");
        var presence = scn.GetDSCard("presence");

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);

        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        assertEquals(3, interiors.size());
        PhysicalCardImpl left = (PhysicalCardImpl) interiors.get(0);
        PhysicalCardImpl mid = (PhysicalCardImpl) interiors.get(1);
        PhysicalCardImpl right = (PhysicalCardImpl) interiors.get(2);

        // Access Denied stand-in: between the current left and middle sites.
        attachEffectToSite(scn, expand, left);
        placeBetweenSites(expand, mid);
        // Restricted Access stand-in: between the current middle and right sites.
        attachEffectToSite(scn, presence, mid);
        placeBetweenSites(presence, right);

        assertTrue(scn.IsAttachedTo(left, expand));
        assertEquals(mid, expand.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
        assertTrue(scn.IsAttachedTo(mid, presence));
        assertEquals(right, presence.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));

        List<PhysicalCard> newOrder = reversed(interiors);
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));

        // Reverse puts the old left site at the right end. The Access Denied
        // stand-in reattaches to the left-er of its pair (old mid) instead of
        // riding to the end.
        assertTrue(scn.IsAttachedTo(mid, expand));
        assertEquals(left, expand.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
        // Restricted Access stand-in reattaches to the old right site, now leftmost.
        assertTrue(scn.IsAttachedTo(right, presence));
        assertEquals(mid, presence.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));

        List<PhysicalCard> after = interiorTops(scn, Title.Death_Star);
        assertEquals(newOrder, after);
        assertEquals(right, after.get(0));
        assertEquals(mid, after.get(1));
        assertEquals(left, after.get(2));
        assertFalse(scn.IsAttachedTo(left, expand));
        assertFalse(scn.IsAttachedTo(left, presence));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void ZeroTargetsIsNoOpAndCannotInitiate() {
        var scn = GetScenario();

        scn.StartGame();

        List<PhysicalCard> beforeEmpty = new ArrayList<PhysicalCard>(locationRow(scn));
        assertFalse(RearrangeSites.canRearrangeInteriorSites(scn.game(), Title.Death_Star));
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, Collections.emptyList()));
        assertTrue(RearrangeSites.rearrangeInteriorSitesByPermutation(scn.game(), Title.Death_Star, Collections.emptyList()));
        assertEquals(beforeEmpty, locationRow(scn));

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        putLocation(scn, corridor);
        putLocation(scn, warRoom);

        assertTrue(RearrangeSites.canRearrangeInteriorSites(scn.game(), Title.Death_Star));
        List<PhysicalCard> before = new ArrayList<PhysicalCard>(locationRow(scn));
        List<PhysicalCard> interiorsBefore = interiorTops(scn, Title.Death_Star);

        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, Collections.emptyList()));
        assertTrue(scn.gameState().reorderTopLocationsInGroup(RearrangeSites.interiorSitesOfSystem(Title.Death_Star), Collections.emptyList()));

        assertEquals(before, locationRow(scn));
        assertEquals(interiorsBefore, interiorTops(scn, Title.Death_Star));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void SameConfigurationIsAllowed() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);

        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, interiors));
        assertEquals(interiors, interiorTops(scn, Title.Death_Star));
        assertTrue(RearrangeSites.rearrangeInteriorSitesByPermutation(scn.game(), Title.Death_Star, Arrays.asList(0, 1, 2)));
        assertEquals(interiors, interiorTops(scn, Title.Death_Star));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void IndexPermutationReordersInteriorSites() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);

        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        assertEquals(3, interiors.size());
        List<PhysicalCard> expected = Arrays.asList(interiors.get(2), interiors.get(0), interiors.get(1));

        assertTrue(RearrangeSites.rearrangeInteriorSitesByPermutation(scn.game(), Title.Death_Star, Arrays.asList(2, 0, 1)));

        assertEquals(expected, interiorTops(scn, Title.Death_Star));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void CaptiveStaysEscorted() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");
        var escort = scn.GetDSCard("escort");
        var captive = scn.GetLSCard("captive");

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        putAtSite(scn, corridor, escort, captive);
        scn.CaptureCardWith(escort, captive);

        assertTrue(captive.isCaptive());
        assertEquals(escort, captive.getAttachedTo());
        assertTrue(scn.CardsAtLocation(corridor, escort));

        List<PhysicalCard> newOrder = reversed(interiorTops(scn, Title.Death_Star));
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));

        assertTrue(captive.isCaptive());
        assertEquals(escort, captive.getAttachedTo());
        assertTrue(scn.CardsAtLocation(corridor, escort));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void RearrangeIsNotMovementOrDeploymentAndCannotMoveUnaffected() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");
        var droid = scn.GetDSCard("droid");
        var bolt = scn.GetDSCard("bolt");

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        putAtSite(scn, corridor, droid);
        scn.AttachCardsTo(droid, bolt);

        assertEquals(corridor, droid.getAtLocation());
        assertTrue(scn.IsAttachedTo(droid, bolt));
        int corridorId = corridor.getCardId();
        int droidId = droid.getCardId();

        List<PhysicalCard> newOrder = reversed(interiorTops(scn, Title.Death_Star));
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));

        // Same site card, same occupant, same bolt. This is not a move or a deploy.
        assertEquals(corridorId, corridor.getCardId());
        assertEquals(droidId, droid.getCardId());
        assertEquals(corridor, droid.getAtLocation());
        assertTrue(scn.CardsAtLocation(corridor, droid));
        assertTrue(scn.IsAttachedTo(droid, bolt));
        assertEquals(Zone.LOCATIONS, corridor.getZone());
        assertTrue(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), droid));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void TrenchSystemAndUnrelatedSitesAreUnchanged() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var db327 = scn.GetDSCard("db327");
        var dsSystem = scn.GetDSCard("ds-system");
        var trench = scn.GetLSCard("trench");
        var marketplace = scn.GetDSStartingLocation();
        var chasm = scn.GetLSStartingLocation();

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, db327);
        putLocation(scn, dsSystem);
        putLocation(scn, trench);

        int marketplaceIndex = marketplace.getLocationZoneIndex();
        int chasmIndex = chasm.getLocationZoneIndex();
        int db327Index = db327.getLocationZoneIndex();
        int systemIndex = dsSystem.getLocationZoneIndex();
        int trenchIndex = trench.getLocationZoneIndex();
        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        List<PhysicalCard> newOrder = reversed(interiors);

        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));

        assertEquals(newOrder, interiorTops(scn, Title.Death_Star));
        List<PhysicalCard> interiorsAfter = interiorTops(scn, Title.Death_Star);
        assertFalse(interiorsAfter.contains(db327));
        assertFalse(interiorsAfter.contains(dsSystem));
        assertFalse(interiorsAfter.contains(trench));
        assertFalse(interiorsAfter.contains(marketplace));
        assertFalse(interiorsAfter.contains(chasm));
        assertEquals(Zone.LOCATIONS, trench.getZone());
        assertEquals(Zone.LOCATIONS, dsSystem.getZone());
        assertEquals(Zone.LOCATIONS, marketplace.getZone());
        assertEquals(Zone.LOCATIONS, chasm.getZone());
        assertEquals(db327Index, db327.getLocationZoneIndex());
        assertEquals(systemIndex, dsSystem.getLocationZoneIndex());
        assertEquals(trenchIndex, trench.getLocationZoneIndex());
        assertEquals(marketplaceIndex, marketplace.getLocationZoneIndex());
        assertEquals(chasmIndex, chasm.getLocationZoneIndex());
        assertIndexesMatchRow(scn);
    }

    @Test
    public void SameHelperReordersBespinInteriorSites() {
        var scn = GetScenario();

        var chasm = scn.GetLSStartingLocation();
        var guest = scn.GetLSCard("guest");
        var incinerator = scn.GetDSCard("incinerator");
        var trooper = scn.GetDSFiller(2);

        scn.StartGame();

        putLocation(scn, guest);
        putLocation(scn, incinerator);
        putAtSite(scn, guest, trooper);

        List<PhysicalCard> interiors = interiorTops(scn, Title.Bespin);
        assertTrue(interiors.contains(chasm));
        assertTrue(interiors.contains(guest));
        assertTrue(interiors.contains(incinerator));
        assertTrue(scn.CardsAtLocation(guest, trooper));

        List<PhysicalCard> newOrder = reversed(interiors);
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Bespin, newOrder));

        assertEquals(newOrder, interiorTops(scn, Title.Bespin));
        assertTrue(scn.CardsAtLocation(guest, trooper));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void InvalidOrdersThatBreakLayoutRulesAreRejected() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var db327 = scn.GetDSCard("db327");
        var marketplace = scn.GetDSStartingLocation();
        var chasm = scn.GetLSStartingLocation();

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, db327);

        List<PhysicalCard> before = new ArrayList<PhysicalCard>(deathStarRow(scn));
        List<PhysicalCard> interiorsBefore = interiorTops(scn, Title.Death_Star);

        // Docking Bay 327 is not an interior-only site, so it cannot be shuffled into that group.
        assertFalse(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star,
                Arrays.asList(db327, corridor, warRoom)));
        assertEquals(before, deathStarRow(scn));
        assertEquals(interiorsBefore, interiorTops(scn, Title.Death_Star));

        // Unrelated systems cannot be mixed into the Death Star interior group.
        assertFalse(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star,
                Arrays.asList(warRoom, marketplace)));
        assertEquals(before, deathStarRow(scn));

        // Same-group helper also rejects mixing Tatooine / Bespin / Death Star.
        assertFalse(RearrangeSites.rearrange(scn.game(), Arrays.asList(corridor, chasm)));
        assertEquals(before, deathStarRow(scn));

        // Duplicates are rejected.
        assertFalse(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star,
                Arrays.asList(corridor, corridor)));
        assertEquals(before, deathStarRow(scn));
    }

    @Test
    public void SameHelperReordersTheedPalaceInteriorSites() {
        var scn = GetScenario();

        var hallway = scn.GetDSCard("hallway");
        var generator = scn.GetDSCard("generator");
        var generatorCore = scn.GetDSCard("generator-core");
        var trooper = scn.GetDSFiller(1);
        var marketplace = scn.GetDSStartingLocation();
        var chasm = scn.GetLSStartingLocation();

        scn.StartGame();

        putLocation(scn, hallway);
        putLocation(scn, generator);
        putLocation(scn, generatorCore);
        putAtSite(scn, hallway, trooper);

        List<PhysicalCard> interiors = interiorTops(scn, Title.Naboo);
        assertEquals(3, interiors.size());
        assertTrue(interiors.contains(hallway));
        assertTrue(interiors.contains(generator));
        assertTrue(interiors.contains(generatorCore));
        assertFalse(interiors.contains(marketplace));
        assertFalse(interiors.contains(chasm));
        assertTrue(scn.CardsAtLocation(hallway, trooper));

        List<PhysicalCard> newOrder = reversed(interiors);
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Naboo, newOrder));

        assertEquals(newOrder, interiorTops(scn, Title.Naboo));
        assertTrue(scn.CardsAtLocation(hallway, trooper));
        assertEquals(hallway, trooper.getAtLocation());
        assertIndexesMatchRow(scn);
    }
    @Test
    public void InvalidPermutationIsRejected() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);

        List<PhysicalCard> before = interiorTops(scn, Title.Death_Star);
        assertEquals(3, before.size());

        // Wrong length is not a permutation of the current interior stacks.
        assertFalse(RearrangeSites.rearrangeInteriorSitesByPermutation(scn.game(), Title.Death_Star,
                Arrays.asList(0, 1)));
        // Duplicates are rejected.
        assertFalse(RearrangeSites.rearrangeInteriorSitesByPermutation(scn.game(), Title.Death_Star,
                Arrays.asList(0, 0, 1)));
        // Out-of-range indexes are rejected.
        assertFalse(RearrangeSites.rearrangeInteriorSitesByPermutation(scn.game(), Title.Death_Star,
                Arrays.asList(0, 1, 3)));

        assertEquals(before, interiorTops(scn, Title.Death_Star));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void PartialOrderOnlySwapsTheOccupiedSlots() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);

        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        assertEquals(3, interiors.size());
        PhysicalCard left = interiors.get(0);
        PhysicalCard mid = interiors.get(1);
        PhysicalCard right = interiors.get(2);

        // Asking only for the two ends swaps those stacks; the middle site stays.
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star,
                Arrays.asList(right, left)));

        List<PhysicalCard> after = interiorTops(scn, Title.Death_Star);
        assertEquals(Arrays.asList(right, mid, left), after);
        assertIndexesMatchRow(scn);
    }

    @Test
    public void TheedThroneRoomAndCourtyardStayOutOfInteriorRow() {
        var scn = GetScenario();

        var throne = scn.GetDSCard("throne");
        var hallway = scn.GetDSCard("hallway");
        var generator = scn.GetDSCard("generator");
        var courtyard = scn.GetDSCard("courtyard");

        scn.StartGame();

        putLocation(scn, throne);
        putLocation(scn, hallway);
        putLocation(scn, generator);
        putLocation(scn, courtyard);

        List<PhysicalCard> interiors = interiorTops(scn, Title.Naboo);
        assertTrue(interiors.contains(hallway));
        assertTrue(interiors.contains(generator));
        assertFalse(interiors.contains(throne));
        assertFalse(interiors.contains(courtyard));

        int throneIndex = throne.getLocationZoneIndex();
        int courtyardIndex = courtyard.getLocationZoneIndex();
        List<PhysicalCard> newOrder = reversed(interiors);
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Naboo, newOrder));

        assertEquals(newOrder, interiorTops(scn, Title.Naboo));
        assertFalse(interiorTops(scn, Title.Naboo).contains(throne));
        assertFalse(interiorTops(scn, Title.Naboo).contains(courtyard));
        assertEquals(throneIndex, throne.getLocationZoneIndex());
        assertEquals(courtyardIndex, courtyard.getLocationZoneIndex());
        assertIndexesMatchRow(scn);
    }

    @Test
    public void RearrangeRelatedSitesEffectReordersInteriorSites() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);

        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        List<PhysicalCard> newOrder = reversed(interiors);
        var action = new SystemQueueAction();
        new RearrangeRelatedSitesEffect(action, RearrangeSites.interiorSitesOfSystem(Title.Death_Star), newOrder)
                .playEffect(scn.game());

        assertEquals(newOrder, interiorTops(scn, Title.Death_Star));
        assertIndexesMatchRow(scn);

        List<PhysicalCard> expected = Arrays.asList(newOrder.get(2), newOrder.get(0), newOrder.get(1));
        new RearrangeRelatedSitesEffect(new SystemQueueAction(), Title.Death_Star,
                RearrangeSites.interiorSitesOfSystem(Title.Death_Star), Arrays.asList(2, 0, 1))
                .playEffect(scn.game());
        assertEquals(expected, interiorTops(scn, Title.Death_Star));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void CentralCoreParticipatesAndConvertedDockingBayStaysOut() {
        var scn = GetScenario();
        var corridor = scn.GetDSCard("corridor");
        var core = scn.GetDSCard("core");
        var warRoom = scn.GetDSCard("war-room");
        var db327 = scn.GetDSCard("db327");
        var lsDb327 = scn.GetLSCard("ls-db327");
        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, core);
        putLocation(scn, warRoom);
        putLocation(scn, db327);
        putLocation(scn, lsDb327);
        assertEquals(lsDb327, scn.gameState().getLocationAtTopOfConvertedLocation(db327));
        assertEquals(Arrays.asList(db327), scn.gameState().getConvertedLocationsUnderTopLocation(lsDb327));
        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        assertTrue(interiors.contains(corridor));
        assertTrue(interiors.contains(core));
        assertTrue(interiors.contains(warRoom));
        assertFalse(interiors.contains(db327));
        assertFalse(interiors.contains(lsDb327));
        int dbIndex = lsDb327.getLocationZoneIndex();
        List<PhysicalCard> newOrder = reversed(interiors);
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));
        assertEquals(newOrder, interiorTops(scn, Title.Death_Star));
        assertFalse(interiorTops(scn, Title.Death_Star).contains(lsDb327));
        assertEquals(lsDb327, scn.gameState().getLocationAtTopOfConvertedLocation(db327));
        assertEquals(Arrays.asList(db327), scn.gameState().getConvertedLocationsUnderTopLocation(lsDb327));
        assertEquals(dbIndex, lsDb327.getLocationZoneIndex());
        assertEquals(lsDb327.getLocationZoneIndex(), db327.getLocationZoneIndex());
        assertIndexesMatchRow(scn);
    }

    @Test
    public void AdjacencyUpdatesAfterRotation() {
        var scn = GetScenario();
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");
        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        assertEquals(3, interiors.size());
        PhysicalCard a = interiors.get(0);
        PhysicalCard b = interiors.get(1);
        PhysicalCard c = interiors.get(2);
        assertTrue(Filters.adjacentSite(a).accepts(scn.game(), b));
        assertTrue(Filters.adjacentSite(b).accepts(scn.game(), c));
        assertFalse(Filters.adjacentSite(a).accepts(scn.game(), c));
        assertTrue(RearrangeSites.rearrangeInteriorSitesByPermutation(scn.game(), Title.Death_Star, Arrays.asList(1, 2, 0)));
        assertEquals(Arrays.asList(b, c, a), interiorTops(scn, Title.Death_Star));
        assertTrue(Filters.adjacentSite(b).accepts(scn.game(), c));
        assertTrue(Filters.adjacentSite(c).accepts(scn.game(), a));
        assertFalse(Filters.adjacentSite(a).accepts(scn.game(), b));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void OneInteriorSiteCanInitiateAndKeepsOrder() {
        var scn = GetScenario();
        var corridor = scn.GetDSCard("corridor");
        scn.StartGame();
        putLocation(scn, corridor);
        assertTrue(RearrangeSites.canRearrangeInteriorSites(scn.game(), Title.Death_Star));
        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        assertEquals(1, interiors.size());
        assertEquals(corridor, interiors.get(0));
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, interiors));
        assertTrue(RearrangeSites.rearrangeInteriorSitesByPermutation(scn.game(), Title.Death_Star, Arrays.asList(0)));
        assertEquals(interiors, interiorTops(scn, Title.Death_Star));
        assertIndexesMatchRow(scn);
    }

    @Test
    public void SiteAttachedEffectThatIsNotBetweenSitesStaysPut() {
        var scn = GetScenario();
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");
        var expand = scn.GetDSCard("expand");
        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        attachEffectToSite(scn, expand, warRoom);
        assertTrue(scn.IsAttachedTo(warRoom, expand));
        assertNull(expand.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
        List<PhysicalCard> newOrder = reversed(interiorTops(scn, Title.Death_Star));
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));
        assertTrue(scn.IsAttachedTo(warRoom, expand));
        assertEquals(newOrder, interiorTops(scn, Title.Death_Star));
        assertIndexesMatchRow(scn);
    }
}
