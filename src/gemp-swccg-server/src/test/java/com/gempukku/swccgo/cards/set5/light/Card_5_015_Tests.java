package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Access Denied (5_015 / blueprint 5_15).
 * Doc tab t.1xouug9yxxg5 / issue #117.
 */
public class Card_5_015_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("accessDenied", "5_15");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("corridor", "1_284");
                    put("warRoom", "1_287");
                    put("vader", "1_168");
                    put("stormie", "1_194");
                    put("dsLift", "1_308");
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
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
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
}
