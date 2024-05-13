package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;

/**
 * A condition that is fulfilled when all the ability for a specified player in battle is provided
 * by cards that are accepted by a specified filter.
 */
public class AllAbilityInBattleProvidedByCondition implements Condition {
    private String _playerId;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when all the ability for a specified player in battle is provided
     * by cards that are accepted by a specified filter.
     * @param playerId the player
     * @param filters the filter
     */
    public AllAbilityInBattleProvidedByCondition(String playerId, Filterable filters) {
        _playerId = playerId;
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (!gameState.isDuringBattle())
            return false;

        Collection<PhysicalCard> cardsWithAbility = Filters.filter(gameState.getBattleState().getAllCardsParticipating(), gameState.getGame(), Filters.and(Filters.owner(_playerId), Filters.hasAbilityOrHasPermanentPilotWithAbility));
        if (cardsWithAbility.isEmpty())
            return false;

        for (PhysicalCard cardWithAbility : cardsWithAbility) {
            if (!Filters.and(_filters).accepts(gameState, modifiersQuerying, cardWithAbility))
                return false;
        }

        return true;
    }
}

