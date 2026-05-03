package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_015_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
					put("kfc","1_015"); //Kal'Falnl C'ndros
					put("falcon","1_143");
					put("nebulon","9_080"); //Nebulon-B Frigate
					put("haashn","9_025"); //Major Haash'n
				}},
				new HashMap<>() {{
				}},
				20,
				20,
				StartingSetup.DefaultLSSpaceSystem,
				StartingSetup.DefaultDSGroundLocation,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void KalFalnlCndrosStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Kal'Falnl C'ndros
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Character
		 * Subtype: Alien
		 * Destiny: 2
		 * Deploy: 0
		 * Power: 1
		 * Ability: 1
		 * Forfeit: 5
		 * Persona: (none)
		 * Species: Quor'sav
		 * Icons: Pilot
		 * Game Text: When in a battle, if both players draw only one battle destiny and yours is higher,
		 * 		reduces opponent's total battle destiny to zero. Landspeed = 3. Adds 2 to power of anything she pilots.
		 * 		May not be aboard starfighters or enclosed vehicles.
		 * Lore: A female Quor'sav, a warm-blooded, avian/monotreme species. 3.5 meters tall. Over-protective mother.
		 * 		Freelance pilot. Has custom-built ship with tall corridors. Lays eggs.
		 * Set: Premiere
		 * Rarity: R1
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("kfc").getBlueprint();

		assertEquals("Kal'Falnl C'ndros", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.ALIEN);
		}});
		assertEquals(2, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertEquals(1, card.getAbility(), scn.epsilon);
		assertEquals(0, card.getDeployCost(), scn.epsilon);
		assertEquals(5, card.getForfeit(), scn.epsilon);
		scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.ALIEN);
			add(Icon.PILOT);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
			add(Keyword.FEMALE);
		}});
		assertEquals(Species.QUORSAV, card.getSpecies());
		assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
		assertEquals(Rarity.R1, card.getRarity());
	}

	@Test
	public void KalFalnlCndrosCanDeployAboardCapitalShip() {
		//test1: kfc cannot be aboard starfighter, but can be aboard non-starfighter starships
		var scn = GetScenario();

		var kfc = scn.GetLSCard("kfc");
		var nebulon = scn.GetLSCard("nebulon");

		var system = scn.GetLSStartingLocation();
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(kfc);

		scn.MoveCardsToLocation(system,nebulon);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(kfc));
		scn.LSDeployCard(kfc);
		assertTrue(scn.LSHasCardChoiceAvailable(site));
		assertTrue(scn.LSHasCardChoiceAvailable(nebulon)); //test1
		scn.LSChooseCard(nebulon);
		scn.LSChoose("Pilot");
		scn.PassAllResponses();

		assertTrue(scn.IsAboardAsPilot(nebulon, kfc));
	}

	@Test
	public void KalFalnlCndrosCannotDeployAboardStarfighter() {
		//test1: kfc cannot be aboard starfighter, so kfc cannot deploy to a starfighter
		var scn = GetScenario();

		var kfc = scn.GetLSCard("kfc");
		var falcon = scn.GetLSCard("falcon");

		var system = scn.GetLSStartingLocation();
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(kfc);

		scn.MoveCardsToLocation(system,falcon);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(kfc));
		scn.LSDeployCard(kfc);
		assertTrue(scn.LSHasCardChoiceAvailable(site));
		assertFalse(scn.LSHasCardChoiceAvailable(falcon)); //test1
	}

	@Test
	public void KalFalnlCndrosCannotEmbarkOnStarfighter() {
		//test1: kfc cannot be aboard starfighter, so kfc cannot embark on a starfighter
		var scn = GetScenario();

		var kfc = scn.GetLSCard("kfc");
		var trooper = scn.GetLSFiller(1);
		var falcon = scn.GetLSCard("falcon");

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site,falcon,kfc,trooper);

		scn.SkipToLSTurn(Phase.MOVE);
		assertTrue(scn.LSCardActionAvailable(trooper, "Embark"));
		assertFalse(scn.LSCardActionAvailable(kfc, "Embark")); //test1
	}

	@Test
	public void KalFalnlCndrosCannotTransferToStarfighterUsingShipDock() {
		//unsuccessful attempt to replicate https://github.com/PlayersCommittee/gemp-swccg-public/issues/221
		//test1: kfc cannot be aboard starfighter, so kfc cannot transfer (via ship-dock) to a starfighter
		var scn = GetScenario();

		var kfc = scn.GetLSCard("kfc");
		var haashn = scn.GetLSCard("haashn");
		var falcon = scn.GetLSCard("falcon");
		var nebulon = scn.GetLSCard("nebulon");

		var system = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(system,falcon,nebulon);
		scn.MoveCardsToLSHand(kfc, haashn);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(haashn);
		scn.LSChooseCard(nebulon);
		scn.LSChoose("Pilot");
		scn.PassAllResponses();
		assertTrue(scn.IsAboardAsPilot(haashn));

		scn.DSPass();

		scn.LSDeployCard(kfc);
		scn.LSChooseCard(nebulon);
		scn.LSChoose("Pilot");
		scn.PassAllResponses();
		assertTrue(scn.IsAboardAsPilot(kfc));

		scn.SkipToPhase(Phase.MOVE);
		scn.LSUseCardAction(nebulon, "dock");
		scn.LSChooseCard(falcon);
		scn.PassAllResponses();

		assertTrue(scn.LSCardActionAvailable(haashn, "Transfer"));
		assertFalse(scn.LSCardActionAvailable(kfc, "Transfer")); //test1
	}

}


