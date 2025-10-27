package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_203_033_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("xwing1", "1_146");
                    put("xwing2", "1_146");
				}},
				new HashMap<>()
				{{
                    put("tat", "203_033"); //tatooine (v)
                    put("devastator", "1_301"); //dark starship
                    put("tie","1_304");
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
    public void TatooineVStatsAndKeywordsAreCorrect() {
        /**
         * Title: Tatooine (V)
         * Uniqueness: Unique
         * Side: Dark
         * Type: Location
         * Subtype: System
         * Destiny: 0
         * Icons: Virtual Set 3
         * Game Text: Dark:  Opponent may not initiate battle here until after your first turn.
         *      Light:  If a player controls, for each of their starships here, their power is +1 in battles at Tatooine sites.
         * Dark Force Icons: 2
         * Dark Force Icons: 1
         * Set: Virtual Set 3
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("tat").getBlueprint();

        assertEquals("Tatooine", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET);
            add(Icon.VIRTUAL_SET_3);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            //null
        }});
        assertEquals(7,card.getParsec());
        assertEquals(ExpansionSet.SET_3,card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }


	@Test
	public void TatooineVUncontrolledAddsNoPowerTest() {
        //test for no power bonus when Tatooine system is unoccupied
        //test for no power bonus when Tatooine system is occupied but not controlled
		var scn = GetScenario();

		var rebeltrooper = scn.GetLSFiller(1);
        var xwing1 = scn.GetLSCard("xwing1");
        var xwing2 = scn.GetLSCard("xwing2");

        var tat = scn.GetDSCard("tat");
		var tat_site = scn.GetDSStartingLocation();
        var stormtrooper = scn.GetDSFiller(1);
        var devastator = scn.GetDSCard("devastator");

		scn.StartGame();
		scn.MoveCardsToLocation(tat_site, rebeltrooper, stormtrooper);
        scn.MoveCardsToDSHand(tat,devastator);
        scn.MoveCardsToLSHand(xwing1,xwing2);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployLocation(tat);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(tat_site);
        scn.PassAllResponses();
            //test no power bonus with Tatooine system empty
        assertEquals(1,scn.GetLSTotalPower());
        assertEquals(1,scn.GetDSTotalPower());
        scn.SkipToDamageSegment(false);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        scn.SkipToLSTurn();
        scn.MoveCardsToLocation(tat,devastator,xwing1,xwing2);

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(tat_site);
        scn.PassAllResponses();
            //test no power bonus with Tatooine system occupied but not controlled
        assertEquals(1,scn.GetLSTotalPower());
        assertEquals(1,scn.GetDSTotalPower());
	}

    @Test
    public void TatooineVControlByDSAddsPowerTest() {
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1);

        var tat = scn.GetDSCard("tat");
        var tat_site = scn.GetDSStartingLocation();
        var stormtrooper = scn.GetDSFiller(1);
        var devastator = scn.GetDSCard("devastator");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveCardsToLocation(tat_site, rebeltrooper, stormtrooper);
        scn.MoveCardsToDSHand(tat,devastator,tie);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployLocation(tat);
        scn.MoveCardsToLocation(tat,devastator);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(tat_site);
        scn.PassAllResponses();
        //test 1 power bonus with Tatooine system controlled with 1 ship
        assertEquals(1,scn.GetLSTotalPower());
        assertEquals(2,scn.GetDSTotalPower());
        scn.SkipToDamageSegment(false);
        scn.PassAllResponses();
        scn.LSPayRemainingBattleDamageFromReserveDeck();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        scn.SkipToLSTurn();
        scn.MoveCardsToLocation(tat,tie);

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(tat_site);
        scn.PassAllResponses();
        //test 2 power bonus with Tatooine system controlled with 2 ships
        assertEquals(1,scn.GetLSTotalPower());
        assertEquals(3,scn.GetDSTotalPower());
    }

    @Test
    public void TatooineVControlByLSAddsPowerTest() {
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1);
        var xwing1 = scn.GetLSCard("xwing1");
        var xwing2 = scn.GetLSCard("xwing2");

        var tat = scn.GetDSCard("tat");
        var tat_site = scn.GetDSStartingLocation();
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToLocation(tat_site, rebeltrooper, stormtrooper);
        scn.MoveCardsToDSHand(tat);
        scn.MoveCardsToLSHand(xwing1,xwing2);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployLocation(tat);
        scn.MoveCardsToLocation(tat,xwing1);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(tat_site);
        scn.PassAllResponses();
        //test 1 power bonus with Tatooine system controlled with 1 ship
        assertEquals(2,scn.GetLSTotalPower());
        assertEquals(1,scn.GetDSTotalPower());
        scn.SkipToDamageSegment(false);
        scn.PassAllResponses();
        scn.DSPayRemainingBattleDamageFromReserveDeck();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        scn.SkipToLSTurn();
        scn.MoveCardsToLocation(tat,xwing2);

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(tat_site);
        scn.PassAllResponses();
        //test 2 power bonus with Tatooine system controlled with 2 ships
        assertEquals(3,scn.GetLSTotalPower());
        assertEquals(1,scn.GetDSTotalPower());
    }

    @Test
    public void TatooineVControlAddsNoPowerAtNonTatooineSiteTest() {
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1);
        var xwing1 = scn.GetLSCard("xwing1");
        var xwing2 = scn.GetLSCard("xwing2");

        var tat = scn.GetDSCard("tat");
        var nontat_site = scn.GetLSStartingLocation(); //cloud city site
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToLocation(nontat_site, rebeltrooper, stormtrooper);
        scn.MoveCardsToDSHand(tat);
        scn.MoveCardsToLSHand(xwing1,xwing2);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployLocation(tat);
        scn.MoveCardsToLocation(tat,xwing1);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(nontat_site);
        scn.PassAllResponses();
        //test 0 power bonus at cloud city with Tatooine system controlled with 1 ship
        assertEquals(1,scn.GetLSTotalPower());
        assertEquals(1,scn.GetDSTotalPower());
    }
}
