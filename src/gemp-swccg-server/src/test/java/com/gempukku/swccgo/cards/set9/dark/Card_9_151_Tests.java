package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_9_151_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("luke", "1_19");
				}},
				new HashMap<>()
				{{
					put("vader", "1_168");
					put("emperor", "9_109");
				}},
				10,
				10,
				StartingSetup.DefaultLSGroundLocation,
				StartingSetup.BHBMObjective,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void BringHimBeforeMeStatsAndKeywordsAreCorrect() {
		/**
		 * Front Title: Bring Him Before Me
		 * Back Title : Take Your Father's Place
		 * Side: Dark
		 * Type: Objective
		 * Destiny: 0/7
		 * Front Game Text: Deploy Throne Room, Insignificant Rebellion and Your Destiny. For remainder of game,
		 * 			Scanning Crew may not be played. Opponent's cards that place a character out of play may not target
		 * 			Luke. You may deploy Emperor (deploy -2) from Reserve Deck; reshuffle. Opponent may deploy Luke from
		 * 			Reserve Deck (deploy -2; reshuffle) or Lost Pile. If Luke is present with Vader and Vader is not
		 * 			escorting a captive, Luke is captured and seized by Vader. Vader may not transfer Luke. Flip this
		 * 			card if Luke captured.
		 * Back Game Text : While this side up, lose 1 Force at end of each of your turns. Once during each of your
		 * 			turns, when Vader, Luke (even as a non-frozen captive) and Emperor are all present at your Throne
		 * 			Room, you may initiate a Luke/Vader duel: Each player draws two destiny. Add ability. Highest total
		 * 			wins. If Vader wins, opponent loses 3 Force. If Luke wins, shuffle Reserve Deck and draw destiny;
		 * 			if destiny > 12, Luke crosses to Dark Side, totally depleting opponent's Life Force. Flip if Luke
		 * 			neither present with Vader nor a captive.
		 * Set: Special Edition
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("bhbm").getBlueprint();

		assertEquals(Title.Bring_Him_Before_Me, card.getTitle());
		assertEquals(Side.DARK, card.getSide());
		assertEquals(0, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.DEATH_STAR_II));

		var back = scn.GetDSCard("bhbm").getOtherSideBlueprint();

		assertEquals(Title.Take_Your_Fathers_Place, back.getTitle());
		assertEquals(Side.DARK, back.getSide());
		assertEquals(7, back.getDestiny(), scn.epsilon);
		assertEquals(1, back.getIconCount(Icon.DEATH_STAR_II));
	}

	@Test
	public void BHBMFlipsWhenLukeEscortedByVaderToThroneRoom() {
		var scn = GetScenario();

		var luke = scn.GetLSCard("luke");

		var throne = scn.GetDSCard("throne");

		var bhbm = scn.GetDSCard("bhbm");
		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(throne, vader);
		assertFalse(bhbm.isFlipped());
		scn.MoveCardsToLocation(throne, luke);

		assertFalse(luke.isCaptive());
		assertNull(luke.getEscort());
		assertNull(luke.getAttachedTo());

		//We perform the next action so that Gemp can realize Luke has poofed into existence
		scn.DSActivateMaxForceAndPass();

		assertTrue(luke.isCaptive());
		assertEquals(vader, luke.getEscort());
		assertEquals(vader, luke.getAttachedTo());

		assertTrue(bhbm.isFlipped());
	}

	@Test
	public void TYFPMakesLSLose3ForceWhenLosingDuel() {
		var scn = GetScenario();

		var luke = scn.GetLSCard("luke");

		var throne = scn.GetDSCard("throne");

		var bhbm = scn.GetDSCard("bhbm");
		var vader = scn.GetDSCard("vader");
		var emperor = scn.GetDSCard("emperor");

		scn.StartGame();

		scn.MoveCardsToLocation(throne, vader, emperor);
		assertFalse(bhbm.isFlipped());
		scn.MoveCardsToLocation(throne, luke);

		assertFalse(luke.isCaptive());
		assertNull(luke.getEscort());
		assertNull(luke.getAttachedTo());

		//We perform the next action so that Gemp can realize Luke has poofed into existence
		scn.DSActivateMaxForceAndPass();
		scn.PassAllResponses();

		assertTrue(luke.isCaptive());
		assertEquals(vader, luke.getEscort());
		assertEquals(vader, luke.getAttachedTo());
		assertTrue(bhbm.isFlipped());

		assertTrue(scn.CardsAtLocation(throne, vader, emperor));

		assertTrue(scn.DSActionAvailable("Initiate a Luke/Vader duel"));

		scn.PrepareLSDestiny(1);
		scn.PrepareLSDestiny(0);
		scn.PrepareDSDestiny(6);
		scn.PrepareDSDestiny(7);
		scn.DSChooseAction("Initiate a Luke/Vader duel");

		scn.PassAllResponses();
		scn.PassAllResponses();

		int life = scn.GetLSLifeForceRemaining();
        scn.LSPayRemainingForceLossFromReserveDeck();
		assertEquals(life-3, scn.GetLSLifeForceRemaining());

		assertTrue(scn.AwaitingLSControlPhaseActions());
	}

}
