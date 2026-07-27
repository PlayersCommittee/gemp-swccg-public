package com.gempukku.swccgo.cards.set1.light;

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
import static org.junit.Assert.assertTrue;

public class Card_1_052_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
					put("kessel_run","1_052");
					put("kessel","1_126");
					put("luminous","9_077");
					put("hoth","3_055");
					put("han","1_011"); //(smuggler)
					put("rycar","1_063"); //Rycar Ryjerd
					put("blues","4_038"); //Smuggler's Blues
					put("kessel_run2","1_052");
				}},
				new HashMap<>() {{
					put("clearance","13_066"); //Do They Have A Code Clearance? (defensive shield)
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
	public void KesselRunStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Kessel Run
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Effect
		 * Subtype: Utinni
		 * Destiny: 5
		 * Icons:
		 * Game Text: Deploy on Kessel. Target one of your smugglers at another system. X = parsec distance between
		 * 		the two systems. When target reaches Kessel, opponent draws destiny. If = 0, starship lost.
		 * 		Otherwise, by returning to first system, 'retrieve' X Lost Force.
		 * Lore: Planet Kessel has infamous glitterstim spice mines attracting smugglers and pirates. A 'Kessel run'
		 * 		is a long, dangerous hyper-route they must travel quickly.
		 * Set: Premiere
		 * Rarity: R2
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("kessel_run").getBlueprint();

		assertEquals(Title.Kessel_Run, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.EFFECT);
		}});
		assertEquals(CardSubtype.UTINNI, card.getCardSubtype());
		assertEquals(5, card.getDestiny(), scn.epsilon);
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
			add(Keyword.UTINNI_EFFECT_THAT_RETRIEVES_FORCE);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.EFFECT);
		}});
		assertEquals(ExpansionSet.PREMIERE,card.getExpansionSet());
		assertEquals(Rarity.R2, card.getRarity());
	}

	@Test
	public void KesselRunRetrievesXForce() {
		var scn = GetScenario();

		var kessel_run = scn.GetLSCard("kessel_run");
		var kessel = scn.GetLSCard("kessel");
		var han = scn.GetLSCard("han");
		var luminous = scn.GetLSCard("luminous");
		var hoth = scn.GetLSCard("hoth");

		scn.StartGame();

		scn.MoveCardsToLSHand(kessel_run, han);

		scn.MoveLocationToTable(hoth);
		scn.MoveLocationToTable(kessel);

		scn.MoveCardsToLocation(hoth, luminous);

		//some cards in lost pile so we can confirm retrieval works
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(han);
		scn.LSChooseCard(luminous); //as pilot
		scn.LSChoose("Pilot");
		scn.PassAllResponses();
		scn.DSPass();

		scn.LSDeployCard(kessel_run);
		scn.LSChooseCard(kessel);
		scn.LSChooseCard(han);
		scn.PassAllResponses();

		scn.PrepareDSDestiny(1); //non-zero to avoid making starship lost
		assertEquals(0, scn.GetDSUsedPileCount());

		scn.SkipToPhase(Phase.MOVE);
		scn.LSUseCardAction(luminous,"hyperspeed");
		scn.LSChooseCard(kessel);
		scn.PassAllResponses();

		assertEquals(1, scn.GetDSUsedPileCount()); //dark drew kessel run destiny
		assertTrue(scn.CardsAtLocation(kessel,luminous)); //starship was not lost

			//unclear why scn.SkipToDSTurn() does not succeed here (fails after 20 pass attempts)
		scn.DSPass(); //move phase
		scn.LSPass();

		scn.LSPass(); //draw phase
		scn.DSPass();

		scn.PassAllResponses(); //recirculate

		//scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.MOVE);
		scn.LSUseCardAction(luminous,"hyperspeed");
		scn.LSChooseCard(hoth);

		assertEquals(4,scn.GetLSLostPileCount());

		scn.PassAllResponses();

		assertEquals(2,scn.GetLSLostPileCount()); //retrieved 3 (parsec 8 - 5 = 3), lost kessel run
		assertEquals(Zone.TOP_OF_LOST_PILE,kessel_run.getZone());
	}

	@Test
	public void KesselRunRetrievesXForceToHandWithSmugglersBlues() {
		//with Smuggler's Blues conditions met, player has option to decide for each force retrieved
		//whether it goes to hand or used pile.
		//test1: retrieved force can be taken into hand
		//test2: retrieved force can be sent to used pile
		var scn = GetScenario();

		var kessel_run = scn.GetLSCard("kessel_run");
		var kessel = scn.GetLSCard("kessel");
		var han = scn.GetLSCard("han");
		var luminous = scn.GetLSCard("luminous");
		var hoth = scn.GetLSCard("hoth");
		var blues = scn.GetLSCard("blues");
		var rycar = scn.GetLSCard("rycar");

		scn.StartGame();

		scn.MoveCardsToLSHand(kessel_run, han, blues, rycar);

		scn.MoveLocationToTable(hoth);
		scn.MoveLocationToTable(kessel);

		scn.MoveCardsToLocation(hoth, luminous);

		//some cards in lost pile so we can confirm retrieval works
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(han);
		scn.LSChooseCard(luminous);
		scn.LSChoose("Pilot");
		scn.PassAllResponses();
		scn.DSPass();

		scn.LSDeployCard(rycar);
		scn.LSChooseCard(han);
		scn.PassAllResponses();
		scn.DSPass();

		scn.LSDeployCard(blues);
		scn.LSChooseCard(han);
		scn.PassAllResponses();
		scn.DSPass();

		scn.LSDeployCard(kessel_run);
		scn.LSChooseCard(kessel);
		scn.LSChooseCard(han);
		scn.PassAllResponses();
		scn.DSPass();

		scn.PrepareDSDestiny(1); //non-zero to avoid making starship lost
		assertEquals(0, scn.GetDSUsedPileCount());

		scn.SkipToPhase(Phase.MOVE);
		scn.LSUseCardAction(luminous,"hyperspeed");
		scn.LSChooseCard(kessel);
		scn.PassAllResponses();

		assertEquals(1, scn.GetDSUsedPileCount()); //dark drew kessel run destiny
		assertTrue(scn.CardsAtLocation(kessel,luminous)); //starship was not lost

		//unclear why scn.SkipToDSTurn() does not succeed here (fails after 20 pass attempts)
		scn.DSPass(); //move phase
		scn.LSPass();

		scn.LSPass(); //draw phase
		scn.DSPass();

		scn.PassAllResponses(); //recirculate

		//scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.MOVE);
		scn.LSUseCardAction(luminous,"hyperspeed");
		scn.LSChooseCard(hoth);

		assertEquals(4,scn.GetLSLostPileCount());

		scn.DSPass(); //Use 1 Force - Optional responses
		scn.LSPass();

		scn.DSPass(); //MOVING_USING_HYPERSPEED - Optional responses
		scn.LSPass();

		scn.DSPass(); //UTINNI_EFFECT_COMPLETED - Optional responses
		scn.LSPass();

		scn.DSPass(); //FORCE_RETRIEVAL_INITIATED - Optional responses
		scn.LSPass();

		scn.DSPass(); //FORCE_RETRIEVAL_ABOUT_TO_RETRIEVE - Optional responses
		scn.LSPass();

		assertEquals(0, scn.GetLSHandCount());
		assertEquals(1, scn.GetLSUsedPileCount());

		assertTrue(scn.LSDecisionAvailable("Choose where to retrieve"));
		assertTrue(scn.LSChoiceAvailable("Used Pile"));
		assertTrue(scn.LSChoiceAvailable("Hand"));
		scn.LSChoose("Hand");
		assertEquals(1, scn.GetLSHandCount()); //test1
		assertEquals(1, scn.GetLSUsedPileCount());

		scn.DSPass(); //RETRIEVED_FORCE - Optional responses
		scn.LSPass();

		assertTrue(scn.LSDecisionAvailable("Choose where to retrieve"));
		assertTrue(scn.LSChoiceAvailable("Used Pile"));
		assertTrue(scn.LSChoiceAvailable("Hand"));
		scn.LSChoose("Used Pile");
		assertEquals(1, scn.GetLSHandCount());
		assertEquals(2, scn.GetLSUsedPileCount()); //test2
	}

	@Test
	public void KesselRunCanBePlayedAgainAfterCompleting() {
		//test1: deploy action for second copy of kessel run is available after first copy is lost (completed)
		var scn = GetScenario();

		var kessel_run = scn.GetLSCard("kessel_run");
		var kessel_run2 = scn.GetLSCard("kessel_run2");
		var kessel = scn.GetLSCard("kessel");
		var han = scn.GetLSCard("han");
		var luminous = scn.GetLSCard("luminous");
		var hoth = scn.GetLSCard("hoth");

		scn.StartGame();

		scn.MoveCardsToLSHand(kessel_run, kessel_run2, han);

		scn.MoveLocationToTable(hoth);
		scn.MoveLocationToTable(kessel);

		scn.MoveCardsToLocation(hoth, luminous);

		//some cards in lost pile so we can confirm retrieval works
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(han);
		scn.LSChooseCard(luminous); //as pilot
		scn.LSChoose("Pilot");
		scn.PassAllResponses();
		scn.DSPass();

		scn.LSDeployCard(kessel_run);
		scn.LSChooseCard(kessel);
		scn.LSChooseCard(han);
		scn.PassAllResponses();

		scn.PrepareDSDestiny(1); //non-zero to avoid making starship lost
		assertEquals(0, scn.GetDSUsedPileCount());

		scn.SkipToPhase(Phase.MOVE);
		scn.LSUseCardAction(luminous,"hyperspeed");
		scn.LSChooseCard(kessel);
		scn.PassAllResponses();

		assertEquals(1, scn.GetDSUsedPileCount()); //dark drew kessel run destiny
		assertTrue(scn.CardsAtLocation(kessel,luminous)); //starship was not lost

		//unclear why scn.SkipToDSTurn() does not succeed here (fails after 20 pass attempts)
		scn.DSPass(); //move phase
		scn.LSPass();

		scn.LSPass(); //draw phase
		scn.DSPass();

		scn.PassAllResponses(); //recirculate

		//scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.MOVE);
		scn.LSUseCardAction(luminous,"hyperspeed");
		scn.LSChooseCard(hoth);

		scn.PassAllResponses();
		assertEquals(Zone.TOP_OF_LOST_PILE,kessel_run.getZone());

		scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(kessel_run2)); //test1
	}

	@Test
	public void KesselRunCanBePlayedAgainAfterGrabbed() {
		//unsuccessfully try to replicate https://github.com/PlayersCommittee/gemp-swccg-public/issues/10
		var scn = GetScenario();

		var kessel_run = scn.GetLSCard("kessel_run");
		var kessel_run2 = scn.GetLSCard("kessel_run2");
		var kessel = scn.GetLSCard("kessel");
		var han = scn.GetLSCard("han");
		var luminous = scn.GetLSCard("luminous");
		var hoth = scn.GetLSCard("hoth");

		var clearance = scn.GetDSCard("clearance");

		scn.StartGame();

		scn.MoveCardsToLSHand(kessel_run, kessel_run2, han);
		scn.MoveCardsToDSHand(clearance);

		scn.MoveLocationToTable(hoth);
		scn.MoveLocationToTable(kessel);

		scn.MoveCardsToLocation(hoth, luminous);

		//some cards in lost pile so we can confirm retrieval works
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());

		scn.SkipToPhase(Phase.DEPLOY);
		scn.DSPlayCard(clearance);
		scn.PassAllResponses();

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(han);
		scn.LSChooseCard(luminous); //as pilot
		scn.LSChoose("Pilot");
		scn.PassAllResponses();
		scn.DSPass();

		scn.LSDeployCard(kessel_run);
		scn.LSChooseCard(kessel);
		scn.LSChooseCard(han);
		scn.PassAllResponses();

		scn.PrepareDSDestiny(1); //non-zero to avoid making starship lost
		assertEquals(0, scn.GetDSUsedPileCount());

		scn.SkipToPhase(Phase.MOVE);
		scn.LSUseCardAction(luminous,"hyperspeed");
		scn.LSChooseCard(kessel);
		scn.PassAllResponses();

		assertEquals(1, scn.GetDSUsedPileCount()); //dark drew kessel run destiny
		assertTrue(scn.CardsAtLocation(kessel,luminous)); //starship was not lost

		//unclear why scn.SkipToDSTurn() does not succeed here (fails after 20 pass attempts)
		scn.DSPass(); //move phase
		scn.LSPass();

		scn.LSPass(); //draw phase
		scn.DSPass();

		scn.PassAllResponses(); //recirculate

		//scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.MOVE);
		scn.LSUseCardAction(luminous,"hyperspeed");
		scn.LSChooseCard(hoth);

		scn.DSPass(); //Use 1 Force - Optional responses
		scn.LSPass();

		scn.DSPass(); //MOVING_USING_HYPERSPEED - Optional responses
		scn.LSPass();

		scn.DSPass(); //UTINNI_EFFECT_COMPLETED - Optional responses
		scn.LSPass();

		scn.DSPass(); //FORCE_RETRIEVAL_INITIATED - Optional responses
		scn.LSPass();

		scn.DSPass(); //FORCE_RETRIEVAL_ABOUT_TO_RETRIEVE - Optional responses
		scn.LSPass();

		assertTrue(scn.DSCardActionAvailable(clearance)); //Just retrieved 1 Force - Optional responses
		scn.DSUseCardAction(clearance);
		scn.PassAllResponses();

		assertEquals(Zone.STACKED,kessel_run.getZone());

		scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(kessel_run2));
	}

}


