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
 * Creates a modifier that resets attrition at a location against the specified player to an unmodifiable value.
 */
public class ResetAttritionModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that resets attrition at a location against the specified player to an unmodifiable value.
     * @param source the source of the reset
     * @param resetValue the reset value
     * @param playerId the player whose total battle destiny is reset
     */
    public ResetAttritionModifier(PhysicalCard source, Filterable locationFilter, int resetValue, String playerId) {
        this(source, locationFilter, null, resetValue, playerId);
    }

    /**
     * Creates a modifier that resets attrition at a location against the specified player to an unmodifiable value.
     * @param source the source of the reset
     * @param locationFilter the filter for locations where the total battle destiny is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     * @param playerId the player whose total battle destiny is reset
     */
    public ResetAttritionModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int resetValue, String playerId) {
        super(source, null, Filters.and(Filters.battleLocation, locationFilter), condition, ModifierType.UNMODIFIABLE_ATTRITION, true);
        _evaluator = new ConstantEvaluator(resetValue);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (gameState.getDarkPlayer().equals(_playerId))
            return "Attrition against dark side =" + GuiUtils.formatAsString(value);
        else
            return "Attrition against light side =" + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
    }
}
