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
 * Tests for Reflections III Dark Used Interrupt 13_88 The Ebb Of Battle.
 * Stats/icons from printed card image / Doc, not from Card13_088.java.
 */
public class Card_13_88_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("luke", "1_027"); // Luke Skywalker (for LS presence / drain)
					put("sense", "1_109");
					put("obi", "11_10"); // Qui-Gon (Jedi) for non-Dark-Jedi combat-card negative
				}},
				new HashMap<>() {{
					put("ebb", "13_88");
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

	@Test
	public void TheEbbOfBattleStatsAndIcons() {
		var scn = GetScenario();
		var ebb = scn.GetDSCard("ebb");
		scn.StartGame();

		assertEquals(5f, ebb.getBlueprint().getDestiny(), 0.001f);
		scn.BlueprintIconCheck(ebb.getBlueprint(), new ArrayList<>() {{
			add(Icon.REFLECTIONS_III);
			add(Icon.INTERRUPT);
			add(Icon.EPISODE_I);
		}});
	}

	@Test
	public void TheEbbOfBattleActivateOneForcePlayableWithReserveAndActivates() {
		var scn = GetScenario();
		var ebb = scn.GetDSCard("ebb");
		scn.StartGame();
		scn.MoveCardsToDSHand(ebb);

		scn.SkipToPhase(Phase.CONTROL);
		int forceBefore = scn.GetDSForcePileCount();
		int reserveBefore = scn.GetDSReserveDeckCount();

		assertTrue(scn.DSCardPlayAvailable(ebb));
		scn.DSPlayCard(ebb);
		scn.PassAllResponses();

		assertEquals(Zone.TOP_OF_USED_PILE, ebb.getZone());
		assertEquals(forceBefore + 1, scn.GetDSForcePileCount());
		assertEquals(reserveBefore - 1, scn.GetDSReserveDeckCount());
	}

	@Test
	public void TheEbbOfBattleActivateOneForcePlayableWithEmptyReserveActivationFails() {
		var scn = GetScenario();
		var ebb = scn.GetDSCard("ebb");
		scn.StartGame();
		scn.MoveCardsToDSHand(ebb);

		// Reach Control with a normal Activate first; then empty Reserve so ActivateForce will fail
		scn.SkipToPhase(Phase.CONTROL);
		while (scn.GetDSReserveDeckCount() > 0) {
			scn.MoveCardsToTopOfDSLostPile(scn.GetTopOfDSReserveDeck());
		}
		assertEquals(0, scn.GetDSReserveDeckCount());
		int forceBefore = scn.GetDSForcePileCount();

		assertTrue(scn.DSCardPlayAvailable(ebb));
		scn.DSPlayCard(ebb);
		scn.PassAllResponses();

		assertEquals(Zone.TOP_OF_USED_PILE, ebb.getZone());
		assertEquals(forceBefore, scn.GetDSForcePileCount());
	}

	@Test
	public void TheEbbOfBattleActivateOneForceImmuneToSense() {
		var scn = GetScenario();
		var ebb = scn.GetDSCard("ebb");
		var sense = scn.GetLSCard("sense");
		scn.StartGame();
		scn.MoveCardsToDSHand(ebb);
		scn.MoveCardsToLSHand(sense);
		scn.LSActivateForceCheat(1);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(ebb));
		scn.DSPlayCard(ebb);

		assertFalse(scn.LSCardPlayAvailable(sense));
		scn.PassAllResponses();
		assertEquals(Zone.TOP_OF_USED_PILE, ebb.getZone());
	}

	@Test
	public void TheEbbOfBattleCancelForceDrainNotPlayableFromHand() {
		var scn = GetScenario();
		var ebb = scn.GetDSCard("ebb");
		var luke = scn.GetLSCard("luke");
		var dsSite = scn.GetDSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToDSHand(ebb);
		scn.MoveCardsToLocation(dsSite, luke);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSForceDrainAvailable(dsSite));
		scn.LSForceDrainAt(dsSite);

		// Cancel-from-combat-card action must not be available from hand
		assertFalse(scn.DSCardPlayAvailable(ebb, "Reveal combat card"));
		assertFalse(scn.DSCardPlayAvailable(ebb, "Cancel Force drain"));
		scn.PassForceDrainStartResponses();
		scn.PassForceDrainEndResponses();
	}

	@Test
	public void TheEbbOfBattleCancelForceDrainWhenCombatCardUnderDarkJedi() {
		var scn = GetScenario();
		var ebb = scn.GetDSCard("ebb");
		var maul = scn.GetDSCard("maul");
		var luke = scn.GetLSCard("luke");
		var dsSite = scn.GetDSStartingLocation();
		var lsSite = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(lsSite, maul);
		scn.MoveCardsToLocation(dsSite, luke);
		scn.MoveCardsToDSHand(ebb);

		// Stack face-down as combat card under Dark Jedi (real combat-card zone)
		scn.RemoveCardZone(ebb);
		scn.gameState().stackCard(ebb, maul, true, false, false);
		ebb.setCombatCard(true);
		assertEquals(Zone.STACKED_FACE_DOWN, ebb.getZone());
		assertTrue(ebb.isCombatCard());

		int lsLifeBefore = scn.GetLSLifeForceRemaining();

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSForceDrainAvailable(dsSite));
		scn.LSForceDrainAt(dsSite);

		assertTrue(scn.DSCardPlayAvailable(ebb, "Reveal combat card"));
		scn.DSPlayCard(ebb, "Reveal combat card");
		scn.PassAllResponses();

		assertEquals(Zone.TOP_OF_LOST_PILE, ebb.getZone());
		assertFalse(scn.IsActiveForceDrain());
		assertEquals(lsLifeBefore, scn.GetLSLifeForceRemaining());
	}

	@Test
	public void TheEbbOfBattleCancelForceDrainNotWhenStackedAsNonCombatSupporting() {
		var scn = GetScenario();
		var ebb = scn.GetDSCard("ebb");
		var maul = scn.GetDSCard("maul");
		var luke = scn.GetLSCard("luke");
		var dsSite = scn.GetDSStartingLocation();
		var lsSite = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(lsSite, maul);
		scn.MoveCardsToLocation(dsSite, luke);

		// Stacked under Dark Jedi but NOT a combat card
		scn.StackCardsOn(maul, ebb);
		assertFalse(ebb.isCombatCard());

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSForceDrainAvailable(dsSite));
		scn.LSForceDrainAt(dsSite);

		assertFalse(scn.DSCardPlayAvailable(ebb, "Reveal combat card"));
		scn.PassForceDrainStartResponses();
		scn.PassForceDrainEndResponses();
	}

	@Test
	public void TheEbbOfBattleCancelForceDrainNotWhenCombatCardUnderJediNotDarkJedi() {
		var scn = GetScenario();
		var ebb = scn.GetDSCard("ebb");
		var obi = scn.GetLSCard("obi");
		var luke = scn.GetLSCard("luke");
		var dsSite = scn.GetDSStartingLocation();
		var lsSite = scn.GetLSStartingLocation();

		scn.StartGame();
		// Doc: LS steals and stacks as combat card under a Jedi (not Dark Jedi) — Action3 must not fire
		scn.MoveCardsToLocation(lsSite, obi);
		scn.MoveCardsToLocation(dsSite, luke);
		scn.MoveCardsToDSHand(ebb);
		ebb.setOwner(scn.LS);
		scn.StackCardsOn(obi, ebb);
		ebb.setCombatCard(true);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSForceDrainAvailable(dsSite));
		scn.LSForceDrainAt(dsSite);

		// Not under Dark Jedi — Action3 must not be available to either player
		assertFalse(scn.DSCardPlayAvailable(ebb, "Reveal combat card"));
		// LS may not hold the current decision here; DS Action3 gate is the VHD check
		if (scn.IsActiveForceDrain()) {
			scn.PassForceDrainStartResponses();
			scn.PassForceDrainEndResponses();
		}
	}
}
