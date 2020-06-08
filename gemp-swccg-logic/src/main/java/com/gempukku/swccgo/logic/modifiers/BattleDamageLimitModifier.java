package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that causes the specified player to take no more than a specified amount of battle damage during battles
 * at specified locations.
 */
public class BattleDamageLimitModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that causes the specified player to take no more than a specified amount of battle damage during
     * battles at specified locations.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param limitAmount the amount of the limit
     * @param playerId the player whose side of the location is affected
     */
    public BattleDamageLimitModifier(PhysicalCard source, Condition condition, int limitAmount, String playerId) {
        this(source, Filters.battleLocation, condition, limitAmount, playerId);
    }

    /**
     * Creates a modifier that causes the specified player to take no more than a specified amount of battle damage during
     * battles at specified locations.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param limitAmount the amount of the limit
     * @param playerId the player whose side of the location is affected
     */
    public BattleDamageLimitModifier(PhysicalCard source, Filterable locationFilter, int limitAmount, String playerId) {
        this(source, locationFilter, null, limitAmount, playerId);
    }

    /**
     * Creates a modifier that causes the specified player to take no more than a specified amount of battle damage during
     * battles at specified locations.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param limitAmount the amount of the limit
     * @param playerId the player whose side of the location is affected
     */
    private BattleDamageLimitModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int limitAmount, String playerId) {
        super(source, null, Filters.and(Filters.battleLocation, locationFilter), condition, ModifierType.BATTLE_DAMAGE_LIMIT, true);
        _evaluator = new ConstantEvaluator(limitAmount);
        _playerId = playerId;
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
    }
}
