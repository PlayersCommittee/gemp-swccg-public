package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled if the specified player has a Rep accepted by a specified filter.
 */
public class RepCondition implements Condition {
    private String _playerId;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled if the specified player has a Rep accepted by a specified filter.
     * @param playerId the player
     * @param filters the filter
     */
    public RepCondition(String playerId, Filterable filters) {
        _playerId = playerId;
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard rep = gameState.getRep(_playerId);
        return rep != null && Filters.and(_filters).accepts(gameState, modifiersQuerying, rep);
    }
}
