package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;


/**
 * An evaluator that returns the result of the specified evaluator multiplied by the number of ThranGuard within the evaluated card.
 */
public class PerThranGuardEvaluator extends BaseEvaluator {
    private int _amountPerThranGuard;

    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the number of Thran Guard within
     * the evaluated card.
     * @param amountPerAwing the amount per Thran Guard
     */
    public PerThranGuardEvaluator(int amountPerThranGuard) {
        _amountPerThranGuard = amountPerThranGuard;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _amountPerThranGuard * Filters.THRAN_GUARD.acceptsCount(gameState, modifiersQuerying, self);
    }
}
