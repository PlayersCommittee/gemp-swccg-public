package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that affects each Carbon-Freezing destiny.
 */
public class EachCarbonFreezingDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that affects each Carbon-Freezing destiny.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public EachCarbonFreezingDestinyModifier(PhysicalCard source, int modifierAmount, boolean cumulative) {
        this(source, null, modifierAmount, cumulative);
    }

    /**
     * Creates a modifier that affects each Carbon-Freezing destiny.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public EachCarbonFreezingDestinyModifier(PhysicalCard source, Condition condition, int modifierAmount, boolean cumulative) {
        this(source, condition, new ConstantEvaluator(modifierAmount), cumulative);
    }

    /**
     * Creates a modifier that affects each Carbon-Freezing destiny.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public EachCarbonFreezingDestinyModifier(PhysicalCard source, Condition condition, Evaluator evaluator, boolean cumulative) {
        super(source, null, null, condition, ModifierType.EACH_CARBON_FREEZING_DESTINY, cumulative);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);

        if (value >= 0)
            return "Each Carbon-Freezing destiny +" + value;
        else
            return "Each Carbon-Freezing destiny " + value;
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
