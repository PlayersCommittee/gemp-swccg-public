package com.gempukku.swccgo.logic.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * The abstract class for a base evaluator.
 */
public abstract class BaseEvaluator implements Evaluator {

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
        throw new UnsupportedOperationException("This method, evaluateExpression(cardAffected), should not be called");
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
        throw new UnsupportedOperationException("This method, evaluateExpression(cardAffected, otherCard), should not be called");
    }
}
