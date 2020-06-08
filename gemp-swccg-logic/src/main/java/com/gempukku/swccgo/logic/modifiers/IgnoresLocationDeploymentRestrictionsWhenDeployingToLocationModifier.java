package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to ignore location deployment restrictions when deploying to specified locations.
 */
public class IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier extends AbstractModifier {
    private Filter _locationFilter;
    private boolean _exceptForceIconsOrPresence;

    /**
     * Creates a modifier that causes cards accepted by the filter to ignore deployment restrictions when deploying to locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param locationFilter the location filter
     */
    public IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter, false);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to ignore deployment restrictions when deploying to locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        this(source, affectFilter, condition, locationFilter, false);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to ignore deployment restrictions when deploying to locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     * @param exceptForceIconsOrPresence true if Force icons or presence requirement is still needed
     */
    public IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter, boolean exceptForceIconsOrPresence) {
        super(source, "Ignores location deployment restrictions when deploying to certain locations", affectFilter, condition, ModifierType.IGNORES_LOCATION_DEPLOYMENT_RESTRICTIONS_WHEN_DEPLOYING_TO_LOCATION);
        _locationFilter = Filters.and(Filters.location, locationFilter);
        _exceptForceIconsOrPresence = exceptForceIconsOrPresence;
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard locationCard) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, locationCard);
    }

    @Override
    public boolean isExceptForceIconOrPresenceRequirement() {
        return _exceptForceIconsOrPresence;
    }
}
