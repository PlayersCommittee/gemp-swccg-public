package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
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

public class Card_1_069_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
					put("yerka","1_069"); //Yerka Mig
					put("ywing","1_147");
					put("ywing2","1_147");
					put("xwing","1_146");
					put("hut","4_089"); //Dagobah: Yoda's Hut
                    put("hoth","3_055");
                    put("skiff","6_088");
				}},
				new HashMap<>() {{
					put("landing","11_092"); //Tatooine: Desert Landing Site
					put("vcsd", "2_155"); //Victory-Class Star Destroyer
				}},
				10,
				10,
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
	public void YerkaMigStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Yerka Mig
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Effect
		 * Subtype: Utinni
		 * Destiny: 3
		 * Icons:
		 * Game Text: Deploy at any location. You may move Mig like a character. Target an opponent's character,
		 * 		who may apprehend (cancel) Mig by reaching same location. Until then, during all battles at same
		 * 		and adjacent sites to Mig, opponent's total power is -1.
		 * Lore: An Imperial bureaucrat with high security clearance who resigned and fled in remorse after the
		 * 		occupation of his home planet, Ralltiir. Now a fugitive from the ISB.
		 * Set: Premiere
		 * Rarity: U1
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("yerka").getBlueprint();

		assertEquals("Yerka Mig", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.EFFECT);
		}});
		assertEquals(CardSubtype.UTINNI, card.getCardSubtype());
		assertEquals(3, card.getDestiny(), scn.epsilon);

		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.EFFECT);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
		}});
		assertTrue(card.isMovesLikeCharacter());
		assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
		assertEquals(Rarity.U1, card.getRarity());
	}

	@Test
	public void YerkaMigCanDeployToSite() {
		//test1: can deploy to site with force icons
		//test2: can deploy to site without force icons
		//test3: after deploying to site, Yerka is at the site
		var scn = GetScenario();

		var yerka = scn.GetLSCard("yerka");

		var trooper = scn.GetDSFiller(1);
		var site = scn.GetDSStartingLocation();
		var landing = scn.GetDSCard("landing");

		scn.StartGame();

		scn.MoveCardsToLSHand(yerka);

		scn.MoveLocationToTable(landing);
		scn.MoveCardsToLocation(landing, trooper);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(yerka));
		scn.LSDeployCard(yerka);
		assertTrue(scn.LSHasCardChoiceAvailable(site)); //test1
		assertTrue(scn.LSHasCardChoiceAvailable(landing)); //test2
		scn.LSChooseCard(site);
		scn.LSChooseCard(trooper);
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertTrue(scn.CardsAtLocation(site, yerka)); //tets3

	}

	@Test
	public void YerkaMigCannotDeployToSystem() {
		//shows fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/975
		var scn = GetScenario();

		var yerka = scn.GetLSCard("yerka");
		var system = scn.GetLSStartingLocation();

		var trooper = scn.GetDSFiller(1);
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(yerka);

		scn.MoveCardsToLocation(site, trooper);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(yerka);
		assertFalse(scn.LSHasCardChoiceAvailable(system));
	}

	@Test
	public void YerkaMigCannotDeployToDagobahSite() {
		var scn = GetScenario();

		var yerka = scn.GetLSCard("yerka");
		var hut = scn.GetLSCard("hut");

		var trooper = scn.GetDSFiller(1);
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(yerka);

		scn.MoveLocationToTable(hut);
		scn.MoveCardsToLocation(site, trooper);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(yerka));
		scn.LSDeployCard(yerka);
		assertTrue(scn.LSHasCardChoiceAvailable(site));
		assertFalse(scn.LSHasCardChoiceAvailable(hut)); //test1
	}

	@Test @Ignore
	public void YerkaMigCanDeployAboardStarship() {
		//test1: can deploy aboard starship with passenger capacity (1+ available)
		//test2: can deploy aboard starship with passenger capacity (0 available)
		//test3: cannot deploy aboard starship without passenger capacity
		//test4: cannot deploy aboard opponent's starship
		//test5: after deploying, is aboard as a passenger
		var scn = GetScenario();

		var yerka = scn.GetLSCard("yerka");
		var ywing = scn.GetLSCard("ywing");
		var ywing2 = scn.GetLSCard("ywing2");
		var xwing = scn.GetLSCard("xwing");
		var rebelTrooper = scn.GetLSFiller(1);
		var system = scn.GetLSStartingLocation();

		var trooper = scn.GetDSFiller(1);
		var vcsd = scn.GetDSCard("vcsd");
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(yerka, rebelTrooper);

		scn.MoveCardsToLocation(site, trooper);
		scn.MoveCardsToLocation(system, xwing, ywing, ywing2, vcsd);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(rebelTrooper);
		scn.LSChooseCard(ywing2);
		scn.PassAllResponses();
		assertTrue(scn.IsAboardAsPassenger(ywing2,rebelTrooper));

		scn.DSPass();

		scn.LSDeployCard(yerka);
		assertTrue(scn.LSHasCardChoiceAvailable(ywing)); //test1
		assertTrue(scn.LSHasCardChoiceAvailable(ywing2)); //test2
		assertFalse(scn.LSHasCardChoiceAvailable(xwing)); //test3
		assertFalse(scn.LSHasCardChoiceAvailable(vcsd)); //test4
		scn.LSChooseCard(ywing2);
		scn.LSChooseCard(trooper);
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
			///FAILS HERE - does not actually deploy aboard
		assertTrue(scn.IsAttachedTo(ywing2, yerka)); //test5
		assertTrue(scn.IsAboardAsPassenger(ywing2, yerka)); //test5
	}

	@Test @Ignore
	public void YerkaMigMovesWithStarship() {
		//test1: when aboard a starship that moves, Yerka stays attached and moves with the ship
		var scn = GetScenario();

		var yerka = scn.GetLSCard("yerka");
		var ywing = scn.GetLSCard("ywing");
		var hoth = scn.GetLSCard("hoth");
		var system = scn.GetLSStartingLocation();

		var trooper = scn.GetDSFiller(1);
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(yerka);

		scn.MoveLocationToTable(hoth);
		scn.MoveCardsToLocation(site, trooper);
		scn.MoveCardsToLocation(system, ywing);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(yerka);
		scn.LSChooseCard(ywing);
		scn.LSChooseCard(trooper);
		scn.PassAllResponses();

		scn.SkipToPhase(Phase.MOVE);

		scn.LSUseCardAction(ywing);
		scn.LSChooseCard(hoth);
		scn.PassAllResponses();

		assertTrue(scn.CardsAtLocation(hoth, ywing));
			///FAILS HERE
		assertTrue(scn.IsAttachedTo(ywing, yerka)); //test1
	}

    @Test
    public void YerkaMigCanEmbarkOnVehicle() {
        //test1: can embark on a vehicle with passenger capacity (1+ available)
        //test2: attaches to skiff
        //test3: is identified as being aboard as passenger
        var scn = GetScenario();

        var yerka = scn.GetLSCard("yerka");
        var skiff = scn.GetLSCard("skiff");
        var site = scn.GetDSStartingLocation();

        var trooper = scn.GetDSFiller(1);
        var vcsd = scn.GetDSCard("vcsd");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(yerka);

        scn.MoveCardsToLocation(site, skiff);
        scn.MoveCardsToLocation(system, vcsd);

        scn.BoardAsPassenger(vcsd, trooper);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(yerka);
        scn.LSChooseCard(site);
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.LSCardActionAvailable(yerka,"Embark")); //test1
        scn.LSUseCardAction(yerka,"Embark");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.IsAttachedTo(skiff, yerka)); //test2
        assertTrue(scn.IsAboardAsPassenger(skiff, yerka)); //test3
    }

    @Test
    public void YerkaMigCanJumpOffVehicle() {
        //test1: when characters aboard are given option to 'jump off' (disembark) a vehicle, Yerka is also allowed to 'jump off'
        //test2: skiff is lost
        //test3: yerka is not lost (and at the location)
        var scn = GetScenario();

        var yerka = scn.GetLSCard("yerka");
        var skiff = scn.GetLSCard("skiff");
        var rebelTrooper = scn.GetLSFiller(1);
        var site = scn.GetDSStartingLocation();

        var trooper = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);
        var trooper4 = scn.GetDSFiller(4);
        var vcsd = scn.GetDSCard("vcsd");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(yerka);

        scn.MoveCardsToLocation(site, skiff, trooper2, trooper3, trooper4);
        scn.MoveCardsToLocation(system, vcsd);

        scn.BoardAsPassenger(vcsd, trooper);
        scn.BoardAsPassenger(skiff, rebelTrooper);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(yerka);
        scn.LSChooseCard(site);
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.MOVE);
        scn.LSUseCardAction(yerka,"Embark");
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToDamageSegment();
        assertTrue(scn.AwaitingLSBattleDamagePayment()); //LS power 1, DS power 2 (3 - 1 from yerka)
        scn.LSChooseCard(skiff);

        assertTrue(scn.LSCardActionAvailable(rebelTrooper, "off")); //jump off
        assertTrue(scn.LSCardActionAvailable(yerka, "off")); //test1
        scn.LSUseCardAction(yerka, "off");

        scn.DSPass(); //DISEMBARKING - Optional responses
        scn.LSPass();

        scn.DSPass(); //DISEMBARKED - Optional responses
        scn.LSPass();

        assertTrue(scn.LSCardActionAvailable(rebelTrooper, "off")); //jump off
        assertFalse(scn.LSCardActionAvailable(yerka, "off")); //jump off
        scn.LSUseCardAction(rebelTrooper, "off");

        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertEquals(Zone.TOP_OF_LOST_PILE,skiff.getZone()); //test2
        assertTrue(scn.CardsAtLocation(site, yerka, rebelTrooper)); //test3
    }

    //add tests for:
	// requires targetable opponent's character to play

	// canceling

	// power -1
	// 	does not apply outside of battle
	// 	applies during battle at same site
	//  applies during battle at adjacent site
	//  does not apply during battle at non-adjacent (but related) site

	// movement
	//  embark/disembark, etc
    //  cannot embark on vehicle without passenger capacity (ewok glider?)

	// deploy
	//	deploy aboard vehicle with passenger capacity
	//  cannot deploy aboard vehicle with no passenger capacity

	// 'aboard'
	//	stays attached to carrying card
}


