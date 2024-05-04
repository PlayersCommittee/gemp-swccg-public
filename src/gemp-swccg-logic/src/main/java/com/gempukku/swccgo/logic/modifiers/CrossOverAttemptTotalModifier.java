package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to total for attempt to cross over a specified character.
 */
public class CrossOverAttemptTotalModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to attempt to cross over a specified character total.
     * @param source the card that is the source of the modifier and whose attempt to be crossed over total is modified
     * @param modifierAmount the amount of the modifier
     */
    public CrossOverAttemptTotalModifier(PhysicalCard source, int modifierAmount) {
        this(source, source, null, modifierAmount);
    }

    /**
     * Creates a modifier to attempt to cross over a specified character total.
     * @param source the card that is the source of the modifier and whose attempt to be crossed over total is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public CrossOverAttemptTotalModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        this(source, source, condition, modifierAmount);
    }

    /**
     * Creates a modifier to attempt to cross over a specified character total.
     * @param source the card that is the source of the modifier and whose attempt to be crossed over total is modified
     * @param affectFilter the filter for cards whose attempt to be crossed over total is modified
     * @param modifierAmount the amount of the modifier
     */
    public CrossOverAttemptTotalModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a modifier to attempt to cross over a specified character total.
     * @param source the card that is the source of the modifier and whose attempt to be crossed over total is modified
     * @param affectFilter the filter for cards whose attempt to be crossed over total is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public CrossOverAttemptTotalModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a modifier to attempt to cross over a specified character total.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose attempt to be crossed over total is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public CrossOverAttemptTotalModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier to attempt to cross over a specified character total.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose attempt to be crossed over total is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public CrossOverAttemptTotalModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.CROSS_OVER_ATTEMPT_TOTAL, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Cross over attempt total +" + GuiUtils.formatAsString(value);
        else
            return "Cross over attempt total " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
