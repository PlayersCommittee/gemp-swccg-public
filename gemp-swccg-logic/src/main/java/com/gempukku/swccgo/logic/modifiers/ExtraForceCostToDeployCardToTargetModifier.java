package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to require extra Force cost to deploy specified cards to targets.
 */
public class ExtraForceCostToDeployCardToTargetModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _targetFilter;

    /**
     * Creates a modifier that requires extra Force cost to deploy cards accepted by the filter to targets accepted by the target filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param targetFilter the target Filter
     */
    protected ExtraForceCostToDeployCardToTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filterable targetFilter) {
        super(source, null, Filters.and(Filters.not(Filters.in_play), affectFilter), condition, ModifierType.EXTRA_FORCE_COST_TO_DEPLOY_TO_TARGET, false);
        _evaluator = evaluator;
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, PhysicalCard target) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, cardToDeploy, target);
    }
}
