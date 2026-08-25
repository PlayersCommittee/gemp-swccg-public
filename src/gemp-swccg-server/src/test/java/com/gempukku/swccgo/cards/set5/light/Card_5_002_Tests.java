package com.gempukku.swccgo.cards.set5.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_5_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("tech", "5_002"); //Cloud City Technician
                    put("jp_ls", "7_131"); //Tatooine: Jabba's Palace (Light)
                }},
                new HashMap<>()
                {{
                    put("jp_ds", "6_171"); //Tatooine: Jabba's Palace (Dark)
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
    public void CloudCityTechnicianStatsAndKeywordsAreCorrect() {
        /**
         * Title: Cloud City Technician
         * Uniqueness: Restricted 3
         * Side: Light
         * Type: Character
         * Subtype: Alien
         * Destiny: 2
         * Deploy: 2
         * Power: 1
         * Ability: 1
         * Forfeit: 2
         * Icons: Cloud City
         * Persona:
         * Keywords:
         * Game Text: When present at a converted site, may use 2 Force to raise your site to the top. Also,
         *      when present at an opponent's site that has a Scomp link, your Force drains are +1 there.
         * Lore: Former Imperial technician disenchanted with the New Order. Sympathetic to the Alliance. His knowledge
         *      of Imperial computer systems makes him a valuable ally.
         * Set: Cloud City
         * Rarity: C
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("tech").getBlueprint();

        assertEquals("Cloud City Technician", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.CLOUD_CITY);
        }});
        assertEquals(ExpansionSet.CLOUD_CITY,card.getExpansionSet());
        assertEquals(Rarity.C,card.getRarity());
    }

    @Test
    public void CloudCityTechnicianCanRaiseConvertedSite() {
        //test1: cannot raise converted site without 2 force available
        //test2: can raise converted site with 2 force available
        //test3: raise action costs 2 force
        //test4: raise action completes
        var scn = GetScenario();

        var tech = scn.GetLSCard("tech");
        var jp_ls = scn.GetLSCard("jp_ls");

        var jp_ds = scn.GetDSCard("jp_ds");

        scn.StartGame();

        scn.MoveLocationToTable(jp_ls);
        scn.MoveCardsToLocation(jp_ls, tech);

        scn.MoveCardsToDSHand(jp_ds);

        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.CardsAtLocation(jp_ls, tech));
        scn.DSDeployCard(jp_ds);
        scn.PassAllResponses();
        assertFalse(scn.CardsAtLocation(jp_ls, tech));
        assertTrue(scn.CardsAtLocation(jp_ds, tech));

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(tech, "Raise")); //test1

        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(0,scn.GetLSUsedPileCount());
        assertTrue(scn.GetLSForcePileCount() >= 2);
        assertTrue(scn.LSCardActionAvailable(tech, "Raise")); //test2

        scn.LSUseCardAction(tech,"Raise");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(2,scn.GetLSUsedPileCount()); //test3
        assertTrue(scn.CardsAtLocation(jp_ls, tech)); //test4
        assertFalse(scn.CardsAtLocation(jp_ds, tech));
    }

    @Test @Ignore
    public void CloudCityTechnicianCanRaiseConvertedSiteDuringBattle() {
        //test1: can raise converted site with 2 force available during battle
        //test2: raise action does not affect battle
        var scn = GetScenario();

        var tech = scn.GetLSCard("tech");
        var jp_ls = scn.GetLSCard("jp_ls");

        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var jp_ds = scn.GetDSCard("jp_ds");

        scn.StartGame();

        scn.MoveLocationToTable(jp_ls);
        scn.MoveCardsToLocation(jp_ls, tech, trooper1, trooper2);

        scn.MoveCardsToDSHand(jp_ds);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(jp_ds);
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(jp_ds);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.LSUseCardAction(tech,"Raise"); //test1
        scn.PassAllResponses();

            ///FAILS HERE - battle state canContinue returns false because can't spot characters at old battle location?
        assertTrue(scn.AwaitingDSWeaponsSegmentActions()); //test2
    }

}
