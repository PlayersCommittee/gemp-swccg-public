package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to total of a card-specific calculation when a specified card is targeted by that action performing the calculation.
 */
public class CalculationTotalWhenTargetedModifier extends AbstractModifier {
    protected Evaluator _evaluator;
    protected Filter _actionSourceFilter;

    /**
     * Creates a modifier to total of a card-specific calculation when the source card is targeted by an action with the
     * card accepted by the action source filter if the source of the action.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param actionSourceFilter the filter for the card that is the source of the action performing the calculation
     */
    public CalculationTotalWhenTargetedModifier(PhysicalCard source, Condition condition, int modifierAmount, Filterable actionSourceFilter) {
        this(source, source, condition, modifierAmount, actionSourceFilter);
    }

    /**
     * Creates a modifier to total of a card-specific calculation when a card accepted by the filter is targeted by an action with the
     * card accepted by the action source filter if the source of the action.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param actionSourceFilter the filter for the card that is the source of the action performing the calculation
     */
    private CalculationTotalWhenTargetedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Filterable actionSourceFilter) {
        super(source, null, affectFilter, condition, ModifierType.CALCULATION_TOTAL_WHEN_TARGETED, false);
        _evaluator = new ConstantEvaluator(modifierAmount);
        _actionSourceFilter = Filters.and(actionSourceFilter);
    }

    @Override
    public boolean isActionSource(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard actionSource) {
        return actionSource != null && Filters.and(_actionSourceFilter).accepts(gameState, modifiersQuerying, actionSource);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
