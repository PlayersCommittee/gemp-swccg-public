package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_8_051_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("flyCasual","8_051");
					put("pilot", "1_027"); //rebel pilot
					put("pilot2", "1_027"); //rebel pilot
                    put("falcon", "1_143");
					put("xwing", "1_146");
                    put("ywing", "1_147");
					put("homeOne", "9_074");
					put("hoth_db","3_059"); //hoth: echo docking bay
				}},
				new HashMap<>()
				{{
					put("deathstar","2_143"); //Careful Planning
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
	public void FlyCasualStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Fly Casual
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Interrupt
		 * Subtype: Used or Lost
		 * Destiny: 6
		 * Icons: Endor
		 * Game Text: USED: Cancel Early Warning Network or It's An Older Code. OR During your deploy phase,
		 * 		deploy one starship (deploy -1) and/or one pilot to a system even without presence or Force icons.
		 * 		LOST: Take one Tydirium or Tantive IV into hand from Reserve Deck; reshuffle.
		 * Lore: As a smuggler, Han had years of experience at avoiding Imperial detection. He chose the approach
		 * 		to Endor's moon as the time to pass some of that knowledge on to Chewie.
		 * Set: Endor
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("flyCasual").getBlueprint();

		assertEquals("Fly Casual", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.ENDOR);
			add(Icon.INTERRUPT);
		}});
		assertEquals(ExpansionSet.ENDOR,card.getExpansionSet());
		assertEquals(Rarity.R,card.getRarity());
	}

	@Test
	public void FlyCasualUsedDeploysDuringYourDeployPhase() {
		//test1: playable during your deploy phase
		//test2: not playable in your non-deploy phase
		//test3: not playable during opponent's deploy phase
		var scn = GetScenario();

		var flyCasual = scn.GetLSCard("flyCasual");
		var ywing = scn.GetLSCard("ywing");

		scn.StartGame();

		scn.MoveCardsToLSHand(flyCasual, ywing);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSCardPlayAvailable(flyCasual,"starship or pilot"));

		scn.SkipToPhase(Phase.BATTLE);
		assertFalse(scn.LSCardPlayAvailable(flyCasual,"starship or pilot"));

		scn.SkipToDSTurn(Phase.DEPLOY);
		scn.DSPass();
		assertTrue(scn.AwaitingLSDeployPhaseActions());
		assertFalse(scn.LSCardPlayAvailable(flyCasual,"starship or pilot"));
	}

	@Test
	public void FlyCasualUsedDeploysOnePilotedStarshipToSystem() {
		//test1: cannot choose unpiloted starship (if no pilot available for simultaneous deploy)
		//test2: can choose piloted starship
		//test3: can choose system with icons
		//test4: can choose system without icons/presence
		//test5: cannot choose non-system (docking bay site)
		//test6: deploys for -1
		var scn = GetScenario();

		var flyCasual = scn.GetLSCard("flyCasual");
		var ywing = scn.GetLSCard("ywing");
		var xwing = scn.GetLSCard("xwing");
		var falcon = scn.GetLSCard("falcon");
		var system = scn.GetLSStartingLocation();
		var hoth_db = scn.GetLSCard("hoth_db");

		var deathstar = scn.GetDSCard("deathstar");

		scn.StartGame();

		scn.MoveCardsToLSHand(flyCasual, ywing, xwing, falcon);
		scn.MoveLocationToTable(deathstar);
		scn.MoveLocationToTable(hoth_db);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSPlayCard(flyCasual,"starship or pilot");
		scn.PassAllResponses();

		assertFalse(scn.LSHasCardChoiceAvailable(falcon)); //test1
		assertTrue(scn.LSHasCardChoiceAvailable(ywing)); //test2
		assertTrue(scn.LSHasCardChoiceAvailable(xwing)); //test2
		scn.LSChooseCard(xwing);

		assertTrue(scn.LSHasCardChoiceAvailable(system)); //test3
		assertTrue(scn.LSHasCardChoiceAvailable(deathstar)); //test4
		assertFalse(scn.LSHasCardChoiceAvailable(hoth_db)); //test5
		scn.LSChooseCard(deathstar);

		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertEquals(Zone.TOP_OF_USED_PILE,flyCasual.getZone());
		assertEquals(2, scn.GetLSUsedPileCount()); //test6 (xwing deploy for 1, fly casual to used pile)
		assertTrue(scn.CardsAtLocation(deathstar, xwing));
	}

	@Test
	public void FlyCasualUsedDeploysOnePilotedStarshipToShipAtSystem() {
		//test1: can choose unpiloted starship (with no pilot available for simultaneous deploy)
		//test2: can choose piloted starship
		//test3: deploys for -1
		//test4: can choose to deploy to a starship at a system with sufficient capacity
		var scn = GetScenario();

		var flyCasual = scn.GetLSCard("flyCasual");
		var ywing = scn.GetLSCard("ywing");
		var falcon = scn.GetLSCard("falcon");
		var homeOne = scn.GetLSCard("homeOne");

		var deathstar = scn.GetDSCard("deathstar");

		scn.StartGame();

		scn.MoveCardsToLSHand(flyCasual, ywing, falcon);
		scn.MoveLocationToTable(deathstar);
		scn.MoveCardsToLocation(deathstar, homeOne);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSPlayCard(flyCasual,"starship or pilot");
		scn.PassAllResponses();

		assertTrue(scn.LSHasCardChoiceAvailable(falcon)); //test1
		assertTrue(scn.LSHasCardChoiceAvailable(ywing)); //test2
		scn.LSChooseCard(falcon);

		//home one automatically selected as only option
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertEquals(Zone.TOP_OF_USED_PILE,flyCasual.getZone());
		assertEquals(3, scn.GetLSUsedPileCount()); //test3 (falcon deploy for 2, fly casual to used pile)
		assertTrue(scn.CardsAtLocation(deathstar, homeOne));
		assertTrue(scn.IsAboard(homeOne, falcon)); //test4
	}

	@Test
	public void FlyCasualUsedDeploysOnePilotToSystem() {
		//shows fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/987
		//test1: cannot choose non-pilot character
		//test2: can choose pilot character
		//test3: can deploy to a piloted starship at a system with capacity
		//test4: can deploy to an unpiloted starship at a system without presence/icons
		//test5: cannot deploy to a starship at a non-system (docking bay) with capacity
		//test6: cannot deploy to a non-system (site)
		//test7: cannot deploy to a starship at a system that does not have capacity
		//test8: can deploy as pilot or passenger
		//test9: deploys for normal deploy cost
		var scn = GetScenario();

		var flyCasual = scn.GetLSCard("flyCasual");
		var pilot = scn.GetLSCard("pilot");
		var pilot2 = scn.GetLSCard("pilot2");
		var ywing = scn.GetLSCard("ywing");
		var xwing = scn.GetLSCard("xwing");
		var homeOne = scn.GetLSCard("homeOne");
		var falcon = scn.GetLSCard("falcon");
		var system = scn.GetLSStartingLocation();
		var hoth_db = scn.GetLSCard("hoth_db");
		var trooper = scn.GetLSFiller(1);

		var deathstar = scn.GetDSCard("deathstar");
		var system2 = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(flyCasual, pilot, pilot2, trooper);
		scn.MoveLocationToTable(deathstar);
		scn.MoveLocationToTable(hoth_db);

		scn.MoveCardsToLocation(deathstar, falcon);
		scn.MoveCardsToLocation(system, homeOne);
		scn.MoveCardsToLocation(system2, xwing);
		scn.MoveCardsToLocation(hoth_db, ywing);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSPlayCard(flyCasual,"starship or pilot");
		scn.PassAllResponses();

		assertFalse(scn.LSHasCardChoiceAvailable(trooper)); //test1
		assertTrue(scn.LSHasCardChoiceAvailable(pilot)); //test2
		assertTrue(scn.LSHasCardChoiceAvailable(pilot2)); //test2
		scn.LSChooseCard(pilot);

		assertTrue(scn.LSHasCardChoiceAvailable(homeOne)); //test3
		assertTrue(scn.LSHasCardChoiceAvailable(falcon)); //test4
		assertFalse(scn.LSHasCardChoiceAvailable(ywing)); //test5
		assertFalse(scn.LSHasCardChoiceAvailable(hoth_db)); //test6
		assertFalse(scn.LSHasCardChoiceAvailable(xwing)); //test7
		scn.LSChooseCard(falcon);

		assertTrue(scn.LSChoiceAvailable("Pilot"));
		assertTrue(scn.LSChoiceAvailable("Passenger"));
		scn.LSChoose("Passenger");

		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertEquals(Zone.TOP_OF_USED_PILE,flyCasual.getZone());
		assertEquals(3, scn.GetLSUsedPileCount()); //test8 (pilot deploy for 2, fly casual to used pile)
		assertTrue(scn.CardsAtLocation(deathstar, falcon));
		assertTrue(scn.IsAboardAsPassenger(falcon, pilot));
	}

	@Test
	public void FlyCasualUsedDeploysOneShipWithSimultaneousDeployToSystem() {
		//test1: can choose an unpiloted ship
		//test2: can deploy to a system without presence/icons
		//test3: simultaneous deploy of a pilot required
		//test4: ship deploys for -1, pilot deploys at normal cost
		var scn = GetScenario();

		var flyCasual = scn.GetLSCard("flyCasual");
		var pilot = scn.GetLSCard("pilot");
		var falcon = scn.GetLSCard("falcon");
		var trooper = scn.GetLSFiller(1);

		var deathstar = scn.GetDSCard("deathstar");

		scn.StartGame();

		scn.MoveCardsToLSHand(flyCasual, falcon, pilot, trooper);
		scn.MoveLocationToTable(deathstar);

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.LSActivateForceCheat(1);
		assertEquals(4, scn.GetLSForcePileCount()); //4 = enough for falcon (3-1) and pilot (2)

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSPlayCard(flyCasual,"starship or pilot");
		scn.PassAllResponses();

		//falcon automatically selected, as only option
		assertTrue(scn.LSDecisionAvailable("Choose where to deploy")); //test1
		assertTrue(scn.LSHasCardChoiceAvailable(deathstar)); //test2
		scn.LSChooseCard(deathstar);

		//pilot automatically selected for simultaneous deployment, as only option

		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertEquals(Zone.TOP_OF_USED_PILE,flyCasual.getZone());
		assertEquals(5, scn.GetLSUsedPileCount()); //test4
		assertTrue(scn.CardsAtLocation(deathstar, falcon));
		assertTrue(scn.IsAboardAsPilot(falcon, pilot));
	}
}
