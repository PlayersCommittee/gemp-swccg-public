package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

import static com.gempukku.swccgo.framework.TestBase.DS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_322_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("miningDroidLS","1_018"); //LIN-V8K
                    put("boushh","110_001");
                }},
				new HashMap<>()
				{{
                    put("timer","1_322");
                    put("timer2","1_322");
                    put("miningDroid","1_186"); //LIN-V8M
                    put("miningDroid2","1_186");
                    put("comlink","1_201");
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
	public void TimerMineStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Timer Mine
		 * Uniqueness: Unrestricted
		 * Side: Dark
		 * Type: Weapon
         * Subtype: Automated
		 * Destiny: 2
		 * Icons: Weapon
		 * Game Text: Deploy on opponent's side at same site as one of your mining droids. 'Explodes' at beginning
         *      of your next turn. Draw destiny. That number of opponent's characters there are immediately lost
         *      (owner's choice). Timer Mine is also lost.
		 * Lore: A timer-activated explosive device designed to be placed by a mining droid. Originally used in ore
         *      and spice mines for demolition. Altered for military use.
		 * Set: Premiere
		 * Rarity: C2
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("timer").getBlueprint();

		assertEquals("Timer Mine", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertEquals(2, card.getDestiny(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.WEAPON);
		}});
        assertEquals(CardSubtype.AUTOMATED, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.MINE);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.WEAPON);
		}});
		assertEquals(ExpansionSet.PREMIERE,card.getExpansionSet());
		assertEquals(Rarity.C2,card.getRarity());
	}

	@Test
	public void TimerMineCanDeployAtSameSiteAsMiningDroidTest() {
        //test1: timer mine cannot be played during your non-deploy phase
        //test2: timer mine can be played during your deploy phase
        //test3: timer mine cannot be deployed to site without your mining droid
        //test4: timer mine can be deployed to site with your mining droid
        //test5: timer mine has no deploy cost
        //test6: timer mine is at the site after deploying
		var scn = GetScenario();

        var timer = scn.GetDSCard("timer");
        var miningDroid = scn.GetDSCard("miningDroid");

        var miningDroidLS = scn.GetLSCard("miningDroidLS");

        var site1 = scn.GetDSStartingLocation();
        var site2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site1, miningDroid);
        scn.MoveCardsToLocation(site2, miningDroidLS);

        scn.MoveCardsToDSHand(timer);

        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(timer)); //test1

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(timer)); //test2
        scn.DSDeployCard(timer);
        assertFalse(scn.DSHasCardChoiceAvailable(site2)); //test3
        assertTrue(scn.DSHasCardChoiceAvailable(site1)); //test4
        scn.DSChooseCard(site1);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertEquals(0, scn.GetDSUsedPileCount()); //test5
        assertTrue(scn.CardsAtLocation(site1, timer, miningDroid)); //test6
        //add assert that mine is on opponent's side of table?
    }

    @Test
    public void TimerMineExplodesAtBeginningOfYourNextTurnTest() {
        //test1: timer mine deployed during your previous turn explodes during your next turn
        //test2: timer mine exploding results in 1 destiny draw
        //test3: timer mine goes to lost pile after exploding
        //test4: timer mine deployed during opponent's turn explodes during your next turn
        var scn = GetScenario();

        var timer = scn.GetDSCard("timer");
        var timer2 = scn.GetDSCard("timer2");
        var miningDroid = scn.GetDSCard("miningDroid");
        var comlink = scn.GetDSCard("comlink");

        var trooper = scn.GetLSFiller(1);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, miningDroid);

        scn.MoveCardsToDSHand(timer, timer2, comlink);
        scn.MoveCardsToLSHand(trooper);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(timer);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        scn.SkipToLSTurn();
        scn.SkipToDSTurn();
        //only one automatic action to take at start of turn, so it is carried out automatically
        //timer mine required response initiated
        //targets to make explode
        assertTrue(scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD")); //test1
        scn.PassAllResponses();
        assertEquals(1, scn.GetDSUsedPileCount());
        assertEquals(Zone.TOP_OF_LOST_PILE, timer.getZone()); //test3

        scn.MoveCardsToLocation(site, trooper);
        scn.AttachCardsTo(miningDroid, comlink);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSForceDrainAt(site);
        assertTrue(scn.DSCardActionAvailable(comlink));
        scn.DSUseCardAction(comlink);
        //timer automatically selected, since only option available
        scn.PassAllResponses();

        scn.DSPayForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(site, timer2));

        scn.SkipToDSTurn();
        //only one automatic action to take at start of turn, so it is carried out automatically
        //timer mine required response initiated
        //targets to make explode
        assertTrue(scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD")); //test4
    }

    @Test
    public void TimerMineCausesOpponentsCharactersToBeLostTest() {
        //test1: characters lost due to timer mine explode are chosen by opponent
        //test2: number of characters selected equals destiny draw (for draw < number of characters)
        //test3: characters selected are sent to lost pile
        //test4: characters not selected are not sent to lost pile
        var scn = GetScenario();

        var timer = scn.GetDSCard("timer");
        var miningDroid = scn.GetDSCard("miningDroid");

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);
        var trooper3 = scn.GetLSFiller(3);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(timer);

        scn.MoveCardsToLocation(site, miningDroid, trooper1, trooper2, trooper3);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(timer);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        scn.SkipToLSTurn();
        scn.PrepareDSDestiny(2);
        scn.SkipToDSTurn();
        //explode happens before activation, so destiny 2 is still prepared
        assertTrue(scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD"));
        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("Choose cards to be lost")); //test1
        assertEquals(2,scn.LSGetChoiceMax()); //test2
        assertEquals(2,scn.LSGetChoiceMin()); //test2
        assertTrue(scn.LSHasCardChoicesAvailable(trooper1, trooper2, trooper3));
        scn.LSChooseCards(trooper2, trooper3);
        scn.PassAllResponses(); //null - Optional responses

        assertTrue(scn.LSDecisionAvailable("Choose card to be lost"));
        scn.LSChooseCard(trooper3);
        scn.PassAllResponses();

        assertEquals(2,scn.GetLSLostPileCount()); //test3
        assertTrue(scn.CardsAtLocation(site, trooper1, miningDroid)); //test4
    }

    @Test
    public void TimerMineCanTargetUndercoverTest() {
        //test1: characters selected to be lost include undercover spies
        var scn = GetScenario();

        var timer = scn.GetDSCard("timer");
        var miningDroid = scn.GetDSCard("miningDroid");

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);
        var boushh = scn.GetLSCard("boushh");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(timer);
        scn.MoveCardsToLSHand(boushh);

        scn.MoveCardsToLocation(site, miningDroid, trooper1, trooper2);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(timer);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        scn.SkipToLSTurn();
        scn.LSActivateForceCheat(3);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(boushh);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertTrue(boushh.isUndercover());
        assertTrue(scn.CardsAtLocation(site, trooper1, trooper2, boushh, miningDroid, timer));

        scn.PrepareDSDestiny(2);
        scn.SkipToDSTurn();
        //explode happens before activation, so destiny 2 is still prepared
        assertTrue(scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD"));
        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("Choose cards to be lost"));
        assertEquals(2,scn.LSGetChoiceMax());
        assertEquals(2,scn.LSGetChoiceMin());
        assertTrue(scn.LSHasCardChoiceAvailable(boushh)); //test1
        scn.LSChooseCards(trooper2, boushh);
        scn.PassAllResponses(); //null - Optional responses

        assertTrue(scn.LSDecisionAvailable("Choose card to be lost"));
        scn.LSChooseCard(boushh);
        scn.PassAllResponses();

        assertEquals(2,scn.GetLSLostPileCount()); //test3
        assertTrue(scn.CardsAtLocation(site, trooper1, miningDroid)); //test4
    }

    @Test
    public void TimerMineCannotTargetCaptiveTest() {
        //test1: characters selected to be lost do not include captives
        var scn = GetScenario();

        var timer = scn.GetDSCard("timer");
        var miningDroid = scn.GetDSCard("miningDroid");
        var stormtrooper = scn.GetDSFiller(1);

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);
        var trooper3 = scn.GetLSFiller(3);
        var trooper4 = scn.GetLSFiller(4);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(timer);

        scn.MoveCardsToLocation(site, miningDroid, stormtrooper, trooper1, trooper2, trooper3, trooper4);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(timer);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        scn.CaptureCardWith(stormtrooper, trooper4);

        scn.PrepareDSDestiny(2);
        scn.SkipToDSTurn();
        //explode happens before activation, so destiny 2 is still prepared
        assertTrue(scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD"));
        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("Choose cards to be lost"));
        assertEquals(2,scn.LSGetChoiceMax());
        assertEquals(2,scn.LSGetChoiceMin());
        assertTrue(scn.LSHasCardChoicesAvailable(trooper1, trooper2, trooper3));
        assertFalse(scn.LSHasCardChoiceAvailable(trooper4)); //test1
        scn.LSChooseCards(trooper2, trooper3);
        scn.PassAllResponses(); //null - Optional responses

        assertTrue(scn.LSDecisionAvailable("Choose card to be lost"));
        scn.LSChooseCard(trooper3);
        scn.PassAllResponses();

        assertEquals(2,scn.GetLSLostPileCount());
        assertTrue(scn.CardsAtLocation(site, trooper1, miningDroid, stormtrooper));
    }

    @Test
    public void TimerMineCausesTargetsAllOpponentsCharactersIfDestinyGreaterThanTotal() {
        //test1: all characters lost (choose order) when explode destiny draw >= characters present
        var scn = GetScenario();

        var timer = scn.GetDSCard("timer");
        var miningDroid = scn.GetDSCard("miningDroid");
        var stormtrooper = scn.GetDSFiller(1);

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);
        var trooper3 = scn.GetLSFiller(3);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(timer);

        scn.MoveCardsToLocation(site, miningDroid, stormtrooper, trooper1, trooper2, trooper3);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(timer);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        //scn.CaptureCardWith(stormtrooper, trooper3);

        scn.PrepareDSDestiny(7);
        scn.SkipToDSTurn();
        //explode happens before activation, so destiny 7 is still prepared
        assertTrue(scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD"));
        scn.PassAllResponses();
        //choose order
        assertTrue(scn.LSDecisionAvailable("Choose card to be lost")); //test1
    }

    @Test
    public void TimerMineTargetsAllOpponentsCharactersLostIfDestinyGreaterThanTotalWithCaptive() {
        //demonstrates https://github.com/PlayersCommittee/gemp-swccg-public/issues/817 fixed
        //test1: all characters lost (choose order) when explode destiny draw >= characters present and a captive is present
        var scn = GetScenario();

        var timer = scn.GetDSCard("timer");
        var miningDroid = scn.GetDSCard("miningDroid");
        var stormtrooper = scn.GetDSFiller(1);

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);
        var trooper3 = scn.GetLSFiller(3);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(timer);

        scn.MoveCardsToLocation(site, miningDroid, stormtrooper, trooper1, trooper2, trooper3);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(timer);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        scn.CaptureCardWith(stormtrooper, trooper3);

        scn.PrepareDSDestiny(7);
        scn.SkipToDSTurn();
        //explode happens before activation, so destiny 7 is still prepared
        assertTrue(scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD"));
        scn.PassAllResponses();
        //choose order
        assertTrue(scn.LSDecisionAvailable("Choose card to be lost"));
        assertTrue(scn.LSHasCardChoicesAvailable(trooper1, trooper2));
        assertFalse(scn.LSHasCardChoiceAvailable(trooper3));
        scn.LSChooseCard(trooper1);
        scn.PassAllResponses();

        assertEquals(2,scn.GetLSLostPileCount());
        assertTrue(scn.CardsAtLocation(site, miningDroid, stormtrooper));
    }

    @Test
    public void TimerMineMultipleExplodeCauseOpponentsCharactersToBeLostTest() {
        //test1: 2 timer mines exploding cause cards at both locations to be lost
        var scn = GetScenario();

        var timer = scn.GetDSCard("timer");
        var miningDroid = scn.GetDSCard("miningDroid");
        var timer2 = scn.GetDSCard("timer2");
        var miningDroid2 = scn.GetDSCard("miningDroid2");

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);

        var site = scn.GetDSStartingLocation();
        var site2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(timer, timer2);

        scn.MoveCardsToLocation(site, miningDroid, trooper1);
        scn.MoveCardsToLocation(site2, miningDroid2, trooper2);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(timer);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        scn.LSPass();

        scn.DSDeployCard(timer2);
        scn.DSChooseCard(site2);
        scn.PassAllResponses();

        scn.SkipToLSTurn();
        scn.PrepareDSDestiny(2);
        scn.PrepareDSDestiny(3);
        scn.SkipToDSTurn();

        scn.DSDecisionAvailable("Start of turn - Required responses"); //two choices available
        assertTrue(scn.DSHasCardChoiceAvailable(timer));
        assertTrue(scn.DSHasCardChoiceAvailable(timer2));

            //issues with DSChooseCard, so manually force the choice below
            //pseudo random which card is mapped to decision index "0", so we don't know which timer mine is being selected!
        scn.PlayerDecided(DS,"0");

        assertTrue(scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD"));
        scn.PassAllResponses();

        assertEquals(2,scn.GetDSLostPileCount()); //both timer mines exploded
        assertEquals(2,scn.GetLSLostPileCount()); //test1: both characters at timer mine sites
    }

    /// other tests to add
    //no destiny in reserve deck to draw
}
