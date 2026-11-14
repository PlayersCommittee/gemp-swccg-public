package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Short-lived cache of query results for ModifiersLogic (perf/modifier-recursion). Some queries depend on each other
 * recursively (a card's state depends on modifiers, whose conditions and "affects card" filters depend on other cards'
 * states, ...), and without reuse the same question is re-derived for every card and modifier that depends on it, several
 * levels deep.
 * <p>
 * Scope: the cache is only used while a query scope is open on one thread: the outermost query, or a read-only
 * computation passed to withScope (gathering the actions of a response window, the game stats). Entries of an earlier
 * scope are never reused (_scopeId): the game state may have changed since. Within a scope the game state does not change.
 * <p>
 * Skipped modifiers: while a modifier is being evaluated it is in the skip set of ModifiersLogic, and nested queries skip
 * it, so an answer can depend on which modifiers are in the skip set. It can only depend on the ones the computation
 * actually tested for membership, and those always have a modifier type the computation queried (ModifiersLogic reports
 * them with noteSkipSetTests: getKeywordModifiersAffectingCard tests only modifiers of the requested type; the full scan of
 * getModifiersAffecting tests all types). Each computation records the modifier types queried by it and by everything it
 * called or reused (_frames); an entry is reused only when the modifiers of those types in the skip set are exactly the
 * same as when it was computed (Entry.matches), besides the same scope, game state object and card (or modifier) object.
 * Then every membership test inside the computation would have the same outcome, so it would return the same answer.
 * <p>
 * Not part of the game state: not copied into snapshots.
 * <p>
 * The JVM option -Dgemp.modifierQueryCache=false turns the cache off: every query is then computed, as without the cache
 * (to compare results or timings).
 */
final class ModifierQueryCache {
    private static final int QUERY_MODIFIERS = 0;
    private static final int QUERY_CARD_STATE = 1;
    private static final int QUERY_CONDITION = 2;
    private static final Modifier[] NO_MODIFIERS = new Modifier[0];
    private static final int MAX_ENTRIES_PER_KEY = 8;

    private static final int SCOPE_NONE = 0;
    private static final int SCOPE_OPENED = 1;
    private static final int SCOPE_INSIDE = 2;

    // false: -Dgemp.modifierQueryCache=false, nothing is cached (see the class description)
    static final boolean ENABLED_BY_DEFAULT = !"false".equals(System.getProperty("gemp.modifierQueryCache"));

    /**
     * The question asked: the kind of query and its arguments.
     */
    record Key(int kind, int a, int b, int cardId) {
    }

    // The modifiers skipped by the queries in progress (owned and changed by ModifiersLogic, only read here). Iterated:
    // should be a LinkedHashSet.
    private final Set<Modifier> _skipSet;
    private final boolean _enabled;
    // The thread that holds the query scope (null: none). Only that thread reads or writes the cache.
    private final AtomicReference<Thread> _scopeOwner = new AtomicReference<Thread>();
    private long _scopeId;
    private Map<Key, Entry> _entries = new HashMap<Key, Entry>();
    // The modifier types queried by each cached computation in progress (innermost last).
    private final ArrayList<EnumSet<ModifierType>> _frames = new ArrayList<EnumSet<ModifierType>>();

    private static final class Entry {
        private final long _scopeId;
        private final GameState _gameState;
        private final Object _subject; // the card, or the modifier whose condition was checked
        private final EnumSet<ModifierType> _typesQueried;
        private final Modifier[] _skippedOfTypesQueried; // the modifiers in the skip set with a type in _typesQueried
        private final Object _value;
        private Entry _next; // another answer to the same question, e.g. computed with other modifiers skipped

        private Entry(long scopeId, GameState gameState, Object subject, EnumSet<ModifierType> typesQueried, Modifier[] skippedOfTypesQueried, Object value) {
            _scopeId = scopeId;
            _gameState = gameState;
            _subject = subject;
            _typesQueried = typesQueried;
            _skippedOfTypesQueried = skippedOfTypesQueried;
            _value = value;
        }

