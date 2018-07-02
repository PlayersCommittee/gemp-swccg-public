package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that causes affected cards to use a calculation for the deploy cost.
 */
public class UseCalculationForDeployCostModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that causes cards accepted by the filter to use a calculation for the deploy cost.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param evaluator the evaluator to use for deploy cost
     */
    public UseCalculationForDeployCostModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to use a calculation for the deploy cost.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator to use for deploy cost
     */
    private UseCalculationForDeployCostModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, "Deploy cost determined by calculation", affectFilter, condition, ModifierType.USE_CALCULATION_FOR_DEPLOY_COST, true);
        _evaluator = evaluator;
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
