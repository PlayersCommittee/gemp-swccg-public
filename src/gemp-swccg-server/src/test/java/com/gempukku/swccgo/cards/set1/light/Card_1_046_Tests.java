package com.gempukku.swccgo.cards.set1.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_046_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("plans", "1_046"); //death star plans
					put("droid", "1_024"); // r2-x2
                    put("droid2", "1_024"); // r2-x2
					put("ds_site", "1_125"); // death star: trash compactor
                    put("ds_db", "1_124"); // death star: docking bay
                    put("yavin_db", "1_136"); // yavin 4: docking bay
                    put("ronto", "7_155"); // ronto (creature vehicle)
				}},
				new HashMap<>()
				{{
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
	public void DeathStarPlansStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Death Star Plans
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Effect
		 * Subtype: Utinni
		 * Destiny: 6
		 * Icons: Effect
		 * Game Text: Deploy on any Death Star site(except docking bay). Target one of your droids(not on Death Star).
         *      When target reaches Utinni Effect, 'steal' plans. If target then moves to any Yavin 4 site,
         *      draw 3 destiny. Retrieve that much lost Force. Lose Utinni Effect.
		 * Lore: 'What's so important? What's he carrying?' 'The technical readouts of that battle station.
         *      I only hope that when the data is analyzed, a weakness can be found.'
		 * Set: Premiere
		 * Rarity: R1
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("plans").getBlueprint();

		assertEquals("Death Star Plans", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
		assertEquals(CardSubtype.UTINNI, card.getCardSubtype());
		assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.UTINNI_EFFECT_THAT_RETRIEVES_FORCE);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.PREMIERE,card.getExpansionSet());
        assertEquals(Rarity.R1,card.getRarity());

	}

	@Test
	public void DeathStarPlansDeploysOnDeathStarNonDockingBay() {
		var scn = GetScenario();

		var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");
        var droid = scn.GetLSCard("droid");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(plans);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,droid);

		scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(plans));

        scn.LSPlayCard(plans);
        assertTrue(scn.LSHasCardChoiceAvailable(ds_site));
        assertFalse(scn.LSHasCardChoiceAvailable(ds_db));
        assertFalse(scn.LSHasCardChoiceAvailable(yavin_db));
        assertFalse(scn.LSHasCardChoiceAvailable(site));
	}

    @Test
    public void DeathStarPlansTargetsDroidNotOnDeathStar() {
        var scn = GetScenario();

        var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");
        var droid = scn.GetLSCard("droid");
        var droid2 = scn.GetLSCard("droid2");
        var trooper = scn.GetLSFiller(1); //(non-droid)

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(plans);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(site,droid,trooper);
        scn.MoveCardsToLocation(ds_db,droid2);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(plans);
        scn.LSChooseCard(ds_site);
        assertTrue(scn.LSHasCardChoiceAvailable(droid));
        assertFalse(scn.LSHasCardChoiceAvailable(droid2)); //because on death star
        assertFalse(scn.LSHasCardChoiceAvailable(trooper)); //because not droid
    }

    @Test
    public void DeathStarPlansTargetStealsPlans() {
        var scn = GetScenario();

        var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");
        var droid = scn.GetLSCard("droid");

        scn.StartGame();

        scn.MoveCardsToLSHand(plans);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,droid);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(plans);
        scn.LSChooseCard(ds_site);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(ds_site,plans));

        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.LSCardActionAvailable(yavin_db));
        scn.LSUseCardAction(yavin_db);
        assertTrue(scn.LSHasCardChoiceAvailable(ds_db));
        scn.LSChooseCard(ds_db);
        assertTrue(scn.LSHasCardChoicesAvailable(droid));
        scn.LSChooseCard(droid);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(ds_db,droid));

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.MOVE);

        scn.LSUseCardAction(droid);
        scn.LSChooseCard(ds_site);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(droid,plans)); //plans stolen
    }

    @Test
    public void DeathStarPlansRetrievesTotalFrom3DestinyDraws() {
        var scn = GetScenario();

        var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");
        var droid = scn.GetLSCard("droid");

        scn.StartGame();

        scn.MoveCardsToLSHand(plans);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,droid);

        scn.SkipToLSTurn(Phase.DEPLOY); //get plans deployed and target droid
        scn.LSPlayCard(plans);
        scn.LSChooseCard(ds_site);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.MOVE); //droid yavin db -> ds db
        scn.LSUseCardAction(yavin_db);
        scn.LSChooseCard(ds_db);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //droid ds db -> ds site
        scn.LSUseCardAction(droid);
        scn.LSChooseCard(ds_site);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //droid ds site -> ds db
        scn.LSUseCardAction(droid);
        scn.LSChooseCard(ds_db);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //droid ds db -> yavin db
        assertTrue(scn.GetLSForcePileCount() >= 8);
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        assertEquals(7,scn.GetLSLostPileCount());
        scn.PrepareLSDestiny(1);
        scn.PrepareLSDestiny(2);
        scn.PrepareLSDestiny(3);
        assertEquals(4,scn.GetLSReserveDeckCount());

        scn.LSUseCardAction(ds_db);
        scn.LSChooseCard(yavin_db);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();
        scn.PassAllResponses(); //RETRIEVED_FORCE - Optional responses, etc

        assertEquals(1,scn.GetLSReserveDeckCount()); //drew 3 destiny
        assertEquals(2,scn.GetLSLostPileCount()); //2 = 7 - 6 retrieved + 1 plans
        assertTrue(plans.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void DeathStarPlansRetrievesWithLessThan3InReserve() {
        var scn = GetScenario();

        var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");
        var droid = scn.GetLSCard("droid");

        scn.StartGame();

        scn.MoveCardsToLSHand(plans);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,droid);

        scn.SkipToLSTurn(Phase.DEPLOY); //get plans deployed and target droid
        scn.LSPlayCard(plans);
        scn.LSChooseCard(ds_site);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.MOVE); //droid yavin db -> ds db
        scn.LSUseCardAction(yavin_db);
        scn.LSChooseCard(ds_db);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //droid ds db -> ds site
        scn.LSUseCardAction(droid);
        scn.LSChooseCard(ds_site);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //droid ds site -> ds db
        scn.LSUseCardAction(droid);
        scn.LSChooseCard(ds_db);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //droid ds db -> yavin db
        assertEquals(1,scn.GetLSReserveDeckCount());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
        assertTrue(scn.GetLSForcePileCount() >= 4);
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        assertEquals(4,scn.GetLSLostPileCount());
        scn.PrepareLSDestiny(1);
        scn.PrepareLSDestiny(2);
        assertEquals(2,scn.GetLSReserveDeckCount());

        scn.LSUseCardAction(ds_db);
        scn.LSChooseCard(yavin_db);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();
        scn.PassAllResponses(); //RETRIEVED_FORCE - Optional responses, etc

        assertEquals(0,scn.GetLSReserveDeckCount()); //drew 2 destiny
        assertEquals(2,scn.GetLSLostPileCount()); //2 = 4 - 3 retrieved + 1 plans
        assertTrue(plans.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    //demonstrates 'bug 2' fixed from: https://github.com/PlayersCommittee/gemp-swccg-public/issues/770
    @Test
    public void DeathStarPlansRetrievesWith0InReserve() {
        var scn = GetScenario();

        var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");
        var droid = scn.GetLSCard("droid");

        scn.StartGame();

        scn.MoveCardsToLSHand(plans);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,droid);

        scn.SkipToLSTurn(Phase.DEPLOY); //get plans deployed and target droid
        scn.LSPlayCard(plans);
        scn.LSChooseCard(ds_site);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.MOVE); //droid yavin db -> ds db
        scn.LSUseCardAction(yavin_db);
        scn.LSChooseCard(ds_db);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //droid ds db -> ds site
        scn.LSUseCardAction(droid);
        scn.LSChooseCard(ds_site);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //droid ds site -> ds db
        scn.LSUseCardAction(droid);
        scn.LSChooseCard(ds_db);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //droid ds db -> yavin db
        assertEquals(1,scn.GetLSReserveDeckCount());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
        assertTrue(scn.GetLSForcePileCount() >= 2);
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        assertEquals(2,scn.GetLSLostPileCount());
        assertEquals(0,scn.GetLSReserveDeckCount());

        scn.LSUseCardAction(ds_db);
        scn.LSChooseCard(yavin_db);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();
        scn.PassAllResponses(); //RETRIEVED_FORCE - Optional responses, etc

        assertEquals(0,scn.GetLSReserveDeckCount()); //drew 0 destiny
        assertEquals(3,scn.GetLSLostPileCount()); //3 = 2 - 0 retrieved + 1 plans
        assertTrue(plans.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void DeathStarPlansDoesNotRetrieveWhenTargetCarriedToYavin() {
        //get target droid to death star site to steal the plans, then move Ronto (with droid as a passenger)
        //to Yavin 4 and disembark - confirm no retrieval triggers because:
        // - ronto is not the target, so ronto's move to yavin does not count
        // - droid's disembark unlimited move action did not cause target to move to yavin (already there)
        var scn = GetScenario();

        var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");
        var droid = scn.GetLSCard("droid");
        var ronto = scn.GetLSCard("ronto");

        scn.StartGame();

        scn.MoveCardsToLSHand(plans);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,droid,ronto);

        scn.SkipToLSTurn(Phase.DEPLOY); //get plans deployed and target droid
        scn.LSPlayCard(plans);
        scn.LSChooseCard(ds_site);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(droid,"Embark");
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSUseCardAction(yavin_db);
        scn.LSChooseCard(ds_db);
        scn.LSChooseCard(ronto);
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSUseCardAction(droid,"Disembark");
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSUseCardAction(droid,"Move");
        scn.LSChooseCard(ds_site);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(droid,plans)); //plans stolen

        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //droid ds site -> ds db
        scn.LSUseCardAction(droid,"Move");
        scn.LSChooseCard(ds_db);
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSUseCardAction(droid,"Embark");
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSUseCardAction(ds_db);
        scn.LSChooseCard(yavin_db);
        scn.LSChooseCard(ronto); //ronto carrying target to yavin
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSUseCardAction(droid,"Disembark");
        scn.PassAllResponses();
        scn.DSPass();

        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertTrue(scn.IsAttachedTo(droid,plans)); //did not trigger yet
    }

    //additional tests:
    //verify cannot play without eligible location and target
}
