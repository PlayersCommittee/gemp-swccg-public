package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Map;

/**
 * A condition that is fulfilled when the specified card is on Cloud City, or when a card accepted by the specified
 * cardFilter is on Cloud City.
 */
public class OnCloudCityCondition implements Condition {
    private Integer _permSourceCardId;
    private Integer _permCardId;
    private Map<InactiveReason, Boolean> _spotOverrides;
    private Filter _cardFilter;

    /**
     * Creates a condition that is fulfilled when the specified card is on Cloud City.
     * @param card the card
     */
    public OnCloudCityCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified cardFilter is on Cloud City.
     * @param source the card that is checking this condition
     * @param cardFilter the card filter
     */
    public OnCloudCityCondition(PhysicalCard source, Filterable cardFilter) {
        this(source, null, cardFilter);
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified cardFilter is on Cloud City.
     * @param source the card that is checking this condition
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param cardFilter the card filter
     */
    public OnCloudCityCondition(PhysicalCard source, Map<InactiveReason, Boolean> spotOverrides, Filterable cardFilter) {
        _permSourceCardId = source.getPermanentCardId();
        _spotOverrides = spotOverrides;
        _cardFilter = Filters.and(cardFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (card != null) {
            return Filters.on_Cloud_City.accepts(gameState, modifiersQuerying, card);
        }
        else {
            Filter filterToUse = Filters.and(Filters.or(_cardFilter, Filters.hasPermanentAboard(_cardFilter), Filters.hasPermanentWeapon(_cardFilter)));
            return Filters.canSpot(gameState.getGame(), source, _spotOverrides, Filters.and(filterToUse, Filters.on_Cloud_City));
        }
    }
}
