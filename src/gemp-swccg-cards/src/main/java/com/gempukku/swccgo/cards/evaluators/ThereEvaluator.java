package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns the number of cards accepted by the specified filter that are "at" the same location the
 * affected card is "at" (or "at" the location if the affected card is a location).
 */
public class ThereEvaluator extends BaseEvaluator {
    private int _permSourceCardId;
    private Filter _filters;

    /**
     * Creates an evaluator returns the number of cards accepted by the specified filter that are "at" the same location
     * the affected card is "at" (or "at" the location if the affected card is a location).
     * @param source the card that is creating this evaluator
     * @param filters the filter
     */
    public ThereEvaluator(PhysicalCard source, Filterable filters) {
        _permSourceCardId = source.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        Filter filterToUse = Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters));
        return Filters.countActive(gameState.getGame(), source, Filters.and(filterToUse, Filters.here(self)));
    }
}
