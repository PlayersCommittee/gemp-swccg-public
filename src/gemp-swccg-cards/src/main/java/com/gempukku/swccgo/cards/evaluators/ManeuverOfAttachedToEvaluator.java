package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the maneuver of the card the specified card is attached to.
 */
public class ManeuverOfAttachedToEvaluator extends BaseEvaluator {
    private int _permCardId;

    /**
     * Creates an evaluator that returns the maneuver of the card the specified card is attached to.
     * @param card the card
     */
    public ManeuverOfAttachedToEvaluator(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        if (card.getAttachedTo() == null)
            return 0;

        return modifiersQuerying.getManeuver(gameState, card.getAttachedTo());
    }
}
