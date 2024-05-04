package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to attempt to 'blow away' Shield Gate total.
 */
public class AttemptToBlowAwayShieldGateTotalModifier extends AbstractModifier {
    protected Evaluator _evaluator;

    /**
     * Creates a modifier to attempt to 'blow away' Shield Gate total.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public AttemptToBlowAwayShieldGateTotalModifier(PhysicalCard source, int modifierAmount) {
        this(source, null, modifierAmount);
    }

    /**
     * Creates a modifier to attempt to 'blow away' Shield Gate total.
     * @param source the source of the modifier
     * @param evaluator the evaluator
     */
    public AttemptToBlowAwayShieldGateTotalModifier(PhysicalCard source, Evaluator evaluator) {
        super(source, null, Filters.Shield_Gate, null, ModifierType.BLOW_AWAY_SHIELD_GATE_ATTEMPT_TOTAL, false);
        _evaluator = evaluator;
    }

    /**
     * Creates a modifier to attempt to 'blow away' Shield Gate total.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public AttemptToBlowAwayShieldGateTotalModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        super(source, null, Filters.Shield_Gate, condition, ModifierType.BLOW_AWAY_SHIELD_GATE_ATTEMPT_TOTAL, false);
        _evaluator = new ConstantEvaluator(modifierAmount);
    }

    /**
     * Creates a modifier to attempt to 'blow away' Shield Gate total.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator
     */
    public AttemptToBlowAwayShieldGateTotalModifier(PhysicalCard source, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.Shield_Gate, condition, ModifierType.BLOW_AWAY_SHIELD_GATE_ATTEMPT_TOTAL, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Attempt to 'blow away' Shield Gate total +" + GuiUtils.formatAsString(value);
        else
            return "Attempt to 'blow away' Shield Gate total " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
