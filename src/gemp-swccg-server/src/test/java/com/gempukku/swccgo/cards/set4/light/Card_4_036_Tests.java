package com.gempukku.swccgo.cards.set4.light;

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

public class Card_4_036_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
					put("rycars_run","4_036");
					put("kessel","1_126");
					put("bigOne","4_082");
					put("ywing","1_147");
					put("han","1_011"); //(smuggler)
					put("rycar","1_063"); //Rycar Ryjerd
					put("blues","4_038"); //Smuggler's Blues
				}},
				new HashMap<>() {{
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
	public void RycarsRunStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Rycar's Run
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Effect
		 * Subtype: Utinni
		 * Destiny: 4
		 * Icons: Dagobah
		 * Game Text: Deploy on a Big One. X = twice the number of asteroid sectors at that system.
		 * 		Target a starfighter at related planet system. When reached by target, relocate Utinni Effect
		 * 		to planet system. When target returns to system, lose Utinni Effect. Retrieve X Force.
		 * Lore: The infamous smuggler Rycar Ryjerd does this all the time. He's an idiot.
		 * Set: Dagobah
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("rycars_run").getBlueprint();

		assertEquals(Title.Rycars_Run, card.getTitle());
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
			add(Icon.DAGOBAH);
		}});
		assertEquals(ExpansionSet.DAGOBAH,card.getExpansionSet());
		assertEquals(Rarity.R, card.getRarity());
	}

	@Test
	public void RycarsRunRetrievesXForce() {
		//basic check with one asteroid sector (Big One) for retrieval of X = 2
		var scn = GetScenario();

		var rycars_run = scn.GetLSCard("rycars_run");
		var kessel = scn.GetLSCard("kessel");
		var ywing = scn.GetLSCard("ywing");
		var bigOne = scn.GetLSCard("bigOne");

		scn.StartGame();

		scn.MoveCardsToLSHand(rycars_run, bigOne);

		scn.MoveLocationToTable(kessel);

		scn.MoveCardsToLocation(kessel, ywing);

		//some cards in lost pile so we can confirm retrieval works
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(bigOne);
		scn.PassAllResponses();
		scn.DSPass();

		scn.LSDeployCard(rycars_run);
		scn.LSChooseCard(bigOne);
		scn.LSChooseCard(ywing);
		scn.PassAllResponses();

		assertTrue(scn.IsAttachedTo(bigOne, rycars_run));

		scn.SkipToPhase(Phase.MOVE);
		scn.LSUseCardAction(ywing,"sector");
		scn.LSChooseCard(bigOne);
		scn.PassAllResponses();

		assertTrue(scn.IsAttachedTo(kessel, rycars_run)); //relocated to related system

		scn.SkipToDSTurn();
		scn.PrepareDSDestiny(1); //for failed asteroid destiny draw

		scn.SkipToLSTurn(Phase.MOVE);
		scn.LSUseCardAction(ywing,"sector");
		scn.LSChooseCard(kessel);

		assertEquals(3,scn.GetLSLostPileCount());

		scn.PassAllResponses();

		assertEquals(2,scn.GetLSLostPileCount()); //3 in lost, lost rycar's run, then retrieved 2 (1 asteroid sector means X = 2)
		assertEquals(Zone.USED_PILE,rycars_run.getZone()); //(lost before retrieval)
	}

	@Test
	public void RycarsRunRetrievesXForceToHandWithSmugglersBlues() {
		//with Smuggler's Blues conditions met, player has option to decide for each force retrieved
		//whether it goes to hand or used pile.
		//test1: retrieved force can be taken into hand
		//test2: retrieved force can be sent to used pile
		var scn = GetScenario();

		var rycars_run = scn.GetLSCard("rycars_run");
		var kessel = scn.GetLSCard("kessel");
		var han = scn.GetLSCard("han");
		var ywing = scn.GetLSCard("ywing");
		var bigOne = scn.GetLSCard("bigOne");
		var blues = scn.GetLSCard("blues");
		var rycar = scn.GetLSCard("rycar");

		scn.StartGame();

		scn.MoveCardsToLSHand(rycars_run, han, bigOne, blues, rycar);

		scn.MoveLocationToTable(kessel);

		scn.MoveCardsToLocation(kessel, ywing);

		//some cards in lost pile so we can confirm retrieval works
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
		scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(bigOne);
		scn.PassAllResponses();
		scn.DSPass();

		scn.LSDeployCard(han);
		scn.LSChooseCard(ywing); //as pilot
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

		scn.LSDeployCard(rycars_run);
		scn.LSChooseCard(bigOne);
		scn.LSChooseCard(ywing);
		scn.PassAllResponses();

		assertTrue(scn.IsAttachedTo(bigOne, rycars_run));

		scn.SkipToPhase(Phase.MOVE);
		scn.LSUseCardAction(ywing,"sector");
		scn.LSChooseCard(bigOne);
		scn.PassAllResponses();

		assertTrue(scn.IsAttachedTo(kessel, rycars_run)); //relocated to related system

		scn.SkipToDSTurn();
		scn.PrepareDSDestiny(1); //for failed asteroid destiny draw

		scn.SkipToLSTurn(Phase.MOVE);
		scn.LSUseCardAction(ywing,"sector");
		scn.LSChooseCard(kessel);

		assertEquals(3,scn.GetLSLostPileCount());

		scn.DSPass(); //Use 1 Force - Optional responses
		scn.LSPass();

		scn.DSPass(); //MOVING_USING_SECTOR_MOVEMENT - Optional responses
		scn.LSPass();

		scn.DSPass(); //UTINNI_EFFECT_COMPLETED - Optional responses
		scn.LSPass();

		scn.DSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses
		scn.LSPass();

		scn.DSPass(); //LOST_FROM_TABLE - Optional responses
		scn.LSPass();

		assertEquals(Zone.TOP_OF_LOST_PILE,rycars_run.getZone()); //(lost before retrieval)

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
}
