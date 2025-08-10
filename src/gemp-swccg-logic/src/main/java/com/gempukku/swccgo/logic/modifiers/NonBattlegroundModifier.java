package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the specified locations to be considered non-battlegrounds (regardless of Force icons).
 */
public class NonBattlegroundModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes locations accepted by the filter to be considered non-battlegrounds (regardless of Force icons).
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public NonBattlegroundModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, locationFilter, null);
    }

    /**
     * Creates a modifier that causes locations accepted by the filter to be considered non-battlegrounds (regardless of Force icons).
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    private NonBattlegroundModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        super(source, "Is non-battleground", Filters.and(Filters.location, locationFilter), condition, ModifierType.NONBATTLEGROUND);
    }
}
