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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_3_119_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
					put("arcona", "2_001");
					put("evram", "2_004"); //commander evram lajaie (allow starships to move to related sys as react)
					put("xwing","1_146");
					put("ywing","1_147");
				}},
				new HashMap<>() {{
					put("comscan", "3_119"); //ComScan Detection
					put("walker","104_005"); //imperial walker (piloted vehicle with landspeed)
					put("devastator","1_302"); //(piloted starship with hyperspeed)
					put("tie","1_304");
					put("yav_par4","1_296");
					put("yav_db","1_297");
					put("hoth_par5","3_143");
					put("hoth_db","3_147");
					put("tat_par7","1_289");
					put("tat_db","1_291");
					put("kess_par8","1_288");
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
	public void ComScanDetectionStatsAndKeywordsAreCorrect() {
		/**
		 * Title: ComScan Detection
		 * Uniqueness: Unrestricted
		 * Side: Dark
		 * Type: Interrupt
		 * Subtype: Used
		 * Destiny: 4
		 * Icons: Hoth
		 * Game Text: If opponent just moved a character, vehicle, or starship as a 'react' to a location, you may
		 * 		immediately move one of your vehicles or starships, if within range, to that location (as a regular move).
		 * Lore: The Imperial Navy boasts the best communications network in the galaxy. Sophisticated control technology allows the Empire to dispatch armed forces without delay.
		 * Set: Hoth
		 * Rarity: C2
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("comscan").getBlueprint();

		assertEquals("ComScan Detection", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
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
		assertEquals(Rarity.C2, card.getRarity());
	}

	@Test
	public void ComScanDetectionCanMoveVehicleToReactDestination() {
		//basic test confirming able to move a vehicle in landspeed range to a location that opponent's card 'react' moved to
		//test1: able to play interrupt from hand
		//test2: able to choose a vehicle in range
		//test3: paid force to perform landspeed move
		//test4: successfully moved to destination
		//tets5: card went to used pile
		var scn = GetScenario();

		var comscan = scn.GetDSCard("comscan");
		var walker = scn.GetDSCard("walker");
		var tat_db = scn.GetDSCard("tat_db");
		var marketplace = scn.GetDSStartingLocation();

		var arcona = scn.GetLSCard("arcona");

		scn.StartGame();

		scn.MoveCardsToDSHand(comscan);

		scn.MoveLocationToTable(tat_db);

		scn.MoveCardsToLocation(tat_db,walker,arcona);

		scn.LSActivateForceCheat(1); //to let arcona react away

		scn.SkipToPhase(Phase.BATTLE);

		assertEquals(3,scn.GetDSForcePileCount()); //and enough force to battle and pay move cost
		scn.DSInitiateBattle(tat_db);

		scn.LSUseCardAction(arcona,"away");
		scn.LSChooseCard(marketplace);

		scn.DSPass(); //Use 1 Force - Optional responses
		scn.LSPass();

		assertFalse(scn.DSCardPlayAvailable(comscan));
		scn.DSPass(); //Moving away
		scn.LSPass();

		assertFalse(scn.DSCardPlayAvailable(comscan));
		scn.DSPass(); //MOVING_USING_LANDSPEED - Optional responses
		scn.LSPass();

		assertTrue(scn.DSCardPlayAvailable(comscan)); //test1: Moved ... optional response
		scn.DSPlayCard(comscan);

		assertTrue(scn.DSDecisionAvailable("Choose vehicle or starship"));
		assertTrue(scn.DSHasCardChoiceAvailable(walker)); //test2
		scn.DSChooseCard(walker);

		scn.LSPass(); //Playing <div class='cardHint' value='3_119'>ComScan Detection</div> - Optional responses
		scn.DSPass();

		scn.LSPass(); //Use 1 Force - Optional responses
		scn.DSPass();

		scn.LSPass(); //MOVING_USING_LANDSPEED - Optional responses
		scn.DSPass();

		scn.LSPass(); //MOVED_USING_LANDSPEED - Optional responses
		scn.DSPass();

		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSBattlePhaseActions());
		assertEquals(1,scn.GetDSForcePileCount()); //test3: 1 to battle, 1 to move
		assertTrue(scn.CardsAtLocation(marketplace,arcona,walker)); //test4
		assertEquals(3,scn.GetDSUsedPileCount()); //test5: 1 to battle, 1 to move, ComScan to Used
	}

	@Test
	public void ComScanDetectionCanMoveStarshipToReactDestination() {
		//basic test confirming able to move a starship in hyperspace range to a location that opponent's card 'react' moved to
		//test1: able to play interrupt from hand
		//test2: able to choose a starship in range
		//test3: paid force to perform hyperspeed move
		//test4: successfully moved to destination
		var scn = GetScenario();

		var comscan = scn.GetDSCard("comscan");
		var devastator = scn.GetDSCard("devastator");
		var tie = scn.GetDSCard("tie");
		var kess_par8 = scn.GetDSCard("kess_par8");
		var tat_par7 = scn.GetDSCard("tat_par7");
		var hoth_par5 = scn.GetDSCard("hoth_par5");
		var hoth_db = scn.GetDSCard("hoth_db");

		var xwing = scn.GetLSCard("xwing");
		var ywing = scn.GetLSCard("ywing");
		var evram = scn.GetLSCard("evram");

		scn.StartGame();

		scn.MoveCardsToDSHand(comscan);

		scn.MoveLocationToTable(kess_par8);
		scn.MoveLocationToTable(tat_par7);
		scn.MoveLocationToTable(hoth_par5);
		scn.MoveLocationToTable(hoth_db);

		scn.MoveCardsToLocation(hoth_db,evram);
		scn.MoveCardsToLocation(hoth_par5, ywing, tie);
		scn.MoveCardsToLocation(tat_par7, xwing);
		scn.MoveCardsToLocation(kess_par8, devastator);

		scn.LSActivateForceCheat(1); //to let xwing react

		scn.SkipToPhase(Phase.BATTLE);

		assertEquals(3,scn.GetDSForcePileCount()); //and enough force to battle and pay move cost
		scn.DSInitiateBattle(hoth_par5);

		scn.LSUseCardAction(evram,"react");
		scn.LSChooseCard(xwing);

		scn.DSPass(); //Use 1 Force - Optional responses
		scn.LSPass();

		assertFalse(scn.DSCardPlayAvailable(comscan));
		scn.DSPass(); //Moving X-wing ... as a 'react' - Optional responses
		scn.LSPass();

		assertFalse(scn.DSCardPlayAvailable(comscan));
		scn.DSPass(); //MOVING_USING_HYPERSPEED - Optional responses
		scn.LSPass();

		assertTrue(scn.DSCardPlayAvailable(comscan)); //test1: Moved ... optional response
		scn.DSPlayCard(comscan);

		assertTrue(scn.DSDecisionAvailable("Choose vehicle or starship"));
		assertTrue(scn.DSHasCardChoiceAvailable(devastator)); //test2
		scn.DSChooseCard(devastator);

		scn.LSPass(); //Playing <div class='cardHint' value='3_119'>ComScan Detection</div> - Optional responses
		scn.DSPass();

		scn.LSPass(); //Use 1 Force - Optional responses
		scn.DSPass();

		scn.LSPass(); //MOVING_USING_HYPERSPEED - Optional responses
		scn.DSPass();

		scn.LSPass(); //MOVED_USING_HYPERSPEED - Optional responses
		scn.DSPass();

		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSWeaponsSegmentActions());
		assertEquals(1,scn.GetDSForcePileCount()); //test3: 1 to battle, 1 to move
		assertTrue(scn.CardsAtLocation(hoth_par5, devastator, xwing)); //test4
	}

	@Test
	public void ComScanDetectionCannotMoveStarshipOutOfRange() {
		//test1: not able to play to move a starship out of range
		//(devastator hyperspeed 3 can't make a 4 parsec hyperspeed move)
		var scn = GetScenario();

		var comscan = scn.GetDSCard("comscan");
		var devastator = scn.GetDSCard("devastator");
		var tie = scn.GetDSCard("tie");
		var kess_par8 = scn.GetDSCard("kess_par8");
		var tat_par7 = scn.GetDSCard("tat_par7");
		var yav_par4 = scn.GetDSCard("yav_par4");
		var yav_db = scn.GetDSCard("yav_db");

		var xwing = scn.GetLSCard("xwing");
		var ywing = scn.GetLSCard("ywing");
		var evram = scn.GetLSCard("evram");

		scn.StartGame();

		scn.MoveCardsToDSHand(comscan);

		scn.MoveLocationToTable(kess_par8);
		scn.MoveLocationToTable(tat_par7);
		scn.MoveLocationToTable(yav_par4);
		scn.MoveLocationToTable(yav_db);

		scn.MoveCardsToLocation(yav_db,evram);
		scn.MoveCardsToLocation(yav_par4, ywing, tie);
		scn.MoveCardsToLocation(tat_par7, xwing);
		scn.MoveCardsToLocation(kess_par8, devastator);

		scn.LSActivateForceCheat(1); //to let xwing react

		scn.SkipToPhase(Phase.BATTLE);

		assertEquals(3,scn.GetDSForcePileCount()); //and enough force to battle and pay move cost
		scn.DSInitiateBattle(yav_par4);

		scn.LSUseCardAction(evram,"react");
		scn.LSChooseCard(xwing);

		scn.DSPass(); //Use 1 Force - Optional responses
		scn.LSPass();

		assertFalse(scn.DSCardPlayAvailable(comscan));
		scn.DSPass(); //Moving X-wing ... as a 'react' - Optional responses
		scn.LSPass();

		assertFalse(scn.DSCardPlayAvailable(comscan));
		scn.DSPass(); //MOVING_USING_HYPERSPEED - Optional responses
		scn.LSPass();

		assertTrue(scn.DSDecisionAvailable("MOVED")); //MOVED_USING_HYPERSPEED - Optional responses
		assertFalse(scn.DSCardPlayAvailable(comscan)); //test1
	}

	@Test
	public void ComScanDetectionCannotMoveVehicleIfCannotPayMoveCost() {
		//test1: not able to play if 0 force available to pay move 1 cost
		var scn = GetScenario();

		var comscan = scn.GetDSCard("comscan");
		var walker = scn.GetDSCard("walker");
		var tat_db = scn.GetDSCard("tat_db");
		var marketplace = scn.GetDSStartingLocation();

		var arcona = scn.GetLSCard("arcona");

		scn.StartGame();

		scn.MoveCardsToDSHand(comscan);

		scn.MoveLocationToTable(tat_db);

		scn.MoveCardsToLocation(tat_db,walker,arcona);

		scn.LSActivateForceCheat(1); //to let arcona react away

		scn.SkipToPhase(Phase.BATTLE);
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
		scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());

		assertEquals(1,scn.GetDSForcePileCount()); //enough force to battle, but not enough to pay move cost
		scn.DSInitiateBattle(tat_db);

		scn.LSUseCardAction(arcona,"away");
		scn.LSChooseCard(marketplace);

		scn.DSPass(); //Use 1 Force - Optional responses
		scn.LSPass();

		assertFalse(scn.DSCardPlayAvailable(comscan));
		scn.DSPass(); //Moving away
		scn.LSPass();

		assertFalse(scn.DSCardPlayAvailable(comscan));
		scn.DSPass(); //MOVING_USING_LANDSPEED - Optional responses
		scn.LSPass();

		assertFalse(scn.DSCardPlayAvailable(comscan)); //test1
	}

	//not playable tests:
	//already used regular move

}


