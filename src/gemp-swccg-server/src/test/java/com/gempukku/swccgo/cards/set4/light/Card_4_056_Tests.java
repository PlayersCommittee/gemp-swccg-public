package com.gempukku.swccgo.cards.set4.light;

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

public class Card_4_056_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("relay", "4_056"); //Lost Relay
                    put("relay2", "4_056"); //Lost Relay
                    put("corvette", "1_140"); //corellian corvette
                    put("kessel","1_126"); //rebel guard
                    put("asteroid","4_081"); //asteroid sector
                    put("asteroid2","4_081"); //asteroid sector
                }},
				new HashMap<>()
				{{
                    put("tie","1_304");
                    put("tie_scout","1_305");
                    put("vengeance","7_310"); //star destroyer with +tie forfeit
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
	public void LostRelayStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Lost Relay
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Interrupt
		 * Subtype: Used
		 * Destiny: 5
		 * Icons: Interrupt, Hoth
		 * Game Text: Target one opponent's starfighter present with one of your starships at an asteroid sector,
         *      before asteroid destiny is drawn this turn. If target lost this turn due to asteroid destiny,
         *      opponent also loses Force equal to target's forfeit value.
		 * Lore: Asteroid fields require starfighters to act as comm relays, forwarding orders to the squadron via
         *      subspace AE-35 Transceivers. If ships are lost, communications break down.
		 * Set: Dagobah
		 * Rarity: C
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("relay").getBlueprint();

		assertEquals("Lost Relay", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
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
            add(Icon.DAGOBAH);
        }});
        assertEquals(ExpansionSet.DAGOBAH,card.getExpansionSet());
        assertEquals(Rarity.C,card.getRarity());

	}

	@Test
	public void LostRelayBasicFunctionalTest() {
        //this tests the basic working conditions for the card
        //test1: able to play before asteroid destiny is drawn
        //test2: able to target eligible starfighter
        //test3: after target lost to asteroid destiny, force must be lost
        //test4: total force loss = starship's forfeit (2)

		var scn = GetScenario();

		var relay = scn.GetLSCard("relay");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");

        var tie = scn.GetDSCard("tie");

		scn.StartGame();

		scn.MoveCardsToLSHand(relay);

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.MoveCardsToLocation(asteroid, corvette, tie);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertTrue(scn.LSCardPlayAvailable(relay)); //test1
        scn.LSPlayCard(relay);
        assertTrue(scn.LSHasCardChoiceAvailable(tie)); //test2
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        scn.DSPass();

        scn.PrepareLSDestiny(7); //for successful asteroid destiny draw

        assertTrue(scn.LSCardActionAvailable(tie, "asteroid")); //draw asteroid destiny against tie
        scn.LSUseCardAction(tie, "asteroid");
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE,tie.getZone());
        assertTrue(scn.AwaitingDSForceLossPayment()); //test3
        scn.DSPayRemainingForceLossFromReserveDeck();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(3, scn.GetDSLostPileCount()); //test4: (tie + 2 force loss)
    }

    @Test
    public void LostRelayCausesForceLossIfTargetLostToAsteroidSectorDrawTest() {
        //test1: after target lost to asteroid destiny = asteroid sector, force must be lost
        //test2: total force loss = starship's forfeit (2)

        var scn = GetScenario();

        var relay = scn.GetLSCard("relay");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");
        var asteroid2 = scn.GetLSCard("asteroid2");

        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLSHand(relay);

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.MoveCardsToLocation(asteroid, asteroid2, corvette, tie);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        scn.LSPlayCard(relay);
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        scn.DSPass();

        scn.MoveCardsToTopOfLSReserveDeck(asteroid2); //for successful asteroid destiny draw

        scn.LSUseCardAction(tie, "asteroid"); //draw asteroid destiny against tie
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE,tie.getZone());
        assertTrue(scn.AwaitingDSForceLossPayment()); //test1
        scn.DSPayRemainingForceLossFromReserveDeck();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(3, scn.GetDSLostPileCount()); //test2: (tie + 2 force loss)
    }

    @Test
    public void LostRelayDoesNotCauseForceLossIfFailedAsteroidDestinyTest() {
        //test1: if target not lost to asteroid destiny, nothing is lost

        var scn = GetScenario();

        var relay = scn.GetLSCard("relay");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");

        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLSHand(relay);

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.MoveCardsToLocation(asteroid, corvette, tie);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertTrue(scn.LSCardPlayAvailable(relay)); //test1
        scn.LSPlayCard(relay);
        assertTrue(scn.LSHasCardChoiceAvailable(tie)); //test2
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        scn.DSPass();

        scn.PrepareLSDestiny(1); //for failed asteroid destiny draw

        scn.LSUseCardAction(tie, "asteroid"); //draw asteroid destiny against tie
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(asteroid,tie,corvette));
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(0, scn.GetDSLostPileCount()); //test1
    }

    @Test
    public void LostRelayNotPlayableAfterAsteroidDestinyDrawnTest() {
        //test1: playable before any asteroid destiny is drawn
        //test2: not playable after first asteroid destiny is drawn

        var scn = GetScenario();

        var relay = scn.GetLSCard("relay");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");

        var tie = scn.GetDSCard("tie");
        var tie_scout = scn.GetDSCard("tie_scout");

        scn.StartGame();

        scn.MoveCardsToLSHand(relay);

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.MoveCardsToLocation(asteroid, corvette, tie, tie_scout);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        scn.PrepareLSDestiny(1); //for failed asteroid destiny draw

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardActionAvailable(tie, "asteroid")); //draw asteroid destiny against
        assertTrue(scn.LSCardActionAvailable(tie_scout, "asteroid"));
        assertTrue(scn.LSCardPlayAvailable(relay)); //test1
        scn.LSUseCardAction(tie_scout, "asteroid");
        scn.PassAllResponses();
        scn.DSPass();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardActionAvailable(tie, "asteroid"));
        assertFalse(scn.LSCardActionAvailable(tie_scout, "asteroid")); //already drew
        assertFalse(scn.LSCardPlayAvailable(relay)); //test2
    }

    @Test
    public void LostRelayNotPlayableIfNotAtAsteroidSectorTest() {
        //test1: not playable with opponent's starfighter present with your starship, but not at non-asteroid sector

        var scn = GetScenario();

        var relay = scn.GetLSCard("relay");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");

        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLSHand(relay);

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.MoveCardsToLocation(kessel, corvette, tie);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardPlayAvailable(relay)); //test1
    }

    @Test
    public void LostRelayNotPlayableIfYourShipNotPresentTest() {
        //test1: not playable with opponent's starfighter at asteroid sector, but not present with your starship

        var scn = GetScenario();

        var relay = scn.GetLSCard("relay");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");

        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLSHand(relay);

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.MoveCardsToLocation(asteroid, tie);
        scn.MoveCardsToLocation(kessel, corvette);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardPlayAvailable(relay)); //test1
    }

    @Test
    public void LostRelayPlayableDuringOwnerTurnTest() {
        //test1: playable during your turn (even though asteroid destiny won't be drawn against the target)

        var scn = GetScenario();

        var relay = scn.GetLSCard("relay");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");

        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLSHand(relay, corvette);
        scn.MoveCardsToDSHand(tie);

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.SkipToLSTurn(Phase.ACTIVATE);

        scn.MoveCardsToLocation(asteroid, corvette, tie);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(relay)); //test1
    }

    @Test
    public void LostRelayForceLossIncludesForfeitModifiersOnTargetWhenLostTest() {
        //test1: cannot target non-starfighter starship
        //test2: force loss matches modified forfeit value (+2 from Vengeance)

        var scn = GetScenario();

        var relay = scn.GetLSCard("relay");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");

        var tie = scn.GetDSCard("tie");
        var vengeance = scn.GetDSCard("vengeance");

        scn.StartGame();

        scn.MoveCardsToLSHand(relay);

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.MoveCardsToLocation(asteroid, corvette, tie, vengeance);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        scn.LSPlayCard(relay);
        assertTrue(scn.LSHasCardChoiceAvailable(tie));
        assertFalse(scn.LSHasCardChoiceAvailable(vengeance)); //test1
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        scn.DSPass();

        scn.PrepareLSDestiny(7); //for successful asteroid destiny draw

        scn.LSUseCardAction(tie, "asteroid"); //draw asteroid destiny against tie
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE,tie.getZone());
        scn.DSPayRemainingForceLossFromReserveDeck();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(5, scn.GetDSLostPileCount()); //test2: (tie + 4 force loss)
    }

    @Test
    public void LostRelayTargetLostInBattleDoesNotCauseForceLossTest() {
        //test1:

        var scn = GetScenario();

        var relay = scn.GetLSCard("relay");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");

        var tie = scn.GetDSCard("tie");
        var tie_scout = scn.GetDSCard("tie_scout");

        scn.StartGame();

        scn.MoveCardsToLSHand(relay, corvette);
        scn.MoveCardsToDSHand(tie, tie_scout);

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.SkipToPhase(Phase.DEPLOY);

        scn.MoveCardsToLocation(asteroid, corvette, tie, tie_scout);
        scn.DSPass();

        scn.LSPlayCard(relay);
        scn.LSChooseCard(tie);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(asteroid);
        scn.PassAllResponses();
        scn.SkipToDamageSegment();

        assertTrue(scn.AwaitingDSBattleDamagePayment());
        assertEquals(3, scn.GetUnpaidDSBattleDamage()); //power 5 (corvette) - power 2 (tie + tie scout)
        scn.DSChooseCard(tie); //forfeit for 2
        scn.PassAllResponses();

        assertEquals(1, scn.GetUnpaidDSBattleDamage());
        scn.DSChooseCard(tie_scout);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertEquals(2, scn.GetDSLostPileCount()); //test1: (tie + tie scout), no force loss
    }

    @Test
    public void LostRelayMultipleOnlyCausesOneInstanceOfForceLossTest() {
        //test1: playing 2 copies with the same target only causes one instance of force loss after the target is lost to asteroid destiny

        var scn = GetScenario();

        var relay = scn.GetLSCard("relay");
        var relay2 = scn.GetLSCard("relay2");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");

        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLSHand(relay, relay2);

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.MoveCardsToLocation(asteroid, corvette, tie);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        scn.LSPlayCard(relay);
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSPlayCard(relay2);
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        scn.DSPass();

        scn.PrepareLSDestiny(7); //for successful asteroid destiny draw

        scn.LSUseCardAction(tie, "asteroid"); //draw asteroid destiny against tie
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE,tie.getZone());
        assertTrue(scn.AwaitingDSForceLossPayment());
        scn.DSPayRemainingForceLossFromReserveDeck();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(3, scn.GetDSLostPileCount()); //test1: (tie + 2 force loss)
    }

    @Test
    public void LostRelayOnlyLastsUntilEndOfCurrentTurnTest() {
        //test1: if targeted starfighter is lost to asteroid destiny on future turns, no force lost

        var scn = GetScenario();

        var relay = scn.GetLSCard("relay");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");

        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLSHand(relay);

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);

        scn.MoveCardsToLocation(asteroid, corvette, tie);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        scn.LSPlayCard(relay);
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        scn.DSPass();

        scn.PrepareLSDestiny(0); //for failed asteroid destiny draw
        scn.LSUseCardAction(tie, "asteroid"); //draw asteroid destiny against tie
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();

        scn.PrepareDSDestiny(0); //for failed asteroid destiny draw
        scn.DSUseCardAction(corvette, "asteroid"); //draw asteroid destiny against corvette
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPass();

        scn.PrepareLSDestiny(7); //for successful asteroid destiny draw
        scn.LSUseCardAction(tie, "asteroid"); //draw asteroid destiny against tie
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE,tie.getZone());
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(1, scn.GetDSLostPileCount()); //test1:
    }

}
