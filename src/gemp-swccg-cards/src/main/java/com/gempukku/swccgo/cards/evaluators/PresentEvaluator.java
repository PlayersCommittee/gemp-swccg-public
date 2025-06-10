package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Map;

/**
 * An evaluator that returns the number of cards accepted by the specified filter that are "present" at the location
 * the specified card is "at" (or "present" at the location if the specified card is a location).
 */
public class PresentEvaluator extends BaseEvaluator {
    private int _permSourceCardId;
    private int _permCardId;
    private Map<InactiveReason, Boolean> _spotOverrides;
    private Filter _filters;

    /**
     * Creates an evaluator that returns the number of cards accepted by the specified filter that are "present" at the location
     * the specified card is "at" (or "present" at the location if the specified card is a location).
     * @param source the card that is creating this evaluator
     * @param filters the filter
     */
    public PresentEvaluator(PhysicalCard source, Filterable filters) {
        this(source, source, null, filters);
    }

    /**
     * Creates an evaluator that returns the number of cards accepted by the specified filter that are "present" at the location
     * the specified card is "at" (or "present" at the location if the specified card is a location).
     * @param source the card that is creating this evaluator
     * @param card the card
     * @param filters the filter
     */
    public PresentEvaluator(PhysicalCard source, PhysicalCard card, Filterable filters) {
        this(source, card, null, filters);
    }

    /**
     * Creates an evaluator that returns the number of cards accepted by the specified filter that are "present" at the location
     * the specified card is "at" (or "present" at the location if the specified card is a location).
     * @param source the card that is creating this evaluator
     * @param spotOverrides overrides for which inactive cards are visible to this evaluator
     * @param filters the filter
     */
    public PresentEvaluator(PhysicalCard source, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        this(source, source, spotOverrides, filters);
    }

    /**
     * Creates an evaluator that returns the number of cards accepted by the specified filter that are "present" at the location
     * the specified card is "at" (or "present" at the location if the specified card is a location).
     * @param source the card that is creating this evaluator
     * @param card the card
     * @param spotOverrides overrides for which inactive cards are visible to this evaluator
     * @param filters the filter
     */
    public PresentEvaluator(PhysicalCard source, PhysicalCard card, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        _permSourceCardId = source.getPermanentCardId();
        _permCardId = card.getPermanentCardId();
        _spotOverrides = spotOverrides;
        _filters = Filters.and(filters);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        Filter filterToUse = Filters.or(_filters, Filters.hasPermanentWeapon(_filters));
        return Filters.countActive(gameState.getGame(), source, _spotOverrides, Filters.and(filterToUse, Filters.present(card)));
    }
}
