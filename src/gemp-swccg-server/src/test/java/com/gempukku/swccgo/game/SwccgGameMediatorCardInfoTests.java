package com.gempukku.swccgo.game;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersLogic;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Card info is requested by the players and spectators of a game on several threads at once. Its modifier queries must not
 * leave a modifier in the skip set of ModifiersLogic, which would make it lost for the rest of the game (#911, #999).
 */
public class SwccgGameMediatorCardInfoTests {
    private static final int THREADS = 8;
    private static final int REQUESTS_PER_THREAD = 3000;

    @Test
    public void concurrentCardInfoRequestsLoseNoModifier() throws Exception {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("chewie", "8_2");
                    put("bowcaster", "8_86");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
        var chewie = scn.GetLSCard("chewie");
        var bowcaster = scn.GetLSCard("bowcaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, chewie, trooper);
        scn.AttachCardsTo(chewie, bowcaster);

        // Chewbacca's Bowcaster fires for free when used by Chewbacca.
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.FIRES_FOR_FREE, bowcaster).isEmpty());

        SwccgGameMediator mediator = mediatorFor(scn.game());
        Player player = mock(Player.class);
        when(player.getName()).thenReturn(chewie.getOwner());
        int[] cardIds = {chewie.getCardId(), bowcaster.getCardId(), trooper.getCardId(), site.getCardId()};

        List<Throwable> failures = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch start = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < THREADS; t++) {
            int offset = t;
            Thread thread = new Thread(() -> {
                try {
                    start.await();
                    for (int i = 0; i < REQUESTS_PER_THREAD; i++)
                        mediator.produceCardInfo(player, cardIds[(i + offset) % cardIds.length]);
                } catch (Throwable e) {
                    failures.add(e);
                }
            });
            thread.start();
            threads.add(thread);
        }
        start.countDown();
        for (Thread thread : threads)
            thread.join();

        assertEquals("card info requests failed: " + failures, 0, failures.size());
        assertTrue("modifiers left in the skip set: " + skipSet(scn.game()), skipSet(scn.game()).isEmpty());
        assertFalse("Chewbacca's Bowcaster no longer fires for free",
                scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.FIRES_FOR_FREE, bowcaster).isEmpty());
    }

    /**
     * A mediator for the game of a test scenario (the mediator creates its own game: replaced).
     */
    private static SwccgGameMediator mediatorFor(SwccgGame game) throws Exception {
        SwccgGameMediator mediator = new SwccgGameMediator("1", mock(SwccgFormat.class), null,
                new SwccgGameParticipant[]{new SwccgGameParticipant(game.getLightPlayer(), new SwccgDeck("light")),
                        new SwccgGameParticipant(game.getDarkPlayer(), new SwccgDeck("dark"))},
                mock(SwccgCardBlueprintLibrary.class), 3600, true, false, false, false, 60, false, false);
        Field gameField = SwccgGameMediator.class.getDeclaredField("_swccgoGame");
        gameField.setAccessible(true);
        gameField.set(mediator, game);
        return mediator;
    }

    @SuppressWarnings("unchecked")
    private static Set<Modifier> skipSet(SwccgGame game) throws Exception {
        Field skipSetField = ModifiersLogic.class.getDeclaredField("_skipSet");
        skipSetField.setAccessible(true);
        return (Set<Modifier>) skipSetField.get(game.getModifiersQuerying());
    }
}
