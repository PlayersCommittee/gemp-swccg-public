package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the affected cards to be Unique.
 */
public class UniqueModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes cards accepted by the filter to be Unique.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public UniqueModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to be Unique.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public UniqueModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Unique (•)", affectFilter, condition, ModifierType.UNIQUE);
    }
}
