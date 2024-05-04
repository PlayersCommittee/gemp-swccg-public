package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to deploy as a 'react'.
 */
public class MayDeployAsReactModifier extends MayDeployAsReactToTargetModifier {

    /**
     * Creates a modifier that allows the source card to deploy as a 'react'.
     * @param source the source of the modifier
     */
    public MayDeployAsReactModifier(PhysicalCard source) {
        this(source, 0);
    }

    /**
     * Creates a modifier that allows the source card to deploy as a 'react'.
     * @param source the source of the modifier
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayDeployAsReactModifier(PhysicalCard source, float changeInCost) {
        super(source, Filters.any, changeInCost);
    }

    /**
     * Creates a modifier that allows the source card to deploy as a 'react'.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayDeployAsReactModifier(PhysicalCard source, Condition condition, float changeInCost) {
        super(source, condition, Filters.any, changeInCost);
    }
}
