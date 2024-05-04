package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the specified cards to ignore deployment restrictions from the specified cards when deploying to certain locations.
 */
public class IgnoresDeploymentRestrictionsFromCardWhenDeployingToLocationModifier extends AbstractModifier {
    private Filter _cardFilter;
    private Filter _locationFilter;

    /**
     * Creates a modifier that causes the specified filter to ignore deployment restrictions from cards accepted by the card filter when deploying to certain locations.
     *
     * @param source       the source of the modifier
     * @param affectFilter the filter
     * @param condition    the condition that must be fulfilled for the modifier to be in effect
     * @param playerId     the player
     * @param cardFilter   the card
     */
    public IgnoresDeploymentRestrictionsFromCardWhenDeployingToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId, Filterable cardFilter, Filterable locationFilter) {
        super(source, "Ignores deployment restrictions from certain cards when deploying to certain locations", affectFilter, condition, ModifierType.IGNORES_DEPLOYMENT_RESTRICTIONS_FROM_CARD_WHEN_DEPLOYING_TO_LOCATION);
        _cardFilter = Filters.and(cardFilter);
        _playerId = playerId;
        _locationFilter = Filters.and(locationFilter);
    }

    public Filter getCardFilter() {
        return _cardFilter;
    }

    public Filter getLocationFilter() {
        return _locationFilter;
    }
}
