package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents specified cards from being converted.
 */
public class MayNotBeConvertedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents the source card from being converted.
     * @param source the source of the modifier
     */
    public MayNotBeConvertedModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that prevents the source card from being converted.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeConvertedModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that prevents card accepted by the filter from being converted.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeConvertedModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents card accepted by the filter from being converted.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeConvertedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be converted", affectFilter, condition, ModifierType.MAY_NOT_BE_CONVERTED);
    }
}
