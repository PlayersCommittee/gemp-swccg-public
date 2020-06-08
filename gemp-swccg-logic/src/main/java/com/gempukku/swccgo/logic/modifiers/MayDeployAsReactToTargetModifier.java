package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to deploy as a 'react' to specified targets.
 */
public class MayDeployAsReactToTargetModifier extends AbstractModifier {
    private Filter _targetFilter;
    private float _changeInCost;

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to targets accepted by the target filter.
     * @param source the source of the modifier
     * @param targetFilter the target filter
     */
    public MayDeployAsReactToTargetModifier(PhysicalCard source, Filterable targetFilter) {
        this(source, null, targetFilter, 0);
    }

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to targets accepted by the target filter.
     * @param source the source of the modifier
     * @param targetFilter the target filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayDeployAsReactToTargetModifier(PhysicalCard source, Filterable targetFilter, float changeInCost) {
        this(source, null, targetFilter, changeInCost);
    }

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to targets accepted by the target filter.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param targetFilter the target filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayDeployAsReactToTargetModifier(PhysicalCard source, Condition condition, Filterable targetFilter, float changeInCost) {
        super(source, null, Filters.and(source, Filters.or(Filters.In_Hand, Filters.canDeployAsIfFromHand)), condition, ModifierType.MAY_DEPLOY_AS_REACT_TO_TARGET, true);
        _targetFilter = Filters.and(targetFilter, Filters.in_play);
        _changeInCost = changeInCost;
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }

    @Override
    public float getChangeInCost() {
        return _changeInCost;
    }
}
