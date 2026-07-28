package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_14_003_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("artoo","14_003"); //Artoo, Brave Little Droid
					put("tapes","203_014"); //stolen data tapes
					put("duneSea","1_130");
					put("ywing","1_147");
				}},
				new HashMap<>()
				{{
					put("zimh", "110_012"); //zuckuss in mist hunter
					put("darkTime", "201_033"); //a dark time for the rebellion (V)
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

	@Test
	public void ArtooBraveLittleDroidStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Artoo, Brave Little Droid
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Character
		 * Subtype: Droid
		 * Model: Astromech
		 * Destiny: 6
		 * Deploy: 4
		 * Power: 1
		 * Forfeit: 4
		 * Icons: Droid, Nav Computer, Theed Palace, Episode 1
		 * Game Text: While aboard any starfighter, adds 2 to its power and hyperspeed. While in battle at a system,
		 * 		may place Artoo in Used Pile to cancel a just-drawn battle destiny. Cancels Lateral Damage targeting
		 * 		a starship at same system.
		 * Lore: Starship maintenance droid within the Naboo droid pool. Personally responsible for saving
		 * 		Amidala's starship and getting her to Tatooine.
		 * Set: Theed Palace
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("artoo").getBlueprint();

		assertEquals("Artoo, Brave Little Droid", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(6, card.getDestiny(), scn.epsilon);
		assertEquals(4, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertEquals(0, card.getAbility(), scn.epsilon);
		assertEquals(4, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.DROID);
		}});
		scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
			add(ModelType.ASTROMECH);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
			add(Persona.R2D2);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.DROID);
			add(Icon.NAV_COMPUTER);
			add(Icon.THEED_PALACE);
			add(Icon.EPISODE_I);
		}});
		assertEquals(ExpansionSet.THEED_PALACE,card.getExpansionSet());
		assertEquals(Rarity.R,card.getRarity());
	}

	@Test
	public void ArtooBraveLittleDroidCanCancelBattleDestiny() {
		//test1: can take action after just drawn battle destiny to cancel it
		//test2: artoo goes to used pile
		//test3: battle destiny is canceled
		var scn = GetScenario();

		var artoo = scn.GetLSCard("artoo");
		var ywing = scn.GetLSCard("ywing");
		var system = scn.GetLSStartingLocation();

		var zimh = scn.GetDSCard("zimh");

        scn.StartGame();

		scn.MoveCardsToLocation(system,ywing,zimh);
		scn.BoardAsPassenger(ywing,artoo); //not properly applying +2 power as a passenger

		scn.SkipToPhase(Phase.BATTLE);
		scn.PrepareDSDestiny(7);
		scn.DSInitiateBattle(system);
		scn.PassAllResponses();
		scn.SkipToPowerSegment();

		scn.DSChooseYes(); //draw 1 battle destiny?

		scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		assertTrue(scn.LSCardActionAvailable(artoo,"Cancel")); //test1
		scn.LSUseCardAction(artoo,"Cancel");
		//scn.PassAllResponses();

		scn.DSPass(); //ABOUT_TO_BE_PLACE_IN_CARD_PILE_FROM_TABLE - Optional responses
		scn.LSPass();

		scn.DSPass(); //PUT_IN_USED_PILE_FROM_TABLE - Optional responses
		scn.LSPass();

		scn.DSPass(); //DESTINY_DRAWN - Optional responses
		scn.LSPass();

		scn.LSPass(); //BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER - Optional responses
		scn.DSPass();

		scn.DSPass(); //BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER - Optional responses
		scn.LSPass();

		scn.LSPass(); //BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS - Optional responses
		scn.DSPass();

		scn.LSPass(); //INITIAL_ATTRITION_CALCULATED - Optional responses
		scn.DSPass();

		assertEquals(Zone.TOP_OF_USED_PILE, artoo.getZone()); //test2
		assertEquals(2,scn.GetUnpaidLSBattleDamage()); //test3 (2 battle damage, implies destiny 7 was canceled)

		scn.LSPayRemainingBattleDamageFromReserveDeck();

		assertTrue(scn.AwaitingLSBattlePhaseActions());
		assertEquals(2,scn.GetLSLostPileCount()); //test3
	}

	@Test
	public void ArtooBraveLittleDroidCannotCancelBattleDestinyIfPrevented() {
		//test1: if battle destinies cannot be canceled, artoo cannot use action
		var scn = GetScenario();

		var artoo = scn.GetLSCard("artoo");
		var ywing = scn.GetLSCard("ywing");
		var system = scn.GetLSStartingLocation();

		var zimh = scn.GetDSCard("zimh");
		var darkTime = scn.GetDSCard("darkTime");

		scn.StartGame();

		scn.MoveCardsToLocation(system,ywing,zimh);
		scn.BoardAsPassenger(ywing,artoo); //not properly applying +2 power as a passenger

		scn.MoveCardsToDSHand(darkTime);

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(darkTime,"Affect battle destiny draws");
		scn.PassAllResponses();

		scn.SkipToPhase(Phase.BATTLE);
		scn.PrepareDSDestiny(7);
		scn.DSInitiateBattle(system);
		scn.PassAllResponses();
		scn.SkipToPowerSegment();

		scn.DSChooseYes(); //draw 1 battle destiny?

		scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		assertFalse(scn.LSCardActionAvailable(artoo,"Cancel")); //test1
	}

	//shows: https://github.com/PlayersCommittee/gemp-swccg-public/issues/944
	@Test @Ignore
	public void ArtooBraveLittleDroidCanCancelBattleDestinyWithStolenDataTapes() {
		//test1: with stolen data tapes on artoo: can take action after just drawn battle destiny to cancel it
		//test2: with stolen data tapes on artoo: artoo goes to used pile
		//test3: with stolen data tapes on artoo: battle destiny is canceled
		var scn = GetScenario();

		var artoo = scn.GetLSCard("artoo");
		var ywing = scn.GetLSCard("ywing");
		var tapes = scn.GetLSCard("tapes");
		var duneSea = scn.GetLSCard("duneSea");
		var system = scn.GetLSStartingLocation();

		var zimh = scn.GetDSCard("zimh");

		scn.StartGame();

		scn.MoveLocationToTable(duneSea);

		scn.MoveCardsToLocation(system,ywing,zimh);
		scn.BoardAsPassenger(ywing,artoo); //not properly applying +2 power as a passenger
		scn.AttachCardsTo(artoo,tapes);

		scn.SkipToPhase(Phase.BATTLE);
		scn.PrepareDSDestiny(7);
		scn.DSInitiateBattle(system);
		scn.PassAllResponses();
		scn.SkipToPowerSegment();

		scn.DSChooseYes(); //draw 1 battle destiny?

		scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		assertTrue(scn.LSCardActionAvailable(artoo,"Cancel")); //test1
		scn.LSUseCardAction(artoo,"Cancel");

		scn.DSPass(); //ABOUT_TO_BE_PLACE_IN_CARD_PILE_FROM_TABLE - Optional responses
		scn.LSPass();

			///stolen data tapes actions (start)
		scn.DSPass(); //ATTACH_FROM_TABLE - Optional responses
		scn.LSPass();

		scn.DSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses
		scn.LSPass();
			///stolen data tapes actions (end?)

		scn.DSPass(); //PUT_IN_USED_PILE_FROM_TABLE - Optional responses
		scn.LSPass();

		scn.DSPass(); //DESTINY_DRAWN - Optional responses
		scn.LSPass();

		///Problem is here? Destiny should not be completed
		scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
		scn.DSPass();

		scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
		scn.DSPass();

		scn.LSPass(); //BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER - Optional responses
		scn.DSPass();

		scn.DSPass(); //BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER - Optional responses
		scn.LSPass();

		scn.LSPass(); //BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS - Optional responses
		scn.DSPass();

		scn.LSPass(); //INITIAL_ATTRITION_CALCULATED - Optional responses
		scn.DSPass();

		assertEquals(Zone.TOP_OF_USED_PILE, artoo.getZone()); //test2
		assertEquals(2,scn.GetUnpaidLSBattleDamage()); //test3 (2 battle damage, implies destiny 7 was canceled)

		scn.LSPayRemainingBattleDamageFromReserveDeck();

		assertTrue(scn.AwaitingLSBattlePhaseActions());
		assertEquals(2,scn.GetLSLostPileCount()); //test3 (2 battle damage, implies destiny 7 was canceled)
	}

}
