package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.List;

/**
 * An evaluator that returns the result of the specified evaluator multiplied by the number of Star Destroyers within the evaluated card.
 */
public class PerStarDestroyerEvaluator extends BaseEvaluator {
    private int _amountPerStarDestroyer;

    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the number of Star Destroyers within
     * the evaluated card.
     * @param amountPerStarDestroyer the amount per Star Destroyer
     */
    public PerStarDestroyerEvaluator(int amountPerStarDestroyer) {
        _amountPerStarDestroyer = amountPerStarDestroyer;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _amountPerStarDestroyer * Filters.Star_Destroyer.acceptsCount(gameState, modifiersQuerying, self);
    }
}
