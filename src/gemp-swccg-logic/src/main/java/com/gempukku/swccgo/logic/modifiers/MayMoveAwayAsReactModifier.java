package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows the source card to move away as 'react'.
 */
public class MayMoveAwayAsReactModifier extends MayMoveAwayAsReactToLocationModifier {

    /**
     * Creates a modifier that allows the source card to move away as 'react'.
     * @param source the source of the modifier
     */
    public MayMoveAwayAsReactModifier(PhysicalCard source) {
        this(source, null);
    }

    /**
     * Creates a modifier that allows source card to move away as 'react'.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayMoveAwayAsReactModifier(PhysicalCard source, Condition condition) {
        super(source, condition, Filters.any, 0);
    }
}
