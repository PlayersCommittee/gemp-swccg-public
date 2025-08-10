package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to the total ability at a location.
 */
public class TotalAbilityModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to total ability at a location for the specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where total ability is modified
     * @param modifierAmount the amount of the modifier
     * @param playerId the player
     */
    public TotalAbilityModifier(PhysicalCard source, Filterable locationFilter, int modifierAmount, String playerId) {
        this(source, locationFilter, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier to total ability at a location for the specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where total ability is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player
     */
    public TotalAbilityModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int modifierAmount, String playerId) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier to total ability at a location for the specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where total ability is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player
     */
    private TotalAbilityModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.TOTAL_ABILITY_AT_LOCATION, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);

        String sideText;
        if (gameState.getSide(_playerId)== Side.DARK)
            sideText = "Dark side";
        else
            sideText = "Light side";

        if (value >= 0)
            return sideText + " total ability +" + GuiUtils.formatAsString(value);
        else
            return sideText + " total ability " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
    }
}
