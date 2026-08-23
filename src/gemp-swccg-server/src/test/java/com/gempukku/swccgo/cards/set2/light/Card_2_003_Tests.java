package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
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

public class Card_2_003_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("chewie", "2_003"); //Chewbacca
					put("landspeeder", "1_149"); //Luke's X-34 Landspeeder (non-enclosed vehicle)
					put("c3p0","1_005"); //C-3PO (See-Threepio)
				}},
				new HashMap<>()
				{{
					put("eppVader","108_006");
					put("drE","1_172");
					put("yab","5_163"); //You Are Beaten
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
	public void ChewbaccaStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Chewbacca
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Character
		 * Subtype: Rebel/Alien
		 * Destiny: 1
		 * Deploy: 4
		 * Power: 6
		 * Ability: 2
		 * Forfeit: 6
		 * Icons: Pilot, Warrior, A New Hope
		 * Persona: Chewie
		 * Game Text: Power +1 at same location as Han. Adds 2 to power of anything he pilots. When piloting Falcon,
		 * 		also adds 1 to maneuver. Your vehicles, starships, and droids at same site that are 'hit' and
		 * 		about to be lost go to Used Pile instead.
		 * Lore: Wookiee smuggler from Kashyyyk. Over 200 years old. Top-notch mechanic and pilot. Jabba has large
		 * 		bounty on this 'walking carpet.' Friends call him Chewie...or Fuzzball.
		 * Set: A New Hope
		 * Rarity: R2
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("chewie").getBlueprint();

		assertEquals("Chewbacca", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(4, card.getDeployCost(), scn.epsilon);
		assertEquals(6, card.getPower(), scn.epsilon);
		assertEquals(2, card.getAbility(), scn.epsilon);
		assertEquals(6, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.REBEL);
			add(CardType.ALIEN);
		}});
		assertEquals(Species.WOOKIEE, card.getSpecies());
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
			add(Keyword.SMUGGLER);
			//null
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
			add(Persona.CHEWIE);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.REBEL);
			add(Icon.ALIEN);
			add(Icon.PILOT);
			add(Icon.WARRIOR);
			add(Icon.A_NEW_HOPE);
		}});
		assertEquals(ExpansionSet.A_NEW_HOPE,card.getExpansionSet());
		assertEquals(Rarity.R2,card.getRarity());
	}

	@Test
	public void ChewbaccaSendsHitDroidForfeitedToUsedPile() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var c3p0 = scn.GetLSCard("c3p0");

		var eppVader = scn.GetDSCard("eppVader");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

		scn.MoveCardsToLocation(site, eppVader, chewie, c3p0);

        scn.SkipToPhase(Phase.BATTLE);
		scn.PrepareDSDestiny(6); //for hit
		scn.PrepareDSDestiny(7);
		scn.PrepareDSDestiny(5); //for destiny so dark wins

		scn.DSInitiateBattle(site);
		scn.PassAllResponses();

		scn.DSUseCardAction(eppVader);
		scn.DSChooseCard(c3p0);
		scn.PassAllResponses();
		assertTrue(c3p0.isHit());

		scn.SkipToEndOfPowerSegment(true);
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSBattleDamagePayment());
		scn.LSChooseCard(c3p0);

			//automatic send to used pile action carried out
		assertTrue(scn.DSDecisionAvailable("FORFEITED_TO_USED_PILE_FROM_TABLE")); //FORFEITED_TO_USED_PILE_FROM_TABLE - Optional responses
		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSBattleDamagePayment());
		assertEquals(Zone.TOP_OF_USED_PILE, c3p0.getZone());
	}

	@Test @Ignore
	public void ChewbaccaSendsDroidHitAndLostInBattleToUsedPile() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var c3p0 = scn.GetLSCard("c3p0");

		var eppVader = scn.GetDSCard("eppVader");
		var drE = scn.GetDSCard("drE");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, eppVader, drE, chewie, c3p0);

		scn.SkipToPhase(Phase.BATTLE);
		scn.PrepareDSDestiny(6); //succeed hitting c3p0
		scn.PrepareDSDestiny(7);

		scn.DSInitiateBattle(site);
		scn.PassAllResponses();

		scn.DSUseCardAction(eppVader);
		scn.DSChooseCard(c3p0);

		scn.LSPass(); //Fire Vader's Lightsaber - Optional responses
		scn.DSPass();


		scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		scn.LSPass(); //DESTINY_DRAWN - Optional responses
		scn.DSPass();

		scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
		scn.DSPass();


		scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		scn.LSPass(); //DESTINY_DRAWN - Optional responses
		scn.DSPass();

		scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
		scn.DSPass();


		scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
		scn.DSPass();

		scn.LSPass(); //FORFEIT_REDUCED_TO_ZERO - Optional responses
		scn.DSPass();

		scn.LSPass(); //Optional responses
		assertTrue(scn.DSCardActionAvailable(drE));
		scn.DSUseCardAction(drE);

		scn.DSChooseCard(c3p0);

		scn.LSPass(); //'Operate' on C-3P0 - Optional responses
		scn.DSPass();

		//automatic send to used pile action should be carried out here
		scn.PassAllResponses();

		scn.SkipToEndOfPowerSegment(false);
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSBattleDamagePayment());
		assertEquals(Zone.TOP_OF_USED_PILE, c3p0.getZone());
	}

	@Test @Ignore
	public void ChewbaccaSendsDroidHitAndExcludedInBattleToUsedPile() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var c3p0 = scn.GetLSCard("c3p0");

		var eppVader = scn.GetDSCard("eppVader");
		var yab = scn.GetDSCard("yab");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, eppVader, chewie, c3p0);
		scn.MoveCardsToDSHand(yab);

		scn.SkipToPhase(Phase.BATTLE);
		scn.PrepareDSDestiny(6); //succeed hitting c3p0
		scn.PrepareDSDestiny(7);

		scn.DSInitiateBattle(site);
		scn.PassAllResponses();

		scn.DSUseCardAction(eppVader);
		scn.DSChooseCard(c3p0);

		scn.PassAllResponses();
		assertTrue(c3p0.isHit());

		scn.LSPass();

		scn.DSPlayCard(yab);
		scn.DSChooseCard(c3p0);

		scn.LSPass(); //Use 2 Force - Optional responses
		scn.DSPass();

		scn.LSPass(); //Playing You Are Beaten - Optional responses
		scn.DSPass();

		assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_EXCLUDED_FROM_BATTLE"));
		scn.DSPass(); //ABOUT_TO_BE_EXCLUDED_FROM_BATTLE - Optional responses
		scn.LSPass();

		assertTrue(c3p0.isHit());
		assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE"));
		scn.DSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses

			//automatic send to used pile action should be carried out here
		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
		assertEquals(Zone.TOP_OF_USED_PILE, c3p0.getZone());
	}


	@Test
	public void ChewbaccaDoesNotSendHitDroidToUsedPileWhenLostViaAllCardsSituation() {
		//AR: "If a vehicle is lost or otherwise leaves the table, any cards aboard it are lost (All Cards situation).
		//Chewbacca cannot save C-3P0 when aboard a vehicle that is lost
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var c3p0 = scn.GetLSCard("c3p0");
		var landspeeder = scn.GetLSCard("landspeeder");

		var eppVader = scn.GetDSCard("eppVader");

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, eppVader, chewie, c3p0, landspeeder);
		scn.BoardAsPassenger(landspeeder,c3p0);

		scn.SkipToDSTurn(Phase.BATTLE);
		scn.PrepareDSDestiny(6); //for hit
		scn.PrepareDSDestiny(7);
		scn.PrepareDSDestiny(5); //for destiny so dark wins

		scn.DSInitiateBattle(site);
		scn.PassAllResponses();

		scn.DSUseCardAction(eppVader);
		scn.DSChooseCard(c3p0);
		scn.PassAllResponses();
		assertTrue(c3p0.isHit());

		scn.SkipToEndOfPowerSegment(true);
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSBattleDamagePayment());
		scn.LSChooseCard(landspeeder);

		scn.LSChooseYes(); //You are choosing to forfeit ..., which has other cards aboard that could be forfeited first. Do you still want to forfeit it?

		scn.DSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses
		scn.LSPass();

		assertTrue(scn.LSDecisionAvailable("Choose card to put on Lost Pile"));
		scn.LSChooseCard(c3p0);

		scn.DSPass(); //Optional responses
		scn.LSPass();

		assertTrue(scn.AwaitingLSBattleDamagePayment());
		assertEquals(Zone.LOST_PILE, c3p0.getZone());
	}

}
