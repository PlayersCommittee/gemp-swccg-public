package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModifierQueryCacheTest {
    private static final ModifierQueryCache.Key KEY_A = new ModifierQueryCache.Key(100, 1, 0, -1);
    private static final ModifierQueryCache.Key KEY_B = new ModifierQueryCache.Key(100, 2, 0, -1);

    private final Set<Modifier> _skipSet = new LinkedHashSet<Modifier>();
    // Enabled whatever -Dgemp.modifierQueryCache says: these tests are about what the cache reuses.
    private final ModifierQueryCache _cache = new ModifierQueryCache(_skipSet, true);
    private final GameState _gameState = new GameState();
    private final Object _subject = new Object();
    private final AtomicInteger _computed = new AtomicInteger();

    /**
     * A modifier of the specified type: the cache only reads its type and identity.
     */
    private static Modifier modifier(ModifierType type) {
        return (Modifier) Proxy.newProxyInstance(Modifier.class.getClassLoader(), new Class<?>[]{Modifier.class}, (proxy, method, args) ->
                switch (method.getName()) {
                    case "getModifierType" -> type;
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    case "toString" -> "Modifier(" + type + ")";
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    /**
     * A query that counts its computations and tests modifiers of the specified type (null: none) for skip set membership.
     */
    private Integer query(ModifierQueryCache.Key key, GameState gameState, Object subject, ModifierType testsType) {
        return _cache.query(key, gameState, subject, null, () -> {
            if (testsType != null)
                _cache.noteSkipSetTests(testsType);
            return _computed.incrementAndGet();
        });
    }

    private Integer query(ModifierType testsType) {
        return query(KEY_A, _gameState, _subject, testsType);
    }

    private <T> T inScope(Supplier<T> computation) {
        return _cache.withScope(computation);
    }

    @Test
    public void notCachedOutsideScope() {
        assertEquals(1, query(null));
        assertEquals(2, query(null));
    }

    @Test
    public void notCachedWhenDisabled() {
        assertEquals(List.of("answer", "answer"), askTwiceInScope(new ModifierQueryCache(_skipSet, false)));
        assertEquals(2, _computed.get());
        _computed.set(0);
        assertEquals(List.of("answer", "answer"), askTwiceInScope(new ModifierQueryCache(_skipSet, true)));
        assertEquals(1, _computed.get());
    }

    private List<String> askTwiceInScope(ModifierQueryCache cache) {
        Supplier<String> computation = () -> {
            _computed.incrementAndGet();
            return "answer";
        };
        return cache.withScope(() -> List.of(
                cache.query(KEY_A, _gameState, _subject, null, computation),
                cache.query(KEY_A, _gameState, _subject, null, computation)));
    }

    @Test
    public void reusedWithinScope() {
        inScope(() -> {
            assertEquals(1, query(null));
            assertEquals(1, query(null));
            return null;
        });
        assertEquals(1, _computed.get());
    }

    @Test
    public void notReusedInNewScope() {
        inScope(() -> query(null));
        inScope(() -> query(null));
        assertEquals(2, _computed.get());
    }

    @Test
    public void notReusedForOtherKeySubjectOrGameState() {
        inScope(() -> {
            query(KEY_A, _gameState, _subject, null);
            query(KEY_B, _gameState, _subject, null);
            query(KEY_A, _gameState, new Object(), null);
            query(KEY_A, new GameState(), _subject, null);
            return null;
        });
        assertEquals(4, _computed.get());
    }

    @Test
    public void skippedModifierOfQueriedTypeBlocksReuse() {
        Modifier power = modifier(ModifierType.POWER);
        inScope(() -> {
            _skipSet.add(power);
            assertEquals(1, query(ModifierType.POWER));
            _skipSet.remove(power);
            assertEquals(2, query(ModifierType.POWER)); // computed with power skipped: not valid now
            _skipSet.add(power);
            assertEquals(1, query(ModifierType.POWER)); // both answers are kept
            return null;
        });
    }

    @Test
    public void skippedModifierOfOtherTypeDoesNotBlockReuse() {
        Modifier armor = modifier(ModifierType.ARMOR);
        inScope(() -> {
            assertEquals(1, query(ModifierType.POWER));
            _skipSet.add(armor);
            assertEquals(1, query(ModifierType.POWER));
            return null;
        });
    }

    @Test
    public void reusedAnswerDependsOnWhatItsComputationQueried() {
        Modifier power = modifier(ModifierType.POWER);
        inScope(() -> {
            // KEY_A tests no modifiers itself, but its computation asks KEY_B, which tests POWER modifiers.
            Supplier<Integer> outer = () -> _cache.query(KEY_A, _gameState, _subject, null, () -> {
                query(KEY_B, _gameState, _subject, ModifierType.POWER);
                return _computed.incrementAndGet();
            });
            assertEquals(2, outer.get());
            assertEquals(2, outer.get());
            _skipSet.add(power);
            assertEquals(4, outer.get()); // both recomputed with power skipped
            return null;
        });
    }

    @Test
    public void mutableAnswerIsCopied() {
        inScope(() -> {
            Supplier<List<String>> listQuery = () -> _cache.query(KEY_A, _gameState, _subject, ArrayList::new, () -> new ArrayList<String>(List.of("x")));
            listQuery.get().add("changed by caller");
            List<String> reused = listQuery.get();
            assertEquals(List.of("x"), reused);
            reused.add("changed by caller");
            assertEquals(List.of("x"), listQuery.get());
            return null;
        });
    }

    @Test
    public void otherThreadsQueryDoesNotChangeTheScopeOwnersBookkeeping() throws Exception {
        CountDownLatch inComputation = new CountDownLatch(1);
        CountDownLatch otherDone = new CountDownLatch(1);
        int[] reused = new int[1];
        Thread owner = new Thread(() -> inScope(() -> {
            // A computation that tests no modifier type, still in progress while the other thread queries.
            _cache.query(KEY_A, _gameState, _subject, null, () -> {
                inComputation.countDown();
                try {
                    otherDone.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return _computed.incrementAndGet();
            });
            // Its answer does not depend on POWER modifiers: reused although one is skipped now.
            _skipSet.add(modifier(ModifierType.POWER));
            reused[0] = query(null);
            return null;
        }));
        owner.start();
        assertTrue(inComputation.await(10, TimeUnit.SECONDS));
        try {
            assertEquals(1, query(ModifierType.POWER)); // another thread holds the scope: computed, tests POWER
        } finally {
            otherDone.countDown();
            owner.join();
        }
        assertEquals(2, reused[0]);
        assertEquals(2, _computed.get());
    }

    @Test
    public void computedWithoutCacheWhileAnotherThreadHoldsScope() throws Exception {
        CountDownLatch scopeHeld = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(1);
        Thread other = new Thread(() -> inScope(() -> {
            scopeHeld.countDown();
            try {
                done.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return null;
        }));
        other.start();
        assertTrue(scopeHeld.await(10, TimeUnit.SECONDS));
        try {
            inScope(() -> {
                assertEquals(1, query(null));
                assertEquals(2, query(null));
                return null;
            });
        } finally {
            done.countDown();
            other.join();
        }
        // Once released, this thread can open the scope again.
        inScope(() -> {
            assertEquals(3, query(null));
            assertEquals(3, query(null));
            return null;
        });
    }
}
