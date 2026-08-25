package com.gempukku.swccgo.cards.set3.dark;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_3_138_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(

                new HashMap<>() {{
                    put("wolfman", "1_030"); //shistavanen wolfman
                    put("boushh","110_001");
                }},
                new HashMap<>() {{
                    put("trample", "3_138");
                    put("scout1", "3_156"); //blizzard scout 1
                    put("tat_db","1_291");
                    put("imperial_barrier", "1_249");
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
    public void TrampleStatsAndKeywordsAreCorrect() {
        /**
         * Title: Trample
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Interrupt
         * Subtype: Used
         * Destiny: 4
         * Icons: Hoth
         * Game Text: If you have a piloted AT-AT or AT-ST present at a site, target opponent's character,
         *             'crashed' vehicle or unpiloted vehicle without armor present. Draw destiny.
         *             Character lost if destiny > ability. Vehicle lost if destiny < 7.
         * Lore: The enormous feet of a walker are designed for mobility on many types of terrain.
         *         They also can be used by merciless pilots to crush the Rebellion.
         * Set: Hoth
         * Rarity: R1
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("trample").getBlueprint();

        assertEquals("Trample", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.HOTH);
        }});
        assertEquals(ExpansionSet.HOTH,card.getExpansionSet());
        assertEquals(Rarity.R1, card.getRarity());
    }

    @Test
    public void TrampleSucceedsIfDestinyGreaterThanAbility() {
        //test1: can target opponent's character
        //test2: cannot target self's character
        //test3: if destiny > character's ability, character is lost
        var scn = GetScenario();

        var trample = scn.GetDSCard("trample");
        var scout1 = scn.GetDSCard("scout1");
        var stormtrooper = scn.GetDSFiller(1);
        var site = scn.GetDSStartingLocation();

        var rebeltrooper = scn.GetLSFiller(1);

        scn.StartGame();

        scn.MoveCardsToDSHand(trample);

        scn.MoveCardsToLocation(site, scout1, stormtrooper, rebeltrooper);

        scn.SkipToPhase(Phase.CONTROL);

        scn.PrepareDSDestiny(2); //for successful trample (2 > ability 1)

        scn.DSPlayCard(trample);
        assertTrue(scn.DSHasCardChoiceAvailable(rebeltrooper));
        assertFalse(scn.DSHasCardChoiceAvailable(stormtrooper));
        scn.DSChooseCard(rebeltrooper);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(1, scn.GetLSLostPileCount());
    }

    @Test
    public void TrampleFailsIfDestinyEqualsAbility() {
        //test1: if destiny = character's ability, character is not lost
        var scn = GetScenario();

        var trample = scn.GetDSCard("trample");
        var scout1 = scn.GetDSCard("scout1");
        var site = scn.GetDSStartingLocation();

        var rebeltrooper = scn.GetLSFiller(1);

        scn.StartGame();

        scn.MoveCardsToDSHand(trample);

        scn.MoveCardsToLocation(site, scout1, rebeltrooper);

        scn.SkipToPhase(Phase.CONTROL);

        scn.PrepareDSDestiny(1); //for failed trample (1 not > ability 1)

        scn.DSPlayCard(trample);
        assertTrue(scn.DSHasCardChoiceAvailable(rebeltrooper));
        scn.DSChooseCard(rebeltrooper);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(0, scn.GetLSLostPileCount());
    }

    @Test
    public void TrampleCanTargetUndercoverSpy() {
        //test1: if destiny = character's ability, character is not lost
        var scn = GetScenario();

        var trample = scn.GetDSCard("trample");
        var scout1 = scn.GetDSCard("scout1");
        var site = scn.GetDSStartingLocation();

        var boushh = scn.GetLSCard("boushh");

        scn.StartGame();

        scn.MoveCardsToDSHand(trample);
        scn.MoveCardsToLSHand(boushh);

        scn.MoveCardsToLocation(site, scout1);

        scn.LSActivateForceCheat(2); //enough to deploy boushh

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(boushh);
        scn.LSChooseCard(site);
        scn.PassAllResponses();

        scn.PrepareDSDestiny(7); //for trample success

        scn.DSPlayCard(trample);
        assertTrue(scn.DSHasCardChoiceAvailable(boushh));
        scn.DSChooseCard(boushh);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertEquals(1, scn.GetLSLostPileCount());
    }

    @Test
    public void TrampleCannotTargetExcludedCharacterDuringBattle_Barrier() {
        //test1: during battle, excluded character (due to barrier)
        // is considered inactive and cannot be targeted by trample
        var scn = GetScenario();

        var trample = scn.GetDSCard("trample");
        var scout1 = scn.GetDSCard("scout1");
        var imperial_barrier = scn.GetDSCard("imperial_barrier");
        var site = scn.GetDSStartingLocation();

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);

        scn.StartGame();

        scn.MoveCardsToDSHand(trample, imperial_barrier);
        scn.MoveCardsToLSHand(trooper2);

        scn.MoveCardsToLocation(site, scout1, trooper1);

        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.LSDeployCard(trooper2);
        scn.LSChooseCard(site);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPlayCard(imperial_barrier);

        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site);

        scn.LSPass(); //ABOUT_TO_BE_EXCLUDED_FROM_BATTLE - Optional responses
        scn.DSPass();

        scn.LSPass(); //EXCLUDED_FROM_BATTLE - Optional responses
        scn.DSPass();

        scn.DSPass(); //BATTLE_INITIATED - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.IsCardActive(trooper2)); //excluded by barrier

        scn.LSPass();

        scn.DSPlayCard(trample);
        assertTrue(scn.DSHasCardChoiceAvailable(trooper1));
        assertFalse(scn.DSHasCardChoiceAvailable(trooper2)); //test1
    }

    @Test @Ignore
    public void TrampleCannotTargetExcludedCharacterDuringBattle_MultipleBattles() {
        //test1: during battle, excluded character (due to participating in a different battle this turn)
        // is considered inactive and cannot be targeted by trample
        var scn = GetScenario();

        var trample = scn.GetDSCard("trample");
        var scout1 = scn.GetDSCard("scout1");
        var stormtrooper = scn.GetDSFiller(1);
        var imperial_barrier = scn.GetDSCard("imperial_barrier");
        var site = scn.GetDSStartingLocation();
        var tat_db = scn.GetDSCard("tat_db");

        var trooper = scn.GetLSFiller(1);
        var wolfman = scn.GetLSCard("wolfman");

        scn.StartGame();

        scn.MoveCardsToDSHand(trample, imperial_barrier);

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(site, scout1, trooper);
        scn.MoveCardsToLocation(tat_db, wolfman, stormtrooper);

        scn.LSActivateForceCheat(1); //to be able to 'react' wolfman to second battle

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(tat_db);
        scn.PassAllResponses();
        scn.SkipToDamageSegment();
        scn.DSPayRemainingForceLossFromReserveDeck();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        scn.LSPass();

        scn.DSInitiateBattle(site);
        scn.LSUseCardAction(wolfman, "react");
        scn.LSChooseCard(site);

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
            ///FAILS HERE - should be inactive just like excluded due to barrier
        assertFalse(scn.IsCardActive(wolfman)); //excluded due to participating in battle elsewhere this turn

        assertTrue(scn.DSCardPlayAvailable(trample));
        scn.DSPlayCard(trample);
        assertTrue(scn.DSHasCardChoiceAvailable(trooper));
        assertFalse(scn.DSHasCardChoiceAvailable(wolfman)); //test1
    }

    @Test @Ignore
    public void TrampleExcludedWalkerCannotTargetCharacterDuringBattle_MultipleBattles() {
        //shows issue: https://github.com/PlayersCommittee/gemp-swccg-public/issues/297
        //test1: during battle, excluded walker (due to participating in a different battle this turn)
        // is considered inactive and cannot be targeted by trample
        var scn = GetScenario();

        var trample = scn.GetDSCard("trample");
        var scout1 = scn.GetDSCard("scout1");
        var stormtrooper = scn.GetDSFiller(1);
        var site = scn.GetDSStartingLocation();
        var tat_db = scn.GetDSCard("tat_db");

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);

        scn.StartGame();

        scn.MoveCardsToDSHand(trample);

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(site, trooper1, stormtrooper);
        scn.MoveCardsToLocation(tat_db, scout1, trooper2);

        scn.SkipToLSTurn();

        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(tat_db);
        scn.PassAllResponses();
        scn.SkipToDamageSegment();
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSPayRemainingBattleDamageFromReserveDeck();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSBattlePhaseActions());
        scn.DSPass();

        scn.LSInitiateBattle(site);
        scn.DSUseCardAction(scout1, "react");
        scn.DSChooseCard(site);

        scn.PassAllResponses();
        scn.LSPass();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.IsCardActive(scout1)); //excluded due to participating in battle elsewhere this turn
        assertFalse(scn.DSCardPlayAvailable(trample));
    }

}


