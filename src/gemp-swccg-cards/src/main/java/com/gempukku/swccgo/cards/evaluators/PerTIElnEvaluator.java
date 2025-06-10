package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;


/**
 * An evaluator that returns the result of the specified evaluator multiplied by the number of TIE/lns within the evaluated card.
 */
public class PerTIElnEvaluator extends BaseEvaluator {
    private int _amountPerTIEln;

    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the number of TIE/lns within
     * the evaluated card.
     * @param amountPerTIEln the amount per TIE/ln
     */
    public PerTIElnEvaluator(int amountPerTIEln) {
        _amountPerTIEln = amountPerTIEln;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _amountPerTIEln * Filters.TIE_ln.acceptsCount(gameState, modifiersQuerying, self);
    }
}
