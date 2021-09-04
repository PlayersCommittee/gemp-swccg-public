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
    private Filter _stackedOnFilter;
    private Filter _stackedCardFilter;

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
     * @param stackedOnFilter the filter
     */
    public StackedEvaluator(PhysicalCard source, Filterable stackedOnFilter) {
        _permSourceCardId = source.getPermanentCardId();
        _stackedOnFilter = Filters.and(stackedOnFilter);
        _stackedCardFilter = Filters.any;
    }

    /**
     * Creates an evaluator that returns the number of cards accepted by stackedCardFilter are stacked on cards accepted by stackedOnFilter
     * @param source the card that is creating this evaluator
     * @param stackedOnFilter the filter
     * @param stackedCardFilter filter for cards that are stacked
     */
    public StackedEvaluator(PhysicalCard source, Filterable stackedOnFilter, Filterable stackedCardFilter) {
        _permSourceCardId = source.getPermanentCardId();
        _stackedOnFilter = Filters.and(stackedOnFilter);
        _stackedCardFilter = Filters.and(stackedCardFilter);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        if (card != null) {
            return gameState.getStackedCards(card).size();
        }
        return Filters.countStacked(gameState.getGame(), Filters.and(Filters.stackedOn(source, _stackedOnFilter), _stackedCardFilter));
    }
}
