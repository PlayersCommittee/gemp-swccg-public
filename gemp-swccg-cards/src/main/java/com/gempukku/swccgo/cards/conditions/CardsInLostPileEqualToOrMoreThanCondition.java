package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified player has at least a specified number of cards in their lost pile.
 */
public class CardsInLostPileEqualToOrMoreThanCondition implements Condition {
    private String _playerId;
    private int _count;

    /**
     * Creates a condition that is fulfilled when the specified player has at least a specified number of cards in lost pile.
     * @param playerId the player
     * @param count the number of cards
     */
    public CardsInLostPileEqualToOrMoreThanCondition(String playerId, int count) {
        _playerId = playerId;
        _count = count;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return GameConditions.numCardsInLostPile(gameState.getGame(), _playerId) >= _count;
    }
}

