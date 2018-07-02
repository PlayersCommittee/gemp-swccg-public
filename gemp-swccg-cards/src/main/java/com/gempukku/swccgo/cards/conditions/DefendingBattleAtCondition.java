package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is in a battle initiated by opponent of owner at a location
 * accepted by the specified location filter.
 */
public class DefendingBattleAtCondition implements Condition {
    private int _permCardId;
    private Filter _locationFilter;

    /**
     * Creates a condition that is fulfilled when the specified card is in a battle initiated by opponent of owner at a
     * location accepted by the specified location filter.
     * @param card the card
     * @param locationFilter the location filter
     */
    public DefendingBattleAtCondition(PhysicalCard card, Filterable locationFilter) {
        _permCardId = card.getPermanentCardId();
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        return Filters.defendingBattle.accepts(gameState, modifiersQuerying, card)
                && Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, gameState.getBattleLocation());
    }
}
