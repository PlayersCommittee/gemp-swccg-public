package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to move as a 'react'.
 */
public class MayMoveAsReactModifier extends MayMoveAsReactToLocationModifier {

    /**
     * Creates a modifier that allows the source card to move as a 'react'.
     * @param source the source of the modifier
     */
    public MayMoveAsReactModifier(PhysicalCard source) {
        this(source, 0);
    }

    /**
     * Creates a modifier that allows the source card to move as a 'react'.
     * @param source the source of the modifier
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayMoveAsReactModifier(PhysicalCard source, float changeInCost) {
        super(source, Filters.any, changeInCost);
    }

    /**
     * Creates a modifier that allows the source card to move as a 'react'.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayMoveAsReactModifier(PhysicalCard source, Condition condition) {
        super(source, condition, Filters.any);
    }
}
