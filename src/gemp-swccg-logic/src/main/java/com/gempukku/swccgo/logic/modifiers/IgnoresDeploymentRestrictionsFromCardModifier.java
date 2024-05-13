package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the specified cards to ignore deployment restrictions from the specified cards.
 */
public class IgnoresDeploymentRestrictionsFromCardModifier extends AbstractModifier {
    private Filter _cardFilter;

    /**
     * Creates a modifier that causes the specified filter to ignore deployment restrictions from cards accepted by the card filter.
     *
     * @param source       the source of the modifier
     * @param affectFilter the filter
     * @param condition    the condition that must be fulfilled for the modifier to be in effect
     * @param playerId     the player
     * @param cardFilter   the card
     */
    public IgnoresDeploymentRestrictionsFromCardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId, Filterable cardFilter) {
        super(source, "Ignores location deployment restrictions from certain cards", affectFilter, condition, ModifierType.IGNORES_DEPLOYMENT_RESTRICTIONS_FROM_CARD);
        _cardFilter = Filters.and(cardFilter);
        _playerId = playerId;
    }

    public Filter getCardFilter() {
        return _cardFilter;
    }
}
