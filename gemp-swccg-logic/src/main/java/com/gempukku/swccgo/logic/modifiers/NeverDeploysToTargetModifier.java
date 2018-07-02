package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier for never being able to deploy to specified targets.
 */
public class NeverDeploysToTargetModifier extends MayNotDeployToTargetModifier {

    /**
     * Creates a modifier for never being able to deploy to specified targets.
     * @param source the card that is the source of the modifier and that may never deploy to specified targets
     * @param targetFilter the filter for targets that affected cards may not deploy to
     */
    public NeverDeploysToTargetModifier(PhysicalCard source, Filterable targetFilter) {
        super(source, source, null, targetFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Never deploys to certain targets";
    }

    /**
     * Determines if this modifier is always in effect.
     * @return true or false
     */
    @Override
    public boolean isAlwaysInEffect() {
        return true;
    }
}
