package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

import java.util.Collection;

/**
 * A modifier for not allowing specified cards to move away from specified locations.
 */
public class MayNotMoveAwayFromLocationModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier for not allowing specified cards to move away from locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to move
     * @param locationFilter the location filter
     */
    public MayNotMoveAwayFromLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier for not allowing specified cards to move away from locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to move
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MayNotMoveAwayFromLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_NOT_MOVE_AWAY_FROM_LOCATION, true);
        _locationFilter = Filters.and(Filters.location, locationFilter);
    }

    @Override
    public boolean prohibitedFromMovingAwayFromLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard fromLocation, PhysicalCard toLocation) {
        // For each location not allowed to move away from, check if the movement is away from that location
        Collection<PhysicalCard> locationsToNotMoveAwayFrom = Filters.filterTopLocationsOnTable(gameState.getGame(), _locationFilter);
        for (PhysicalCard locationToNotMoveAwayFrom : locationsToNotMoveAwayFrom) {
            if (!Filters.toward(fromLocation, locationToNotMoveAwayFrom).accepts(gameState, modifiersQuerying, toLocation)) {
                return true;
            }
        }
        return false;
    }
}
