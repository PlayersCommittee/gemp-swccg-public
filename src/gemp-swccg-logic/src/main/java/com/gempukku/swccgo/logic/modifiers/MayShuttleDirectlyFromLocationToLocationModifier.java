package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows the specified player to shuttle cards directly between specified locations.
 */
public class MayShuttleDirectlyFromLocationToLocationModifier extends AbstractModifier {
    private Filter _fromLocationFilter;
    private Filter _toLocationFilter;

    /**
     * Creates a modifier that allows the specified player to shuttle cards directly between specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public MayShuttleDirectlyFromLocationToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable fromLocationFilter, Filterable toLocationFilter) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MAY_SHUTTLE_DIRECTLY_FROM_LOCATION_TO_LOCATION, true);
        _fromLocationFilter = Filters.and(Filters.location, fromLocationFilter);
        _toLocationFilter = Filters.and(Filters.location, toLocationFilter);
    }

    @Override
    public boolean isGrantedToShuttleFromLocationToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return Filters.and(_fromLocationFilter).accepts(gameState, modifiersQuerying, fromLocation)
                && Filters.and(_toLocationFilter).accepts(gameState, modifiersQuerying, toLocation);
    }
}
