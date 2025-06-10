package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An interface used to represent a condition that can be checked as to whether it is fulfilled or not.
 */
public interface Condition {
    boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying);
}
