package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns the value of "while in play" data of the card data stored as a Float, or 0 if not a valid float.
 */
public class InPlayDataAsFloatEvaluator extends BaseEvaluator {
    private int _permCardId;

    /**
     * Creates an evaluator that returns value of "while in play" data stored as a Float.
     * @param card the card
     */
    public InPlayDataAsFloatEvaluator(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        float value = 0;
        if (card.getWhileInPlayData() != null && card.getWhileInPlayData().getFloatValue() != null) {
            value = card.getWhileInPlayData().getFloatValue();
        }
        return value;
    }
}
