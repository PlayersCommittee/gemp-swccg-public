package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Map;

/**
 * An evaluator that returns the number of cards on the table accepted by the specified filter.
 */
public class OnTableEvaluator extends BaseEvaluator {
    private int _permSourceCardId;
    private Map<InactiveReason, Boolean> _spotOverrides;
    private Filter _filters;

    /**
     * Creates an evaluator that returns the number of cards on the table accepted by the specified filter.
     * @param source the card that is creating this evaluator
     * @param filters the filter
     */
    public OnTableEvaluator(PhysicalCard source, Filterable filters) {
        this(source, null, filters);
    }

    /**
     * Creates an evaluator that returns the number of cards on the table accepted by the specified filter.
     * @param source the card that is creating this evaluator
     * @param spotOverrides overrides for which inactive cards are visible to this evaluator
     * @param filters the filter
     */
    public OnTableEvaluator(PhysicalCard source, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        _permSourceCardId = source.getPermanentCardId();
        _spotOverrides = spotOverrides;
        _filters = Filters.and(filters);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        Filter filterToUse = Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters));
        return Filters.countActive(gameState.getGame(), source, _spotOverrides, filterToUse);
    }
}
