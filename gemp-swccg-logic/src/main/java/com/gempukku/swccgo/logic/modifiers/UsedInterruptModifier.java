package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the affected cards to not be considered Used Interrupts.
 */
public class UsedInterruptModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the affected cards to not be considered Used Interrupts.
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     */
    public UsedInterruptModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Used Interrupt", affectFilter, ModifierType.USED_INTERRUPT);
    }

    /**
     * Creates a modifier that causes the affected cards to not be considered Used Interrupts.
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public UsedInterruptModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Used Interrupt", affectFilter, condition, ModifierType.USED_INTERRUPT);
    }
}
