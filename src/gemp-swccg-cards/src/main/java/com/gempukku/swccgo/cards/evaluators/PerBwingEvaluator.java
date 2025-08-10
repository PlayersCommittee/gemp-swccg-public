package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;


/**
 * An evaluator that returns the result of the specified evaluator multiplied by the number of B-wings within the evaluated card.
 */
public class PerBwingEvaluator extends BaseEvaluator {
    private int _amountPerBwing;

    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the number of B-wings within
     * the evaluated card.
     * @param amountPerBwing the amount per B-wing
     */
    public PerBwingEvaluator(int amountPerBwing) {
        _amountPerBwing = amountPerBwing;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _amountPerBwing * Filters.B_wing.acceptsCount(gameState, modifiersQuerying, self);
    }
}
