package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents the specified player from losing any Force from a card affected by the modifier.
 */
public class NoForceLossFromCardModifier extends LimitForceLossFromCardModifier {

    /**
     * Creates a modifier that prevents the specified player from losing any Force from a card affected by the modifier.
     * @param source the source card of this modifier
     * @param affectFilter the filter
     * @param playerId the player whose Force loss is prevented
     */
    public NoForceLossFromCardModifier(PhysicalCard source, Filterable affectFilter, String playerId) {
        this(source, affectFilter, null, playerId);
    }

    /**
     * Creates a modifier that prevents the specified player from losing any Force from a card affected by the modifier.
     * @param source the source card of this modifier
     * @param affectFilter the filter
     * @param condition the condition under which this modifier is in effect
     * @param playerId the player whose Force loss is prevented
     */
    public NoForceLossFromCardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, affectFilter, condition, 0, playerId);
    }
}
