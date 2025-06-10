package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns the number of locations accepted by the specified filter that the specified player controls.
 */
public class ControlsEvaluator extends BaseEvaluator {
    private String _playerId;
    private Filter _filters;

    /**
     * Creates an evaluator that returns the number of locations accepted by the specified filter that the specified player
     * controls.
     * @param playerId the player
     * @param filters the filter
     */
    public ControlsEvaluator(String playerId, Filterable filters) {
        _playerId = playerId;
        _filters = Filters.and(filters);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return Filters.countTopLocationsOnTable(gameState.getGame(), Filters.and(_filters, Filters.controls(_playerId)));
    }
}
