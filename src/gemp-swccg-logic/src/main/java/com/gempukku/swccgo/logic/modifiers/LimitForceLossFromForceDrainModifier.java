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
 * A modifier that sets the maximum amount of Force that the specified player can lose from a Force drain at a location
 * affected by the modifier.
 */
public class LimitForceLossFromForceDrainModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that sets the maximum amount of Force that the specified player can lose from any Force drain.
     * @param source the source card of this modifier
     * @param modifierAmount the maximum Force loss limit
     * @param playerId the player whose maximum Force loss is limited
     */
    public LimitForceLossFromForceDrainModifier(PhysicalCard source, int modifierAmount, String playerId) {
        this(source, Filters.any, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier that sets the maximum amount of Force that the specified player can lose from a Force drain at
     * a location affected by the modifier.
     * @param source the source card of this modifier
     * @param locationFilter the filter for locations whose Force loss from Force drains are limited when a card accepted
     *                     by the filter is the Force drain
     * @param modifierAmount the maximum Force loss limit
     * @param playerId the player whose maximum Force loss is limited
     */
    public LimitForceLossFromForceDrainModifier(PhysicalCard source, Filterable locationFilter, int modifierAmount, String playerId) {
        this(source, locationFilter, null, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that sets the maximum amount of Force that the specified player can lose from a Force drain at
     * a location affected by the modifier.
     * @param source the source card of this modifier
     * @param condition the condition under which this modifier is in effect
     * @param modifierAmount the maximum Force loss limit
     * @param playerId the player whose maximum Force loss is limited
     */
    public LimitForceLossFromForceDrainModifier(PhysicalCard source, Condition condition, int modifierAmount, String playerId) {
        this(source, Filters.any, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that sets the maximum amount of Force that the specified player can lose from a Force drain at
     * a location affected by the modifier.
     * @param source the source card of this modifier
     * @param locationFilter the filter for locations whose Force loss from Force drains are limited when a card accepted
     *                     by the filter is the Force drain
     * @param condition the condition under which this modifier is in effect
     * @param modifierAmount the maximum Force loss limit
     * @param playerId the player whose maximum Force loss is limited
     */
    public LimitForceLossFromForceDrainModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int modifierAmount, String playerId) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that sets the maximum amount of Force that the specified player can lose from a Force drain at
     * a location affected by the modifier.
     * @param source the source card of this modifier
     * @param locationFilter the filter for locations whose Force loss from Force drains are limited when a card accepted
     *                     by the filter is the Force drain
     * @param condition the condition under which this modifier is in effect
     * @param evaluator the evaluator that determine the amount of the maximum Force loss limit
     * @param playerId the player whose maximum Force loss is limited
     */
    public LimitForceLossFromForceDrainModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.LIMIT_FORCE_LOSS_FROM_FORCE_DRAIN, true);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String sideText;
        if (gameState.getDarkPlayer().equals(_playerId))
            sideText = "Dark side";
        else
            sideText = "Light side";

        if (value==0)
            return sideText + " loses no Force from Force drain";
        else
            return sideText + " Force loss from Force drain limited to " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getForceLossLimit(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
    }
}
