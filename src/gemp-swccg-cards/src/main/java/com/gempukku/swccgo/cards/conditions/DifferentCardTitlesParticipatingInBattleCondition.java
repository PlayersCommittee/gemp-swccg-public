package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collection;

/**
 * A condition that is fulfilled when cards with different card titles that are accepted by the specified filter are
 * participating in battle.
 */
public class DifferentCardTitlesParticipatingInBattleCondition implements Condition {
    private Filter _filter;

    /**
     * Creates a condition that is fulfilled when cards with different card titles that are accepted by the specified filter
     * are participating in battle.
     * @param filter the filter
     */
    public DifferentCardTitlesParticipatingInBattleCondition(Filterable filter) {
        _filter = Filters.and(filter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (!gameState.isDuringBattle())
            return false;

        Collection<PhysicalCard> participatingInBattle = Filters.filter(gameState.getBattleState().getAllCardsParticipating(), gameState.getGame(), _filter);
        for (PhysicalCard cardInBattle : participatingInBattle) {
            if (!Filters.filterCount(participatingInBattle, gameState.getGame(), 1, Filters.not(Filters.sameTitle(cardInBattle))).isEmpty()) {
                return true;
            }
        }

        return false;
    }
}

