package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns the Podrace losing race total.
 */
public class LosingRaceTotalEvaluator extends BaseEvaluator {

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return gameState.getLoserRaceTotal();
    }
}
