package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to deploy as a 'react' to the battle location for free.
 */
public class MayDeployAsReactToBattleForFreeModifier extends MayDeployAsReactToBattleModifier {

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to the battle location for free.
     * @param source the source of the modifier
     */
    public MayDeployAsReactToBattleForFreeModifier(PhysicalCard source) {
        this(source, null);
    }

    /**
     * Creates a modifier which allows the source card to deploy as a 'react' to the battle location for free.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayDeployAsReactToBattleForFreeModifier(PhysicalCard source, Condition condition) {
        super(source, condition);
    }

    @Override
    public boolean isReactForFree() {
        return true;
    }
}
