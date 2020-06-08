package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to attempt to 'blow away' Blockade Flagship total.
 */
public class AttemptToBlowAwayBlockadeFlagshipTotalModifier extends AbstractModifier {
    protected Evaluator _evaluator;

    /**
     * Creates a modifier to attempt to 'blow away' Blockade Flagship total.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public AttemptToBlowAwayBlockadeFlagshipTotalModifier(PhysicalCard source, int modifierAmount) {
        this(source, null, modifierAmount);
    }

    /**
     * Creates a modifier to attempt to 'blow away' Blockade Flagship total.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public AttemptToBlowAwayBlockadeFlagshipTotalModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        super(source, null, null, condition, ModifierType.BLOW_AWAY_BLOCKADE_FLAGSHIP_ATTEMPT_TOTAL, false);
        _evaluator = new ConstantEvaluator(modifierAmount);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Attempt to 'blow away' Blockade Flagship total +" + GuiUtils.formatAsString(value);
        else
            return "Attempt to 'blow away' Blockade Flagship total " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
