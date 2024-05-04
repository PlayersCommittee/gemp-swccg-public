package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents the specified player from losing any Force from a Force drain at a location
 * affected by the modifier.
 */
public class NoForceLossFromForceDrainModifier extends LimitForceLossFromForceDrainModifier {

    /**
     * Creates a modifier that prevents the specified player from losing any Force from a Force drain at a location
     * affected by the modifier.
     * @param source the source card of this modifier
     * @param locationFilter the filter
     * @param playerId the player whose Force loss is prevented
     */
    public NoForceLossFromForceDrainModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier that prevents the specified player from losing any Force from a Force drain at a location
     * affected by the modifier.
     * @param source the source card of this modifier
     * @param locationFilter the filter
     * @param condition the condition under which this modifier is in effect
     * @param playerId the player whose Force loss is prevented
     */
    public NoForceLossFromForceDrainModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, locationFilter, condition, 0, playerId);
    }
}
