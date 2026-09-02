package com.gempukku.swccgo.rules.layout;

import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.layout.LocationGroup;
import com.gempukku.swccgo.game.layout.RearrangeSites;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
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
                    put("incinerator", "5_170"); // Cloud City: Incinerator (Dark)
                    put("cloud-car", "5_178"); // Cloud Car
                    put("escort", "1_166"); // Colonel Wullf Yularen
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
     * Attaches an Effect to a site. Access Denied / Restricted Access / Laser Gate
     * are not implemented, so tests use Expand The Empire (deploys on a site) to
     * show attached cards stay with the site. The engine rejects orders that
     * would place a card whose game text contains "between two" at either end
     * of the group, without naming Laser Gate.
     */
    private void attachEffectToSite(VirtualTableScenario scn, PhysicalCardImpl effect, PhysicalCardImpl site) {
        scn.AttachCardsTo(site, effect);
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
        scn.BoardAsPassenger(cloudCar, pilot);

        assertTrue(scn.CardsAtLocation(corridor, cloudCar));
        assertTrue(scn.IsAboardAsPassenger(cloudCar, pilot));

        List<PhysicalCard> newOrder = reversed(interiorTops(scn, Title.Death_Star));
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));

        assertTrue(scn.CardsAtLocation(corridor, cloudCar));
        assertTrue(scn.IsAboardAsPassenger(cloudCar, pilot));
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

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        // Middle site so the attached Effect stays between two interiors after a reverse.
        attachEffectToSite(scn, expand, warRoom);

        assertTrue(scn.IsAttachedTo(warRoom, expand));

        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        List<PhysicalCard> newOrder = reversed(interiors);
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));

        assertTrue(scn.IsAttachedTo(warRoom, expand));
        assertEquals(newOrder, interiorTops(scn, Title.Death_Star));
        assertEquals(warRoom, newOrder.get(1));
        assertTrue(scn.IsAdjacentTo(warRoom, (PhysicalCardImpl) newOrder.get(0)));
        assertTrue(scn.IsAdjacentTo(warRoom, (PhysicalCardImpl) newOrder.get(2)));
    }

    @Test
    public void ZeroTargetsIsNoOp() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);

        List<PhysicalCard> before = new ArrayList<PhysicalCard>(locationRow(scn));
        List<PhysicalCard> interiorsBefore = interiorTops(scn, Title.Death_Star);

        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, Collections.emptyList()));
        assertTrue(scn.gameState().reorderTopLocationsInGroup(RearrangeSites.interiorSitesOfSystem(Title.Death_Star), Collections.emptyList()));

        assertEquals(before, locationRow(scn));
        assertEquals(interiorsBefore, interiorTops(scn, Title.Death_Star));
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
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        putAtSite(scn, corridor, trooper);
        scn.ApplyAdHocModifier(new MayNotMoveModifier(trooper));

        assertTrue(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), trooper));
        assertEquals(corridor, trooper.getAtLocation());
        int corridorId = corridor.getCardId();

        List<PhysicalCard> newOrder = reversed(interiorTops(scn, Title.Death_Star));
        assertTrue(RearrangeSites.rearrangeInteriorSites(scn.game(), Title.Death_Star, newOrder));

        // Same site card, same occupant. This is not a move or a deploy.
        assertEquals(corridorId, corridor.getCardId());
        assertEquals(corridor, trooper.getAtLocation());
        assertTrue(scn.CardsAtLocation(corridor, trooper));
        assertEquals(Zone.LOCATIONS, corridor.getZone());
        assertTrue(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), trooper));
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
}