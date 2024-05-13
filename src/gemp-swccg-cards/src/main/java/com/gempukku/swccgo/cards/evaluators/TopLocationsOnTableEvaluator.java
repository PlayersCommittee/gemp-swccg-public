package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the number of top locations on the table accepted by the specified filter.
 */
public class TopLocationsOnTableEvaluator extends BaseEvaluator {
    private Filter _filters;

    /**
     * Creates an evaluator that returns the number of top locations on the table accepted by the specified filter.
     * @param filters the filter
     */
    public TopLocationsOnTableEvaluator(Filterable filters) {
        _filters = Filters.and(filters);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return Filters.countTopLocationsOnTable(gameState.getGame(), _filters);
    }
}
