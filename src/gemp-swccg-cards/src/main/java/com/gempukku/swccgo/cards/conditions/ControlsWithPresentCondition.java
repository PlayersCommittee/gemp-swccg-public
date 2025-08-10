package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified player controls the specified location with a card accepted by the
 * specified filter present.
 */
public class ControlsWithPresentCondition implements Condition {
    private String _playerId;
    private Integer _permLocationCardId;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when the specified player controls the specified location with a card accepted
     * by the specified filter present.
     * @param playerId the player
     * @param location the location
     * @param filters the filter
     */
    public ControlsWithPresentCondition(String playerId, PhysicalCard location, Filterable filters) {
        _playerId = playerId;
        _permLocationCardId = location.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard location = gameState.findCardByPermanentId(_permLocationCardId);

        return Filters.controls(_playerId).accepts(gameState, modifiersQuerying, location)
                && Filters.canSpot(gameState.getGame(), location, Filters.and(_filters, Filters.owner(_playerId), Filters.present(location)));
    }
}
