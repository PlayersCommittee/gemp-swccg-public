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
 * An modifier that changes the amount of existing immunity to attrition.
 */
public class ImmunityToAttritionChangeModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that changes the amount of existing immunity to attrition of cards accepted by the filter.
     *
     * @param source the source of the modifier
     */
    public ImmunityToAttritionChangeModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a modifier that changes the amount of existing immunity to attrition of cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param modifierAmount the amount of the modifier
     */
    public ImmunityToAttritionChangeModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a modifier that changes the amount of existing immunity to attrition of cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param modifierAmount the amount of the modifier
     */
    public ImmunityToAttritionChangeModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a modifier that changes the amount of existing immunity to attrition of cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public ImmunityToAttritionChangeModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that changes the amount of existing immunity to attrition of cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public ImmunityToAttritionChangeModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that changes the amount of existing immunity to attrition of cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ImmunityToAttritionChangeModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.IMMUNITY_TO_ATTRITION_CHANGE, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value > 0)
            return "Immunity to attrition +" + GuiUtils.formatAsString(value);
        if (value < 0)
            return "Immunity to attrition " + GuiUtils.formatAsString(value);
        return null;
    }

    @Override
    public float getImmunityToAttritionChangedModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
    }
}
