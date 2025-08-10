package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns the number of cards accepted by the specified filter that are at the same site or at adjacent
 * sites as the specified card.
 */
public class AtSameOrAdjacentSitesEvaluator extends BaseEvaluator {
    private int _permCardId;
    private Filter _filters;

    /**
     * Creates an evaluator that the number of cards accepted by the specified filter that are at the same site or at adjacent
     * sites as the specified card.
     * @param card the card
     * @param filters the filter
     */
    public AtSameOrAdjacentSitesEvaluator(PhysicalCard card, Filterable filters) {
        _permCardId = card.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);
        if (location == null || !Filters.site.accepts(gameState, modifiersQuerying, location))
            return 0;

        Filter filterToUse = Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters));
        return Filters.countActive(gameState.getGame(), card, Filters.and(filterToUse, Filters.atSameOrAdjacentSite(card)));
    }
}
