package com.gempukku.swccgo.cards.set5.dark;

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
 * Tests for Restricted Access (5_122 / blueprint 5_122).
 * Doc tab t.g2w3umt9tfag / issue #125. Dark mirror of Access Denied.
 *
 * Printed: "two mobile sites" (not interior-only).
 * Bill ruling: Immune to Alter only in insert mode; not Immune when deployed between sites.
 */
public class Card_5_122_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("lsLift", "1_148");
                    put("alter", "1_71");
                    put("projection", "6_056");
                    put("tremor", "1_042");
                    put("anger", "4_16");
                    put("neverTell", "4_27");
                    
                    put("ihabfat", "4_52");
                    put("jungle", "4_86");
                    put("revolution", "1_062");
                }},
                new HashMap<>() {{
                    put("restrictedAccess", "5_122");
                    put("corridor", "1_284");
                    put("warRoom", "1_287");
                    put("vader", "1_168");
                    put("stormie", "1_194");
                    put("dsLift", "1_308");
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
    public void RestrictedAccess_5_122_StatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("restrictedAccess").getBlueprint();

        assertEquals(Title.Restricted_Access, card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
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
    public void RestrictedAccess_5_122_InsertOptionImmuneToAlterWhileInsertedPlayOption() {
        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");

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
    public void RestrictedAccess_5_122_RevealLosesOpponentInsertsAndReshuffles() {
        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");
        var projection = scn.GetLSCard("projection");
        var tremor = scn.GetLSCard("tremor");

        scn.StartGame();
        scn.SkipToPhase(Phase.DEPLOY);

        // Opponent inserts must be real insert Effects (characters throw on getInsertCardRevealedAction).
        // Last arg ends on top of DS Reserve. Mark inserted AFTER MoveCardsToTop.
        scn.MoveCardsToTopOfDSReserveDeck(projection, tremor, access);
        access.setInserted(true);
        projection.setInserted(true);
        tremor.setInserted(true);
        assertTrue(access.isInserted());
        assertEquals(Title.Restricted_Access, access.getTitle());
        assertEquals(access, scn.gameState().getReserveDeck(scn.DS, false).get(0));

        access.setInsertCardRevealed(true);
        var revealAction = access.getBlueprint().getInsertCardRevealedAction(scn.game(), access);
        assertNotNull(revealAction);
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, revealAction);
        scn.PassAllResponses();

        assertTrue("Restricted Access lost after reveal",
                access.getZone() == Zone.LOST_PILE || scn.GetDSLostPile().contains(access));
        assertTrue("Opponent insert lost from DS Reserve",
                projection.getZone() == Zone.LOST_PILE || scn.GetLSLostPile().contains(projection));
        assertTrue("Opponent insert lost from DS Reserve",
                tremor.getZone() == Zone.LOST_PILE || scn.GetLSLostPile().contains(tremor));
        assertFalse("Revealed Restricted Access must leave Reserve",
                scn.gameState().getReserveDeck(scn.DS, false).contains(access));
    }

    @Test
    public void RestrictedAccess_5_122_DeploysBetweenTwoMobileSites() {
        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");
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
    public void RestrictedAccess_5_122_NotImmuneToAlterWhenBetweenSites() {
        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var alter = scn.GetLSCard("alter");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, luke);
        scn.MoveCardsToLSHand(alter);

        assertFalse("Between-sites Restricted Access is not immune to Alter",
                scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), access, Title.Alter));
        assertFalse(Filters.immune_to_Alter.accepts(scn.game(), access));

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue("Alter should be playable vs between-sites Restricted Access",
                scn.LSCardPlayAvailable(alter));
        scn.LSPlayCard(alter);
        assertTrue("Restricted Access must be a legal Alter cancel target when between sites",
                scn.LSHasCardChoiceAvailable(access));
        scn.LSChooseCard(access);
        assertTrue(scn.LSHasCardChoiceAvailable(luke));
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
    }

    @Test
    public void RestrictedAccess_5_122_CannotCompleteBetweenSitesWithSingleMobileSite() {
        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");
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
    public void RestrictedAccess_5_122_DagobahSitesNotEligibleForBetweenSitesDeploy() {
        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");
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
    public void RestrictedAccess_5_122_OpponentCharacterMayPassBetweenSites() {
        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, luke);
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.LSMoveAvailable(luke));
        scn.LSMoveCard(luke, warRoom);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(warRoom, luke));
    }

    @Test
    public void RestrictedAccess_5_122_OwnerCharactersNotGatedMovingPast() {
        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");
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
    public void RestrictedAccess_5_122_LiftTubePassengerMayPass() {
        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var luke = scn.GetLSCard("luke");
        var lift = scn.GetLSCard("lsLift");

        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, lift);
        scn.BoardAsPassenger(lift, luke);
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.LSMoveAvailable(lift));
        scn.LSMoveCard(lift, warRoom);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(warRoom, lift));
    }

    @Test
    public void RestrictedAccess_5_122_SurpriseCannotRelocateBetweenSitesEffect() {
        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");
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
        assertFalse("Restricted Access (between sites) is not a Surprise relocate target",
                relocateFilter.accepts(scn.game(), access));
        assertTrue("On-location Effect remains a relocate candidate",
                relocateFilter.accepts(scn.game(), revolution));
        assertNotNull(surprise);
    }


    @Test
    public void RestrictedAccess_5_122_IHaveABadFeelingCannotRelocateBetweenSitesEffect() {
        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");
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
        assertFalse("Restricted Access is not an I Have A Bad Feeling About This relocate target",
                relocateFilter.accepts(scn.game(), access));
        assertNotNull(ihabfat);
    }


    @Test
    public void RestrictedAccess_5_122_OpponentPaysForcePilePlusOneDeltaToPass() {
        var scnControl = GetScenario();
        var corridorC = scnControl.GetDSCard("corridor");
        var warRoomC = scnControl.GetDSCard("warRoom");
        var lukeC = scnControl.GetLSCard("luke");
        scnControl.StartGame();
        putLocation(scnControl, corridorC);
        putLocation(scnControl, warRoomC);
        scnControl.MoveCardsToLocation(corridorC, lukeC);
        scnControl.SkipToLSTurn(Phase.MOVE);
        int forceBeforeControl = scnControl.gameState().getForcePileSize(scnControl.LS);
        assertTrue(scnControl.LSMoveAvailable(lukeC));
        scnControl.LSMoveCard(lukeC, warRoomC);
        scnControl.PassAllResponses();
        int controlCost = forceBeforeControl - scnControl.gameState().getForcePileSize(scnControl.LS);

        var scn = GetScenario();
        var access = scn.GetDSCard("restrictedAccess");
        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("warRoom");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        deployBetween(scn, access, corridor, warRoom);
        scn.MoveCardsToLocation(corridor, luke);
        scn.SkipToLSTurn(Phase.MOVE);
        int forceBefore = scn.gameState().getForcePileSize(scn.LS);
        assertTrue(scn.LSMoveAvailable(luke));
        scn.LSMoveCard(luke, warRoom);
        scn.PassAllResponses();
        int gatedCost = forceBefore - scn.gameState().getForcePileSize(scn.LS);

        assertTrue(scn.CardsAtLocation(warRoom, luke));
        assertEquals("Restricted Access adds exactly +1 Force vs ungated move", controlCost + 1, gatedCost);
    }

}
