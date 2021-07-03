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
    private int _permSourceCardId;
    private Filter _filters;

    /**
     * Creates an evaluator that returns the number of cards out of play accepted by the specified filter.
     *
     * @param source  the card that is creating this evaluator
     * @param filters the filter
     */
    public OutOfPlayEvaluator(PhysicalCard source, Filterable filters) {
        _permSourceCardId = source.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        Filter filterToUse = Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters));
        return Filters.count(gameState.getOutOfPlayPile(source.getOwner()), gameState.getGame(), filterToUse);
    }
}
