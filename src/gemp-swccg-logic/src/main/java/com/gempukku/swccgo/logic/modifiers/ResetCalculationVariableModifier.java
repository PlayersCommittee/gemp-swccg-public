package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets the value of a variable used in a card calculation.
 */
public class ResetCalculationVariableModifier extends AbstractModifier {
    private float _resetValue;
    private Variable _variable; // variable used in card calculation (e.g. X, Y, or Z), default is X

    /**
     * Creates a modifier for the value of a variable used in a card calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose variable is modified
     * @param resetValue the reset value
     * @param variable the variable to modify
     */
    public ResetCalculationVariableModifier(PhysicalCard source, Filterable affectFilter, float resetValue, Variable variable) {
        this(source, affectFilter, null, resetValue, variable);
    }

    /**
     * Creates a modifier for the value of a variable used in a card calculation.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose variable is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     * @param variable the variable to modify
     */
    private ResetCalculationVariableModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float resetValue, Variable variable) {
        super(source, null, affectFilter, condition, ModifierType.RESET_CALCULATION, true);
        _resetValue = resetValue;
        _variable = variable;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _variable + " = " + GuiUtils.formatAsString(_resetValue);
    }

    @Override
    public boolean isAffectedVariable(Variable variable) {
        return variable == _variable;
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _resetValue;
    }
}
