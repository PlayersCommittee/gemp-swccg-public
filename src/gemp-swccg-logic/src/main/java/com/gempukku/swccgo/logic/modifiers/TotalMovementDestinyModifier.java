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
 * A modifier that affects total movement destiny for specified starship.
 */
public class TotalMovementDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that affects total movement destiny for starship accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param modifierAmount the amount of the modifier
     */
    public TotalMovementDestinyModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that affects total movement destiny for starship accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public TotalMovementDestinyModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a modifier that affects total movement destiny for starship accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public TotalMovementDestinyModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.and(Filters.starship, affectFilter), condition, ModifierType.TOTAL_MOVEMENT_DESTINY, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Total movement destiny +" + GuiUtils.formatAsString(value);
        else
            return "Total movement destiny " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard starship) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, starship);
    }
}
