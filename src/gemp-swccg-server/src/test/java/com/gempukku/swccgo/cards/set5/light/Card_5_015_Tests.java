package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.LocationPlacementDirection;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Access Denied (5_015 / blueprint 5_15).
 */
public class Card_5_015_Tests {

    private static final String BETWEEN_SITES_TEXT = "Deploy between two mobile sites";

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("accessDenied", "5_15");
                    put("accessDenied2", "5_15");
                    put("luke", "1_19");
                    put("anger", "4_16");
                    put("ihabfat", "4_52");
                    put("revolution", "1_062");
                    put("jungle", "4_86");
                }},
                new HashMap<>() {{
                    put("corridor", "1_284");
                    put("warRoom", "1_287");
                    put("conference", "2_144");
                    put("deathStar", "2_143");
                    put("vader", "1_168");
                    put("stormie", "1_194");
                    put("dsLift", "1_308");
                    put("alter", "1_234");
                    put("disturbance", "1_208");
                    put("knowledge", "4_125");
                    put("surprise", "5_156");
                    put("cave", "4_158");
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

    private void deployBetween(VirtualTableScenario scn, PhysicalCardImpl effect,
                               PhysicalCardImpl siteA, PhysicalCardImpl siteB) {
        scn.PlaceBetweenSites(siteA, siteB, effect);
        effect.setPlayCardOptionId(PlayCardOptionId.PLAY_CARD_OPTION_1);
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
    public void AccessDeniedStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("accessDenied").getBlueprint();

        assertEquals(Title.Access_Denied, card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(ExpansionSet.CLOUD_CITY, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_SITE);
        }});
        assertTrue(card.getGameText().contains("Insert face up"));
        assertTrue(card.getGameText().contains("two mobile sites"));
        assertTrue(card.getGameText().contains("Lift Tube"));
        assertTrue("Insert function text keeps Immune to Alter",
                card.getGameText().contains("Immune to Alter"));
        assertFalse("Between-sites must not be blueprint-immune to Alter",
                card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void AccessDeniedInsertOptionImmuneToAlterWhileInsertedPlayOption() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");

        scn.StartGame();
        var alwaysOn = access.getBlueprint().getAlwaysOnModifiers(scn.game(), access);
        assertNotNull(alwaysOn);
        assertFalse(alwaysOn.isEmpty());
        assertTrue("AlwaysOn modifiers include Immune to Alter for insert option",
                alwaysOn.stream().anyMatch(m -> {
                    String text = m.getText(scn.gameState(), scn.game().getModifiersQuerying(), access);
                    return text != null && text.contains("Alter");
                }));
        assertTrue(access.getBlueprint().getGameText().contains("Immune to Alter"));
    }

    @Test
    public void AccessDeniedRevealLosesOpponentInsertsAndReshuffles() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var disturbance = scn.GetDSCard("disturbance");
        var knowledge = scn.GetDSCard("knowledge");

        scn.StartGame();
        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.MoveCardsToTopOfLSReserveDeck(knowledge, disturbance, access);
        access.setInserted(true);
        disturbance.setInserted(true);
        knowledge.setInserted(true);
        assertTrue(access.isInserted());
        assertEquals(access, scn.gameState().getReserveDeck(scn.LS, false).get(0));

        access.setInsertCardRevealed(true);
        var revealAction = access.getBlueprint().getInsertCardRevealedAction(scn.game(), access);
        assertNotNull(revealAction);
        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, revealAction);
        scn.PassAllResponses();

        assertTrue("Access Denied lost after reveal",
                access.getZone() == Zone.LOST_PILE || scn.GetLSLostPile().contains(access));
        assertTrue("Opponent insert A Disturbance In The Force lost from LS Reserve",
                disturbance.getZone() == Zone.LOST_PILE || scn.GetDSLostPile().contains(disturbance));
        assertTrue("Opponent insert Knowledge And Defense lost from LS Reserve",
                knowledge.getZone() == Zone.LOST_PILE || scn.GetDSLostPile().contains(knowledge));
        assertFalse("Revealed Access Denied must leave Reserve",
                scn.gameState().getReserveDeck(scn.LS, false).contains(access));
    }

    @Test
    public void AccessDeniedDeploysBetweenTwoMobileSites() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        scn.MoveCardsToLSHand(access);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(access, BETWEEN_SITES_TEXT));
        scn.LSPlayCard(access, BETWEEN_SITES_TEXT);
        scn.LSChooseCard(corridor);
        scn.LSChooseCard(warRoom);
        scn.PassAllResponses();

        assertEquals(Zone.BETWEEN_SITES, access.getZone());
        assertEquals(PlayCardOptionId.PLAY_CARD_OPTION_1, access.getPlayCardOptionId());
        PhysicalCard left = scn.gameState().getBetweenSiteLeft(access);
        PhysicalCard right = scn.gameState().getBetweenSiteRight(access);
        assertTrue((left == corridor && right == warRoom) || (left == warRoom && right == corridor));
        List<PhysicalCard> visual = visualRow(scn);
        assertTrue(visual.indexOf(access) > visual.indexOf(left));
        assertTrue(visual.indexOf(access) < visual.indexOf(right));
        assertEquals(1, scn.game().getModifiersQuerying().getDistanceBetweenSites(scn.gameState(), corridor, warRoom).intValue());
    }

    @Test
    public void AccessDeniedNotImmuneToAlterWhenBetweenSites() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var alter = scn.GetDSCard("alter");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, vader);
        scn.MoveCardsToDSHand(alter);

        assertFalse("Between-sites Access Denied is not immune to Alter",
                scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), access, Title.Alter));
        assertFalse(Filters.immune_to_Alter.accepts(scn.game(), access));

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue("Alter should be playable vs between-sites Access Denied",
                scn.DSCardPlayAvailable(alter));
        scn.DSPlayCard(alter);
        assertTrue("Access Denied must be a legal Alter cancel target when between sites",
                scn.DSHasCardChoiceAvailable(access));
        scn.DSChooseCard(access);
        assertTrue(scn.DSHasCardChoiceAvailable(vader));
        scn.DSChooseCard(vader);
        scn.PassAllResponses();
    }

    @Test
    public void AccessDeniedCannotCompleteBetweenSitesWithSingleMobileSite() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var corridor = scn.GetDSCard("corridor");

        scn.StartGame();
        putLocation(scn, corridor);
        scn.MoveCardsToLSHand(access);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse("No adjacent eligible mobile site exists for between-sites deploy",
                scn.LSCardPlayAvailable(access, BETWEEN_SITES_TEXT));
    }

    @Test
    public void AccessDeniedDagobahSitesNotEligibleForBetweenSitesDeploy() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var jungle = scn.GetLSCard("jungle");
        var cave = scn.GetDSCard("cave");

        scn.StartGame();
        putLocation(scn, jungle);
        putLocation(scn, cave);

        assertFalse("Dagobah Jungle is not an eligible between-sites mobile target",
                Filters.and(Filters.mobile_site,
                        Filters.not(Filters.or(Filters.Dagobah_location, Filters.AhchTo_location))).accepts(scn.game(), jungle));
        assertFalse("Dagobah Cave is not an eligible between-sites mobile target",
                Filters.and(Filters.mobile_site,
                        Filters.not(Filters.or(Filters.Dagobah_location, Filters.AhchTo_location))).accepts(scn.game(), cave));
        assertNotNull(access);
    }

    @Test
    public void AccessDeniedOpponentCharacterMayPassBetweenSites() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, vader);

        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.DSMoveAvailable(vader));
        scn.DSMoveCard(vader, warRoom);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(warRoom, vader));
    }

    @Test
    public void AccessDeniedOwnerCharactersNotGatedMovingPast() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var luke = scn.GetLSCard("luke");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, luke);

        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue("LS/owner characters move past Access Denied without +1 / Lift Tube gate",
                scn.LSMoveAvailable(luke));
        scn.LSMoveCard(luke, warRoom);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(warRoom, luke));
    }

    @Test
    public void AccessDeniedLiftTubePassengerMayPass() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var dsLift = scn.GetDSCard("dsLift");
        var stormie = scn.GetDSCard("stormie");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, dsLift, stormie);
        scn.BoardAsPassenger(dsLift, stormie);

        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.DSMoveAvailable(dsLift));
        scn.DSMoveCard(dsLift, warRoom);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(warRoom, dsLift));
        assertTrue(scn.IsAboardAsPassenger(dsLift, stormie));
    }

    @Test
    public void AccessDeniedSurpriseCannotRelocateBetweenSitesEffect() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var revolution = scn.GetLSCard("revolution");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var surprise = scn.GetDSCard("surprise");
        var starting = scn.GetLSStartingLocation();

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        scn.AttachCardsTo(starting, revolution);
        scn.MoveCardsToDSHand(surprise);

        assertEquals(Zone.BETWEEN_SITES, access.getZone());
        assertFalse("Between-sites Access Denied is not attached to a location",
                Filters.attachedTo(Filters.location).accepts(scn.game(), access));
        assertTrue("On-location Effect remains a relocate candidate",
                Filters.and(Filters.Effect, Filters.except(Filters.immune_to_Alter), Filters.attachedTo(Filters.location))
                        .accepts(scn.game(), revolution));
        assertNotNull(surprise);
    }

    @Test
    public void AccessDeniedIHaveABadFeelingCannotRelocateBetweenSitesEffect() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var ihabfat = scn.GetLSCard("ihabfat");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        scn.MoveCardsToLSHand(ihabfat);

        assertEquals(Zone.BETWEEN_SITES, access.getZone());
        assertFalse("Access Denied is not an I Have A Bad Feeling About This relocate target",
                Filters.attachedTo(Filters.location).accepts(scn.game(), access));
        assertNotNull(ihabfat);
    }

    @Test
    public void AccessDeniedOpponentPaysForcePilePlusOneDeltaToPass() {
        var scnControl = GetScenario();
        var corridorC = scnControl.GetDSCard("corridor");
        var warRoomC = scnControl.GetDSCard("warRoom");
        var vaderC = scnControl.GetDSCard("vader");
        scnControl.StartGame();
        putLocation(scnControl, corridorC);
        putLocation(scnControl, warRoomC);
        scnControl.MoveCardsToLocation(corridorC, vaderC);
        scnControl.SkipToPhase(Phase.MOVE);
        int forceBeforeControl = scnControl.gameState().getForcePileSize(scnControl.DS);
        assertTrue(scnControl.DSMoveAvailable(vaderC));
        scnControl.DSMoveCard(vaderC, warRoomC);
        scnControl.PassAllResponses();
        int controlCost = forceBeforeControl - scnControl.gameState().getForcePileSize(scnControl.DS);

        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var vader = scn.GetDSCard("vader");
        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, vader);
        scn.SkipToPhase(Phase.MOVE);
        int forceBefore = scn.gameState().getForcePileSize(scn.DS);
        assertTrue(scn.DSMoveAvailable(vader));
        scn.DSMoveCard(vader, warRoom);
        scn.PassAllResponses();
        int gatedCost = forceBefore - scn.gameState().getForcePileSize(scn.DS);

        assertTrue(scn.CardsAtLocation(warRoom, vader));
        assertEquals("Access Denied adds exactly +1 Force vs ungated move", controlCost + 1, gatedCost);
    }

    @Test
    public void AccessDeniedTwoCopiesOnTheSamePathAddOneForce() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var access2 = scn.GetLSCard("accessDenied2");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        deployBetween(scn, access2, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, vader);

        float gatedQuery = scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.gameState(), vader, corridor, warRoom, false, 0);

        var scnControl = GetScenario();
        var corridorC = scnControl.GetDSCard("corridor");
        var warRoomC = scnControl.GetDSCard("warRoom");
        var vaderC = scnControl.GetDSCard("vader");
        scnControl.StartGame();
        putLocation(scnControl, corridorC);
        putLocation(scnControl, warRoomC);
        scnControl.MoveCardsToLocation(corridorC, vaderC);
        float controlCost = scnControl.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scnControl.gameState(), vaderC, corridorC, warRoomC, false, 0);

        assertEquals("Two Access Denied copies add +1 Force once", controlCost + 1, gatedQuery, 0.01f);

        scn.SkipToPhase(Phase.MOVE);
        int forceBefore = scn.gameState().getForcePileSize(scn.DS);
        assertTrue(scn.DSMoveAvailable(vader));
        scn.DSMoveCard(vader, warRoom);
        scn.PassAllResponses();
        int gatedCost = forceBefore - scn.gameState().getForcePileSize(scn.DS);

        scnControl.SkipToPhase(Phase.MOVE);
        int forceBeforeC = scnControl.gameState().getForcePileSize(scnControl.DS);
        scnControl.DSMoveCard(vaderC, warRoomC);
        scnControl.PassAllResponses();
        int controlPaid = forceBeforeC - scnControl.gameState().getForcePileSize(scnControl.DS);
        assertEquals(controlPaid + 1, gatedCost);
        assertTrue(scn.CardsAtLocation(warRoom, vader));
    }

    @Test
    public void AccessDeniedTwoCopiesOnAThreeSitePathAddOneForce() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var access2 = scn.GetLSCard("accessDenied2");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var conference = scn.GetDSCard("conference");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);

        PhysicalCardImpl first = null;
        PhysicalCardImpl second = null;
        PhysicalCardImpl third = null;
        for (PhysicalCard card : visualRow(scn)) {
            if (card.getZone() != Zone.LOCATIONS) {
                continue;
            }
            if (card.getCardId() != corridor.getCardId()
                    && card.getCardId() != warRoom.getCardId()
                    && card.getCardId() != conference.getCardId()) {
                continue;
            }
            if (first == null) {
                first = (PhysicalCardImpl) card;
            } else if (second == null) {
                second = (PhysicalCardImpl) card;
            } else {
                third = (PhysicalCardImpl) card;
            }
        }
        assertNotNull(first);
        assertNotNull(second);
        assertNotNull(third);
        deployBetween(scn, access, first, second);
        deployBetween(scn, access2, second, third);
        scn.MoveCardsToLocation(first, vader);

        assertEquals(2, scn.gameState().getBetweenSiteCardsCrossed(first, third).size());

        var scnControl = GetScenario();
        var corridorC = scnControl.GetDSCard("corridor");
        var warRoomC = scnControl.GetDSCard("warRoom");
        var conferenceC = scnControl.GetDSCard("conference");
        var vaderC = scnControl.GetDSCard("vader");
        scnControl.StartGame();
        putLocation(scnControl, corridorC);
        putLocation(scnControl, warRoomC);
        putLocation(scnControl, conferenceC);
        PhysicalCardImpl firstC = null;
        PhysicalCardImpl thirdC = null;
        for (PhysicalCard card : visualRow(scnControl)) {
            if (card.getZone() != Zone.LOCATIONS) {
                continue;
            }
            if (card.getCardId() != corridorC.getCardId()
                    && card.getCardId() != warRoomC.getCardId()
                    && card.getCardId() != conferenceC.getCardId()) {
                continue;
            }
            if (firstC == null) {
                firstC = (PhysicalCardImpl) card;
            } else {
                thirdC = (PhysicalCardImpl) card;
            }
        }
        assertNotNull(firstC);
        assertNotNull(thirdC);
        scnControl.MoveCardsToLocation(firstC, vaderC);
        float controlCost = scnControl.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scnControl.gameState(), vaderC, firstC, thirdC, false, 0);
        float gatedCost = scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.gameState(), vader, first, third, false, 0);
        assertEquals("A-Access Denied-B-Access Denied-C adds +1 Force once",
                controlCost + 1, gatedCost, 0.01f);
    }

    @Test
    public void AccessDeniedLostWhenRelatedSystemIsBlownAway() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var deathStar = scn.GetDSCard("deathStar");

        scn.StartGame();
        putLocation(scn, deathStar);
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        assertEquals(Zone.BETWEEN_SITES, access.getZone());
        assertEquals(Zone.LOCATIONS, deathStar.getZone());
        assertTrue("Death Star interiors are related to the Death Star system",
                Filters.relatedLocation(deathStar).accepts(scn.game(), corridor));

        scn.SkipToPhase(Phase.CONTROL);
        String actor = scn.GetDecidingPlayer();
        scn.ExecuteAdHocEffect(actor, deathStar, new BlowAwayEffect(
                new TopLevelGameTextAction(deathStar, actor, deathStar.getCardId()), deathStar));
        for (int i = 0; i < 25; i++) {
            if (scn.GetLSLostPile().contains(access) && (corridor.getZone() == Zone.LOST_PILE
                    || corridor.getZone() == Zone.TOP_OF_LOST_PILE)) {
                break;
            }
            String text = scn.GetCurrentDecision().getText().toLowerCase();
            if (text.contains("action or pass")) {
                if (scn.GetLSLostPile().contains(access)) {
                    break;
                }
                throw new AssertionError("Back at phase actions before Access Denied left; access="
                        + access.getZone() + " corridor=" + corridor.getZone()
                        + " blown=" + deathStar.isBlownAway());
            }
            if (scn.AwaitingDSForceLossPayment()) {
                scn.DSPayRemainingForceLossFromReserveDeck();
                continue;
            }
            if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayRemainingForceLossFromReserveDeck();
                continue;
            }
            if (text.contains("put on lost pile") || text.contains("choose card")) {
                String player = scn.GetDecidingPlayer();
                List<String> ids = scn.DS.equals(player) ? scn.DSGetCardChoices() : scn.LSGetCardChoices();
                assertTrue("Lost-pile order has a card choice", ids != null && !ids.isEmpty());
                scn.PlayerDecided(player, ids.get(0));
                continue;
            }
            try {
                scn.PassResponses();
            } catch (RuntimeException e) {
                throw new AssertionError("Stuck on decision: " + text
                        + "; blown=" + deathStar.isBlownAway()
                        + "; corridor=" + corridor.getZone()
                        + "; access=" + access.getZone(), e);
            }
        }

        assertInZone(Zone.LOST_PILE, corridor);
        assertInZone(Zone.LOST_PILE, access);
        assertTrue(scn.GetLSLostPile().contains(access));
    }

    @Test
    public void AccessDeniedPassCostAppliesOnATwoSitePath() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var conference = scn.GetDSCard("conference");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        deployBetween(scn, access, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, vader);

        assertEquals(java.util.Arrays.asList(access), scn.gameState().getBetweenSiteCardsCrossed(corridor, conference));
        var scnControl = GetScenario();
        var corridorC = scnControl.GetDSCard("corridor");
        var conferenceC = scnControl.GetDSCard("conference");
        var warRoomC = scnControl.GetDSCard("warRoom");
        var vaderC = scnControl.GetDSCard("vader");
        scnControl.StartGame();
        putLocation(scnControl, corridorC);
        putLocation(scnControl, warRoomC);
        putLocation(scnControl, conferenceC);
        scnControl.MoveCardsToLocation(corridorC, vaderC);
        float controlCost = scnControl.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scnControl.gameState(), vaderC, corridorC, conferenceC, false, 0);
        float gatedCost = scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.gameState(), vader, corridor, conference, false, 0);
        assertEquals("Pass cost applies on a longer landspeed path, not only the adjacent pair",
                controlCost + 1, gatedCost, 0.01f);
    }

    @Test
    public void AccessDeniedNewSiteLeftOfRightKeepsSlotOnTheLeft() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var conference = scn.GetDSCard("conference");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);

        PhysicalCard left = scn.gameState().getBetweenSiteLeft(access);
        PhysicalCard right = scn.gameState().getBetweenSiteRight(access);
        LocationPlacement placement = placementRelative(scn, conference, (PhysicalCardImpl) right, LocationPlacementDirection.LEFT_OF);
        assertNotNull(placement);
        scn.MoveLocationToTable(conference, placement);

        assertEquals(left, scn.gameState().getBetweenSiteLeft(access));
        assertEquals(conference, scn.gameState().getBetweenSiteRight(access));
        List<PhysicalCard> visual = visualRow(scn);
        assertTrue(visual.indexOf(left) < visual.indexOf(access));
        assertTrue(visual.indexOf(access) < visual.indexOf(conference));
        assertTrue(visual.indexOf(conference) < visual.indexOf(right));
        assertEquals(Zone.BETWEEN_SITES, access.getZone());
    }
}
