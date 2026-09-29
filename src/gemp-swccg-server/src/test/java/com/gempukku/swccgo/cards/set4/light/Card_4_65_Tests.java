package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_4_65_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("squeeze", "4_65");
                    put("ywing", "1_147");
                    put("kessel", "1_126");
                    put("asteroid", "4_081");
                }},
                new HashMap<>() {{
                    put("tie1", "1_304");
                    put("tie2", "1_304");
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

    protected VirtualTableScenario GetTrenchBattleScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("squeeze", "4_65");
                    put("ywing", "1_147");
                    put("deathstar", "7_117");
                    put("trench", "2_62");
                }},
                new HashMap<>() {{
                    put("tie1", "1_304");
                    put("tie2", "1_304");
                }},
                10,
                10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetAttackRunScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("squeeze", "4_65");
                    put("red2", "2_70");
                    put("biggs", "1_3");
                    put("torpedoes", "1_158");
                    put("deathstar", "7_117");
                    put("trench", "2_62");
                    put("attackrun", "2_42");
                }},
                new HashMap<>() {{
                    put("tie1", "1_304");
                    put("tie2", "1_304");
                }},
                10,
                10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void TightSqueezeStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("squeeze").getBlueprint();

        assertEquals("Tight Squeeze", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.DAGOBAH);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>() {{
        }});
        assertEquals(ExpansionSet.DAGOBAH, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void TightSqueezeNotPlayableWithOnlyOneOpposingStarfighter() {
        var scn = GetScenario();

        var squeeze = scn.GetLSCard("squeeze");
        var ywing = scn.GetLSCard("ywing");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");
        var tie1 = scn.GetDSCard("tie1");

        scn.StartGame();
        scn.MoveCardsToLSHand(squeeze);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);
        scn.MoveCardsToLocation(asteroid, ywing, tie1);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(asteroid);
        scn.PassAllResponses();

        assertFalse(scn.LSCardPlayAvailable(squeeze));
    }

    @Test
    public void TightSqueezePlayableWithLoneStarfighterAndTwoOpposingInAsteroidBattle() {
        var scn = GetScenario();

        var squeeze = scn.GetLSCard("squeeze");
        var ywing = scn.GetLSCard("ywing");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");
        var tie1 = scn.GetDSCard("tie1");
        var tie2 = scn.GetDSCard("tie2");

        scn.StartGame();
        scn.MoveCardsToLSHand(squeeze);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(asteroid, ywing, tie1, tie2);
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(asteroid);
        scn.PassAllResponses();

        assertTrue(scn.LSCardPlayAvailable(squeeze));
        int forceBefore = scn.GetLSForcePileCount();
        scn.LSPlayCard(squeeze);
        scn.PassAllResponses();

        assertEquals(forceBefore - 1, scn.GetLSForcePileCount());
        assertEquals(Zone.TOP_OF_LOST_PILE, squeeze.getZone());
    }

    @Test
    public void TightSqueezeAtEndOfBattleOpponentForfeitsTwoParticipatingStarfighters() {
        var scn = GetTrenchBattleScenario();

        var squeeze = scn.GetLSCard("squeeze");
        var ywing = scn.GetLSCard("ywing");
        var deathstar = scn.GetLSCard("deathstar");
        var trench = scn.GetLSCard("trench");
        var tie1 = scn.GetDSCard("tie1");
        var tie2 = scn.GetDSCard("tie2");

        scn.StartGame();
        scn.MoveCardsToLSHand(squeeze);
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(trench);
        scn.MoveCardsToLocation(trench, ywing, tie1, tie2);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(trench);
        scn.PassAllResponses();

        assertTrue(scn.LSCardPlayAvailable(squeeze));
        scn.LSPlayCard(squeeze);
        scn.PassAllResponses();

        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(false);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        if (scn.AwaitingDSDamageSegmentActions() || scn.AwaitingLSDamageSegmentActions()) {
            scn.PassDamageSegmentActions();
        }

        // Exactly two legal targets often auto-selects; otherwise choose both TIEs
        if (scn.DSDecisionAvailable("Choose vehicle or starfighter to forfeit")) {
            assertTrue(scn.DSHasCardChoiceAvailable(tie1));
            assertTrue(scn.DSHasCardChoiceAvailable(tie2));
            scn.DSChooseCards(tie1, tie2);
        }
        // Finish leave-table responses for both forfeited ships
        for (int i = 0; i < 10; i++) {
            if (tie1.getZone() != Zone.AT_LOCATION && tie2.getZone() != Zone.AT_LOCATION) {
                break;
            }
            if (scn.DSAnyDecisionsAvailable() || scn.LSAnyDecisionsAvailable()) {
                scn.PassAllResponses();
                scn.PassResponses("ABOUT_TO_BE_LOST_FROM_TABLE");
                scn.PassResponses("LOST_FROM_TABLE");
                scn.PassCardLeavingTable();
            } else {
                break;
            }
        }

        assertTrue(tie1.getZone() == Zone.LOST_PILE || tie1.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(tie2.getZone() == Zone.LOST_PILE || tie2.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(scn.CardsAtLocation(trench, ywing));
    }

    @Test
    public void TightSqueezePlayableDuringAttackRunWithLoneStarfighterAndTwoOpposingAtTrench() {
        var scn = GetAttackRunScenario();

        var squeeze = scn.GetLSCard("squeeze");
        var attackrun = scn.GetLSCard("attackrun");
        var red2 = scn.GetLSCard("red2");
        var biggs = scn.GetLSCard("biggs");
        var torpedoes = scn.GetLSCard("torpedoes");
        var deathstar = scn.GetLSCard("deathstar");
        var trench = scn.GetLSCard("trench");
        var tie1 = scn.GetDSCard("tie1");
        var tie2 = scn.GetDSCard("tie2");

        scn.StartGame();
        scn.MoveCardsToLSHand(squeeze);
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(trench);
        scn.AttachCardsTo(trench, attackrun);
        scn.MoveCardsToLocation(deathstar, red2, tie1, tie2);
        scn.BoardAsPilot(red2, biggs);
        scn.AttachCardsTo(red2, torpedoes);

        scn.SkipToLSTurn(Phase.MOVE);

        assertTrue(scn.LSActionAvailable("Attempt to 'blow away' Death Star"));
        scn.PrepareLSDestiny(1);
        scn.PrepareLSDestiny(1);
        scn.LSChooseAction("Attempt to 'blow away' Death Star");
        scn.PassResponses("MOVING_AT_START_OF_ATTACK_RUN");
        scn.PassResponses("MOVED_AT_START_OF_ATTACK_RUN");

        scn.DSChooseCard(tie1);
        scn.PassResponses("MOVING_AT_START_OF_ATTACK_RUN");
        scn.PassResponses("MOVED_AT_START_OF_ATTACK_RUN");
        scn.DSChooseCard(tie2);
        scn.PassResponses("MOVING_AT_START_OF_ATTACK_RUN");
        scn.PassResponses("MOVED_AT_START_OF_ATTACK_RUN");

        assertTrue(scn.LSCardPlayAvailable(squeeze));
        scn.LSPlayCard(squeeze);
        scn.PassAllResponses();
        assertEquals(Zone.TOP_OF_LOST_PILE, squeeze.getZone());
    }
}
