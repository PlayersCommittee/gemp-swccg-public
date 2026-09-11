package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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
 * VHD tests for Premiere 1_103 Panic (reveal/deploy/lose shared path; Closes #303).
 */
public class Card_1_103_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("panic", "1_103");
                    put("trooper", "1_028");
                    put("luke", "1_019");
                    put("junk", "1_079");
                    put("ls_cantina", "1_128");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("emergency", "1_244");
                    put("st1", "1_194");
                    put("st2", "1_194");
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
    public void PanicStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("panic").getBlueprint();

        assertEquals(Title.Panic, card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
        assertEquals(Rarity.U1, card.getRarity());
    }

    @Test
    public void PanicRevealsDeploysMatchingAndLosesRestWithoutDrawingIntoHand() {
        var scn = GetScenario();

        var panic = scn.GetLSCard("panic");
        var trooper = scn.GetLSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var junk = scn.GetLSCard("junk");
        var lsCantina = scn.GetLSCard("ls_cantina");

        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveCardsToLSHand(panic);
        scn.MoveLocationToTable(lsCantina);
        scn.MoveCardsToLocation(lsCantina, trooper, vader);

        // junk then luke on top (luke is topmost)
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.MoveCardsToTopOfOwnReserveDeck(junk, luke);
        scn.DSInitiateBattle(lsCantina);

        assertTrue(scn.LSCardPlayAvailable(panic));
        scn.LSPlayCard(panic);
        scn.PassAllResponses();

        assertTrue("Expected reveal-count decision; got: " + decisionText(scn),
                scn.LSDecisionAvailable("Choose number of cards to reveal"));
        scn.LSDecided(2);

        // Both players acknowledge the reveal UI
        assertTrue("Expected LS reveal UI; got: " + decisionText(scn),
                scn.LSDecisionAvailable("Top card") || scn.LSDecisionAvailable("Reserve Deck"));
        scn.LSPass();
        if (scn.DSDecisionAvailable("Top card") || scn.DSDecisionAvailable("Reserve Deck")) {
            scn.DSPass();
        }

        // Deploy matching character (Luke) for free, or stop if none offered
        assertTrue("Expected deploy choice; got: " + decisionText(scn),
                scn.LSDecisionAvailable("Choose card to deploy"));
        {
            var bp = scn.LSGetBPChoices();
            var ids = scn.LSGetCardChoices();
            String lukeBp = luke.getBlueprintId(true);
            int idx = -1;
            for (int i = 0; i < bp.size(); i++) {
                if (normalizeBp(lukeBp).equals(normalizeBp(bp.get(i)))) {
                    idx = i;
                    break;
                }
            }
            assertTrue("Luke should appear in deploy choices; bp=" + bp + " lukeBp=" + lukeBp, idx >= 0);
            scn.LSDecided(ids.get(idx));
        }
        resolveUntilIdle(scn, 40);

        assertTrue("Panic should finish in Used pile; zone=" + panic.getZone() + " lastDecision=" + decisionText(scn),
                panic.getZone() == Zone.TOP_OF_USED_PILE || panic.getZone() == Zone.USED_PILE);
        assertFalse("Non-matching revealed interrupt should not remain in Reserve Deck; zone=" + junk.getZone(),
                junk.getZone() == Zone.RESERVE_DECK || junk.getZone() == Zone.TOP_OF_RESERVE_DECK);
        assertFalse("Luke should not be left in hand after Panic; zone=" + luke.getZone(), luke.getZone() == Zone.HAND);
    }

    @Test
    public void PanicNotAvailableIfNotMoreThanDoublePower() {
        var scn = GetScenario();

        var panic = scn.GetLSCard("panic");
        var luke = scn.GetLSCard("luke");
        var lsCantina = scn.GetLSCard("ls_cantina");

        var st1 = scn.GetDSCard("st1");

        scn.StartGame();
        scn.MoveCardsToLSHand(panic);
        scn.MoveLocationToTable(lsCantina);
        scn.MoveCardsToLocation(lsCantina, luke, st1);

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSAnyDecisionsAvailable());
        scn.DSInitiateBattle(lsCantina);

        assertFalse("Panic must not be playable when opponent power is not more than double",
                scn.LSAnyDecisionsAvailable() && scn.LSCardPlayAvailable(panic));
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
            // Action menus also expose cardId; answer with actionId (or pass), never a bare cardId.
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
                    // All unselectable + min 0 (e.g. acknowledge-only / decline deploy): empty OK
                    scn.PlayerDecided(player, pick == null ? "" : pick);
                } else {
                    // Target selection without selectable flags: pick first cardId
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
