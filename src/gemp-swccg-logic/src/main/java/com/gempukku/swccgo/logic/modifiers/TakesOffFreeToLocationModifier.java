package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to take off free to specified locations.
 */
public class TakesOffFreeToLocationModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that causes the source card to take off for free to specified locations.
     * @param source the card that is the source of the modifier and takes off for free to specified locations
     * @param locationFilter the location filter
     */
    public TakesOffFreeToLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, source, null, locationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to take off for free to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that take off for free to specified locations
     * @param locationFilter the location filter
     */
    public TakesOffFreeToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier that causes the source card to take off for free to specified locations.
     * @param source the card that is the source of the modifier and takes off for free to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public TakesOffFreeToLocationModifier(PhysicalCard source, Condition condition, Filterable locationFilter) {
        this(source, source, condition, locationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to take off for free free to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that take off for free to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public TakesOffFreeToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.TAKES_OFF_FOR_FREE_TO_LOCATION, true);
        _locationFilter = Filters.and(Filters.location, locationFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public boolean isMoveFreeToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, location);
    }
}
