package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents cards affected from being canceled.
 */
public class MayNotBeCanceledModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeCanceledModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeCanceledModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be canceled", affectFilter, condition, ModifierType.MAY_NOT_BE_CANCELED);
    }
}
