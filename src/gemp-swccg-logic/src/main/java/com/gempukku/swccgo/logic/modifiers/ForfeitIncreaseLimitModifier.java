package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A forfeit increase limit modifier.
 */
public class ForfeitIncreaseLimitModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a forfeit increase limit modifier.
     *
     * @param source         the source of the modifier
     * @param affectFilter   the filter for cards whose forfeit value is modified
     * @param modifierAmount the amount of the modifier
     */
    public ForfeitIncreaseLimitModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount), false);
    }

    /**
     * Creates a forfeit increase limit modifier.
     *
     * @param source       the source of the modifier
     * @param affectFilter the filter for cards whose forfeit value is modified
     * @param condition    the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator    the evaluator that calculates the amount of the modifier
     * @param cumulative   true if the modifier is cumulative, otherwise false
     */
    public ForfeitIncreaseLimitModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, boolean cumulative) {
        super(source, null, affectFilter, condition, ModifierType.FORFEIT_INCREASE_MODIFIER_LIMIT, cumulative);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = getForfeitModifierLimit(gameState, modifiersQuerying, self);
        if (value > 0)
            return "Forfeit may not be increased by more than " + GuiUtils.formatAsString(value);
        else
            return null;
    }

    @Override
    public float getForfeitModifierLimit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
