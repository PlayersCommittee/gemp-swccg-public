package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collection;

/**
 * An evaluator that returns the number of all cards what were 'beheaded'.
 */
public class BeheadedVictimsEvaluator extends BaseEvaluator {

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        float numCards = 0;
        Collection<PhysicalCard> outOfPlayCards = gameState.getAllOutOfPlayCards();
        for (PhysicalCard outOfPlayCard : outOfPlayCards) {
            if (outOfPlayCard.isBeheaded()) {
                numCards++;
            }
        }
        return numCards;
    }
}
