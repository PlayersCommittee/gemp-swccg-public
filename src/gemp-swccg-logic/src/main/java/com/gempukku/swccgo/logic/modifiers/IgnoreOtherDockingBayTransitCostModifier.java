package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the transit cost of the other docking bay involved in transit with the specified docking bay to be ignored.
 */
public class IgnoreOtherDockingBayTransitCostModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the transit cost of the other docking bay involved in transit with the source docking
     * bay for the specified player to be ignored.
     * @param source the source of the modifier
     * @param playerId the player
     */
    public IgnoreOtherDockingBayTransitCostModifier(PhysicalCard source, String playerId) {
        this(source, source, null, playerId);
    }

    /**
     * Creates a modifier that causes the transit cost of the other docking bay involved in transit with a docking bay
     * accepted by the filter for the specified player to be ignored.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    private IgnoreOtherDockingBayTransitCostModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.docking_bay, affectFilter), condition, ModifierType.IGNORE_OTHER_DOCKING_BAY_TRANSIT_COST, true);
        _playerId = playerId;
    }
}
