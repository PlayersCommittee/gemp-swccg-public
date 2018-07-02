package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows specified cards to deploy to specified locations.
 */
public class MayDeployToLocationModifier extends MayDeployToTargetModifier {

    /**
     * Creates a modifier which allows cards accepted by the filter to deploy to locations accepted by the target filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param locationFilter the location filter
     */
    public MayDeployToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier which allows cards accepted by the filter to deploy to locations accepted by the target filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MayDeployToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, affectFilter, condition, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)));
    }
}
