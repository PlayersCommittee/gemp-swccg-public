package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the number of cards stacked on the specified card (or stacked on cards accepted by a specified
 * filter).
 */
public class StackedEvaluator extends BaseEvaluator {
    private Integer _permCardId;
    private Integer _permSourceCardId;
    private Filter _filters;

    /**
     * Creates an evaluator that returns the number of cards stacked on the specified card.
     * @param card the card
     */
    public StackedEvaluator(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    /**
     * Creates an evaluator that returns the number of cards stacked on cards accepted by the specified filter.
     * @param source the card that is creating this evaluator
     * @param filters the filter
     */
    public StackedEvaluator(PhysicalCard source, Filterable filters) {
        _permSourceCardId = source.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        if (card != null) {
            return gameState.getStackedCards(card).size();
        }
        return Filters.countStacked(gameState.getGame(), Filters.stackedOn(source, _filters));
    }
}
