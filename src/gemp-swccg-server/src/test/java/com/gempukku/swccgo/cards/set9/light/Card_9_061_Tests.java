package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_9_061_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
				}},
				new HashMap<>()
				{{
					put("vader", "1_168");
				}},
				10,
				10,
				StartingSetup.ThereIsGoodInHimObjective,
				StartingSetup.DefaultDSGroundLocation,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void ThereIsGoodInHimStatsAndKeywordsAreCorrect() {
		/**
		 * Front Title: There Is Good In Him
		 * Back Title : I Can Save Him
		 * Side: Light
		 * Type: Objective
		 * Destiny: 0/7
		 * Front Game Text: Deploy Chief Chirpa's Hut (with [Death Star II] Luke and Luke's Lightsaber there), Endor:
		 * 			Landing Platform and I Feel The Conflict.
		 * 			For remainder of game, you may not play Alter, Strangle, or Captive Fury.
		 * 			While this side up, your Force generation is +2 at Luke's site. While an Imperial is at Landing
		 * 			Platform, you may not Force drain or generate Force at Luke's location. When any Imperial is at
		 * 			Luke's site, Luke is captured (seized by an Imperial, if possible, even if not a warrior).
		 * 			Flip this card if Luke captured.
		 * Back Game Text : While this side up, at end of each of opponent's turns, opponent loses 2 Force unless Vader
		 * 			is escorting Luke. At any time, an Imperial escorting Luke may transfer Luke to Vader, if present
		 * 			with Vader. Vader may not transfer Luke. Once during each of your turns, if Vader present with Luke
		 * 			(even as a non-frozen captive), may shuffle Reserve Deck and draw destiny. If destiny > 14, Vader
		 * 			crosses to Light Side, totally depleting opponent's Life Force.
		 * 			Flip if Luke neither present with Vader nor a captive.
		 * Set: Death Star II
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("tigih").getBlueprint();

		assertEquals(Title.There_Is_Good_In_Him, card.getTitle());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(0, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.DEATH_STAR_II));

		var back = scn.GetLSCard("tigih").getOtherSideBlueprint();

		assertEquals(Title.I_Can_Save_Him, back.getTitle());
		assertEquals(Side.LIGHT, back.getSide());
		assertEquals(7, back.getDestiny(), scn.epsilon);
		assertEquals(1, back.getIconCount(Icon.DEATH_STAR_II));
	}

	@Test
	public void ICanSaveHimMakesDSLose2ForceAtEndOfTurnIfVaderNotEscortingLuke() {
		var scn = GetScenario();

		var lsjk = scn.GetLSCard("lsjk");
		var tigih = scn.GetLSCard("tigih");

		var hut = scn.GetLSCard("hut");
		var platform = scn.GetLSCard("platform");

		var trooper = scn.GetDSFiller(1);
		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		assertFalse(tigih.isFlipped());
		scn.MoveCardsToLocation(hut, trooper);
		scn.MoveCardsToLocation(platform, vader);

		scn.SkipToPhase(Phase.CONTROL);

		assertTrue(lsjk.isCaptive());
		assertEquals(trooper, lsjk.getEscort());
		assertEquals(trooper, lsjk.getAttachedTo());
		assertTrue(trooper.getCardsEscorting().contains(lsjk));
		assertTrue(tigih.isFlipped());

		scn.SkipToPhase(Phase.DRAW);
		scn.DSPass();
		scn.LSPass();

		assertTrue(scn.DSDecisionAvailable("FORCE_LOSS_INITIATED - Optional responses"));
	}

	@Test
	public void ICanSaveHimOffersTransferToVaderAndStopsAutoForceLoss() {
		var scn = GetScenario();

		var lsjk = scn.GetLSCard("lsjk");
		var tigih = scn.GetLSCard("tigih");

		var hut = scn.GetLSCard("hut");
		var platform = scn.GetLSCard("platform");

		var trooper = scn.GetDSFiller(1);
		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		assertFalse(tigih.isFlipped());
		scn.MoveCardsToLocation(hut, trooper);
		scn.MoveCardsToLocation(platform, vader);

		scn.SkipToPhase(Phase.MOVE);

		assertTrue(lsjk.isCaptive());
		assertEquals(trooper, lsjk.getEscort());
		assertEquals(trooper, lsjk.getAttachedTo());
		assertTrue(trooper.getCardsEscorting().contains(lsjk));
		assertTrue(tigih.isFlipped());

		assertTrue(scn.DSMoveAvailable(trooper));
		scn.DSMoveCard(trooper, platform);
		scn.PassAllResponses();
		scn.LSPass();

		assertTrue(scn.DSActionAvailable("Transfer Luke to Vader"));
		scn.DSChooseAction("Transfer Luke to Vader");
		scn.PassAllResponses();

		assertTrue(lsjk.isCaptive());
		assertEquals(vader, lsjk.getEscort());
		assertEquals(vader, lsjk.getAttachedTo());
		assertTrue(vader.getCardsEscorting().contains(lsjk));
		assertTrue(tigih.isFlipped());
		assertTrue(trooper.getCardsEscorting().isEmpty());

		scn.SkipToPhase(Phase.DRAW);
		scn.DSPass();
		scn.LSPass();

		assertFalse(scn.DSDecisionAvailable("FORCE_LOSS_INITIATED - Optional responses"));
	}
}
