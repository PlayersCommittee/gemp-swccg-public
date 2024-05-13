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
 * A race destiny modifier.
 */
public class RaceDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a race destiny modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified
     * @param modifierAmount the amount of the modifier
     */
    public RaceDestinyModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a race destiny modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    private RaceDestinyModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.and(Filters.raceDestiny, affectFilter), condition, ModifierType.RACE_DESTINY, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Race destiny +" + GuiUtils.formatAsString(value);
        else
            return "Race destiny " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
