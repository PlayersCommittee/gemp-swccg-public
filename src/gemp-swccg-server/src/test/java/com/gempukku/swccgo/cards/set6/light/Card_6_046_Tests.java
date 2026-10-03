package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_6_046_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
                put("yarkora", "6_46");
                put("yarkora2", "6_46");
                put("saelt", "6_37"); //species: yarkora
                put("momaw", "1_20"); //spy
                put("fives", "203_2");
            }},
            new HashMap<>() {{
                put("garindan", "1_177"); //spy
                put("e3", "5_097"); //E-3PO (protocol droid)
                put("eChuTa", "5_138"); //to cancel game text
            }},
            20,
            20,
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
    public void YarkoraStatsAndKeywordsAreCorrect() {
        /**
         * Title: Yarkora
         * Uniqueness: Restricted 3
         * Side: Light
         * Type: Character
         * Destiny: 3
         * Deploy: 2
         * Power: 1
         * Ability: 1
         * Forfeit: 2
         * Icons: Alien, Jabba's Palace
         * Game Text: If at same site as an Undercover spy during your control phase, may draw destiny.
         *          Each of your Yarkoras on table may cumulatively subtract one from that destiny.
         *          Spy's 'cover is broken' if destiny = spy's ability.
         * Lore: Mysterious, secretive aliens. Tend to be found as couriers, scouts and t'bac farmers.
         *          Some have helped the Alliance's efforts at counter-espionage.
         * Set: Jabba's Palace
         * Rarity: C
         */

        var scn = GetScenario();
        var card = scn.GetLSCard("yarkora").getBlueprint();
        assertEquals("Yarkora", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        assertEquals(Species.YARKORA, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.JABBAS_PALACE);
        }});
        assertEquals(ExpansionSet.JABBAS_PALACE, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void YarkoraCanTargetOpponentUndercoverSpy() {
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var site = scn.GetLSStartingLocation();
        var garindan = scn.GetDSCard("garindan");

        scn.StartGame();
        scn.MoveCardsToLocation(site, yarkora, garindan);
        scn.MakeCardGoUndercover(garindan);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(garindan.isUndercover());
        assertTrue(scn.LSCardActionAvailable(yarkora, "Break a spy's cover"));
    }

    @Test
    public void YarkoraCanTargetOwnUndercoverSpy() {
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var momaw = scn.GetLSCard("momaw");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, yarkora, momaw);
        scn.MakeCardGoUndercover(momaw);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(momaw.isUndercover());
        assertTrue(scn.LSCardActionAvailable(yarkora, "Break a spy's cover"));
    }

    @Test
    public void YarkoraCannotTargetOutsideControlPhase() {
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var site = scn.GetLSStartingLocation();
        var garindan = scn.GetDSCard("garindan");

        scn.StartGame();
        scn.MoveCardsToLocation(site, yarkora, garindan);
        scn.MakeCardGoUndercover(garindan);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(garindan.isUndercover());
        assertFalse(scn.LSCardActionAvailable(yarkora, "Break a spy's cover"));
    }

    @Test
    public void YarkoraCannotTargetSpyAtDifferentSite() {
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var site = scn.GetLSStartingLocation();
        var garindan = scn.GetDSCard("garindan");
        var site2 = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, yarkora);

        scn.MoveCardsToLocation(site2, garindan);
        scn.MakeCardGoUndercover(garindan);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(garindan.isUndercover());
        assertFalse(scn.LSCardActionAvailable(yarkora, "Break a spy's cover"));
    }

    @Test
    public void YarkoraBreaksCoverWhenDestinyEqualsAbility() {
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var garindan = scn.GetDSCard("garindan");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, yarkora, garindan);
        scn.MakeCardGoUndercover(garindan);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(garindan.isUndercover());
        scn.PrepareLSDestiny(1); // Garindan ability 1
        scn.LSUseCardAction(yarkora, "Break a spy's cover");
        scn.LSChooseCard(garindan);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(garindan.isUndercover());
    }

    @Test
    public void YarkoraDoesNotBreakCoverWhenDestinyLessThanAbility() {
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var garindan = scn.GetDSCard("garindan");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, yarkora, garindan);
        scn.MakeCardGoUndercover(garindan);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(garindan.isUndercover());
        scn.PrepareLSDestiny(0); // Garindan ability 1
        scn.LSUseCardAction(yarkora, "Break a spy's cover");
        scn.LSChooseCard(garindan);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(garindan.isUndercover());
    }

    @Test
    public void YarkoraDoesNotBreakCoverWhenDestinyMoreThanAbility() {
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var garindan = scn.GetDSCard("garindan");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, yarkora, garindan);
        scn.MakeCardGoUndercover(garindan);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(garindan.isUndercover());
        scn.PrepareLSDestiny(2); // Garindan ability 1
        scn.LSUseCardAction(yarkora, "Break a spy's cover");
        scn.LSChooseCard(garindan);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(garindan.isUndercover());
    }

    @Test
    public void YarkoraMaySubtractOneFromBreakCoverDestiny() {
        //test1: yarkora can subtract 1 after seeing destiny draw
        //test2: yarkora cannot subtract a second time
        //test2: yarkora subtract changes destiny draw to result in breaking cover
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var garindan = scn.GetDSCard("garindan");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, yarkora, garindan);
        scn.MakeCardGoUndercover(garindan);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(2); // 2 -1 = Garindan ability 1
        scn.LSUseCardAction(yarkora, "Break a spy's cover");
        scn.LSChooseCard(garindan);

        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        assertTrue(scn.LSCardActionAvailable(yarkora, "Subtract 1")); //test1
        scn.LSUseCardAction(yarkora, "Subtract 1");

        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(yarkora, "Subtract 1")); //test2
        scn.LSPass();

        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(garindan.isUndercover()); //test3
    }

    @Test
    public void YarkoraSubtractIsCumulative() {
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var yarkora2 = scn.GetLSCard("yarkora2");
        var garindan = scn.GetDSCard("garindan");
        var site = scn.GetLSStartingLocation();
        var site2 = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, yarkora, garindan);
        scn.MoveCardsToLocation(site2, yarkora2);
        scn.MakeCardGoUndercover(garindan);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(3); // 3 -1 -1 = Garindan ability 1
        scn.LSUseCardAction(yarkora, "Break a spy's cover");
        scn.LSChooseCard(garindan);

        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        assertTrue(scn.LSCardActionAvailable(yarkora, "Subtract 1"));
        assertTrue(scn.LSCardActionAvailable(yarkora2, "Subtract 1"));
        scn.LSUseCardAction(yarkora, "Subtract 1");

        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(yarkora, "Subtract 1"));
        assertTrue(scn.LSCardActionAvailable(yarkora2, "Subtract 1"));
        scn.LSUseCardAction(yarkora2, "Subtract 1");

        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(garindan.isUndercover());
    }

    @Test
    public void YarkoraSubtractsAreIndependentActions() {
        //intermediate destiny states exist between multiple subtracts which allows fives to take
        //an action on destiny = 5 (even though starting destiny is 6 and final destiny is 4)
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var yarkora2 = scn.GetLSCard("yarkora2");
        var fives = scn.GetLSCard("fives");
        var garindan = scn.GetDSCard("garindan");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, yarkora, yarkora2, garindan, fives);
        scn.MakeCardGoUndercover(garindan);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(6);
        assertEquals(0,scn.GetLSHandCount());
        scn.LSUseCardAction(yarkora, "Break a spy's cover");
        scn.LSChooseCard(garindan);

        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        assertTrue(scn.LSCardActionAvailable(yarkora, "Subtract 1"));
        assertTrue(scn.LSCardActionAvailable(yarkora2, "Subtract 1"));
        assertFalse(scn.LSCardActionAvailable(fives, "into hand")); //still destiny 6
        scn.LSUseCardAction(yarkora, "Subtract 1");

        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(yarkora, "Subtract 1"));
        assertTrue(scn.LSCardActionAvailable(yarkora2, "Subtract 1"));
        assertTrue(scn.LSCardActionAvailable(fives, "into hand")); //destiny 5
        scn.LSUseCardAction(fives, "into hand");

        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(yarkora, "Subtract 1"));
        assertTrue(scn.LSCardActionAvailable(yarkora2, "Subtract 1"));
        assertFalse(scn.LSCardActionAvailable(fives, "into hand"));
        scn.LSUseCardAction(yarkora2, "Subtract 1");

        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(1,scn.GetLSHandCount()); //destiny was drawn into hand
        assertTrue(garindan.isUndercover()); //destiny draw of 4 did not break cover
    }

    @Test
    public void YarkoraGrantsSubtractBySpecies() {
        //test 1: yarkora grants a non-title yarkora (Saelt-Marae) a subtract action
        //test 2: yarkora does not grant a non-yarkora title/species (rebel trooper) a subtract action
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var saelt = scn.GetLSCard("saelt");
        var trooper = scn.GetLSFiller(1);
        var garindan = scn.GetDSCard("garindan");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, yarkora, garindan, saelt, trooper);
        scn.MakeCardGoUndercover(garindan);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(3); // 3 -1 (Yarkora) -1 (Saelt) = 1
        scn.LSUseCardAction(yarkora, "Break a spy's cover");
        scn.LSChooseCard(garindan);

        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        assertTrue(scn.LSCardActionAvailable(yarkora, "Subtract 1"));
        assertTrue(scn.LSCardActionAvailable(saelt, "Subtract 1")); //test1
        assertFalse(scn.LSCardActionAvailable(trooper, "Subtract 1")); //test2
    }

    @Test
    public void YarkoraGrantsSubtractToYarkoraWithCanceledGameText() {
        //shows other yarkora are using a granted action - not their own
        var scn = GetScenario();
        var yarkora = scn.GetLSCard("yarkora");
        var yarkora2 = scn.GetLSCard("yarkora2");
        var garindan = scn.GetDSCard("garindan");
        var e3 = scn.GetDSCard("e3");
        var eChuTa = scn.GetDSCard("eChuTa");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(eChuTa);

        scn.MoveCardsToLocation(site, yarkora, yarkora2, garindan, e3);
        scn.MakeCardGoUndercover(garindan);

        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.LSPass();
        scn.LSChooseYes(); //(are you sure?)

            //cancel yarkora2's game text for rest of this turn
        scn.DSPlayCard(eChuTa, "Insult");
        scn.DSChooseCard(e3);
        scn.DSChooseCard(yarkora2);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareLSDestiny(3);
        assertTrue(scn.LSCardActionAvailable(yarkora, "Break a spy's cover"));
        assertFalse(scn.LSCardActionAvailable(yarkora2, "Break a spy's cover")); //game text canceled!
        scn.LSUseCardAction(yarkora, "Break a spy's cover");
        scn.LSChooseCard(garindan);

        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        assertTrue(scn.LSCardActionAvailable(yarkora, "Subtract 1"));
        assertTrue(scn.LSCardActionAvailable(yarkora2, "Subtract 1")); //(even though own game text canceled)
    }

}
