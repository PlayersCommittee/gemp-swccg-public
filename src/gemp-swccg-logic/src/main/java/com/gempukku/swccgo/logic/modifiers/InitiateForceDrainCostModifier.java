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
 * A modifier for the cost to initiate a Force drain at specified locations.
 */
public class InitiateForceDrainCostModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier for the cost for the specified player to initiate a Force drain at any location.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player
     */
    public InitiateForceDrainCostModifier(PhysicalCard source, int modifierAmount, String playerId) {
        this(source, Filters.any, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier for the cost for the specified player to initiate a Force drain at any location.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player
     */
    public InitiateForceDrainCostModifier(PhysicalCard source, Condition condition, int modifierAmount, String playerId) {
        this(source, Filters.any, condition, modifierAmount, playerId);
    }

    /**
     * Creates a modifier for the cost for the specified player to initiate a Force drain at locations accepted by the location
     * filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param modifierAmount the amount of the modifier
     * @param playerId the player
     */
    public InitiateForceDrainCostModifier(PhysicalCard source, Filterable locationFilter, int modifierAmount, String playerId) {
        this(source, locationFilter, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier for the cost for the specified player to initiate a Force drain at locations accepted by the location
     * filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player
     */
    public InitiateForceDrainCostModifier(PhysicalCard source, Filterable locationFilter, Evaluator evaluator, String playerId) {
        this(source, locationFilter, null, evaluator, playerId);
    }

    /**
     * Creates a modifier for the cost for the specified player to initiate a Force drain at locations accepted by the location
     * filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player
     */
    public InitiateForceDrainCostModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int modifierAmount, String playerId) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier for the cost for the specified player to initiate a Force drain at locations accepted by the location
     * filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player
     */
    public InitiateForceDrainCostModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(Filters.location, Filters.in_play, locationFilter), condition, ModifierType.INITIATE_FORCE_DRAIN_COST, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String sideText;
        if (gameState.getDarkPlayer().equals(_playerId))
            sideText = "Dark side initiates Force drain for ";
        else
            sideText = "Light side initiates Force drain for ";

        if (value >= 0)
            return sideText + "+" + GuiUtils.formatAsString(value) + " Force";
        else
            return sideText + GuiUtils.formatAsString(value) + " Force";
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
    }
}
