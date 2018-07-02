package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to move free from specified locations when using landspeed.
 */
public class MovesFreeFromLocationUsingLandspeedModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that causes the source card to move free from specified locations when using landspeed.
     * @param source the card that is the source of the modifier and moves free from specified locations using landspeed
     * @param locationFilter the location filter
     */
    public MovesFreeFromLocationUsingLandspeedModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, source, null, locationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to move free from specified locations when using landspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that move free from specified locations when using landspeed
     * @param locationFilter the location filter
     */
    public MovesFreeFromLocationUsingLandspeedModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier that causes the source card to move free from specified locations when using landspeed.
     * @param source the card that is the source of the modifier and moves free from specified locations when using landspeed
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MovesFreeFromLocationUsingLandspeedModifier(PhysicalCard source, Condition condition, Filterable locationFilter) {
        this(source, source, condition, locationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to move free from specified locations when using landspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that move free from specified locations when using landspeed
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MovesFreeFromLocationUsingLandspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MOVES_FREE_FROM_LOCATION_USING_LANDSPEED, true);
        _locationFilter = Filters.and(Filters.location, locationFilter);
    }

    @Override
    public boolean isMoveFreeFromLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, location);
    }
}
