package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.TestBase.DS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_3_137_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
				}},
				new HashMap<>() {{
					put("thatsit", "3_137");
					//put("zimh","110_12"); //starfighter
					put("devastator","1_302"); //capital
					put("tie","1_304"); //no hyperspeed
					put("probedroid1","3_90");
					put("probedroid2","3_90");
					put("probedroid3","3_90");
					put("kiff_par2","2_147");
					put("ral_par3","2_148");
					put("yav_par4","1_296");
					put("yav_db","1_297");
					put("hoth_par5","3_143");
					put("hoth_db","3_147");
					put("kash_par6","2_146");
					put("tat_par7","1_289");
					put("tat_db","1_291");
					put("kess_par8","1_288");
					put("deathstar","2_143");
					put("clouds1","5_174");
					put("clouds2","5_174");
					put("undercover","2_129");
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
	public void ThatsItStatsAndKeywordsAreCorrect() {
		/**
		 * Title: That's It, The Rebels Are There!
		 * Uniqueness: Unrestricted
		 * Side: Dark
		 * Type: Interrupt
		 * Subtype: Used
		 * Destiny: 4
		 * Icons: Hoth
		 * Game Text: If you have a probe droid at a site during your control phase, move one of your starships to the related system. That starship cannot move again this turn.
		 * Lore: 'That is the system and I'm sure Skywalker is with them.'
		 * Set: Hoth
		 * Rarity: U2
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("thatsit").getBlueprint();

		assertEquals("That's It, The Rebels Are There!", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(CardSubtype.USED, card.getCardSubtype());
		assertEquals(4, card.getDestiny(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.INTERRUPT);
			add(Icon.HOTH);
		}});
		assertEquals(ExpansionSet.HOTH,card.getExpansionSet());
		assertEquals(Rarity.U2, card.getRarity());
	}

	@Test
	public void ThatsItRequiresYourProbeDroidAtRelatedSite() {
		//test coverage:
		//cannot play if your probe droid is at unrelated site
		//cannot play if opponent's probe droid is at related site
		//can play if your probe droid at related site
		//can play if your undercover probe droid at related site

		var scn = GetScenario();

		var thatsit = scn.GetDSCard("thatsit");
		var probedroid1 = scn.GetDSCard("probedroid1");
		var probedroid2 = scn.GetDSCard("probedroid2");
		var probedroid3 = scn.GetDSCard("probedroid3");
		var devastator = scn.GetDSCard("devastator");

		var hoth_par5 = scn.GetDSCard("hoth_par5");
		var hoth_db = scn.GetDSCard("hoth_db");
		var tat_db = scn.GetDSCard("tat_db");
		var kess_par8 = scn.GetDSCard("kess_par8");
		var undercover = scn.GetDSCard("undercover");

		scn.StartGame();

		scn.MoveCardsToDSHand(thatsit,probedroid1,probedroid3,undercover);

		scn.MoveLocationToTable(hoth_par5);
		scn.MoveLocationToTable(hoth_db);
		scn.MoveLocationToTable(tat_db);
		scn.MoveLocationToTable(kess_par8);

		scn.MoveCardsToLocation(kess_par8,devastator);
		scn.MoveCardsToLocation(tat_db,probedroid2);

		scn.DSActivateMaxForceAndPass();

		assertTrue(scn.CardsAtLocation(kess_par8,devastator));
		assertEquals(8,kess_par8.getBlueprint().getParsec());
		assertEquals(5,hoth_par5.getBlueprint().getParsec());
		assertEquals(3,devastator.getBlueprint().getHyperspeed(), scn.epsilon); //devastator is in range of hoth
		assertTrue(scn.GetDSForcePileCount() >= 1); //and enough force to pay move cost

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.CardsAtLocation(tat_db,probedroid2));
		assertFalse(scn.DSCardPlayAvailable(thatsit)); //verifies unable to play with your probe droid at unrelated site

		scn.SkipToPhase(Phase.DEPLOY);
		scn.MoveCardsToLocation(hoth_db,probedroid3); //cheat onto table
		probedroid3.setOwner(scn.GetOpponent()); //steal from DS to LS (from StealOneCardToLocationEffect)
		probedroid3.setZoneOwner(scn.GetOpponent());
		assertTrue(probedroid3.isStolen());

		scn.SkipToLSTurn();

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.CardsAtLocation(hoth_db,probedroid3));
		assertNotEquals(devastator.getOwner(),probedroid3.getOwner());
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertFalse(scn.DSCardPlayAvailable(thatsit)); //verifies unable to play with opponent's probe droid at related site

		scn.SkipToDSTurn(Phase.DEPLOY);
		assertTrue(scn.GetDSForcePileCount() >= probedroid1.getBlueprint().getDeployCost()); //enough to deploy probe droid
		scn.DSDeployCardAndPassResponses(probedroid1,hoth_db); //now, probe droid conditions are met

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.DSCardPlayAvailable(thatsit)); //verifies eligible when probe droid at hoth site and starship in range of hoth

		scn.SkipToDSTurn(Phase.DEPLOY);
		scn.DSPlayCard(undercover);
		scn.DSChooseCard(probedroid1);
		scn.PassAllResponses();
		assertTrue(probedroid1.isUndercover());

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.DSCardPlayAvailable(thatsit)); //verifies eligible when undercover probe droid at hoth site and starship in range of hoth
	}

	@Test
	public void ThatsItRequiresYourControlPhase() {
		//test coverage:
		//can play during your control phase
		//cannot play during one of your non-control phases
		//cannot play during opponent's control phase
		//cannot play if unable to pay movement cost

		var scn = GetScenario();

		var thatsit = scn.GetDSCard("thatsit");
		var probedroid1 = scn.GetDSCard("probedroid1");
		var devastator = scn.GetDSCard("devastator");
		var hoth_par5 = scn.GetDSCard("hoth_par5");
		var hoth_db = scn.GetDSCard("hoth_db");
		var kess_par8 = scn.GetDSCard("kess_par8");

		scn.StartGame();

		scn.MoveCardsToDSHand(thatsit,probedroid1);

		scn.MoveLocationToTable(hoth_par5);
		scn.MoveLocationToTable(hoth_db);
		scn.MoveLocationToTable(kess_par8);

		scn.MoveCardsToLocation(kess_par8,devastator);
		scn.MoveCardsToLocation(hoth_db,probedroid1);

		scn.DSActivateMaxForceAndPass();

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.DSCardPlayAvailable(thatsit)); //verifies able to play during your control phase

		scn.SkipToPhase(Phase.MOVE);
		assertTrue(scn.AwaitingDSMovePhaseActions());
		assertFalse(scn.DSCardPlayAvailable(thatsit)); //verifies unable to play during your non-control phase

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.LSPass();
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertFalse(scn.DSCardPlayAvailable(thatsit)); //verifies unable to play during opponent's control phase

		scn.SkipToDSTurn(Phase.ACTIVATE);
		scn.DSPass();
		scn.DSChooseYes(); //pass without activating
		assertEquals(11,scn.GetDSForcePileCount());
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile()); //spend all (11) force
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
		assertEquals(0,scn.GetDSForcePileCount());
		scn.LSPass();

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertFalse(scn.DSCardPlayAvailable(thatsit)); //verifies unable to play if not enough to pay move cost (1 for hyperspeed)
	}

	@Test
	public void ThatsItMovesViaHyperspeed() {
		//test coverage:
		//cannot choose system out of range for hyperspeed move
		//can choose system in range for hyperspeed move
		//success: cost 1 is paid
		//success: hyperspeed movement completes successfully

		var scn = GetScenario();

		var thatsit = scn.GetDSCard("thatsit");
		var probedroid1 = scn.GetDSCard("probedroid1");
		var probedroid2 = scn.GetDSCard("probedroid2");
		var probedroid3 = scn.GetDSCard("probedroid3");
		var devastator = scn.GetDSCard("devastator");
		var yav_par4 = scn.GetDSCard("yav_par4");
		var yav_db = scn.GetDSCard("yav_db");
		var hoth_par5 = scn.GetDSCard("hoth_par5");
		var hoth_db = scn.GetDSCard("hoth_db");
		var kash_par6 = scn.GetDSCard("kash_par6");
		var tat_db = scn.GetDSCard("tat_db");
		var kess_par8 = scn.GetDSCard("kess_par8");

		scn.StartGame();

		scn.MoveCardsToDSHand(thatsit,probedroid1);

		scn.MoveLocationToTable(yav_par4);
		scn.MoveLocationToTable(yav_db);
		scn.MoveLocationToTable(hoth_par5);
		scn.MoveLocationToTable(hoth_db);
		scn.MoveLocationToTable(kash_par6);
		scn.MoveLocationToTable(tat_db);
		scn.MoveLocationToTable(kess_par8);

		scn.MoveCardsToLocation(tat_db,probedroid2);
		scn.MoveCardsToLocation(yav_db,probedroid3);
		scn.MoveCardsToLocation(kess_par8,devastator);

		scn.DSActivateMaxForceAndPass();
		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.GetDSForcePileCount() >= 1); //enough force to pay move cost
		assertFalse(scn.DSCardPlayAvailable(thatsit)); //verifies unable to play your probe droid at unrelated site or droid at related site but system is out of range

		scn.MoveCardsToLocation(hoth_db,probedroid1);

		scn.SkipToPhase(Phase.MOVE);
		assertTrue(scn.DSCardPlayAvailable(devastator)); //able to move

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.CardsAtLocation(kess_par8,devastator));
		assertEquals(8,kess_par8.getBlueprint().getParsec());
		assertEquals(5,hoth_par5.getBlueprint().getParsec());
		assertEquals(3,devastator.getBlueprint().getHyperspeed(), scn.epsilon); //hoth is in range
		assertTrue(scn.DSCardPlayAvailable(thatsit)); //verifies eligible when probe droid at hoth site and starship in range of hoth

		scn.DSPlayCard(thatsit);
		assertTrue(scn.DSHasCardChoiceAvailable(devastator));
		assertEquals(1,scn.DSGetCardChoiceCount()); //only one eligible ship to choose
		scn.DSChooseCard(devastator);
		assertTrue(scn.DSHasCardChoiceAvailable(hoth_par5)); //3 parsec away is close enough for hyperspeed 3 move
		assertFalse(scn.DSHasCardChoiceAvailable(yav_par4)); //4 parsec away is too far
		assertFalse(scn.DSHasCardChoiceAvailable(kash_par6)); //2 parsec close enough but missing probe droid at related site
		assertEquals(1,scn.DSGetCardChoiceCount()); //only 1 eligible system
		scn.DSChooseCard(hoth_par5);
		scn.LSPass(); //optional move cost response
		scn.DSPass();
		assertEquals(1,scn.GetDSUsedPileCount()); //paid 1 to move
		scn.PassAllResponses(); //many responses: playing thatsit, hyperspeed move attempt, hyperspeed move finished, card in used pile
		assertFalse(scn.CardsAtLocation(kess_par8,devastator));
		assertTrue(scn.CardsAtLocation(hoth_par5,devastator)); //verifies successfully moved
		assertEquals(2,scn.GetDSUsedPileCount()); //thatsit placed on used pile
		assertTrue(scn.AwaitingLSControlPhaseActions());

		scn.SkipToPhase(Phase.MOVE);
		assertTrue(scn.GetDSForcePileCount() >= 1); //enough force to pay move cost
		assertFalse(scn.DSCardPlayAvailable(devastator)); //not allowed to move for rest of turn
	}

	@Test
	public void ThatsItMovesViaTakeOff() {
		//test coverage:
		//can choose related system to move to via take off
		//success: take off is free
		//success: take off completes successfully

		var scn = GetScenario();

		var thatsit = scn.GetDSCard("thatsit");
		var probedroid1 = scn.GetDSCard("probedroid1");
		var tie = scn.GetDSCard("tie");
		var hoth_par5 = scn.GetDSCard("hoth_par5");
		var hoth_db = scn.GetDSCard("hoth_db");

		scn.StartGame();

		scn.MoveCardsToDSHand(thatsit);

		scn.MoveLocationToTable(hoth_par5);
		scn.MoveLocationToTable(hoth_db);

		scn.MoveCardsToLocation(hoth_db,tie); //landed

		scn.DSActivateMaxForceAndPass();
		scn.SkipToPhase(Phase.CONTROL);
		assertFalse(scn.DSCardPlayAvailable(thatsit));

		scn.SkipToLSTurn();
		scn.MoveCardsToLocation(hoth_db,probedroid1);

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(thatsit)); //verifies eligible when probe droid at hoth site and starship is landed

		scn.DSPlayCard(thatsit);
		assertTrue(scn.DSHasCardChoiceAvailable(tie));
		assertEquals(1,scn.DSGetCardChoiceCount()); //only one eligible ship to choose
		scn.DSChooseCard(tie);
		assertTrue(scn.DSHasCardChoiceAvailable(hoth_par5)); //3 parsec away is close enough for hyperspeed 3 move
		assertEquals(1,scn.DSGetCardChoiceCount()); //only 1 eligible system
		scn.DSChooseCard(hoth_par5);
		scn.PassAllResponses();
		assertFalse(scn.CardsAtLocation(hoth_db,tie));
		assertTrue(scn.CardsAtLocation(hoth_par5,tie)); //verifies successful take off
		assertEquals(1,scn.GetDSUsedPileCount()); //thatsit placed on used pile
		assertTrue(scn.AwaitingLSControlPhaseActions());
	}

	@Test
	public void ThatsItMoveViaSector() {
		//test coverage:
		//can choose related system to move to via sector movement
		//cannot choose related system to move to via sector movement if out of range (1+ sectors between ship and system)
		//success: cost 1 is paid
		//success: sector movement completes successfully

		var scn = GetScenario();

		var thatsit = scn.GetDSCard("thatsit");
		var probedroid1 = scn.GetDSCard("probedroid1");
		var tie = scn.GetDSCard("tie");
		var hoth_par5 = scn.GetDSCard("hoth_par5");
		var hoth_db = scn.GetDSCard("hoth_db");
		var clouds1 = scn.GetDSCard("clouds1");
		var clouds2 = scn.GetDSCard("clouds2");

		scn.StartGame();

		scn.MoveCardsToDSHand(thatsit,clouds1,clouds2);

		scn.MoveLocationToTable(hoth_par5);
		scn.MoveLocationToTable(hoth_db);


		scn.DSActivateMaxForceAndPass();
		scn.SkipToPhase(Phase.DEPLOY);

		scn.DSPlayCard(clouds1);
		scn.PassAllResponses();
		scn.LSPass();

		scn.DSPlayCard(clouds2);
		scn.DSChoose("Left"); //(of clouds1)
		scn.PassAllResponses();
		scn.LSPass();

		scn.MoveCardsToLocation(hoth_db,probedroid1);
		scn.MoveCardsToLocation(clouds2,tie);
		//arranged: hoth_db, clouds2, clouds1, hoth_par5
		//with:     probe1   tie

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.GetDSForcePileCount() >= 1); //enough force to pay move cost
		assertFalse(scn.DSCardPlayAvailable(thatsit)); //unable to reach hoth system with sector movement (too far away)

		scn.SkipToDSTurn(Phase.MOVE);
		scn.DSMoveCard(tie,clouds1);
		scn.PassAllResponses();
		//arranged: hoth_db, clouds2, clouds1, hoth_par5
		//with:     probe1            tie

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(thatsit)); //verifies eligible when probe droid at hoth site and starship can move to system with sector movement

		scn.DSPlayCard(thatsit);
		assertTrue(scn.DSHasCardChoiceAvailable(tie));
		assertEquals(1,scn.DSGetCardChoiceCount()); //only one eligible ship to choose
		scn.DSChooseCard(tie);
		assertTrue(scn.DSHasCardChoiceAvailable(hoth_par5)); //1 sector move away
		assertEquals(1,scn.DSGetCardChoiceCount()); //only 1 eligible system
		scn.DSChooseCard(hoth_par5);
		scn.PassAllResponses();
		assertFalse(scn.CardsAtLocation(clouds2,tie));
		assertTrue(scn.CardsAtLocation(hoth_par5,tie)); //verifies successful sector move
		assertEquals(2,scn.GetDSUsedPileCount()); //1 force to move + thatsit placed on used pile
		assertTrue(scn.AwaitingLSControlPhaseActions());
	}

	@Test
	public void ThatsItMovesViaWithoutHyperspace() {
		//test coverage:
		//can choose related system to move to via 'without hyperspace move' (from orbiting Death Star)
		//success: cost 1 is paid
		//success: move without hyperspace completes successfully

		var scn = GetScenario();

		var thatsit = scn.GetDSCard("thatsit");
		var probedroid1 = scn.GetDSCard("probedroid1");
		var tie = scn.GetDSCard("tie");
		var hoth_par5 = scn.GetDSCard("hoth_par5");
		var hoth_db = scn.GetDSCard("hoth_db");
		var deathstar = scn.GetDSCard("deathstar");

		scn.StartGame();

		scn.MoveCardsToDSHand(thatsit);

		scn.MoveLocationToTable(deathstar);
		scn.MoveLocationToTable(hoth_par5);
		scn.MoveLocationToTable(hoth_db);

		scn.MoveCardsToLocation(hoth_db,probedroid1);
		scn.MoveCardsToLocation(deathstar,tie);

		scn.DSActivateMaxForceAndPass();
		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.GetDSForcePileCount() >= 1); //enough force to pay move cost
		assertFalse(scn.DSCardPlayAvailable(thatsit));

		scn.SkipToPhase(Phase.MOVE);
		assertTrue(scn.DSCardPlayAvailable(deathstar));
		scn.DSPlayCard(deathstar);
		scn.DSChoose("1");
		scn.PassAllResponses();

		scn.SkipToDSTurn(Phase.MOVE);
		scn.DSPlayCard(deathstar);
		scn.DSChoose("2");
		scn.PassAllResponses();

		scn.SkipToDSTurn(Phase.MOVE);
		scn.DSPlayCard(deathstar);
		scn.DSChoose("3");
		scn.PassAllResponses();

		scn.SkipToDSTurn(Phase.MOVE);
		scn.DSPlayCard(deathstar);
		scn.DSChoose("4");
		scn.PassAllResponses();

		scn.SkipToDSTurn(Phase.MOVE);
		scn.DSPlayCard(deathstar);
		scn.DSChoose("5");
		scn.DSChoose("Orbit");
		scn.PassAllResponses();
		assertEquals("Hoth",deathstar.getSystemOrbited()); //finally orbiting

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.CardsAtLocation(deathstar,tie));
		assertTrue(scn.DSCardPlayAvailable(thatsit)); //verifies eligible when probe droid at hoth site and starship is landed

		scn.DSPlayCard(thatsit);
		assertTrue(scn.DSHasCardChoiceAvailable(tie));
		assertEquals(1,scn.DSGetCardChoiceCount()); //only one eligible ship to choose
		scn.DSChooseCard(tie);
		assertTrue(scn.DSHasCardChoiceAvailable(hoth_par5)); //deathstar orbiting hoth - can move without hyperspeed
		assertEquals(1,scn.DSGetCardChoiceCount()); //only 1 eligible system
		scn.DSChooseCard(hoth_par5);
		scn.PassAllResponses();
		assertFalse(scn.CardsAtLocation(deathstar,tie));
		assertTrue(scn.CardsAtLocation(hoth_par5,tie)); //verifies successful move without hyperspeed
		assertEquals(2,scn.GetDSUsedPileCount()); //1 force to move + thatsit placed on used pile
		assertTrue(scn.AwaitingLSControlPhaseActions());
	}
}


