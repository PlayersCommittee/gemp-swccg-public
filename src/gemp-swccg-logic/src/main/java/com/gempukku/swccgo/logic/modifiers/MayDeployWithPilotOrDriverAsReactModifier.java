package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier which allows the source card to deploy as a 'react' with a specified driver.
 */
public class MayDeployWithPilotOrDriverAsReactModifier extends MayDeployWithPilotOrDriverAsReactToTargetModifier {

    /**
     * Creates a modifier that allows the source card to deploy as a 'react' with a driver accepted by the driver filter.
     * @param source the source of the modifier
     * @param driverFilter the driver filter
     */
    public MayDeployWithPilotOrDriverAsReactModifier(PhysicalCard source, Filterable driverFilter) {
        this(source, driverFilter, 0);
    }

    /**
     * Creates a modifier that allows the source card to deploy as a 'react' with a driver accepted by the driver filter.
     * @param source the source of the modifier
     * @param driverFilter the driver filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayDeployWithPilotOrDriverAsReactModifier(PhysicalCard source, Filterable driverFilter, float changeInCost) {
        super(source, driverFilter, Filters.any, changeInCost);
    }
}
