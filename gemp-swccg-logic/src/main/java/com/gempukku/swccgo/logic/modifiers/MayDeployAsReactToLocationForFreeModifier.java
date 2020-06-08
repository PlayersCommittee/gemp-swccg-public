package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to deploy as a 'react' to specified locations for free.
 */
public class MayDeployAsReactToLocationForFreeModifier extends MayDeployAsReactToLocationModifier {

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to locations accepted by the location filter
     * for free.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public MayDeployAsReactToLocationForFreeModifier(PhysicalCard source, Filterable locationFilter) {
        super(source, null, locationFilter);
    }

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to locations accepted by the location filter
     * for free.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MayDeployAsReactToLocationForFreeModifier(PhysicalCard source, Condition condition, Filterable locationFilter) {
        super(source, condition, locationFilter);
    }

    @Override
    public boolean isReactForFree() {
        return true;
    }
}
