package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified player has fewer than the specified number of cards in hand.
 */
public class CardsInHandFewerThanCondition implements Condition {
    private String _playerId;
    private int _count;

    /**
     * Creates a condition that is fulfilled when the specified player has fewer than the specified number of cards in hand.
     * @param playerId the player
     * @param count the number of cards
     */
    public CardsInHandFewerThanCondition(String playerId, int count) {
        _playerId = playerId;
        _count = count;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return gameState.getHand(_playerId).size() < _count;
    }
}

