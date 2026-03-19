package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_3_159_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("ywing","1_147");
                    put("snowspeeder","3_069");
                }},
				new HashMap<>()
				{{
                    put("eweb", "3_159"); //E-web Blaster
                    put("eg6","1_175"); //power droid (power source for artillery)
                    put("barrier","1_249");
                    put("cantina","1_290");
                    put("kessel","1_288");
                    put("db_94","1_291"); //tatooine: docking bay 94
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
	public void EwebBlasterStatsAndKeywordsAreCorrect() {
		/**
		 * Title: E-web Blaster
		 * Uniqueness: Unrestricted
		 * Side: Dark
		 * Type: Weapon
         * Subtype: Artillery
		 * Destiny: 5
		 * Icons: Hoth, Weapon
		 * Game Text: Deploy on any site. May be moved with two warriors for 1 additional Force.
         *      Your warrior present may target a starfighter (use 5 as defense value), character, creature
         *      or vehicle using 2 Force. Draw destiny. Target hit if destiny +1 > defense value.
		 * Lore: Massive infantry weapon powerful enough to damage even starfighters.
		 * Set: Hoth
		 * Rarity: C1
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("eweb").getBlueprint();

		assertEquals("E-web Blaster", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertEquals(5, card.getDestiny(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.WEAPON);
		}});
        assertEquals(CardSubtype.ARTILLERY, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER);
            add(Keyword.ARTILLERY_WEAPON_MAY_USE_DB_TRANSIT);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.HOTH);
			add(Icon.WEAPON);
		}});
		assertEquals(ExpansionSet.HOTH,card.getExpansionSet());
		assertEquals(Rarity.C1,card.getRarity());
	}

	@Test
	public void EwebDeployCostTest() {
        //Test1: deploys on a site (yours, interior)
        //Test2: deploys on a site (opponent's, exterior)
        //Test3: does not deploy on a system
        //Test4: does not deploy on your warrior
        //Test5: deploys for 3 force
		var scn = GetScenario();

        var eweb = scn.GetDSCard("eweb");
        var cantina = scn.GetDSCard("cantina");
        var kessel = scn.GetDSCard("kessel");
        var warrior = scn.GetDSFiller(1); //stormtrooper

        var ls_site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(kessel);

        scn.MoveCardsToLocation(ls_site, warrior);

        scn.MoveCardsToDSHand(eweb);

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.GetDSForcePileCount() >= 3); //enough to deploy
        assertTrue(scn.DSDeployAvailable(eweb));
        scn.DSDeployCard(eweb);

        assertTrue(scn.DSHasCardChoiceAvailable(cantina)); //Test1: deploys on a site (yours)
        assertTrue(scn.DSHasCardChoiceAvailable(ls_site)); //Test2: deploys on a site (opponent's)
        assertFalse(scn.DSHasCardChoiceAvailable(kessel)); //Test3: does not deploy on non-site location
        assertFalse(scn.DSHasCardChoiceAvailable(warrior)); //Test4: does not deploy on your warrior
        assertEquals(0,scn.GetDSUsedPileCount());
        scn.DSChooseCard(cantina);
        scn.PassAllResponses();

        assertEquals(3,scn.GetDSUsedPileCount()); //Test5: paid 3 to deploy
        assertTrue(scn.IsAttachedTo(cantina, eweb));
        assertFalse(scn.CardsAtLocation(cantina, eweb)); //(attached, so not 'at' for test)
    }

    /// ---------------- WEAPON FIRING TEST GROUP -----------------------

    @Test
    public void EwebFireBasicActionTest() {
        //Test1: action provided to fire e-web (during battle) when conditions are met:
        //      your warrior present, your power source present, opponent's character present
        //Test2: when firing, must select a character to fire
        //Test3: must select 1 opponent's character as target
        //Test4: cost of 2 force is paid
        //Test5: 1 destiny is drawn
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1); //rebel trooper

        var eweb = scn.GetDSCard("eweb");
        var eg6 = scn.GetDSCard("eg6");
        var cantina = scn.GetDSCard("cantina");
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(cantina);

        scn.MoveCardsToLocation(cantina, eg6, stormtrooper, rebeltrooper);

        scn.AttachCardsTo(cantina, eweb);

        scn.DSActivateForceCheat(1);

        scn.SkipToPhase(Phase.BATTLE);

        assertEquals(4,scn.GetDSForcePileCount()); //enough to battle, fire
        scn.DSInitiateBattle(cantina);

        assertEquals(1,scn.GetDSUsedPileCount());

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(eweb, "Fire")); //test1
        scn.DSUseCardAction(eweb,"Fire");

        assertTrue(scn.DSDecisionAvailable("Choose character to fire")); //test2
        scn.DSChooseCard(stormtrooper);

        assertTrue(scn.DSDecisionAvailable("Choose target")); //test3
        scn.DSChooseCard(rebeltrooper);

        scn.LSPass(); //Use 2 Force - Optional responses
        scn.DSPass();

        assertEquals(3,scn.GetDSUsedPileCount()); //test4

        scn.LSPass(); //Fire E-web Blaster - Optional responses
        scn.DSPass();

        //first destiny
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

        assertEquals(4,scn.GetDSUsedPileCount()); //drew 1 destiny
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions()); //finished weapon firing action
    }

    @Test
    public void EwebHitCharacterTest() {
        //Test1: hits character with destiny +1 > defense value
        //(target rebel trooper with ability 1 and draw a destiny of 1 = hit)
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1); //rebel trooper

        var eweb = scn.GetDSCard("eweb");
        var eg6 = scn.GetDSCard("eg6");
        var db_94 = scn.GetDSCard("db_94");
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(db_94);

        scn.MoveCardsToLocation(db_94, eg6, stormtrooper, rebeltrooper);

        scn.AttachCardsTo(db_94, eweb);

        scn.DSActivateForceCheat(1);

        scn.SkipToPhase(Phase.BATTLE);

        scn.PrepareDSDestiny(1);

        scn.DSInitiateBattle(db_94);

        scn.DSUseCardAction(eweb,"Fire");
        scn.DSChooseCard(stormtrooper);
        scn.DSChooseCard(rebeltrooper);
        scn.PassAllResponses();

        assertTrue(rebeltrooper.isHit());
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    @Test
    public void EwebMissCharacterTest() {
        //Test1: misses character with destiny +1 <= defense value
        //(target rebel trooper with ability 1 and draw a destiny of 0 = miss)
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1); //rebel trooper

        var eweb = scn.GetDSCard("eweb");
        var eg6 = scn.GetDSCard("eg6");
        var db_94 = scn.GetDSCard("db_94");
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(db_94);

        scn.MoveCardsToLocation(db_94, eg6, stormtrooper, rebeltrooper);

        scn.AttachCardsTo(db_94, eweb);

        scn.DSActivateForceCheat(1);

        scn.SkipToPhase(Phase.BATTLE);

        scn.PrepareDSDestiny(0);

        scn.DSInitiateBattle(db_94);

        scn.DSUseCardAction(eweb,"Fire");
        scn.DSChooseCard(stormtrooper);
        scn.DSChooseCard(rebeltrooper);
        scn.PassAllResponses();

        assertFalse(rebeltrooper.isHit());
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    @Test
    public void EwebHitStarshipTest() {
        //Test1: hits starship with destiny +1 > 5
        //(target y-wing and draw a destiny of 5 = hit)
        var scn = GetScenario();

        var ywing = scn.GetLSCard("ywing");

        var eweb = scn.GetDSCard("eweb");
        var eg6 = scn.GetDSCard("eg6");
        var db_94 = scn.GetDSCard("db_94");
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(db_94);

        scn.MoveCardsToLocation(db_94, eg6, stormtrooper, ywing);

        scn.AttachCardsTo(db_94, eweb);

        scn.DSActivateForceCheat(1);

        scn.SkipToPhase(Phase.BATTLE);

        scn.PrepareDSDestiny(5);

        scn.DSInitiateBattle(db_94);

        scn.DSUseCardAction(eweb,"Fire");
        scn.DSChooseCard(stormtrooper);
        scn.DSChooseCard(ywing);
        scn.PassAllResponses();

        assertTrue(ywing.isHit());
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    @Test
    public void EwebMissStarshipTest() {
        //Test1: misses starship with destiny +1 <=> 5
        //(target y-wing and draw a destiny of 4 = miss)
        var scn = GetScenario();

        var ywing = scn.GetLSCard("ywing");

        var eweb = scn.GetDSCard("eweb");
        var eg6 = scn.GetDSCard("eg6");
        var db_94 = scn.GetDSCard("db_94");
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(db_94);

        scn.MoveCardsToLocation(db_94, eg6, stormtrooper, ywing);

        scn.AttachCardsTo(db_94, eweb);

        scn.DSActivateForceCheat(1);

        scn.SkipToPhase(Phase.BATTLE);

        scn.PrepareDSDestiny(4);

        scn.DSInitiateBattle(db_94);

        scn.DSUseCardAction(eweb,"Fire");
        scn.DSChooseCard(stormtrooper);
        scn.DSChooseCard(ywing);
        scn.PassAllResponses();

        assertFalse(ywing.isHit());
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    @Test
    public void EwebHitVehicleTest() {
        //Test1: hits vehicle with destiny +1 > defense value
        //(target snowspeeder with maneuver 4 and draw a destiny of 4 = hit)
        var scn = GetScenario();

        var snowspeeder = scn.GetLSCard("snowspeeder");

        var eweb = scn.GetDSCard("eweb");
        var eg6 = scn.GetDSCard("eg6");
        var db_94 = scn.GetDSCard("db_94");
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(db_94);

        scn.MoveCardsToLocation(db_94, eg6, stormtrooper, snowspeeder);

        scn.AttachCardsTo(db_94, eweb);

        scn.DSActivateForceCheat(1);

        scn.SkipToPhase(Phase.BATTLE);

        scn.PrepareDSDestiny(4);

        scn.DSInitiateBattle(db_94);

        scn.DSUseCardAction(eweb,"Fire");
        scn.DSChooseCard(stormtrooper);
        scn.DSChooseCard(snowspeeder);
        scn.PassAllResponses();

        assertTrue(snowspeeder.isHit());
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    @Test
    public void EwebMissVehicleTest() {
        //Test1: hits vehicle with destiny +1 <=> defense value
        //(target snowspeeder with maneuver 4 and draw a destiny of 3 = miss)
        var scn = GetScenario();

        var snowspeeder = scn.GetLSCard("snowspeeder");

        var eweb = scn.GetDSCard("eweb");
        var eg6 = scn.GetDSCard("eg6");
        var db_94 = scn.GetDSCard("db_94");
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(db_94);

        scn.MoveCardsToLocation(db_94, eg6, stormtrooper, snowspeeder);

        scn.AttachCardsTo(db_94, eweb);

        scn.DSActivateForceCheat(1);

        scn.SkipToPhase(Phase.BATTLE);

        scn.PrepareDSDestiny(3);

        scn.DSInitiateBattle(db_94);

        scn.DSUseCardAction(eweb,"Fire");
        scn.DSChooseCard(stormtrooper);
        scn.DSChooseCard(snowspeeder);
        scn.PassAllResponses();

        assertFalse(snowspeeder.isHit());
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }


    /// fire tests to add:
    //see Medium Repeating Blaster Cannon for other firing test ideas

    //cannot fire at non-adjacent sites (battle at related, but non-adjacent site?)
    //firing outside of battle (via sniper?), can target 1 character at same site
    //check hit vs creatures
    //check hit vs character
    //check hit vs starfighter
    //check hit vs vehicle

    /// ---------------- MOVEMENT TEST GROUP -----------------------

    @Test
    public void EwebCanBeCarriedBasicTest() {
        //Test1: E-web present with 2 warriors can be carried to an adjacent site
        var scn = GetScenario();

        var eweb = scn.GetDSCard("eweb");
        var warrior1 = scn.GetDSFiller(1);
        var warrior2 = scn.GetDSFiller(2);
        var cantina = scn.GetDSCard("cantina");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveCardsToLocation(cantina, warrior1, warrior2);
        scn.AttachCardsTo(cantina, eweb);
        scn.SkipToPhase(Phase.MOVE);

        assertEquals(3,scn.GetDSForcePileCount()); //enough to pay cost 3 to move

        assertTrue(scn.DSMoveAvailable(warrior1)); //landspeed to marketplace
        assertTrue(scn.DSMoveAvailable(warrior2)); //landspeed to marketplace
        assertTrue(scn.DSCardActionAvailable(eweb, "Move using two warriors"));
        scn.DSUseCardAction(eweb,"Move using two warriors");

        assertTrue(scn.DSDecisionAvailable("Choose site"));
        assertTrue(scn.DSHasCardChoiceAvailable(marketplace));
        assertFalse(scn.DSHasCardChoiceAvailable(cantina));
        scn.DSChooseCard(marketplace);

        assertTrue(scn.DSDecisionAvailable("Choose first warrior"));
        assertTrue(scn.DSHasCardChoiceAvailable(warrior1));
        assertTrue(scn.DSHasCardChoiceAvailable(warrior2));
        scn.DSChooseCard(warrior1);

        assertTrue(scn.DSDecisionAvailable("Choose second warrior"));
        assertFalse(scn.DSHasCardChoiceAvailable(warrior1));
        assertTrue(scn.DSHasCardChoiceAvailable(warrior2));
        scn.DSChooseCard(warrior2);

        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertEquals(0,scn.GetDSForcePileCount()); //paid 3 to move
        assertTrue(scn.CardsAtLocation(marketplace, warrior1));
        assertTrue(scn.CardsAtLocation(marketplace, warrior2));
        assertFalse(scn.IsAttachedTo(cantina,eweb));
        assertTrue(scn.IsAttachedTo(marketplace,eweb));
    }

    @Test
    public void EwebCannotBeCarriedIfNotEnoughForceTest() {
        //Test1: E-web present with 2 warriors cannot be carried to an adjacent site if not enough force available to pay
        var scn = GetScenario();

        var eweb = scn.GetDSCard("eweb");
        var warrior1 = scn.GetDSFiller(1);
        var warrior2 = scn.GetDSFiller(2);
        var cantina = scn.GetDSCard("cantina");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveCardsToLocation(cantina, warrior1, warrior2);
        scn.AttachCardsTo(cantina, eweb);

        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile()); //use 1 force so we don't have enough to pay cost of 3

        scn.SkipToPhase(Phase.MOVE);

        assertEquals(2,scn.GetDSForcePileCount()); //not enough to pay cost 3 to move

        assertTrue(scn.DSMoveAvailable(warrior1)); //landspeed to marketplace
        assertTrue(scn.DSMoveAvailable(warrior2)); //landspeed to marketplace
        assertFalse(scn.DSCardActionAvailable(eweb, "Move using two warriors"));
    }

    //see Medium Repeating Blaster Cannon for other movement tests

}
