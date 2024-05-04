package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to deploy as a 'react' to the battle location.
 */
public class MayDeployAsReactToBattleModifier extends MayDeployAsReactToLocationModifier {

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to the battle location.
     * @param source the source of the modifier
     */
    public MayDeployAsReactToBattleModifier(PhysicalCard source) {
        this(source, null);
    }

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to the battle location.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayDeployAsReactToBattleModifier(PhysicalCard source, Condition condition) {
        super(source, condition, Filters.battleLocation);
    }
}
