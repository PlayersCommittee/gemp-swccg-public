package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_8_110_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("odds", "4_027"); //never tell me the odds
                }},
                new HashMap<>()
                {{
                    put("vesden", "8_110"); //navy trooper vesden
                    put("u3p0", "2_107");
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
    public void NavyTrooperVesdenStatsAndKeywordsAreCorrect() {
        /**
         * Title: Navy Trooper Vesden
         * Uniqueness: Unique
         * Side: Dark
         * Type: Character
         * Subtype: Imperial
         * Destiny: 3
         * Deploy: 2
         * Power: 2
         * Ability: 1
         * Forfeit: 3
         * Icons: Endor
         * Keywords:
         * Persona:
         * Game Text: If present with a Scomp link when Never Tell Me The Odds just reached the top of your
         *      Reserve Deck, may 'shield' (add 3 to destiny number of) one of your characters at each location
         *      for remainder of turn.
         * Lore: Counterintelligence agent assigned by ISB. Operates sensors designed to protect the control bunker
         *      from infiltration.
         * Set: Endor
         * Rarity: U
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("vesden").getBlueprint();

        assertEquals("Navy Trooper Vesden", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.TROOPER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WARRIOR);
            add(Icon.IMPERIAL);
            add(Icon.ENDOR);
        }});
        assertEquals(ExpansionSet.ENDOR,card.getExpansionSet());
        assertEquals(Rarity.U,card.getRarity());
    }

    @Test
    public void NavyTrooperVesdenMayShieldWhenOddsRevealed() {
        //Test1: can use optional shield action in response to odds being revealed
        //Test2: can choose one of your characters at a location to shield
        //Test3: cannot choose opponent's characters at a location to shield
        //Test4: can choose another character to shield at a different location (as shielded cards)
        //Test5: cannot choose another character to shield at same location (as shielded cards)
        //Test6: shielded card has destiny increased by 3
        //Test7: non-shielded card does not have destiny increased
        //Test8: shielded cards can successfully change Odds damage
        //Test9: shield destiny change lasts until end of turn
        //Test10: shield destiny change does not persist to next turn
        var scn = GetScenario();

        var vesden = scn.GetDSCard("vesden");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);

        var odds = scn.GetLSCard("odds");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var rebelTrooper3 = scn.GetLSFiller(3);

        var site = scn.GetLSStartingLocation();
        var otherLocation = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, vesden, trooper1);
        scn.MoveCardsToLocation(otherLocation, rebelTrooper1, rebelTrooper2, rebelTrooper3, trooper2, trooper3);

        scn.MoveCardsToLSHand(odds);

        scn.SkipToLSTurn(Phase.DEPLOY);
            //move all but 1 card out of DS reserve so insert will be placed at the bottom
            //then lose 1 force from battle damage to trigger insert
        assertEquals(5, scn.GetDSReserveDeckCount());
        scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck());
        scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck());
        scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck());
        scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck());
        assertEquals(1, scn.GetDSReserveDeckCount());

        scn.LSPlayCard(odds);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(otherLocation);
        scn.PassAllResponses();
        scn.SkipToDamageSegment();
        scn.DSPayForceLossFromReserveDeck();

        scn.LSPass(); //INSERT_CARD_REVEALED - Optional responses
        assertTrue(scn.DSCardActionAvailable(vesden, "Shield")); //test1

        scn.DSUseCardAction(vesden, "Shield");
        assertTrue(scn.DSDecisionAvailable("Choose card to 'shield'"));
        assertTrue(scn.DSHasCardChoiceAvailable(vesden)); //test2
        assertTrue(scn.DSHasCardChoiceAvailable(trooper1)); //test2
        assertTrue(scn.DSHasCardChoiceAvailable(trooper2)); //test2
        assertTrue(scn.DSHasCardChoiceAvailable(trooper3)); //test2
        assertFalse(scn.DSHasCardChoiceAvailable(rebelTrooper1)); //test3
        assertFalse(scn.DSHasCardChoiceAvailable(rebelTrooper2)); //test3
        assertFalse(scn.DSHasCardChoiceAvailable(rebelTrooper3)); //test3
        scn.DSChooseCard(trooper2);

        assertTrue(scn.DSDecisionAvailable("Choose card to 'shield'"));
        assertTrue(scn.DSHasCardChoiceAvailable(vesden)); //test4
        assertTrue(scn.DSHasCardChoiceAvailable(trooper1)); //test4
        assertFalse(scn.DSHasCardChoiceAvailable(trooper2)); //test5
        assertFalse(scn.DSHasCardChoiceAvailable(trooper3)); //test5
        scn.DSChooseCard(trooper1);

        assertEquals(4, scn.GetDestiny(trooper1)); //test6
        assertEquals(4, scn.GetDestiny(trooper2)); //test6
        assertEquals(1, scn.GetDestiny(trooper3)); //test7

        scn.PassAllResponses();

        //expect odds total LS: 1 + 1 + 1 (trooper1, trooper2, trooper3)
        //expect odds total DS: 1 + 3 + 4 (trooper3, vesden, trooper1)
        //expected result: LS loses 5
        scn.AwaitingLSForceLossPayment();

        assertEquals(1, scn.GetLSLostPileCount()); //(odds)
        scn.LSPayForceLossFromForcePile(); //(not enough in reserve to lose all 5)
        scn.LSPayForceLossFromForcePile();
        scn.LSPayRemainingForceLossFromReserveDeck();
        assertEquals(6, scn.GetLSLostPileCount()); //test8

        scn.SkipToPhase(Phase.DRAW);
        assertEquals(4, scn.GetDestiny(trooper1)); //test9
        scn.SkipToDSTurn();
        assertEquals(1, scn.GetDestiny(trooper1)); //test10
    }

    @Test
    public void NavyTrooperVesdenCannotShieldInactiveCharacters() {
        //Test1: can use optional shield action in response to odds being revealed
        //Test2: can choose one of your characters at a location to shield
        //Test3: cannot choose opponent's characters at a location to shield
        //Test4: can choose another character to shield at a different location (as shielded cards)
        //Test5: cannot choose another character to shield at same location (as shielded cards)
        //Test6: shielded card has destiny increased by 3
        //Test7: non-shielded card does not have destiny increased
        //Test8: shielded cards can successfully change Odds damage
        //Test9: shield destiny change lasts until end of turn
        //Test10: shield destiny change does not persist to next turn
        var scn = GetScenario();

        var vesden = scn.GetDSCard("vesden");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);
        var u3p0 = scn.GetDSCard("u3p0");

        var odds = scn.GetLSCard("odds");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var rebelTrooper3 = scn.GetLSFiller(3);

        var site = scn.GetLSStartingLocation();
        var otherLocation = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, vesden, trooper1);
        scn.MoveCardsToLocation(otherLocation, rebelTrooper1, rebelTrooper2, rebelTrooper3, trooper2, trooper3);

        scn.MoveCardsToDSHand(u3p0);
        scn.MoveCardsToLSHand(odds);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(u3p0);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        assertTrue(u3p0.isUndercover());
        assertFalse(scn.IsCardActive(u3p0));

        scn.SkipToLSTurn(Phase.DEPLOY);
        //move all but 1 card out of DS reserve so insert will be placed at the bottom
        //then lose 1 force from battle damage to trigger insert
        assertEquals(7, scn.GetDSReserveDeckCount());
        scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck());
        scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck());
        scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck());
        scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck());
        scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck());
        scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck());
        assertEquals(1, scn.GetDSReserveDeckCount());

        scn.LSPlayCard(odds);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(otherLocation);
        scn.PassAllResponses();
        scn.SkipToDamageSegment();
        scn.DSPayForceLossFromReserveDeck();

        scn.LSPass(); //INSERT_CARD_REVEALED - Optional responses
        assertTrue(scn.DSCardActionAvailable(vesden, "Shield")); //test1

        scn.DSUseCardAction(vesden, "Shield");
        assertTrue(scn.DSDecisionAvailable("Choose card to 'shield'"));
        assertTrue(scn.DSHasCardChoiceAvailable(vesden));
        assertTrue(scn.DSHasCardChoiceAvailable(trooper1));
        assertFalse(scn.DSHasCardChoiceAvailable(u3p0)); //test1
        assertTrue(scn.DSHasCardChoiceAvailable(trooper2));
        assertTrue(scn.DSHasCardChoiceAvailable(trooper3));
    }

    //tests to add:
    // can choose character at non-site location

}
