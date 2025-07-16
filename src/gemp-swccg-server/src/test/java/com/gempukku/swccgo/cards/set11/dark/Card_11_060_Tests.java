package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.TestBase.DS;
import static org.junit.Assert.*;

public class Card_11_060_Tests {
	//strings to check and play specific actions
	String action1 = "Place top race destiny in Lost Pile";
	String action2 = "'Repair' a Podracer";

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
					put("ls_podracer","11_047"); //anakin's
					put("bay","11_045");
				}},
				new HashMap<>() {{
					put("pitdroid1", "11_060");
					put("pitdroid2", "11_060");
					put("pitdroid3", "11_060");
					put("arena","11_094");
					put("podracer1","11_098"); //teemto's
					put("podracer2","11_096"); //dud bolt's
					put("boonta","11_079");
					put("walker","104_005");
				}},
				20,
				20,
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
	public void PitDroidStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Pit Droid
		 * Uniqueness: Nonunique
		 * Side: Dark
		 * Type: Character
		 * Subtype: Droid
		 * Destiny: 3
		 * Deploy: 1
		 * Power: 1
		 * Forfeit: 2
		 * Persona: (none)
		 * Icons: Tatooine, Episode 1
		 * Game Text: While at Podracer Bay, once during each of your control phases may lose 1 Force to
		 * 		target your Podracer. Place target's top race destiny in Lost Pile, and draw one race destiny.
		 * 		During any control phase may use 1 Force to 'repair' your Podracer.
		 * Lore: Manufactured by Serv-O-Droid on Cyrillia. Collapses into a compact form when hit on the nose.
		 * Set: Tatooine
		 * Rarity: C
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("pitdroid1").getBlueprint();

		assertEquals("Pit Droid", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.DROID);
		}});
		assertEquals(3, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertEquals(0, card.getAbility(), scn.epsilon);
		assertEquals(1, card.getDeployCost(), scn.epsilon);
		assertEquals(2, card.getForfeit(), scn.epsilon);
		scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
			add(ModelType.REPAIR);
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.DROID);
			add(Icon.TATOOINE);
			add(Icon.EPISODE_I);
		}});
		assertEquals(ExpansionSet.TATOOINE, card.getExpansionSet());
		assertEquals(Rarity.C, card.getRarity());
	}

	@Test
	public void PitDroidAction1RemovesTopRaceDestiny() {
		//tests action1: While at Podracer Bay, once during each of your control phases may lose 1 Force to target your Podracer. Place target's top race destiny in Lost Pile, and draw one race destiny.
		//test coverage:
		//pit droid at Podracer Bay can use action 1
		//pit droid not at Podracer bay cannot use action 1
		//eligible podracer must have a top race destiny
		//cannot use action 1 during your non-control phase
		//cannot use action 1 during opponent's control phase
		//(each) pit droid can only use action 1 once during your control phase

		var scn = GetScenario();

		var pitdroid1 = scn.GetDSCard("pitdroid1");
		var pitdroid2 = scn.GetDSCard("pitdroid2");
		var pitdroid3 = scn.GetDSCard("pitdroid3");
		var podracer1 = scn.GetDSCard("podracer1");
		var podracer2 = scn.GetDSCard("podracer2");
		var arena = scn.GetDSCard("arena");
		var boonta = scn.GetDSCard("boonta");

		var bay = scn.GetLSCard("bay");

		scn.StartGame();
		scn.MoveLocationToTable(bay);
		scn.MoveLocationToTable(arena);

		scn.MoveCardsToDSHand(pitdroid1,pitdroid2,pitdroid3,podracer1,podracer2,boonta);

		scn.MoveCardsToLocation(bay,pitdroid1,pitdroid2);
		scn.MoveCardsToLocation(arena,pitdroid3,podracer1,podracer2,boonta);

		scn.DSActivateMaxForceAndPass();

		scn.DSUseCardAction(boonta); //start podrace
		scn.PassAllResponses();
		scn.LSPass();

		scn.SkipToPhase(Phase.CONTROL);

		assertFalse(scn.DSCardActionAvailable(pitdroid1, action1)); //unavailable, no podracer with destiny stacked

		scn.PrepareDSDestiny(7);
		scn.DSPlayCard(boonta); //draw race destiny
		assertTrue(scn.DSHasCardChoicesAvailable(podracer1,podracer2));
		scn.DSChooseCard(podracer1);
		scn.PassAllResponses();
		scn.DSChooseYes();
		scn.PassAllResponses();
		scn.LSPass();

		assertTrue(scn.DSCardPlayAvailable(pitdroid1, action1)); //podracer now available with destiny stacked
		assertTrue(scn.DSCardPlayAvailable(pitdroid2, action1)); //
		assertFalse(scn.DSCardPlayAvailable(pitdroid3, action1)); //verifies cannot use when not at bay

		scn.SkipToPhase(Phase.DEPLOY);
		assertFalse(scn.DSCardPlayAvailable(pitdroid1, action1)); //podracer with destiny stacked, your turn, but not control phase

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.LSPass();
		assertFalse(scn.DSCardPlayAvailable(pitdroid1, action1)); //podracer with destiny stacked, control phase, but not yours

		scn.SkipToDSTurn(Phase.CONTROL);

		assertEquals(0,scn.GetDSLostPileCount()); //lost pile empty
		scn.DSUseCardAction(pitdroid1);
		assertTrue(scn.DSHasCardChoicesAvailable(podracer1)); //has a stacked race destiny
		assertFalse(scn.DSHasCardChoicesAvailable(podracer2)); //no stacked race destiny
		scn.DSChooseCard(podracer1);
		scn.PassAllResponses();

		scn.DSChooseCard(scn.GetTopOfDSReserveDeck()); //pay cost: lose 1 force
		scn.PassCardLeavingTable();
		scn.PassAllResponses();

		assertTrue(scn.DSHasCardChoicesAvailable(podracer1)); //may draw race destiny with either podracer
		assertTrue(scn.DSHasCardChoicesAvailable(podracer2));
		scn.DSChooseCard(podracer2);
		scn.PassAllResponses();
		scn.DSChooseYes(); //yes to stack as a race destiny
		scn.PassAllResponses();
		scn.LSPass();

		assertEquals(2,scn.GetDSLostPileCount()); //lost top stacked card from podracer

		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertFalse(scn.DSCardPlayAvailable(pitdroid1, action1)); //verifies once per turn action cannot be used again
		assertTrue(scn.DSCardPlayAvailable(pitdroid2, action1)); //podracer available with destiny stacked
		assertFalse(scn.DSCardPlayAvailable(pitdroid3, action1)); //not at bay
	}

	@Test
	public void PitDroidAction1CannotTargetOpponentsPodracer() {
		//tests action1: While at Podracer Bay, once during each of your control phases may lose 1 Force to target your Podracer. Place target's top race destiny in Lost Pile, and draw one race destiny.
		//test coverage:
		//pit droid at Podracer Bay cannot use action 1 to target opponent's Podracer with stacked race destiny

		var scn = GetScenario();

		var pitdroid1 = scn.GetDSCard("pitdroid1");
		var podracer1 = scn.GetDSCard("podracer1");
		var podracer2 = scn.GetDSCard("podracer2");
		var arena = scn.GetDSCard("arena");
		var boonta = scn.GetDSCard("boonta");

		var ls_podracer = scn.GetLSCard("ls_podracer");
		var bay = scn.GetLSCard("bay");

		scn.StartGame();
		scn.MoveLocationToTable(bay);
		scn.MoveLocationToTable(arena);

		scn.MoveCardsToLocation(bay,pitdroid1,ls_podracer);
		scn.MoveCardsToLocation(arena,podracer1,podracer2,boonta);

		scn.DSActivateMaxForceAndPass();

		scn.DSUseCardAction(boonta); //start podrace
		scn.PassAllResponses();
		scn.LSPass();

		scn.SkipToPhase(Phase.CONTROL);

		scn.DSPlayCard(boonta); //draw race destiny
		scn.DSChooseCard(podracer1);
		scn.PassAllResponses();
		scn.DSChooseYes(); //stack it
		scn.PassAllResponses();

		scn.LSPlayCard(boonta); //draw race destiny (2 on anakin's)
		scn.PassAllResponses();
		scn.LSChooseYes(); //stack first
		scn.PassAllResponses();
		scn.LSChooseYes(); //stack second
		scn.PassAllResponses();

		assertTrue(scn.DSCardPlayAvailable(pitdroid1, action1)); //podracer now available with destiny stacked

		assertEquals(0,scn.GetDSLostPileCount()); //lost pile empty
		scn.DSUseCardAction(pitdroid1);
		assertTrue(scn.DSHasCardChoicesAvailable(podracer1)); //has a stacked race destiny
		assertFalse(scn.DSHasCardChoicesAvailable(ls_podracer)); //opponent's with stacked race destiny
	}

	@Test
	public void PitDroidAction1UsableFromEnclosedVehicle() {
		//tests action1: While at Podracer Bay, once during each of your control phases may lose 1 Force to target your Podracer. Place target's top race destiny in Lost Pile, and draw one race destiny.
		//test coverage:
		//pit droid inside enclosed vehicle (at Podracer Bay) can use action 1

		//this is really testing underlying mechanics for 'at' mechanics - move over to mechanics test area later?

		var scn = GetScenario();

		var pitdroid1 = scn.GetDSCard("pitdroid1");
		var podracer1 = scn.GetDSCard("podracer1");
		var arena = scn.GetDSCard("arena");
		var boonta = scn.GetDSCard("boonta");
		var walker = scn.GetDSCard("walker");

		var bay = scn.GetLSCard("bay");

		scn.StartGame();
		scn.MoveLocationToTable(bay);
		scn.MoveLocationToTable(arena);

		scn.MoveCardsToDSHand(pitdroid1);

		scn.MoveCardsToLocation(bay,walker);
		scn.MoveCardsToLocation(arena,podracer1,boonta);

		scn.DSActivateMaxForceAndPass();

		scn.DSUseCardAction(boonta); //start podrace
		scn.PassAllResponses();
		scn.LSPass();

		scn.SkipToPhase(Phase.DEPLOY);
		assertTrue(scn.DSCardPlayAvailable(pitdroid1));
		scn.DSDeployCard(pitdroid1);
		scn.PassAllResponses();
		scn.DSChooseCard(walker);
		scn.PassAllResponses();

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);

		scn.PrepareDSDestiny(7);
		scn.DSPlayCard(boonta); //draw race destiny
		scn.PassAllResponses();
		scn.DSChooseYes();
		scn.PassAllResponses();
		scn.LSPass();

		assertTrue(scn.DSCardPlayAvailable(pitdroid1, action1)); //podracer inside vehicle at bay

		assertEquals(0,scn.GetDSLostPileCount()); //lost pile empty
		scn.DSUseCardAction(pitdroid1);
		assertTrue(scn.DSHasCardChoicesAvailable(podracer1)); //has a stacked race destiny
		scn.DSChooseCard(podracer1);
		scn.PassAllResponses();

		scn.DSChooseCard(scn.GetTopOfDSReserveDeck()); //pay cost: lose 1 force
		scn.PassCardLeavingTable();
		scn.PassAllResponses();

		scn.DSChooseYes(); //yes to stack as a race destiny
		scn.PassAllResponses();
		scn.LSPass();
		assertEquals(2,scn.GetDSLostPileCount()); //lost top stacked card from podracer

		assertTrue(scn.AwaitingDSControlPhaseActions());
	}

	@Test
	public void PitDroidAction2RepairsPodracer() {
		//tests action2: During any control phase may use 1 Force to 'repair' your Podracer.
		//test coverage:
		//can use action 2 during owner's control phase
		//can use action 2 during opponent's control phase
		//cannot use action 2 during a non-control phase
		//can repair owner's damaged podracer
		//cannot repair opponent's podracer
		//cannot use unless podrace active
		//cannot use action 2 without at least 1 owner's damaged podracer

		var scn = GetScenario();

		var pitdroid1 = scn.GetDSCard("pitdroid1");
		var podracer1 = scn.GetDSCard("podracer1");
		var podracer2 = scn.GetDSCard("podracer2");
		var arena = scn.GetDSCard("arena");
		var boonta = scn.GetDSCard("boonta");

		var ls_podracer = scn.GetLSCard("ls_podracer");

		scn.StartGame();
		scn.MoveLocationToTable(arena);

		scn.MoveCardsToDSHand(boonta);

		scn.MoveCardsToLocation(arena,podracer1,podracer2,ls_podracer,pitdroid1,boonta); //cheat cards to table
		podracer1.setDamaged(true);
		podracer2.setDamaged(true);
		ls_podracer.setDamaged(true);

		scn.DSActivateMaxForceAndPass();
		scn.SkipToLSTurn(Phase.CONTROL);
		scn.LSPass();
		assertFalse(scn.DSCardActionAvailable(pitdroid1, action2)); //podrace not active

		scn.DSUseCardAction(boonta); //start podrace
		scn.PassAllResponses();
		scn.LSPass();

		assertTrue(scn.DSCardActionAvailable(pitdroid1, action2)); //verifies can play during opponent's control phase
		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSPass();
		assertFalse(scn.DSCardActionAvailable(pitdroid1, action2)); //verifies cannot play during a non-control phase

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(pitdroid1, action2)); //verifies can play during owner's control phase

		assertEquals(11,scn.GetDSForcePileCount());
		scn.DSUseCardAction(pitdroid1);
		assertTrue(scn.DSHasCardChoicesAvailable(podracer1)); //damaged
		assertTrue(scn.DSHasCardChoicesAvailable(podracer2)); //damaged
		assertFalse(scn.DSHasCardChoicesAvailable(ls_podracer)); //verifies can't repair opponent's damaged racer
		scn.DSChooseCard(podracer1);
		scn.PassAllResponses();
		scn.LSPass();
		assertEquals(10,scn.GetDSForcePileCount()); //verifies cost of 1 force
		assertFalse(podracer1.isDamaged()); //just repaired
		assertTrue(podracer2.isDamaged());
		assertTrue(ls_podracer.isDamaged());

		assertTrue(scn.DSCardActionAvailable(pitdroid1, action2)); //verifies may use more than once per phase
		scn.DSUseCardAction(pitdroid1);
		assertFalse(scn.DSHasCardChoicesAvailable(podracer1)); //repaired already
		assertTrue(scn.DSHasCardChoicesAvailable(podracer2)); //damaged
		scn.DSChooseCard(podracer2);
		scn.PassAllResponses();
		scn.LSPass();
		assertFalse(podracer1.isDamaged());
		assertFalse(podracer2.isDamaged()); //just repaired
		assertTrue(ls_podracer.isDamaged());

		assertFalse(scn.DSCardActionAvailable(pitdroid1, action2)); //verifies may not use if no damaged podracers
	}

	//other tests to consider:
	//test with Neck And Neck to ensure drawing is prevented (this should probably be under Neck And Neck card test, though)
}


