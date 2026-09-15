package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Guards against the lost-modifier / AOBS skip-set leak: an exception (or snapshot)
 * while a modifier is in the recursion guard must not disable that modifier forever.
 */
class ModifiersLogicSkipSetTests {

    @Test
    void exceptionDuringModifierQueryDoesNotPermanentlySkipModifier() {
        GameState gameState = mock(GameState.class);
        PhysicalCard card = mock(PhysicalCard.class);
        when(card.getPermanentCardId()).thenReturn(1);
        when(gameState.findCardByPermanentId(any())).thenReturn(null);

        AtomicInteger calls = new AtomicInteger();
        Condition condition = (gs, mq) -> {
            if (calls.getAndIncrement() == 0) {
                throw new IllegalStateException("simulated query failure");
            }
            return true;
        };

        FiresForFreeModifier modifier = new FiresForFreeModifier(null, Filters.any, condition);
        ModifiersLogic modifiersLogic = newModifiersLogic(gameState);
        modifiersLogic.addAlwaysOnModifier(modifier);

        assertThrows(IllegalStateException.class,
                () -> modifiersLogic.getModifiersAffectingCard(gameState, ModifierType.FIRES_FOR_FREE, card));

        List<Modifier> after = modifiersLogic.getModifiersAffectingCard(gameState, ModifierType.FIRES_FOR_FREE, card);
        assertEquals(1, after.size());
        assertEquals(modifier, after.get(0));
    }

    @Test
    void nestedRemoveOfSameTypeDoesNotPermanentlySkipModifier() {
        GameState gameState = mock(GameState.class);
        PhysicalCard card = mock(PhysicalCard.class);
        when(card.getPermanentCardId()).thenReturn(1);
        when(gameState.findCardByPermanentId(any())).thenReturn(null);

        ModifiersLogic modifiersLogic = newModifiersLogic(gameState);
        FiresForFreeModifier other = new FiresForFreeModifier(null, Filters.any, (gs, mq) -> true);
        modifiersLogic.addAlwaysOnModifier(other);

        Condition condition = (gs, mq) -> {
            modifiersLogic.removeModifier(other);
            return true;
        };
        FiresForFreeModifier modifier = new FiresForFreeModifier(null, Filters.any, condition);
        modifiersLogic.addAlwaysOnModifier(modifier);

        // Iterating a copy means this query must not throw ConcurrentModificationException.
        modifiersLogic.getModifiersAffectingCard(gameState, ModifierType.FIRES_FOR_FREE, card);

        List<Modifier> after = modifiersLogic.getModifiersAffectingCard(gameState, ModifierType.FIRES_FOR_FREE, card);
        assertEquals(1, after.size());
        assertEquals(modifier, after.get(0));
    }

    @Test
    void snapshotDoesNotPersistInFlightSkipSet() throws Exception {
        GameState gameState = mock(GameState.class);
        when(gameState.findCardByPermanentId(any())).thenReturn(null);

        FiresForFreeModifier modifier = new FiresForFreeModifier(null, Filters.any, (gs, mq) -> true);
        ModifiersLogic live = newModifiersLogic(gameState);
        live.addAlwaysOnModifier(modifier);

        Field skipSetField = ModifiersLogic.class.getDeclaredField("_skipSet");
        skipSetField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Set<Modifier> liveSkipSet = (Set<Modifier>) skipSetField.get(live);
        liveSkipSet.add(modifier);

        ModifiersLogic snapshot = new ModifiersLogic();
        live.generateSnapshot(snapshot, new SnapshotData());

        @SuppressWarnings("unchecked")
        Set<Modifier> snapshotSkipSet = (Set<Modifier>) skipSetField.get(snapshot);
        assertTrue(snapshotSkipSet.isEmpty());
    }

    private static ModifiersLogic newModifiersLogic(GameState gameState) {
        SwccgGame game = mock(SwccgGame.class);
        ModifiersLogic modifiersLogic = new ModifiersLogic(game);
        when(game.getModifiersQuerying()).thenReturn(modifiersLogic);
        when(game.getGameState()).thenReturn(gameState);
        when(gameState.getGame()).thenReturn(game);
        return modifiersLogic;
    }
}
