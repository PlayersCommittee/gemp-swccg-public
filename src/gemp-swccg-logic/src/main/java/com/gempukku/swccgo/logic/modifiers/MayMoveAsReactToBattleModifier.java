package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to move as a 'react' to the battle location.
 */
public class MayMoveAsReactToBattleModifier extends MayMoveAsReactToLocationModifier {

    /**
     * Creates a modifier which allows the source card to move as a 'react' to the battle location.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public MayMoveAsReactToBattleModifier(PhysicalCard source, Filterable locationFilter) {
        super(source, null, Filters.and(Filters.battleLocation, locationFilter));
    }

    /**
     * Creates a modifier which allows the source card to move as a 'react' to the battle location.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MayMoveAsReactToBattleModifier(PhysicalCard source, Condition condition, Filterable locationFilter) {
        super(source, condition, Filters.and(Filters.battleLocation, locationFilter));
    }
}
