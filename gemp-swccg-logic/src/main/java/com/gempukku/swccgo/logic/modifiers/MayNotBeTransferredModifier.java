package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for a captive not being able to be transferred.
 */
public class MayNotBeTransferredModifier extends AbstractModifier {

    /**
     * Creates a modifier that prohibits the source card from being transferred.
     * @param source the source card of the modifier
     */
    public MayNotBeTransferredModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that prohibits the source card from being transferred.
     * @param source the source card of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeTransferredModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that prohibits captives accepted by the input filter from being transferred.
     * @param source the source card of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeTransferredModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prohibits cards accepted by the input filter from being transferred.
     * @param source the source card of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    private MayNotBeTransferredModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be transferred", Filters.and(Filters.captive, affectFilter), condition, ModifierType.MAY_NOT_BE_TRANSFERRED, true);
    }
}
