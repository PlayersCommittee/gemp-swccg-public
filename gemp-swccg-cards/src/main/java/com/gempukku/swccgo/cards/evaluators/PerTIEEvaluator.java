package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;


/**
 * An evaluator that returns the result of the specified evaluator multiplied by the number of TIEs within the evaluated card.
 */
public class PerTIEEvaluator extends BaseEvaluator {
    private int _amountPerTIE;

    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the number of TIEs within
     * the evaluated card.
     * @param amountPerTIE the amount per TIE
     */
    public PerTIEEvaluator(int amountPerTIE) {
        _amountPerTIE = amountPerTIE;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _amountPerTIE * Filters.TIE.acceptsCount(gameState, modifiersQuerying, self);
    }
}
