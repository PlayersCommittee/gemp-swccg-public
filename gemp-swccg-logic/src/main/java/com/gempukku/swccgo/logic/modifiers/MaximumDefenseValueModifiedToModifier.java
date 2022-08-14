package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that defines the maximum value a defense value can be modified to that is above printed value by modifiers.
 */
public class MaximumDefenseValueModifiedToModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that defines the maximmum value a defense value can be modified to that is above printed value by modifiers.
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     * @param modifierAmount the amount of the modifier
     */
    public MaximumDefenseValueModifiedToModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that defines the maximmum value a defense value can be modified to that is above printed value by modifiers.
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    private MaximumDefenseValueModifiedToModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.MAX_DEFENSE_VALUE_MODIFIED_TO, true);
        _evaluator = evaluator;
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
