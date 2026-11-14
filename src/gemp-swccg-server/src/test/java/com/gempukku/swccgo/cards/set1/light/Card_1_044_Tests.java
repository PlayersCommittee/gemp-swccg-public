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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1_044_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("beggar", "1_044");
                    put("jess", "6_021");
                    put("talz", "1_031"); //(alien that can be targeted by jess)
                }},
                new HashMap<>()
                {{
                }},
                20,
                20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, //(exterior tatooine location valid for beggar)
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void BeggarStatsAndKeywordsAreCorrect() {
        /**
         * Title: Beggar
         * Uniqueness: Unique
         * Side: Light
         * Type: Effect
         * Subtype: Normal
         * Destiny: 3
         * Icons: Effect
         * Game Text: Use 3 Force to deploy on any exterior Tatooine site (free at Beggar's Canyon).
         *      You may use any amount of Force in the opponent's Force Pile during your turns.
         *      However, if you use more than 1 of the opponent's Force in a turn, Beggar is lost.
         * Lore: Many Mos Eisley citizens, once swindled and robbed, become destitute.
         *      Unable to afford off-planet passage, they live in the streets and do odd jobs or beg.
         * Set: Premiere
         * Rarity: R1
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("beggar").getBlueprint();

        assertEquals("Beggar", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_SITE);
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
    public void BeggarRequiresUsingOpponentForceIfCannotPayWithOwnForce() {
        //test1: action with force cost is available even without sufficient force in owner's pile
        //test2: required to use opponent's force
        //test3: min selection choice is correct (1)
        //test4: max selection choice is lowest of cost (1) and opponent's force available (3)
        //test5: after choosing 1, not prompted to use more force
        //test6: beggar not lost at end of turn (used <= 1 force)
        var scn = GetScenario();

        var beggar = scn.GetLSCard("beggar");
        var jess = scn.GetLSCard("jess");
        var talz = scn.GetLSCard("talz");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.AttachCardsTo(site,beggar);
        scn.MoveCardsToLocation(site,jess,talz);

        scn.SkipToLSTurn(Phase.ACTIVATE);

        assertEquals(3,scn.GetDSForcePileCount());
        assertEquals(0,scn.GetLSForcePileCount());
        assertEquals(0,scn.GetDSUsedPileCount());

        assertTrue(scn.LSCardActionAvailable(jess,"Charm")); //test1
        scn.LSUseCardAction(jess,"Charm");
        scn.LSChooseCard(talz);

        assertTrue(scn.LSDecisionAvailable("opponent's Force to use")); //test2
        assertEquals(1,scn.LSGetChoiceMin()); //test3
        assertEquals(1,scn.LSGetChoiceMax()); //test4
        scn.LSDecided(1);
        scn.PassAllResponses();

        assertEquals(1,scn.GetDSUsedPileCount()); //test5
        scn.DSPass();

        assertTrue(scn.AwaitingLSActivatePhaseActions());
        scn.LSActivateMaxForceAndPass();
        scn.SkipToDSTurn();

        assertEquals(0,scn.GetLSLostPileCount()); //test6
    }

    @Test
    public void BeggarAllowsUsingOpponentForceMoreThanOnce() {
        //test1: deploy with force cost can use opponent's force
        //test2: max selectable force cost is capped by force cost (deploy value of 3)
        //test3: can use beggar again to use up more of opponent's force
        //test4: max selectable force cost is capped by remaining force cost (deploy value of 3 minus already selected)
        //test5: deploy completes with partial cost paid from opponent's force
        //test6: 2 of opponent's force used (selected 1 + 1)
        //test7: 1 of own force used (cost of 3 - 2 from opponent's force)
        //test8: beggar lost at end of turn (used > 1 force)

        var scn = GetScenario();

        var beggar = scn.GetLSCard("beggar");
        var jess = scn.GetLSCard("jess");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.AttachCardsTo(site,beggar);
        scn.MoveCardsToLSHand(jess);

        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.DSActivateForceCheat(1);
        scn.LSActivateForceCheat(2);
        assertEquals(4,scn.GetDSForcePileCount());
        assertEquals(5,scn.GetLSForcePileCount());
        assertEquals(0,scn.GetDSUsedPileCount());

        scn.LSDeployCard(jess);
        scn.LSChooseCard(site);

        scn.DSPass(); //Use 3 Force - Optional responses

        assertTrue(scn.LSCardActionAvailable(beggar,"opponent's Force")); //test1
        scn.LSUseCardAction(beggar,"opponent's Force");
        assertEquals(1,scn.LSGetChoiceMin());
        assertEquals(3,scn.LSGetChoiceMax()); //test2
        scn.LSDecided(1);

        scn.DSPass(); //Use 2 Force - Optional responses

        assertTrue(scn.LSCardActionAvailable(beggar,"opponent's Force")); //test3
        scn.LSUseCardAction(beggar,"opponent's Force");
        assertEquals(1,scn.LSGetChoiceMin());
        assertEquals(2,scn.LSGetChoiceMax()); //test4
        scn.LSDecided(1);

        scn.DSPass(); //Use 1 Force - Optional responses

        assertTrue(scn.LSCardActionAvailable(beggar,"opponent's Force"));

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.CardsAtLocation(site,jess)); //test5
        assertEquals(2,scn.GetDSUsedPileCount()); //test6
        assertEquals(1,scn.GetLSUsedPileCount()); //test7

        scn.SkipToPhase(Phase.DRAW);

        scn.LSPass(); //Choose Draw action or Pass
        scn.DSPass();

        scn.LSPass(); //RECIRCULATED - Optional responses
        scn.DSPass();

        scn.LSPass(); //RECIRCULATED - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE"));
        scn.PassAllResponses();

        scn.SkipToDSTurn();

        assertEquals(1,scn.GetLSLostPileCount()); //test8
    }

    @Test
    public void BeggarRequiresUsingOpponentForceIfCanOnlyPayPartialWithOwnForce() {
        //test1: deploy with force cost must use some of opponent's force if owner has enough to pay part, but not full cost
        //test2: required to use opponent's force
        //test3: min selection choice is correct 2: cost 3 - owner's force available (1)
        //test4: max selection choice is correct 3: lowest of cost (3) and opponent's force available (4)
        //test5: after choosing 2, not prompted to use more force
        //test6: after choosing 2 (meeting obligation), optional action available to use more
        //test7: deploy completed
        //test8: 2 of opponent's force used
        //test9: 1 of own force used (cost of 3 - 2 from opponent's force)
        //test10: beggar lost at end of turn (used > 1 force)

        var scn = GetScenario();

        var beggar = scn.GetLSCard("beggar");
        var jess = scn.GetLSCard("jess");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.AttachCardsTo(site,beggar);
        scn.MoveCardsToLSHand(jess);

        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.DSActivateForceCheat(1);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetTopOfLSForcePile()); //'unactive' 1 force
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetTopOfLSForcePile()); //'unactive' 1 force

        assertEquals(4,scn.GetDSForcePileCount());
        assertEquals(1,scn.GetLSForcePileCount());
        assertEquals(0,scn.GetDSUsedPileCount());

        scn.LSDeployCard(jess); //test1
        scn.LSChooseCard(site);

        assertTrue(scn.LSDecisionAvailable("opponent's Force to use")); //test2
        assertEquals(2,scn.LSGetChoiceMin()); //test3
        assertEquals(3,scn.LSGetChoiceMax()); //test4
        scn.LSDecided(2);

        assertTrue(scn.DSAnyDecisionsAvailable()); //test5
        scn.DSPass(); //Use 1 Force - Optional responses

        assertTrue(scn.LSCardActionAvailable(beggar,"opponent's Force")); //test6

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.CardsAtLocation(site,jess)); //test7
        assertEquals(2,scn.GetDSUsedPileCount()); //test8
        assertEquals(1,scn.GetLSUsedPileCount()); //test9

        scn.SkipToPhase(Phase.DRAW);

        scn.LSPass(); //Choose Draw action or Pass
        scn.DSPass();

        scn.LSPass(); //RECIRCULATED - Optional responses
        scn.DSPass();

        scn.LSPass(); //RECIRCULATED - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE"));
        scn.PassAllResponses();

        scn.SkipToDSTurn();

        assertEquals(1,scn.GetLSLostPileCount()); //test10
    }

    //check other beggar usage cases

    //beggar deploy checks: location and cost reqs

}
