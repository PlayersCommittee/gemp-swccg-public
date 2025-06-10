package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier for the value of a variable used in a card calculation.
 */
public class CalculationVariableModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Variable _variable; // variable used in card calculation (e.g. X, Y, or Z), default is X

    /**
     * Creates a modifier for the value of the variable X used in a card calculation.
     * @param source the source of the modifier and the card with the variable
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public CalculationVariableModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, source, null, evaluator, Variable.X, false);
    }

    /**
     * Creates a modifier for the value of the variable X used in a card calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose variable is modified
     * @param modifierAmount the amount of the modifier
     */
    public CalculationVariableModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount) {
        this(source, affectFilter, null, modifierAmount, false);
    }

    /**
     * Creates a modifier for the value of the variable X used in a card calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose variable is modified
     * @param modifierAmount the amount of the modifier
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public CalculationVariableModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount, boolean cumulative) {
        this(source, affectFilter, null, modifierAmount, cumulative);
    }

    /**
     * Creates a modifier for the value of a variable used in a card calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose variable is modified
     * @param modifierAmount the amount of the modifier
     * @param variable the variable to modify
     */
    public CalculationVariableModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount, Variable variable) {
        this(source, affectFilter, null, modifierAmount, variable);
    }

    /**
     * Creates a modifier for the value of a variable used in a card calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose variable is modified
     * @param modifierAmount the amount of the modifier
     * @param variable the variable to modify
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public CalculationVariableModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount, Variable variable, boolean cumulative) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount), variable, cumulative);
    }

    /**
     * Creates a modifier for the value of the variable X used in a card calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose variable is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public CalculationVariableModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), false);
    }

    /**
     * Creates a modifier for the value of the variable X used in a card calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose variable is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public CalculationVariableModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float modifierAmount, boolean cumulative) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), cumulative);
    }

    /**
     * Creates a modifier for the value of a variable used in a card calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose variable is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param variable the variable to modify
     */
    public CalculationVariableModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float modifierAmount, Variable variable) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), variable, false);
    }

    /**
     * Creates a modifier for the value of the variable X used in a card calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose variable is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public CalculationVariableModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, boolean cumulative) {
        this(source, affectFilter, condition, evaluator, Variable.X, cumulative);
    }

    /**
     * Creates a modifier for the value of a variable used in a card calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose variable is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param variable the variable to modify
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public CalculationVariableModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Variable variable, boolean cumulative) {
        super(source, null, affectFilter, condition, ModifierType.ADDITION_CALCULATION, cumulative);
        _evaluator = evaluator;
        _variable = variable;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return _variable + " is +" + GuiUtils.formatAsString(value);
        else
            return _variable + " is " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getAdditionCalculationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Variable variable) {
        if (_variable==variable)
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
        else
            return 0;
    }
}
