package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Wise Advice (7_81): "Your Immediate Effects may deploy for free."
 * Issue 990: that "may" is optional, so Grappling Hook / Immediate Effects get two
 * deploy actions (printed cost and free) when Wise Advice is on table and the player
 * has enough Force. Same coding as Battle Plan's two initiate-battle actions.
 *
 * UX: free deploy is offered first; the paid action states the Force cost
 * (Use X Force Immediate Effects via DefinedByGameTextDeployCostModifier).
 */
public class Card_7_081_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("wiseAdvice", "7_81");
					put("grapplingHook", "2_33");
					put("wyttpou", "2_41");
					put("trooper", "1_28");
					put("walkway", "5_79");
					put("mosEisley", "1_133");
				}},
				new HashMap<>()
				{{
					put("reinforcements", "1_251");
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

	private int countGrabActions(VirtualTableScenario scn) {
		return grabActionTexts(scn).size();
	}

	private List<String> grabActionTexts(VirtualTableScenario scn) {
		List<String> grabActions = new ArrayList<>();
		for (String text : scn.GetLSAvailableActions()) {
			if (text != null && text.toLowerCase().contains("grab")) {
				grabActions.add(text);
			}
		}
		return grabActions;
	}

	/**
	 * DS plays Imperial Reinforcements (top-level Interrupt). Grappling Hook / WYTTPOU
	 * are optional-before grabbers on that play.
	 */
	private void StartInterruptSoGrabberCanRespond(VirtualTableScenario scn, boolean wiseAdviceOnTable, String grabberKey) {
		var site = scn.GetLSCard("mosEisley");
		var trooper = scn.GetLSCard("trooper");
		var reinforcements = scn.GetDSCard("reinforcements");
		var grabber = scn.GetLSCard(grabberKey);
		var wiseAdvice = scn.GetLSCard("wiseAdvice");

		if (wiseAdviceOnTable) {
			scn.MoveCardsToLSHand(grabber, wiseAdvice);
		}
		else {
			scn.MoveCardsToLSHand(grabber);
		}
		scn.MoveCardsToDSHand(reinforcements);

		scn.StartGame();
		scn.MoveLocationToTable(site);
		scn.MoveCardsToLocation(site, trooper);
		scn.LSActivateForceCheat(8);
		scn.DSActivateForceCheat(2);

		if (wiseAdviceOnTable) {
			scn.SkipToLSTurn(Phase.DEPLOY);
			assertTrue(scn.LSDeployAvailable(wiseAdvice) || scn.LSCardPlayAvailable(wiseAdvice));
			scn.LSDeployCard(wiseAdvice);
			scn.PassAllResponses();
			assertEquals(Zone.SIDE_OF_TABLE, wiseAdvice.getZone());
		}

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue("reinforcements not playable; LS=" + scn.GetLSAvailableActions() + " DS=" + scn.GetDSAvailableActions(),
				scn.DSCardPlayAvailable(reinforcements) || scn.DSCardActionAvailable(reinforcements));
		scn.DSPlayCard(reinforcements);
		// Pay cost optional responses, then grabbers see isPlayingCard.
		scn.LSPass();
		scn.DSPass();
	}

	@Test
	public void GrapplingHookHasOneGrabActionWithoutWiseAdvice() {
		var scn = GetScenario();
		var grapplingHook = scn.GetLSCard("grapplingHook");

		StartInterruptSoGrabberCanRespond(scn, false, "grapplingHook");

		assertTrue(scn.LSCardActionAvailable(grapplingHook) || scn.LSCardPlayAvailable(grapplingHook));
		assertEquals(1, countGrabActions(scn));
		assertFalse(scn.LSCardActionAvailable(grapplingHook, "for free"));
	}

	@Test
	public void GrapplingHookHasFreeAndPrintedActionsWhenWiseAdviceIsOnTable() {
		// Issue 990 repro: Wise Advice in play, Grappling Hook in hand, enough Force
		// for printed cost 1. Expect two actions (free first, then paid with Force cost).
		var scn = GetScenario();
		var grapplingHook = scn.GetLSCard("grapplingHook");

		StartInterruptSoGrabberCanRespond(scn, true, "grapplingHook");

		assertTrue(scn.LSCardActionAvailable(grapplingHook) || scn.LSCardPlayAvailable(grapplingHook));
		List<String> grabs = grabActionTexts(scn);
		assertEquals("grab actions: " + grabs, 2, grabs.size());
		assertTrue("first action should be free: " + grabs, grabs.get(0).toLowerCase().contains("for free"));
		assertTrue("second action should state 1 Force: " + grabs, grabs.get(1).contains("for 1 Force"));
		assertTrue(scn.LSCardActionAvailable(grapplingHook, "for free"));
		assertTrue(scn.LSCardActionAvailable(grapplingHook, "for 1 Force"));
	}

	@Test
	public void GrapplingHookPrintedActionWithWiseAdvicePaysOneForce() {
		var scn = GetScenario();
		var grapplingHook = scn.GetLSCard("grapplingHook");

		StartInterruptSoGrabberCanRespond(scn, true, "grapplingHook");

		int forceBefore = scn.GetLSForcePileCount();
		assertTrue(forceBefore >= 1);
		assertEquals(2, countGrabActions(scn));
		assertTrue(scn.LSCardActionAvailable(grapplingHook, "for 1 Force"));

		scn.LSPlayCard(grapplingHook, "for 1 Force");
		scn.PassAllResponses();

		assertEquals(Zone.SIDE_OF_TABLE, grapplingHook.getZone());
		assertEquals(forceBefore - 1, scn.GetLSForcePileCount());
	}

	@Test
	public void GrapplingHookFreeActionWithWiseAdvicePaysZeroForce() {
		var scn = GetScenario();
		var grapplingHook = scn.GetLSCard("grapplingHook");

		StartInterruptSoGrabberCanRespond(scn, true, "grapplingHook");

		int forceBefore = scn.GetLSForcePileCount();
		assertEquals(2, countGrabActions(scn));
		assertTrue(scn.LSCardActionAvailable(grapplingHook, "for free"));

		scn.LSPlayCard(grapplingHook, "for free");
		scn.PassAllResponses();

		assertEquals(Zone.SIDE_OF_TABLE, grapplingHook.getZone());
		assertEquals(forceBefore, scn.GetLSForcePileCount());
	}

	@Test
	public void WhatreYouTryinToPushOnUsHasFreeAndPrintedActionsWhenWiseAdviceIsOnTable() {
		var scn = GetScenario();
		var wyttpou = scn.GetLSCard("wyttpou");

		StartInterruptSoGrabberCanRespond(scn, true, "wyttpou");

		assertTrue(scn.LSCardActionAvailable(wyttpou) || scn.LSCardPlayAvailable(wyttpou));
		List<String> grabs = grabActionTexts(scn);
		assertEquals("grab actions: " + grabs, 2, grabs.size());
		assertTrue("first action should be free: " + grabs, grabs.get(0).toLowerCase().contains("for free"));
		assertTrue("second action should state 3 Force: " + grabs, grabs.get(1).contains("for 3 Force"));
		assertTrue(scn.LSCardActionAvailable(wyttpou, "for free"));
		assertTrue(scn.LSCardActionAvailable(wyttpou, "for 3 Force"));
	}

	@Test
	public void WhatreYouTryinToPushOnUsPrintedActionWithWiseAdvicePaysThreeForce() {
		var scn = GetScenario();
		var wyttpou = scn.GetLSCard("wyttpou");

		StartInterruptSoGrabberCanRespond(scn, true, "wyttpou");

		int forceBefore = scn.GetLSForcePileCount();
		assertTrue(forceBefore >= 3);
		assertEquals(2, countGrabActions(scn));
		assertTrue(scn.LSCardActionAvailable(wyttpou, "for 3 Force"));

		scn.LSPlayCard(wyttpou, "for 3 Force");
		scn.PassAllResponses();

		assertEquals(Zone.SIDE_OF_TABLE, wyttpou.getZone());
		assertEquals(forceBefore - 3, scn.GetLSForcePileCount());
	}
}
