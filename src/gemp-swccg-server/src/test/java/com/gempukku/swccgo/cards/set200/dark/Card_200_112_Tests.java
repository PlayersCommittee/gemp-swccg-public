package com.gempukku.swccgo.cards.set200.dark;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_200_112_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(

                new HashMap<>() {{
                    put("ls_control", "4_048");
                }},
                new HashMap<>() {{
                    put("something", "200_112"); //Something Special Planned For Them (V)
                    put("devastator","1_302");
                    put("tie","1_304");
                    put("ds_control", "4_139");
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
    public void SomethingSpecialPlannedForThemVStatsAndKeywordsAreCorrect() {
        /**
         * Title: Something Special Planned For Them (V)
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Effect
         * Destiny: 5
         * Icons: Death Star  II
         * Game Text: Deploy on table. Once per game, if opponent just played an Interrupt, may stack it here.
         *         To play any new Interrupt of same name, players must first stack it here. May place this Effect
         *         in Lost Pile (place cards here in owners' Used Pile) to retrieve a starship. (Immune to Alter.)
         * Lore: The high command of the Emperor's fleet is selected as much for loyalty and obedience as for
         *         martial skills. A wise admiral knows better than to question Palpatine.
         * Set: Virtual Set 0
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("something").getBlueprint();

        assertEquals("Something Special Planned For Them", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.GRABBER);
            add(Icon.DEATH_STAR_II);
            add(Icon.VIRTUAL_SET_0);
        }});
        assertEquals(ExpansionSet.SET_0,card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SomethingSpecialPlannedForThemVDeploysFree() {
        //test1: deploys on table
        //test2: deploy was free
        var scn = GetScenario();

        var something = scn.GetDSCard("something");
        var trooper = scn.GetDSFiller(1);

        var ls_control = scn.GetLSCard("ls_control");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(something);
        scn.MoveCardsToLSHand(ls_control);

        scn.MoveCardsToLocation(site, trooper);

        scn.SkipToPhase(Phase.DEPLOY);

        scn.DSPlayCard(something);
        scn.PassAllResponses();

        assertEquals(Zone.SIDE_OF_TABLE, something.getZone()); //test1
        assertEquals(0, scn.GetDSUsedPileCount()); //test2

        scn.SkipToLSTurn();
    }

    @Test
    public void SomethingSpecialPlannedForThemVCanGrabInterrupt() {
        //test1: cannot grab own interrupt just played
        //test2: can grab opponent's interrupt just played
        //test3: grabbed interrupt is stacked
        var scn = GetScenario();

        var something = scn.GetDSCard("something");
        var ds_control = scn.GetDSCard("ds_control");
        var trooper = scn.GetDSFiller(1);

        var ls_control = scn.GetLSCard("ls_control");
        var rebelTrooper = scn.GetLSFiller(1);

        var site1 = scn.GetLSStartingLocation();
        var site2 = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToSideOfTable(something);

        scn.MoveCardsToDSHand(ds_control);
        scn.MoveCardsToLSHand(ls_control);

        scn.MoveCardsToLocation(site1, trooper);
        scn.MoveCardsToLocation(site2, rebelTrooper);

        scn.SkipToLSTurn(Phase.CONTROL);

        scn.LSForceDrainAt(site2);
        scn.DSPlayCard(ds_control);

        scn.LSPass(); //Playing Control - Optional responses
        assertFalse(scn.DSCardActionAvailable(something, "Grab")); //test1
        scn.DSPass();
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.CONTROL);

        scn.DSForceDrainAt(site1);
        scn.LSPlayCard(ls_control);

        assertTrue(scn.DSCardActionAvailable(something, "Grab")); //test2
        scn.DSUseCardAction(something, "Grab");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(0, scn.GetLSLostPileCount());
        assertTrue(scn.IsStackedOn(something, ls_control)); //test3
    }

    @Test
    public void SomethingSpecialPlannedForThemVCanRetrieveStarship() {
        //test1: can retrieve a starship
        //test2: (NOT IMPLEMENTED) cannot retrieve a non-starship (character)
        //test3: retrieved starship goes to used pile
        //test4: something special is sent to lost pile
        var scn = GetScenario();

        var something = scn.GetDSCard("something");
        var tie = scn.GetDSCard("tie");
        var devastator = scn.GetDSCard("devastator");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveCardsToSideOfTable(something);

        scn.MoveCardsToTopOfDSLostPile(tie, trooper, devastator);

        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(something, "Place")); //test1
        scn.DSUseCardAction(something, "Place");
        scn.PassAllResponses();

        assertTrue(scn.DSHasCardChoiceAvailable(tie)); //test1
            //this check doesn't work because trooper is shown (but can't be selected)?
        //assertFalse(scn.DSHasCardChoiceAvailable(trooper)); //test2
        assertTrue(scn.DSHasCardChoiceAvailable(devastator)); //test1
        scn.DSChooseCard(tie);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(1, scn.GetDSUsedPileCount());
        assertEquals(Zone.TOP_OF_USED_PILE, tie.getZone()); //test3

        assertEquals(3, scn.GetDSLostPileCount());
        assertEquals(Zone.TOP_OF_LOST_PILE, something.getZone()); //test4
    }

    @Test
    public void SomethingSpecialPlannedForThemVCanAttemptRetrievalWithNoLostPile() {
        //test1: can attempt to retrieve with no cards in lost pile
        //test2: something special is sent to lost pile before retrieval attempt
        var scn = GetScenario();

        var something = scn.GetDSCard("something");

        scn.StartGame();

        scn.MoveCardsToSideOfTable(something);

        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(something, "Place")); //test1
        scn.DSUseCardAction(something, "Place");
        scn.PassAllResponses();

        assertEquals(1, scn.GetDSLostPileCount());
        assertEquals(Zone.TOP_OF_LOST_PILE, something.getZone()); //test2

        assertTrue(scn.DSDecisionAvailable("Verify")); //verify no eligible cards to retrieve
        scn.DSPass();
        scn.LSPass();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
    }


    @Test
    public void SomethingSpecialPlannedForThemVStackedCardsGoToOwnersUsedPiles() {
        //test1: grabs owner's copy of interrupt with same title
        //test2: stacked ds card goes to ds used pile
        //test3: stacked ls card goes to ls used pile
        //test4: something special is sent to lost pile before retrieval attempt
        var scn = GetScenario();

        var something = scn.GetDSCard("something");
        var ds_control = scn.GetDSCard("ds_control");
        var trooper = scn.GetDSFiller(1);

        var ls_control = scn.GetLSCard("ls_control");
        var rebelTrooper = scn.GetLSFiller(1);

        var site1 = scn.GetLSStartingLocation();
        var site2 = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToSideOfTable(something);

        scn.MoveCardsToDSHand(ds_control);
        scn.MoveCardsToLSHand(ls_control);

        scn.MoveCardsToLocation(site1, trooper);
        scn.MoveCardsToLocation(site2, rebelTrooper);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSForceDrainAt(site1);
        scn.LSPlayCard(ls_control);

        scn.DSUseCardAction(something, "Grab");
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSForceDrainAt(site2);
        scn.DSPlayCard(ds_control);
        scn.PassAllResponses();

        assertTrue(scn.IsStackedOn(something, ls_control));
        assertTrue(scn.IsStackedOn(something, ds_control)); //test1

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.DSCardActionAvailable(something, "Place"));
        scn.DSUseCardAction(something, "Place");
        scn.PassAllResponses();

        assertEquals(1, scn.GetDSUsedPileCount()); //control
        assertEquals(Zone.TOP_OF_USED_PILE, ds_control.getZone()); //test2

        assertEquals(1, scn.GetLSUsedPileCount());
        assertEquals(Zone.TOP_OF_USED_PILE, ls_control.getZone()); //test3

        assertEquals(1, scn.GetDSLostPileCount());
        assertEquals(Zone.TOP_OF_LOST_PILE, something.getZone()); //test4

        assertTrue(scn.DSDecisionAvailable("Verify")); //verify no eligible cards to retrieve
        scn.DSPass();
        scn.LSPass();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
    }

}


