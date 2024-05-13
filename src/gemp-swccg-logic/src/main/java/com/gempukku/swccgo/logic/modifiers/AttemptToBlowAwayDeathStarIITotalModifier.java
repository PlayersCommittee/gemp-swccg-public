package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to attempt to 'blow away' Death Star II total.
 */
public class AttemptToBlowAwayDeathStarIITotalModifier extends EpicEventCalculationTotalModifier {

    /**
     * Creates a modifier to attempt to 'blow away' Death Star II total.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public AttemptToBlowAwayDeathStarIITotalModifier(PhysicalCard source, int modifierAmount) {
        this(source, null, modifierAmount);
    }

    /**
     * Creates a modifier to attempt to 'blow away' Death Star II total.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public AttemptToBlowAwayDeathStarIITotalModifier(PhysicalCard source, Evaluator evaluator) {
        super(source, Filters.That_Things_Operational, null, evaluator, false);
    }

    /**
     * Creates a modifier to attempt to 'blow away' Death Star II total.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public AttemptToBlowAwayDeathStarIITotalModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        super(source, Filters.That_Things_Operational, condition, new ConstantEvaluator(modifierAmount), false);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Attempt to 'blow away' Death Star II total +" + GuiUtils.formatAsString(value);
        else
            return "Attempt to 'blow away' Death Star II total " + GuiUtils.formatAsString(value);
    }
}
