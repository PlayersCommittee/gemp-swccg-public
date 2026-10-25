package com.gempukku.swccgo.cards.set8.dark;

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

/**
 * VHD tests for Endor 8_144 Go For Help! (shared reveal helper with LEAVE_ON_TOP leftovers).
 */
public class Card_8_144_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("luke", "1_019");
                    put("han", "1_013");
                    put("chewie", "1_003");
                }},
                new HashMap<>()
                {{
                    put("goforhelp", "8_144");
                    put("bikerscout", "8_092");
                    put("speederbike", "8_169");
                    put("junk", "1_301");
                    put("hothsite1", "3_150");
                    put("hothsite2", "3_148");
                    put("hothsite3", "3_149");
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
    public void GoForHelpStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();

        var card = scn.GetDSCard("goforhelp").getBlueprint();

        assertEquals("Go For Help!", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.ENDOR);
        }});
        assertEquals(ExpansionSet.ENDOR, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void GoForHelpRevealsDeploysMatchingAndLeavesRestOnTopOfReserve() {
        // Shared Panic/ED reveal path: cards stay in Reserve; leftovers LEAVE_ON_TOP (not lost).
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        var chewie = scn.GetLSCard("chewie");

        var goforhelp = scn.GetDSCard("goforhelp");
        var bikerscout = scn.GetDSCard("bikerscout");
        var speederbike = scn.GetDSCard("speederbike");
        var junk = scn.GetDSCard("junk");
        var dsFiller = scn.GetDSFiller(1);

        var hothsite1 = scn.GetDSCard("hothsite1");
        var hothsite2 = scn.GetDSCard("hothsite2");
        var hothsite3 = scn.GetDSCard("hothsite3");

        scn.StartGame();

        scn.MoveCardsToDSHand(goforhelp);

        scn.MoveLocationToTable(hothsite1);
        scn.MoveLocationToTable(hothsite2);
        scn.MoveLocationToTable(hothsite3);

        scn.MoveCardsToLocation(hothsite2, luke, han, chewie, dsFiller);

        scn.SkipToLSTurn(Phase.BATTLE);
        // Restack after Force activation so reveal sees these three on top
        scn.MoveCardsToTopOfOwnReserveDeck(speederbike, junk, bikerscout);
        scn.LSInitiateBattle(hothsite2);

        assertTrue(scn.DSDecisionAvailable("Battle just initiated"));
        assertTrue(scn.DSCardPlayAvailable(goforhelp));
        scn.DSPlayCard(goforhelp);
        scn.PassAllResponses();

        // Both players acknowledge reveal UI (not draw-into-hand)
        assertTrue("Expected DS reveal UI; got: " + decisionText(scn),
                scn.DSDecisionAvailable("Top card") || scn.DSDecisionAvailable("Reserve Deck"));
        scn.DSPass();
        if (scn.LSDecisionAvailable("Top card") || scn.LSDecisionAvailable("Reserve Deck")) {
            scn.LSPass();
        }

        assertTrue("Scout should still be in Reserve after reveal; zone=" + bikerscout.getZone(),
                bikerscout.getZone() == Zone.RESERVE_DECK || bikerscout.getZone() == Zone.TOP_OF_RESERVE_DECK);
        assertTrue("Junk should still be in Reserve after reveal; zone=" + junk.getZone(),
                junk.getZone() == Zone.RESERVE_DECK || junk.getZone() == Zone.TOP_OF_RESERVE_DECK);

        // Prefer scout/speeder when offered, otherwise resolve generically
        for (int i = 0; i < 50; i++) {
            if (goforhelp.getZone() == Zone.TOP_OF_USED_PILE || goforhelp.getZone() == Zone.USED_PILE) {
                break;
            }
            if (!scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
                break;
            }
            if (scn.DSDecisionAvailable("Choose card to deploy")) {
                var ids = scn.DSGetCardChoices();
                var bp = scn.DSGetBPChoices();
                String[] selectable = scn.DSGetADParam("selectable");
                String pick = null;
                if (ids != null && bp != null) {
                    for (int c = 0; c < ids.size(); c++) {
                        boolean isSelectable = selectable == null || (c < selectable.length && "true".equalsIgnoreCase(selectable[c]));
                        if (!isSelectable) {
                            continue;
                        }
                        String choice = normalizeBp(bp.get(c));
                        if (normalizeBp(bikerscout.getBlueprintId(true)).equals(choice)
                                || normalizeBp(speederbike.getBlueprintId(true)).equals(choice)) {
                            pick = ids.get(c);
                            break;
                        }
                        if (pick == null) {
                            pick = ids.get(c);
                        }
                    }
                }
                scn.DSDecided(pick == null ? "" : pick);
                continue;
            }
            resolveOneDecision(scn);
        }

        assertTrue("Go For Help should finish in Used pile; zone=" + goforhelp.getZone(),
                goforhelp.getZone() == Zone.TOP_OF_USED_PILE || goforhelp.getZone() == Zone.USED_PILE);
        assertTrue("Non-matching leftover must stay in Reserve (LEAVE_ON_TOP), not lost; zone=" + junk.getZone(),
                junk.getZone() == Zone.RESERVE_DECK || junk.getZone() == Zone.TOP_OF_RESERVE_DECK);
        assertFalse("Junk must not be lost under Go For Help; zone=" + junk.getZone(),
                junk.getZone() == Zone.LOST_PILE || junk.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void GoForHelpNotAvailableIfNotDoublePower() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");

        var goforhelp = scn.GetDSCard("goforhelp");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);
        var trooper4 = scn.GetDSFiller(4);

        var hothsite2 = scn.GetDSCard("hothsite2");

        scn.StartGame();

        scn.MoveCardsToDSHand(goforhelp);
        scn.MoveLocationToTable(hothsite2);

        scn.MoveCardsToLocation(hothsite2, luke, trooper1, trooper2, trooper3, trooper4);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(hothsite2);

        assertTrue(scn.LSDecisionAvailable("Choose weapons segment action")
                || scn.LSDecisionAvailable("Choose Battle action")
                || scn.AwaitingLSWeaponsSegmentActions());
        assertFalse("Go For Help must not be playable without double power",
                scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(goforhelp));
    }

    @Test
    public void GoForHelpBikerScoutKeywordPresent() {
        var scn = GetScenario();
        var bikerscout = scn.GetDSCard("bikerscout").getBlueprint();
        assertTrue(bikerscout.hasKeyword(Keyword.SCOUT) || bikerscout.hasKeyword(Keyword.BIKER_SCOUT));
    }


    private static void resolveOneDecision(VirtualTableScenario scn) {
        String player = scn.GetDecidingPlayer();
        var decision = scn.GetAwaitingDecision(player);
        if (decision == null) {
            return;
        }
        String text = decision.getText() == null ? "" : decision.getText().toLowerCase();
        if (text.contains("optional response")) {
            scn.PlayerPass(player);
            return;
        }
        String[] actionIds = scn.GetADParam(player, "actionId");
        if (actionIds != null && actionIds.length > 0) {
            String[] actionTexts = scn.GetADParam(player, "actionText");
            if (actionTexts != null) {
                for (int a = 0; a < actionTexts.length; a++) {
                    if (actionTexts[a] != null && actionTexts[a].toLowerCase().contains("pass")) {
                        scn.PlayerDecided(player, actionIds[a]);
                        return;
                    }
                }
            }
            scn.PlayerPass(player);
            return;
        }
        String[] cardIds = scn.GetADParam(player, "cardId");
        String[] selectable = scn.GetADParam(player, "selectable");
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
            return;
        }
        String[] results = scn.GetADParam(player, "results");
        if (results != null && results.length > 0) {
            scn.PlayerDecided(player, "0");
            return;
        }
        String[] max = scn.GetADParam(player, "max");
        if (max != null && max.length > 0) {
            scn.PlayerDecided(player, max[0]);
            return;
        }
        scn.PlayerPass(player);
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