package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to Attack Run total.
 */
public class AttackRunTotalModifier extends EpicEventCalculationTotalModifier {

    /**
     * Creates a modifier to Attack Run total.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public AttackRunTotalModifier(PhysicalCard source, float modifierAmount) {
        super(source, Filters.Attack_Run, null, new ConstantEvaluator(modifierAmount), false);
    }

    /**
     * Creates a modifier to Attack Run total.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public AttackRunTotalModifier(PhysicalCard source, Condition condition, float modifierAmount) {
        super(source, Filters.Attack_Run, condition, new ConstantEvaluator(modifierAmount), false);
    }

    /**
     * Creates a modifier to Attack Run total.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public AttackRunTotalModifier(PhysicalCard source, Condition condition, Evaluator evaluator, boolean cumulative) {
        super(source, Filters.Attack_Run, condition, evaluator, cumulative);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Attack Run total +" + GuiUtils.formatAsString(value);
        else
            return "Attack Run total " + GuiUtils.formatAsString(value);
    }
}
