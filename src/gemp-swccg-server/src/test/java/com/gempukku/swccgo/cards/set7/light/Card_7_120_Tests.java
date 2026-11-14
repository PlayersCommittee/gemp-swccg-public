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

public class Card_7_120_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("farm", "7_120");
                    put("hydroponics", "1_037");
                    put("vaporator", "1_041");
                    put("backdoor", "8_069");
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
    public void FarmStatsAndKeywordsAreCorrect() {
        /**
         * Title: Farm
         * Uniqueness: Diamond
         * Side: Light
         * Type: Location
         * Subtype: Site
         * Destiny: 0
         * Icons: Special Edition, Planet, Exterior
         * Game Text: Light: During your deploy phase, Hydroponics Station or Vaporator may deploy here from
         *              Reserve Deck; reshuffle. May not be deployed to Bespin, Coruscant, Dagobah, Hoth, Kessel.
         *          Dark: May not be deployed to Bespin, Coruscant, Dagobah, Hoth, Kessel.
         * Light Force Icons: 2
         * Dark Force Icons: 1
         * Set: Special Edition
         * Rarity: F
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("farm").getBlueprint();

        assertEquals("Farm", card.getTitle());
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
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FARM);
        }});
        assertEquals(ExpansionSet.SPECIAL_EDITION,card.getExpansionSet());
        assertEquals(Rarity.F, card.getRarity());
    }

	@Test
	public void FarmCanDeployHydroponicsFromReserve() {
        //test1: Can use LS text to deploy hydroponics
        //test2: can deploy to farm
        //test3: cannot deploy to non-farm location
        //test4: cannot deploy to a card at farm
        //test5: hydroponics is successfully deployed
        //test6: force cost was paid
        var scn = GetScenario();

        var farm = scn.GetLSCard("farm");
        var hydroponics = scn.GetLSCard("hydroponics"); //deploy cost of 1
        var rebeltrooper = scn.GetLSFiller(1);

        var tat_site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(farm, rebeltrooper, hydroponics);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(farm);
        //scn.LSChooseCard(tat_site); //only valid choice is automatically selected
        scn.LSChoose("Left");
        scn.PassAllResponses();
        scn.DSPass();

        scn.MoveCardsToLocation(farm, rebeltrooper);
        scn.MoveCardsToTopOfLSReserveDeck(hydroponics);
        assertEquals(0,scn.GetLSUsedPileCount());

        scn.LSUseCardAction(farm, "Deploy");
        assertTrue(scn.LSHasCardChoiceAvailable(hydroponics)); //test1
        scn.LSChooseCard(hydroponics);

        scn.DSPass(); //LOOKED_AT_CARDS_IN_CARD_PILE - Optional responses
        scn.LSPass();

        //scn.LSChooseCard(farm); //only valid choice is automatically selected (test2, test3, test4)
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(farm, hydroponics)); //test5
        assertEquals(1,scn.GetLSUsedPileCount()); //test6
    }

    @Test
    public void FarmCanDeployVaporatorFromReserve() {
        //test1: Can use LS text to deploy vaporator
        //test2: can deploy to farm
        //test3: cannot deploy to non-farm location
        //test4: cannot deploy to a card at farm
        //test5: vaporator is successfully deployed
        //test6: force cost was paid
        var scn = GetScenario();

        var farm = scn.GetLSCard("farm");
        var vaporator = scn.GetLSCard("vaporator"); //deploy cost of 1
        var rebeltrooper = scn.GetLSFiller(1);

        var tat_site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(farm, rebeltrooper, vaporator);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(farm);
        //scn.LSChooseCard(tat_site); //only valid choice is automatically selected
        scn.LSChoose("Left");
        scn.PassAllResponses();
        scn.DSPass();

        scn.MoveCardsToLocation(farm, rebeltrooper);
        scn.MoveCardsToTopOfLSReserveDeck(vaporator);
        assertEquals(0,scn.GetLSUsedPileCount());

        scn.LSUseCardAction(farm, "Deploy");
        assertTrue(scn.LSHasCardChoiceAvailable(vaporator)); //test1
        scn.LSChooseCard(vaporator);

        scn.DSPass(); //LOOKED_AT_CARDS_IN_CARD_PILE - Optional responses
        scn.LSPass();

        //scn.LSChooseCard(farm); //only valid choice is automatically selected (test2, test3, test4)
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(farm, vaporator)); //test5
        assertEquals(1,scn.GetLSUsedPileCount()); //test6
    }

    @Test
    public void FarmAtNonTatooineLocationCanDeployHydroponicsFromReserve() {
        //normal requirements for hydroponics (exterior tatooine site) are ignored
        //test1: Can use LS text to deploy hydroponics
        //test2: can deploy to farm
        var scn = GetScenario();

        var farm = scn.GetLSCard("farm");
        var hydroponics = scn.GetLSCard("hydroponics"); //deploy cost of 1
        var backdoor = scn.GetLSCard("backdoor");

        scn.StartGame();

        scn.MoveCardsToLSHand(farm, hydroponics);

        scn.MoveLocationToTable(backdoor);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(farm);
        scn.LSChooseCard(backdoor);
        scn.LSChoose("Left");
        scn.PassAllResponses();
        scn.DSPass();

        scn.MoveCardsToTopOfLSReserveDeck(hydroponics);

        scn.LSUseCardAction(farm, "Deploy");
        assertTrue(scn.LSHasCardChoiceAvailable(hydroponics)); //test1
        scn.LSChooseCard(hydroponics);

        scn.DSPass(); //LOOKED_AT_CARDS_IN_CARD_PILE - Optional responses
        scn.LSPass();

        //scn.LSChooseCard(farm); //only valid choice is automatically selected
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(farm, hydroponics)); //test2
    }

}
