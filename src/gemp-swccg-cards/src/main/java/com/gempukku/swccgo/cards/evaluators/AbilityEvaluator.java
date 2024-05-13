package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the ability of the specified card (or the card the expression is evaluated against if no
 * card is specified).
 */
public class AbilityEvaluator extends BaseEvaluator {
    private Integer _permCardId;

    /**
     * Creates an evaluator that returns the ability of the card the evaluator is evaluated against.
     */
    public AbilityEvaluator() {
    }

    /**
     * Creates an evaluator that returns the ability of the specified card.
     * @param card the card
     */
    public AbilityEvaluator(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (card != null)
            return modifiersQuerying.getAbility(gameState, card);
        else
            return modifiersQuerying.getAbility(gameState, target);
    }
}
