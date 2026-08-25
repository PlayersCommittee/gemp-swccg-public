package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_4_113_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("elom","6_012");
                    put("r2","2_014"); //r2-d2 (droid character)
                }},
                new HashMap<>()
                {{
                    put("snake", "4_113"); //vine snake
                    put("snake2", "4_113");
                    put("tat_db", "1_291");
                    put("hoth_db", "3_147");
                    put("exec_db", "7_282");
                    put("dag_cave", "4_158");
                    put("kessel", "1_288");
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
    public void VineSnakeStatsAndKeywordsAreCorrect() {
        /**
         * Title: Vine Snake
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Creature
         * Destiny: 3
         * Icons: Dagobah, Creature, Selective
         * Game Text: Habitat: planet sites (except Hoth). Attacks a character by attaching. X starts at 0.
         *             Every move phase, draw destiny; each time destiny > ability, add 1 to X.
         *            Character is power -X (eaten if power = 0).
         * Lore: Found on various planets throughout the galaxy. Hides among hanging vines, dropping on unsuspecting
         *             travelers that pass beneath. Kills its victims through gradual constriction.
         * Set: Dagobah
         * Rarity: C
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("snake").getBlueprint();

        assertEquals("Vine Snake", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(0, card.getFerocity(), scn.epsilon);
        assertEquals(3, card.getSpecialDefenseValue(), scn.epsilon);
        assertEquals(0, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.CREATURE);
        }});
        assertEquals(null, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.OPHIDIAN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.PARASITE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.CREATURE);
            add(Icon.SELECTIVE_CREATURE);
        }});
        assertEquals(ExpansionSet.DAGOBAH,card.getExpansionSet());
        assertEquals(Rarity.C,card.getRarity());
    }

    @Test
    public void VineSnakeDeployTest() {
        //Test1: cannot deploy to system
        //Test2: cannot deploy to non-planet site
        //Test3: cannot deploy to hoth site
        //Test4: can deploy to Dagobah
        //Test5: can deploy to planet site
        //Test6: deploys for 2 force
        var scn = GetScenario();

        var snake = scn.GetDSCard("snake");
        var kessel = scn.GetDSCard("kessel");
        var exec_db = scn.GetDSCard("exec_db");
        var hoth_db = scn.GetDSCard("hoth_db");
        var dag_cave = scn.GetDSCard("dag_cave");
        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);
        scn.MoveLocationToTable(hoth_db);
        scn.MoveLocationToTable(exec_db);
        scn.MoveLocationToTable(dag_cave);
        scn.MoveLocationToTable(kessel);

        scn.MoveCardsToDSHand(snake);

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.GetDSForcePileCount() >= 2);
        assertTrue(scn.DSDeployAvailable(snake));
        scn.DSDeployCard(snake);

        assertFalse(scn.DSHasCardChoiceAvailable(kessel)); //test1
        assertFalse(scn.DSHasCardChoiceAvailable(exec_db)); //test2
        assertFalse(scn.DSHasCardChoiceAvailable(hoth_db)); //test3
        assertTrue(scn.DSHasCardChoiceAvailable(dag_cave)); //test4
        assertTrue(scn.DSHasCardChoiceAvailable(tat_db)); //test5
        assertEquals(0,scn.GetDSUsedPileCount());
        scn.DSChooseCard(tat_db);
        scn.PassAllResponses();

        assertEquals(2,scn.GetDSUsedPileCount()); //test6
        assertTrue(scn.CardsAtLocation(tat_db, snake)); //
    }

    @Test
    public void VineSnakeAttachesToHost() {
        //Test1: vine snake can target characters on both sides
        //Test2: vine snake can attach to non-droid host
        //Test3: second vine snake can attach to same host
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1);

        var snake = scn.GetDSCard("snake");
        var snake2 = scn.GetDSCard("snake2");
        var trooper = scn.GetDSFiller(1);
        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, snake, snake2, rebeltrooper, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCardActionAvailable(snake, "attack"));
        scn.DSUseCardAction(snake, "attack");
        assertTrue(scn.DSDecisionAvailable("Choose whose non-creature to attack")); //test1
        scn.DSChoose("Light");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(rebeltrooper, snake)); //test2

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        scn.LSPass();

        scn.DSUseCardAction(snake2, "attack");
        assertTrue(scn.DSDecisionAvailable("Choose whose non-creature to attack"));
        scn.DSChoose("Light");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(rebeltrooper, snake));
        assertTrue(scn.IsAttachedTo(rebeltrooper, snake2)); //test3
    }

    @Test
    public void VineSnakeCannotTargetDroid() {
        //Test1: vine snake cannot target a droid as a host (inferred by being unable to choose sides if one side only has droid characters)
        var scn = GetScenario();

        var r2 = scn.GetLSCard("r2");

        var snake = scn.GetDSCard("snake");
        var trooper = scn.GetDSFiller(1);
        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, snake, r2, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCardActionAvailable(snake, "attack"));
        scn.DSUseCardAction(snake, "attack");
        assertFalse(scn.DSDecisionAvailable("Choose whose non-creature to attack")); //test1
            //only dark has a valid host, so dark auto-selected
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(trooper, snake));
    }

    @Test
    public void VineSnakeXIncFailsIfDestinyEqualsAbility() {
        //Test1: draws for X during owner's move phase
        //Test2: X does not change if destiny < ability
        //Test3: draws for X during opponent's move phase
        //Test4: X does not change if destiny = ability
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1);

        var snake = scn.GetDSCard("snake");
        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, snake, rebeltrooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(snake, "attack");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(rebeltrooper, snake));
        assertEquals(1, scn.GetPower(rebeltrooper), scn.epsilon);

        scn.SkipToPhase(Phase.MOVE);
        scn.PrepareDSDestiny(0);
        scn.DSUseCardAction(snake, "Draw destiny"); //test1
        scn.PassAllResponses();

        assertEquals(1, scn.GetPower(rebeltrooper), scn.epsilon); //test2: 0 not > 1 ability = failed to increase X

        scn.SkipToLSTurn(Phase.MOVE);
        scn.PrepareDSDestiny(1); //test1: 1 not > 1 ability = fail to increase X
        scn.LSPass();
        scn.DSUseCardAction(snake, "Draw destiny"); //test3
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(rebeltrooper, snake));
        assertEquals(1, scn.GetPower(rebeltrooper), scn.epsilon); //test4: 1 not > 1 ability = failed to increase X
    }

    @Test
    public void VineSnakeXIncSucceedsIfDestinyGreaterThanAbility() {
        //Test1: X increases if destiny > ability
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1);

        var snake = scn.GetDSCard("snake");
        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, snake, rebeltrooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(snake, "attack");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(rebeltrooper, snake));
        assertEquals(1, scn.GetPower(rebeltrooper), scn.epsilon);

        scn.SkipToPhase(Phase.MOVE);
        scn.PrepareDSDestiny(2); // > trooper's ability of 1
        scn.DSUseCardAction(snake, "Draw destiny");
        scn.PassAllResponses();
        assertEquals(1, scn.GetLSLostPileCount()); //test1 (lost because X = 1 caused trooper's power = 0)
    }

    @Test
    public void VineSnakeCannotTargetNewHostWhileAttachedToHost() {
        //shows fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/63
        //AR: "While attached, a creature will not attempt to attach to another target, and it may not voluntarily detach."
        //Test1: vine snake does not have attack action while attached to host
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1);

        var snake = scn.GetDSCard("snake");
        var trooper = scn.GetDSFiller(1);
        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, snake, rebeltrooper, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(snake, "attack");
        scn.DSChoose("Light");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(rebeltrooper, snake)); //test2

        scn.SkipToPhase(Phase.MOVE);
        scn.PrepareDSDestiny(0); //fail to increase X
        scn.DSUseCardAction(snake, "Draw destiny");
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.MOVE);
        scn.PrepareDSDestiny(1); //fail to increase X
        scn.LSPass();
        scn.DSUseCardAction(snake, "Draw destiny");
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.IsAttachedTo(rebeltrooper, snake));
        assertFalse(scn.DSCardActionAvailable(snake, "attack")); //test1
    }

    @Test
    public void VineSnakeXResetsAfterEating() {
        //shows fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/63
        //Test1: after eating a host with X > 0, vine snake's X resets to 0 when attaching to new host
        //eat stormtrooper (x = 1), then attach to power 1 rebeltrooper and confirm power = 1 (not 0)
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1);

        var snake = scn.GetDSCard("snake");
        var trooper = scn.GetDSFiller(1);
        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, snake, rebeltrooper, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(snake, "attack");
        scn.DSChoose("Dark");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(trooper, snake));

        scn.SkipToPhase(Phase.MOVE);
        scn.PrepareDSDestiny(7); //for success increase X
        scn.DSUseCardAction(snake, "Draw destiny");
        scn.PassAllResponses();

        assertFalse(scn.IsAttachedTo(trooper, snake));
        assertEquals(1, scn.GetDSLostPileCount()); //eaten trooper

        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSUseCardAction(snake, "attack");
        //LS auto selected
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(rebeltrooper, snake));
        assertEquals(1, scn.GetPower(rebeltrooper), scn.epsilon); //test1 (not affected by X = 1 from previous host)

        assertTrue(scn.AwaitingLSBattlePhaseActions());
    }

    @Test
    public void VineSnakeCanEatMoreThanOneHost() {
        //Test1: after eating a host, vine snake can attach to new host
        //Test2: vine snake can eat the second host
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1);

        var snake = scn.GetDSCard("snake");
        var trooper = scn.GetDSFiller(1);
        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, snake, rebeltrooper, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(snake, "attack");
        scn.DSChoose("Dark");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.MOVE);
        scn.PrepareDSDestiny(7); //success
        scn.DSUseCardAction(snake, "Draw destiny");
        scn.PassAllResponses();
        assertEquals(1, scn.GetDSLostPileCount()); //first host eaten

        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSUseCardAction(snake, "attack"); //test1
        //LS auto selected
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.MOVE);
        scn.PrepareDSDestiny(6); //success
        scn.DSUseCardAction(snake, "Draw destiny");
        scn.PassAllResponses();
        assertEquals(1, scn.GetLSLostPileCount()); //test2 (second host eaten)
    }

    @Test
    public void VineSnakeXIsNotCumulative() {
        //AR: multiple Vine Snakes are not cumulative (you apply only the largest value for X)
        //Test1: after snakeA has X = 1 and snakeB has x = 1, host's power is reduced by max(1,1) = 1
        //Test2: after snakeA has X = 2 and snakeB has x = 1, host's power is reduced by max(2,1) = 2
        var scn = GetScenario();

        var elom = scn.GetLSCard("elom");

        var snakeA = scn.GetDSCard("snake");
        var snakeB = scn.GetDSCard("snake2");
        var trooper = scn.GetDSFiller(1);
        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, snakeA, snakeB, elom, trooper);

        assertEquals(4, scn.GetPower(elom), scn.epsilon); //1 + 3 (due to imperial)

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(snakeA, "attack");
        scn.DSChoose("Light");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        scn.LSPass();

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(snakeB, "attack");
        scn.DSChoose("Light");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        assertEquals(4, scn.GetPower(elom), scn.epsilon); //4 - 0 (max of 0, 0) = 4

        scn.SkipToPhase(Phase.MOVE);
        scn.PrepareDSDestiny(7); //success
        scn.DSUseCardAction(snakeA, "Draw destiny");
        scn.PassAllResponses();

        scn.LSPass();

        scn.SkipToPhase(Phase.MOVE);
        scn.PrepareDSDestiny(6); //success
        scn.DSUseCardAction(snakeB, "Draw destiny");
        scn.PassAllResponses();

        assertEquals(3, scn.GetPower(elom), scn.epsilon); //test1: 4 - 1 (max of 1, 1) = 3

        scn.SkipToLSTurn(Phase.MOVE);
        scn.PrepareDSDestiny(5); //success
        scn.LSPass();
        scn.DSUseCardAction(snakeA, "Draw destiny");
        scn.PassAllResponses();

        assertEquals(2, scn.GetPower(elom), scn.epsilon); //test2: 4 - 2 (max of 2, 1) = 2
    }

    @Test
    public void VineSnakeEatsHostAsSoonAsPowerIs0() {
        //Test1: if host's power changes to 0 (without changing X), host is immediately eaten
        //use elom with temporary +3 power from trooper, increase X, and then move trooper away to make elom power = 0
        var scn = GetScenario();

        var elom = scn.GetLSCard("elom");

        var snake = scn.GetDSCard("snake");
        var trooper = scn.GetDSFiller(1);
        var tat_db = scn.GetDSCard("tat_db");
        var tat_site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, snake, elom, trooper);

        assertEquals(4, scn.GetPower(elom), scn.epsilon); //1 + 3 (due to imperial)

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(snake, "attack");
        scn.DSChoose("Light");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.MOVE);
        scn.PrepareDSDestiny(7); //success
        scn.DSUseCardAction(snake, "Draw destiny");
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.MOVE);
        scn.PrepareDSDestiny(6); //success
        scn.LSPass();
        scn.DSUseCardAction(snake, "Draw destiny");
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.MOVE);
        assertEquals(2, scn.GetPower(elom), scn.epsilon); //1 + 3 - 2 = 2

        scn.DSUseCardAction(trooper);
        scn.DSChooseCard(tat_site);
        scn.PassAllResponses();

        assertEquals(1, scn.GetLSLostPileCount()); //test1 host eaten
    }

    @Test
    public void VineSnakeStaysActiveWhileHostIsInactive() {
        //AR: While a parasite is attached to an inactive host, the parasite remains active. This is a specific exception to the Inactive rules.
        //Test1: vine snake is active while attached to host with missing status
        //Test2: vine snake can take move phase action to attempt to increase X while attached to missing host
        //Test3: vine snake can eat a missing host
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSFiller(1);

        var snake = scn.GetDSCard("snake");
        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, snake, rebeltrooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(snake, "attack");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(rebeltrooper, snake));
        assertEquals(1, scn.GetPower(rebeltrooper), scn.epsilon);

        scn.SkipToPhase(Phase.MOVE);
        scn.PrepareDSDestiny(0); //fail
        scn.DSUseCardAction(snake, "Draw destiny");
        scn.PassAllResponses();

        scn.MakeCardGoMissing(rebeltrooper);

        assertTrue(rebeltrooper.isMissing());
        assertFalse(scn.IsCardActive(rebeltrooper));
        assertTrue(scn.IsCardActive(snake)); //test1

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSPass();
        scn.PrepareDSDestiny(2); // > trooper's ability of 1
        scn.DSUseCardAction(snake, "Draw destiny"); //test2
        scn.PassAllResponses();
        assertEquals(1, scn.GetLSLostPileCount()); //test3
    }
}
