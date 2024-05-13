package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents specified Objectives from flipping.
 */
public class MayNotBeFlippedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents Objectives accepted by the filter from flipping.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeFlippedModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not be flipped", affectFilter, ModifierType.MAY_NOT_BE_FLIPPED);
    }

    /**
     * Creates a modifier that prevents Objectives accepted by the filter from flipping.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param affectFilter the filter
     */
    public MayNotBeFlippedModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "May not be flipped", affectFilter, condition, ModifierType.MAY_NOT_BE_FLIPPED);
    }
}
