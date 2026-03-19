package com.gempukku.swccgo.cards.set3.light;

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
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_3_077_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("mrbc", "3_077"); //Medium Repeating Blaster Cannon
                    put("nonwarrior","1_031"); //talz
                    put("cantina","1_128");
                    put("jp","7_131"); //tatooine: jabba's palace
                    put("kessel","1_126");
                    put("eg4","3_008"); //power droid (power source for artillery)
                    put("gunganGuard1","14_013"); //gungan guard (warrior with +2 move cost)
                    put("gunganGuard2","14_013");
                    put("mando1","216_030"); //heavy infantry mandolorian (warrior with landspeed = 3)
                    put("mando2","216_030");
                    put("yutani","8_001"); //captain yutani
                }},
				new HashMap<>()
				{{
                    put("barrier","1_249");
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
	public void MRBCStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Medium Repeating Blaster Cannon
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Weapon
         * Subtype: Artillery
		 * Destiny: 1
		 * Icons: Hoth, Weapon
		 * Game Text: Deploy on a site. May be moved by two warriors for 1 additional Force. Your warrior present
         *      may target up to two characters or two creatures at same or adjacent site using 2 Force.
         *      Draw two destiny. Target(s) hit if total destiny > total defense value.
		 * Lore: Merr-Sonn Mark II repeating blaster. Accepts power cells from a very wide variety of sources,
         *      a benefit for Rebels accustomed to scavenging for supplies.
		 * Set: Hoth
		 * Rarity: C1
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("mrbc").getBlueprint();

		assertEquals("Medium Repeating Blaster Cannon", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.WEAPON);
		}});
        assertEquals(CardSubtype.ARTILLERY, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER);
            add(Keyword.CANNON);
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
	public void MRBCDeployCostTest() {
        //Test1: deploys on a site (yours, interior)
        //Test2: deploys on a site (opponent's, exterior)
        //Test3: does not deploy on a system
        //Test4: does not deploy on your warrior
        //Test5: deploys for 3 force
		var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var cantina = scn.GetLSCard("cantina");
        var kessel = scn.GetLSCard("kessel");
        var warrior = scn.GetLSFiller(1); //rebel trooper

        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(kessel);

        scn.MoveCardsToLocation(marketplace, warrior);

        scn.MoveCardsToLSHand(mrbc);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.GetLSForcePileCount() >= 3); //enough to deploy
        assertTrue(scn.LSDeployAvailable(mrbc));
        scn.LSDeployCard(mrbc);

        assertTrue(scn.LSHasCardChoiceAvailable(cantina)); //Test1: deploys on a site (yours)
        assertTrue(scn.LSHasCardChoiceAvailable(marketplace)); //Test2: deploys on a site (opponent's)
        assertFalse(scn.LSHasCardChoiceAvailable(kessel)); //Test3: does not deploy on non-site location
        assertFalse(scn.LSHasCardChoiceAvailable(warrior)); //Test4: does not deploy on your warrior
        assertEquals(0,scn.GetLSUsedPileCount());
        scn.LSChooseCard(cantina);
        scn.PassAllResponses();

        assertEquals(3,scn.GetLSUsedPileCount()); //Test5: paid 3 to deploy
        assertTrue(scn.IsAttachedTo(cantina, mrbc));
        assertFalse(scn.CardsAtLocation(cantina, mrbc)); //(attached, so not 'at' for test)
    }

    /// ---------------- WEAPON FIRING TEST GROUP -----------------------

    @Test
    public void MRBCFireBasicActionTest() {
        //Test1: action provided to fire mrbc (during battle) when conditions are met:
        //      your warrior present, your power source present, opponent's character present
        //Test2: no action for firing at 2 targets is provided if only 1 eligible
        //Test3: when firing, must select a character to fire
        //Test4: must select 1 opponent's character as target
        //Test5: cost of 2 force is paid
        //Test6: 2 destiny are drawn
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var eg4 = scn.GetLSCard("eg4");
        var warrior = scn.GetLSFiller(1); //rebel trooper
        var cantina = scn.GetLSCard("cantina");

        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(cantina);

        scn.MoveCardsToLocation(cantina, eg4, warrior, stormtrooper);

        scn.AttachCardsTo(cantina, mrbc);

        scn.SkipToLSTurn(Phase.BATTLE);

        assertEquals(5,scn.GetLSForcePileCount()); //enough to battle, fire
        scn.LSInitiateBattle(cantina);

        assertEquals(1,scn.GetLSUsedPileCount());

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(mrbc, "Fire")); //test1
        assertEquals(1,scn.LSGetActionChoices().size()); //test2
        assertFalse(scn.LSCardActionAvailable(mrbc, "at 2 targets")); //test2
        scn.LSUseCardAction(mrbc,"Fire");

        assertTrue(scn.LSDecisionAvailable("Choose character to fire")); //test3
        scn.LSChooseCard(warrior);

        assertTrue(scn.LSDecisionAvailable("Choose target")); //test4
        scn.LSChooseCard(stormtrooper);

        scn.DSPass(); //Use 2 Force - Optional responses
        scn.LSPass();

        assertEquals(3,scn.GetLSUsedPileCount()); //test5

        scn.DSPass(); //Fire Medium Repeating Blaster Cannon - Optional responses
        scn.LSPass();

        //first destiny
        scn.DSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        scn.LSPass();

        scn.DSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.LSPass();

        //second destiny
        scn.DSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        scn.LSPass();

        scn.DSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.LSPass();


        scn.DSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.LSPass();

        assertEquals(5,scn.GetLSUsedPileCount()); //drew 2 destiny
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions()); //finished weapon firing action
    }

    @Test
    public void MRBCMayTargetOneCharacterAtSameSite() {
        //Test1: warrior can fire mrbc with power source at one character, at same site, during battle
        //Test2: no option for firing at 2 targets if only 1 eligible
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var eg4 = scn.GetLSCard("eg4");
        var warrior = scn.GetLSFiller(1); //rebel trooper
        var cantina = scn.GetLSCard("cantina");

        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, eg4, warrior, stormtrooper);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(cantina);

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(mrbc, "Fire")); //test1
        assertEquals(1,scn.LSGetActionChoices().size()); //test2
        assertFalse(scn.LSCardActionAvailable(mrbc, "at 2 targets")); //test2
        scn.LSUseCardAction(mrbc,"Fire");

        assertTrue(scn.LSDecisionAvailable("Choose character to fire"));
        scn.LSChooseCard(warrior);

        assertTrue(scn.LSDecisionAvailable("Choose target"));
        assertFalse(scn.LSDecisionAvailable("Choose targets"));
        scn.LSChooseCard(stormtrooper);
    }

    @Test
    public void MRBCMayTargetTwoCharacterAtSameSite() {
        //Test1: warrior can fire mrbc with power source at one character, at same site, during battle
        //Test2: warrior can fire mrbc with power source at two characters, at same site, during battle
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var eg4 = scn.GetLSCard("eg4");
        var warrior = scn.GetLSFiller(1); //rebel trooper
        var cantina = scn.GetLSCard("cantina");

        var stormtrooper1 = scn.GetDSFiller(1);
        var stormtrooper2 = scn.GetDSFiller(2);

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, eg4, warrior, stormtrooper1, stormtrooper2);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(cantina);

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertEquals(2,scn.LSGetActionChoices().size()); //test1 (infer that choices are to fire at 1 or 2)
//        assertEquals("Fire Medium Repeating Blaster Cannon",scn.LSGetActionChoices().get(0));
//        assertEquals("Fire Medium Repeating Blaster Cannon at 2 targets",scn.LSGetActionChoices().get(1));
        assertTrue(scn.LSCardActionAvailable(mrbc, "at 2 targets")); //test2
        scn.LSUseCardAction(mrbc,"at 2 targets");

        assertTrue(scn.LSDecisionAvailable("Choose character to fire"));
        scn.LSChooseCard(warrior);

        assertTrue(scn.LSDecisionAvailable("Choose targets"));
        assertTrue(scn.LSHasCardChoiceAvailable(stormtrooper1));
        assertTrue(scn.LSHasCardChoiceAvailable(stormtrooper2));
        scn.LSChooseCards(stormtrooper1, stormtrooper2);
    }

    @Test
    public void MRBCMayNotFireWithoutPowerSourceTest() {
        //Test1: action not provided to fire mrbc (during battle) when no power source present
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var warrior = scn.GetLSFiller(1); //rebel trooper
        var cantina = scn.GetLSCard("cantina");

        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, warrior, stormtrooper);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.BATTLE);

        assertEquals(5,scn.GetLSForcePileCount()); //enough to battle, fire
        scn.LSInitiateBattle(cantina);

        assertEquals(1,scn.GetLSUsedPileCount());

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(mrbc, "Fire")); //test1
    }

    @Test
    public void MRBCMayTargetOneCharacterAtAdjacentSite() {
        //Test1: warrior can fire mrbc with power source at one character, at adjacent site, during battle
        //Test2: no option for firing at 2 targets if only 1 eligible
        //Test3: only warrior with mrbc is eligible to fire
        //Test4: only opponent's character(s) in battle are eligible targets
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var eg4 = scn.GetLSCard("eg4");
        var warrior1 = scn.GetLSFiller(1);
        var warrior2 = scn.GetLSFiller(2);
        var cantina = scn.GetLSCard("cantina");

        var stormtrooper1 = scn.GetDSFiller(1);
        var stormtrooper2 = scn.GetDSFiller(2);
        var stormtrooper3 = scn.GetDSFiller(3);
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, eg4, warrior1, stormtrooper2, stormtrooper3);
        scn.MoveCardsToLocation(marketplace, warrior2, stormtrooper1);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(marketplace);

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(mrbc, "Fire")); //test1
        assertEquals(1,scn.LSGetActionChoices().size()); //test2
        assertFalse(scn.LSCardActionAvailable(mrbc, "at 2 targets")); //test2
        scn.LSUseCardAction(mrbc,"Fire");

        assertTrue(scn.LSDecisionAvailable("Choose character to fire"));
        assertTrue(scn.LSHasCardChoiceAvailable(warrior1)); //test3
        assertFalse(scn.LSHasCardChoiceAvailable(warrior2));
        scn.LSChooseCard(warrior1);

        assertTrue(scn.LSDecisionAvailable("Choose target"));
        assertFalse(scn.LSDecisionAvailable("Choose targets"));
        assertTrue(scn.LSHasCardChoiceAvailable(stormtrooper1));
        assertFalse(scn.LSHasCardChoiceAvailable(stormtrooper2)); //test4
        assertFalse(scn.LSHasCardChoiceAvailable(stormtrooper3)); //test4
        scn.LSChooseCard(stormtrooper1);
    }

    @Test
    public void MRBCMayTargetTwoCharactersAtAdjacentSite() {
        //Test1: warrior can fire mrbc with power source at one character, at adjacent site, during battle
        //Test1: warrior can fire mrbc with power source at two characters, at adjacent site, during battle
        //Test3: only warrior with mrbc is eligible to fire
        //Test4: only opponent's characters in battle are eligible targets
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var eg4 = scn.GetLSCard("eg4");
        var warrior1 = scn.GetLSFiller(1);
        var warrior2 = scn.GetLSFiller(2);
        var cantina = scn.GetLSCard("cantina");

        var stormtrooper1 = scn.GetDSFiller(1);
        var stormtrooper2 = scn.GetDSFiller(2);
        var stormtrooper3 = scn.GetDSFiller(3);
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, eg4, warrior1, stormtrooper3);
        scn.MoveCardsToLocation(marketplace, warrior2, stormtrooper1, stormtrooper2);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(marketplace);

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertEquals(2,scn.LSGetActionChoices().size()); //test2
        assertTrue(scn.LSCardActionAvailable(mrbc, "at 2 targets")); //test2
        scn.LSUseCardAction(mrbc,"at 2 targets");

        assertTrue(scn.LSDecisionAvailable("Choose character to fire"));
        assertTrue(scn.LSHasCardChoiceAvailable(warrior1)); //test3
        assertFalse(scn.LSHasCardChoiceAvailable(warrior2));
        scn.LSChooseCard(warrior1);

        assertTrue(scn.LSDecisionAvailable("Choose targets"));
        assertTrue(scn.LSHasCardChoiceAvailable(stormtrooper1));
        assertTrue(scn.LSHasCardChoiceAvailable(stormtrooper2));
        assertFalse(scn.LSHasCardChoiceAvailable(stormtrooper3)); //test4
        scn.LSChooseCards(stormtrooper1, stormtrooper2);
    }

    @Test
    public void MRBCMayNotFireWithoutWarriorTest() {
        //Test1: action not provided to fire mrbc (during battle) when no warrior present
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var eg4 = scn.GetLSCard("eg4");
        var nonwarrior = scn.GetLSCard("nonwarrior");
        var cantina = scn.GetLSCard("cantina");

        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, eg4, nonwarrior, stormtrooper);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(cantina);

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(mrbc, "Fire")); //test1
    }

    /// fire tests to add:
    //cannot fire at non-adjacent sites (battle at related, but non-adjacent site?)
    // firing outside of battle (via sorry about the mess?), can target 1 character at same site and 1 at adjacent site
    //can fire at creatures
    //check destiny draws (0, 1, or 2)
    //check hit vs defense for 1 character
    //check hit vs combined defense for 2 characters
    //verify hit targets are lost after battle
    //verify cannot target vehicles/starships


    /// ---------------- MOVEMENT TEST GROUP -----------------------

    @Test
    public void MRBCMayNotSelfMoveTest() {
        //Test1: no movement phase actions available when alone
        //      no self-move with landspeed (even with power source present)
        //      no MRBC-specific carry action
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var eg4 = scn.GetLSCard("eg4");
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace
        scn.MoveCardsToLocation(cantina, eg4);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.MOVE);

        assertTrue(scn.LSMoveAvailable(eg4)); //landspeed to marketplace (DS starting site)
        assertFalse(scn.LSCardActionAvailable(mrbc)); //no actions of any kind
    }

    @Test
    public void MRBCCanBeCarriedBasicTest() {
        //Test1: MRBC present with 2 warriors can be carried to an adjacent site
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var warrior1 = scn.GetLSFiller(1);
        var warrior2 = scn.GetLSFiller(2);
        var cantina = scn.GetLSCard("cantina");

        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveCardsToLocation(cantina, warrior1, warrior2);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.MOVE);

        assertEquals(5,scn.GetLSForcePileCount()); //enough to pay cost 3 to move

        assertTrue(scn.LSMoveAvailable(warrior1)); //landspeed to marketplace
        assertTrue(scn.LSMoveAvailable(warrior2)); //landspeed to marketplace
        assertTrue(scn.LSCardActionAvailable(mrbc, "Move using two warriors"));
        scn.LSUseCardAction(mrbc,"Move using two warriors");

        assertTrue(scn.LSDecisionAvailable("Choose site"));
        assertTrue(scn.LSHasCardChoiceAvailable(marketplace));
        assertFalse(scn.LSHasCardChoiceAvailable(cantina));
        scn.LSChooseCard(marketplace);

        assertTrue(scn.LSDecisionAvailable("Choose first warrior"));
        assertTrue(scn.LSHasCardChoiceAvailable(warrior1));
        assertTrue(scn.LSHasCardChoiceAvailable(warrior2));
        scn.LSChooseCard(warrior1);

        assertTrue(scn.LSDecisionAvailable("Choose second warrior"));
        assertFalse(scn.LSHasCardChoiceAvailable(warrior1));
        assertTrue(scn.LSHasCardChoiceAvailable(warrior2));
        scn.LSChooseCard(warrior2);

        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertEquals(2,scn.GetLSForcePileCount()); //paid 3 to move
        assertTrue(scn.CardsAtLocation(marketplace, warrior1));
        assertTrue(scn.CardsAtLocation(marketplace, warrior2));
        assertFalse(scn.IsAttachedTo(cantina,mrbc));
        assertTrue(scn.IsAttachedTo(marketplace,mrbc));
    }

    @Test
    public void MRBCCannotBeCarriedIfLessThanTwoWarriorsTest() {
        //Test1: MRBC present with 1 warrior cannot be carried to an adjacent site
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var warrior1 = scn.GetLSFiller(1);
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveCardsToLocation(cantina, warrior1);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.MOVE);

        assertTrue(scn.GetLSForcePileCount() >= 3); //enough to pay cost to move

        assertTrue(scn.LSMoveAvailable(warrior1)); //landspeed to marketplace
        assertFalse(scn.LSCardActionAvailable(mrbc, "Move using two warriors"));
    }

    @Test
    public void MRBCCannotBeCarriedIfWarriorCannotMoveTest() {
        //Test1: MRBC present with 2 warriors (but only 1 can move) cannot be carried to an adjacent site

        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var warrior1 = scn.GetLSFiller(1);
        var warrior2 = scn.GetLSFiller(2);
        var cantina = scn.GetLSCard("cantina");

        var barrier = scn.GetDSCard("barrier");

        scn.MoveCardsToLSHand(warrior2);
        scn.MoveCardsToDSHand(barrier);

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveCardsToLocation(cantina, warrior1);
        scn.AttachCardsTo(cantina, mrbc);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(warrior2);
        scn.LSChooseCard(cantina);
        scn.DSPass();
        scn.LSPass();
        scn.DSPlayCard(barrier);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.GetLSForcePileCount() >= 3); //enough to pay cost to move

        assertTrue(scn.LSMoveAvailable(warrior1)); //landspeed to marketplace
        assertFalse(scn.LSMoveAvailable(warrior2)); //landspeed to marketplace
        assertFalse(scn.LSCardActionAvailable(mrbc, "Move using two warriors")); //test1
    }

    @Test
    public void MRBCCannotBeCarriedIfCannotPayCostTest() {
        //Test1: MRBC present with 2 warriors cannot be carried if not enough in Force Pile to pay for the movement
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var gunganGuard1 = scn.GetLSCard("gunganGuard1"); //move cost = 3
        var gunganGuard2 = scn.GetLSCard("gunganGuard2");
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveCardsToLocation(cantina, gunganGuard1, gunganGuard2);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.MOVE);

        scn.LSActivateForceCheat(1);
        assertEquals(6,scn.GetLSForcePileCount()); //just less than the cost 7 to move (3 + 3 + 1)

        assertTrue(scn.LSMoveAvailable(gunganGuard1)); //landspeed to marketplace
        assertTrue(scn.LSMoveAvailable(gunganGuard2)); //landspeed to marketplace
        assertFalse(scn.LSCardActionAvailable(mrbc, "Move using two warriors")); //test1
    }

    @Test
    public void MRBCWarriorThatAlreadyMovedCannotCarryTest() {
        //Test1: MRBC present with 2 warriors (but 1 has already used regular move action) cannot be carried to an adjacent site

        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var warrior1 = scn.GetLSFiller(1);
        var warrior2 = scn.GetLSFiller(2);
        var cantina = scn.GetLSCard("cantina");

        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveCardsToLocation(cantina, warrior1);
        scn.MoveCardsToLocation(marketplace,warrior2);
        scn.AttachCardsTo(cantina, mrbc);

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(warrior2,"Move");
        scn.LSChooseCard(cantina);
        scn.PassAllResponses();
        scn.DSPass();

        assertTrue(scn.GetLSForcePileCount() >= 3); //enough to pay cost to move

        assertTrue(scn.LSMoveAvailable(warrior1)); //landspeed to marketplace
        assertFalse(scn.LSMoveAvailable(warrior2)); //already moved
        assertFalse(scn.LSCardActionAvailable(mrbc, "Move using two warriors")); //test1
    }

    @Test
    public void MRBCWarriorThatCarriedCannotMoveAgainTest() {
        //carried MRBC uses the target warrior's regular move action for the turn
        //Test1: warriors that carried mrbc cannot perform another landspeed move to adjacent site
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var warrior1 = scn.GetLSFiller(1);
        var warrior2 = scn.GetLSFiller(2);
        var warrior3 = scn.GetLSFiller(3);
        var cantina = scn.GetLSCard("cantina");

        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveCardsToLocation(cantina, warrior1, warrior2);
        scn.MoveCardsToLocation(marketplace,warrior3);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.MOVE);

        assertTrue(scn.LSCardActionAvailable(mrbc, "Move using two warriors"));
        scn.LSUseCardAction(mrbc,"Move using two warriors");
        scn.LSChooseCard(marketplace);
        scn.LSChooseCard(warrior1);
        scn.LSChooseCard(warrior2);
        scn.PassAllResponses();
        scn.DSPass();

        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertEquals(2,scn.GetLSForcePileCount()); //enough to pay for regular movement
        assertTrue(scn.CardsAtLocation(marketplace, warrior1, warrior2, warrior3));
        assertFalse(scn.LSMoveAvailable(warrior1)); //test1
        assertFalse(scn.LSMoveAvailable(warrior2)); //test1
        assertTrue(scn.LSMoveAvailable(warrior3));
    }

    @Test
    public void MRBCCarryActionIsUnlimitedTest() {
        //MRBC can be carried multiple times per turn (but will need different warrior pairs each time)
        //Test1: action is available to move MRBC a second time in the same turn
        //Test2: MRBC is successfully moved a second time
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var warrior1 = scn.GetLSFiller(1);
        var warrior2 = scn.GetLSFiller(2);
        var warrior3 = scn.GetLSFiller(3);
        var warrior4 = scn.GetLSFiller(4);
        var cantina = scn.GetLSCard("cantina");

        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveCardsToLocation(cantina, warrior1, warrior2);
        scn.MoveCardsToLocation(marketplace,warrior3, warrior4);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.MOVE);

        scn.LSActivateForceCheat(1);

        scn.LSUseCardAction(mrbc,"Move using two warriors");
        scn.LSChooseCard(marketplace);
        scn.LSChooseCard(warrior1);
        scn.LSChooseCard(warrior2);
        scn.PassAllResponses();
        scn.DSPass();

        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertEquals(3,scn.GetLSForcePileCount()); //enough to pay for regular movement
        assertTrue(scn.CardsAtLocation(marketplace, warrior1, warrior2, warrior3, warrior4));
        assertTrue(scn.IsAttachedTo(marketplace,mrbc));

        scn.LSUseCardAction(mrbc,"Move using two warriors");
        scn.LSChooseCard(cantina);
        scn.LSChooseCard(warrior3);
        scn.LSChooseCard(warrior4);
        scn.PassAllResponses();
        scn.DSPass();

        assertTrue(scn.CardsAtLocation(cantina, warrior3, warrior4));
        assertTrue(scn.IsAttachedTo(cantina,mrbc));
    }

    @Test
    public void MRBCCarryIsLimitedToLowestWarriorLandspeedTest() {
        //Test1: MRBC present with 2 warriors can be carried only to sites within range of the lowest landspeed
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var warrior1 = scn.GetLSFiller(1);
        var mando1 = scn.GetLSCard("mando1");
        var cantina = scn.GetLSCard("cantina");
        var jp = scn.GetLSCard("jp");

        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveLocationToTable(jp); //now site order is jp - marketplace - cantina
        scn.MoveCardsToLocation(cantina, warrior1, mando1);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.MOVE);

        assertEquals(7,scn.GetLSForcePileCount()); //enough to pay cost 3 to move

        assertTrue(scn.LSMoveAvailable(warrior1)); //landspeed to marketplace
        assertTrue(scn.LSMoveAvailable(mando1)); //landspeed to marketplace or jp
        assertTrue(scn.LSCardActionAvailable(mrbc, "Move using two warriors"));
        scn.LSUseCardAction(mrbc,"Move using two warriors");

        assertTrue(scn.LSDecisionAvailable("Choose site"));
        assertTrue(scn.LSHasCardChoiceAvailable(marketplace));
        assertFalse(scn.LSHasCardChoiceAvailable(jp)); //test1
    }

    @Test
    public void MRBCCanBeCarriedToNonAdjacentSitesTest() {
        //Test1: MRBC present with 2 warriors with landspeed > 1 can be targeted to carry to a non-adjacent site in range
        //Test2: carry action completes
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var mando1 = scn.GetLSCard("mando1");
        var mando2 = scn.GetLSCard("mando2");
        var cantina = scn.GetLSCard("cantina");
        var jp = scn.GetLSCard("jp");

        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveLocationToTable(jp); //now site order is jp - marketplace - cantina
        scn.MoveCardsToLocation(cantina, mando1, mando2);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.MOVE);

        assertEquals(7,scn.GetLSForcePileCount()); //enough to pay cost 3 to move

        scn.LSUseCardAction(mrbc,"Move using two warriors");

        assertTrue(scn.LSDecisionAvailable("Choose site"));
        assertTrue(scn.LSHasCardChoiceAvailable(marketplace));
        assertTrue(scn.LSHasCardChoiceAvailable(jp)); //test1
        scn.LSChooseCard(jp);

        assertTrue(scn.LSDecisionAvailable("Choose first warrior"));
        scn.LSChooseCard(mando1);

        assertTrue(scn.LSDecisionAvailable("Choose second warrior"));
        scn.LSChooseCard(mando2);

        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.CardsAtLocation(jp, mando1, mando2));
        assertTrue(scn.IsAttachedTo(jp,mrbc)); //test2
    }

    @Test
    public void MRBCWarriorCarrySelectionIsLimitedByLandspeedTest() {
        //Test1: warrior with insufficient landspeed to reach the target site cannot be selected as first warrior
        //Test2: warrior with insufficient landspeed to reach the target site cannot be selected as second warrior
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var warrior1 = scn.GetLSFiller(1);
        var mando1 = scn.GetLSCard("mando1");
        var mando2 = scn.GetLSCard("mando2");
        var cantina = scn.GetLSCard("cantina");
        var jp = scn.GetLSCard("jp");

        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveLocationToTable(jp); //now site order is jp - marketplace - cantina
        scn.MoveCardsToLocation(cantina, warrior1, mando1, mando2);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.MOVE);

        assertEquals(7,scn.GetLSForcePileCount()); //enough to pay cost 3 to move

        scn.LSUseCardAction(mrbc,"Move using two warriors");

        assertTrue(scn.LSDecisionAvailable("Choose site"));
        assertTrue(scn.LSHasCardChoiceAvailable(marketplace));
        assertTrue(scn.LSHasCardChoiceAvailable(jp)); //test1
        scn.LSChooseCard(jp);

        assertTrue(scn.LSDecisionAvailable("Choose first warrior"));
        assertTrue(scn.LSHasCardChoiceAvailable(mando1));
        assertTrue(scn.LSHasCardChoiceAvailable(mando2));
        assertFalse(scn.LSHasCardChoiceAvailable(warrior1)); //test2
        scn.LSChooseCard(mando1);

        assertTrue(scn.LSDecisionAvailable("Choose second warrior"));
        assertFalse(scn.LSHasCardChoiceAvailable(mando1));
        assertTrue(scn.LSHasCardChoiceAvailable(mando2));
        assertFalse(scn.LSHasCardChoiceAvailable(warrior1)); //test3
    }

    @Test
    public void MRBCCanBeCarriedByYutaniBasicTest() {
        //Test1: MRBC present with Yutani can be carried to an adjacent site by Yutani alone
        //Test2: paid force cost of 1
        //Test3: Yutani and MRBC successfully moved
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var yutani = scn.GetLSCard("yutani");
        var cantina = scn.GetLSCard("cantina");

        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveCardsToLocation(cantina, yutani);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.MOVE);

        assertEquals(5,scn.GetLSForcePileCount()); //enough to pay cost 3 to move

        assertTrue(scn.LSMoveAvailable(yutani)); //landspeed to marketplace
        assertFalse(scn.LSCardActionAvailable(mrbc, "Move using two warriors"));
        assertTrue(scn.LSCardActionAvailable(mrbc, "Move (for free) using one warrior")); //test1
        scn.LSUseCardAction(mrbc,"Move (for free) using one warrior");

        assertTrue(scn.LSDecisionAvailable("Choose site"));
        assertTrue(scn.LSHasCardChoiceAvailable(marketplace));
        assertFalse(scn.LSHasCardChoiceAvailable(cantina));
        scn.LSChooseCard(marketplace);

        assertTrue(scn.LSDecisionAvailable("Choose warrior"));
        assertTrue(scn.LSHasCardChoiceAvailable(yutani));
        scn.LSChooseCard(yutani);

        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertEquals(4,scn.GetLSForcePileCount()); //test2
        assertTrue(scn.CardsAtLocation(marketplace, yutani));
        assertFalse(scn.IsAttachedTo(cantina,mrbc));
        assertTrue(scn.IsAttachedTo(marketplace,mrbc)); //test3
    }

    @Test
    public void MRBCCanBeCarriedByWarriorAndYutaniTest() {
        //Test1: MRBC present with Yutani can be carried to an adjacent site by Yutani and another warrior for +1 cost
        //Test2: paid force cost of 3
        //Test3: Yutani, warrior, and MRBC successfully moved
        var scn = GetScenario();

        var mrbc = scn.GetLSCard("mrbc");
        var warrior = scn.GetLSFiller(1);
        var yutani = scn.GetLSCard("yutani");
        var cantina = scn.GetLSCard("cantina");

        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina); //adjacent to marketplace (DS starting site)
        scn.MoveCardsToLocation(cantina, yutani, warrior);
        scn.AttachCardsTo(cantina, mrbc);
        scn.SkipToLSTurn(Phase.MOVE);

        assertEquals(5,scn.GetLSForcePileCount()); //enough to pay cost 3 to move

        assertTrue(scn.LSMoveAvailable(yutani)); //landspeed to marketplace
        assertTrue(scn.LSMoveAvailable(warrior)); //landspeed to marketplace
        assertTrue(scn.LSCardActionAvailable(mrbc, "Move using two warriors"));
        assertTrue(scn.LSCardActionAvailable(mrbc, "Move (for free) using one warrior")); //test1
        scn.LSUseCardAction(mrbc,"Move using two warriors");

        assertTrue(scn.LSDecisionAvailable("Choose site"));
        assertTrue(scn.LSHasCardChoiceAvailable(marketplace));
        assertFalse(scn.LSHasCardChoiceAvailable(cantina));
        scn.LSChooseCard(marketplace);

        assertTrue(scn.LSDecisionAvailable("Choose first warrior"));
        assertTrue(scn.LSHasCardChoiceAvailable(yutani));
        assertTrue(scn.LSHasCardChoiceAvailable(warrior));
        scn.LSChooseCard(yutani);

        assertTrue(scn.LSDecisionAvailable("Choose second warrior"));
        assertFalse(scn.LSHasCardChoiceAvailable(yutani));
        assertTrue(scn.LSHasCardChoiceAvailable(warrior));
        scn.LSChooseCard(warrior);

        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertEquals(2,scn.GetLSForcePileCount()); //paid 3 to move
        assertTrue(scn.CardsAtLocation(marketplace, yutani));
        assertTrue(scn.CardsAtLocation(marketplace, warrior));
        assertFalse(scn.IsAttachedTo(cantina,mrbc));
        assertTrue(scn.IsAttachedTo(marketplace,mrbc));
    }

    /// other move tests to add
    //could be clearer to use hoth marker sites for clear landspeed move checks?

    //can use DB transit
    //can only use DB transit once per turn
    //can fire, carry, etc after DB transit (checks properly re-attached to destination)

    //can only choose warriors
    //can only choose warriors within movement cost limitations for site selected
    //cannot choose undercover spy

    //warrior aboard open vehicle

    //'just moved' tests:
    //  racing swoop can follow either of 2 warriors that carried MRBC
    //  grand admiral thrawn (V)

}