        private boolean matches(long scopeId, GameState gameState, Object subject, Set<Modifier> skipSet) {
            if (_scopeId != scopeId || _gameState != gameState || _subject != subject)
                return false;
            int relevant = 0;
            for (Modifier modifier : skipSet) {
                if (_typesQueried.contains(modifier.getModifierType())) {
                    relevant++;
                    boolean found = false;
                    for (Modifier skipped : _skippedOfTypesQueried) {
                        if (skipped == modifier) {
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        return false;
                }
            }
            return relevant == _skippedOfTypesQueried.length;
        }
    }

    /**
     * @param skipSet the modifiers skipped by the queries in progress, kept up to date by the caller
     */
    ModifierQueryCache(Set<Modifier> skipSet) {
        this(skipSet, ENABLED_BY_DEFAULT);
    }

    /**
     * @param skipSet the modifiers skipped by the queries in progress, kept up to date by the caller
     * @param enabled false: nothing is cached, every query is computed
     */
    ModifierQueryCache(Set<Modifier> skipSet, boolean enabled) {
        _skipSet = skipSet;
        _enabled = enabled;
    }

    static Key modifiersKey(ModifierType modifierType, Keyword keyword, PhysicalCard card) {
        return new Key(QUERY_MODIFIERS, modifierType.ordinal(), keyword == null ? -1 : keyword.ordinal(), card == null ? -1 : card.getCardId());
    }

    static Key cardStateKey(int flags, PhysicalCard card) {
        return new Key(QUERY_CARD_STATE, flags, 0, card.getCardId());
    }

    static Key conditionKey(Modifier modifier) {
        return new Key(QUERY_CONDITION, modifier.hashCode(), 0, -1);
    }

    /**
     * Performs a query, reusing an earlier answer to the same question of the current query scope when there is one. The
     * outermost query of a scope (nothing to reuse yet), and a query while another thread holds the scope, are computed
     * without the cache.
     * @param copy makes a copy of a mutable answer, so the cached one is not shared (null: the answer is immutable)
     */
    @SuppressWarnings("unchecked")
    <T> T query(Key key, GameState gameState, Object subject, UnaryOperator<T> copy, Supplier<T> computation) {
        int scope = enterScope();
        if (scope != SCOPE_INSIDE) {
            try {
                return computation.get();
            } finally {
                if (scope == SCOPE_OPENED)
                    closeScope();
            }
        }
        Entry entry = findEntry(key, gameState, subject);
        if (entry != null)
            return copy == null ? (T) entry._value : copy.apply((T) entry._value);
        EnumSet<ModifierType> frame = openFrame();
        T result;
        try {
            result = computation.get();
        } finally {
            closeFrame(frame);
        }
        storeEntry(key, gameState, subject, frame, copy == null ? result : copy.apply(result));
        return result;
    }

    /**
     * Performs a computation that only reads the game state in one query scope (see ModifiersQuerying.withQueryCache).
     */
    <T> T withScope(Supplier<T> readOnlyComputation) {
        int scope = enterScope();
        try {
            return readOnlyComputation.get();
        } finally {
            if (scope == SCOPE_OPENED)
                closeScope();
        }
    }

    /**
     * Records that the computation in progress tests modifiers of the specified type (null: of all types) for membership
     * of the skip set. Only for the thread that holds the query scope: another thread's queries are not cached.
     */
    void noteSkipSetTests(ModifierType modifierType) {
        if (_scopeOwner.get() == Thread.currentThread() && !_frames.isEmpty()) {
            EnumSet<ModifierType> frame = _frames.get(_frames.size() - 1);
            if (modifierType != null)
                frame.add(modifierType);
            else
                frame.addAll(EnumSet.allOf(ModifierType.class));
        }
    }

    /**
     * @return SCOPE_OPENED if this call opened the query scope (the caller must call closeScope when done), SCOPE_INSIDE
     * if the current thread already holds it, SCOPE_NONE if another thread holds it or the cache is disabled
     */
    private int enterScope() {
        if (!_enabled)
            return SCOPE_NONE;
        Thread thread = Thread.currentThread();
        if (_scopeOwner.get() == thread)
            return SCOPE_INSIDE;
        if (!_scopeOwner.compareAndSet(null, thread))
            return SCOPE_NONE;
        _scopeId++;
        return SCOPE_OPENED;
    }

    private void closeScope() {
        // Entries of this scope are never reused (_scopeId); drop a large table rather than keep it around.
        if (_entries.size() > 4096)
            _entries = new HashMap<Key, Entry>();
        _frames.clear();
        _scopeOwner.set(null);
    }

    private Entry findEntry(Key key, GameState gameState, Object subject) {
        Entry entry = _entries.get(key);
        while (entry != null && !entry.matches(_scopeId, gameState, subject, _skipSet))
            entry = entry._next;
        if (entry == null)
            return null;
        // Reusing the answer makes the computation in progress depend on the same skipped modifiers.
        if (!_frames.isEmpty())
            _frames.get(_frames.size() - 1).addAll(entry._typesQueried);
        return entry;
    }

    private EnumSet<ModifierType> openFrame() {
        EnumSet<ModifierType> frame = EnumSet.noneOf(ModifierType.class);
        _frames.add(frame);
        return frame;
    }

    private void closeFrame(EnumSet<ModifierType> frame) {
        _frames.remove(_frames.size() - 1);
        if (!_frames.isEmpty())
            _frames.get(_frames.size() - 1).addAll(frame);
    }

    private void storeEntry(Key key, GameState gameState, Object subject, EnumSet<ModifierType> frame, Object value) {
        Modifier[] skipped = NO_MODIFIERS;
        if (!_skipSet.isEmpty()) {
            List<Modifier> relevant = new ArrayList<Modifier>(_skipSet.size());
            for (Modifier modifier : _skipSet) {
                if (frame.contains(modifier.getModifierType()))
                    relevant.add(modifier);
            }
            skipped = relevant.toArray(NO_MODIFIERS);
        }
        Entry entry = new Entry(_scopeId, gameState, subject, frame, skipped, value);
        // Keep up to MAX_ENTRIES_PER_KEY answers per question of this scope (newest first), drop the rest.
        Entry previous = _entries.put(key, entry);
        int kept = 1;
        for (Entry e = previous; e != null && e._scopeId == _scopeId && kept < MAX_ENTRIES_PER_KEY; e = e._next, kept++) {
            entry._next = e;
            entry = e;
        }
        entry._next = null;
    }
}
