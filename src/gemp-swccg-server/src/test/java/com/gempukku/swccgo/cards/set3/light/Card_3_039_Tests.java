package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_3_039_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
					put("first","3_039"); //the first transport is away!
					put("luminous","9_077");
					put("hoth","3_055");
					put("ord","3_064"); //ord mantell
					put("hoth_db","3_059"); //hoth: echo docking bay
				}},
				new HashMap<>() {{
					put("something_special","9_132"); //something special planned for them
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
	public void TheFirstTransportIsAwayStatsAndKeywordsAreCorrect() {
		/**
		 * Title: The First Transport Is Away!
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Effect
		 * Subtype: Utinni
		 * Destiny: 4
		 * Icons: Hoth
		 * Game Text: Deploy on any system (except Hoth). Target a Medium Transport at a Hoth site.
		 * 		When reached by target: Retrieve X Force, where X = twice the number of passengers.
		 * 		Relocate Utinni Effect to Hoth system. Your total power is +2 in battles at Hoth sites.
		 * Lore: 'When you've gotten past the energy shield, proceed directly to the rendezvous point. Understood? Good luck!'
		 * Set: Hoth
		 * Rarity: R1
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("first").getBlueprint();

		assertEquals(Title.The_First_Transport_Is_Away, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.EFFECT);
		}});
		assertEquals(CardSubtype.UTINNI, card.getCardSubtype());
		assertEquals(4, card.getDestiny(), scn.epsilon);
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
			add(Keyword.UTINNI_EFFECT_THAT_RETRIEVES_FORCE);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.EFFECT);
			add(Icon.HOTH);
		}});
		assertEquals(ExpansionSet.HOTH,card.getExpansionSet());
		assertEquals(Rarity.R1, card.getRarity());
	}

	@Test
	public void TheFirstTransportIsAwayRetrievesXForce() {
		var scn = GetScenario();

		var first = scn.GetLSCard("first");
		var luminous = scn.GetLSCard("luminous");
		var hoth = scn.GetLSCard("hoth");
		var ord = scn.GetLSCard("ord");
		var hoth_db = scn.GetLSCard("hoth_db");
		var rebelTrooper = scn.GetLSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLSHand(first,rebelTrooper);

		scn.MoveLocationToTable(hoth_db);
		scn.MoveLocationToTable(hoth);
		scn.MoveLocationToTable(ord);

		scn.MoveCardsToLocation(hoth_db,luminous);

		//some cards in lost pile so we can confirm retrieval works
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(rebelTrooper);
		scn.LSChooseCard(luminous); //as passenger
		scn.PassAllResponses();
		scn.DSPass();

		assertTrue(scn.LSCardPlayAvailable(first));
		scn.LSPlayCard(first);

		assertTrue(scn.LSHasCardChoiceAvailable(ord)); //a non-hoth system
		assertFalse(scn.LSHasCardChoiceAvailable(hoth));
		scn.LSChooseCard(ord);

		assertTrue(scn.LSHasCardChoiceAvailable(luminous)); //a medium transport at hoth
		scn.LSChooseCard(luminous);

		scn.PassAllResponses();
		assertTrue(scn.IsAttachedTo(ord,first));
		assertTrue(scn.AwaitingDSDeployPhaseActions());

		scn.SkipToPhase(Phase.MOVE);
		assertTrue(scn.LSCardActionAvailable(luminous,"Take off"));
		scn.LSUseCardAction(luminous,"Take off");
		assertTrue(scn.LSDecisionAvailable("Choose"));
		scn.LSChooseCard(hoth);
		scn.PassAllResponses();
		assertTrue(scn.CardsAtLocation(hoth,luminous));

		scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.MOVE);
		assertTrue(scn.LSCardActionAvailable(luminous,"hyperspeed"));
		scn.LSUseCardAction(luminous,"hyperspeed");
		scn.LSChooseCard(ord);

		assertEquals(3,scn.GetLSLostPileCount());

		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSMovePhaseActions());

		assertEquals(1,scn.GetLSLostPileCount()); //retrieved 2 (X = 2 * 1 passenger)
		assertTrue(scn.IsAttachedTo(hoth,first));
	}

	@Test
	public void TheFirstTransportIsAwayDoesNotRelocateToHothIfPlacedOutOfPlay() {
		var scn = GetScenario();

		var first = scn.GetLSCard("first");
		var luminous = scn.GetLSCard("luminous");
		var hoth = scn.GetLSCard("hoth");
		var ord = scn.GetLSCard("ord");
		var hoth_db = scn.GetLSCard("hoth_db");
		var rebelTrooper = scn.GetLSFiller(1);

		var something_special = scn.GetDSCard("something_special");

		scn.StartGame();

		scn.MoveCardsToDSSideOfTable(something_special);

		scn.MoveCardsToLSHand(first,rebelTrooper);

		scn.MoveLocationToTable(hoth_db);
		scn.MoveLocationToTable(hoth);
		scn.MoveLocationToTable(ord);

		scn.MoveCardsToLocation(hoth_db,luminous);

		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(rebelTrooper);
		scn.LSChooseCard(luminous); //as passenger
		scn.PassAllResponses();
		scn.DSPass();

		assertTrue(scn.LSCardPlayAvailable(first));
		scn.LSPlayCard(first);

		assertTrue(scn.LSHasCardChoiceAvailable(ord)); //a non-hoth system
		assertFalse(scn.LSHasCardChoiceAvailable(hoth));
		scn.LSChooseCard(ord);

		assertTrue(scn.LSHasCardChoiceAvailable(luminous)); //a medium transport at hoth
		scn.LSChooseCard(luminous);

		scn.PassAllResponses();
		assertTrue(scn.IsAttachedTo(ord,first));
		assertTrue(scn.AwaitingDSDeployPhaseActions());

		scn.SkipToPhase(Phase.MOVE);
		assertTrue(scn.LSCardActionAvailable(luminous,"Take off"));
		scn.LSUseCardAction(luminous,"Take off");
		assertTrue(scn.LSDecisionAvailable("Choose"));
		scn.LSChooseCard(hoth);
		scn.PassAllResponses();
		assertTrue(scn.CardsAtLocation(hoth,luminous));

		scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.MOVE);
		assertTrue(scn.LSCardActionAvailable(luminous,"hyperspeed"));
		scn.LSUseCardAction(luminous,"hyperspeed");
		scn.LSChooseCard(ord);

		assertEquals(3,scn.GetLSLostPileCount());

		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSMovePhaseActions());

		assertEquals(1,scn.GetLSLostPileCount()); //retrieved 2 (X = 2 * 1 passenger)
		assertFalse(scn.IsAttachedTo(hoth,first)); //since was sent oop by something special
		assertEquals(Zone.OUT_OF_PLAY,first.getZone());
	}
}


