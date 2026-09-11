package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Reflections III Dark Lost Interrupt 13_70 Force Push.
 * Stats/icons from printed/JSON/doc, not from Card13_070.java.
 */
public class Card_13_70_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("obi", "11_10"); // Qui-Gon Jinn (Jedi)
					put("lsCombat4", "1_105"); // Rebel Barrier destiny 4
					put("lsCombat5", "1_102"); // Out Of Nowhere destiny 5
					put("lsCombat5b", "1_110"); // Skywalkers destiny 5
					put("sense", "1_109");
				}},
				new HashMap<>() {{
					put("forcePush", "13_70");
					put("maul", "11_54"); // Darth Maul (Dark Jedi)
				}},
				40,
				40,
				StartingSetup.DefaultLSGroundLocation,
				StartingSetup.DefaultDSGroundLocation,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	private void preparePresentWithCombatCards(VirtualTableScenario scn, boolean stackCombatOnJedi, boolean darkJediPresent,
											   boolean useDestiny4, boolean stackSecondCombat) {
		var forcePush = scn.GetDSCard("forcePush");
		var obi = scn.GetLSCard("obi");
		var maul = scn.GetDSCard("maul");
		var site = scn.GetLSStartingLocation();
		var combatA = useDestiny4 ? scn.GetLSCard("lsCombat4") : scn.GetLSCard("lsCombat5");
		var combatB = scn.GetLSCard("lsCombat5b");

		scn.StartGame();
		if (darkJediPresent) {
			scn.MoveCardsToLocation(site, obi, maul);
		} else {
			scn.MoveCardsToLocation(site, obi);
			// Maul elsewhere (DS starting site)
			scn.MoveCardsToLocation(scn.GetDSStartingLocation(), maul);
		}
		scn.MoveCardsToDSHand(forcePush);

		if (stackCombatOnJedi) {
			scn.StackCardsOn(obi, combatA);
			combatA.setCombatCard(true);
			if (stackSecondCombat) {
				scn.StackCardsOn(obi, combatB);
				combatB.setCombatCard(true);
			}
		}
	}

	@Test
	public void ForcePushStatsAndIcons() {
		var scn = GetScenario();
		var forcePush = scn.GetDSCard("forcePush");
		scn.StartGame();

		assertEquals(5f, forcePush.getBlueprint().getDestiny(), 0.001f);
		scn.BlueprintIconCheck(forcePush.getBlueprint(), new ArrayList<>() {{
			add(Icon.REFLECTIONS_III);
			add(Icon.INTERRUPT);
			add(Icon.EPISODE_I);
		}});
	}

	@Test
	public void ForcePushPlayableWhenOpponentsJediHasCombatCardPresentWithDarkJedi() {
		var scn = GetScenario();
		preparePresentWithCombatCards(scn, true, true, true, false);
		var forcePush = scn.GetDSCard("forcePush");

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(forcePush));
	}

	@Test
	public void ForcePushNotPlayableWhenJediHasNoCombatCards() {
		var scn = GetScenario();
		preparePresentWithCombatCards(scn, false, true, true, false);
		var forcePush = scn.GetDSCard("forcePush");

		scn.SkipToPhase(Phase.CONTROL);
		assertFalse(scn.DSCardPlayAvailable(forcePush));
	}

	@Test
	public void ForcePushNotPlayableWhenJediNotPresentWithDarkJedi() {
		var scn = GetScenario();
		preparePresentWithCombatCards(scn, true, false, true, false);
		var forcePush = scn.GetDSCard("forcePush");

		scn.SkipToPhase(Phase.CONTROL);
		assertFalse(scn.DSCardPlayAvailable(forcePush));
	}

	@Test
	public void ForcePushDestinyFourOrLessLosesOneForceAndCombatCardStaysStacked() {
		var scn = GetScenario();
		preparePresentWithCombatCards(scn, true, true, true, false);
		var forcePush = scn.GetDSCard("forcePush");
		var obi = scn.GetLSCard("obi");
		var combat = scn.GetLSCard("lsCombat4");

		int lifeBefore = scn.GetDSLifeForceRemaining();

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(forcePush));
		scn.DSPlayCard(forcePush);
		scn.DSChooseCard(obi);
		scn.PassAllResponses();
		// Lose 1 Force (destiny <= 4)
		scn.PassAllResponses();
		scn.DSChooseCard(scn.GetTopOfDSForcePile());
		scn.PassAllResponses();

		assertEquals(Zone.TOP_OF_LOST_PILE, forcePush.getZone());
		assertTrue(scn.GetStackedCards(obi).contains(combat));
		assertEquals(lifeBefore - 1, scn.GetDSLifeForceRemaining());
	}

	@Test
	public void ForcePushDestinyGreaterThanFourPlacesCombatCardOnOpponentsReserveDeck() {
		var scn = GetScenario();
		preparePresentWithCombatCards(scn, true, true, false, false);
		var forcePush = scn.GetDSCard("forcePush");
		var obi = scn.GetLSCard("obi");
		var combat = scn.GetLSCard("lsCombat5");

		int lifeBefore = scn.GetDSLifeForceRemaining();

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(forcePush));
		scn.DSPlayCard(forcePush);
		scn.DSChooseCard(obi);
		scn.PassAllResponses();

		assertEquals(Zone.TOP_OF_LOST_PILE, forcePush.getZone());
		assertFalse(scn.GetStackedCards(obi).contains(combat));
		assertEquals(Zone.TOP_OF_RESERVE_DECK, combat.getZone());
		assertEquals(combat, scn.GetTopOfLSReserveDeck());
		assertEquals(lifeBefore, scn.GetDSLifeForceRemaining());
	}

	@Test
	public void ForcePushWithTwoCombatCardsRevealsOnlyOne() {
		var scn = GetScenario();
		// Both destinies > 4 so whichever is revealed goes to Reserve; the other stays stacked
		preparePresentWithCombatCards(scn, true, true, false, true);
		var forcePush = scn.GetDSCard("forcePush");
		var obi = scn.GetLSCard("obi");
		var combatA = scn.GetLSCard("lsCombat5");
		var combatB = scn.GetLSCard("lsCombat5b");

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(forcePush));
		scn.DSPlayCard(forcePush);
		scn.DSChooseCard(obi);
		scn.PassAllResponses();

		assertEquals(Zone.TOP_OF_LOST_PILE, forcePush.getZone());
		boolean aOnReserve = combatA.getZone() == Zone.TOP_OF_RESERVE_DECK || combatA.getZone() == Zone.RESERVE_DECK;
		boolean bOnReserve = combatB.getZone() == Zone.TOP_OF_RESERVE_DECK || combatB.getZone() == Zone.RESERVE_DECK;
		assertTrue(aOnReserve ^ bOnReserve);
		assertEquals(1, scn.GetStackedCards(obi).size());
	}

	@Test
	public void ForcePushImmuneToSense() {
		var scn = GetScenario();
		preparePresentWithCombatCards(scn, true, true, true, false);
		var forcePush = scn.GetDSCard("forcePush");
		var sense = scn.GetLSCard("sense");
		var obi = scn.GetLSCard("obi");

		scn.MoveCardsToLSHand(sense);
		scn.LSActivateForceCheat(1);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(forcePush));
		scn.DSPlayCard(forcePush);
		scn.DSChooseCard(obi);

		// Optional responses to playing Force Push - Sense must not be available
		assertFalse(scn.LSCardPlayAvailable(sense));
		scn.PassAllResponses();
		// Destiny 4 combat card -> lose 1 Force
		scn.PassAllResponses();
		scn.DSChooseCard(scn.GetTopOfDSForcePile());
		scn.PassAllResponses();
		assertEquals(Zone.TOP_OF_LOST_PILE, forcePush.getZone());
	}
}
