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
 * A modifier that resets the cost to initiate battle at specified locations.
 */
public class ResetInitiateBattleCostModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that resets the cost for either player to initiate battle at the source location.
     * @param source the location card that is the source of the modifier and whose initiate battle cost is modified
     * @param modifierAmount the amount of the modifier
     */
    public ResetInitiateBattleCostModifier(PhysicalCard source, int modifierAmount) {
        this(source, source, null, modifierAmount, null);
    }

    /**
     * Creates a modifier that resets the cost for the specified player to initiate battle at the source location.
     * @param source the location card that is the source of the modifier and whose initiate battle cost is modified
     * @param modifierAmount the amount of the modifier
     * @param playerId the player
     */
    public ResetInitiateBattleCostModifier(PhysicalCard source, int modifierAmount, String playerId) {
        this(source, source, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier that resets the cost for either player to initiate battle at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param modifierAmount the amount of the modifier
     */
    public ResetInitiateBattleCostModifier(PhysicalCard source, Filterable locationFilter, int modifierAmount) {
        this(source, locationFilter, null, modifierAmount, null);
    }

    /**
     * Creates a modifier that resets the cost for the specified player to initiate battle at locations accepted by the location
     * filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param modifierAmount the amount of the modifier
     * @param playerId the player
     */
    public ResetInitiateBattleCostModifier(PhysicalCard source, Filterable locationFilter, int modifierAmount, String playerId) {
        this(source, locationFilter, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier that resets the cost for either player to initiate battle at locations accepted by the location
     * filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public ResetInitiateBattleCostModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int modifierAmount) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that resets the cost for the specified player to initiate battle at locations accepted by the location
     * filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player
     */
    public ResetInitiateBattleCostModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int modifierAmount, String playerId) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that resets the cost for either player to initiate battle at locations accepted by the location
     * filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ResetInitiateBattleCostModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator) {
        this(source, locationFilter, condition, evaluator, null);
    }

    /**
     * Creates a modifier that resets the cost for the specified player to initiate battle at locations accepted by the location
     * filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player
     */
    public ResetInitiateBattleCostModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.UNMODIFIABLE_INITIATE_BATTLE_COST, true);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String sideText;
        if (gameState.getDarkPlayer().equals(_playerId))
            sideText = "Dark side initiates battle for ";
        else
            sideText = "Light side initiates battle for ";

        if (value > 0)
            return sideText + GuiUtils.formatAsString(value) + " Force";
        else
            return sideText + "free";
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
    }
}
