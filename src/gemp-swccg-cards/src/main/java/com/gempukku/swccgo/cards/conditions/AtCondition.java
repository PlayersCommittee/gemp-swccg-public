package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Map;

/**
 * A condition that is fulfilled when the specified card is "at" a location accepted by the specified locationFilter,
 * or when a card accepted by the specified cardFilter (or that has a permanent aboard accepted by the specified cardFilter)
 * is "at" a location accepted by the specified locationFilter, or when the specified card is "at" the specified system.
 */
public class AtCondition implements Condition {
    private Integer _permSourceCardId;
    private Integer _permCardId;
    private Map<InactiveReason, Boolean> _spotOverrides;
    private Filter _cardFilter;
    private Filter _locationFilter;
    private String _system;

    /**
     * Creates a condition that is fulfilled when the input card is "at" a location accepted by the specified locationFilter.
     * @param card the card
     * @param locationFilter the location filter
     */
    public AtCondition(PhysicalCard card, Filterable locationFilter) {
        _permCardId = card.getPermanentCardId();
        _locationFilter = Filters.and(locationFilter);
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified cardFilter (or that has a permanent aboard
     * accepted by the specified cardFilter) is "at" a location accepted by the specified locationFilter.
     * @param source the card that is checking this condition
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     */
    public AtCondition(PhysicalCard source, Filterable cardFilter, Filterable locationFilter) {
        this(source, null, cardFilter, locationFilter);
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified cardFilter (or that has a permanent aboard
     * accepted by the specified cardFilter) is "at" a location accepted by the specified locationFilter.
     * @param source the card that is checking this condition
     * @param spotOverrides overrides for which inactive cards are visible to this condition check
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     */
    public AtCondition(PhysicalCard source, Map<InactiveReason, Boolean> spotOverrides, Filterable cardFilter, Filterable locationFilter) {
        _permSourceCardId = source.getPermanentCardId();
        _spotOverrides = spotOverrides;
        _cardFilter = Filters.and(cardFilter);
        _locationFilter = Filters.and(locationFilter);
    }

    /**
     * Creates a condition that is fulfilled when the specified card is at the system of the specified name.
     * @param card the card
     * @param system the name of the system
     */
    public AtCondition(PhysicalCard card, String system) {
        _permCardId = card.getPermanentCardId();
        _system = system;
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified cardFilter (or that has a permanent aboard
     * accepted by the specified cardFilter) is at the system of the specified name.
     * @param source the card that is checking this condition
     * @param cardFilter the card filter
     * @param system the name of the system
     */
    public AtCondition(PhysicalCard source, Filterable cardFilter, String system) {
        this(source, null, cardFilter, system);
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified cardFilter (or that has a permanent aboard
     * accepted by the specified cardFilter) is at the system of the specified name.
     * @param source the card that is checking this condition
     * @param spotOverrides overrides for which inactive cards are visible to this condition check
     * @param cardFilter the card filter
     * @param system the name of the system
     */
    public AtCondition(PhysicalCard source, Map<InactiveReason, Boolean> spotOverrides, Filterable cardFilter, String system) {
        _permSourceCardId = source.getPermanentCardId();
        _spotOverrides = spotOverrides;
        _cardFilter = Filters.and(cardFilter);
        _system = system;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (card != null) {
            if (_locationFilter != null)
                return Filters.at(_locationFilter).accepts(gameState, modifiersQuerying, card);
            else
                return _system.equals(modifiersQuerying.getSystemThatCardIsAt(gameState, card));
        }
        else {
            Filter filterToUse = Filters.and(Filters.or(_cardFilter, Filters.hasPermanentAboard(_cardFilter), Filters.hasPermanentWeapon(_cardFilter)));
            if (_locationFilter != null) {
                return Filters.canSpot(gameState.getGame(), source, _spotOverrides, Filters.and(filterToUse, Filters.at(_locationFilter)));
            }
            else {
                return Filters.canSpot(gameState.getGame(), source, _spotOverrides, Filters.and(filterToUse, Filters.at(_system)));
            }
        }
    }
}
