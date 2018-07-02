package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the specified player to take no battle damage during battles at specified locations.
 */
public class NoBattleDamageModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the specified player to take no battle damage during battles at specified locations.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player whose side of the location is affected
     */
    public NoBattleDamageModifier(PhysicalCard source, Condition condition, String playerId) {
        this(source, Filters.battleLocation, condition, playerId);
    }

    /**
     * Creates a modifier that causes the specified player to take no battle damage during battles at specified locations.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player whose side of the location is affected
     */
    public NoBattleDamageModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier that causes the specified player to take no battle damage during battles at specified locations.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player whose side of the location is affected
     */
    public NoBattleDamageModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.battleLocation, locationFilter), condition, ModifierType.NO_BATTLE_DAMAGE, true);
        _playerId = playerId;
    }
}
