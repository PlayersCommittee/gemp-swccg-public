package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for "May not draw battle destiny" at specified battle locations.
 */
public class MayNotDrawBattleDestinyModifier extends MayNotDrawMoreThanBattleDestinyModifier {

    /**
     * Creates a "May not draw battle destiny" modifier.
     * @param source the source of the modifier
     * @param playerId the player that may not draw battle destiny
     */
    public MayNotDrawBattleDestinyModifier(PhysicalCard source, String playerId) {
        this(source, Filters.any, null, playerId);
    }

    /**
     * Creates a "May not draw battle destiny" modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param playerId the player that may not draw battle destiny
     */
    public MayNotDrawBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a "May not draw battle destiny" modifier.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not draw battle destiny
     */
    public MayNotDrawBattleDestinyModifier(PhysicalCard source, Condition condition, String playerId) {
        this(source, Filters.any, condition, playerId);
    }

    /**
     * Creates a "May not draw battle destiny" modifier.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not draw battle destiny
     */
    public MayNotDrawBattleDestinyModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, locationFilter, condition, 0, playerId);
    }
}
