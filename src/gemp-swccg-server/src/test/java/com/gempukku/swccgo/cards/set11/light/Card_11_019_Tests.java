package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_11_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obi", "1_021");
                    put("icbhg", "11_019");
                    put("hs", "7_063");
                    put("skull", "6_074");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                }},
                20,
                20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ICantBelieveHesGoneStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("icbhg").getBlueprint();

        assertEquals("I Can't Believe He's Gone", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.TATOOINE);
            add(Icon.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        assertEquals(ExpansionSet.TATOOINE, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void ICantBelieveHesGoneDeploysWhenHeroicSacrificePlacesObiWanOutOfPlay() {
        var scn = GetScenario();
        var obi = scn.GetLSCard("obi");
        var icbhg = scn.GetLSCard("icbhg");
        var hs = scn.GetLSCard("hs");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(icbhg, hs);
        scn.MoveCardsToLocation(site, obi, vader);
        scn.LSActivateForceCheat(2);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(false);
        assertTrue(scn.DSWonBattle());
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertTrue(scn.LSHasCardChoiceAvailable(obi));
        scn.LSChooseCard(obi);
        passUntilLsCanPlay(scn, hs);

        assertTrue("Heroic Sacrifice deploys after forfeiting Obi-Wan", lsCanPlay(scn, hs));
        scn.LSPlayCard(hs);
        scn.LSChooseCard(site);
        scn.PassResponses("Deploying");
        confirmRequiredJustDeployed(scn);
        if (scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText().toLowerCase().contains("place out of play")) {
            scn.LSChooseCard(obi);
        }
        passUntilLsCanPlay(scn, icbhg);

        assertTrue("I Can't Believe He's Gone deploys when Heroic Sacrifice places Obi-Wan out of play",
                lsCanPlay(scn, icbhg));
        scn.LSPlayCard(icbhg);
        scn.PassAllResponses();

        assertEquals(Zone.SIDE_OF_TABLE, icbhg.getZone());
        assertEquals(Zone.OUT_OF_PLAY, obi.getZone());
    }

    @Test
    public void ICantBelieveHesGoneDoesNotDeployWhenObiWanPlacedOutOfPlayAsGenericLostPileCard() {
        var scn = GetScenario();
        var obi = scn.GetLSCard("obi");
        var icbhg = scn.GetLSCard("icbhg");
        var skull = scn.GetLSCard("skull");
        var vader = scn.GetDSCard("vader");
        var rebel = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(icbhg, skull);
        scn.MoveCardsToTopOfLSLostPile(obi);
        scn.MoveCardsToLocation(site, rebel, vader);
        scn.LSActivateForceCheat(2);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardPlayAvailable(skull));
        scn.LSPlayCard(skull);
        passOptionalUntil(scn, "place out of play");
        if (scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText().toLowerCase().contains("place out of play")) {
            scn.LSChooseCard(obi);
        }
        passUntilLsCanPlay(scn, icbhg);

        assertFalse("I Can't Believe He's Gone does not deploy for generic Lost Pile out of play",
                lsCanPlay(scn, icbhg));
        assertEquals(Zone.OUT_OF_PLAY, obi.getZone());
        assertEquals(Zone.HAND, icbhg.getZone());
    }

    private static boolean lsCanPlay(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.LSAnyActionsAvailable() && scn.LSCardPlayAvailable(card);
    }

    private static void confirmRequiredJustDeployed(VirtualTableScenario scn) {
        var decision = scn.GetCurrentDecision();
        if (decision == null || !decision.getText().toLowerCase().contains("required")) {
            return;
        }
        String[] texts = decision.getDecisionParameters().get("actionText");
        String[] actionIds = decision.getDecisionParameters().get("actionId");
        if (texts != null && actionIds != null) {
            for (int i = 0; i < texts.length; i++) {
                if (texts[i] != null && texts[i].toLowerCase().contains("place")) {
                    scn.PlayerDecided(scn.GetDecidingPlayer(), actionIds[i]);
                    return;
                }
            }
        }
        scn.PlayerDecided(scn.GetDecidingPlayer(), "0");
    }

    private static void passOptionalUntil(VirtualTableScenario scn, String textFragment) {
        String needle = textFragment.toLowerCase();
        for (int i = 0; i < 15; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            if (decision.getText().toLowerCase().contains(needle)) {
                return;
            }
            if (!decision.getText().toLowerCase().contains("optional")) {
                return;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }

    private static void passUntilLsCanPlay(VirtualTableScenario scn, PhysicalCardImpl card) {
        for (int i = 0; i < 15; i++) {
            if (lsCanPlay(scn, card)) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional")) {
                return;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }
}
