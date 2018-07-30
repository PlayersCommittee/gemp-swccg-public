package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to ignore objective restrictions when force draining at specified locations.
 */
public class IgnoresObjectiveRestrictionsWhenForceDrainingAtLocationModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that causes cards accepted by the filter to ignore objective restrictions when force draining at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param locationFilter the location filter
     */
    public IgnoresObjectiveRestrictionsWhenForceDrainingAtLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to ignore objective restrictions when force draining at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public IgnoresObjectiveRestrictionsWhenForceDrainingAtLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, "Ignores objective restrictions when force draining at certain locations", affectFilter, condition, ModifierType.IGNORES_OBJECTIVE_RESTRICTIONS_WHEN_FORCE_DRAINING_AT_LOCATION);
        _locationFilter = Filters.and(Filters.location, locationFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard locationCard) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, locationCard);
    }
}
