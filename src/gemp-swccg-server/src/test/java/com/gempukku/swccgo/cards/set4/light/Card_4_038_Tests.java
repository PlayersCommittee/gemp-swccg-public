package com.gempukku.swccgo.cards.set4.light;

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

public class Card_4_038_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(

                new HashMap<>() {{
                    put("han","1_011"); //(smuggler)
                    put("blues","4_038"); //Smuggler's Blues
                }},
                new HashMap<>() {{
                    put("ponda","1_190"); //(smuggler)
                    put("limited","1_255"); //Limited Resources
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
    public void SmugglersBluesStatsAndKeywordsAreCorrect() {
        /**
         * Title: Smuggler's Blues
         * Uniqueness: Unique
         * Side: Light
         * Type: Effect
         * Subtype: Normal
         * Destiny: 3
         * Icons: Dagobah
         * Game Text: Deploy on a smuggler. May use 2 Force to cancel Limited Resources. Also, if 'trained' by
         *         Rycar Ryjerd and piloting a starship when that starship completes Kessel Run, Rycar's Run or
         *         The First Transport Is Away, any retrieved Force may be taken into hand.
         * Lore: It's the lure of easy credits. It's got a very strong appeal. Perhaps you'd understand better
         *         wearing my flight suit. It's the ultimate special modification, it's the smuggler's blues.
         * Set: Dagobah
         * Rarity: R
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("blues").getBlueprint();

        assertEquals("Smuggler's Blues", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.DAGOBAH);
        }});
        assertEquals(ExpansionSet.DAGOBAH,card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void SmugglersBluesDeploysOnSmuggler() {
        //test1: cannot deploy on non-smuggler (rebel trooper)
        //test2: can deploy on your smuggler (han)
        //test3: can deploy on opponent's smuggler (ponda)
        //test4: after deploy, stays attached to targeted smuggler
        var scn = GetScenario();

        var blues = scn.GetLSCard("blues");
        var han = scn.GetLSCard("han");
        var rebelTrooper = scn.GetLSFiller(1);

        var ponda = scn.GetDSCard("ponda");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(blues);

        scn.MoveCardsToLocation(site, ponda, han, rebelTrooper);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(blues));
        scn.LSDeployCard(blues);
        assertFalse(scn.LSHasCardChoiceAvailable(rebelTrooper));
        assertTrue(scn.LSHasCardChoiceAvailable(han));
        assertTrue(scn.LSHasCardChoiceAvailable(ponda));
        scn.LSChooseCard(han);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(han, blues));
    }

    @Test
    public void SmugglersBluesMayCancelLimitedResources() {
        //test1: with 2 force available, can take optional response to cancel limited resources
        //test2: 2 force cost paid
        //test3: limited resources canceled (no force loss inflicted by limited resources)
        //test3: limited resources canceled (limited resources sent to lost pile)
        var scn = GetScenario();

        var blues = scn.GetLSCard("blues");

        var ponda = scn.GetDSCard("ponda");
        var limited = scn.GetDSCard("limited");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(limited);

        scn.MoveCardsToLocation(site, ponda);
        scn.AttachCardsTo(ponda, blues);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPlayCard(limited);

        assertEquals(0, scn.GetLSUsedPileCount());
        assertTrue(scn.GetLSForcePileCount() >= 2); //enough to cancel
        assertTrue(scn.LSCardActionAvailable(blues, "Cancel"));
        scn.LSUseCardAction(blues,"Cancel");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertEquals(2, scn.GetLSUsedPileCount()); //test2
        assertEquals(0, scn.GetLSLostPileCount()); //test3
        assertEquals(Zone.TOP_OF_LOST_PILE, limited.getZone()); //test4
    }

    //much cleaner to put the retrieve into hand tests into the 3 respective utinni effect card
    //test files (already set up for 'normal' retrieval)

    //Kessel Run:
    //(to be added)

    //Rycar's Run:
    //(to be added)

    //The First Transport Is Away:
    //see Card_4_038_Tests: TheFirstTransportIsAwayRetrievesXForceToHandWithSmugglersBlues

}


