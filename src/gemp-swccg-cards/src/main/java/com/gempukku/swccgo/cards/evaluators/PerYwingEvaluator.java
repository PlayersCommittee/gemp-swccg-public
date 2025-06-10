package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.List;

/**
 * An evaluator that returns the result of the specified evaluator multiplied by the number of Y-wings within the evaluated card.
 */
public class PerYwingEvaluator extends BaseEvaluator {
    private int _amountPerYwing;

    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the number of Y-wings within
     * the evaluated card.
     * @param amountPerYwing the amount per Y-wing
     */
    public PerYwingEvaluator(int amountPerYwing) {
        _amountPerYwing = amountPerYwing;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        int numYwings = 0;
        List<ModelType> modelTypes = self.getBlueprint().getModelTypes();
        for (ModelType modelType : modelTypes) {
            if (modelType == ModelType.Y_WING) {
                numYwings++;
            }
        }

        return _amountPerYwing * numYwings;
    }
}
