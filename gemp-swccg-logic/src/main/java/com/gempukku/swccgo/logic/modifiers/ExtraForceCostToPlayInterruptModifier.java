package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to require extra Force cost to play specified Interrupts.
 */
public class ExtraForceCostToPlayInterruptModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that requires extra Force cost to play Interrupt cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param modifierAmount the amount of the modifier
     */
    public ExtraForceCostToPlayInterruptModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a modifier that requires extra Force cost to play Interrupt cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public ExtraForceCostToPlayInterruptModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that requires extra Force cost to play Interrupt cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ExtraForceCostToPlayInterruptModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a modifier that requires extra Force cost to play Interrupt cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ExtraForceCostToPlayInterruptModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.and(Filters.Interrupt, affectFilter), condition, ModifierType.EXTRA_FORCE_COST_TO_PLAY_INTERRUPT, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value > 0)
            return "Must use +" + GuiUtils.formatAsString(value) + " Force to play";
        else
            return "";
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
