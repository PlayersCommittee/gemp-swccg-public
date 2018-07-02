package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to move.
 */
public class MayNotMoveModifier extends AbstractModifier {

    /**
     * Creates a modifier that prohibits the source card from moving.
     * @param source the source card of the modifier
     */
    public MayNotMoveModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that prohibits cards accepted by the input filter from moving.
     * @param source the source card of the modifier
     * @param affectFilter the filter
     */
    public MayNotMoveModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prohibits the source card from moving.
     * @param source the source card of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotMoveModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that prohibits cards accepted by the input filter from moving.
     * @param source the source card of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotMoveModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not move", Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MAY_NOT_MOVE, true);
    }
}
