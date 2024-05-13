package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to land free to specified locations.
 */
public class LandsFreeToLocationModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that causes the source card to land for free to specified locations.
     * @param source the card that is the source of the modifier and lands for free to specified locations
     * @param locationFilter the location filter
     */
    public LandsFreeToLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, source, null, locationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to land for free to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that land for free to specified locations
     * @param locationFilter the location filter
     */
    public LandsFreeToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier that causes the source card to land for free to specified locations.
     * @param source the card that is the source of the modifier and lands for free to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public LandsFreeToLocationModifier(PhysicalCard source, Condition condition, Filterable locationFilter) {
        this(source, source, condition, locationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to land for free to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that land for free to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public LandsFreeToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.LANDS_FOR_FREE_TO_LOCATION, true);
        _locationFilter = Filters.and(Filters.location, locationFilter);
    }

    @Override
    public boolean isMoveFreeToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, location);
    }
}
