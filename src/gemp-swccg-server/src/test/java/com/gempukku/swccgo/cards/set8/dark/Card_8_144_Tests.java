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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Card_8_144_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("luke", "1_019"); // Luke Skywalker - high power
                    put("han", "1_013"); // Han Solo
                    put("chewie", "1_003"); // Chewbacca
                }},
                new HashMap<>()
                {{
                    put("goforhelp", "8_144"); // Go For Help!
                    put("bikerscout", "8_092"); // Biker Scout Trooper
                    put("speederbike", "8_169"); // Speeder Bike
                    put("junk", "1_301"); // TIE Fighter - not scout/speeder bike
                    put("hothsite1", "3_150"); // Hoth: Wampa Cave (exterior)
                    put("hothsite2", "3_148"); // Hoth: Ice Plains (exterior)
                    put("hothsite3", "3_149"); // Hoth: North Ridge (exterior)
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
        /**
         * Title: Go For Help!
         * Uniqueness: Unique
         * Side: Dark
         * Type: Interrupt
         * Subtype: Used
         * Destiny: 5
         * Icons: Interrupt, Endor
         * Game Text: If opponent just initiated a battle at an exterior site with double your total power,
         *      reveal top 3 cards of your Reserve Deck. If any of those cards are scouts or speeder bikes,
         *      deploy them for free to that battle (replace others on top of Reserve Deck in same order).
         * Lore: When confronted with enemy troops, biker scouts are instructed to immediately call for reinforcements.
         * Set: Endor
         * Rarity: C
         */

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
    public void GoForHelpRevealsAndDeploysScoutAndSpeederBikeToBattle() {
        //test1: playable when LS just initiated battle at exterior site with >= double DS power
        //test2: reveals top 3; deploys scout and speeder bike for free to battle
        //test3: non-matching revealed card stays on top of Reserve Deck
        //test4: Go For Help! goes to used pile
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

        // LS heavy power vs one weak DS character at exterior site
        scn.MoveCardsToLocation(hothsite2, luke, han, chewie, dsFiller);

        // Stack Reserve Deck so top 3 are: bikerscout, junk, speederbike
        // MoveCardsToTop: last argument becomes top
        scn.MoveCardsToTopOfOwnReserveDeck(speederbike, junk, bikerscout);

        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(hothsite2);

        assertTrue(scn.DSDecisionAvailable("Battle just initiated")); //test1 window
        assertTrue(scn.DSCardPlayAvailable(goforhelp));
        scn.DSPlayCard(goforhelp);

        scn.LSPass(); // Playing Go For Help! - Optional responses
        scn.DSPass();

        // Reveal UI for both players (min/max 0 selection = acknowledge)
        assertTrue(scn.DSDecisionAvailable("Top card") || scn.LSDecisionAvailable("Top card"));
        if (scn.DSDecisionAvailable("Top card")) {
            scn.DSDecided("");
        }
        if (scn.LSDecisionAvailable("Top card")) {
            scn.LSDecided("");
        }
        // Opponent may still be acknowledging
        if (scn.LSDecisionAvailable("Top card")) {
            scn.LSDecided("");
        }
        if (scn.DSDecisionAvailable("Top card")) {
            scn.DSDecided("");
        }

        // Deploy scout / speeder bike (any order). Auto-deploys if only one remains.
        for (int i = 0; i < 3; i++) {
            if (scn.DSDecisionAvailable("Choose scout or speeder bike")) {
                if (scn.DSHasCardChoicesAvailable(bikerscout)) {
                    scn.DSChooseCard(bikerscout);
                } else if (scn.DSHasCardChoicesAvailable(speederbike)) {
                    scn.DSChooseCard(speederbike);
                } else {
                    break;
                }
                scn.PassAllResponses();
            } else {
                break;
            }
        }
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(hothsite2, bikerscout)); //test2
        assertTrue(scn.CardsAtLocation(hothsite2, speederbike)); //test2
        assertSame(Zone.TOP_OF_RESERVE_DECK, junk.getZone()); //test3
        assertSame(Zone.TOP_OF_USED_PILE, goforhelp.getZone()); //test4
    }

    @Test
    public void GoForHelpNotAvailableIfNotDoublePower() {
        //test1: not playable when opponent does not have at least double your power
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

        // Many DS vs one LS - LS does not have double DS power
        scn.MoveCardsToLocation(hothsite2, luke, trooper1, trooper2, trooper3, trooper4);

        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(hothsite2);

        assertTrue(scn.DSDecisionAvailable("Battle just initiated"));
        assertFalse(scn.DSCardPlayAvailable(goforhelp)); //test1
    }

    @Test
    public void GoForHelpBikerScoutKeywordPresent() {
        // Mouse/VHD edge: confirm scout keyword used by deploy filter
        var scn = GetScenario();
        var bikerscout = scn.GetDSCard("bikerscout").getBlueprint();
        assertTrue(bikerscout.hasKeyword(Keyword.SCOUT) || bikerscout.hasKeyword(Keyword.BIKER_SCOUT));
    }
}
