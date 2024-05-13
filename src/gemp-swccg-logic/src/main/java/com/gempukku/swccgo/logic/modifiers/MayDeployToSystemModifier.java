package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier which allows specified cards to deploy to a specified system (and locations on that system).
 */
public class MayDeployToSystemModifier extends MayDeployToTargetModifier {

    /**
     * Creates a modifier which allows cards accepted by the filter to deploy to a specified system (and locations on that system).
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param system the system
     */
    public MayDeployToSystemModifier(PhysicalCard source, Filterable affectFilter, String system) {
        super(source, affectFilter, Filters.partOfSystem(system));
    }
}
