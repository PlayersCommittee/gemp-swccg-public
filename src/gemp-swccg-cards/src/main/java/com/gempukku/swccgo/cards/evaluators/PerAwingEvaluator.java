package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;


/**
 * An evaluator that returns the result of the specified evaluator multiplied by the number of A-wings within the evaluated card.
 */
public class PerAwingEvaluator extends BaseEvaluator {
    private int _amountPerAwing;

    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the number of A-wings within
     * the evaluated card.
     * @param amountPerAwing the amount per A-wing
     */
    public PerAwingEvaluator(int amountPerAwing) {
        _amountPerAwing = amountPerAwing;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _amountPerAwing * Filters.A_wing.acceptsCount(gameState, modifiersQuerying, self);
    }
}
