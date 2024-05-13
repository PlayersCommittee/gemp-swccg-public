package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the number of cards in the specified player's hand.
 */
public class HandSizeEvaluator extends BaseEvaluator {
    private String _playerId;

    /**
     * Creates an evaluator that returns the number of cards in the specified player's hand.
     * @param playerId the player
     */
    public HandSizeEvaluator(String playerId) {
        _playerId = playerId;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return gameState.getHand(_playerId).size();
    }
}
