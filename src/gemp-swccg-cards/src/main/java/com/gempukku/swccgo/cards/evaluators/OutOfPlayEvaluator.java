package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the number of cards out of play accepted by the specified filter.
 */
public class OutOfPlayEvaluator extends BaseEvaluator {
    private Filter _filters;
    private String _playerId;

    /**
     * Creates an evaluator that returns the number of cards out of play accepted by the specified filter.
     *
     * @param source  the card that is creating this evaluator, its owners out of play area will be evaluated
     * @param filters the filter
     */
    public OutOfPlayEvaluator(PhysicalCard source, Filterable filters) {
        _filters = Filters.and(filters);
        _playerId = source.getOwner();
    }

    /**
     * Creates an evaluator that returns the number of cards out of play accepted by the specified filter.
     *
     * @param source  the card that is creating this evaluator
     * @param filters the filter
     * @param playerId the player whose out of play area will be evaluated
     */
    public OutOfPlayEvaluator(PhysicalCard source, Filterable filters, String playerId) {
        _filters = Filters.and(filters);
        _playerId = playerId;
    }


    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        Filter filterToUse = Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters));
        return Filters.count(gameState.getOutOfPlayPile(_playerId), gameState.getGame(), filterToUse);
    }
}
