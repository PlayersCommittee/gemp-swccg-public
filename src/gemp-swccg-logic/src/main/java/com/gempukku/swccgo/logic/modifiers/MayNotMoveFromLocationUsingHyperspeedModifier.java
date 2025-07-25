package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for not allowing specified cards to move from specified locations using hyperspeed.
 */
public class MayNotMoveFromLocationUsingHyperspeedModifier extends AbstractModifier {
    private Filter _affectedFilter;

    /**
     * Creates a modifier for not allowing specified cards to move using hyperspeed from locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to move
     * @param locationFilter the location filter
     */
    public MayNotMoveFromLocationUsingHyperspeedModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier for not allowing specified cards to move using hyperspeed from locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to move
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MayNotMoveFromLocationUsingHyperspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_USING_HYPERSPEED, true);
        _affectedFilter = Filters.and(affectFilter);
    }

    @Override
    public boolean prohibitedFromMovingFromLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToMove) {
        return Filters.and(_affectedFilter).accepts(gameState, modifiersQuerying, cardToMove);
    }
}
