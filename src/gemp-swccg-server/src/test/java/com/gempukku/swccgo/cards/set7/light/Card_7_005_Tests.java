package com.gempukku.swccgo.cards.set7.light;

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

public class Card_7_005_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("spy", "7_005"); //bothan spy
                    put("droid", "1_024"); // r2-x2
                    put("plans", "1_046"); //death star plans
                    put("ds_site", "1_125"); // death star: trash compactor
                    put("ds_db", "1_124"); // death star: docking bay
                    put("yavin_db", "1_136"); // yavin 4: docking bay
                    put("nabrun", "1_097");
                }},
				new HashMap<>()
				{{
                    put("alter", "1_234");
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
	public void BothanSpyStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Bothan Spy
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Character
		 * Subtype: Alien
		 * Destiny: 3
		 * Deploy: 2
		 * Power: 1
		 * Ability: 2
		 * Forfeit: 3
		 * Icons: Alien, Special Edition
         * Species: Bothan
         * Keywords: Spy
		 * Game Text: May be targeted (instead of a droid) by Death Star Plans. When targeted by Death Star Plans,
         *      makes that Effect immune to Alter and adds one destiny to Force retrieved with Death Star Plans.
         *      May not be targeted by Nabrun Leids.
		 * Lore: Bothans operate the most complex spy network in the galaxy. Discovered the location of the second
         *      Death Star. Ambitious. Resourceful. Furry. Tend to die in large numbers.
		 * Set: Special Edition
		 * Rarity: C
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("spy").getBlueprint();

		assertEquals("Bothan Spy", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
        assertFalse(card.hasAlternateImageSuffix());
		assertEquals(3, card.getDestiny(), scn.epsilon);
		assertEquals(2, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertEquals(2, card.getAbility(), scn.epsilon);
		assertEquals(3, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.ALIEN);
		}});
        assertEquals(Species.BOTHAN,card.getSpecies());
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
			add(Keyword.SPY);
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.ALIEN);
			add(Icon.SPECIAL_EDITION);
		}});
		assertEquals(ExpansionSet.SPECIAL_EDITION,card.getExpansionSet());
		assertEquals(Rarity.C,card.getRarity());
	}

    @Test
    public void BothanSpyMayBeTargetedByDeathStarPlans() {
        var scn = GetScenario();

        var spy = scn.GetLSCard("spy");
        var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");

        scn.StartGame();

        scn.MoveCardsToLSHand(plans);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,spy);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(plans);
        scn.LSChooseCard(ds_site);
        scn.LSChooseCard(spy);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(ds_site,plans));
    }

    //demonstrates 'bug 1' from: https://github.com/PlayersCommittee/gemp-swccg-public/issues/770
    @Test @Ignore
	public void BothanSpyMakesDeathStarPlansTargetingSelfImmuneToAlter() {
		var scn = GetScenario();

        var spy = scn.GetLSCard("spy");
        var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");

        var trooper = scn.GetDSFiller(1);
        var alter = scn.GetDSCard("alter");

        scn.StartGame();

        scn.MoveCardsToLSHand(plans);
        scn.MoveCardsToDSHand(trooper,alter);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,spy,trooper);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(plans);
        scn.LSChooseCard(ds_site);
        scn.LSChooseCard(spy);

        assertTrue(scn.DSDecisionAvailable("Deploying")); //Deploying Death Star Plans - Optional responses

        assertFalse(scn.DSCardPlayAvailable(alter)); /// FAILS HERE - have not met the condition of being targeted yet?
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(ds_site,plans));
        assertFalse(scn.DSCardPlayAvailable(alter));
    }

    @Test
    public void BothanSpyDoesNotMakeDeathStarPlansTargetingNonSelfImmuneToAlter() {
        var scn = GetScenario();

        var spy = scn.GetLSCard("spy");
        var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");
        var droid = scn.GetLSCard("droid");

        var trooper = scn.GetDSFiller(1);
        var alter = scn.GetDSCard("alter");

        scn.StartGame();

        scn.MoveCardsToLSHand(plans);
        scn.MoveCardsToDSHand(trooper,alter);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,droid,spy,trooper);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(plans);
        scn.LSChooseCard(ds_site);
        scn.LSChooseCard(droid);

        assertTrue(scn.DSDecisionAvailable("Deploying")); //Deploying •Death Star Plans - Optional responses
        assertTrue(scn.DSCardPlayAvailable(alter));
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(ds_site,plans));
        assertTrue(scn.DSCardPlayAvailable(alter));
    }

    @Test
    public void BothanSpyMayNotBeTargetedByNabrun() {
        var scn = GetScenario();

        var spy = scn.GetLSCard("spy");
        var ds_site = scn.GetLSCard("ds_site");
        var yavin_db = scn.GetLSCard("yavin_db");
        var nabrun = scn.GetLSCard("nabrun");

        scn.StartGame();

        scn.MoveCardsToLSHand(nabrun);

        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,spy);

        scn.SkipToLSTurn(Phase.CONTROL);

            /// should not allow attempting to play Nabrun here?
