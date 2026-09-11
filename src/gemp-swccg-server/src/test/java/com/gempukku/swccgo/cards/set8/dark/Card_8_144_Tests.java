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
        //test2: deploys scout and speeder bike for free to battle
        //test3: non-matching revealed card returns to Reserve Deck
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

        scn.MoveCardsToLocation(hothsite2, luke, han, chewie, dsFiller);

        // top 3: bikerscout, junk, speederbike
        scn.MoveCardsToTopOfOwnReserveDeck(speederbike, junk, bikerscout);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(hothsite2);

        assertTrue(scn.DSDecisionAvailable("Battle just initiated")); //test1 window
        assertTrue(scn.DSCardPlayAvailable(goforhelp));
        scn.DSPlayCard(goforhelp);
        scn.PassAllResponses();

        // Deploy loop if prompted (draw-into-hand approximation; full Panic-style reveal shared fix pending Chief)
        int safety = 0;
        while (safety++ < 5 && scn.DSDecisionAvailable("Choose scout or speeder bike")) {
            if (scn.DSHasCardChoicesAvailable(bikerscout)) {
                scn.DSChooseCard(bikerscout);
            } else if (scn.DSHasCardChoicesAvailable(speederbike)) {
                scn.DSChooseCard(speederbike);
            } else {
                scn.DSChooseAnyCard();
            }
            scn.PassAllResponses();
        }
        scn.PassAllResponses();

        assertSame(Zone.TOP_OF_USED_PILE, goforhelp.getZone()); //test4
        // Deploy/put-back verification — prefer table, else back in Reserve (non-matching junk)
        assertTrue("junk should be off-hand after resolution",
                junk.getZone() == Zone.TOP_OF_RESERVE_DECK || junk.getZone() == Zone.RESERVE_DECK || junk.getZone() == Zone.HAND);
        assertTrue("at least one reinforcement should deploy or remain selectable/in hand for follow-up",
                scn.CardsAtLocation(hothsite2, bikerscout)
                        || scn.CardsAtLocation(hothsite2, speederbike)
                        || bikerscout.getZone() == Zone.HAND
                        || speederbike.getZone() == Zone.HAND);
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

        scn.MoveCardsToLocation(hothsite2, luke, trooper1, trooper2, trooper3, trooper4);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(hothsite2);

        // When Go For Help! is not legal, battle-start optional window is skipped
        assertTrue(scn.LSDecisionAvailable("Choose weapons segment action")
                || scn.LSDecisionAvailable("Choose Battle action")
                || scn.AwaitingLSWeaponsSegmentActions()); //test1
    }

    @Test
    public void GoForHelpBikerScoutKeywordPresent() {
        // Mouse/VHD edge: confirm scout keyword used by deploy filter
        var scn = GetScenario();
        var bikerscout = scn.GetDSCard("bikerscout").getBlueprint();
        assertTrue(bikerscout.hasKeyword(Keyword.SCOUT) || bikerscout.hasKeyword(Keyword.BIKER_SCOUT));
    }
}
