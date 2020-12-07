package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that causes Sith Probe Droids to not be able to be deployed or moved to same location
 * as another Sith Probe Droid
 */
public class MayNotDeployOrMoveSithProbeDroidToLocationsModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected Sith Probe Droids to not be able to be deployed or moved to same location
     * as another Sith Probe Droid
     * @param source the source of the modifier
     */

    public MayNotDeployOrMoveSithProbeDroidToLocationsModifier(PhysicalCard source) {
        super(source, "May not deploy or move to same location as another Sith Probe Droid", Filters.Sith_Probe_Droid, null, ModifierType.MAY_NOT_DEPLOY_MOVE_SITH_PROBE_DROID);
    }
}