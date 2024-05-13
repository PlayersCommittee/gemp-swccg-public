package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents specified binary droid from being turned on.
 */
public class MayNotBeTurnedOnModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents binary droids accepted by the filter from being turned on.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeTurnedOnModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not turn on", affectFilter, ModifierType.MAY_NOT_BE_TURNED_ON);
    }

    /**
     * Creates a modifier that prevents binary droid accepted by the filter from being turned on.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param affectFilter the filter
     */
    public MayNotBeTurnedOnModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "May not turn on", affectFilter, condition, ModifierType.MAY_NOT_BE_TURNED_ON);
    }
}
