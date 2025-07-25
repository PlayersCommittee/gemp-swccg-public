package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.List;

/**
 * An evaluator that returns the ability of the specified card's driver.
 */
public class AbilityOfDriverEvaluator extends BaseEvaluator {
    private Integer _permCardId;

    /**
     * Creates an evaluator that returns the ability of the specified card's driver.
     * @param card the card
     */
    public AbilityOfDriverEvaluator(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        float ability = 0;
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);
        if (Filters.transport_vehicle.accepts(gameState, modifiersQuerying, card)) {
            List<PhysicalCard> drivers = gameState.getPilotCardsAboard(modifiersQuerying, card, true);
            for (PhysicalCard driver : drivers) {
                ability = Math.max(ability, modifiersQuerying.getAbility(gameState, driver));
            }
        }
        return ability;
    }
}
