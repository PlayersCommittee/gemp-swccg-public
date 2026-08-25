package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_213_032_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                }},
                new HashMap<>()
                {{
                    put("blaster", "1_317"); //imperial blaster
                    put("keder", "12_109"); //Keder The Black (assassin, deploys undercover)
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.ShadowCollectiveObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ShadowCollectiveStatsAndKeywordsAreCorrect() {
        /**
         * Front Title: Shadow Collective
         * Back Title : You Know Who I Answer To
         * Side: Dark
         * Type: Objective
         * Destiny: 0/7
         * Front Game Text : Deploy Maul's Chambers. If Massassi Throne Room on table, may deploy [Set 13] Maul to
         *                         Maul's Chambers.
         *                 For remainder of game, you may not deploy cards with ability (or [Episode I] droids) except
         *                         characters with 'Black Sun,' 'Crimson Dawn,' or 'Hutt' in lore, assassins, gangsters,
         *                         [Episode I] bounty hunters, and [Independent] starships. Once per turn, may deploy
         *                         a non-unique blaster (or a card with 'First Light' in title) from Reserve Deck;
         *                         reshuffle.
         *                 Flip this card if you just 'hit' a character (or during your battle phase if your gangsters
         *                         control two battlegrounds).
         * Back Game Text: May immediately re-circulate and shuffle your Reserve Deck.
         *                 While this side up, if your gangster leader in battle at same site as your non-unique
         *                         blaster, may add one destiny to total power. If Maul alone, during your draw phase
         *                         may peek at the cards in your Force Pile
         *                 Flip this card at end of turn. If you are about to flip this card and you occupy three
         *                         battlegrounds, opponent loses 1 Force.
         * Set: Set 13
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("shadow").getBlueprint();

        assertEquals(Title.Shadow_Collective, card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.VIRTUAL_SET_13);
        }});
        assertEquals(ExpansionSet.SET_13,card.getExpansionSet());
        assertEquals(Rarity.V,card.getRarity());

        var back = scn.GetDSCard("shadow").getOtherSideBlueprint();
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Title.You_Know_Who_I_Answer_To, back.getTitle());
        assertEquals(Side.DARK, back.getSide());
        assertEquals(7, back.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(back, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintIconCheck(back, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.VIRTUAL_SET_13);
        }});
        assertEquals(ExpansionSet.SET_13,back.getExpansionSet());
        assertEquals(Rarity.V,back.getRarity());
    }

    @Test
    public void ShadowCollectiveCanDownloadOncePerYourDeployPhase() {
        //test1: cannot use download action during your non-deploy phase
        //test2: can use download action during your deploy phase
        //test3: cannot use download action a second time during your deploy phase
        //test4: cannot use download action during opponent's deploy phase
        var scn = GetScenario();

        var trooper = scn.GetDSFiller(1);
        var blaster = scn.GetDSCard("blaster");

        // Pulled from the Shadow Collective default setup:
        var shadow = scn.GetDSCard("shadow");
        var chambers = scn.GetDSCard("chambers");

        scn.StartGame();

        scn.MoveCardsToLocation(chambers, trooper);
        scn.MoveCardsToDSHand(blaster); //(keep out of reserve so download attempt will fail)

        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(shadow, "Deploy a card")); //test1

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(shadow, "Deploy a card")); //test2
        scn.DSUseCardAction(shadow, "Deploy a card");

        scn.DSPass(); //unsuccessful attempt
        scn.LSPass();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());

        scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(shadow, "Deploy a card")); //test3

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(shadow, "Deploy a card")); //test4
    }

    @Test
    public void ShadowCollectiveCanDownloadBlaster() {
        //test1: can choose a non-unique blaster
        //test2: can choose an eligible target to deploy blaster on
        //test3: blaster is deployed on selected target
        //test4: force cost to deploy is paid
        var scn = GetScenario();

        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var blaster = scn.GetDSCard("blaster");

        // Pulled from the Shadow Collective default setup:
        var shadow = scn.GetDSCard("shadow");
        var chambers = scn.GetDSCard("chambers");

        scn.StartGame();

        scn.MoveCardsToLocation(chambers, trooper1, trooper2);
        scn.MoveCardsToDSHand(blaster);

         scn.SkipToPhase(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(blaster);
        scn.DSUseCardAction(shadow, "Deploy a card");

        assertTrue(scn.DSHasCardChoiceAvailable(blaster)); //test1
        scn.DSChooseCard(blaster);
        scn.PassAllResponses();

        assertTrue(scn.DSHasCardChoiceAvailable(trooper1)); //test2
        assertTrue(scn.DSHasCardChoiceAvailable(trooper2)); //test2
        scn.DSChooseCard(trooper1);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(trooper1, blaster)); //test3
        assertEquals(1,scn.GetDSUsedPileCount()); //test4
    }

    @Test
    public void ShadowCollectiveCanDownloadBlasterToUndercoverSpy() {
        //demonstrates fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/971
        //test1: can choose a non-unique blaster
        //test2: can choose an eligible undercover spy
        //test3: blaster is deployed on undercover spy
        var scn = GetScenario();

        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var keder = scn.GetDSCard("keder");
        var blaster = scn.GetDSCard("blaster");

        // Pulled from the Shadow Collective default setup:
        var shadow = scn.GetDSCard("shadow");
        var chambers = scn.GetDSCard("chambers");

        scn.StartGame();

        scn.MoveCardsToLocation(chambers, trooper1, trooper2);
        scn.MoveCardsToDSHand(blaster, keder);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSActivateForceCheat(1); //enough to deploy keder and blaster
        scn.MoveCardsToTopOfDSReserveDeck(blaster);

        scn.DSDeployCard(keder);
        scn.DSChooseCard(chambers);
        scn.PassAllResponses();

        assertTrue(keder.isUndercover());
        scn.LSPass();

        scn.DSUseCardAction(shadow, "Deploy a card");

        assertTrue(scn.DSHasCardChoiceAvailable(blaster)); //test1
        scn.DSChooseCard(blaster);
        scn.PassAllResponses();

        assertTrue(scn.DSHasCardChoiceAvailable(trooper1));
        assertTrue(scn.DSHasCardChoiceAvailable(trooper2));
        assertTrue(scn.DSHasCardChoiceAvailable(keder)); //test2
        scn.DSChooseCard(keder);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(keder, blaster)); //test3
    }

}
