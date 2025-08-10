package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when a card or a specified number of cards accepted by the specified filter has been
 * played by the specified player this game.
 */
public class HasPlayedThisGameCondition implements Condition {
    private String _playerId;
    private Filter _filter;
    private int _count;

    /**
     * Creates a condition that is fulfilled when cards accepted by the specified filter have been played at least once
     * by the specified player this game.
     * @param source the card that is checking this condition
     * @param filter the filter
     * @param player the player
     */
    public HasPlayedThisGameCondition(PhysicalCard source, Filterable filter, String player) {
        this(source, 1, filter, player);
    }

    /**
     * Creates a condition that is fulfilled when cards accepted by the specified filter have been played at least a
     * number of times specified by count by the specified player this game.
     * @param source the card that is checking this condition
     * @param count the number of cards
     * @param filter the filter
     * @param player the player
     */
    public HasPlayedThisGameCondition(PhysicalCard source, int count, Filterable filter, String player) {
        _playerId = player;
        _filter = Filters.and(filter);
        _count = count;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return GameConditions.hasDeployedAtLeastXCardsThisGame(gameState.getGame(), _playerId, _count, _filter);
    }

}
