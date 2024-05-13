package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows affected cards to deploy as landed to specified locations.
 */
public class MayDeployAsLandedToLocationModifier extends AbstractModifier {
    private Filter _targetFilters;

    /**
     * Creates a modifier that allows the source card to deploy as landed to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public MayDeployAsLandedToLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, source, null, locationFilter);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy as landed to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param locationFilter the location filter
     */
    public MayDeployAsLandedToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy as landed to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MayDeployAsLandedToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_DEPLOY_AS_LANDED_TO_TARGET, true);
        _targetFilters = Filters.locationAndCardsAtLocation(Filters.and(locationFilter));
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Valid deploy as landed to specific locations";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilters).accepts(gameState, modifiersQuerying, target);
    }
}
