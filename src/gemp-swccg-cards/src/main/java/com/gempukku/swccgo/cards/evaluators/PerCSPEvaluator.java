package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;


/**
 * An evaluator that returns the result of the specified evaluator multiplied by the number of CSP within the evaluated card.
 */
public class PerCSPEvaluator extends BaseEvaluator {
    private int _amountPerCSP;

    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the number of CSP within
     * the evaluated card.
     * @param amountPerAwing the amount per CSP
     */
    public PerCSPEvaluator(int amountPerCSP) {
        _amountPerCSP = amountPerCSP;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _amountPerCSP * Filters.CSP.acceptsCount(gameState, modifiersQuerying, self);
    }
}
