package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_222_001_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
				}},
				new HashMap<>()
				{{
                    put("collection", "222_001"); //A Fine Addition To My Collection
					put("grievous", "203_027");
                    put("vader_saber", "1_324");
                    put("mara_saber", "110_011");
                    put("dark_jedi_saber", "1_314");
                    put("vader","1_168");
                    put("blaster","1_317");
                    put("parry","11_083"); //Lightsaber Parry
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
	public void AFineAdditionToMyCollectionStatsAndKeywordsAreCorrect() {
		/**
		 * Title: A Fine Addition To My Collection
		 * Uniqueness: UNIQUE
		 * Side: DARK
		 * Type: Interrupt
		 * Subtype: Lost
		 * Destiny: 5
		 * Icons: Episode I, V Set 22
		 * Game Text: If Grievous just swung a lightsaber, add one battle destiny. OR
         *      Cancel an attempt to target Grievous with a lightsaber. OR
         *      Deploy any lightsaber from your Lost Pile on Grievous (he may use it until he is no longer carrying it).
		 * Lore:
		 * Set: 22
		 * Rarity: V
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("collection").getBlueprint();

		assertEquals("A Fine Addition To My Collection", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
		assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            //null
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.EPISODE_I);
            add(Icon.VIRTUAL_SET_22);
        }});
	}

	@Test
	public void AFineAdditionToMyCollectionAction3DeploysASaberFromLostPile() {
        //test1: collection can't be played without grievous on table
        //test2: collection can't deploy from lost outside of deploy phase
        //test3: collection can't choose non-lightsabers from lost pile
        //test4: collection can choose from multiple lightsabers in lost pile
        //test5: collection deploys selected lightsaber on grievous
        //tets6: collection deploys selected lightsaber at normal cost
		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var grievous = scn.GetDSCard("grievous");
        var collection = scn.GetDSCard("collection");
        var vader = scn.GetDSCard("vader");
        var vader_saber = scn.GetDSCard("vader_saber");
        var mara_saber = scn.GetDSCard("mara_saber");
        var dark_jedi_saber = scn.GetDSCard("dark_jedi_saber");
        var blaster = scn.GetDSCard("blaster");
        var parry = scn.GetDSCard("parry");

		scn.StartGame();

		scn.MoveCardsToLocation(site, vader);

        scn.MoveCardsToDSHand(collection,grievous);

        scn.MoveCardsToTopOfDSLostPile(vader_saber,mara_saber,dark_jedi_saber,parry);

		scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(collection)); //test1

        scn.MoveCardsToLocation(site,grievous);
        scn.SkipToPhase(Phase.MOVE);
        assertFalse(scn.DSCardPlayAvailable(collection)); //test2

        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(collection)); //test2
        scn.DSPlayCard(collection);
        scn.PassAllResponses();
        assertFalse(scn.DSHasCardChoicesAvailable(blaster,parry)); //test3
        assertTrue(scn.DSHasCardChoicesAvailable(vader_saber,mara_saber,dark_jedi_saber)); //test4
        assertTrue(scn.GetDSForcePileCount() >= 3);
        scn.DSChooseCard(dark_jedi_saber);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(grievous,dark_jedi_saber)); //test5
        assertEquals(3,scn.GetDSUsedPileCount()); //test6
        assertEquals(Zone.TOP_OF_LOST_PILE,collection.getZone());
	}

    @Test
    public void AFineAdditionToMyCollectionAction3MayUseDeployedDarkJediSaber() {
        //test1: can use deployed saber (add to a force drain)
        var scn = GetScenario();

        var site = scn.GetLSStartingLocation();

        var grievous = scn.GetDSCard("grievous");
        var collection = scn.GetDSCard("collection");
        var dark_jedi_saber = scn.GetDSCard("dark_jedi_saber");

        scn.StartGame();

        scn.MoveCardsToLocation(site, grievous);

        scn.MoveCardsToDSHand(collection);

        scn.MoveCardsToTopOfDSLostPile(dark_jedi_saber);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(collection);
        scn.PassAllResponses();
        scn.DSChooseCard(dark_jedi_saber);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(grievous,dark_jedi_saber));
        assertTrue(scn.IsCardActive(dark_jedi_saber));

        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(site,"Force drain"));
        scn.DSUseCardAction(site);

        scn.LSPass();

        assertTrue(scn.DSCardActionAvailable(dark_jedi_saber,"Add 1"));
        scn.DSUseCardAction(dark_jedi_saber);
        scn.PassAllResponses();

        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSControlPhaseActions());
    }

    //shows https://github.com/PlayersCommittee/gemp-swccg-public/issues/889
    //parallel test to above, but with persona restricted saber
    //fails because persona restricted saber deploys but is inactive
    @Test @Ignore
    public void AFineAdditionToMyCollectionAction3MayUseDeployedVadersSaber() {
        //test1:
        var scn = GetScenario();

        var site = scn.GetLSStartingLocation();

        var grievous = scn.GetDSCard("grievous");
        var collection = scn.GetDSCard("collection");
        var vader_saber = scn.GetDSCard("vader_saber");

        scn.StartGame();

        scn.MoveCardsToLocation(site, grievous);

        scn.MoveCardsToDSHand(collection);

        scn.MoveCardsToTopOfDSLostPile(vader_saber);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(collection);
        scn.PassAllResponses();
        scn.DSChooseCard(vader_saber);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(grievous, vader_saber));
        assertTrue(scn.IsCardActive(vader_saber)); /// FAILS HERE

        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(site, "Force drain"));
        scn.DSUseCardAction(site);

        scn.LSPass();

        assertTrue(scn.DSCardActionAvailable(vader_saber, "Add 1"));
        scn.DSUseCardAction(vader_saber);
        scn.PassAllResponses();

        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSControlPhaseActions());
    }

    @Test
    public void AFineAdditionToMyCollectionAction3MayNotUseSaberAfterNoLongerCarryingIt() {
        //test1: after deploying a lightsaber grievious would normally not be able to use (via Collection)
        //      and transferring away, may not transfer back to grievious
        var scn = GetScenario();

        var site = scn.GetLSStartingLocation();

        var grievous = scn.GetDSCard("grievous");
        var collection = scn.GetDSCard("collection");
        var vader_saber = scn.GetDSCard("vader_saber");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();

        scn.MoveCardsToLocation(site, grievous, vader);

        scn.MoveCardsToDSHand(collection);

        scn.MoveCardsToTopOfDSLostPile(vader_saber);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(collection);
        scn.PassAllResponses();
        scn.DSChooseCard(vader_saber);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(grievous, vader_saber));

        scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());

        assertTrue(scn.DSCardActionAvailable(vader_saber)); //transfer
        scn.DSUseCardAction(vader_saber);
        assertTrue(scn.DSHasCardChoiceAvailable(vader)); //transfer to vader
        scn.DSChooseCard(vader);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(vader, vader_saber));
        assertTrue(scn.IsCardActive(vader_saber));

        //grievious should lose the ability to use Vader's Lightsaber, now

        scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());

        assertFalse(scn.DSCardActionAvailable(vader_saber)); //no eligible target to transfer to
    }
    //add tests:
    //AFineAdditionToMyCollectionAction1AddsOneBattleDestiny
    //AFineAdditionToMyCollectionAction2CancelsLightsaberTargeting
}
