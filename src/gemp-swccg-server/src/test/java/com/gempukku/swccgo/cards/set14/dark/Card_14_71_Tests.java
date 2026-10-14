package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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

/**
 * VHD tests for Theed Palace 14_71 3B3-21 (reveal X / deploy battle droids / lose rest; Closes #171).
 */
public class Card_14_71_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_019");
                    put("ls_hallway", "14_51");
                }},
                new HashMap<>() {{
                    put("threeb3", "14_71");
                    put("bd1", "14_70");
                    put("bd2", "14_72");
                    put("junk", "1_249");
                    put("ds_hallway", "14_112");
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

    @Test
    public void ThreeB3TwentyOneStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("threeb3").getBlueprint();

        assertEquals("3B3-21", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.INFANTRY_BATTLE_DROID);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.THEED_PALACE);
            add(Icon.EPISODE_I);
            add(Icon.PRESENCE);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.BATTLE);
        }});
        assertEquals(ExpansionSet.THEED_PALACE, card.getExpansionSet());
        assertEquals(Rarity.U, card.getRarity());
    }

    @Test
    public void ThreeB3TwentyOneNotAvailableIfYouInitiateBattle() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var lsHallway = scn.GetLSCard("ls_hallway");

        var threeb3 = scn.GetDSCard("threeb3");

        scn.StartGame();
        scn.MoveLocationToTable(lsHallway);
        scn.MoveCardsToLocation(lsHallway, luke, threeb3);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(lsHallway);

        assertFalse("3B3-21 must not trigger when DS initiates battle",
                scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(threeb3, "Reveal"));
    }

    @Test
    public void ThreeB3TwentyOneRevealsDeploysBattleDroidsAndLosesRestWhenOpponentInitiates() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var lsHallway = scn.GetLSCard("ls_hallway");

        var threeb3 = scn.GetDSCard("threeb3");
        var bd1 = scn.GetDSCard("bd1");
        var junk = scn.GetDSCard("junk");

        scn.StartGame();
        scn.MoveLocationToTable(lsHallway);
        scn.MoveCardsToLocation(lsHallway, luke, threeb3);

        scn.SkipToLSTurn(Phase.BATTLE);
        // junk then bd1 on top (bd1 is topmost)
        scn.MoveCardsToTopOfOwnReserveDeck(junk, bd1);
        scn.LSInitiateBattle(lsHallway);

        assertTrue("Expected 3B3-21 reveal action; decision=" + decisionText(scn),
                scn.DSCardActionAvailable(threeb3, "Reveal"));
        scn.DSUseCardAction(threeb3, "Reveal");

        assertTrue("Expected Force amount decision; got: " + decisionText(scn),
                scn.DSDecisionAvailable("Choose amount of Force to use"));
        scn.DSDecided(2);
        scn.PassAllResponses(); // Use X Force cost optional responses

        // Both players acknowledge the reveal UI
        assertTrue("Expected DS reveal UI; got: " + decisionText(scn),
                scn.DSDecisionAvailable("Top card") || scn.DSDecisionAvailable("Reserve Deck"));
        scn.DSPass();
        if (scn.LSDecisionAvailable("Top card") || scn.LSDecisionAvailable("Reserve Deck")) {
            scn.LSPass();
        }

        assertTrue("Expected deploy choice; got: " + decisionText(scn),
                scn.DSDecisionAvailable("Choose card to deploy"));
        {
            var bp = scn.DSGetBPChoices();
            var ids = scn.DSGetCardChoices();
            String bdBp = bd1.getBlueprintId(true);
            int idx = -1;
            for (int i = 0; i < bp.size(); i++) {
                if (normalizeBp(bdBp).equals(normalizeBp(bp.get(i)))) {
                    idx = i;
                    break;
                }
            }
            assertTrue("Battle droid should appear in deploy choices; bp=" + bp + " bdBp=" + bdBp, idx >= 0);
            scn.DSDecided(ids.get(idx));
        }
        resolveUntilIdle(scn, 50);

        assertFalse("Non-battle-droid revealed card should not remain in Reserve Deck; zone=" + junk.getZone(),
                junk.getZone() == Zone.RESERVE_DECK || junk.getZone() == Zone.TOP_OF_RESERVE_DECK);
        assertFalse("Deployed battle droid should not remain in Reserve Deck; zone=" + bd1.getZone(),
                bd1.getZone() == Zone.RESERVE_DECK || bd1.getZone() == Zone.TOP_OF_RESERVE_DECK);
        assertFalse("Battle droid should not be left in hand; zone=" + bd1.getZone(), bd1.getZone() == Zone.HAND);
    }

    @Test
    public void ThreeB3TwentyOneXLimitedByForceAndReserveSize() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var lsHallway = scn.GetLSCard("ls_hallway");

        var threeb3 = scn.GetDSCard("threeb3");
        var bd1 = scn.GetDSCard("bd1");
        var bd2 = scn.GetDSCard("bd2");

        scn.StartGame();
        scn.MoveLocationToTable(lsHallway);
        scn.MoveCardsToLocation(lsHallway, luke, threeb3);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToTopOfOwnReserveDeck(bd2, bd1);
        // Leave only bd1+bd2 in Reserve Deck
        var toClear = new java.util.ArrayList<>(scn.GetDSReserveDeck());
        for (var card : toClear) {
            if (card != bd1 && card != bd2) {
                scn.MoveCardsToTopOfDSUsedPile((com.gempukku.swccgo.game.PhysicalCardImpl) card);
            }
        }
        scn.MoveCardsToTopOfOwnReserveDeck(bd2, bd1);
        assertEquals(2, scn.GetDSReserveDeckCount());

        scn.LSInitiateBattle(lsHallway);
        assertTrue(scn.DSCardActionAvailable(threeb3, "Reveal"));
        scn.DSUseCardAction(threeb3, "Reveal");

        assertTrue(scn.DSDecisionAvailable("Choose amount of Force to use"));
        String[] max = scn.GetADParam(scn.DS, "max");
        assertTrue("Expected max param; decision=" + decisionText(scn), max != null && max.length > 0);
        assertEquals("X max should be limited by remaining Reserve Deck size", "2", max[0]);
        scn.DSDecided(2);
        scn.PassAllResponses();
        resolveUntilIdle(scn, 40);
    }

    private static void resolveUntilIdle(VirtualTableScenario scn, int maxSteps) {
        for (int i = 0; i < maxSteps; i++) {
            if (!scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
                break;
            }
            String player = scn.GetDecidingPlayer();
            var decision = scn.GetAwaitingDecision(player);
            if (decision == null) {
                break;
            }
            String text = decision.getText() == null ? "" : decision.getText().toLowerCase();

            if (text.contains("optional response")) {
                scn.PlayerPass(player);
                continue;
            }

            String[] cardIds = scn.GetADParam(player, "cardId");
            String[] selectable = scn.GetADParam(player, "selectable");
            String[] actionIds = scn.GetADParam(player, "actionId");
            if (actionIds != null && actionIds.length > 0) {
                String[] actionTexts = scn.GetADParam(player, "actionText");
                if (actionTexts != null) {
                    boolean passed = false;
                    for (int a = 0; a < actionTexts.length; a++) {
                        if (actionTexts[a] != null && actionTexts[a].toLowerCase().contains("pass")) {
                            scn.PlayerDecided(player, actionIds[a]);
                            passed = true;
                            break;
                        }
                    }
                    if (!passed) {
                        scn.PlayerPass(player);
                    }
                } else {
                    scn.PlayerPass(player);
                }
                continue;
            }

            if (cardIds != null && cardIds.length > 0) {
                String pick = null;
                if (selectable != null && selectable.length == cardIds.length) {
                    for (int c = 0; c < cardIds.length; c++) {
                        if ("true".equalsIgnoreCase(selectable[c])) {
                            pick = cardIds[c];
                            break;
                        }
                    }
                    scn.PlayerDecided(player, pick == null ? "" : pick);
                } else {
                    scn.PlayerDecided(player, cardIds[0]);
                }
                continue;
            }

            String[] results = scn.GetADParam(player, "results");
            if (results != null && results.length > 0) {
                scn.PlayerDecided(player, "0");
                continue;
            }

            String[] max = scn.GetADParam(player, "max");
            if (max != null && max.length > 0) {
                scn.PlayerDecided(player, max[0]);
                continue;
            }

            scn.PlayerPass(player);
        }
    }

    private static String normalizeBp(String bp) {
        if (bp == null || !bp.contains("_")) {
            return bp;
        }
        String[] parts = bp.split("_", 2);
        try {
            return parts[0] + "_" + Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return bp;
        }
    }

    private static String decisionText(VirtualTableScenario scn) {
        var d = scn.GetCurrentDecision();
        return d == null ? "null" : d.getText();
    }
}

