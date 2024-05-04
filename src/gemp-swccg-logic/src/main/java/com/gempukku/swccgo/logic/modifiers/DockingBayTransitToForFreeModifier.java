package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the docking bay transit cost to the specified docking bay to be free.
 */
public class DockingBayTransitToForFreeModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the docking bay transit cost to the source docking bay for the specified player
     * to be free.
     * @param source the source of the modifier
     * @param playerId the player
     */
    public DockingBayTransitToForFreeModifier(PhysicalCard source, String playerId) {
        this(source, source, null, playerId);
    }

    /**
     * Creates a modifier that causes the docking bay transit cost to the source docking bay for the specified player
     * to be free.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public DockingBayTransitToForFreeModifier(PhysicalCard source, Condition condition, String playerId) {
        this(source, source, condition, playerId);
    }

    /**
     * Creates a modifier that causes the docking bay transit cost to docking bays accepted by the filter for the specified player
     * to be free.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param playerId the player
     */
    public DockingBayTransitToForFreeModifier(PhysicalCard source, Filterable affectFilter, String playerId) {
        this(source, affectFilter, null, playerId);
    }

    /**
     * Creates a modifier that causes the docking bay transit cost to docking bays accepted by the filter for the specified player
     * to be free.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    private DockingBayTransitToForFreeModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.docking_bay, affectFilter), condition, ModifierType.DOCKING_BAY_TRANSIT_TO_FOR_FREE, true);
        _playerId = playerId;
    }
}
