package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes specified cards to ignore location deployment restrictions from specific cards when deploying to specified locations.
 */
public class IgnoresLocationDeploymentRestrictionsFromCardWhenDeployingToLocationModifier extends AbstractModifier {
    private Filter _cardFilter;
    private Filter _locationFilter;
    private boolean _exceptForceIconsOrPresence;

    /**
     * Creates a modifier that causes cards accepted by the filter to ignore deployment restrictions from specific cards when deploying to locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param locationFilter the location filter
     */
    public IgnoresLocationDeploymentRestrictionsFromCardWhenDeployingToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable cardFilter, Filterable locationFilter) {
        this(source, affectFilter, null, cardFilter, locationFilter, false);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to ignore deployment restrictions from specific cards when deploying to locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public IgnoresLocationDeploymentRestrictionsFromCardWhenDeployingToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable cardFilter, Filterable locationFilter) {
        this(source, affectFilter, condition, cardFilter, locationFilter, false);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to ignore deployment restrictions from specific cards when deploying to locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     * @param exceptForceIconsOrPresence true if Force icons or presence requirement is still needed
     */
    public IgnoresLocationDeploymentRestrictionsFromCardWhenDeployingToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable cardFilter, Filterable locationFilter, boolean exceptForceIconsOrPresence) {
        super(source, "Ignores location deployment restrictions from certain cards when deploying to certain locations", affectFilter, condition, ModifierType.IGNORES_LOCATION_DEPLOYMENT_RESTRICTIONS_FROM_CARD_WHEN_DEPLOYING_TO_LOCATION);
        _cardFilter = Filters.and(cardFilter);
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

    public Filter getCardFilter() {
        return _cardFilter;
    }
}
