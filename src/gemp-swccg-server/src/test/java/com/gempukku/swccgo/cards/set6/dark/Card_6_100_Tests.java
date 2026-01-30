package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_6_100_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("cantina","1_128"); //tatooine: cantina
					put("jp_ac","6_081"); //jabba's palace: audience chamber
					put("arcona1","2_001");
					put("arcona2","2_001");
					put("wolfman1","1_030");
					put("wolfman2","1_030");
				}},
				new HashMap<>()
				{{
					put("cz4", "6_100");
					put("cz4_2", "6_100");
					put("jp","6_171"); //tatooine: jabba's palace
					put("jp_cavern","6_165"); //jabba's palace: entrance cavern
					put("abyssin","6_091");
					put("lobel","7_186");
					put("gamorrean","6_105");
					put("greedo","2_089");
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
	public void CZ_4StatsAndKeywordsAreCorrect() {
		/**
		 * Title: CZ-4
		 * Uniqueness: Unique
		 * Side: Dark
		 * Type: Character
		 * Subtype: Droid
		 * Destiny: 4
		 * Deploy: 2
		 * Power: 1
		 * Forfeit: 3
		 * Icons: Droid, Jabba's Palace
		 * Game Text: Opponent may not 'react' to or from same site. You may 'react' to a battle or Force drain
		 * 		at same or adjacent Jabba's Palace site by deploying (at normal use of the Force)
		 * 		one non-unique alien to that site from Reserve Deck; reshuffle.
		 * Lore: Very common communications droid. Some have been modified to be defense drones.
		 * 		Programmed to warn their masters of an imminent attack.
		 * Set: Jabba's Palace
		 * Rarity: C
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("cz4").getBlueprint();

		assertEquals("CZ-4", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertEquals(4, card.getDestiny(), scn.epsilon);
		assertEquals(2, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertEquals(0, card.getAbility(), scn.epsilon);
		assertEquals(3, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.DROID);
		}});
		scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
			add(ModelType.COMMUNICATIONS);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.DROID);
			add(Icon.JABBAS_PALACE);
		}});
		assertEquals(ExpansionSet.JABBAS_PALACE,card.getExpansionSet());
		assertEquals(Rarity.C,card.getRarity());
	}

	@Test
	public void CZ_4PreventsOpponentReactingToSameSite() {
		//test1: react to a force drain under normal circumstances works
		//test2: react to a force drain at CZ4's site is prevented
		var scn = GetScenario();

		var wolfman1 = scn.GetLSCard("wolfman1");
		var wolfman2 = scn.GetLSCard("wolfman2");
		var cantina = scn.GetLSCard("cantina");
		var jp_ac = scn.GetLSCard("jp_ac");

		var cz4 = scn.GetDSCard("cz4");
        var stormtrooper1 = scn.GetDSFiller(1);
        var stormtrooper2 = scn.GetDSFiller(2);
		var jp_cavern = scn.GetDSCard("jp_cavern");
		var jp = scn.GetDSCard("jp");

        scn.StartGame();

		scn.MoveLocationToTable(cantina);
		scn.MoveLocationToTable(jp);
		scn.MoveLocationToTable(jp_cavern);
		scn.MoveLocationToTable(jp_ac);

		scn.MoveCardsToLocation(cantina,cz4,stormtrooper1);
		scn.MoveCardsToLocation(jp,wolfman1);
		scn.MoveCardsToLocation(jp_cavern,wolfman2);
		scn.MoveCardsToLocation(jp_ac,stormtrooper2);

		scn.LSActivateForceCheat(2); //enough to react twice

		scn.SkipToPhase(Phase.CONTROL);

		assertTrue(scn.DSForceDrainAvailable(cantina));
		assertTrue(scn.DSForceDrainAvailable(jp_ac));

		scn.DSForceDrainAt(jp_ac);
		assertTrue(scn.LSDecisionAvailable("Force drain initiated"));
		assertTrue(scn.LSCardActionAvailable(wolfman2,"react")); //test1
		scn.LSUseCardAction(wolfman2,"react");

		assertTrue(scn.LSDecisionAvailable("Choose where to move"));
		assertTrue(scn.LSHasCardChoiceAvailable(jp_ac));
		assertFalse(scn.LSHasCardChoicesAvailable(jp_cavern,jp,cantina));
		scn.LSChooseCard(jp_ac);
		scn.PassAllResponses();

		assertTrue(scn.CardsAtLocation(jp_ac,stormtrooper2,wolfman2));

		scn.LSPass();

		assertTrue(scn.DSForceDrainAvailable(cantina));
		scn.DSForceDrainAt(cantina);
		assertTrue(scn.LSDecisionAvailable("FORCE_DRAIN_INITIATED")); //passed option to react
		assertFalse(scn.LSCardActionAvailable(wolfman1,"react")); //test2
	}

	@Test
	public void CZ_4PreventsOpponentReactingFromSameSite() {
		//test1: react away from battle under normal circumstances works
		//test2: react away from battle at CZ4's site is prevented
		var scn = GetScenario();

		var arcona1 = scn.GetLSCard("arcona1");
		var arcona2 = scn.GetLSCard("arcona2");
		var cantina = scn.GetLSCard("cantina");
		var jp_ac = scn.GetLSCard("jp_ac");

		var cz4 = scn.GetDSCard("cz4");
		var stormtrooper1 = scn.GetDSFiller(1);
		var stormtrooper2 = scn.GetDSFiller(2);
		var jp_cavern = scn.GetDSCard("jp_cavern");
		var jp = scn.GetDSCard("jp");

		scn.StartGame();

		scn.MoveLocationToTable(cantina);
		scn.MoveLocationToTable(jp);
		scn.MoveLocationToTable(jp_cavern);
		scn.MoveLocationToTable(jp_ac);

		scn.MoveCardsToLocation(cantina,cz4,stormtrooper1,arcona1);
		scn.MoveCardsToLocation(jp_ac,stormtrooper2,arcona2);

		scn.LSActivateForceCheat(2); //enough to react twice

		scn.SkipToPhase(Phase.BATTLE);

		assertTrue(scn.DSCanInitiateBattle(cantina));
		assertTrue(scn.DSCanInitiateBattle(jp_ac));

		scn.DSInitiateBattle(jp_ac);
		assertTrue(scn.LSDecisionAvailable("Battle just initiated"));
		assertTrue(scn.LSCardActionAvailable(arcona2,"react")); //test1
		scn.LSUseCardAction(arcona2,"react");

		assertTrue(scn.LSDecisionAvailable("Choose where to move"));
		assertTrue(scn.LSHasCardChoiceAvailable(jp_cavern));
		assertFalse(scn.LSHasCardChoicesAvailable(jp_ac,jp,cantina));
		scn.LSChooseCard(jp_cavern);
		scn.PassAllResponses();

		assertTrue(scn.CardsAtLocation(jp_ac,stormtrooper2));
		assertTrue(scn.CardsAtLocation(jp_cavern,arcona2));

		scn.LSPass();

		assertTrue(scn.DSCanInitiateBattle(cantina));
		scn.DSInitiateBattle(cantina);
		assertTrue(scn.AwaitingDSWeaponsSegmentActions()); //test2: (passed any timing to react)
	}

	@Test
	public void CZ_4MayDeployAsReactToDrainAtSameJPSite() {
		//test1: may not react to force drain at CZ-4's non-jabba's palace site
		//test2: may react (by deploying a non-unique alien to the site) to force drain at CZ-4's jabba's palace site
		//test3: able to deploy a non-unique alien as a react
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var cantina = scn.GetLSCard("cantina");
		var jp_ac = scn.GetLSCard("jp_ac");

		var cz4 = scn.GetDSCard("cz4");
		var cz4_2 = scn.GetDSCard("cz4_2");
		var abyssin = scn.GetDSCard("abyssin");
		var lobel = scn.GetDSCard("lobel");//
		var jp_cavern = scn.GetDSCard("jp_cavern");
		var jp = scn.GetDSCard("jp");

		scn.StartGame();

		scn.MoveLocationToTable(cantina);
		scn.MoveLocationToTable(jp);
		scn.MoveLocationToTable(jp_cavern);
		scn.MoveLocationToTable(jp_ac);

		scn.MoveCardsToLocation(cantina,cz4,rebelTrooper1);
		scn.MoveCardsToLocation(jp_ac,cz4_2,rebelTrooper2);

		scn.MoveCardsToDSHand(abyssin,lobel);

		scn.DSActivateForceCheat(3);

		scn.SkipToLSTurn(Phase.CONTROL);

		scn.MoveCardsToTopOfDSReserveDeck(abyssin,lobel); //get some valid aliens put back in reserve deck

		assertEquals(6,scn.GetDSForcePileCount());

		assertTrue(scn.LSForceDrainAvailable(cantina));
		assertTrue(scn.LSForceDrainAvailable(jp_ac));

		scn.LSForceDrainAt(cantina);
		assertTrue(scn.DSDecisionAvailable("FORCE_DRAIN_INITIATED")); //test1: passed option to react
		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSForceLossPayment());
		scn.DSChooseCard(scn.GetTopOfDSForcePile());
		scn.PassAllResponses();
		scn.DSChooseCard(scn.GetTopOfDSForcePile());
		scn.PassAllResponses();

		scn.DSPass();
		assertEquals(4,scn.GetDSForcePileCount());

		assertTrue(scn.LSForceDrainAvailable(jp_ac));
		scn.LSForceDrainAt(jp_ac);
		assertTrue(scn.DSDecisionAvailable("Force drain initiated"));
		assertTrue(scn.DSCardActionAvailable(cz4_2,"Deploy")); //test2
		scn.DSUseCardAction(cz4_2,"Deploy");

		assertTrue(scn.DSHasCardChoicesAvailable(abyssin,lobel));
		scn.DSChooseCard(abyssin);

		scn.LSPass(); //LOOKED_AT_CARDS_IN_CARD_PILE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Use 2 Force - Optional responses
		scn.DSPass();

		scn.LSPass(); //SHUFFLE_CARD_PILE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Deploying Abyssin - Optional responses
		scn.DSPass();

		scn.LSPass(); //PLAY - Optional responses
		scn.DSPass();

		assertTrue(scn.CardsAtLocation(jp_ac,cz4_2,abyssin)); //test3: successfully reacted to CZ-4 location
	}

	@Test
	public void CZ_4MayDeployAsReactToDrainAtAdjacentJPSite() {
		//test1: may not react to force drain at jabba's palace site not adjacent to CZ-4
		//test2: may react (by deploying a non-unique alien to the site) to force drain at jabba's palace site adjacent to CZ-4
		//test3: able to deploy a non-unique alien as a react
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var jp_ac = scn.GetLSCard("jp_ac");

		var cz4 = scn.GetDSCard("cz4");
		var abyssin = scn.GetDSCard("abyssin");
		var lobel = scn.GetDSCard("lobel");
		var jp_cavern = scn.GetDSCard("jp_cavern");
		var jp = scn.GetDSCard("jp");

		scn.StartGame();

		scn.MoveLocationToTable(jp);
		scn.MoveLocationToTable(jp_cavern);
		scn.MoveLocationToTable(jp_ac);

		scn.MoveCardsToLocation(jp,rebelTrooper1);
		scn.MoveCardsToLocation(jp_cavern,rebelTrooper2);
		scn.MoveCardsToLocation(jp_ac,cz4);

		scn.MoveCardsToDSHand(abyssin,lobel);

		scn.DSActivateForceCheat(3);

		scn.SkipToLSTurn(Phase.CONTROL);

		scn.MoveCardsToTopOfDSReserveDeck(abyssin,lobel); //get some valid aliens put back in reserve deck

		assertEquals(6,scn.GetDSForcePileCount());

		assertTrue(scn.LSForceDrainAvailable(jp));
		assertTrue(scn.LSForceDrainAvailable(jp_cavern));

		scn.LSForceDrainAt(jp);
		assertTrue(scn.DSDecisionAvailable("FORCE_DRAIN_INITIATED")); //test1: passed option to react
		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSForceLossPayment());
		scn.DSChooseCard(scn.GetTopOfDSForcePile());
		scn.PassAllResponses();
		scn.DSChooseCard(scn.GetTopOfDSForcePile());
		scn.PassAllResponses();

		scn.DSPass();
		assertEquals(4,scn.GetDSForcePileCount());

		assertTrue(scn.LSForceDrainAvailable(jp_cavern));
		scn.LSForceDrainAt(jp_cavern);
		assertTrue(scn.DSDecisionAvailable("Force drain initiated"));
		assertTrue(scn.DSCardActionAvailable(cz4,"Deploy")); //test2
		scn.DSUseCardAction(cz4,"Deploy");

		assertTrue(scn.DSHasCardChoicesAvailable(abyssin,lobel));
		scn.DSChooseCard(abyssin);

		scn.LSPass(); //LOOKED_AT_CARDS_IN_CARD_PILE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Use 2 Force - Optional responses
		scn.DSPass();

		scn.LSPass(); //SHUFFLE_CARD_PILE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Deploying Abyssin - Optional responses
		scn.DSPass();

		scn.LSPass(); //PLAY - Optional responses
		scn.DSPass();

		assertTrue(scn.CardsAtLocation(jp_ac,cz4));
		assertTrue(scn.CardsAtLocation(jp_cavern,abyssin)); //test3: successfully reacted to CZ-4 location
	}

	@Test
	public void CZ_4MayDeployAsReactToBattleAtSameJPSite() {
		//test1: may not react to battle at CZ-4's non-jabba's palace site
		//test2: may react (by deploying a non-unique alien to the site) to battle at CZ-4's jabba's palace site
		//test3: able to deploy a non-unique alien as a react
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var cantina = scn.GetLSCard("cantina");
		var jp_ac = scn.GetLSCard("jp_ac");

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var cz4 = scn.GetDSCard("cz4");
		var cz4_2 = scn.GetDSCard("cz4_2");
		var abyssin = scn.GetDSCard("abyssin");
		var lobel = scn.GetDSCard("lobel");//
		var jp_cavern = scn.GetDSCard("jp_cavern");
		var jp = scn.GetDSCard("jp");

		scn.StartGame();

		scn.MoveLocationToTable(cantina);
		scn.MoveLocationToTable(jp);
		scn.MoveLocationToTable(jp_cavern);
		scn.MoveLocationToTable(jp_ac);

		scn.MoveCardsToLocation(cantina,cz4,trooper1,rebelTrooper1);
		scn.MoveCardsToLocation(jp_ac,cz4_2,trooper2,rebelTrooper2);

		scn.MoveCardsToDSHand(abyssin,lobel);

		scn.SkipToLSTurn(Phase.BATTLE);

		scn.MoveCardsToTopOfDSReserveDeck(abyssin,lobel); //get some valid aliens put back in reserve deck

		assertEquals(3,scn.GetDSForcePileCount());

		assertTrue(scn.LSCanInitiateBattle(cantina));
		assertTrue(scn.LSCanInitiateBattle(jp_ac));

		scn.LSInitiateBattle(cantina);
		assertTrue(scn.AwaitingLSWeaponsSegmentActions()); //test1: passed option to react
		scn.SkipToDamageSegment();
		assertTrue(scn.AwaitingLSBattleDamagePayment());
		scn.LSPayBattleDamageFromReserveDeck();
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSBattlePhaseActions());
		scn.DSPass();
		assertEquals(3,scn.GetDSForcePileCount());

		assertTrue(scn.LSCanInitiateBattle(jp_ac));
		scn.LSInitiateBattle(jp_ac);
		assertTrue(scn.DSDecisionAvailable("Battle just initiated"));
		assertTrue(scn.DSCardActionAvailable(cz4_2,"Deploy")); //test2
		scn.DSUseCardAction(cz4_2,"Deploy");

		assertTrue(scn.DSHasCardChoicesAvailable(abyssin,lobel));
		scn.DSChooseCard(abyssin);

		scn.LSPass(); //LOOKED_AT_CARDS_IN_CARD_PILE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Use 2 Force - Optional responses
		scn.DSPass();

		scn.LSPass(); //SHUFFLE_CARD_PILE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Deploying Abyssin - Optional responses
		scn.DSPass();

		scn.LSPass(); //PLAY - Optional responses
		scn.DSPass();

		assertTrue(scn.CardsAtLocation(jp_ac,cz4_2,abyssin)); //test3: successfully reacted to CZ-4 location
	}

	@Test
	public void CZ_4MayDeployAsReactToBattleAtAdjacentJPSite() {
		//test1: may not react to battle at jabba's palace site not adjacent to CZ-4
		//test2: may react (by deploying a non-unique alien to the site) to battle at jabba's palace site adjacent to CZ-4
		//test3: able to deploy a non-unique alien as a react
		//test4: paid normal force cost to deploy the non-unique alien as a react
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var jp_ac = scn.GetLSCard("jp_ac");

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var cz4 = scn.GetDSCard("cz4");
		var abyssin = scn.GetDSCard("abyssin");
		var lobel = scn.GetDSCard("lobel");
		var jp_cavern = scn.GetDSCard("jp_cavern");
		var jp = scn.GetDSCard("jp");

		scn.StartGame();

		scn.MoveLocationToTable(jp);
		scn.MoveLocationToTable(jp_cavern);
		scn.MoveLocationToTable(jp_ac);

		scn.MoveCardsToLocation(jp,trooper1,rebelTrooper1);
		scn.MoveCardsToLocation(jp_cavern,trooper2,rebelTrooper2);
		scn.MoveCardsToLocation(jp_ac,cz4);

		scn.MoveCardsToDSHand(abyssin,lobel);

		scn.SkipToLSTurn(Phase.BATTLE);

		scn.MoveCardsToTopOfDSReserveDeck(abyssin,lobel); //get some valid aliens put back in reserve deck

		assertEquals(3,scn.GetDSForcePileCount());

		assertTrue(scn.LSCanInitiateBattle(jp));
		assertTrue(scn.LSCanInitiateBattle(jp_cavern));

		scn.LSInitiateBattle(jp);
		assertTrue(scn.AwaitingLSWeaponsSegmentActions()); //test1: passed option to react
		scn.SkipToDamageSegment();
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSBattlePhaseActions());
		scn.DSPass();
		assertEquals(3,scn.GetDSForcePileCount());

		assertTrue(scn.LSCanInitiateBattle(jp_cavern));
		scn.LSInitiateBattle(jp_cavern);
		assertTrue(scn.DSDecisionAvailable("Battle just initiated"));
		assertTrue(scn.DSCardActionAvailable(cz4,"Deploy")); //test2
		scn.DSUseCardAction(cz4,"Deploy");

		assertTrue(scn.DSHasCardChoicesAvailable(abyssin,lobel));
		scn.DSChooseCard(abyssin);

		scn.LSPass(); //LOOKED_AT_CARDS_IN_CARD_PILE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Use 2 Force - Optional responses
		scn.DSPass();

		scn.LSPass(); //SHUFFLE_CARD_PILE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Deploying Abyssin - Optional responses
		scn.DSPass();

		scn.LSPass(); //PLAY - Optional responses
		scn.DSPass();

		assertTrue(scn.CardsAtLocation(jp_ac,cz4));
		assertTrue(scn.CardsAtLocation(jp_cavern,abyssin)); //test3: successfully reacted to CZ-4 location
		assertEquals(1,scn.GetDSForcePileCount()); //test4: paid normal deploy cost of 2
	}

	/// manually confirmed - need replace test2-4 checks for DSHasCardChoiceAvailable with some other function?
	/// Currently, when deploying from reserve deck, all cards in reserve deck return true (shown in the
	/// card selection box?) even though only some of them can actually be selected...
	@Test @Ignore
	public void CZ_4MayOnlyDeployNonUniqueAlienFromReserve() {
		//test limitations for card to deploy from reserve deck
		//test1: may deploy one non-unique alien with deploy cost <= available force
		//test2: may not deploy one non-unique imperial with deploy cost <= available force
		//test3: may not deploy one unique alien with deploy cost <= available force
		//test4: may not deploy one non-unique alien with deploy cost > than available force
		//test5: paid normal force cost to deploy
		//test6: "One rule" prevents CZ-4 from attempting to use react action again
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var jp_ac = scn.GetLSCard("jp_ac");

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var cz4 = scn.GetDSCard("cz4");
		var abyssin = scn.GetDSCard("abyssin");
		var lobel = scn.GetDSCard("lobel");
		var greedo = scn.GetDSCard("greedo");
		var gamorrean = scn.GetDSCard("gamorrean");


		scn.StartGame();

		scn.MoveLocationToTable(jp_ac);

		scn.MoveCardsToLocation(jp_ac,cz4,trooper1,rebelTrooper1);

		scn.MoveCardsToDSHand(abyssin,lobel,trooper2,greedo,gamorrean);

		scn.SkipToLSTurn(Phase.BATTLE);

		scn.MoveCardsToTopOfDSReserveDeck(abyssin,lobel,trooper2,greedo,gamorrean); //non-unique aliens, non-unique imperial, unique alien

		assertEquals(3,scn.GetDSForcePileCount());

		assertTrue(scn.LSCanInitiateBattle(jp_ac));

		assertTrue(scn.LSCanInitiateBattle(jp_ac));
		scn.LSInitiateBattle(jp_ac);
		assertTrue(scn.DSDecisionAvailable("Battle just initiated"));
		assertTrue(scn.DSCardActionAvailable(cz4,"Deploy")); //test2
		scn.DSUseCardAction(cz4,"Deploy");

		///FAILS HERE - reporting 13 (all cards in reserve) instead of 2 (valid choices)
		assertEquals(2,scn.DSGetCardChoiceCount());
		assertTrue(scn.DSHasCardChoicesAvailable(abyssin,lobel)); //test1: non-unique aliens
		assertFalse(scn.DSHasCardChoiceAvailable(trooper2)); //test2: not alien
		assertFalse(scn.DSHasCardChoiceAvailable(greedo)); //test3: not non-unique
		assertFalse(scn.DSHasCardChoiceAvailable(gamorrean)); //test4: not enough force to deploy
		scn.DSChooseCard(abyssin);

		scn.LSPass(); //LOOKED_AT_CARDS_IN_CARD_PILE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Use 2 Force - Optional responses
		scn.DSPass();

		scn.LSPass(); //SHUFFLE_CARD_PILE - Optional responses
		scn.DSPass();

		scn.LSPass(); //Deploying Abyssin - Optional responses
		scn.DSPass();

		scn.LSPass(); //PLAY - Optional responses
		scn.DSPass();

		assertEquals(1,scn.GetDSForcePileCount()); //test5: paid normal deploy cost of 2

		scn.LSPass(); //BATTLE_INITIATED - Optional responses
		assertTrue(scn.DSDecisionAvailable("BATTLE_INITIATED")); //test6: passed opportunity to react again
	}

}
