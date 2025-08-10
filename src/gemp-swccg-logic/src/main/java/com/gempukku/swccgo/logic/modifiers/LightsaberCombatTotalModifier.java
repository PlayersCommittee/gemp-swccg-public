package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A lightsaber combat total modifier for a specified character.
 */
public class LightsaberCombatTotalModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a lightsaber combat total modifier for a character accepted by the character filter.
     * @param source the source of the modifier
     * @param characterFilter the character filter
     * @param modifierAmount the amount of the modifier
     */
    public LightsaberCombatTotalModifier(PhysicalCard source, Filterable characterFilter, float modifierAmount) {
        this(source, characterFilter, null, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a lightsaber combat total modifier for a character accepted by the character filter.
     * @param source the source of the modifier
     * @param characterFilter the character filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public LightsaberCombatTotalModifier(PhysicalCard source, Filterable characterFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.and(Filters.character, characterFilter), condition, ModifierType.LIGHTSABER_COMBAT_TOTAL, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Lightsaber combat total +" + GuiUtils.formatAsString(value);
        else
            return "Lightsaber combat total " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
