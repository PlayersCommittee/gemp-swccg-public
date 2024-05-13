package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that causes affected Operatives to not be able to be deployed to moved to same location on matching planet
 * as another of that player's Operatives of same card title.
 */
public class MayNotDeployOrMoveOperativeToLocationsRuleModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected Operatives to not be able to be deployed to moved to same location on matching planet
     * as another of that player's Operatives of same card title.
     * @param source the source of the modifier
     */
    public MayNotDeployOrMoveOperativeToLocationsRuleModifier(PhysicalCard source) {
        super(source, "May not deploy or move to same matching planet locations as another matching operative", Filters.operative, null, ModifierType.MAY_NOT_DEPLOY_MOVE_OPERATIVE_RULE);
    }
}
