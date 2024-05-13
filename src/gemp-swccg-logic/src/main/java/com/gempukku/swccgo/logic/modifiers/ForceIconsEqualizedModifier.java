package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that adds Force icons to affected locations to equalize number light and dark Force icons.
 */
public class ForceIconsEqualizedModifier extends AbstractModifier {

    /**
     * Creates a modifier adds Force icons to locations accepted by the location filter to equalize number light and dark Force icons.
     * @param source the source of the modifier
     * @param locationFilter the filter
     */
    public ForceIconsEqualizedModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, locationFilter, null);
    }

    /**
     * Creates a modifier adds Force icons to locations accepted by the location filter to equalize number light and dark Force icons.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ForceIconsEqualizedModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        super(source, "Force icons equalized", Filters.and(Filters.location, locationFilter), condition, ModifierType.EQUALIZE_FORCE_ICONS);
    }
}
