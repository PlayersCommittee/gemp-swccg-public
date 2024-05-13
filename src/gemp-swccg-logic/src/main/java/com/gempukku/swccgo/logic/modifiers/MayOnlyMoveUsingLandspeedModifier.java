package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for only being able to move using landspeed.
 */
public class MayOnlyMoveUsingLandspeedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prohibits cards accepted by the input filter from moving except using landspeed.
     * @param source the source card of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayOnlyMoveUsingLandspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May only move using landspeed", Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MAY_ONLY_MOVE_USING_LANDSPEED, true);
    }
}
