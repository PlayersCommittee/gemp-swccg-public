package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to move free from specified locations to specified locations when using landspeed.
 */
public class MovesFreeFromLocationToLocationUsingLandspeedModifier extends AbstractModifier {
    private Filter _fromLocationFilter;
    private Filter _toLocationFilter;

    /**
     * Creates a modifier that causes the source card to move free from specified locations to specified locations when using landspeed.
     * @param source the card that is the source of the modifier and moves free from specified locations to specified locations
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public MovesFreeFromLocationToLocationUsingLandspeedModifier(PhysicalCard source, Filterable fromLocationFilter, Filterable toLocationFilter) {
        this(source, source, null, fromLocationFilter, toLocationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to move free from specified locations to specified locations when using landspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that move free from specified locations to specified locations
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public MovesFreeFromLocationToLocationUsingLandspeedModifier(PhysicalCard source, Filterable affectFilter, Filterable fromLocationFilter, Filterable toLocationFilter) {
        this(source, affectFilter, null, fromLocationFilter, toLocationFilter);
    }

    /**
     * Creates a modifier that causes the source card to move free from specified locations to specified locations when using landspeed.
     * @param source the card that is the source of the modifier and moves free from specified locations to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public MovesFreeFromLocationToLocationUsingLandspeedModifier(PhysicalCard source, Condition condition, Filterable fromLocationFilter, Filterable toLocationFilter) {
        this(source, source, condition, fromLocationFilter, toLocationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to move free from specified locations to specified locations when using landspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that move free from specified locations to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public MovesFreeFromLocationToLocationUsingLandspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable fromLocationFilter, Filterable toLocationFilter) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION_USING_LANDSPEED, true);
        _fromLocationFilter = Filters.and(Filters.location, fromLocationFilter);
        _toLocationFilter = Filters.and(Filters.location, toLocationFilter);
    }

    @Override
    public boolean isMoveFreeFromLocationToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return Filters.and(_fromLocationFilter).accepts(gameState, modifiersQuerying, fromLocation)
                && Filters.and(_toLocationFilter).accepts(gameState, modifiersQuerying, toLocation);
    }
}
