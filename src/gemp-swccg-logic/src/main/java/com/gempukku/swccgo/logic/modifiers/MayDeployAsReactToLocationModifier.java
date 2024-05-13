package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to deploy as a 'react' to specified locations.
 */
public class MayDeployAsReactToLocationModifier extends MayDeployAsReactToTargetModifier {

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public MayDeployAsReactToLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, locationFilter, 0);
    }

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayDeployAsReactToLocationModifier(PhysicalCard source, Filterable locationFilter, float changeInCost) {
        this(source, null, locationFilter, changeInCost);
    }

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MayDeployAsReactToLocationModifier(PhysicalCard source, Condition condition, Filterable locationFilter) {
        this(source, condition, locationFilter, 0);
    }

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayDeployAsReactToLocationModifier(PhysicalCard source, Condition condition, Filterable locationFilter, float changeInCost) {
        super(source, condition, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)), changeInCost);
    }
}
