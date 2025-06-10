package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for multiplying the value of a variable used in a calculation.
 */
public class VariableMultiplierModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Variable _variable; // variable used in card calculation (e.g. X, Y, or Z), default is X

    /**
     * Creates a modifier for multiplying the value of the variable X used in a calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose value of the variable is modified
     * @param modifierAmount the amount of the modifier
     */
    public VariableMultiplierModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a modifier for multiplying the value of the specified variable used in a calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose value of the variable is modified
     * @param modifierAmount the amount of the modifier
     * @param variable the variable
     */
    public VariableMultiplierModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Variable variable) {
        this(source, affectFilter, null, modifierAmount, variable);
    }

    /**
     * Creates a modifier for multiplying the value of the variable X used in a calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose value of the variable is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public VariableMultiplierModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier for multiplying the value of the specified variable used in a calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose value of the variable is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param variable the variable
     */
    public VariableMultiplierModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Variable variable) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), variable);
    }

    /**
     * Creates a modifier for multiplying the value of the variable X used in a calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose value of the variable is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public VariableMultiplierModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        this(source, affectFilter, condition, evaluator, Variable.X);
    }

    /**
     * Creates a modifier for multiplying the value of the specified variable used in a calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose value of the variable is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param variable the variable
     */
    public VariableMultiplierModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Variable variable) {
        super(source, null, affectFilter, condition, ModifierType.MULTIPLICATION_CALCULATION, false);
        _evaluator = evaluator;
        _variable = variable;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value == 3)
            return _variable + " is tripled";
        else if (value == 2)
            return _variable + " is doubled";
        else
            return _variable + " is *" + value;
    }

    @Override
    public int getMultiplicationCalculationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Variable variable) {
        if (_variable == variable)
            return (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
        else
            return 1;
    }
}
