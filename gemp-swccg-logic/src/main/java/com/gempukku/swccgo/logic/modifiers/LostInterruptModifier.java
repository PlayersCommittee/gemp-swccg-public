package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the affected cards to be Lost Interrupts.
 */
public class LostInterruptModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes Interrupts accepted by the filter to be Lost Interrupts.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public LostInterruptModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes Interrupts accepted by the filter to be Lost Interrupts.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public LostInterruptModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Lost Interrupt", Filters.and(Filters.Interrupt, affectFilter), condition, ModifierType.LOST_INTERRUPT);
    }
}
