package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collection;

/**
 * An evaluator that returns the ability of all cards whose 'soup was eaten'.
 */
public class SoupEatenEvaluator extends BaseEvaluator {

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        float totalAbility = 0;
        Collection<PhysicalCard> outOfPlayCards = gameState.getAllOutOfPlayCards();
        for (PhysicalCard outOfPlayCard : outOfPlayCards) {
            Float soupEaten = outOfPlayCard.getSoupEaten();
            if (soupEaten != null) {
                totalAbility += soupEaten;
            }
        }
        return totalAbility;
    }
}
