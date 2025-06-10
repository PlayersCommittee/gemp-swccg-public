package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows affected Interrupts to be played to cancel a Force drain.
 */
public class MayPlayToCancelForceDrainModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that allows an Interrupt accepted by the filter to be played to cancel a Force drain.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayPlayToCancelForceDrainModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null, Filters.any);
    }

    /**
     * Creates a modifier that allows an Interrupt accepted by the filter to be played to cancel a Force drain at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayPlayToCancelForceDrainModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier that allows an Interrupt accepted by the filter to be played to cancel a Force drain at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayPlayToCancelForceDrainModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, Filters.and(Filters.Interrupt, affectFilter), condition, ModifierType.MAY_PLAY_TO_CANCEL_FORCE_DRAIN, true);
        _locationFilter = Filters.and(Filters.location, locationFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, targetCard);
    }
}
