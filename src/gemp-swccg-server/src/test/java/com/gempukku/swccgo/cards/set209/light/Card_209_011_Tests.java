package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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

public class Card_209_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("rose", "209_011"); //Rose Tico
                    put("finn", "204_006");
                    put("landspeeder", "1_149"); //Luke's X-34 Landspeeder (non-enclosed vehicle)
                }},
                new HashMap<>()
                {{
                    put("eppVader","108_006");
                    put("drE","1_172");
                    put("yab","5_163"); //You Are Beaten
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
    public void RoseTicoStatsAndKeywordsAreCorrect() {
        /**
         * Title: Rose Tico
         * Uniqueness: Unique
         * Side: Light
         * Type: Character
         * Subtype: Resistance
         * Destiny: 5
         * Deploy: 1
         * Power: 1
         * Ability: 2
         * Forfeit: 2
         * Icons: Pilot, Warrior, Episode VII, Virtual Set 9
         * Persona: Rose
         * Game Text: If Finn is about to be lost from same site, may place him in Used Pile instead.
         *         Once during battle, if your starship (or your other Resistance character) here is about to be 'hit'
         *         (and Rose is not 'hit'), may cause Rose to be 'hit' (and forfeit = 0) instead.
         * Lore: Female
         * Set: Set 9
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("rose").getBlueprint();

        assertEquals(Title.Rose, card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.RESISTANCE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FEMALE);
            //null
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.ROSE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.RESISTANCE);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_VII);
            add(Icon.VIRTUAL_SET_9);
        }});
        assertEquals(ExpansionSet.SET_9,card.getExpansionSet());
        assertEquals(Rarity.V,card.getRarity());
    }

    @Test
    public void RoseTicoCanSendForfeitedFinnToUsedPile() {
        var scn = GetScenario();

        var rose = scn.GetLSCard("rose");
        var finn = scn.GetLSCard("finn");

        var eppVader = scn.GetDSCard("eppVader");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, eppVader, rose, finn);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToEndOfPowerSegment(false);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(finn);

        assertTrue(scn.LSDecisionAvailable("About to forfeit"));
        assertTrue(scn.LSCardActionAvailable(rose, "Used Pile"));
        scn.LSUseCardAction(rose, "Used Pile");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertEquals(Zone.TOP_OF_USED_PILE, finn.getZone());
    }

    @Test
    public void RoseTicoCanSendFinnHitAndLostInBattleToUsedPile() {
        var scn = GetScenario();

        var rose = scn.GetLSCard("rose");
        var finn = scn.GetLSCard("finn");

        var eppVader = scn.GetDSCard("eppVader");
        var drE = scn.GetDSCard("drE");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, eppVader, drE, rose, finn);

        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(6); //succeed hitting finn
        scn.PrepareDSDestiny(7);

        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        scn.DSUseCardAction(eppVader);
        scn.DSChooseCard(finn);

        scn.LSPass(); //Fire Vader's Lightsaber - Optional responses
        scn.DSPass();


        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();


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

        scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
        scn.DSPass();

        scn.LSPass(); //FORFEIT_REDUCED_TO_ZERO - Optional responses
        scn.DSPass();

        scn.LSPass(); //Optional responses
        assertTrue(scn.DSCardActionAvailable(drE));
        scn.DSUseCardAction(drE);

        scn.DSChooseCard(finn);

        scn.LSPass(); //'Operate' on Finn - Optional responses
        scn.DSPass();

        assertTrue(scn.LSDecisionAvailable("About to lose"));
        assertTrue(scn.LSCardActionAvailable(rose, "Used Pile"));
        scn.LSUseCardAction(rose, "Used Pile");
        scn.PassAllResponses();

        scn.SkipToEndOfPowerSegment(false);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertEquals(Zone.TOP_OF_USED_PILE, finn.getZone());
    }

    @Test
    public void RoseTicoCanSendFinnHitAndExcludedInBattleToUsedPile() {
        var scn = GetScenario();

        var rose = scn.GetLSCard("rose");
        var finn = scn.GetLSCard("finn");

        var eppVader = scn.GetDSCard("eppVader");
        var yab = scn.GetDSCard("yab");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, eppVader, rose, finn);
        scn.MoveCardsToDSHand(yab);

        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(6); //succeed hitting finn
        scn.PrepareDSDestiny(7);

        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        scn.DSUseCardAction(eppVader);
        scn.DSChooseCard(finn);

        scn.PassAllResponses();

        scn.LSPass();

        scn.DSPlayCard(yab);
        scn.DSChooseCard(finn);

        scn.LSPass(); //Use 2 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //Playing You Are Beaten - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_EXCLUDED_FROM_BATTLE"));
        scn.DSPass(); //ABOUT_TO_BE_EXCLUDED_FROM_BATTLE - Optional responses
        scn.LSPass();

        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE"));
        scn.DSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses

            ///FAILS HERE
        assertTrue(scn.LSDecisionAvailable("About to lose"));
        assertTrue(scn.LSCardActionAvailable(rose, "Used Pile"));
        scn.LSUseCardAction(rose, "Used Pile");
        scn.PassAllResponses();

        scn.SkipToEndOfPowerSegment(false);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertEquals(Zone.TOP_OF_USED_PILE, finn.getZone());
    }

    @Test
    public void RoseTicoCannotSendFinnToUsedPileWhenLostViaAllCardsSituation() {
        //AR: "If a vehicle is lost or otherwise leaves the table, any cards aboard it are lost (All Cards situation).
        //Rose cannot save Finn when aboard a vehicle that is lost
        var scn = GetScenario();

        var rose = scn.GetLSCard("rose");
        var finn = scn.GetLSCard("finn");
        var landspeeder = scn.GetLSCard("landspeeder");

        var eppVader = scn.GetDSCard("eppVader");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, eppVader, rose, finn, landspeeder);
        scn.BoardAsPassenger(landspeeder,finn);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToEndOfPowerSegment(false);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(landspeeder);

        scn.LSChooseYes(); //You are choosing to forfeit ..., which has other cards aboard that could be forfeited first. Do you still want to forfeit it?

        scn.DSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses
        assertFalse(scn.LSCardActionAvailable(rose,"Used Pile"));
        scn.LSPass();

        assertTrue(scn.LSDecisionAvailable("Choose card to put on Lost Pile"));
        scn.LSChooseCard(finn);

        scn.DSPass(); //Optional responses
        assertFalse(scn.LSCardActionAvailable(rose,"Used Pile"));
        scn.LSPass();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
    }

}
