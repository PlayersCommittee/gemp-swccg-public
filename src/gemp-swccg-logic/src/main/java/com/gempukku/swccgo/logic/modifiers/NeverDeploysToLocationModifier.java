package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for never being able to deploy to specified locations.
 */
public class NeverDeploysToLocationModifier extends MayNotDeployToLocationModifier {

    /**
     * Creates a modifier for never being able to deploy to specified targets.
     * @param source the card that is the source of the modifier and that may not deploy to specified targets
     * @param targetFilter the filter for targets that affected cards may never deploy to
     */
    public NeverDeploysToLocationModifier(PhysicalCard source, Filterable targetFilter) {
        super(source, source, null, targetFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Never deploys to certain locations";
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