//        assertFalse(scn.LSCardPlayAvailable(nabrun));

        scn.LSPlayCard(nabrun);
        scn.LSChooseCard(yavin_db);
        scn.LSChooseCard(ds_site);

        assertTrue(scn.AwaitingLSControlPhaseActions()); //attempt to start playing Nabrun fails here because no eligible target
    }

    @Test
    public void BothanSpyTargetedByDeathStarPlansRetrievesTotalFrom4DestinyDraws() {
        //very similar to DeathStarPlansRetrievesTotalFrom3DestinyDraws()

        var scn = GetScenario();

        var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");
        var spy = scn.GetLSCard("spy");

        scn.StartGame();

        scn.MoveCardsToLSHand(plans);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,spy);

        scn.SkipToLSTurn(Phase.DEPLOY); //get plans deployed and target spy
        scn.LSPlayCard(plans);
        scn.LSChooseCard(ds_site);
        scn.LSChooseCard(spy);
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.MOVE); //spy yavin db -> ds db
        scn.LSUseCardAction(yavin_db);
        scn.LSChooseCard(ds_db);
        scn.LSChooseCard(spy);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //spy ds db -> ds site
        scn.LSUseCardAction(spy);
        scn.LSChooseCard(ds_site);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //spy ds site -> ds db
        scn.LSUseCardAction(spy);
        scn.LSChooseCard(ds_db);
        scn.PassAllResponses();
        scn.SkipToDSTurn();

        scn.SkipToLSTurn(Phase.MOVE); //spy ds db -> yavin db
        assertTrue(scn.GetLSForcePileCount() >= 8);
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSForcePile());
        assertEquals(7,scn.GetLSLostPileCount());
        scn.PrepareLSDestiny(3);
        scn.PrepareLSDestiny(2);
        scn.PrepareLSDestiny(1);
        scn.PrepareLSDestiny(0);
        assertEquals(5,scn.GetLSReserveDeckCount());

        scn.LSUseCardAction(ds_db);
        scn.LSChooseCard(yavin_db);
        scn.LSChooseCard(spy);
        scn.PassAllResponses();
        scn.PassAllResponses(); //RETRIEVED_FORCE - Optional responses, etc

        assertEquals(1,scn.GetLSReserveDeckCount()); //drew 4 destiny (destiny 0, 1, 2, and then 3)
        assertEquals(2,scn.GetLSLostPileCount()); //2 = 7 - 6 retrieved + 1 plans
        assertTrue(plans.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void BothanSpyNotTargetedByDeathStarPlansDoesNotAddRetrievalDestiny() {
        //very similar to DeathStarPlansRetrievesTotalFrom3DestinyDraws()

        var scn = GetScenario();

        var plans = scn.GetLSCard("plans");
        var ds_site = scn.GetLSCard("ds_site");
        var ds_db = scn.GetLSCard("ds_db");
        var yavin_db = scn.GetLSCard("yavin_db");
        var spy = scn.GetLSCard("spy");
        var droid = scn.GetLSCard("droid");

        scn.StartGame();

        scn.MoveCardsToLSHand(plans);

        scn.MoveLocationToTable(ds_db);
        scn.MoveLocationToTable(ds_site);
        scn.MoveLocationToTable(yavin_db);

        scn.MoveCardsToLocation(yavin_db,spy,droid);

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
        scn.PrepareLSDestiny(3);
        scn.PrepareLSDestiny(2);
        scn.PrepareLSDestiny(1);
        scn.PrepareLSDestiny(0);
        assertEquals(5,scn.GetLSReserveDeckCount());

        scn.LSUseCardAction(ds_db);
        scn.LSChooseCard(yavin_db);
        scn.LSChooseCard(droid);
        scn.PassAllResponses();
        scn.PassAllResponses(); //RETRIEVED_FORCE - Optional responses, etc

        assertEquals(2,scn.GetLSReserveDeckCount()); //drew 3 destiny (destiny 0, 1, and then 2)
        assertEquals(5,scn.GetLSLostPileCount()); //5 = 7 - 3 retrieved + 1 plans
        assertTrue(plans.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
