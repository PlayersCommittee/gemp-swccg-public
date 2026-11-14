package com.gempukku.swccgo.cards.set13.dark;

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

public class Card_13_070_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
                put("qui", "11_10"); // Qui-Gon Jinn (Jedi)
                put("obiwan", "13_33"); // Obi-Wan Kenobi, Jedi Knight
                put("lsCombat4", "1_105"); // Rebel Barrier destiny 4
                put("lsCombat5", "1_102"); // Out Of Nowhere destiny 5
                put("lsCombat5b", "1_102");
                put("sense", "1_109");
                put("strikeBlocked", "13_43");
                put("ihabfat", "4_052");
            }},
            new HashMap<>() {{
                put("forcePush", "13_70");
                put("maul", "11_54"); // Darth Maul (Dark Jedi)
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
    public void ForcePushStatsAndKeywordsAreCorrect() {
        /**
         * Title: Force Push
         * Uniqueness: Unique
         * Side: Dark
         * Type: Interrupt
         * Subtype: Used
         * Destiny: 5
         * Icons: Episode I, Reflections III
         * Game Text: Target opponent's Jedi with at least one combat card present with your Dark Jedi.
         *          Reveal one of target's combat cards (random selection). If revealed card's destiny > 4,
         *          place it on top of opponent's Reserve Deck. Otherwise, lose 1 Force. (Immune to Sense.)
         * Lore: A fully-trained Sith warrior has more weapons at his disposal than just a lightsaber.
         * Set: Reflections III
         * Rarity: PM
         */

        var scn = GetScenario();
        var card = scn.GetDSCard("forcePush").getBlueprint();

        assertEquals("Force Push", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REFLECTIONS_III);
            add(Icon.INTERRUPT);
            add(Icon.EPISODE_I);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.REFLECTIONS_III, card.getExpansionSet());
        assertEquals(Rarity.PM, card.getRarity());
    }

    @Test
    public void ForcePushPlayableWhenOpponentsJediHasCombatCardPresentWithDarkJedi() {
        var scn = GetScenario();

        var qui = scn.GetLSCard("qui");
        var lsCombat4 = scn.GetLSCard("lsCombat4");
        var site = scn.GetLSStartingLocation();

        var forcePush = scn.GetDSCard("forcePush");
        var maul = scn.GetDSCard("maul");

        scn.StartGame();
        scn.MoveCardsToLocation(site, qui, maul);
        scn.MoveCardsToDSHand(forcePush);

        scn.StackCardsOn(qui, true, lsCombat4);
        lsCombat4.setCombatCard(true);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(forcePush));
    }

    @Test
    public void ForcePushNotPlayableWhenJediHasNoCombatCards() {
        var scn = GetScenario();

        var qui = scn.GetLSCard("qui");
        var site = scn.GetLSStartingLocation();

        var forcePush = scn.GetDSCard("forcePush");
        var maul = scn.GetDSCard("maul");

        scn.StartGame();
        scn.MoveCardsToLocation(site, qui, maul);
        scn.MoveCardsToDSHand(forcePush);

        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(forcePush));
    }

    @Test
    public void ForcePushNotPlayableWhenJediHasStackedNonCombatCard() {
        var scn = GetScenario();

        var qui = scn.GetLSCard("qui");
        var rebelTrooper = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        var forcePush = scn.GetDSCard("forcePush");
        var maul = scn.GetDSCard("maul");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToLocation(site, qui, maul);
        scn.MoveCardsToDSHand(forcePush);

        scn.StackCardsOn(qui, true, rebelTrooper);

        scn.StackCardsOn(qui, true, trooper);
        trooper.setHatredCard(true);

        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(forcePush));
    }

    @Test
    public void ForcePushNotPlayableWhenJediNotPresentWithDarkJedi() {
        var scn = GetScenario();

        var qui = scn.GetLSCard("qui");
        var lsCombat4 = scn.GetLSCard("lsCombat4");
        var site = scn.GetLSStartingLocation();

        var forcePush = scn.GetDSCard("forcePush");
        var maul = scn.GetDSCard("maul");
        var site2 = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, qui);
        scn.MoveCardsToLocation(site2, maul);
        scn.MoveCardsToDSHand(forcePush);

        scn.StackCardsOn(qui, true, lsCombat4);
        lsCombat4.setCombatCard(true);

        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(forcePush));
    }

    @Test
    public void ForcePushDestinyFourOrLessLosesOneForceAndCombatCardStaysStacked() {
        var scn = GetScenario();

        var qui = scn.GetLSCard("qui");
        var lsCombat4 = scn.GetLSCard("lsCombat4");
        var site = scn.GetLSStartingLocation();

        var forcePush = scn.GetDSCard("forcePush");
        var maul = scn.GetDSCard("maul");

        scn.StartGame();
        scn.MoveCardsToLocation(site, qui, maul);
        scn.MoveCardsToDSHand(forcePush);

        scn.StackCardsOn(qui, true, lsCombat4);
        lsCombat4.setCombatCard(true);

        assertEquals(4, lsCombat4.getBlueprint().getDestiny(), scn.epsilon);

        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(0, scn.GetDSLostPileCount());
        assertTrue(scn.DSCardPlayAvailable(forcePush));
        scn.DSPlayCard(forcePush);
        scn.DSChooseCard(qui);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSForceLossPayment());
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(Zone.TOP_OF_USED_PILE, forcePush.getZone());
        assertEquals(1, scn.GetDSLostPileCount()); //1 force loss
        assertTrue(scn.IsStackedOn(qui, lsCombat4));
    }

    @Test
    public void ForcePushDestinyGreaterThanFourPlacesCombatCardOnOpponentsReserveDeck() {
        var scn = GetScenario();

        var qui = scn.GetLSCard("qui");
        var lsCombat5 = scn.GetLSCard("lsCombat5");
        var site = scn.GetLSStartingLocation();

        var forcePush = scn.GetDSCard("forcePush");
        var maul = scn.GetDSCard("maul");

        scn.StartGame();
        scn.MoveCardsToLocation(site, qui, maul);
        scn.MoveCardsToDSHand(forcePush);

        scn.StackCardsOn(qui, true, lsCombat5);
        lsCombat5.setCombatCard(true);

        assertEquals(5, lsCombat5.getBlueprint().getDestiny(), scn.epsilon);

        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(0, scn.GetDSLostPileCount());
        assertTrue(scn.DSCardPlayAvailable(forcePush));
        scn.DSPlayCard(forcePush);
        scn.DSChooseCard(qui);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(Zone.TOP_OF_USED_PILE, forcePush.getZone());
        assertEquals(0, scn.GetDSLostPileCount());
        assertFalse(scn.IsStackedOn(qui, lsCombat5));
        assertEquals(lsCombat5, scn.GetTopOfLSReserveDeck());
    }

    @Test
    public void ForcePushWithTwoCombatCardsOnlySendsOneToReserveDeck() {
        var scn = GetScenario();

        var qui = scn.GetLSCard("qui");
        var lsCombat5 = scn.GetLSCard("lsCombat5");
        var lsCombat5b = scn.GetLSCard("lsCombat5b");
        var site = scn.GetLSStartingLocation();

        var forcePush = scn.GetDSCard("forcePush");
        var maul = scn.GetDSCard("maul");

        scn.StartGame();
        scn.MoveCardsToLocation(site, qui, maul);
        scn.MoveCardsToDSHand(forcePush);

        scn.StackCardsOn(qui, true, lsCombat5, lsCombat5b);
        lsCombat5.setCombatCard(true);
        lsCombat5b.setCombatCard(true);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPlayCard(forcePush);
        scn.DSChooseCard(qui);
        scn.PassAllResponses();

        //random selection, so use XOR to confirm 1 combat card stayed on qui and 1 combat card was moved to reserve
        assertTrue(scn.IsStackedOn(qui, lsCombat5) ^ scn.IsStackedOn(qui, lsCombat5b));
        assertTrue(scn.GetTopOfLSReserveDeck().equals(lsCombat5) ^ scn.GetTopOfLSReserveDeck().equals(lsCombat5b));
    }

    @Test
    public void ForcePushDoesNotLetStrikeBlockedCancelTheRevealedCombatCard() {
        //Strike Blocked only responds to combat destiny revealed during lightsaber combat
        var scn = GetScenario();

        var qui = scn.GetLSCard("qui");
        var lsCombat5 = scn.GetLSCard("lsCombat5");
        var strikeBlocked = scn.GetLSCard("strikeBlocked");
        var site = scn.GetLSStartingLocation();

        var forcePush = scn.GetDSCard("forcePush");
        var maul = scn.GetDSCard("maul");

        scn.StartGame();
        scn.MoveCardsToLocation(site, qui, maul);
        scn.MoveCardsToDSHand(forcePush);
        scn.MoveCardsToLSHand(strikeBlocked);

        scn.StackCardsOn(qui, true, lsCombat5);
        lsCombat5.setCombatCard(true);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPlayCard(forcePush);
        scn.DSChooseCard(qui);

        assertFalse(scn.LSCardPlayAvailable(strikeBlocked, "Cancel attempt to use combat card"));
        scn.LSPass(); //Playing Force Push - Optional responses
        scn.DSPass();

        assertTrue(scn.LSDecisionAvailable("REMOVED_FROM_STACKED - Optional responses"));
        //passed response window
    }

    @Test
    public void ForcePushMayBeRetargetedToAnotherJediWithCombatCards() {
        var scn = GetScenario();

        var ihabfat = scn.GetLSCard("ihabfat");
        var qui = scn.GetLSCard("qui");
        var obiwan = scn.GetLSCard("obiwan");
        var lsCombat5 = scn.GetLSCard("lsCombat5");
        var lsCombat5b = scn.GetLSCard("lsCombat5b");
        var site = scn.GetLSStartingLocation();

        var forcePush = scn.GetDSCard("forcePush");
        var maul = scn.GetDSCard("maul");

        scn.StartGame();
        scn.MoveCardsToLocation(site, qui, obiwan, maul);
        scn.MoveCardsToDSHand(forcePush);
        scn.MoveCardsToLSHand(ihabfat);
        scn.StackCardsOn(qui, true, lsCombat5);
        lsCombat5.setCombatCard(true);
        scn.StackCardsOn(obiwan, true, lsCombat5b);
        lsCombat5b.setCombatCard(true);
        scn.LSActivateForceCheat(3);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPlayCard(forcePush);
        assertTrue(scn.DSHasCardChoicesAvailable(qui, obiwan));
        scn.DSChooseCard(qui);

        assertTrue(scn.LSCardPlayAvailable(ihabfat));
        scn.LSPlayCard(ihabfat);
        assertTrue(scn.LSHasCardChoiceAvailable(qui));
        scn.LSChooseCard(qui);
        assertTrue(scn.LSHasCardChoiceAvailable(obiwan));
        assertFalse(scn.LSHasCardChoiceAvailable(qui));
        scn.LSChooseCard(obiwan);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.IsStackedOn(qui, lsCombat5));
        assertFalse(scn.IsStackedOn(qui, lsCombat5b));
        assertEquals(lsCombat5b, scn.GetTopOfLSReserveDeck());
    }

    @Test
    public void ForcePushIsImmuneToSense() {
        var scn = GetScenario();

        var qui = scn.GetLSCard("qui");
        var lsCombat4 = scn.GetLSCard("lsCombat4");
        var sense = scn.GetLSCard("sense");
        var site = scn.GetLSStartingLocation();

        var forcePush = scn.GetDSCard("forcePush");
        var maul = scn.GetDSCard("maul");

        scn.StartGame();
        scn.MoveCardsToLocation(site, qui, maul);
        scn.MoveCardsToDSHand(forcePush);
        scn.MoveCardsToLSHand(sense);

        scn.StackCardsOn(qui, true, lsCombat4);
        lsCombat4.setCombatCard(true);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPlayCard(forcePush);
        scn.DSChooseCard(qui);

        //Playing Force Push - Optional responses
        assertFalse(scn.LSCardPlayAvailable(sense));
    }
}
