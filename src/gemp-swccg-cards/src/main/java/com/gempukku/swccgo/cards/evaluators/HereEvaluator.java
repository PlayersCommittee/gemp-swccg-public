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
 * An evaluator that returns the number of cards accepted by the specified filter that are "at" the same location the
 * specified card is "at" (or "at" the location if the specified card is a location).
 */
public class HereEvaluator extends BaseEvaluator {
    private int _permCardId;
    private Filter _filters;
    private Map<InactiveReason, Boolean> _spotOverrides;

    /**
     * Creates an evaluator that returns the number of cards accepted by the specified filter that are "at" the same location
     * the specified card is "at" (or "at" the location if the specified card is a location).
     * @param card the card
     * @param filters the filter
     */
    public HereEvaluator(PhysicalCard card, Filterable filters) {
        this(card, null, filters);
    }

    /**
     * Creates an evaluator that returns the number of cards accepted by the specified filter that are "at" the same location
     * the specified card is "at" (or "at" the location if the specified card is a location).
     * @param card the card
     * @param spotOverrides the spotoverrides
     * @param filters the filter
     */
    public HereEvaluator(PhysicalCard card, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        _permCardId = card.getPermanentCardId();
        _filters = Filters.and(filters);
        _spotOverrides = spotOverrides;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        Filter filterToUse = Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters));
        return Filters.countActive(gameState.getGame(), card, _spotOverrides, Filters.and(filterToUse, Filters.here(card)));
    }
}
