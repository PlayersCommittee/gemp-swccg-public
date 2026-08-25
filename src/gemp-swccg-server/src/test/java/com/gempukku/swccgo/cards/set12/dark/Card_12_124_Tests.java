package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
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

public class Card_12_124_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(

                new HashMap<>() {{
                    put("podracer","11_047"); //anakin's
                    put("bay","11_045");
                }},
                new HashMap<>() {{
                    put("raider", "12_124"); //tusken raider
                    put("raider2", "12_124");
                    put("arena","11_094");
                    put("ds_podracer","11_098"); //teemto's
                    put("boonta","11_079");
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
    public void TuskenRaiderStatsAndKeywordsAreCorrect() {
        /**
         * Title: Tusken Raider
         * Uniqueness: Nonunique
         * Side: Dark
         * Type: Character
         * Subtype: Alien
         * Destiny: 3
         * Deploy: 2
         * Power: 2
         * Ability: 1
         * Forfeit: 2
         * Species: Tusken Raider
         * Icons: Coruscant, Episode 1
         * Game Text: While at Podrace Arena, if opponent just placed race destiny on their Podracer, once per turn
         *             may use 3 Force to 'shoot' (no other Tusken Raiders may 'shoot' this turn.) Draw destiny.
         *             If destiny > 3, place race destiny in opponent's hand.
         * Lore: Tusken Raiders are constantly taking bets to see who can hit the most Podracers on the
         *         Boonta Eve racetrack.
         * Set: Coruscant
         * Rarity: C
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("raider").getBlueprint();

        assertEquals("Tusken Raider", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        assertEquals(Species.TUSKEN_RAIDER,card.getSpecies());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.CORUSCANT);
            add(Icon.EPISODE_I);
        }});
        assertEquals(ExpansionSet.CORUSCANT, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void TuskenRaiderAtPodraceArenaMayShoot() {
        //test1: raider at arena can shoot
        //test2: raider at non-arena cannot shoot
        var scn = GetScenario();

        var raider = scn.GetDSCard("raider");
        var raider2 = scn.GetDSCard("raider2");

        var arena = scn.GetDSCard("arena");
        var boonta = scn.GetDSCard("boonta");

        var podracer = scn.GetLSCard("podracer");
        var bay = scn.GetLSCard("bay");

        scn.StartGame();
        scn.MoveLocationToTable(bay);
        scn.MoveLocationToTable(arena);

        scn.MoveCardsToLocation(bay,raider2);
        scn.MoveCardsToLocation(arena,raider,podracer,boonta);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSUseCardAction(boonta); //start podrace
        scn.PassAllResponses();

        scn.LSPlayCard(boonta); //draw race destiny
        scn.PassAllResponses();
        scn.LSChooseYes(); //stack first race destiny

        assertTrue(scn.DSCardActionAvailable(raider, "Shoot")); //test1
        assertFalse(scn.DSCardActionAvailable(raider2, "Shoot")); //test2
    }

    @Test
    public void TuskenRaiderShootSucceedsDestinyMoreThan3() {
        //test2: successful shoot (destiny > 3) causes race destiny to go to LS hand
        var scn = GetScenario();

        var raider = scn.GetDSCard("raider");

        var arena = scn.GetDSCard("arena");
        var boonta = scn.GetDSCard("boonta");

        var podracer = scn.GetLSCard("podracer");

        scn.StartGame();
        scn.MoveLocationToTable(arena);

        scn.MoveCardsToLocation(arena,raider,podracer,boonta);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSUseCardAction(boonta); //start podrace
        scn.PassAllResponses();

        scn.LSPlayCard(boonta); //draw race destiny
        scn.PassAllResponses();
        scn.LSChooseYes(); //stack first race destiny

        assertEquals(0, scn.GetDSUsedPileCount());
        assertEquals(0, scn.GetLSHandCount());
        scn.PrepareDSDestiny(4); //for success
        scn.DSUseCardAction(raider,"Shoot");

        scn.PassAllResponses();

        assertEquals(4, scn.GetDSUsedPileCount()); //test1 (3 force cost + destiny)
        assertEquals(1, scn.GetLSHandCount()); //test2
    }

    @Test
    public void TuskenRaiderShootFailsDestiny3() {
        //test1: failed shoot (destiny 3) does not cause race destiny to go to hand
        var scn = GetScenario();

        var raider = scn.GetDSCard("raider");

        var arena = scn.GetDSCard("arena");
        var boonta = scn.GetDSCard("boonta");

        var podracer = scn.GetLSCard("podracer");

        scn.StartGame();
        scn.MoveLocationToTable(arena);

        scn.MoveCardsToLocation(arena,raider,podracer,boonta);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSUseCardAction(boonta); //start podrace
        scn.PassAllResponses();

        scn.LSPlayCard(boonta); //draw race destiny
        scn.PassAllResponses();
        scn.LSChooseYes(); //stack first race destiny

        scn.PrepareDSDestiny(3); //for fail
        scn.DSUseCardAction(raider,"Shoot");

        scn.PassAllResponses();

        assertEquals(0, scn.GetLSHandCount()); //test2
    }

    @Test
    public void TuskenRaiderAtPodraceArenaMayNotShootTwice() {
        //test1: raider at arena cannot take second 'shoot' action this turn
        //test2: second raider at arena cannot take first 'shoot' action after 'shoot' action was taken
        var scn = GetScenario();

        var raider = scn.GetDSCard("raider");
        var raider2 = scn.GetDSCard("raider2");

        var arena = scn.GetDSCard("arena");
        var boonta = scn.GetDSCard("boonta");

        var podracer = scn.GetLSCard("podracer");

        scn.StartGame();
        scn.MoveLocationToTable(arena);

        scn.MoveCardsToLocation(arena,raider,raider2,podracer,boonta);

        scn.SkipToPhase(Phase.CONTROL);

        scn.DSActivateForceCheat(3);
        assertEquals(6, scn.GetDSForcePileCount()); //enough to pay for 'shoot' action twice

        scn.DSUseCardAction(boonta); //start podrace
        scn.PassAllResponses();

        scn.LSPlayCard(boonta); //draw race destiny
        scn.PassAllResponses();
        scn.LSChooseYes(); //stack first race destiny

        assertTrue(scn.DSCardActionAvailable(raider, "Shoot"));
        assertTrue(scn.DSCardActionAvailable(raider2, "Shoot"));

        scn.PrepareDSDestiny(3); //for fail
        scn.DSUseCardAction(raider,"Shoot");
        scn.PassAllResponses();

        scn.LSChooseYes(); //stack second race destiny
        assertEquals(3, scn.GetDSForcePileCount()); //enough to pay
        assertTrue(scn.GetDSReserveDeckCount() >= 1); //enough to draw destiny
        assertFalse(scn.DSCardActionAvailable(raider, "Shoot")); //test1
        assertFalse(scn.DSCardActionAvailable(raider2, "Shoot")); //test2
    }

    @Test
    public void TuskenRaiderMayShootAgainNextTurn() {
        //test1: raider at arena can take 'shoot' action in later turn
        var scn = GetScenario();

        var raider = scn.GetDSCard("raider");

        var arena = scn.GetDSCard("arena");
        var boonta = scn.GetDSCard("boonta");

        var podracer = scn.GetLSCard("podracer");

        scn.StartGame();
        scn.MoveLocationToTable(arena);

        scn.MoveCardsToLocation(arena,raider,podracer,boonta);

        scn.SkipToPhase(Phase.CONTROL);

        scn.DSUseCardAction(boonta); //start podrace
        scn.PassAllResponses();

        scn.LSPlayCard(boonta); //draw race destiny
        scn.PassAllResponses();
        scn.LSChooseYes(); //stack first race destiny

        scn.PrepareDSDestiny(3); //for fail
        scn.DSUseCardAction(raider,"Shoot");
        scn.PassAllResponses();

        scn.LSChooseYes(); //stack second race destiny
        scn.PassAllResponses();

        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPass();

        scn.LSPlayCard(boonta); //draw race destiny
        scn.PassAllResponses();
        scn.LSChooseYes(); //stack first race destiny

        assertTrue(scn.DSCardActionAvailable(raider, "Shoot")); //test1
    }

    @Test
    public void TuskenRaiderCannotShootOwnRaceDestiny() {
        //test1: raider at arena can use shoot action on own just stacked race destiny
        var scn = GetScenario();

        var raider = scn.GetDSCard("raider");

        var arena = scn.GetDSCard("arena");
        var boonta = scn.GetDSCard("boonta");
        var ds_podracer = scn.GetDSCard("ds_podracer");

        scn.StartGame();
        scn.MoveLocationToTable(arena);

        scn.MoveCardsToLocation(arena,raider,ds_podracer,boonta);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSUseCardAction(boonta); //start podrace
        scn.PassAllResponses();

        scn.LSPass();

        scn.DSPlayCard(boonta); //draw race destiny
        scn.PassAllResponses();
        scn.DSChooseYes(); //stack first race destiny

        scn.LSPass(); //RACE_DESTINY_STACKED - Optional responses
        assertFalse(scn.DSCardActionAvailable(raider, "Shoot")); //test1
    }

    //other tests:
    //    works with other tusken raider cards (mix with premiere raider)
}


