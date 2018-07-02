package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * This interface represents a filter that each card is either accepted by or not.
 * Each filter that implements this interface defines which cards the filter accepts with the accepts method.
 */
public interface FilterInterface extends Filterable {
    boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    int acceptsCount(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
}
