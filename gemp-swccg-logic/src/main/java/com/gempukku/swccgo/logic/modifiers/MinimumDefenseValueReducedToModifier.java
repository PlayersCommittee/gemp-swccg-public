package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that defines the minimum value a defense value can be modified (reduced) to that is below printed value by modifiers.
 */
public class MinimumDefenseValueReducedToModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that defines the minimum value a defense value can be modified (reduced) to that is below printed value by modifiers.
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     * @param modifierAmount the amount of the modifier
     */
    public MinimumDefenseValueReducedToModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that defines the minimum value a defense value can be modified (reduced) to that is below printed value by modifiers.
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    private MinimumDefenseValueReducedToModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.MIN_DEFENSE_VALUE_REDUCED_TO, true);
        _evaluator = evaluator;
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
