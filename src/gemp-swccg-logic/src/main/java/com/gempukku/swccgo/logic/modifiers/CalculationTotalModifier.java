package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to total of a card-specific calculation for an action performing the calculation.
 */
public class CalculationTotalModifier extends AbstractModifier {
    protected Evaluator _evaluator;

    /**
     * Creates a modifier to total of a card-specific when a card accepted by the filter is the source of the action.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param modifierAmount the amount of the modifier
     */
    public CalculationTotalModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a modifier to total of a card-specific calculation when a card accepted by the filter is targeted by an action with the
     * card accepted by the action source filter if the source of the action.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    private CalculationTotalModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        super(source, null, affectFilter, condition, ModifierType.CALCULATION_TOTAL, false);
        _evaluator = new ConstantEvaluator(modifierAmount);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
