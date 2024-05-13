package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.List;

/**
 * A condition that is fulfilled if a card accepted by the filter been played by the player this turn.
 */
public class CardPlayedThisTurnByPlayerCondition implements Condition {
    private String _playerId;
    private Filter _filter;

    /**
     * Creates a condition that is fulfilled if a card accepted by the filter been played by the player this turn.
     * @param playerId the player
     * @param filter the filter
     */
    public CardPlayedThisTurnByPlayerCondition(String playerId, Filter filter) {
        _playerId = playerId;
        _filter = filter;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        List<PhysicalCard> cardsPlayedThisTurn = modifiersQuerying.getCardsPlayedThisTurn(_playerId);
        for (PhysicalCard cardPlayedThisTurn : cardsPlayedThisTurn) {
            if (Filters.and(_filter).accepts(gameState, modifiersQuerying, cardPlayedThisTurn)) {
                return true;
            }
        }
        return false;
    }
}
