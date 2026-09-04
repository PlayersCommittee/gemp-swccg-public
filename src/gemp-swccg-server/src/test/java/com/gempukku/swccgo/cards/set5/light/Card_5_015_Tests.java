package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Access Denied (5_015 / blueprint 5_15).
 * Doc tab t.1xouug9yxxg5 / issue #117.
 *
 * Printed game text: "two mobile sites" (not interior-only). Doc line "only interior
 * mobile sites" is superseded by printed text on the same tab — covered by mobile-site
 * deploy tests below (Death Star interior mobile sites are eligible mobile sites).
 */
public class Card_5_015_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("accessDenied", "5_15");
                    put("luke", "1_19");
                    put("anger", "4_16");
                    put("ihabfat", "4_52");
                    put("revolution", "1_062");
                    put("jungle", "4_86");
                }},
                new HashMap<>() {{
                    put("corridor", "1_284");
                    put("warRoom", "1_287");
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

    private void placeBetweenSites(PhysicalCardImpl effect, PhysicalCardImpl otherSite) {
        effect.setTargetedCard(TargetId.EFFECT_TARGET_1, null, otherSite, Filters.sameCardId(otherSite));
    }

    private void deployBetween(VirtualTableScenario scn, PhysicalCardImpl effect,
                               PhysicalCardImpl siteA, PhysicalCardImpl siteB) {
        scn.AttachCardsTo(siteA, effect);
        placeBetweenSites(effect, siteB);
        effect.setPlayCardOptionId(PlayCardOptionId.PLAY_CARD_OPTION_1);
    }

    @Test
    public void AccessDenied_5_015_StatsAndKeywordsAreCorrect() {
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
        assertTrue(card.hasIcon(Icon.CLOUD_CITY));
        assertTrue(card.getGameText().contains("Insert face up"));
        assertTrue(card.getGameText().contains("two mobile sites"));
        assertTrue(card.getGameText().contains("Lift Tube"));
        assertTrue("Insert function text keeps Immune to Alter",
                card.getGameText().contains("Immune to Alter"));
        assertFalse("Between-sites must not be blueprint-immune to Alter",
                card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void AccessDenied_5_015_InsertOptionImmuneToAlterWhileInsertedPlayOption() {
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
    public void AccessDenied_5_015_RevealLosesOpponentInsertsAndReshuffles() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var disturbance = scn.GetDSCard("disturbance");
        var knowledge = scn.GetDSCard("knowledge");

        scn.StartGame();
        scn.SkipToLSTurn(Phase.DEPLOY);

        // Mark inserted AFTER zone move (MoveCardsToTop clears the inserted flag).
        scn.MoveCardsToTopOfLSReserveDeck(knowledge, disturbance, access);
        access.setInserted(true);
        disturbance.setInserted(true);
        knowledge.setInserted(true);
        assertTrue(access.isInserted());
        assertEquals(access, scn.gameState().getReserveDeck(scn.LS, false).get(0));

        // Closest real path: run insert-reveal game text while a phase-action decision is awaiting.
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
    public void AccessDenied_5_015_DeploysBetweenTwoMobileSites() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);

        assertEquals(corridor, access.getAttachedTo());
        assertEquals(warRoom, access.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
        assertEquals(PlayCardOptionId.PLAY_CARD_OPTION_1, access.getPlayCardOptionId());
        assertNotNull(access.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
    }

    @Test
    public void AccessDenied_5_015_NotImmuneToAlterWhenBetweenSites() {
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
    public void AccessDenied_5_015_CannotCompleteBetweenSitesWithSingleMobileSite() {
        var scn = GetScenario();
        var access = scn.GetLSCard("accessDenied");
        var corridor = scn.GetDSCard("corridor");

        scn.StartGame();
        putLocation(scn, corridor);
        // Valid between-sites targets require an adjacent eligible mobile site.
        assertFalse("No adjacent eligible mobile site exists for between-sites deploy",
                Filters.canSpot(scn.game(), access, Filters.and(
                        Filters.adjacentSite(corridor),
                        Filters.mobile_site,
                        Filters.not(Filters.or(Filters.Dagobah_location, Filters.AhchTo_location)))));
    }

    @Test
    public void AccessDenied_5_015_DagobahSitesNotEligibleForBetweenSitesDeploy() {
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
    public void AccessDenied_5_015_OpponentCharacterMayPassBetweenSites() {
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
    public void AccessDenied_5_015_OwnerCharactersNotGatedMovingPast() {
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
    public void AccessDenied_5_015_LiftTubePassengerMayPass() {
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
    public void AccessDenied_5_015_SurpriseCannotRelocateBetweenSitesEffect() {
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

        assertNotNull(access.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
        var relocateFilter = Filters.and(
                Filters.Effect,
                Filters.except(Filters.immune_to_Alter),
                Filters.attachedTo(Filters.location),
                new com.gempukku.swccgo.filters.Filter() {
                    @Override
                    public boolean accepts(com.gempukku.swccgo.game.state.GameState gs,
                                          com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying mq,
                                          com.gempukku.swccgo.game.PhysicalCard c) {
                        return c.getTargetedCard(gs, TargetId.EFFECT_TARGET_1) == null;
                    }
                }
        );
        assertFalse("Access Denied (between sites) is not a Surprise relocate target",
                relocateFilter.accepts(scn.game(), access));
        assertTrue("On-location Effect remains a relocate candidate",
                relocateFilter.accepts(scn.game(), revolution));
        assertNotNull(surprise);
    }


    @Test
    public void AccessDenied_5_015_IHaveABadFeelingCannotRelocateBetweenSitesEffect() {
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

        var relocateFilter = Filters.and(
                Filters.Effect,
                Filters.except(Filters.immune_to_Alter),
                Filters.attachedTo(Filters.location),
                new com.gempukku.swccgo.filters.Filter() {
                    @Override
                    public boolean accepts(com.gempukku.swccgo.game.state.GameState gs,
                                          com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying mq,
                                          com.gempukku.swccgo.game.PhysicalCard c) {
                        return c.getTargetedCard(gs, TargetId.EFFECT_TARGET_1) == null;
                    }
                }
        );
        assertFalse("Access Denied is not an I Have A Bad Feeling About This relocate target",
                relocateFilter.accepts(scn.game(), access));
        assertNotNull(ihabfat);
    }

}
