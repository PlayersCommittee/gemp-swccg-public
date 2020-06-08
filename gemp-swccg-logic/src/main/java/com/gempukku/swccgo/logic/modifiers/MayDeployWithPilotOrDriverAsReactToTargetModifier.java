package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to deploy as a 'react' with a specified pilot or driver to specified targets.
 */
public class MayDeployWithPilotOrDriverAsReactToTargetModifier extends AbstractModifier {
    private Filter _pilotOrDriverFilter;
    private Filter _targetFilter;
    private float _changeInCost;

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' with a pilot or driver accepted by the pilot
     * or driver filter to targets accepted by the target filter.
     * @param source the source of the modifier
     * @param pilotOrDriverFilter the pilot or driver filter
     * @param targetFilter the target filter
     */
    public MayDeployWithPilotOrDriverAsReactToTargetModifier(PhysicalCard source, Filterable pilotOrDriverFilter, Filterable targetFilter) {
        this(source, null, pilotOrDriverFilter, targetFilter, 0);
    }

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' with a pilot or driver accepted by the pilot
     * or driver filter to targets accepted by the target filter.
     * @param source the source of the modifier
     * @param pilotOrDriverFilter the pilot or driver filter
     * @param targetFilter the target filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayDeployWithPilotOrDriverAsReactToTargetModifier(PhysicalCard source, Filterable pilotOrDriverFilter, Filterable targetFilter, float changeInCost) {
        this(source, null, pilotOrDriverFilter, targetFilter, changeInCost);
    }

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' with a pilot or driver accepted by the pilot
     * or driver filter to targets accepted by the target filter.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param pilotOrDriverFilter the pilot or driver filter
     * @param targetFilter the target filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayDeployWithPilotOrDriverAsReactToTargetModifier(PhysicalCard source, Condition condition, Filterable pilotOrDriverFilter, Filterable targetFilter, float changeInCost) {
        super(source, null, Filters.and(source, Filters.or(Filters.In_Hand, Filters.canDeployAsIfFromHand)), condition, ModifierType.MAY_DEPLOY_WITH_PILOT_OR_DRIVER_AS_REACT_TO_TARGET, true);
        _pilotOrDriverFilter = Filters.and(pilotOrDriverFilter, Filters.or(Filters.In_Hand, Filters.canDeployAsIfFromHand));
        _targetFilter = Filters.and(targetFilter, Filters.in_play);
        _changeInCost = changeInCost;
    }

    @Override
    public Filter getPilotOrDriverFilter() {
        return _pilotOrDriverFilter;
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
