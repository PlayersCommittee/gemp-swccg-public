package com.gempukku.swccgo.cards.set110.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_110_004_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("leia", "1_17");
					put("loves_you", "6_75");
				}},
				new HashMap<>()
				{{
				}},
				10,
				10,
				StartingSetup.ProfitObjective,
				StartingSetup.DefaultDSGroundLocation,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void ProfitStatsAndKeywordsAreCorrect() {
		/**
		 * Front Title: You Can Either Profit By This...
		 * Back Title : Or Be Destroyed
		 * Side: Light
		 * Type: Objective
		 * Destiny: 0/7
		 * Front Game Text: Deploy Tatooine: Jabba's Palace and Audience Chamber (with Han frozen there, he may not
		 * 			be moved when frozen). Opponent may deploy up to two aliens at Audience Chamber. While this side up,
		 * 			opponent may not Force Drain at Audience Chamber and you may not Force drain at Tatooine locations.
		 * 			You may not play Frozen Assets. Luke, C-3PO and R2-D2 are deploy -2 at Jabba's Palace sites (Master
		 * 			Luke deploys for free instead). Flip this card if Han is on Tatooine and not a captive. Place out of
		 * 			play if Tatooine is 'blown away.'
		 * Back Game Text : Immediately retrieve 5 Force (or 10 if Han has power < 4) once per game. While this side up,
		 * 			cancels the game text of Bad Feeling Have I. Your unpiloted starfighters may deploy to exterior
		 * 			Tatooine locations. During your control phase, opponent loses 1 Force for each battleground location
		 * 			occupied by Han, Luke, Leia, Chewie, or Lando. Flip this card if Han is captured or not on table.
		 * 			Place out of play if Tatooine is 'blown away.'
		 * Set: Enhanced Jabba's Palace
		 * Rarity: PM
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("profit").getBlueprint();

		assertEquals(Title.You_Can_Either_Profit_By_This, card.getTitle());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(0, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.PREMIUM));

		var back = scn.GetLSCard("profit").getOtherSideBlueprint();

		assertEquals(Title.Or_Be_Destroyed, back.getTitle());
		assertEquals(Side.LIGHT, back.getSide());
		assertEquals(7, back.getDestiny(), scn.epsilon);
		assertEquals(1, back.getIconCount(Icon.PREMIUM));
	}

	@Test
	public void ProfitFrontSideFlipsWhenHanFreedByControllingAlly() {
		var scn = GetScenario();

		var trooper = scn.GetLSFiller(1);

		// Pulled from the Profit default setup:
		var profit = scn.GetLSCard("profit");
		var chamber = scn.GetLSCard("chamber");
		var han = scn.GetLSCard("han");

		scn.StartGame();

		//Han is already there due to Profit's starting text
		scn.MoveCardsToLocation(chamber, trooper);

		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(han.isFrozen());
		assertTrue(han.isCaptive());
		assertAtLocation(chamber, han, trooper);
		assertFalse(profit.isFlipped());
		assertTrue(scn.LSActionAvailable("Release an unattended frozen captive"));

		scn.LSChooseAction("Release an unattended frozen captive");
		scn.LSChooseCard(han);

		assertFalse(han.isFrozen());
		assertFalse(han.isCaptive());
		assertAtLocation(chamber, han);
		assertTrue(profit.isFlipped());
	}

	@Test
	public void ProfitFrontSideFlipsWhenHanFreedBySomeoneWhoLovesYou() {
		var scn = GetScenario();

		var leia = scn.GetLSCard("leia");
		var loves_you = scn.GetLSCard("loves_you");
		scn.MoveCardsToHand(loves_you);

		// Pulled from the Profit default setup:
		var profit = scn.GetLSCard("profit");
		var chamber = scn.GetLSCard("chamber");
		var han = scn.GetLSCard("han");

		var stormtrooper = scn.GetDSFiller(1);

		scn.StartGame();

		//Han is already there due to Profit's starting text
		scn.MoveCardsToLocation(chamber, leia, stormtrooper);

		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(han.isFrozen());
		assertTrue(han.isCaptive());
		assertAtLocation(chamber, han, leia);
		assertFalse(profit.isFlipped());
		assertInHand(loves_you);

		//With an occupying Stormtrooper, Han can't be freed using the default method
		assertFalse(scn.LSActionAvailable("Release an unattended frozen captive"));

		assertTrue(scn.LSCardPlayAvailable(loves_you));
		scn.LSPlayCardAndPassResponses(loves_you, han);

		assertFalse(han.isFrozen());
		assertFalse(han.isCaptive());
		assertAtLocation(chamber, han);
		assertTrue(profit.isFlipped());
	}

}
