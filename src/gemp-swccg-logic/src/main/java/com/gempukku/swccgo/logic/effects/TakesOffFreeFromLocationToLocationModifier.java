package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AbstractModifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A modifier that causes specified cards to take off free from specified locations to specified locations.
 */
public class TakesOffFreeFromLocationToLocationModifier extends AbstractModifier {
    private Filter _fromLocationFilter;
    private Filter _toLocationFilter;

    /**
     * Creates a modifier that causes the source card to take off free from specified locations to specified locations.
     * @param source the card that is the source of the modifier and moves free from specified locations to specified locations
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public TakesOffFreeFromLocationToLocationModifier(PhysicalCard source, Filterable fromLocationFilter, Filterable toLocationFilter) {
        this(source, source, null, fromLocationFilter, toLocationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to take off free from specified locations to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that move free from specified locations to specified locations
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public TakesOffFreeFromLocationToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable fromLocationFilter, Filterable toLocationFilter) {
        this(source, affectFilter, null, fromLocationFilter, toLocationFilter);
    }

    /**
     * Creates a modifier that causes the source card to take off free from specified locations to specified locations.
     * @param source the card that is the source of the modifier and moves free from specified locations to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public TakesOffFreeFromLocationToLocationModifier(PhysicalCard source, Condition condition, Filterable fromLocationFilter, Filterable toLocationFilter) {
        this(source, source, condition, fromLocationFilter, toLocationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to take off free from specified locations to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that move free from specified locations to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public TakesOffFreeFromLocationToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable fromLocationFilter, Filterable toLocationFilter) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.TAKES_OFF_FOR_FREE_FROM_LOCATION_TO_LOCATION, true);
        _fromLocationFilter = Filters.and(Filters.location, fromLocationFilter);
        _toLocationFilter = Filters.and(Filters.location, toLocationFilter);
    }

    @Override
    public boolean isMoveFreeFromLocationToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return Filters.and(_fromLocationFilter).accepts(gameState, modifiersQuerying, fromLocation)
                && Filters.and(_toLocationFilter).accepts(gameState, modifiersQuerying, toLocation);
    }
}
