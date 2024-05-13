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
 * A duel total modifier for a specified character.
 */
public class DuelTotalModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a duel total modifier for a character accepted by the character filter.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public DuelTotalModifier(PhysicalCard source, Filterable characterFilter, float modifierAmount) {
        this(source, characterFilter, null, modifierAmount);
    }

    /**
     * Creates a duel total modifier for a character accepted by the character filter.
     * @param source the source of the modifier
     * @param characterFilter the character filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public DuelTotalModifier(PhysicalCard source, Filterable characterFilter, Condition condition, float modifierAmount) {
        this(source, characterFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a duel total modifier.
     * @param source the source of the modifier
     * @param characterFilter the character filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DuelTotalModifier(PhysicalCard source, Filterable characterFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.and(Filters.character, characterFilter), condition, ModifierType.DUEL_TOTAL, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Duel total +" + GuiUtils.formatAsString(value);
        else
            return "Duel total " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
    }
}
