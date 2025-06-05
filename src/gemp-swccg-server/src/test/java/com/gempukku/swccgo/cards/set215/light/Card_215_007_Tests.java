package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.*;

public class Card_215_007_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					// The card itself is included in the Rescue the Princess objective pack
					put("bothan", "7_5"); //spy
				}},
				new HashMap<>()
				{{
				}},
				10,
				10,
				StartingSetup.RescueThePrincessVObjective,
				StartingSetup.DefaultDSGroundLocation,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void DetentionBlockCorridorStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Death Star: Detention Block Corridor (V)
		 * Side: Light
		 * Type: LOCATION
		 * Uniqueness: UNIQUE
		 * Destiny: 0
		 * Icons: Special Edition, Interior Site, Mobile, Scomp Link, V Set 15
		 * Dark Side Game Text: Once per game, may [download] a non-[Maintenance] Imperial trooper here.
		 * Light Side Game Text: If you control with a spy, may use 2 Force to release Leia here (retrieve 1 Force).
		 * Set: 15
		 * Rarity: V
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("corridor").getBlueprint();

		assertEquals(Title.Detention_Block_Corridor, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertTrue(card.isCardType(CardType.LOCATION));
		//assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
		assertEquals(0, card.getDestiny(), scn.epsilon);

		assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
		assertEquals(0, card.getIconCount(Icon.LIGHT_FORCE));

		assertEquals(1, card.getIconCount(Icon.INTERIOR_SITE));
		assertEquals(1, card.getIconCount(Icon.MOBILE));
		assertEquals(1, card.getIconCount(Icon.SCOMP_LINK));
		assertEquals(1, card.getIconCount(Icon.SPECIAL_EDITION));
		assertEquals(1, card.getIconCount(Icon.VIRTUAL_SET_15));
		assertTrue(card.hasKeyword(Keyword.PRISON));
	}

	@Test
	public void DetentionBlockCorridorLSCanUse2ForceToReleaseLeiaAndRetrieve1ForceIfOccupiedWithSpy() {
		var scn = GetScenario();

		var prisoner = scn.GetLSCard("prisoner");
		var bothan = scn.GetLSCard("bothan");
		var corridor = scn.GetLSCard("corridor");

		scn.StartGame();

		scn.MoveCardsToLocation(corridor, bothan);

		scn.MoveCardsToTopOfLSLostPile(scn.GetLSFiller(1));

		scn.SkipToLSTurn(Phase.MOVE);

		assertAtLocation(corridor, bothan);
		assertTrue(prisoner.isCaptive());
		assertTrue(prisoner.isImprisoned());
		assertEquals(corridor, prisoner.getAttachedTo());
		assertTrue(bothan.getBlueprint().hasKeyword(Keyword.SPY));
		assertEquals(7, scn.GetLSForcePileCount());
		assertEquals(0, scn.GetLSUsedPileCount());
		assertEquals(1, scn.GetLSLostPileCount());

		assertTrue(scn.LSActionAvailable("Use 2 Force to release Leia here"));
		scn.LSChooseAction("Use 2 Force to release Leia here");
		scn.PassAllResponses();

		assertAtLocation(corridor, prisoner);
		assertFalse(prisoner.isCaptive());
		assertFalse(prisoner.isImprisoned());
		assertNull(prisoner.getAttachedTo());

		assertEquals(5, scn.GetLSForcePileCount());
		//2 Force used to use the ability, and 1 retrieved from the lost pile
		assertEquals(3, scn.GetLSUsedPileCount());
		assertEquals(0, scn.GetLSLostPileCount());
	}

	@Test
	public void DetentionBlockCorridorReleaseAbilityCannotBeUsedWithoutASpy() {
		var scn = GetScenario();

		var prisoner = scn.GetLSCard("prisoner");
		var rebel = scn.GetLSFiller(1);
		var corridor = scn.GetLSCard("corridor");

		scn.StartGame();

		scn.MoveCardsToLocation(corridor, rebel);

		scn.SkipToLSTurn(Phase.MOVE);

		assertAtLocation(corridor, rebel);
		assertTrue(prisoner.isCaptive());
		assertTrue(prisoner.isImprisoned());
		assertEquals(corridor, prisoner.getAttachedTo());
		assertFalse(rebel.getBlueprint().hasKeyword(Keyword.SPY));

		assertFalse(scn.LSActionAvailable("Use 2 Force to release Leia here"));
	}

}
