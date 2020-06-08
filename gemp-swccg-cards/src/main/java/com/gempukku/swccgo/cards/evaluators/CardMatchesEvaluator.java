package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the specified matches value if the card used in the evaluation is accepted by the specified
 * filter, otherwise returns the specified default value.
 */
public class CardMatchesEvaluator extends BaseEvaluator {
    private Filter _filters;
    private Evaluator _matches;
    private int _default;

    /**
     * Creates an evaluator that returns the specified matches value if the card used in the evaluation is accepted by the
     * specified filter, otherwise returns the specified default value.
     * @param defaultValue the default value
     * @param matches the evaluator to return the value of if the card is accepted by the specified filter
     * @param filters the filter
     */
    public CardMatchesEvaluator(int defaultValue, Evaluator matches, Filterable filters) {
        _default = defaultValue;
        _matches = matches;
        _filters = Filters.and(filters);
    }

    /**
     * Creates an evaluator that returns the value from the specified evaluator if the card used in the evaluation is
     * accepted by the specified filter, otherwise returns the specified default value.
     * @param defaultValue the default value
     * @param matches the value to return if the card is accepted by the specified filter
     * @param filters the filter
     */
    public CardMatchesEvaluator(int defaultValue, int matches, Filterable filters) {
        _default = defaultValue;
        _matches = new ConstantEvaluator(matches);
        _filters = Filters.and(filters);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return Filters.and(_filters).accepts(gameState, modifiersQuerying, self) ? _matches.evaluateExpression(gameState, modifiersQuerying, self) : _default;
    }
}
