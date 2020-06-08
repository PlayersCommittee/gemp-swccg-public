package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier which allows the source card to deploy as a 'react' for free.
 */
public class MayDeployAsReactForFreeModifier extends MayDeployAsReactModifier {

    /**
     * Creates a modifier that allows the source card to deploy as a 'react' for free.
     * @param source the source of the modifier
     */
    public MayDeployAsReactForFreeModifier(PhysicalCard source) {
        super(source);
    }

    @Override
    public boolean isReactForFree() {
        return true;
    }
}
