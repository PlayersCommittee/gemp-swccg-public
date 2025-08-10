package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.TestBase.DS;
import static org.junit.Assert.*;

public class Card_11_084_Tests {
	//strings to check and play specific actions on Pit Crews, since multiple available
	String action1 = "Reveal opponent's hand";
	String action2 = "'Repair' a Podracer";
	String action3 = "Make a Podracer 'damaged'";

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
					put("rebeltrooper1", "1_028");
					put("rebeltrooper2", "1_028");
					put("rebeltrooper3", "1_028");
					put("rebeltrooper4", "1_028");
					put("ywing1", "1_147");
					put("ywing2", "1_147");
					put("ywing3", "1_147");
					put("jawa1", "1_012");
					put("jawa2", "1_012");
					put("losingtrack", "11_037");
					put("ls_podracer","11_047"); //anakin's
				}},
				new HashMap<>() {{
					put("pitcrews", "11_084");
					put("arena","11_094");
					put("podracer1","11_098"); //teemto's
					put("podracer2","11_096"); //dud bolt's
					put("boonta","11_079");
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
	public void PitCrewsStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Pit Crews
		 * Uniqueness: Unique
		 * Side: Dark
		 * Type: Interrupt
		 * Subtype: Lost
		 * Destiny: 4
		 * Icons: Tatooine, Episode 1
		 * Game Text: Use 3 Force to reveal opponent's hand. All cards opponent has 3 or more of in hand are lost.
		 * 		OR Use 1 Force to 'repair' your Podracer.
		 * 		OR Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target Podracer is 'damaged.'
		 * Lore: Pit droids are used by Podracer pilots to assist in the maintenance of their racer.
		 * 		While a high standard is usually maintained, sometimes things can get out of hand.
		 * Set: Tatooine
		 * Rarity: U
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("pitcrews").getBlueprint();

		assertEquals("Pit Crews", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(CardSubtype.LOST, card.getCardSubtype());
		assertEquals(4, card.getDestiny(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.INTERRUPT);
			add(Icon.TATOOINE);
			add(Icon.EPISODE_I);
		}});
		assertEquals(ExpansionSet.TATOOINE,card.getExpansionSet());
		assertEquals(Rarity.U, card.getRarity());
	}

	@Test
	public void PitCrewsAction1RemovesThreeOrMoreCopies() {
		//tests action1: Use 3 Force to reveal opponent's hand. All cards opponent has 3 or more of in hand are lost.
		//test coverage:
			//costs 3 force to play
			//reveals LS hand
			//LS cards in hand with > 3 copies (4 troopers) are lost
			//LS cards in hand with 3 copies (3 y-wings) are lost
			//LS cards in hand with < 3 copies (2 jawas) are not lost

		var scn = GetScenario();

		var pitcrews = scn.GetDSCard("pitcrews");

		var rebeltrooper1 = scn.GetLSCard("rebeltrooper1");
		var rebeltrooper2 = scn.GetLSCard("rebeltrooper2");
		var rebeltrooper3 = scn.GetLSCard("rebeltrooper3");
		var rebeltrooper4 = scn.GetLSCard("rebeltrooper4");
		var ywing1 = scn.GetLSCard("ywing1");
		var ywing2 = scn.GetLSCard("ywing2");
		var ywing3 = scn.GetLSCard("ywing3");
		var jawa1 = scn.GetLSCard("jawa1");
		var jawa2 = scn.GetLSCard("jawa2");

		scn.StartGame();

		scn.MoveCardsToDSHand(pitcrews);

		scn.MoveCardsToLSHand(rebeltrooper1,rebeltrooper2,rebeltrooper3,rebeltrooper4);
		scn.MoveCardsToLSHand(ywing1,ywing2,ywing3);
		scn.MoveCardsToLSHand(jawa1,jawa2);

		assertEquals(1, scn.GetDSHandCount()); //pit crews
		assertEquals(9, scn.GetLSHandCount()); //4 troopers + 3 ywings + 2 jawas
		scn.DSActivateMaxForceAndPass();

		assertEquals(3,scn.GetDSForcePileCount()); //enough force to play
		assertTrue(scn.DSCardPlayAvailable(pitcrews, action1));
		scn.DSPlayCard(pitcrews, action1);
		scn.PassAllResponses();

		assertEquals(0,scn.GetDSForcePileCount()); //spent 3 force
		scn.DSDismissRevealedCards(); //DS sees revealed opponent's hand

		assertEquals(7,scn.LSGetSelectableCount()); //4 troopers + 3 ywings
		scn.LSChooseCard(rebeltrooper1);
		scn.PassAllResponses();
		scn.LSChooseCard(rebeltrooper2);
		scn.PassAllResponses();
		scn.LSChooseCard(rebeltrooper3);
		scn.PassAllResponses();
		scn.LSChooseCard(rebeltrooper4);
		scn.PassAllResponses();
		scn.LSChooseCard(ywing1);
		scn.PassAllResponses();
		scn.LSChooseCard(ywing2);
		scn.PassAllResponses();
		//last card (ywing1) chosen automatically

		assertEquals(7, scn.GetLSLostPileCount()); //4 troopers, 3 ywings
		assertEquals(2, scn.GetLSHandCount()); //2 jawas

		assertEquals(1, scn.GetDSLostPileCount()); //pitcrews placed in lost pile
		assertEquals(0, scn.GetDSHandCount());
	}

	@Test
	public void PitCrewsAction2RepairsDamagedPodracer_1() {
		//tests action2: Use 1 Force to 'repair' your Podracer.
		//test coverage:
			//costs 1 force to play
			//can target your damaged podracer
			//repairs your damaged podracer
			//cannot target your undamaged podracer
			//cannot target opponent's damaged podracer

		var scn = GetScenario();

		var pitcrews = scn.GetDSCard("pitcrews");
		var arena = scn.GetDSCard("arena");
		var podracer1 = scn.GetDSCard("podracer1");
		var podracer2 = scn.GetDSCard("podracer2");
		var boonta = scn.GetDSCard("boonta");

		var losingtrack = scn.GetLSCard("losingtrack");
		var ls_podracer = scn.GetLSCard("ls_podracer");

		scn.StartGame();

		scn.MoveCardsToDSHand(pitcrews,arena,podracer1,podracer2,boonta);

		scn.MoveCardsToLSHand(losingtrack);

		assertEquals(5, scn.GetDSHandCount());
		assertEquals(1, scn.GetLSHandCount());

		scn.SkipToPhase(Phase.DEPLOY);
		scn.DSDeployLocation(arena);
		scn.PassAllResponses();
		scn.LSPass();

		scn.MoveCardsToLocation(arena,ls_podracer); //cheat a damaged LS podracer onto table
		ls_podracer.setDamaged(true);
		assertTrue(ls_podracer.isDamaged());

		scn.DSDeployCardAndPassResponses(podracer1,arena);
		scn.LSPass();

		scn.DSDeployCardAndPassResponses(podracer2,arena);
		scn.LSPass();

		scn.DSDeployCardAndPassResponses(boonta,arena); //must have podrace active to play losingtrack
		scn.LSPass();

		scn.DSUseCardAction(boonta); //start podrace
		scn.PassAllResponses();

		assertEquals(5, podracer1.getBlueprint().getDestiny(), scn.epsilon);
		scn.PrepareLSDestiny(6); //guarantee success
		scn.LSPlayCardAndPassResponses(losingtrack,"damage", podracer1);
		assertTrue(podracer1.isDamaged());
		assertFalse(podracer2.isDamaged());
		assertTrue(ls_podracer.isDamaged());

		assertTrue(scn.DSCardPlayAvailable(pitcrews,action2));
		scn.DSPlayCard(pitcrews,action2);
		assertTrue(scn.DSHasCardChoicesAvailable(podracer1)); //your damaged podracer is the only option
		assertFalse(scn.DSHasCardChoicesAvailable(podracer2)); //not damaged
		assertFalse(scn.DSHasCardChoicesAvailable(ls_podracer)); //damaged, but not yours
		scn.DSChooseCard(podracer1);
		scn.PassAllResponses();

		assertFalse(podracer1.isDamaged()); //successfully repaired
		assertEquals(2,scn.GetDSForcePileCount()); //spent 1 force
		assertEquals(1, scn.GetDSLostPileCount()); //pitcrews in lost pile
	}

	@Test
	public void PitCrewsAction2RepairsDamagedPodracer_2() {
		//tests action2: Use 1 Force to 'repair' your Podracer.
		//test coverage:
			//unable to play if not Podracing
			//can select one of your multiple damaged podracers

		var scn = GetScenario();

		var pitcrews = scn.GetDSCard("pitcrews");
		var arena = scn.GetDSCard("arena");
		var podracer1 = scn.GetDSCard("podracer1");
		var podracer2 = scn.GetDSCard("podracer2");
		var boonta = scn.GetDSCard("boonta"); //epic event to start podrace

		scn.StartGame();

		scn.MoveCardsToDSHand(pitcrews,arena,podracer1,podracer2,boonta);

		scn.SkipToPhase(Phase.DEPLOY);
		scn.DSDeployLocation(arena);
		scn.PassAllResponses();
		scn.LSPass();

		scn.MoveCardsToLocation(arena,podracer1,podracer2); //cheat damaged podracers onto table
		podracer1.setDamaged(true);
		podracer2.setDamaged(true);

		scn.DSDeployCardAndPassResponses(boonta,arena); //must have podrace active to play losingtrack
		scn.LSPass();

		assertFalse(scn.DSCardPlayAvailable(pitcrews,action2)); //must have podrace active to play Pit Crews 2nd action

		scn.DSUseCardAction(boonta); //start podrace
		scn.PassAllResponses();
		scn.LSPass();

		assertTrue(podracer1.isDamaged());
		assertTrue(podracer2.isDamaged());
		scn.DSPlayCard(pitcrews,action2);
		assertTrue(scn.DSHasCardChoicesAvailable(podracer1,podracer2)); //both your damaged podracers
		scn.DSChooseCard(podracer2);
		scn.PassAllResponses();

		assertTrue(podracer1.isDamaged()); //not repaired
		assertFalse(podracer2.isDamaged()); //successfully repaired
		assertEquals(2,scn.GetDSForcePileCount()); //spent 1 force
		assertEquals(1, scn.GetDSLostPileCount()); //pitcrews in lost pile
	}

	@Test
	public void PitCrewsAction3CanDamageOwnPodracer() {
		//tests action3: Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target Podracer is 'damaged.'
		//test coverage:
			//unable to play if not Podracing
			//draw success damages own targeted podracer

		var scn = GetScenario();

		var pitcrews = scn.GetDSCard("pitcrews");
		var arena = scn.GetDSCard("arena");
		var podracer1 = scn.GetDSCard("podracer1");
		var podracer2 = scn.GetDSCard("podracer2");
		var boonta = scn.GetDSCard("boonta");

		scn.StartGame();

		scn.MoveCardsToDSHand(pitcrews,arena,podracer1,podracer2,boonta);

		scn.SkipToPhase(Phase.DEPLOY);
		scn.DSDeployLocation(arena);
		scn.PassAllResponses();
		scn.LSPass();

		scn.MoveCardsToLocation(arena,podracer1,podracer2); //cheat podracers onto table

		assertFalse(scn.DSCardPlayAvailable(pitcrews,action3)); //must have podrace active to play Pit Crews 3rd action

		scn.DSDeployCardAndPassResponses(boonta,arena);
		scn.LSPass();

		scn.DSUseCardAction(boonta); //start podrace
		scn.PassAllResponses();
		scn.LSPass();

		assertTrue(scn.DSCardPlayAvailable(pitcrews,action3));
		assertFalse(podracer1.isDamaged());
		assertFalse(podracer2.isDamaged());
		assertEquals(5, podracer1.getBlueprint().getDestiny(), scn.epsilon);
		scn.PrepareDSDestiny(6); //guarantee success
		scn.DSPlayCardAndPassResponses(pitcrews,action3);
		assertTrue(scn.DSHasCardChoicesAvailable(podracer1,podracer2));
		scn.DSChooseCard(podracer1);
		scn.PassAllResponses();

		assertTrue(podracer1.isDamaged());
		assertFalse(podracer2.isDamaged());
	}

	@Test
	public void PitCrewsAction3CanDamageOpponentsPodracer() {
		//tests action3: Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target Podracer is 'damaged.'
		//test coverage:
			//able to target opponent's Podracer
			//draw success damages opponent's podracer

		var scn = GetScenario();

		var pitcrews = scn.GetDSCard("pitcrews");
		var arena = scn.GetDSCard("arena");
		var podracer1 = scn.GetDSCard("podracer1");
		var podracer2 = scn.GetDSCard("podracer2");
		var boonta = scn.GetDSCard("boonta");

		var ls_podracer = scn.GetLSCard("ls_podracer");

		scn.StartGame();

		scn.MoveCardsToDSHand(pitcrews,arena,podracer1,podracer2,boonta);

		scn.SkipToPhase(Phase.DEPLOY);
		scn.DSDeployLocation(arena);
		scn.PassAllResponses();
		scn.LSPass();

		scn.MoveCardsToLocation(arena,ls_podracer,podracer1,podracer2); //cheat podracers onto table

		scn.DSDeployCardAndPassResponses(boonta,arena);
		scn.LSPass();

		scn.DSUseCardAction(boonta); //start podrace
		scn.PassAllResponses();
		scn.LSPass();

		assertFalse(podracer1.isDamaged());
		assertFalse(podracer2.isDamaged());
		assertFalse(ls_podracer.isDamaged());
		assertEquals(3, ls_podracer.getBlueprint().getDestiny(), scn.epsilon);
		scn.PrepareDSDestiny(4); //guarantee success
		scn.DSPlayCardAndPassResponses(pitcrews,action3,ls_podracer);

		assertFalse(podracer1.isDamaged());
		assertFalse(podracer2.isDamaged());
		assertTrue(ls_podracer.isDamaged()); //successfully damaged
	}

	@Test
	public void PitCrewsAction3CanTargetDamagedPodracer() {
		//tests action3: Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target Podracer is 'damaged.'
		//test coverage:
			//can target a damaged Podracer
			//draw success on damaged Podracer stays damaged

		var scn = GetScenario();

		var pitcrews = scn.GetDSCard("pitcrews");
		var arena = scn.GetDSCard("arena");
		var podracer1 = scn.GetDSCard("podracer1");
		var podracer2 = scn.GetDSCard("podracer2");
		var boonta = scn.GetDSCard("boonta");

		var ls_podracer = scn.GetLSCard("ls_podracer");

		scn.StartGame();

		scn.MoveCardsToDSHand(pitcrews,arena,podracer1,podracer2,boonta);

		assertEquals(5, scn.GetDSHandCount());
		assertEquals(0, scn.GetLSHandCount());

		scn.SkipToPhase(Phase.DEPLOY);
		scn.DSDeployLocation(arena);
		scn.PassAllResponses();
		scn.LSPass();

		scn.MoveCardsToLocation(arena,ls_podracer,podracer1,podracer2); //cheat podracers onto table
		ls_podracer.setDamaged(true); //cheat damaged state

		scn.DSDeployCardAndPassResponses(boonta,arena);
		scn.LSPass();

		scn.DSUseCardAction(boonta); //start podrace
		scn.PassAllResponses();
		scn.LSPass();

		assertFalse(podracer1.isDamaged());
		assertFalse(podracer2.isDamaged());
		assertTrue(ls_podracer.isDamaged());
		assertEquals(3, ls_podracer.getBlueprint().getDestiny(), scn.epsilon);
		scn.PrepareDSDestiny(4); //guarantee success
		scn.DSPlayCardAndPassResponses(pitcrews,action3,ls_podracer);

		assertFalse(podracer1.isDamaged());
		assertFalse(podracer2.isDamaged());
		assertTrue(ls_podracer.isDamaged()); //no change - still damaged
	}

	@Test
	public void PitCrewsAction3DrawFailDoesNotDamagePodracer() {
		//tests action3: Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target Podracer is 'damaged.'
		//test coverage:
			//draw failure (destiny too low) does not damage targeted podracer

		var scn = GetScenario();

		var pitcrews = scn.GetDSCard("pitcrews");
		var arena = scn.GetDSCard("arena");
		var podracer1 = scn.GetDSCard("podracer1");
		var podracer2 = scn.GetDSCard("podracer2");
		var boonta = scn.GetDSCard("boonta");

		var ls_podracer = scn.GetLSCard("ls_podracer");

		scn.StartGame();

		scn.MoveCardsToDSHand(pitcrews,arena,podracer1,podracer2,boonta);

		assertEquals(5, scn.GetDSHandCount());
		assertEquals(0, scn.GetLSHandCount());

		scn.SkipToPhase(Phase.DEPLOY);
		scn.DSDeployLocation(arena);
		scn.PassAllResponses();
		scn.LSPass();

		scn.MoveCardsToLocation(arena,ls_podracer,podracer1,podracer2); //cheat podracers onto table

		scn.DSDeployCardAndPassResponses(boonta,arena);
		scn.LSPass();

		scn.DSUseCardAction(boonta); //start podrace
		scn.PassAllResponses();
		scn.LSPass();

		assertFalse(podracer1.isDamaged());
		assertFalse(podracer2.isDamaged());
		assertFalse(ls_podracer.isDamaged());
		assertEquals(3, ls_podracer.getBlueprint().getDestiny(), scn.epsilon);
		scn.PrepareDSDestiny(3); //guarantee failure
		scn.DSPlayCardAndPassResponses(pitcrews,action3);
		assertTrue(scn.DSHasCardChoicesAvailable(podracer1,podracer2,ls_podracer));
		scn.DSChooseCard(ls_podracer);
		scn.PassAllResponses();

		assertFalse(podracer1.isDamaged());
		assertFalse(podracer2.isDamaged());
		assertFalse(ls_podracer.isDamaged()); //failed destiny draw
	}

	@Test
	public void PitCrewsAction3NoDrawDoesNotDamagePodracer() {
		//tests action3: Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target Podracer is 'damaged.'
		//test coverage:
			//draw failure (no destiny) does not damage targeted podracer

		var scn = GetScenario();

		var pitcrews = scn.GetDSCard("pitcrews");
		var arena = scn.GetDSCard("arena");
		var boonta = scn.GetDSCard("boonta");

		var ls_podracer = scn.GetLSCard("ls_podracer");

		scn.StartGame();

		scn.MoveCardsToDSHand(pitcrews,arena,boonta);

		scn.SkipToPhase(Phase.DEPLOY);
		scn.DSDeployLocation(arena);
		scn.PassAllResponses();
		scn.LSPass();

		scn.MoveCardsToLocation(arena,ls_podracer); //cheat podracers onto table

		scn.DSDeployCardAndPassResponses(boonta,arena);
		scn.LSPass();

		//cheat an empty reserve deck
		assertEquals(9,scn.GetDSReserveDeckCount());
		scn.DrawCardsFromReserve(DS,8); //draw up
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSReserveDeck()); //move last card to used
		assertEquals(0,scn.GetDSReserveDeckCount());

		scn.DSUseCardAction(boonta); //start podrace
		scn.PassAllResponses();
		scn.LSPass();

		assertFalse(ls_podracer.isDamaged());
		scn.DSPlayCardAndPassResponses(pitcrews,action3);
		assertTrue(scn.DSHasCardChoicesAvailable(ls_podracer));
		scn.DSChooseCard(ls_podracer);
		scn.PassAllResponses();

		assertFalse(ls_podracer.isDamaged()); //failed destiny draw
		assertEquals(1, scn.GetDSLostPileCount()); //pitcrews in lost pile
	}

	//additional tests to add: verify unable to play action1 and action2 if insufficient force available
}


