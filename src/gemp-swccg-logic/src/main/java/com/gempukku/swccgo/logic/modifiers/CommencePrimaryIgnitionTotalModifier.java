package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to Commence Primary Ignition total.
 */
public class CommencePrimaryIgnitionTotalModifier extends EpicEventCalculationTotalModifier {

    /**
     * Creates a modifier to Commence Primary Ignition total.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public CommencePrimaryIgnitionTotalModifier(PhysicalCard source, int modifierAmount) {
        super(source, Filters.Commence_Primary_Ignition, null, new ConstantEvaluator(modifierAmount), false);
    }

    /**
     * Creates a modifier to Commence Primary Ignition total.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public CommencePrimaryIgnitionTotalModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        super(source, Filters.Commence_Primary_Ignition, condition, new ConstantEvaluator(modifierAmount), false);
    }

    /**
     * Creates a modifier to Commence Primary Ignition total.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public CommencePrimaryIgnitionTotalModifier(PhysicalCard source, Condition condition, int modifierAmount, boolean cumulative) {
        super(source, Filters.Commence_Primary_Ignition, condition, new ConstantEvaluator(modifierAmount), cumulative);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Commence Primary Ignition total +" + GuiUtils.formatAsString(value);
        else
            return "Commence Primary Ignition total " + GuiUtils.formatAsString(value);
    }
}
