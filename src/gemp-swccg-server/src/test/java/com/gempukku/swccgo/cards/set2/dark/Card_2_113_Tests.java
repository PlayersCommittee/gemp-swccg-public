package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.LocationPlacementDirection;
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
import com.gempukku.swccgo.game.layout.LocationPlacement;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Laser Gate (2_113).
 */
public class Card_2_113_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("trooper", "1_28");
                    put("blaster", "1_152");
                    put("lift", "1_148");
                }},
                new HashMap<>() {{
                    put("laserGate", "2_113");
                    put("laserGate2", "2_113");
                    put("corridor", "1_284");
                    put("warRoom", "1_287");
                    put("conference", "2_144");
                    put("vader", "1_168");
                    put("stormie", "1_194");
                    put("dsLift", "1_308");
                    put("speeder", "8_169");
                    put("dsBlaster", "1_317");
                    put("bewil", "5_092");
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

    private void deployGateBetween(VirtualTableScenario scn, PhysicalCardImpl gate,
                                   PhysicalCardImpl siteA, PhysicalCardImpl siteB) {
        scn.PlaceBetweenSites(siteA, siteB, gate);
    }

    private List<PhysicalCard> visualRow(VirtualTableScenario scn) {
        return scn.gameState().getVisualRowCardsInOrder();
    }

    private LocationPlacement placementRelative(VirtualTableScenario scn, PhysicalCardImpl loc,
                                                PhysicalCard other, LocationPlacementDirection direction) {
        for (LocationPlacement cand : scn.gameState().getLocationPlacement(scn.game(), loc, null, null)) {
            if (cand.getOtherCard() != null && cand.getOtherCard().getCardId() == other.getCardId()
                    && ((direction.isRightOf() && cand.getDirection().isRightOf())
                    || (direction.isLeftOf() && cand.getDirection().isLeftOf()))) {
                return new LocationPlacement(cand.getParentSystem(), cand.getParentStarshipOrVehiclePersona(),
                        cand.getParentStarshipOrVehicleCard(), cand.getOtherCard(), direction);
            }
        }
        return null;
    }

    @Test
    public void LaserGateStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("laserGate").getBlueprint();

        assertEquals(Title.Laser_Gate, card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        assertEquals(ExpansionSet.A_NEW_HOPE, card.getExpansionSet());
        assertEquals(Rarity.U2, card.getRarity());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_SITE);
        }});
        assertTrue(card.getGameText().contains("interior mobile sites"));
        assertTrue(card.getGameText().contains("defense value = 3"));
    }

    @Test
    public void LaserGateDeploysBetweenTwoInteriorMobileSites() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);

        scn.MoveCardsToHand(gate);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.DSCardPlayAvailable(gate));
        scn.DSPlayCard(gate);
        scn.DSChooseCard(corridor);
        scn.DSChooseCard(warRoom);
        scn.PassAllResponses();

        assertEquals(Zone.BETWEEN_SITES, gate.getZone());
        PhysicalCard left = scn.gameState().getBetweenSiteLeft(gate);
        PhysicalCard right = scn.gameState().getBetweenSiteRight(gate);
        assertTrue((left == corridor && right == warRoom) || (left == warRoom && right == corridor));
        List<PhysicalCard> visual = visualRow(scn);
        int gateIdx = visual.indexOf(gate);
        assertTrue(gateIdx > visual.indexOf(left));
        assertTrue(gateIdx < visual.indexOf(right));
        assertEquals(1, scn.game().getModifiersQuerying().getDistanceBetweenSites(scn.gameState(), corridor, warRoom).intValue());
    }

    @Test
    public void LaserGateCannotDeployWithoutAdjacentInteriorMobilePair() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        var corridor = scn.GetDSCard("corridor");

        scn.StartGame();
        putLocation(scn, corridor);

        scn.MoveCardsToHand(gate);
        scn.SkipToPhase(Phase.DEPLOY);

        assertFalse("Laser Gate should not be playable with a single interior mobile site",
                scn.DSCardPlayAvailable(gate));
    }

    @Test
    public void LaserGateBlocksWeakCharactersAndNonLiftTubeVehicles() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var stormie = scn.GetDSCard("stormie");
        var speeder = scn.GetDSCard("speeder");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployGateBetween(scn, gate, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, stormie, speeder);

        scn.SkipToPhase(Phase.MOVE);

        assertFalse("Weak character (power+ability <= 4) blocked by Laser Gate",
                scn.DSMoveAvailable(stormie));
        assertTrue(scn.CardsAtLocation(corridor, stormie));

        assertFalse("Non-Lift-Tube vehicle cannot move past Laser Gate",
                scn.DSMoveAvailable(speeder));
        assertTrue(scn.CardsAtLocation(corridor, speeder));
        assertFalse(scn.CardsAtLocation(warRoom, speeder));
    }

    @Test
    public void LaserGateAllowsStrongCharacters() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployGateBetween(scn, gate, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, vader);

        scn.SkipToPhase(Phase.MOVE);

        assertTrue("Vader (power+ability > 4) may move past Laser Gate", scn.DSMoveAvailable(vader));
        scn.DSMoveCard(vader, warRoom);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(warRoom, vader));
    }

    @Test
    public void LaserGateLiftTubeMayPassToFarSite() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var dsLift = scn.GetDSCard("dsLift");
        var stormie = scn.GetDSCard("stormie");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployGateBetween(scn, gate, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, dsLift, stormie);
        scn.BoardAsPassenger(dsLift, stormie);

        scn.SkipToPhase(Phase.MOVE);

        assertTrue("Lift Tube (with passenger) may move past Laser Gate", scn.DSMoveAvailable(dsLift));
        scn.DSMoveCard(dsLift, warRoom);
        scn.PassAllResponses();
        assertTrue("Lift Tube must arrive at far bounding site", scn.CardsAtLocation(warRoom, dsLift));
        assertTrue("Passenger remains aboard Lift Tube at far site",
                scn.IsAboardAsPassenger(dsLift, stormie));
    }

    @Test
    public void LaserGateBlocksVehicleUsingLandspeedAcrossTwoSites() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var conference = scn.GetDSCard("conference");
        var speeder = scn.GetDSCard("speeder");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        deployGateBetween(scn, gate, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, speeder);

        scn.SkipToPhase(Phase.MOVE);

        assertFalse("Landspeed past Laser Gate is still blocked for other vehicles",
                scn.DSMoveAvailable(speeder));
        assertTrue(scn.CardsAtLocation(corridor, speeder));
        assertEquals(java.util.Arrays.asList(gate), scn.gameState().getBetweenSiteCardsCrossed(corridor, conference));
    }

    @Test
    public void LaserGateDefenseValueIs3() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployGateBetween(scn, gate, corridor, warRoom);

        assertEquals(3, scn.GetDefense(gate));
    }

    @Test
    public void LaserGateCharacterWeaponMayTargetFromAttachedBoundingSite() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployGateBetween(scn, gate, corridor, warRoom);

        scn.MoveCardsToLocation(corridor, luke, vader);
        scn.AttachCardsTo(luke, blaster);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(corridor));
        scn.LSInitiateBattle(corridor);
        scn.PassBattleStartResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());

        assertTrue("Character weapon grant for Laser Gate",
                scn.game().getModifiersQuerying().grantedMayBeTargetedBy(scn.gameState(), gate, blaster));
        assertTrue("As-if-present grant for either-site targeting",
                scn.game().getModifiersQuerying().canBeTargetedByWeaponsAsIfPresent(scn.gameState(), gate));

        scn.LSUseCardAction(blaster);
        assertTrue("Battle at site A: Laser Gate must be a legal weapon target",
                scn.LSHasCardChoiceAvailable(gate));
        scn.LSChooseCard(gate);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
    }

    @Test
    public void LaserGateCharacterWeaponMayTargetFromFarBoundingSite() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var conference = scn.GetDSCard("conference");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var stormie = scn.GetDSCard("stormie");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        deployGateBetween(scn, gate, corridor, warRoom);

        scn.MoveCardsToLocation(warRoom, luke, stormie);
        scn.AttachCardsTo(luke, blaster);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(warRoom));
        scn.LSInitiateBattle(warRoom);
        scn.PassBattleStartResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());

        assertTrue("Character weapon grant for Laser Gate",
                scn.game().getModifiersQuerying().grantedMayBeTargetedBy(scn.gameState(), gate, blaster));
        assertTrue("As-if-present grant for either-site targeting",
                scn.game().getModifiersQuerying().canBeTargetedByWeaponsAsIfPresent(scn.gameState(), gate));
        assertTrue("Between-sites filter includes far bounding site",
                Filters.betweenSitesIncluding(warRoom).accepts(scn.game(), gate));

        scn.LSUseCardAction(blaster);
        assertTrue("Battle at far site B: Laser Gate must be a legal weapon target",
                scn.LSHasCardChoiceAvailable(gate));
        assertNotNull(conference);
        assertEquals(Zone.BETWEEN_SITES, gate.getZone());
    }

    @Test
    public void LaserGateTwoCopiesSitAsSeparateColumnsAndBothBlock() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        var gate2 = scn.GetDSCard("laserGate2");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var stormie = scn.GetDSCard("stormie");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployGateBetween(scn, gate, corridor, warRoom);
        deployGateBetween(scn, gate2, corridor, warRoom);

        List<PhysicalCard> visual = visualRow(scn);
        int leftIdx = visual.indexOf(corridor);
        int rightIdx = visual.indexOf(warRoom);
        if (leftIdx > rightIdx) {
            int tmp = leftIdx;
            leftIdx = rightIdx;
            rightIdx = tmp;
        }
        assertEquals(leftIdx + 3, rightIdx);
        assertTrue(visual.subList(leftIdx + 1, rightIdx).contains(gate));
        assertTrue(visual.subList(leftIdx + 1, rightIdx).contains(gate2));
        assertEquals(1, scn.game().getModifiersQuerying().getDistanceBetweenSites(scn.gameState(), corridor, warRoom).intValue());

        scn.MoveCardsToLocation(corridor, stormie);
        scn.SkipToPhase(Phase.MOVE);
        assertFalse(scn.DSMoveAvailable(stormie));
    }

    @Test
    public void LaserGateNewSiteRightOfLeftKeepsGateOnTheRight() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var conference = scn.GetDSCard("conference");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployGateBetween(scn, gate, corridor, warRoom);

        PhysicalCard left = scn.gameState().getBetweenSiteLeft(gate);
        PhysicalCard right = scn.gameState().getBetweenSiteRight(gate);
        LocationPlacement placement = placementRelative(scn, conference, (PhysicalCardImpl) left, LocationPlacementDirection.RIGHT_OF);
        assertNotNull(placement);
        scn.MoveLocationToTable(conference, placement);

        assertEquals(conference, scn.gameState().getBetweenSiteLeft(gate));
        assertEquals(right, scn.gameState().getBetweenSiteRight(gate));
        List<PhysicalCard> visual = visualRow(scn);
        assertTrue(visual.indexOf(left) < visual.indexOf(conference));
        assertTrue(visual.indexOf(conference) < visual.indexOf(gate));
        assertTrue(visual.indexOf(gate) < visual.indexOf(right));
        assertEquals(Zone.BETWEEN_SITES, gate.getZone());
    }

    @Test
    public void LaserGateMatchesLaserGateFilter() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("laserGate");
        assertTrue(Filters.Laser_Gate.accepts(scn.game(), gate));
        assertTrue(Filters.or(Filters.Laser_Gate, Filters.Heart_Of_The_Chasm, Filters.Rite_Of_Passage).accepts(scn.game(), gate));
    }
}
