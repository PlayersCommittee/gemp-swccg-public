package com.gempukku.swccgo.cards.set7.dark;

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
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_7_258_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
					put("ywing", "1_147");
					put("transport","3_065"); //medium transport
					put("rectenna","2_027"); //(device)
					put("blaster","1_152");
					put("wristComlink","7_054"); //(device)
					put("talz","1_031");
					put("kfc","1_015"); //Kal'Falnl C'ndros
				}},
				new HashMap<>() {{
					put("overwhelmed","7_258");
					put("executor","4_167");
					put("beacon","7_217"); //homing beacon (device)
				}},
				10,
				10,
				StartingSetup.DefaultLSSpaceSystem,
				StartingSetup.DefaultDSSpaceSystem,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void OverwhelmedStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Overwhelmed
		 * Uniqueness: Unrestricted
		 * Side: Dark
		 * Type: Interrupt
		 * Subtype: Lost
		 * Destiny: 5
		 * Icons: Special Edition
		 * Game Text: During your deploy phase, target a system where your total power is more than double
		 * 		opponent's total power and opponent has no Jedi or starship weapon. Place all opponent's
		 * 		starships there (and cards on them) in owner's Used Pile.
		 * Lore: When the Empire amasses its fleet, the only option for the Alliance is retreat.
		 * Set: Special Edition
		 * Rarity: C
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("overwhelmed").getBlueprint();

		assertEquals("Overwhelmed", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(5, card.getDestiny(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.INTERRUPT);
			add(Icon.SPECIAL_EDITION);
		}});
		assertEquals(ExpansionSet.SPECIAL_EDITION,card.getExpansionSet());
		assertEquals(Rarity.C, card.getRarity());
	}

	@Test
	public void OverwhelmedSendsCardsToUsedPileSimple() {
		//shows issue fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/524

		//test1: dark chooses initial selection of cards to place in used pile
		//test2: dark can choose non-ship to place in used pile
		//test3: light chooses order of cards if dark chooses a card owned by light that has attached cards
		//test4: light can choose order from a combined 'pool' of cards (card selected by dark and attached cards)
		//test5: all cards were sent to used pile and the last selected card is at the top

		var scn = GetScenario();

		var transport = scn.GetLSCard("transport");
		var talz = scn.GetLSCard("talz");
		var kfc = scn.GetLSCard("kfc");
		var trooper = scn.GetLSFiller(1);

		var overwhelmed = scn.GetDSCard("overwhelmed");
		var executor = scn.GetDSCard("executor");

		var system = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToDSHand(overwhelmed);

		scn.MoveCardsToLocation(system, executor, transport);

		scn.BoardAsPassenger(transport, talz, kfc, trooper);

		scn.SkipToPhase(Phase.DEPLOY);

		scn.DSPlayCard(overwhelmed);
		scn.DSChooseCard(system);
		assertTrue(scn.DSDecisionAvailable("all starships")); //not picking order of cards yet
		assertTrue(scn.DSHasCardChoiceAvailable(transport));
		scn.DSChooseCard(transport);
		scn.PassAllResponses();

		assertTrue(scn.DSDecisionAvailable("place on Used Pile")); //test1
		assertTrue(scn.DSHasCardChoiceAvailable(transport));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
		assertTrue(scn.DSHasCardChoiceAvailable(talz));
		assertTrue(scn.DSHasCardChoiceAvailable(kfc));

		scn.DSChooseCard(trooper); //test2

		scn.LSPass(); //ABOUT_TO_BE_PLACE_IN_CARD_PILE_FROM_TABLE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Optional responses
		scn.DSPass();

		assertTrue(scn.DSDecisionAvailable("place on Used Pile"));
		assertTrue(scn.DSHasCardChoiceAvailable(transport));
		assertFalse(scn.DSHasCardChoiceAvailable(trooper)); //(already in Used Pile)
		assertTrue(scn.DSHasCardChoiceAvailable(talz));
		assertTrue(scn.DSHasCardChoiceAvailable(kfc));

		scn.DSChooseCard(transport);

		scn.LSPass(); //ABOUT_TO_BE_PLACE_IN_CARD_PILE_FROM_TABLE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Optional responses
		scn.DSPass();

		//LS owns transport and now chooses order of cards placed in pile from transport and all attached cards
		assertTrue(scn.LSDecisionAvailable("Choose card to put on Used Pile")); //test3
		assertTrue(scn.LSHasCardChoiceAvailable(transport)); //test4
		assertTrue(scn.LSHasCardChoiceAvailable(talz));
		assertTrue(scn.LSHasCardChoiceAvailable(kfc));

		scn.LSChooseCard(talz);
		scn.LSChooseCard(transport);
		//scn.LSChooseCard(kfc); //last card automatically selected

		assertEquals(Zone.USED_PILE, trooper.getZone()); //current order of cards in Used Pile (bottom to top)
		assertEquals(Zone.USED_PILE, talz.getZone());
		assertEquals(Zone.USED_PILE, transport.getZone());
		assertEquals(Zone.TOP_OF_USED_PILE, kfc.getZone()); //test5

		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSDeployPhaseActions());
	}

	@Test @Ignore
	public void OverwhelmedSendsCardsToUsedPileComplex() {
		//check card selection with a variety of ships, characters aboard, and weapons/devices attached to those ships and characters

		//devices attached to ships being sent to used pile are not selectable for order as expected?
		//look into ownership, 'aboard' check, and the filterActive used in Overwhelmed for adding attached cards?

		var scn = GetScenario();

		var ywing = scn.GetLSCard("ywing");
		var trooper = scn.GetLSFiller(1);
		var blaster = scn.GetLSCard("blaster");
		var wristComlink = scn.GetLSCard("wristComlink");
		var transport = scn.GetLSCard("transport");
		var rectenna = scn.GetLSCard("rectenna");
		var talz = scn.GetLSCard("talz");
		var kfc = scn.GetLSCard("kfc");

		var overwhelmed = scn.GetDSCard("overwhelmed");
		var executor = scn.GetDSCard("executor");
		var beacon = scn.GetDSCard("beacon");

		var system = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToDSHand(overwhelmed);

		scn.MoveCardsToLocation(system, executor, ywing, transport);

		scn.BoardAsPassenger(ywing, trooper);
		scn.AttachCardsTo(trooper, blaster, wristComlink);

		scn.BoardAsPassenger(transport, talz, kfc);
		scn.AttachCardsTo(transport, rectenna, beacon);

		scn.SkipToPhase(Phase.DEPLOY);

		scn.DSPlayCard(overwhelmed);
		scn.DSChooseCard(system);
		assertTrue(scn.DSDecisionAvailable("all starships")); //not picking order of cards yet
		assertTrue(scn.DSHasCardChoiceAvailable(ywing));
		assertTrue(scn.DSHasCardChoiceAvailable(transport));
		assertFalse(scn.DSHasCardChoiceAvailable(trooper));
		scn.DSChooseCards(ywing, transport);
		scn.PassAllResponses();

		assertTrue(scn.DSDecisionAvailable("place on Used Pile"));
		assertTrue(scn.DSHasCardChoiceAvailable(ywing));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
		assertTrue(scn.DSHasCardChoiceAvailable(blaster));
		assertTrue(scn.DSHasCardChoiceAvailable(wristComlink));

		assertTrue(scn.DSHasCardChoiceAvailable(transport));
		///assertTrue(scn.DSHasCardChoiceAvailable(beacon)); /////// Need to investigate this...
		///assertTrue(scn.DSHasCardChoiceAvailable(rectenna)); /////// Need to investigate this...
		assertTrue(scn.DSHasCardChoiceAvailable(talz));
		assertTrue(scn.DSHasCardChoiceAvailable(kfc));

		scn.DSChooseCard(ywing);

		scn.PassAllResponses();

		//LS owns ywing and now chooses order of cards placed in pile from ywing and all attached cards
		assertTrue(scn.LSDecisionAvailable("Choose card to put on Used Pile"));
		assertTrue(scn.LSHasCardChoiceAvailable(ywing));
		assertTrue(scn.LSHasCardChoiceAvailable(trooper));
		assertTrue(scn.LSHasCardChoiceAvailable(blaster));
		assertTrue(scn.LSHasCardChoiceAvailable(wristComlink));

		assertFalse(scn.LSHasCardChoiceAvailable(transport));
		assertFalse(scn.LSHasCardChoiceAvailable(beacon));
		assertFalse(scn.LSHasCardChoiceAvailable(rectenna));
		assertFalse(scn.LSHasCardChoiceAvailable(talz));
		assertFalse(scn.LSHasCardChoiceAvailable(kfc));

		scn.LSChooseCard(trooper);

		assertTrue(scn.LSDecisionAvailable("Choose card to put on Used Pile"));
		assertTrue(scn.LSHasCardChoiceAvailable(ywing));
		assertFalse(scn.LSHasCardChoiceAvailable(trooper)); //(already in Used Pile)
		assertTrue(scn.LSHasCardChoiceAvailable(blaster));
		assertTrue(scn.LSHasCardChoiceAvailable(wristComlink));

		scn.LSChooseCard(blaster);
		scn.LSChooseCard(ywing);
		//scn.LSChooseCard(wristComlink); //last card automatically selected

		assertEquals(Zone.USED_PILE, trooper.getZone()); //current order of cards in Used Pile (bottom to top)
		assertEquals(Zone.USED_PILE, blaster.getZone());
		assertEquals(Zone.USED_PILE, ywing.getZone());
		assertEquals(Zone.TOP_OF_USED_PILE, wristComlink.getZone());

		scn.PassAllResponses();

		assertTrue(scn.DSDecisionAvailable("place on Used Pile"));
		assertFalse(scn.DSHasCardChoiceAvailable(ywing)); //(ywing and its attached cards already in Used Pile)

		assertTrue(scn.DSHasCardChoiceAvailable(transport));
		///assertTrue(scn.DSHasCardChoiceAvailable(beacon)); /////// Need to investigate this...
		///assertTrue(scn.DSHasCardChoiceAvailable(rectenna)); /////// Need to investigate this...
		assertTrue(scn.DSHasCardChoiceAvailable(talz));
		assertTrue(scn.DSHasCardChoiceAvailable(kfc));

		scn.DSChooseCard(talz);
		scn.PassAllResponses();

		assertTrue(scn.DSDecisionAvailable("place on Used Pile"));
		assertTrue(scn.DSHasCardChoiceAvailable(transport));
		///assertTrue(scn.DSHasCardChoiceAvailable(beacon)); /////// Need to investigate this...
		///assertTrue(scn.DSHasCardChoiceAvailable(rectenna)); /////// Need to investigate this...
		assertFalse(scn.DSHasCardChoiceAvailable(talz)); //(already in Used Pile)
		assertTrue(scn.DSHasCardChoiceAvailable(kfc));

		scn.DSChooseCard(transport);
		///somehow right here, beacon is being automatically sent to DS Used Pile
		scn.PassAllResponses();

		//LS owns transport and now chooses order of cards placed in pile from transport and all attached cards
		assertTrue(scn.LSDecisionAvailable("Choose card to put on Used Pile"));
		assertTrue(scn.LSHasCardChoiceAvailable(transport));
		///assertTrue(scn.LSHasCardChoiceAvailable(beacon)); /////// Need to investigate this...
		assertTrue(scn.LSHasCardChoiceAvailable(rectenna));
		assertTrue(scn.LSHasCardChoiceAvailable(kfc));

		scn.LSChooseCard(transport);
		scn.LSChooseCard(rectenna);
		//scn.LSChooseCard(kfc); //last card automatically selected

		assertEquals(Zone.USED_PILE, trooper.getZone()); //current order of cards in Used Pile (bottom to top)
		assertEquals(Zone.USED_PILE, blaster.getZone());
		assertEquals(Zone.USED_PILE, ywing.getZone());
		assertEquals(Zone.USED_PILE, wristComlink.getZone());
		assertEquals(Zone.USED_PILE, talz.getZone());
		assertEquals(Zone.USED_PILE, transport.getZone());
		assertEquals(Zone.USED_PILE, rectenna.getZone());
		assertEquals(Zone.TOP_OF_USED_PILE, kfc.getZone());

		assertEquals(Zone.TOP_OF_USED_PILE, beacon.getZone());

		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSDeployPhaseActions());
	}

	//add test for inactive cards

}


