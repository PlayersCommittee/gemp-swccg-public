package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected cards from moving using hyperspeed.
 */
public class MayNotMoveUsingHyperspeedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents card accepted by the filter from moving using hyperspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotMoveUsingHyperspeedModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents card accepted by the filter from moving using hyperspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotMoveUsingHyperspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not move using hyperspeed", Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MAY_NOT_MOVE_USING_HYPERSPEED, true);
    }
}
