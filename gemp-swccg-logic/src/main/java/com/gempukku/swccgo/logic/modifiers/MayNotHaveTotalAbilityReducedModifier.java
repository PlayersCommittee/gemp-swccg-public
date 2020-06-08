package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to have total ability at the location reduced.
 */
public class MayNotHaveTotalAbilityReducedModifier extends AbstractModifier {

    /**
     * Creates a modifier for not being able to have the specified player's total ability at locations accepted by the location filter reduced.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player
     */
    public MayNotHaveTotalAbilityReducedModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier for not being able to have the specified player's total ability at locations accepted by the location filter reduced.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public MayNotHaveTotalAbilityReducedModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_HAVE_TOTAL_ABILITY_AT_LOCATION_REDUCED, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _playerId + "'s total ability may not be reduced";
    }
}
