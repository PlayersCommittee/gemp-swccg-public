package com.gempukku.swccgo.cards.set7.light;

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
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class Card_7_127_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("spaceport_street", "7_127");
                    put("spaceport_db", "7_126");
                    put("spaceport_city", "7_125");
                    put("tat","1_127");
                    put("kessel","1_126");
                    put("lars_farm","1_132");
                    put("boushh","110_001");
				}},
				new HashMap<>()
				{{
                    put("spaceport_prefect", "7_290");
				}},
				40,
				40,
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
    public void SpaceportStreetStatsAndKeywordsAreCorrect() {
        /**
         * Title: Spaceport Street
         * Uniqueness: Diamond
         * Side: Light
         * Type: Location
         * Subtype: Site
         * Destiny: 0
         * Icons: Special Edition, Planet, Exterior
         * Game Text: Light: During your move phase, may move free between here and any related _spaceport site. May not be deployed to Bespin, Dagobah, Endor, Hoth, Kashyyyk or Yavin 4.
         *          Dark: Imperials are power -1 here. May not be deployed to Bespin, Dagobah, Endor, Hoth, Kashyyyk or Yavin 4.
         * Light Force Icons: 1
         * Dark Force Icons: 1
         * Set: Special Edition
         * Rarity: F
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("spaceport_street").getBlueprint();

        assertEquals("Spaceport Street", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.SPECIAL_EDITION);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPACEPORT_SITE);
        }});
        assertEquals(ExpansionSet.SPECIAL_EDITION,card.getExpansionSet());
        assertEquals(Rarity.F, card.getRarity());
    }

	@Test
	public void SpaceportStreetLSCanRelocateToRelatedSpaceportSiteTest() {
        //test1: Can use LS text to target to relocate to related spaceport site
        //test2: Cannot use LS text to target to relocate to same site
        //test3: Cannot use LS text to target to relocate to unrelated spaceport site
        //test4: Cannot use LS text to target to relocate to related non-spaceport site
        //test5: Can successfully relocate to a related spaceport site
        //test6: Relocation is free
        var scn = GetScenario();

		var rebeltrooper = scn.GetLSFiller(1);
        var tat = scn.GetLSCard("tat");
        var kessel = scn.GetLSCard("kessel");
        var spaceport_street = scn.GetLSCard("spaceport_street");
        var spaceport_db = scn.GetLSCard("spaceport_db");
        var spaceport_city = scn.GetLSCard("spaceport_city");
        var lars_farm = scn.GetLSCard("lars_farm");

        var spaceport_prefect = scn.GetDSCard("spaceport_prefect");

        scn.StartGame();

        scn.MoveLocationToTable(tat);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(lars_farm);

        scn.MoveCardsToDSHand(spaceport_prefect);
        scn.MoveCardsToLSHand(rebeltrooper, spaceport_db, spaceport_street, spaceport_city);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(spaceport_prefect);
        scn.DSChooseCard(tat);
        scn.DSChooseCard(lars_farm);
        scn.DSChoose("Left");
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(spaceport_street);
        scn.LSChooseCard(tat);
        scn.LSChoose("Left");
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSPlayCard(spaceport_db);
        scn.LSChooseCard(tat);
        scn.LSChooseCard(spaceport_street);
        scn.LSChoose("Left");
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSPlayCard(spaceport_city);
        scn.LSChooseCard(kessel);
        scn.PassAllResponses();
        scn.DSPass();

        scn.MoveCardsToLocation(spaceport_street, rebeltrooper);
        scn.SkipToPhase(Phase.MOVE);

        assertEquals(0,scn.GetLSUsedPileCount());

        assertTrue(scn.LSCardActionAvailable(spaceport_street,"from here to related spaceport site"));
        scn.LSUseCardAction(spaceport_street,"from here to related spaceport site");
        assertTrue(scn.LSDecisionAvailable("move from"));
        scn.LSChooseCard(spaceport_street);
        assertTrue(scn.LSDecisionAvailable("move to"));
        assertTrue(scn.LSHasCardChoiceAvailable(spaceport_db)); //test1: spaceport and related
        assertTrue(scn.LSHasCardChoiceAvailable(spaceport_prefect)); //test1: spaceport and related
        assertFalse(scn.LSHasCardChoiceAvailable(spaceport_street)); //test2: same site
        assertFalse(scn.LSHasCardChoiceAvailable(spaceport_city)); //test3: spaceport, but not related
        assertFalse(scn.LSHasCardChoiceAvailable(lars_farm)); //test4: related, but not spaceport

        scn.LSChooseCard(spaceport_prefect);
        assertTrue(scn.LSDecisionAvailable("card to move"));
        scn.LSChooseCard(rebeltrooper);

        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(spaceport_prefect,rebeltrooper)); //test5: successfully relocated
        assertEquals(0,scn.GetLSUsedPileCount()); //test6: movement was free
    }

    @Test
    public void SpaceportStreetLSCanRelocateFromRelatedSpaceportSiteTest() {
        //test1: Can use LS text to target to relocate from related spaceport site
        //test2: Cannot use LS text to target from relocate to same site
        //test3: Cannot use LS text to target from unrelated spaceport site
        //test4: Cannot use LS text to target from related non-spaceport site
        //test5: Can successfully relocate to spaceport street
        //test6: Relocation is free
        var scn = GetScenario();

        var rebeltrooper1 = scn.GetLSFiller(1);
        var rebeltrooper2 = scn.GetLSFiller(2);
        var rebeltrooper3 = scn.GetLSFiller(3);
        var rebeltrooper4 = scn.GetLSFiller(4);
        var rebeltrooper5 = scn.GetLSFiller(5);
        var tat = scn.GetLSCard("tat");
        var kessel = scn.GetLSCard("kessel");
        var spaceport_street = scn.GetLSCard("spaceport_street");
        var spaceport_db = scn.GetLSCard("spaceport_db");
        var spaceport_city = scn.GetLSCard("spaceport_city");
        var lars_farm = scn.GetLSCard("lars_farm");

        var spaceport_prefect = scn.GetDSCard("spaceport_prefect");

        scn.StartGame();

        scn.MoveLocationToTable(tat);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(lars_farm);
        scn.MoveCardsToLocation(lars_farm,rebeltrooper1);

        scn.MoveCardsToDSHand(spaceport_prefect);
        scn.MoveCardsToLSHand(rebeltrooper2, rebeltrooper3, rebeltrooper4, rebeltrooper5, spaceport_db, spaceport_street, spaceport_city);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(spaceport_prefect);
        scn.DSChooseCard(tat);
        scn.DSChooseCard(lars_farm);
        scn.DSChoose("Left");
        scn.PassAllResponses();
        scn.MoveCardsToLocation(spaceport_prefect,rebeltrooper2);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(spaceport_street);
        scn.LSChooseCard(tat);
        scn.LSChoose("Left");
        scn.PassAllResponses();
        scn.DSPass();
        scn.MoveCardsToLocation(spaceport_street,rebeltrooper3);

        scn.LSPlayCard(spaceport_db);
        scn.LSChooseCard(tat);
        scn.LSChooseCard(spaceport_street);
        scn.LSChoose("Left");
        scn.PassAllResponses();
        scn.DSPass();
        scn.MoveCardsToLocation(spaceport_db,rebeltrooper4);

        scn.LSPlayCard(spaceport_city);
        scn.LSChooseCard(kessel);
        scn.PassAllResponses();
        scn.DSPass();
        scn.MoveCardsToLocation(spaceport_city,rebeltrooper5);

        scn.SkipToPhase(Phase.MOVE);

        assertEquals(0,scn.GetLSUsedPileCount());

        assertTrue(scn.LSCardActionAvailable(spaceport_street,"from related spaceport site to"));
        scn.LSUseCardAction(spaceport_street,"from related spaceport site to");
        assertTrue(scn.LSDecisionAvailable("move from"));

        assertTrue(scn.LSHasCardChoiceAvailable(spaceport_db)); //test1: spaceport and related
        assertTrue(scn.LSHasCardChoiceAvailable(spaceport_prefect)); //test1: spaceport and related
        assertFalse(scn.LSHasCardChoiceAvailable(spaceport_street)); //test2: same site
        assertFalse(scn.LSHasCardChoiceAvailable(spaceport_city)); //test3: spaceport, but not related
        assertFalse(scn.LSHasCardChoiceAvailable(lars_farm)); //test4: related, but not spaceport

        scn.LSChooseCard(spaceport_db);
        assertTrue(scn.LSDecisionAvailable("move to"));

        scn.LSChooseCard(spaceport_street);
        assertTrue(scn.LSDecisionAvailable("card to move"));
        scn.LSChooseCard(rebeltrooper4);

        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(spaceport_street,rebeltrooper4)); //test5: successfully relocated
        assertEquals(0,scn.GetLSUsedPileCount()); //test6: movement was free
    }

    @Test
    public void SpaceportStreetLSCanRelocateToRelatedSpaceportSiteUndercoverSpyTest() {
        //shows fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/676
        //test1: LS can choose undercover spy to relocate with LS text
        //test2: LS undercover spy can successfully relocate to a related spaceport site
        var scn = GetScenario();

        var tat = scn.GetLSCard("tat");
        var spaceport_street = scn.GetLSCard("spaceport_street");
        var spaceport_db = scn.GetLSCard("spaceport_db");
        var spaceport_city = scn.GetLSCard("spaceport_city");
        var lars_farm = scn.GetLSCard("lars_farm");
        var boushh = scn.GetLSCard("boushh");

        var spaceport_prefect = scn.GetDSCard("spaceport_prefect");

        scn.StartGame();

        scn.MoveLocationToTable(tat);
        scn.MoveLocationToTable(lars_farm);

        scn.MoveCardsToDSHand(spaceport_prefect);
        scn.MoveCardsToLSHand(boushh, spaceport_db, spaceport_street, spaceport_city);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(spaceport_prefect);
        scn.DSChooseCard(lars_farm);
        scn.DSChoose("Left");
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(spaceport_street);
        scn.LSChoose("Left");
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSPlayCard(spaceport_db);
        scn.LSChooseCard(spaceport_street);
        scn.LSChoose("Left");
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSDeployCard(boushh);
        scn.LSChooseCard(spaceport_street);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.MOVE);

        assertTrue(scn.LSCardActionAvailable(spaceport_street,"from here to related spaceport site"));
        scn.LSUseCardAction(spaceport_street,"from here to related spaceport site");
        assertTrue(scn.LSDecisionAvailable("move from"));
        scn.LSChooseCard(spaceport_street);
        assertTrue(scn.LSDecisionAvailable("move to"));
        assertTrue(scn.LSHasCardChoiceAvailable(spaceport_prefect));

        scn.LSChooseCard(spaceport_prefect);
        assertTrue(scn.LSDecisionAvailable("card to move"));
        assertTrue(scn.LSHasCardChoiceAvailable(boushh)); //test1
        scn.LSChooseCard(boushh);

        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(spaceport_prefect,boushh)); //test2: successfully relocated
    }

    @Test
    public void SpaceportStreetLSCanRelocateFromRelatedSpaceportSiteUndercoverSpyTest() {
        //test1: LS can choose undercover spy to relocate with LS text
        //test2: LS undercover spy can successfully relocate to spaceport street
        var scn = GetScenario();

        var tat = scn.GetLSCard("tat");
        var spaceport_street = scn.GetLSCard("spaceport_street");
        var spaceport_db = scn.GetLSCard("spaceport_db");
        var spaceport_city = scn.GetLSCard("spaceport_city");
        var lars_farm = scn.GetLSCard("lars_farm");
        var boushh = scn.GetLSCard("boushh");

        var spaceport_prefect = scn.GetDSCard("spaceport_prefect");

        scn.StartGame();

        scn.MoveLocationToTable(tat);
        scn.MoveLocationToTable(lars_farm);

        scn.MoveCardsToDSHand(spaceport_prefect);
        scn.MoveCardsToLSHand(boushh, spaceport_db, spaceport_street, spaceport_city);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(spaceport_prefect);
        scn.DSChooseCard(lars_farm);
        scn.DSChoose("Left");
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(spaceport_street);
        scn.LSChoose("Left");
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSPlayCard(spaceport_db);
        scn.LSChooseCard(spaceport_street);
        scn.LSChoose("Left");
        scn.PassAllResponses();
        scn.DSPass();

        scn.LSDeployCard(boushh);
        scn.LSChooseCard(spaceport_db);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.MOVE);

        assertTrue(scn.LSCardActionAvailable(spaceport_street,"from related spaceport site to"));
        scn.LSUseCardAction(spaceport_street,"from related spaceport site to");
        assertTrue(scn.LSDecisionAvailable("move from"));
        assertTrue(scn.LSHasCardChoiceAvailable(spaceport_db)); //test1: spaceport and related
        scn.LSChooseCard(spaceport_db);
        assertTrue(scn.LSDecisionAvailable("move to"));
        scn.LSChooseCard(spaceport_street);
        assertTrue(scn.LSDecisionAvailable("card to move"));
        scn.LSChooseCard(boushh);

        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(spaceport_street,boushh)); //test2: successfully relocated
    }

}
