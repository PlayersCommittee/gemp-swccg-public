package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A power increase limit modifier.
 */
public class PowerIncreaseLimitModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a power increase limit modifier.
     *
     * @param source         the source of the modifier
     * @param affectFilter   the filter for cards whose forfeit value is modified
     * @param modifierAmount the amount of the modifier
     */
    public PowerIncreaseLimitModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount), false);
    }

    /**
     * Creates a power increase limit modifier.
     *
     * @param source       the source of the modifier
     * @param affectFilter the filter for cards whose forfeit value is modified
     * @param condition    the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator    the evaluator that calculates the amount of the modifier
     * @param cumulative   true if the modifier is cumulative, otherwise false
     */
    public PowerIncreaseLimitModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, boolean cumulative) {
        super(source, null, affectFilter, condition, ModifierType.POWER_INCREASE_MODIFIER_LIMIT, cumulative);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = getPowerModifierLimit(gameState, modifiersQuerying, self);
        if (value > 0)
            return "Power may not be increased by more than " + GuiUtils.formatAsString(value);
        else
            return null;
    }

    @Override
    public float getPowerModifierLimit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
