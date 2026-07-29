package com.gempukku.swccgo.cards.set4.dark;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_4_122_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
					put("homeone", "9_074");
					put("xwing","1_146");
					put("alter_ls","1_071");
				}},
				new HashMap<>() {{
					put("flagship","4_122");
					put("vcsd","2_155"); //victory class star destroyer
					put("dreadnaught", "106_013"); //(capital, not star destroyer, hyperspeed)
					put("tie","1_304");
					put("alter_ds","1_234");
					put("bigOne","4_156"); //(asteroid sector)
					put("hoth","3_143"); //parsec 5
					put("hoth_db","3_147");
					put("deflectorV","203_029"); //Deflector Shield Generators (V) (armor +2)
				}},
				10,
				10,
				StartingSetup.DefaultLSSpaceSystem, //parsec 2
				StartingSetup.DefaultDSSpaceSystem, //parsec 5
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void FlagshipStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Flagship
		 * Uniqueness: Unique
		 * Side: Dark
		 * Type: Effect
		 * Subtype: Normal
		 * Destiny: 3
		 * Icons: Dagobah
		 * Game Text: Use 2 Force to deploy on your Star Destroyer. Your other starships may move as a 'react'
		 * 		to same system or sector (for free). If starship about to be lost, you lose X Force,
		 * 		where X = starship's armor.	(Immune to your Alter.)
		 * Lore: After the Battle of Yavin, it was politically necessary to demonstrate the unstoppable might
		 * 		of the Empire. The Executor and Death Squadron ensured this objective.
		 * Set: Dagobah
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("flagship").getBlueprint();

		assertEquals("Flagship", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.EFFECT);
		}});
		assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
		assertEquals(3, card.getDestiny(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.EFFECT);
			add(Icon.DAGOBAH);
		}});
		assertEquals(ExpansionSet.DAGOBAH,card.getExpansionSet());
		assertEquals(Rarity.R, card.getRarity());
	}

	@Test
	public void FlagshipDeploysOnStarDestroyer() {
		//test1: deploys on star destroyer
		//test2: does not deploy on starship that is not a star destroyer
		//test2: costs 2
		//test3: flagship is attached to target
		var scn = GetScenario();

		var flagship = scn.GetDSCard("flagship");
		var vcsd = scn.GetDSCard("vcsd");
		var dreadnaught = scn.GetDSCard("dreadnaught");
		var tie = scn.GetDSCard("tie");
		var system = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToDSHand(flagship);

		scn.MoveCardsToLocation(system,vcsd,dreadnaught,tie);

		scn.SkipToPhase(Phase.DEPLOY);
		assertEquals(0,scn.GetDSUsedPileCount());
		assertTrue(scn.DSDeployAvailable(flagship));
		scn.DSDeployCard(flagship);
		assertTrue(scn.DSHasCardChoiceAvailable(vcsd)); //test1
		assertFalse(scn.DSHasCardChoiceAvailable(dreadnaught)); //test2 (capital)
		assertFalse(scn.DSHasCardChoiceAvailable(tie)); //test2 (starfighter)
		scn.DSChooseCard(vcsd);
		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSDeployPhaseActions());
		assertEquals(2, scn.GetDSUsedPileCount()); //test3
		assertTrue(scn.IsAttachedTo(vcsd,flagship)); //test4
	}

	@Test
	public void FlagshipImmuneToDarkAlter() {
		//test1: immune to owner (ds) alter
		//test2: not immune to opponent's (ls) alter
		var scn = GetScenario();

		var flagship = scn.GetDSCard("flagship");
		var vcsd = scn.GetDSCard("vcsd");
		var alter_ds = scn.GetDSCard("alter_ds");
		var stormtrooper = scn.GetDSFiller(1);
		var system = scn.GetDSStartingLocation();

		var homeone = scn.GetLSCard("homeone");
		var alter_ls = scn.GetLSCard("alter_ls");
		var rebeltrooper = scn.GetLSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(system,vcsd,homeone);

		scn.MoveCardsToDSHand(alter_ds);
		scn.MoveCardsToLSHand(alter_ls);

		scn.AttachCardsTo(vcsd,flagship);
		scn.BoardAsPassenger(vcsd,stormtrooper); //ds character with ability on table
		scn.BoardAsPassenger(homeone,rebeltrooper); //ls character with ability on table

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertFalse(scn.DSCardPlayAvailable(alter_ds)); //test1
		scn.DSPass();

		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(scn.LSCardPlayAvailable(alter_ls));
		scn.LSPlayCard(alter_ls);
		assertTrue(scn.LSHasCardChoiceAvailable(flagship)); //test2
	}

	@Test
	public void FlagshipShipsMayMoveAsReactForFreeToSystem() {
		//test1: cannot move opponent's ship as a react
		//test2: capital ship may move as react to battle location using hyperspeed
		//test3: starfighter may move as react to battle location using take off
		//test4: hyperspeed move was free
		//test5: take off was free
		var scn = GetScenario();

		var flagship = scn.GetDSCard("flagship");
		var vcsd = scn.GetDSCard("vcsd");
		var dreadnaught = scn.GetDSCard("dreadnaught");
		var tie = scn.GetDSCard("tie");
		var hoth = scn.GetDSCard("hoth");
		var hoth_db = scn.GetDSCard("hoth_db");
		var system = scn.GetDSStartingLocation();

		var xwing = scn.GetLSCard("xwing");
		var homeone = scn.GetLSCard("homeone");

		scn.StartGame();

		scn.MoveLocationToTable(hoth);
		scn.MoveLocationToTable(hoth_db);

		scn.MoveCardsToLocation(hoth,vcsd,xwing);
		scn.MoveCardsToLocation(system,dreadnaught,homeone);
		scn.MoveCardsToLocation(hoth_db,tie);

		scn.AttachCardsTo(vcsd,flagship);

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSInitiateBattle(hoth);

		assertTrue(scn.DSCardActionAvailable(flagship, "Move starship"));
		scn.DSUseCardAction(flagship, "Move starship");
		assertFalse(scn.DSHasCardChoiceAvailable(vcsd)); //already in battle
		assertFalse(scn.DSHasCardChoiceAvailable(homeone)); //test1
		assertTrue(scn.DSHasCardChoiceAvailable(dreadnaught)); //test2
		assertTrue(scn.DSHasCardChoiceAvailable(tie));//test3

		scn.DSChooseCard(dreadnaught);

		scn.LSPass(); //Moving
		scn.DSPass();

		scn.LSPass(); //MOVING_USING_HYPERSPEED - Optional responses
		scn.DSPass();

		scn.LSPass(); //MOVED_USING_HYPERSPEED - Optional responses
		scn.DSPass();

		scn.LSPass(); //BATTLE_INITIATED - Optional responses

		assertTrue(scn.CardsAtLocation(hoth, vcsd, xwing, dreadnaught));
		assertEquals(0,scn.GetDSUsedPileCount()); //test4

		scn.DSUseCardAction(flagship, "Move starship");
		assertFalse(scn.DSHasCardChoiceAvailable(dreadnaught));
		assertTrue(scn.DSHasCardChoiceAvailable(tie));//test3

		scn.DSChooseCard(tie);

		scn.LSPass(); //Taking Tie Fighter off
		scn.DSPass();

		scn.LSPass(); //TAKING_OFF - Optional responses
		scn.DSPass();

		scn.LSPass(); //TOOK_OFF - Optional responses
		scn.DSPass();

		scn.DSPass(); //Perform a movement after regular move of the 'react' or Pass

		scn.LSPass(); //BATTLE_INITIATED - Optional responses
		assertTrue(scn.CardsAtLocation(hoth, vcsd, xwing, dreadnaught, tie));
		assertEquals(0,scn.GetDSUsedPileCount()); //test5
	}

	@Test
	public void FlagshipShipsMayMoveAsReactForFreeToSector() {
		//test1: capital ship may move as react to sector battle location (Big One) using sector movement
		//test2: sector move was free

		var scn = GetScenario();

		var flagship = scn.GetDSCard("flagship");
		var vcsd = scn.GetDSCard("vcsd");
		var dreadnaught = scn.GetDSCard("dreadnaught");
		var bigOne = scn.GetDSCard("bigOne");
		var system = scn.GetDSStartingLocation();

		var xwing = scn.GetLSCard("xwing");

		scn.StartGame();

		scn.MoveCardsToDSHand(vcsd,flagship,bigOne);

		scn.MoveCardsToLocation(system,dreadnaught);

		scn.SkipToPhase(Phase.DEPLOY);
		scn.DSDeployCard(bigOne);
		scn.DSChooseCard(system);
		scn.PassAllResponses();

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.MoveCardsToLocation(bigOne,vcsd,xwing);
		scn.AttachCardsTo(vcsd,flagship);

		scn.SkipToPhase(Phase.BATTLE);
		scn.LSInitiateBattle(bigOne);

		assertTrue(scn.DSCardActionAvailable(flagship, "Move starship"));
		scn.DSUseCardAction(flagship, "Move starship");
		assertTrue(scn.DSHasCardChoiceAvailable(dreadnaught)); //test1

		scn.DSChooseCard(dreadnaught);

		scn.LSPass(); //Moving
		scn.DSPass();

		scn.LSPass(); //MOVING_USING_SECTOR_MOVEMENT - Optional responses
		scn.DSPass();

		scn.LSPass(); //MOVED_USING_SECTOR_MOVEMENT - Optional responses
		scn.DSPass();

		scn.LSPass(); //BATTLE_INITIATED - Optional responses

		assertTrue(scn.CardsAtLocation(bigOne, vcsd, xwing, dreadnaught));
		assertEquals(0,scn.GetDSUsedPileCount()); //test2
	}

	@Test
	public void FlagshipXForceLost() {
		//test1: when target ship lost, causes force loss equal to armor (VCSD armor = 5)
		//test2: ship and flagship are lost after force loss
		var scn = GetScenario();

		var flagship = scn.GetDSCard("flagship");
		var vcsd = scn.GetDSCard("vcsd");
		var system = scn.GetDSStartingLocation();

		var homeone = scn.GetLSCard("homeone");

		scn.StartGame();

		scn.MoveCardsToLocation(system,vcsd,homeone);

		scn.AttachCardsTo(vcsd,flagship);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(system);
		scn.PassAllResponses();

		scn.SkipToEndOfPowerSegment(false);
		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSBattleDamagePayment());
		scn.DSChooseCard(vcsd);

		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSForceLossPayment());
		assertEquals(0,scn.GetDSLostPileCount());
		scn.DSPayRemainingForceLossFromReserveDeck();
		assertEquals(5,scn.GetDSLostPileCount()); //test1

		scn.LSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses
		scn.DSPass();

		assertTrue(scn.DSDecisionAvailable("Choose card to put on Lost Pile"));
		assertTrue(scn.DSHasCardChoiceAvailable(flagship));
		assertTrue(scn.DSHasCardChoiceAvailable(vcsd));
		scn.DSChooseCard(flagship);

		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSBattlePhaseActions());
		assertEquals(7,scn.GetDSLostPileCount()); //test2 (5 + vcsd + flagship)
	}

	@Test
	public void FlagshipXForceLostAffectedByArmorModifiers() {
		//test1: when target ship lost, causes force loss equal to armor, including modifiers (VCSD armor = 5 + 2 from deflector)
		var scn = GetScenario();

		var flagship = scn.GetDSCard("flagship");
		var vcsd = scn.GetDSCard("vcsd");
		var deflectorV = scn.GetDSCard("deflectorV");
		var system = scn.GetDSStartingLocation();

		var homeone = scn.GetLSCard("homeone");

		scn.StartGame();

		scn.MoveCardsToLocation(system,vcsd,homeone);

		scn.AttachCardsTo(vcsd,flagship, deflectorV);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(system);
		scn.PassAllResponses();

		scn.SkipToEndOfPowerSegment(false);
		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSBattleDamagePayment());
		scn.DSChooseCard(vcsd);

		scn.PassAllResponses();
		assertEquals(0,scn.GetDSLostPileCount());
		scn.DSPayRemainingForceLossFromReserveDeck();
		assertEquals(7,scn.GetDSLostPileCount()); //test1
	}

	//no effective way to test for free react move to a force drain?

}


