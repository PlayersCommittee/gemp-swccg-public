package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to move away as a 'react' to specified locations.
 */
public class MayMoveAwayAsReactToLocationModifier extends AbstractModifier {
    private Filter _locationFilter;
    private float _changeInCost;

    /**
     * Creates a modifier which allows the source card to move away as a 'react' to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public MayMoveAwayAsReactToLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, null, locationFilter, 0);
    }

    /**
     * Creates a modifier which allows the source card to move away as a 'react' to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MayMoveAwayAsReactToLocationModifier(PhysicalCard source, Condition condition, Filterable locationFilter) {
        this(source, condition, locationFilter, 0);
    }

    /**
     * Creates a modifier which allows the source card to move away as a 'react' to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayMoveAwayAsReactToLocationModifier(PhysicalCard source, Filterable locationFilter, float changeInCost) {
        this(source, null, locationFilter, changeInCost);
    }

    /**
     * Creates a modifier which allows the source card to move away as a 'react' to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayMoveAwayAsReactToLocationModifier(PhysicalCard source, Condition condition, Filterable locationFilter, float changeInCost) {
        super(source, null, source, condition, ModifierType.MAY_MOVE_AWAY_AS_REACT_TO_LOCATION, true);
        _locationFilter = Filters.and(locationFilter, Filters.location, Filters.in_play);
        _changeInCost = changeInCost;
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, target);
    }

    @Override
    public float getChangeInCost() {
        return _changeInCost;
    }
}
