package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.List;

/**
 * An evaluator that returns the result of the specified evaluator multiplied by the number of X-wings within the evaluated card.
 */
public class PerXwingEvaluator extends BaseEvaluator {
    private int _amountPerXwing;

    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the number of X-wings within
     * the evaluated card.
     * @param amountPerYwing the amount per X-wing
     */
    public PerXwingEvaluator(int amountPerYwing) {
        _amountPerXwing = amountPerYwing;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        int numXwings = 0;
        List<ModelType> modelTypes = self.getBlueprint().getModelTypes();
        for (ModelType modelType : modelTypes) {
            if (modelType == ModelType.X_WING) {
                numXwings++;
            }
        }

        return _amountPerXwing * numXwings;
    }
}
