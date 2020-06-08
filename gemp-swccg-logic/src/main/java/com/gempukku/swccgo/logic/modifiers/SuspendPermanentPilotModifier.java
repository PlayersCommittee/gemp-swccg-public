package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that suspends the permanent pilots of the affected cards.
 */
public class SuspendPermanentPilotModifier extends AbstractModifier {

    /**
     * Creates a modifier that suspends the permanent pilot of the source card.
     * @param source the card that is the source of the modifier and that is affected by the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public SuspendPermanentPilotModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that suspends the permanent pilot of cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public SuspendPermanentPilotModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that suspends the permanent pilot of cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    private SuspendPermanentPilotModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Permanent pilot suspended", affectFilter, condition, ModifierType.SUSPEND_PERMANENT_PILOT);
    }
}
