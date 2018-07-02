package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to move to specified locations using hyperspeed.
 */
public class MayNotMoveToLocationUsingHyperspeedModifier extends AbstractModifier {
    private Filter _affectedFilter;

    /**
     * Creates a modifier for not being able to move to specified locations using hyperspeed.
     * @param source the card that is the source of the modifier and that may not move to specified locations
     * @param locationFilter the filter for locations that affected cards may not move to
     */
    public MayNotMoveToLocationUsingHyperspeedModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, source, null, locationFilter);
    }

    /**
     * Creates a modifier for not being able to move to specified locations using hyperspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not move to specified locations
     * @param locationFilter the filter for locations that affected cards may not move to
     */
    public MayNotMoveToLocationUsingHyperspeedModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier for not being able to move to specified locations using hyperspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not move to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the filter for locations that affected cards may not move to
     */
    public MayNotMoveToLocationUsingHyperspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_MOVE_TO_LOCATION_USING_HYPERSPEED, true);
        _affectedFilter = Filters.and(affectFilter);
    }

    @Override
    public boolean prohibitedFromMovingToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToMove) {
        return Filters.and(_affectedFilter).accepts(gameState, modifiersQuerying, cardToMove);
    }
}
