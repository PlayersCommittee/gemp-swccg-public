package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.common.GameEndReason;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.TestBase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameCommunicationChannel;
import com.gempukku.swccgo.game.state.GameEvent;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Guards the "compute the display-only game stats once per decision, not once per loop iteration"
 * behaviour of {@link TurnProcedure#carryOutPendingActionsUntilDecisionNeeded()}.
 *
 * The stats recompute is a full modifier-system sweep of both players' cards. It used to run on
 * every iteration of the processing loop, and a Force activation spends 13 loop iterations per
 * Force, so activating 18 Force ran it 241 times for one decision. It now runs once on the way
 * out of the loop.
 *
 * These tests assert both halves of that: the stats are still computed and still reach the client
 * at every point where the engine stops, and the number of computations no longer grows with the
 * amount of work the engine did while it was running.
 */
public class GameStatsUpdateFrequencyTest {

    /** Enough filler for a reserve deck that can pay for an 18 Force activation. */
    private static final int FILLER = 60;

    /**
     * A GameStats that counts how many times the engine recomputed it. Installed in place of the
     * TurnProcedure's own instance; every other behaviour is the real one.
     */
    private static final class CountingGameStats extends GameStats {
        private int computations;

        @Override
        public boolean updateGameStats(SwccgGame game) {
            computations++;
            return super.updateGameStats(game);
        }
    }

    /** The scenario plus the two counters attached to it. */
    private static final class Fixture {
        private final VirtualTableScenario scn;
        private final CountingGameStats stats;
        private final GameCommunicationChannel channel;

        private Fixture(VirtualTableScenario scn, CountingGameStats stats, GameCommunicationChannel channel) {
            this.scn = scn;
            this.stats = stats;
            this.channel = channel;
        }

        /** Forgets everything recorded so far, so the next assertion covers one decision only. */
        private void resetCounters() {
            stats.computations = 0;
            channel.consumeGameEvents();
        }

        private int statsEventsSentToClient() {
            int count = 0;
            for (GameEvent event : channel.consumeGameEvents())
                if (event.getType() == GameEvent.Type.GS)
                    count++;
            return count;
        }
    }

    /**
     * Two starting locations, big reserve decks, and Dark Side total Force generation raised high
     * enough that the Activate-phase decision will accept an answer of 18. The generation modifier
     * has to be applied before the game starts: the maximum is baked into the decision when the
     * action list is gathered.
     */
    private Fixture buildFixture() {
        VirtualTableScenario scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>(), FILLER, FILLER,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                VirtualTableScenario.Open);

        CountingGameStats stats = installCountingGameStats(scn);

        scn.ApplyAdHocModifier(new TotalForceGenerationModifier(null, 60, TestBase.DS));
        scn.StartGame();

        // A real client channel, so the assertions about what the client sees are made against the
        // same listener production uses. Registering it replays the whole board, which is drained
        // by the first resetCounters() call.
        GameCommunicationChannel channel = new GameCommunicationChannel(TestBase.DS, 1);
        scn.game().addGameStateListener(TestBase.DS, channel);

        Fixture fixture = new Fixture(scn, stats, channel);
        fixture.resetCounters();
        return fixture;
    }

    /**
     * Swaps the TurnProcedure's GameStats for a counting subclass. The field is private and there
     * is no injection point, so this is done reflectively; a rename fails the test loudly rather
     * than silently counting nothing.
     */
    private static CountingGameStats installCountingGameStats(VirtualTableScenario scn) {
        try {
            Field turnProcedureField = DefaultSwccgGame.class.getDeclaredField("_turnProcedure");
            turnProcedureField.setAccessible(true);
            TurnProcedure turnProcedure = (TurnProcedure) turnProcedureField.get(scn.game());

            Field gameStatsField = TurnProcedure.class.getDeclaredField("_gameStats");
            gameStatsField.setAccessible(true);
            CountingGameStats counting = new CountingGameStats();
            gameStatsField.set(turnProcedure, counting);
            return counting;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Could not install the counting GameStats. If DefaultSwccgGame._turnProcedure"
                    + " or TurnProcedure._gameStats was renamed, update this test.", e);
        }
    }

    /**
     * The number of stats recomputations for a decision must not scale with the engine work that
     * decision causes. Activating 18 Force is answered by two players, so two calls into the
     * processing loop, so two recomputations. Before the change this same activation recomputed
     * the stats 241 times (13 loop iterations per Force plus a fixed 7).
     */
    @Test
    public void statsAreNotRecomputedPerLoopIteration() {
        Fixture fixture = buildFixture();
        VirtualTableScenario scn = fixture.scn;

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        assertTrue(scn.DSActionAvailable("Activate Force"));
        scn.DSChooseAction("Activate Force");
        assertTrue("maxToActivate was " + scn.DSGetChoiceMax(), scn.DSGetChoiceMax() >= 18);

        int reserveBefore = scn.GetDSReserveDeckCount();
        int forcePileBefore = scn.GetDSForcePileCount();
        fixture.resetCounters();

        scn.DSDecided(18);
        assertTrue(scn.LSDecisionAvailable("Choose amount of Force to allow opponent to activate"));
        scn.LSDecided(18);

        assertEquals("18 Force should have been activated", forcePileBefore + 18, scn.GetDSForcePileCount());
        assertEquals("18 cards should have left the reserve deck", reserveBefore - 18, scn.GetDSReserveDeckCount());

        assertEquals("one stats recomputation per answer, regardless of how much Force was activated",
                2, fixture.stats.computations);
        int statsEvents = fixture.statsEventsSentToClient();
        assertTrue("the client should get at most one stats update per answer, got " + statsEvents,
                statsEvents <= 2);
    }

    /**
     * The same activation one Force at a time costs the same number of recomputations as eighteen
     * at a time: the count follows the number of decisions, not the number of Force.
     */
    @Test
    public void statsRecomputationsDoNotGrowWithTheAmountOfForceActivated() {
        Fixture one = buildFixture();
        one.scn.DSChooseAction("Activate Force");
        one.resetCounters();
        one.scn.DSDecided(1);

        Fixture eighteen = buildFixture();
        eighteen.scn.DSChooseAction("Activate Force");
        eighteen.resetCounters();
        eighteen.scn.DSDecided(18);
        assertTrue(eighteen.scn.LSDecisionAvailable("Choose amount of Force to allow opponent to activate"));
        eighteen.scn.LSDecided(18);

        assertEquals("activating 1 Force takes one answer and one recomputation", 1, one.stats.computations);
        assertEquals("activating 18 Force takes two answers and two recomputations",
                2, eighteen.stats.computations);
    }

    /**
     * When the engine stops and hands a decision back to a player, the stats have been recomputed
     * and the change has been sent to the client. This is the coalesced update that replaces the
     * per-iteration ones.
     */
    @Test
    public void statsAreSentWhenTheEngineStopsForADecision() {
        Fixture fixture = buildFixture();
        VirtualTableScenario scn = fixture.scn;

        scn.DSChooseAction("Activate Force");
        fixture.resetCounters();

        // One Force is activated on the Dark Side answer itself; the engine then stops again.
        scn.DSDecided(1);

        assertTrue("the engine stopped at a pending decision",
                scn.DSGetDecision() != null || scn.LSGetDecision() != null);
        assertEquals("the stats are recomputed once, at the point the engine stops",
                1, fixture.stats.computations);
        assertEquals("the client is told about the new Force pile", 1, fixture.statsEventsSentToClient());
    }

    /**
     * Declining the Activate-phase action window raises the "you have not activated Force"
     * confirmation synchronously while the answer is being processed, so the processing loop finds
     * a decision already pending and its body never runs at all. The recompute still has to happen:
     * the state changed outside the loop. This is why the call on the way out of the loop is
     * unconditional rather than guarded on having done an iteration.
     */
    @Test
    public void statsAreRecomputedEvenWhenTheLoopBodyNeverRuns() {
        Fixture fixture = buildFixture();

        fixture.resetCounters();
        fixture.scn.DSPass();

        assertEquals(1, fixture.stats.computations);
    }

    /**
     * A decided game still gets a final stats computation, so the status bar is not left showing
     * a value from before the last thing that happened.
     */
    @Test
    public void statsAreRecomputedWhenTheGameHasAWinner() {
        Fixture fixture = buildFixture();

        fixture.resetCounters();
        fixture.scn.game().playerLost(TestBase.LS, GameEndReason.LOSS__FORCE_DEPLETED);
        fixture.scn.game().carryOutPendingActionsUntilDecisionNeeded();

        assertNotNull("the game should have a winner", fixture.scn.game().getWinner());
        assertEquals(1, fixture.stats.computations);
    }
}
