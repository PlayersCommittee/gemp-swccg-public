package com.gempukku.swccgo.rules.game_end;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameEndTests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                }},
                new HashMap<>()
                {{
                    put("vader", "1_168");
                    put("visage","4_135"); //Visage of the Emperor
                    put("holo","4_161"); //Executor: Holotheatre
                    put("boba","13_059"); //Boba Fett, Bounty Hunter (maintenance)
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.BHBMObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    //Life Force
    //Your Reserve Deck, Force Pile and Used Pile. If these
    //three piles are totally depleted, you lose the game! Note
    //that Unresolved Destiny Draws (see entry, Ch. 1) are
    //also considered to be a part of your Life Force, as well
    //as your sabacc hand (see Appendix C). Cards in your
    //hand, on table or in the Lost Pile are not counted as
    //part of your Life Force.

    @Test
    public void DSLosesIfDSLifeForceDepletedDuringLSTurn() {
        //test1: DS losing final life force to LS force drain results in game end, DS loss
        var scn = GetScenario();

        var trooper = scn.GetLSFiller(1);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, trooper);

        final int startingDSLifeForce = scn.GetDSReserveDeckCount();

        //cheat deplete all but 1 life force
        for(int i = 0; i < (startingDSLifeForce - 1); i++) {
            scn.MoveCardsToTopOfDSLostPile(scn.GetTopOfDSReserveDeck());
        }

        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(1,scn.GetDSReserveDeckCount());
        assertEquals(1,scn.GetDSLifeForceRemaining());
        scn.LSForceDrainAt(site);
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetTopOfDSReserveDeck());

        assertTrue(scn.GameIsFinished());
        assertTrue(scn.LSWonGame()); //test1
    }

    @Test
    public void DSLosesIfDSLifeForceDepletedDuringDSTurn() {
        //test1: DS losing final life force to battle damage during DS turn results in game end, DS loss
        //could rework to simplify
        var scn = GetScenario();

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);
        var trooper3 = scn.GetLSFiller(3);

        var trooper = scn.GetDSFiller(1);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, trooper1, trooper2, trooper3, trooper);

        final int startingDSLifeForce = scn.GetDSReserveDeckCount();

        //cheat deplete all but 2 life force
        for(int i = 0; i < (startingDSLifeForce - 2); i++) {
            scn.MoveCardsToTopOfDSLostPile(scn.GetTopOfDSReserveDeck());
        }
        scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck()); //to initiate battle

        assertEquals(1,scn.GetDSForcePileCount());
        assertEquals(1,scn.GetDSReserveDeckCount());
        assertEquals(2,scn.GetDSLifeForceRemaining());

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment();
        assertEquals(2,scn.GetUnpaidDSBattleDamage());
        scn.DSPayBattleDamageFromUsedPile();
        scn.DSChooseCard(scn.GetTopOfDSReserveDeck());

        assertTrue(scn.GameIsFinished());
        assertTrue(scn.LSWonGame()); //test1
    }

    @Test
    public void LSLosesIfLSLifeForceDepletedDuringDSTurn() {
        //test1: LS losing final life force to DS force drain results in game end, LS loss
        var scn = GetScenario();

        var trooper = scn.GetDSFiller(1);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, trooper);

        final int startingLSLifeForce = scn.GetLSReserveDeckCount();

        //cheat deplete all but 1 life force
        for(int i = 0; i < (startingLSLifeForce - 1); i++) {
            scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
        }

        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(1,scn.GetLSReserveDeckCount());
        assertEquals(1,scn.GetLSLifeForceRemaining());
        scn.DSForceDrainAt(site);
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());

        assertTrue(scn.GameIsFinished());
        assertTrue(scn.DSWonGame()); //test1
    }

    @Test
    public void LSLosesIfLSLifeForceDepletedDuringEndOfDSTurn() {
        //test1: LS losing final life force to damage during DS end of turn results in game end, LS loss
        var scn = GetScenario();

        var visage = scn.GetDSCard("visage");
        var holo = scn.GetDSCard("holo");

        scn.StartGame();

        scn.MoveLocationToTable(holo);
        scn.MoveCardsToLocation(holo, visage);

        scn.SkipToPhase(Phase.ACTIVATE);

        final int startingLSLifeForce = scn.GetLSReserveDeckCount();

        //cheat deplete all but 1 life force
        for(int i = 0; i < (startingLSLifeForce - 1); i++) {
            scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
        }

        scn.SkipToPhase(Phase.DRAW);
        assertEquals(1,scn.GetLSLifeForceRemaining());

        scn.DSPass();
        scn.LSPass();

        scn.PassAllResponses(); //FORCE_LOSS_INITIATED - Optional responses
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());

        assertTrue(scn.GameIsFinished());
        assertTrue(scn.DSWonGame()); //test1
    }

    @Test
    public void DSLosesIfDSLifeForceDepletedDuringEndOfDSTurn() {
        //test1: DS losing final life force to damage during DS end of turn results in game end, DS loss
        var scn = GetScenario();

        var visage = scn.GetDSCard("visage");
        var holo = scn.GetDSCard("holo");

        scn.StartGame();

        scn.MoveLocationToTable(holo);
        scn.MoveCardsToLocation(holo, visage);

        scn.SkipToPhase(Phase.ACTIVATE);

        final int startingDSLifeForce = scn.GetDSReserveDeckCount();

        //cheat deplete all but 1 life force
        for(int i = 0; i < (startingDSLifeForce - 1); i++) {
            scn.MoveCardsToTopOfDSLostPile(scn.GetTopOfDSReserveDeck());
        }

        scn.SkipToPhase(Phase.DRAW);
        assertEquals(1,scn.GetDSLifeForceRemaining());

        scn.DSPass();
        scn.LSPass();

        scn.PassAllResponses(); //FORCE_LOSS_INITIATED - Optional responses
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetTopOfDSReserveDeck());

        assertTrue(scn.GameIsFinished());
        assertTrue(scn.LSWonGame()); //test1
    }

    @Test
    public void DSLosesIfDSLifeForceDepletedDuringEndOfDSTurnMaintenanceCost() {
        //test1: DS losing final life force during maintenance cost payment results in game end, DS loss
        var scn = GetScenario();

        var boba = scn.GetDSCard("boba");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, boba);

        scn.SkipToPhase(Phase.ACTIVATE);

        final int startingDSLifeForce = scn.GetDSReserveDeckCount();

        //cheat deplete all but 1 life force
        for(int i = 0; i < (startingDSLifeForce - 1); i++) {
            scn.MoveCardsToTopOfDSLostPile(scn.GetTopOfDSReserveDeck());
        }

        scn.SkipToPhase(Phase.DRAW);
        assertEquals(1,scn.GetDSLifeForceRemaining());

        scn.DSPass();
        scn.LSPass();

        scn.DSChoose("lose"); //maintenance cost: lose 2 force
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetTopOfDSReserveDeck()); //first force loss

        assertEquals(0,scn.GetDSLifeForceRemaining());
        assertTrue(scn.GameIsFinished());
        assertTrue(scn.LSWonGame()); //test1
    }

    @Test
    public void LSLosesIfLSLifeForceDepletedDuringStartOfTurn() {
        //test1: LS losing final life force during start of DS turn results in game end, LS loss
        //shows fixed:
        // https://github.com/PlayersCommittee/gemp-swccg-public/issues/328
        // https://github.com/PlayersCommittee/gemp-swccg-public/issues/842

        var scn = GetScenario();

        var bhbm = scn.GetDSCard("bhbm");
        var destiny = scn.GetDSCard("destiny");

        var vader = scn.GetDSCard("vader");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, vader);

        scn.SkipToPhase(Phase.ACTIVATE);

        assertTrue(scn.IsAttachedTo(bhbm,destiny));

        final int startingLSLifeForce = scn.GetLSReserveDeckCount();

        //cheat deplete all but 1 life force
        for(int i = 0; i < (startingLSLifeForce - 1); i++) {
            scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
        }

        scn.SkipToLSTurn();
        scn.SkipToDSTurn();

        assertEquals(1,scn.GetLSLifeForceRemaining());

        scn.PassAllResponses();

        scn.LSChooseCard(scn.GetTopOfLSReserveDeck()); //first force loss
        assertEquals(0,scn.GetLSLifeForceRemaining());
        assertTrue(scn.GameIsFinished());
        assertTrue(scn.DSWonGame()); //test1
    }

    //add other game end tests
    //  does not end if last card in reserve is drawn during sabacc
    //  does not end if last life force is part of unresolved destiny

    //  could use visage -> ICBW -> sense -> DODN to cause additional force loss during between turns phase
}
