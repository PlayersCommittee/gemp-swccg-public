package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to total asteroid destiny when targeting affected cards.
 */
public class TotalAsteroidDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _targetFilter;

    /**
     * Creates a modifier to total asteroid destiny when targeting a card accepted by the target filter.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     */
    public TotalAsteroidDestinyModifier(PhysicalCard source, int modifierAmount, Filterable targetFilter) {
        this(source, null, modifierAmount, targetFilter);
    }

    /**
     * Creates a modifier to total asteroid destiny when targeting a card accepted by the target filter.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     */
    public TotalAsteroidDestinyModifier(PhysicalCard source, Condition condition, int modifierAmount, Filterable targetFilter) {
        this(source, condition, new ConstantEvaluator(modifierAmount), targetFilter);
    }

    /**
     * Creates a modifier to total asteroid destiny when targeting a card accepted by the target filter.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param targetFilter the target filter
     */
    public TotalAsteroidDestinyModifier(PhysicalCard source, Condition condition, Evaluator evaluator, Filterable targetFilter) {
        super(source, null, null, condition, ModifierType.TOTAL_ASTEROID_DESTINY, false);
        _evaluator = evaluator;
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, targetCard);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, target);
    }
}
