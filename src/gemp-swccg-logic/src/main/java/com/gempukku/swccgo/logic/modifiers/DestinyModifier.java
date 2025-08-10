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
 * A destiny modifier.
 */
public class DestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a destiny modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified
     * @param modifierAmount the amount of the modifier
     */
    public DestinyModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a destiny modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DestinyModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a destiny modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public DestinyModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a destiny modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DestinyModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.DESTINY, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = getDestinyModifier(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Destiny +" + GuiUtils.formatAsString(value);
        else
            return "Destiny " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
        float limit = modifiersQuerying.getDestinyModifierLimit(gameState, modifiersQuerying, physicalCard);
        if (limit > 0 && value > limit) {
            value = limit;
        }
        return value;
    }
}
