package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that resets deploy cost to an unmodifiable value when deploying to specified locations.
 */
public class ResetDeployCostToLocationModifier extends ResetDeployCostToTargetModifier {

    /**
     * Creates a modifier that resets deploy cost to an unmodifiable value deploying to specified locations.
     * @param source the card that is the source of the reset and whose deploy cost is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     * @param locationFilter the filter for deploy to locations
     */
    public ResetDeployCostToLocationModifier(PhysicalCard source, Condition condition, float resetValue, Filterable locationFilter) {
        this(source, source, condition, resetValue, locationFilter);
    }

    /**
     * Creates a modifier that resets deploy cost to an unmodifiable value deploying to specified locations.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose deploy cost is reset
     * @param resetValue the reset value
     * @param locationFilter the filter for deploy to locations
     */
    public ResetDeployCostToLocationModifier(PhysicalCard source, Filterable affectFilter, float resetValue, Filterable locationFilter) {
        super(source, affectFilter, null, resetValue, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)));
    }

    /**
     * Creates a modifier that resets deploy cost to an unmodifiable value deploying to specified locations.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose deploy cost is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     * @param locationFilter the filter for deploy to locations
     */
    public ResetDeployCostToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float resetValue, Filterable locationFilter) {
        super(source, affectFilter, condition, resetValue, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)));
    }
}
