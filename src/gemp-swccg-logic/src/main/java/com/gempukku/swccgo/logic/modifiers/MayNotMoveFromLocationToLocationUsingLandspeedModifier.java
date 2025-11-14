package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for not allowing specified cards to move from specified locations to specified locations using landspeed.
 */
public class MayNotMoveFromLocationToLocationUsingLandspeedModifier extends AbstractModifier {
    private Filter _affectedFilter;
    private Filter _toLocationFilter;

    /**
     * Creates a modifier for not allowing specified cards to move from locations accepted by the from location filter to
     * locations accepted by the to location filter using landspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to move
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public MayNotMoveFromLocationToLocationUsingLandspeedModifier(PhysicalCard source, Filterable affectFilter, Filterable fromLocationFilter, Filterable toLocationFilter) {
        this(source, affectFilter, null, fromLocationFilter, toLocationFilter);
    }

    /**
     * Creates a modifier for not allowing specified cards to move from locations accepted by the from location filter to
     * locations accepted by the to location filter using landspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to move
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public MayNotMoveFromLocationToLocationUsingLandspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable fromLocationFilter, Filterable toLocationFilter) {
        super(source, "May not move using landspeed to or from certain locations", Filters.and(Filters.location, fromLocationFilter), condition, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_TO_LOCATION_USING_LANDSPEED, true);
        _affectedFilter = Filters.and(affectFilter);
        _toLocationFilter = Filters.and(Filters.location, toLocationFilter);
    }

    @Override
    public boolean prohibitedFromMovingFromLocationToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToMove, PhysicalCard toLocation) {
        return Filters.and(_affectedFilter).accepts(gameState, modifiersQuerying, cardToMove)
                && Filters.and(_toLocationFilter).accepts(gameState, modifiersQuerying, toLocation);
    }
}
