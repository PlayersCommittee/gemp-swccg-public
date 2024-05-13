package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents specified cards from being deployed.
 */
public class MayNotDeployModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents the source card from being deployed.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotDeployModifier(PhysicalCard source, Condition condition) {
        super(source, "May not be deployed", Filters.and(source, Filters.not(Filters.in_play)), condition, ModifierType.MAY_NOT_PLAY);
    }

    /**
     * Creates a modifier that prevents the specified player from deploying cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param playerId the player
     */
    public MayNotDeployModifier(PhysicalCard source, Filterable affectFilter, String playerId) {
        this(source, affectFilter, null, playerId);
    }

    /**
     * Creates a modifier that prevents the specified player from deploying cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public MayNotDeployModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, "May not be deployed", Filters.and(Filters.owner(playerId), affectFilter, Filters.not(Filters.in_play)), condition, ModifierType.MAY_NOT_PLAY);
    }
}
