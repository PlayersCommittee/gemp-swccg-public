package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to Target The Main Generator total.
 */
public class TargetTheMainGeneratorTotalModifier extends EpicEventCalculationTotalModifier {

    /**
     * Creates a modifier to Target The Main Generator total.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public TargetTheMainGeneratorTotalModifier(PhysicalCard source, int modifierAmount) {
        this(source, null, modifierAmount);
    }

    /**
     * Creates a modifier to Target The Main Generator total.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public TargetTheMainGeneratorTotalModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        super(source, Filters.Target_The_Main_Generator, condition, new ConstantEvaluator(modifierAmount), false);
    }

    /**
     * Creates a modifier to Target The Main Generator total.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public TargetTheMainGeneratorTotalModifier(PhysicalCard source, Condition condition, Evaluator evaluator, boolean cumulative) {
        super(source, Filters.Target_The_Main_Generator, condition, evaluator, cumulative);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Target The Main Generator total +" + GuiUtils.formatAsString(value);
        else
            return "Target The Main Generator total " + GuiUtils.formatAsString(value);
    }
}
