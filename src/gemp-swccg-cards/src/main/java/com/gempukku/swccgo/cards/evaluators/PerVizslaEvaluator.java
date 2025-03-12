package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;


/**
 * An evaluator that returns the result of the specified evaluator multiplied by the number of CSP within the evaluated card.
 */
public class PerVizslaEvaluator extends BaseEvaluator {
    private int _amountPerVizsla;

    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the number of CSP within
     * the evaluated card.
     * @param amountPerVizsla the amount per Vizsla
     */
    public PerVizslaEvaluator(int amountPerVizsla) {
        _amountPerVizsla = amountPerVizsla;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _amountPerVizsla * Filters.VIZSLA_character.acceptsCount(gameState, modifiersQuerying, self);
    }
}
